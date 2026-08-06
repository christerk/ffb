package com.fumbbl.ffb.test.skills.bb2020;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Bb2016IntegrationTest extends AbstractStateTest {

    @Test
    public void bb2016BothDownKnocksDownBoth() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2016")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").armour(2, 2).armour(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "BB2016: Both players fall on Both Down");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "BB2016: Both players fall on Both Down");
    }

    @Test
    public void bb2016BlockPreventsFallingOnBothDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2016")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Block")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").armour(2, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "BB2016: Attacker with Block stays standing on Both Down");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "BB2016: Defender without Block falls on Both Down");
    }

    @Test
    public void bb2016DodgeIsAvailableOnDefender() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2016")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 4, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("stumble");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "BB2016: Block should proceed with Dodge on POW_PUSHBACK");
    }
}
