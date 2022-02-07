package fr.epita.assistant.jws.presentation.request;


import fr.epita.assistant.jws.domain.entity.GameEntity;
import fr.epita.assistant.jws.domain.entity.Pos;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.PlayerService;
import javassist.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerRequest
{
    @Inject GameService gameService;
    @Inject PlayerService playerService;

    @POST
    @Path("{gameId}/players/{playerId}/move")
    public GameEntity move(@PathParam("gameId") long gameId, @PathParam("playerId") long playerId, Pos pos) throws NotFoundException
    {
        return playerService.movePlayer(gameId, playerId, pos);
    }

    @POST
    @Path("{gameId}/players/{playerId}/bomb")
    public GameEntity bomb(@PathParam("gameId") long gameId, @PathParam("playerId") long playerId, Pos pos) throws NotFoundException
    {
        return playerService.putBomb(gameId, playerId, pos);
    }
}
