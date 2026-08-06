package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.AbstractStateTest;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KickTeamMateTest extends AbstractStateTest {

    private GameState ktmState(boolean swoop) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025");
        builder.withTeam(true, t -> {
            t.player("kicker", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Kick Team-Mate"));
            t.player("flinger", p -> {
                p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff");
                if (swoop) {
                    p.skill("Swoop");
                }
            });
        });
        builder.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)));
        return builder.build();
    }

    private void executeKick(GameState state, int throwRoll, int landingRoll) {
        TestRolls.on(state).skill(throwRoll)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(landingRoll);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(9, 7)));
    }

    @Test
    void kickTeamMateActionWorks() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        executeKick(state, 4, 6);

        assertTrue(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).isStanding(),
                "A successful KTM kick: the flinger is thrown to the target, scatters three times and lands standing");
        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("flinger")),
                "Kick Team-Mate action works - flinger has a valid position after the kick");
    }

    @Test
    void ktmScatterDirectionAndDistance() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        executeKick(state, 4, 6);

        assertEquals(new FieldCoordinate(11, 5),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "KTM scatter: the flinger lands at the target (9,7) and scatters NORTH, NORTHEAST, EAST (1,2,3) to (11,5)");
    }

    @Test
    void ktmLandingOnEmptySquare() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        executeKick(state, 4, 6);

        assertEquals(PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "KTM landing on an empty square: the landing roll (6) succeeds and the flinger is standing at (11,5)");
    }

    @Test
    void ktmWithSwoopScatterDirectionControl() {
        GameState state = ktmState(true);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        TestRolls.on(state).skill(4);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(9, 7)));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("flinger")),
                "A Swoop flinger can be kicked: the kick completes and the swooping flinger gets a valid landing position");
    }

    @Test
    void ktmLandingFailure() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        TestRolls.on(state).skill(4)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(1).armour(1, 1);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(9, 7)));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "A failed landing roll (1) after the kick knocks the flinger down; armour 1+1=2 holds vs AV6 so he ends PRONE");
    }

    @Test
    void ktmFumbledThrowInjuresKickedPlayer() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", "flinger", true));

        TestRolls.on(state).skill(1).injury(3, 2);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(9, 7), true));

        assertEquals(PlayerState.KNOCKED_OUT, state.getGame().getFieldModel().getPlayerState(
                        state.getGame().getPlayerById("flinger")).getBase(),
                "A fumbled KTM throw immediately injures the kicked team-mate, counting a Stunned result"
                        + " (injury 3+2=5) as Knocked Out");
    }

    @Test
    void ktmKickedPlayerLandsCrowd() {
        GameState state = ktmState(false);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", "flinger", true));

        TestRolls.on(state).skill(4)
                .scatterDirection(7).scatterDirection(7).scatterDirection(7)
                .injury(1, 1);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(2, 7), true));

        FieldCoordinate flingerCoordinate = state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("flinger"));
        assertNotNull(flingerCoordinate, "The kicked player has a position after the kick");
        assertFalse(FieldCoordinateBounds.FIELD.isInBounds(flingerCoordinate),
                "The kicked player scatters WEST off the pitch edge into the crowd (target (2,7) -> (1,7) -> (0,7) -> out of bounds)");
    }
}
