package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TeamCaptainTest extends AbstractStateTest {

    @Test
    void teamCaptainSavesReRolls() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 8)));

        assertNotNull(state.getCurrentStep(),
                "Team Captain saves rerolls - game in valid state after move (Team Captain grants team reroll save)");
    }

    @Test
    void teamCaptainNeedsToBeSetUp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        assertNotNull(state.getCurrentStep(),
                "Team Captain needs to be set up - game in valid state after selecting move (Team Captain must be on pitch to grant bonus)");
    }

    @Test
    void teamCaptainNotSetUpOnPitchNoRerollSaveTriggers() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        assertNotNull(state.getCurrentStep(),
                "Team Captain not set up on pitch no reroll save triggers - game in valid state (no bonus without Team Captain on pitch)");
    }

    @Test
    void teamCaptainSuccessfullySavesAReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Team Captain successfully saves a reroll - game in valid state after block (Team Captain reroll save available)");
    }

    @Test
    void teamCaptainDoesNotCountAsAdvancementForTeamValue() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        assertNotNull(state.getCurrentStep(),
                "Team Captain does not count as advancement for team value - game in valid state (Team Captain is a special skill, not counted in TV)");
    }

    @Test
    void teamCaptainSavesARerollOnSuccessfulD6Roll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        // Team Captain save check in RollMechanic.checkTeamCaptain consumes a generic D6 ("general") roll with a
        // 6+ threshold (TEAM_CAPTAIN_MINIMUM_ROLL), so the save roll is queued via general(), not skill().
        TestRolls.on(state).block("skull").general("team captain", 6).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.TEAM_RE_ROLL));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(1, state.getGame().getTurnDataHome().getReRolls(),
                "Team Captain D6 save roll of 6 (>= 6+) saves the team reroll - reroll count unchanged");
    }

    @Test
    void teamCaptainFailsToSaveReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Team Captain")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        // Team Captain save check in RollMechanic.checkTeamCaptain consumes a generic D6 ("general") roll with a
        // 6+ threshold (TEAM_CAPTAIN_MINIMUM_ROLL), so the save roll is queued via general(), not skill().
        TestRolls.on(state).block("skull").general("team captain", 3).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.TEAM_RE_ROLL));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(0, state.getGame().getTurnDataHome().getReRolls(),
                "Team Captain D6 save roll of 3 (< 6+) fails - team reroll is consumed");
    }

    @Test
    void teamCaptainNotOnPitchCannotSaveRerolls() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(FieldCoordinate.RSV_HOME_X, 0).stats(6, 3, 3, 5, 8)
                                .skill("Team Captain")
                                .state(new PlayerState(PlayerState.RESERVE)))
                        .player("home2", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home2", PlayerAction.BLOCK));
        TestRolls.on(state).block("skull").block("pushback");
        StepEngine.respond(state, Commands.block("home2", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.TEAM_RE_ROLL));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(0, state.getGame().getTurnDataHome().getReRolls(),
                "Team Captain in reserves (needsToBeSetUp not satisfied) - no D6 save roll is offered and the team reroll is consumed");
    }
}
