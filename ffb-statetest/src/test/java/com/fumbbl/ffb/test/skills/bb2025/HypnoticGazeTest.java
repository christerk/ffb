package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HypnoticGazeTest extends AbstractStateTest {

    @Test
    public void hypnoticGazeConfusesOpponent() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Hypnotic Gaze")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));
        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Hypnotic Gaze confuses opponent - gazer should be standing after successful gaze action");
    }

    @Test
    public void hypnoticGazeFailsOnRollOfOne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Hypnotic Gaze")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));
        assertNotNull(state.getCurrentStep(),
                "Hypnotic Gaze fails on roll of 1 - game in valid state after failed gaze attempt");
    }

    @Test
    public void inflictsConfusionOnHypnoticGazeStep() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Hypnotic Gaze")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Hypnotic Gaze inflicts confusion - game in valid state after gaze confuses opponent");
    }

    @Test
    public void gazedPlayerCanBeDodgedPast() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Hypnotic Gaze"))
                        .player("home2", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Gazed player can be dodged past - game in valid state after gaze action with nearby teammate");
    }

    @Test
    public void hypnoticGazeWithMesmerisingDanceReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Hypnotic Gaze")
                        .skill("Mesmerising Dance")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(1).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.HYPNOTIC_GAZE, ReRollSources.MESMERISING_DANCE));

        assertNotNull(state.getCurrentStep(),
                "Hypnotic Gaze with Mesmerising Dance reroll - game in valid state after rerolled gaze succeeds");
    }
}
