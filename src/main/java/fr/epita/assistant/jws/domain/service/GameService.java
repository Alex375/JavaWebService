package fr.epita.domain.service;

import fr.epita.data.model.GameMapModel;
import fr.epita.data.model.GameModel;
import fr.epita.data.model.PlayerModel;
import fr.epita.domain.entity.GameEntity;
import fr.epita.domain.entity.PlayerEntity;
import fr.epita.domain.entity.Pos;
import fr.epita.presentation.responseObject.GameResponse;
import fr.epita.utils.Map;
import javassist.NotFoundException;
import lombok.val;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.scheduling.annotation.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService
{
    @Inject PlayerService playerService;
    @ConfigProperty(name="JWS_MAP_PATH") String path;

    public GameEntity modelToEntity(GameModel gameModel)
    {
        return new GameEntity(
                gameModel.startTime,
                gameModel.state,
                gameModel.players.stream()
                        .map(playerService::modelToEntity)
                        .collect(Collectors.toList()),
                Map.formatMap(gameModel.map.map),
                gameModel.id);

    }

    @Transactional
    public Set<GameModel> getAllGamesModel()
    {
        return GameModel.<GameModel>findAll().stream().collect(Collectors.toSet());
    }

    @Transactional
    public GameModel getGameModel(long id)
    {
        return GameModel.findById(id);
    }

    @Transactional
    public Set<GameEntity> getAllGames()
    {

        return GameModel.<GameModel>findAll()
                .stream()
                .map(this::modelToEntity)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<GameResponse> getAllGameRepsonse()
    {
        System.out.println("Map path -> " + path);
        System.out.println("Thick duration -> " + playerService.thickDuration);
        System.out.println("Thick bomb -> " + playerService.delayBomb);
        System.out.println("Thick move -> " + playerService.delayMovement);
        return GameModel.<GameModel>findAll()
                .stream()
                .map(game -> new GameResponse(game.id, game.players.size(), game.state))
                .collect(Collectors.toSet());
    }

    @Transactional
    public GameEntity newGame(PlayerEntity player) throws BadRequestException
    {
        if (player == null || player.name == null)
            throw new BadRequestException("Bad request (request or name is null)");
        System.out.println("Path ---->  " + path);
        val model = new GameModel()
                .withStartTime(new Timestamp(System.currentTimeMillis()))
                .withState("STARTING")
                .withPlayers(new ArrayList<>())
                .withMap(new GameMapModel()
                        .withMap(Map.readMap(path)));

        val playerModel = new PlayerModel()
                .withLives(3)
                .withGame(model)
                .withName(player.name)
                .withPosx(1)
                .withPosy(1);
        model.players.add(playerModel);

        PlayerModel.persist(playerModel);
        GameModel.persist(model);
        return this.modelToEntity(model);
    }

    @Transactional
    public GameEntity getGame(long id) throws NotFoundException
    {
        GameModel model = getGameModel(id);
        if (model == null)
            throw new NotFoundException("Cannot found game with this id");
        return this.modelToEntity(model);
    }

    @Transactional
    public GameEntity joinGame(long id, PlayerEntity player) throws NotFoundException, BadRequestException
    {
        GameModel game = getGameModel(id);
        if (game == null)
            throw new NotFoundException("Game with this ID does not exist");
        if (game.players.size() >= 4 || !Objects.equals(game.state, "STARTING") || player == null || player.name == null)
            throw new BadRequestException("The request is null, or the player name is null or the game cannot be started (already started, too many players)");

        int x = 1;
        int y = 1;

        switch (game.players.size())
        {
            case 1:
                x = 15;
                break;
            case 2:
                y = 13;
                break;
            case 3:
                x = 15;
                y = 13;
                break;
        }


        val playerModel = new PlayerModel()
                .withLives(3)
                .withGame(game)
                .withName(player.name)
                .withPosx(x)
                .withPosy(y);
        game.players.add(playerModel);
        PlayerModel.persist(playerModel);
        GameModel.persist(game);
        return modelToEntity(game);
    }

    @Transactional
    public GameEntity startGame(long id) throws NotFoundException
    {
        val gameModel =  getGameModel(id);
        if (gameModel == null)
            throw new NotFoundException("Cannot found game with this id");
        gameModel.state = "RUNNING";
        gameModel.startTime = new Timestamp(System.currentTimeMillis());
        GameModel.persist(gameModel);
        return this.modelToEntity(gameModel);
    }

    @Transactional
    @Scheduled(fixedRate = 5)
    public void updateGame()
    {
        Set<GameModel> games = GameModel.<GameModel>findAll()
                .stream()
                .filter(gameModel -> gameModel.state.equals("RUNNING"))
                .collect(Collectors.toSet());

        games.forEach(
                gameModel -> {
                    if (gameModel.players.size() <= 1 || gameModel.players.stream().filter(p -> (p.lives > 0)).count() <= 1){
                        gameModel.state = "FINISHED";
                        GameModel.persist(gameModel);
                        System.out.println("Game with id " + gameModel.id + " finished");
                    }
                    else
                    {
                        gameModel.players.forEach(
                                playerModel -> {
                                    if (playerModel.lastBomb != null && new Timestamp(System.currentTimeMillis()).getTime() - playerModel.lastBomb.getTime() >= playerService.thickDuration * playerService.delayBomb)
                                    {
                                        System.out.println("Delay -> " + playerService.thickDuration * playerService.delayBomb);
                                        System.out.println("Game with id " + gameModel.id + " explosing");
                                        GameModel ne = playerService.exploseBomb(gameModel, new Pos(playerModel.bombPosX, playerModel.bombPosY));
                                        playerModel.lastBomb = null;
                                        PlayerModel.persist(playerModel);
                                        GameModel.persist(ne);
                                    }
                                }
                        );
                    }
                }
        );

    }


}
