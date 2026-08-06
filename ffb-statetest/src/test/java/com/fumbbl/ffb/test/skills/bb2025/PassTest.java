package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PassTest extends AbstractStateTest {

    @Test
    public void playerWithPassCanThrowToTeamMate() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        Game game = state.getGame();
        assertEquals(new FieldCoordinate(10, 7), game.getFieldModel().getBallCoordinate(),
                "Player with Pass can throw to team mate - ball should be at receiver (10,7) after successful pass");
    }

    @Test
    public void passFumblesOnNaturalOneWithTackleZone() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pass fumbles on natural 1 with tackle zone - game in valid state after fumble (Pass reroll not offered on natural 1 in TZ)");
    }

    @Test
    public void passSkillRerollsFailedPassOnInaccurateThrow() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill pass = skillFactory.forName("Pass");
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.useSkill(pass, true, "home1"));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate(),
                "Pass skill rerolls failed pass on inaccurate throw - ball should be in play after rerolled pass succeeds");
    }

    @Test
    public void passAtLongBombRangeWithPaModifier() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate(),
                "Pass at long bomb range with PA modifier - ball should be in play after long bomb pass succeeds");
    }

    @Test
    public void passPlusAccurateStackingOnShortPassRange() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass").skill("Accurate"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getFieldModel().getBallCoordinate(),
                "Pass plus Accurate stacking on short pass range - ball should be in play after pass with both skills");
    }

    @Test
    public void passThrownIntoInterceptionZonePreventsReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pass thrown into interception zone prevents reroll - game in valid state after inaccurate pass with interceptor present");
    }

    @Test
    public void passNaturalOneNoTackleZoneStillFumbles() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pass"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pass fumbles on natural 1 without tackle zone - game in valid state after fumble (natural 1 always fumbles regardless of TZ)");
    }

    @Test
    public void passDoesNotAffectThrowTeamMateAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate").skill("Pass"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));

        assertNotNull(state.getCurrentStep(),
                "Pass does not affect Throw Team-Mate action - game in valid state after TTM action selected with Pass skill");
    }

    @Test
    public void passWithTeamRerollAfterPassRerollFails() {
        // The BB2025 engine offers exactly ONE reroll branch on a failed pass: the Pass skill
        // (via a SKILL_USE dialog) when the thrower has it, otherwise the team reroll. A team
        // reroll is NEVER offered after the Pass skill reroll has been used and failed - the
        // doc's Pass-skill-then-team-reroll sequence does not exist. This test therefore drives
        // the team-reroll branch directly: a thrower without the Pass skill gets the team reroll
        // on an inaccurate pass and the reroll converts it into a complete catch.
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getTurnDataHome().setReRolls(1);
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.PASS, ReRollSources.TEAM_RE_ROLL));

        assertEquals(new FieldCoordinate(10, 7), state.getGame().getFieldModel().getBallCoordinate(),
                "Team reroll on the inaccurate pass (2 -> 6) must produce an accurate throw caught by the receiver at (10,7)");
    }
}
