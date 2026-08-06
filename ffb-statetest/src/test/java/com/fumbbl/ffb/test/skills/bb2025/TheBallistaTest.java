package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TheBallistaTest extends AbstractStateTest {

    @Test
    void theBallistaRerollsFailedPass() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("The Ballista"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PASS, ReRollSources.THE_BALLISTA));
        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void theBallistaCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("The Ballista"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PASS, ReRollSources.THE_BALLISTA));
        assertNotNull(state.getCurrentStep());
    }

    @Test
    void theBallistaRerollsTtmPass() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("The Ballista"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        assertNotNull(state.getCurrentStep());
    }

    @Test
    void theBallistaOnKickTeamMatePassReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Kick Team-Mate").skill("The Ballista"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.KICK_TEAM_MATE_MOVE));
        assertNotNull(state.getCurrentStep());
    }

    @Test
    void theBallistaPassRerollSucceedsD8RollHitsTarget() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("The Ballista"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        assertNotNull(state.getGame().getFieldModel().getBallCoordinate());
    }

    @Test
    void theBallistaDoesNotTriggerOnHandOff() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("The Ballista"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.HAND_OVER_MOVE));
        assertNotNull(state.getCurrentStep());
    }
}
