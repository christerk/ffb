package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest extends AbstractStateTest {

    @Test
    public void blockFlowSelectsPlayerAndInitiatesBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        IStep step = StepEngine.start(state);
        assertNotNull(step, "Expected a step after start");

        step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        assertNotNull(step, "Expected a step after selectPlayer");

        TestRolls.on(state).block("pushback");
        step = StepEngine.respond(state, Commands.block("home1", "away1"));
        assertNotNull(step, "Expected a step after block command");
    }

    @Test
    public void bothDownResolvesBothPlayersToProne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        TestRolls.on(state)
                .block("bothdown")
                .armour(2, 2)
                .armour(2, 2);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));

        assertTrue(attackerState.getBase() == PlayerState.PRONE || attackerState.getBase() == PlayerState.STUNNED,
                "Expected attacker to be PRONE or STUNNED after BOTH_DOWN without Block skill, was " + attackerState.getBase());
        assertTrue(defenderState.getBase() == PlayerState.PRONE || defenderState.getBase() == PlayerState.STUNNED,
                "Expected defender to be PRONE or STUNNED after BOTH_DOWN, was " + defenderState.getBase());
    }

    @Test
    public void bothDownWithBlockSkillOnlyDefenderFalls() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Block")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        TestRolls.on(state)
                .block("bothdown")
                .armour(2, 2);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));

        assertEquals(PlayerState.STANDING, attackerState.getBase(),
                "Expected attacker to be STANDING after BOTH_DOWN with Block skill, was " + attackerState.getBase());
        assertTrue(defenderState.getBase() == PlayerState.PRONE || defenderState.getBase() == PlayerState.STUNNED,
                "Expected defender to be PRONE or STUNNED after BOTH_DOWN, was " + defenderState.getBase());
    }

    @Test
    public void pushbackResolvesWithNoFallAndPlayersStanding() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep pushStep = StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(pushStep, "Expected a step after block choice for pushback flow");
        assertEquals(StepId.PUSHBACK, pushStep.getId(), "Expected PUSHBACK step after block choice with pushback result");

        pushStep = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(pushStep, "Expected a step after pushback command");
        assertEquals(StepId.FOLLOWUP, pushStep.getId(), "Expected FOLLOWUP step after pushback completes");

        pushStep = StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));

        assertEquals(PlayerState.STANDING, attackerState.getBase(),
                "Expected attacker to be STANDING after pushback, was " + attackerState.getBase());
        assertEquals(PlayerState.STANDING, defenderState.getBase(),
                "Expected defender to be STANDING after pushback, was " + defenderState.getBase());
    }
}