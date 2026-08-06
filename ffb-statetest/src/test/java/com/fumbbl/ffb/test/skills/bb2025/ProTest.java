package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProTest extends AbstractStateTest {

    @Test
    void proAllowsOncePerTurnReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).isStanding());
    }

    @Test
    void proD4SucceedsAndGrantsReRoll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        TestRolls.on(state).skill(4).skill(6);
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.DODGE, ReRollSources.PRO));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")));
    }

    @Test
    void proD4FailsAndNoReRollGranted() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.DODGE, ReRollSources.PRO));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void canRerollOncePerTurnOnFailedCatch() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7)
                .withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(1).scatterDirection(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void canRerollOncePerTurnOnFailedGfi() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).goingForIt(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(9, 7)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(9, 7), new FieldCoordinate(10, 7)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(10, 7), new FieldCoordinate(11, 7)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(11, 7), new FieldCoordinate(12, 7)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(12, 7), new FieldCoordinate(13, 7)));
        TestRolls.on(state).skill(4).goingForIt(6);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(13, 7), new FieldCoordinate(14, 7)));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.RUSH, ReRollSources.PRO));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void canRerollOncePerTurnOnFailedDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        TestRolls.on(state).skill(4).skill(6);
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.DODGE, ReRollSources.PRO));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void proPlusLonerInteractionLonerPreventsProReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro").skill("Loner")))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    void proOncePerTurnSecondFailedRollBlocked() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pro")))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(7, 5).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        TestRolls.on(state).skill(4).skill(6);
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.DODGE, ReRollSources.PRO));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(8, 6)));

        assertNotNull(state.getCurrentStep());
    }
}
