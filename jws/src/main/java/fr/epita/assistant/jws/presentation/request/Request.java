package fr.epita.assistant.jws.presentation.request;

import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.presentation.responseObject.GameResponse;
import javassist.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@ApplicationScoped
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Request
{
    @Inject GameService gameService;

    @GET
    public Set<GameResponse> getAllGame()
    {
        return gameService.getAllGameRepsonse();
    }

    @GET
    @Path("/{id}")
    public GameEntity get(@PathParam("id") long id) throws NotFoundException
    {
        return gameService.getGame(id);
    }

    @POST
    @Path("/{id}")
    public GameEntity join(@PathParam("id") long id, PlayerEntity player) throws NotFoundException, BadRequestException
    {
        return gameService.joinGame(id, player);
    }

    @POST
    public GameEntity postNewGame(PlayerEntity player) throws BadRequestException
    {
        return gameService.newGame(player);
    }

    @PATCH
    @Path("/{id}/start")
    public GameEntity startGame(@PathParam("id") long id) throws NotFoundException
    {
        return gameService.startGame(id);
    }
}
