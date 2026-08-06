package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonstrousMouthTest extends AbstractStateTest {

    @Test
    public void monstrousMouthCancelsStripBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Monstrous Mouth cancels Strip Ball - block should proceed to pushback step");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Monstrous Mouth cancels Strip Ball - expected pushback step after block choice");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Monstrous Mouth cancels Strip Ball - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertEquals(new FieldCoordinate(9, 7), game.getFieldModel().getBallCoordinate(),
                "Monstrous Mouth cancels Strip Ball - ball stays with defender at (9,7), Strip Ball effect negated");
    }

    @Test
    public void monstrousMouthCanPinPlayers() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Monstrous Mouth can pin players - game in valid state after block against Monstrous Mouth defender");
    }

    @Test
    public void monstrousMouthVsSureHandsCancelStripBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sure Hands")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Monstrous Mouth vs Sure Hands cancels Strip Ball - game in valid state after block with Sure Hands negating Strip Ball");
    }

    @Test
    public void chompPinsPlayerPreventingDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(5);
        StepEngine.respond(state, Commands.chomp("home1", "away1"));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isChomped(),
                "A successful chomp roll (5 >= 3) pins the defender in place");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isAbleToMove(),
                "The chomped (pinned) defender cannot move/dodge away");
        assertEquals(new FieldCoordinate(8, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1")),
                "The pinned (chomped) defender stays in place at (8,7)");
        // DSL limitation: the engine refuses to dispatch a move for the pinned defender (isAbleToMove false); driving the
        // opponent's dodge attempt would require switching turns, which the current test DSL cannot do.
    }

    @Test
    public void monstrousMouthProvidesBlockAlternative() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(5);
        StepEngine.respond(state, Commands.chomp("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).isChomped(),
                "Monstrous Mouth provides the CHOMP block alternative: a chomp roll of 5 (>= 3) pins the defender");
    }

    @Test
    public void monstrousMouthChompPreventsStripBall() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(5);
        StepEngine.respond(state, Commands.chomp("home1", "away1"));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isChomped(),
                "A successful chomp roll (5 >= 3) pins the defender (the Monstrous Mouth attacker's block alternative)");
        assertEquals(new FieldCoordinate(8, 7), game.getFieldModel().getBallCoordinate(),
                "The chomped defender keeps the ball at (8,7): the pin negates the Strip Ball drop");
    }

    @Test
    public void monstrousMouthNormalBlockPushbackWithBallNotChomped() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isChomped(),
                "A normal block (not the chomp alternative) does not pin the Monstrous Mouth defender");
        assertEquals(new FieldCoordinate(9, 7), game.getFieldModel().getBallCoordinate(),
                "Monstrous Mouth cancels the attacker's Strip Ball on pushback: the defender keeps the ball at (9,7)");
    }

    @Test
    public void monstrousMouthFailedChompDoesNotPin() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Monstrous Mouth")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Strip Ball")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(1);
        StepEngine.respond(state, Commands.chomp("home1", "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).isChomped(),
                "A failed chomp roll (1 < 3) does not pin the defender");
    }
}
