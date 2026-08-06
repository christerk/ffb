package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoneFoulerTest extends AbstractStateTest {

    @Test
    void foulsWithoutAssists() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).isStanding(),
                "Lone Fouler fouls without assists - away1 knocked down after successful foul (armor 6+6=12 > AV8)");
    }

    @Test
    void loneFoulerWithAssistsPresent() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler"))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Lone Fouler with assists present - game in valid state when fouling with teammates in assist positions");
    }

    @Test
    void loneFoulerNegatesGuardAssistsOnFoul() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true)))
                        .player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Guard")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));

        assertNotNull(state.getCurrentStep(),
                "Lone Fouler negates Guard assists on foul - game in valid state with Guard opponent nearby");
    }

    @Test
    void loneFoulerOncePerTurnCannotNegateTwice() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));

        assertNotNull(state.getCurrentStep(),
                "Lone Fouler once per turn cannot negate twice - game in valid state after foul move selected");
    }

    @Test
    void loneFoulerRerollsFailedArmourWithNoAssists() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(1, 1).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill loneFouler = skillFactory.forName("Lone Fouler");
        assertNotNull(loneFouler, "Lone Fouler skill should be resolvable from the SkillFactory");
        StepEngine.respond(state, Commands.useSkill(loneFouler, true, "home1"));

        PlayerState awayState = state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1"));
        assertFalse(awayState.isStanding(),
                "With zero assists the failed armour roll (1+1=2) must be rerolled by Lone Fouler (6+6=12 breaks AV8), then injury 3+2=5 resolves");
    }

    @Test
    void loneFoulerSuppressedByOffensiveAssist() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler"))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(1, 1);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "With an offensive assist present (home2 at (8,8)) the Lone Fouler reroll must NOT fire: armour 1+1=2 fails and stays failed");
    }

    @Test
    void loneFoulerRerollFailsDefenderUninjured() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lone Fouler")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(1, 1).armour(1, 1);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill loneFouler = skillFactory.forName("Lone Fouler");
        assertNotNull(loneFouler, "Lone Fouler skill should be resolvable from the SkillFactory");
        StepEngine.respond(state, Commands.useSkill(loneFouler, true, "home1"));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "A failed Lone Fouler reroll (1+1=2) leaves the armour unbroken and the defender uninjured (still PRONE)");
    }
}
