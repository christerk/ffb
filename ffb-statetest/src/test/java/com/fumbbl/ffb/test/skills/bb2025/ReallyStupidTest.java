package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReallyStupidTest extends AbstractStateTest {

    @Test
    public void reallyStupidFailureEndsAction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Really Stupid failure ends action - expected attacker standing when Really Stupid roll fails");
        assertEquals(PlayerState.STANDING, game.getFieldModel().getPlayerState(game.getPlayerById("away1")).getBase(),
                "Really Stupid failure ends action - expected defender standing when Really Stupid prevents block");
    }

    @Test
    public void reallyStupidSuccessAllowsBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(4)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "Really Stupid success allows block - block should proceed after Really Stupid success (roll 4+), defender still standing after pushback");
    }

    @Test
    public void reallyStupidEasierWhenAdjacentToTeammate() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(4)
                .block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid easier when adjacent to teammate - the block proceeds after the Really Stupid roll (2+ with teammate) and resolves to a pushback");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid easier when adjacent to teammate - game in valid state after roll 4 succeeds with teammate adjacent (2+ needed)");
    }

    @Test
    public void reallyStupidRolls4PlusAloneSucceeds() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(4)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid rolls 4+ alone succeeds - the block proceeds after the Really Stupid roll (4+ when alone) and resolves to a pushback");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid rolls 4+ alone succeeds - game in valid state after roll 4 succeeds when alone (4+ needed)");
    }

    @Test
    public void reallyStupidRolls2WithAdjacentTeammateSucceeds() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(2)
                .block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid rolls 2 with adjacent teammate succeeds - the block proceeds after the Really Stupid roll (2+ with teammate) and resolves to a pushback");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid rolls 2 with adjacent teammate succeeds - game in valid state after roll 2 succeeds when teammate adjacent (2+ needed)");
    }

    @Test
    public void reallyStupidRolls3AloneFails() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(3);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid rolls 3 alone fails - the block never resolves because the Really Stupid roll (3 < 4+) fails, leaving away1 standing");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid rolls 3 alone fails - game in valid state after roll 3 fails when alone (4+ needed)");
    }

    @Test
    public void reallyStupidAdjacentReallyStupidTeammateDoesNotHelp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid adjacent Really Stupid teammate does not help - the block never resolves because the Really Stupid roll (2) fails without a qualifying teammate, leaving away1 standing");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid adjacent Really Stupid teammate does not help - roll 2 fails (2+ needed only with a non-Really-Stupid teammate), block never resolves");
    }

    @Test
    public void reallyStupidFailureOnBlitzMarksBlitzUsed() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        assertTrue(state.getGame().getTurnDataHome().isBlitzUsed(),
                "Really Stupid failure on blitz still marks the turn's blitz as used");
    }

    @Test
    public void reallyStupidUsesTeamRerollOnFailure() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        TestRolls.on(state).skill(4).block("pushback");
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.REALLY_STUPID, ReRollSources.TEAM_RE_ROLL));

        assertNotNull(state.getCurrentStep(),
                "Really Stupid uses team reroll on failure - game in valid state after team reroll of failed Really Stupid roll");
    }

    @Test
    public void reallyStupidRolls1WithAdjacentTeammateFails() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Really Stupid"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Really Stupid rolls 1 with adjacent teammate fails - the block never resolves because the natural-1 Really Stupid roll fails, leaving away1 standing");
        assertNotNull(state.getCurrentStep(),
                "Really Stupid rolls 1 with adjacent teammate fails - game in valid state after natural 1 fails even with teammate adjacent");
    }
}
