package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.GameModel;
import fr.epita.assistant.jws.data.model.PlayerModel;
import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.entity.Pos;
import fr.epita.assistant.jws.utils.Map;
import javassist.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PlayerService
{
    public @ConfigProperty(name="JWS_TICK_DURATION") int thickDuration;
    public @ConfigProperty(name="JWS_DELAY_MOVEMENT") int delayMovement;
    public @ConfigProperty(name="JWS_DELAY_BOMB") int delayBomb;
    @Inject GameService gameService;

    public PlayerEntity modelToEntity(PlayerModel playerModel)
    {
        return new PlayerEntity(
                playerModel.id,
                playerModel.name,
                playerModel.lives,
                playerModel.posx,
                playerModel.posy
        );
    }

    @Transactional
    public GameEntity movePlayer(long gameId, long playerId, Pos pos) throws NotFoundException, BadRequestException
    {
        GameModel game = gameService.getGameModel(gameId);
        if (game == null || game.players.stream().noneMatch(player -> player.id == playerId))
            throw new NotFoundException("The game with this ID, or the player does not exist");
        PlayerModel playerModel = game.players.stream()
                .filter(player -> player.id == playerId)
                .findFirst()
                .orElse(null);
        if (!Objects.equals(game.state, "RUNNING")
                || playerModel.lives <= 0
                || pos.posX > 17
                || pos.posY > 15
                || Map.formatMapNoRLE(game.map.map).get(pos.posY).charAt(pos.posX) != 'G')
            throw new BadRequestException("The game is not running or the player is already dead. Or, the player cannot move to the specified position");

        if (playerModel.lastMovement != null && new Timestamp(System.currentTimeMillis()).getTime() - playerModel.lastMovement.getTime() >= thickDuration * delayMovement)
            throw new WebApplicationException("Too many request", 429);

        playerModel.posx = pos.posX;
        playerModel.posy = pos.posY;
        PlayerModel.persist(playerModel);
        return gameService.modelToEntity(game);
    }

    @Transactional
    public GameEntity putBomb(long gameId, long playerId, Pos pos) throws NotFoundException
    {
        System.out.println("Putting bomb in " + pos + " for player " + playerId + " in game " + gameId + ".");
        GameModel game = gameService.getGameModel(gameId);
        if (game == null || game.players.stream().noneMatch(player -> player.id == playerId))
            throw new NotFoundException("The game with this ID, or the player does not exist");

        PlayerModel player = game.players.stream()
                .filter(p -> p.id == playerId)
                .findFirst()
                .orElse(null);

        if (pos == null
                || !Objects.equals(game.state, "RUNNING")
                || player.lives <= 0
                || pos.posX > 17
                || pos.posY > 15
                || Map.formatMapNoRLE(game.map.map).get(pos.posY).charAt(pos.posX) != 'G')
            throw new BadRequestException("The request is null, or the player name is null or the game cannot be started (already started, too many players), or not the same as the player");


        if (player.lastBomb != null)
            throw new WebApplicationException("Too many request", 429);

        System.out.println("Bomb posed");
        game.map.map = Map.putMap(game.map.map, pos, 'B');
        player.lastBomb = new Timestamp(System.currentTimeMillis());
        player.bombPosX = pos.posX;
        player.bombPosY = pos.posY;
        PlayerModel.persist(player);
        GameModel.persist(game);


        return gameService.modelToEntity(game);
    }

    public GameModel exploseBomb(GameModel game, Pos pos)
    {
        System.out.println("Explosing");
        String map = game.map.map;
        map = Map.putMap(map, pos, 'G');
        List<String> mapL = Map.formatMapNoRLE(map);
        int startX = pos.posX == 0 ? 0 : pos.posX - 1;
        int startY = pos.posY == 0 ? 0 : pos.posY - 1;
        int endX = pos.posX >= 16 ? 16 : pos.posX + 1;
        int endY = pos.posY >= 16 ? 16 : pos.posY + 1;

        for (int x = startX; x <= endX; x++)
        {
            for (int y = startY; y <= endY; y++)
            {
                if (mapL.get(y).charAt(x) == 'W')
                {
                    if ((Math.abs(x - (startX + 1)) + Math.abs(y - (startY + 1))) >= 2)
                    {
                        System.out.println("Ignoring x " + x + " y " + y);
                        continue;
                    }
                    System.out.println("Destroying at " + pos);
                    map = Map.putMap(map, new Pos(x, y), 'G');
                    mapL = Map.formatMapNoRLE(map);
                }
            }
        }
        for (PlayerModel player : game.players)
        {
            if (Math.abs(player.posx - pos.posX) <= 1
                && Math.abs(player.posy - pos.posY) == 0
                || Math.abs(player.posy - pos.posY) <= 1
                && Math.abs(player.posx - pos.posX) == 0)
            {
                player.lives--;
            }
        }
        game.map.map = map;
        System.out.println("New map " + game.map.map);
//        System.out.println("nnn map " + GameMapModel.<GameModel>findById(game.id).map.map);
//        GameMapModel.persist(game.map);
//        GameModel.persist(game);
        return game;
    }

}