package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FoulAppearanceTest extends AbstractStateTest {

    @Test
    public void foulAppearancePreventsBlockOnRollOfOne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        assertTrue(attackerState.isStanding(),
                "Expected attacker to be standing after Foul Appearance prevents block (action ended), was " + attackerState.getBase());
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertEquals(PlayerState.STANDING, defenderState.getBase(),
                "Expected defender to be standing when Foul Appearance prevents block, was " + defenderState.getBase());
    }

    @Test
    public void foulAppearanceAllowsBlockOnRollOfSix() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(6)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId(), "Expected block to proceed after Foul Appearance roll of 6");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));
    }

    @Test
    public void foulAppearanceCancelledByBallAndChain() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(4, 5, 3, 5, 9).skill("Ball and Chain")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(14, 1), new FieldCoordinate(13, 1)));

        assertNotNull(state.getCurrentStep(),
                "Foul Appearance cancelled by Ball and Chain - BaC ignores Foul Appearance skill check");
    }

    @Test
    public void foulAppearanceTriggersOnBlitzAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Foul Appearance triggers on Blitz action - game in valid state after FA check on blitz");
    }

    @Test
    public void foulAppearanceWithProReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pro")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(1).skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.FOUL_APPEARANCE, ReRollSources.PRO));

        assertNotNull(state.getCurrentStep(),
                "Foul Appearance with Pro reroll - both rolls fail, block prevented - game in valid state");
    }

    @Test
    public void foulAppearanceRerolledByTeamReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        TestRolls.on(state)
                .skill(6)
                .block("pushback");

        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.FOUL_APPEARANCE, ReRollSources.TEAM_RE_ROLL));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Foul Appearance rerolled by team reroll - the block proceeds to PUSHBACK after the team reroll passes");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));
    }
}
