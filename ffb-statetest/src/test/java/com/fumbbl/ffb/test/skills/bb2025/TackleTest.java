package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TackleTest extends AbstractStateTest {

    @Test
    public void tackleCancelsDodgeOnPowPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("stumble")
                .armour(6, 6)
                .injury(2, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Tackle cancels Dodge on pow/pushback - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Expected PUSHBACK step directly when Tackle cancels Dodge");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Tackle cancels Dodge on pow/pushback - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertFalse(defenderState.isStanding(),
                "Expected defender to be down (not standing) when Dodge is cancelled by Tackle, was "
                        + defenderState.getBase());
    }

    @Test
    public void tackleCancelsDodgeOnNonBlitzDodge() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Dodge")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep(),
                "Tackle cancels Dodge on non-blitz dodge - game in valid state after failed dodge (Tackle prevents Dodge reroll)");
        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Dodge D6=1 fails (natural 1 auto-fails against target 3+) and Tackle cancels canRerollDodge, so no Dodge"
                        + " reroll is offered and home1 falls prone (armour 1+1=2 does not break AV8)");
    }

    @Test
    public void tackleCancelsDodgeOnDefenderStumblesResult() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("stumble").armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Tackle cancels Dodge on defender stumbles result - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Stumble goes straight to PUSHBACK with no BLOCK_DODGE step when Tackle cancels Dodge");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));
        assertNotNull(state.getCurrentStep(),
                "Tackle cancels Dodge on defender stumbles result - game in valid state (Tackle prevents Dodge from converting Stumble to Pushback)");
        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Tackle cancels Dodge's ignoreDefenderStumblesResult, so the Stumble resolves as a knockdown: armour(6,6)"
                        + " breaks AV8 and injury(2,2) stuns away1");
    }

    @Test
    public void tackleVsWatchOutCancelsIgnoresDefenderStumblesResult() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Watch Out")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("stumble").armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Tackle vs Watch Out - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "No BLOCK_DODGE step when Tackle cancels Watch Out's first-block stumble protection");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));
        assertNotNull(state.getCurrentStep(),
                "Tackle vs Watch Out cancels/ignores defender stumbles result - game in valid state after Stumble with Tackle vs Watch Out");
        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Tackle cancels Watch Out's ignoresDefenderStumblesResultForFirstBlock, so the first-block Stumble still"
                        + " resolves as a knockdown: armour(6,6) breaks AV8 and injury(2,2) stuns away1");
    }

    @Test
    public void tackleCancelsDodgeDodgeReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Dodge")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep(),
                "Tackle cancels Dodge reroll - game in valid state after failed dodge (Tackle prevents Dodge skill reroll)");
        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "The failed dodge (D6=1) cannot be rerolled because Tackle cancels canRerollDodge, so home1 falls prone"
                        + " (armour 1+1=2 does not break AV8)");
    }

    @Test
    public void tackleVsBlitzDodgeOnBlitz() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Tackle")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("stumble")
                .armour(6, 6)
                .injury(2, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Tackle vs blitz Dodge - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "No BLOCK_DODGE step during blitz when Tackle cancels Dodge's stumble protection");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Tackle vs blitz Dodge - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "Tackle cancels Dodge's ignoreDefenderStumblesResult during the blitz, so the Stumble resolves as a"
                        + " knockdown: armour(6,6) breaks AV8 and injury(2,2) stuns the defender");
    }
}
