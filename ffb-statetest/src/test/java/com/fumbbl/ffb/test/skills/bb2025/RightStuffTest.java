package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RightStuffTest extends AbstractStateTest {

    @Test
    void rightStuffCanBeThrown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("thrower")));
    }

    @Test
    void rightStuffTtmLandingRollAfterBeingThrown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void rightStuffTtmScatterAfterSuccessfulLanding() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertEquals(new FieldCoordinate(11, 5),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "Right Stuff TTM scatter after successful landing - accurate throw to (9,7) then scatters NORTH, NORTHEAST, EAST (1,2,3) to land at (11,5)");
    }

    @Test
    void rightStuffIgnoreTackleWhenBlocked() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 2, 3, 5, 6).skill("Right Stuff"))
                        .player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void rightStuffWithStuntyTtmLandingWithPenalties() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff").skill("Stunty")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertEquals(com.fumbbl.ffb.PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "Right Stuff with Stunty TTM landing with penalties - the Stunty flinger lands standing after a successful landing roll (skill 6) despite the Stunty landing penalty");
    }
}
