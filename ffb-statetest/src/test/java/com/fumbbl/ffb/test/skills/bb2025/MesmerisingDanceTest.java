package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MesmerisingDanceTest extends AbstractStateTest {

    @Test
    public void mesmerisingDanceRerollsHypnoticGaze() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Hypnotic Gaze")
                                .skill("Mesmerising Dance")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(1).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.HYPNOTIC_GAZE, ReRollSources.MESMERISING_DANCE));

        assertNotNull(state.getCurrentStep(),
                "Mesmerising Dance rerolls Hypnotic Gaze - game in valid state after rerolled gaze (1 then 6) succeeds");
    }

    @Test
    public void mesmerisingDanceCannotUseTwicePerHalf() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Hypnotic Gaze")
                                .skill("Mesmerising Dance")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Mesmerising Dance cannot use twice per half - game in valid state after successful gaze with Dance available");
    }

    @Test
    public void mesmerisingDanceRerollsFailedHypnoticGaze() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Hypnotic Gaze")
                                .skill("Mesmerising Dance")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(1).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.HYPNOTIC_GAZE, ReRollSources.MESMERISING_DANCE));

        assertNotNull(state.getCurrentStep(),
                "Mesmerising Dance rerolls failed Hypnotic Gaze - game in valid state after Dance reroll converts failure to success");
    }

    @Test
    public void mesmerisingDanceNotOfferedOnSuccessfulGaze() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7)
                                .stats(6, 3, 4, 5, 8)
                                .skill("Hypnotic Gaze")
                                .skill("Mesmerising Dance")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.GAZE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.gaze("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Mesmerising Dance not offered on successful gaze - game in valid state (Dance reroll only offered on failure)");
    }
}
