package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.AbstractStateTest;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JumpUpTest extends AbstractStateTest {

    @Test
    public void jumpUpAllowsFreeStandUp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(10, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(10, 7), playerPos,
                "Expected player to reach (10,7) after Jump Up free stand-up, was at " + playerPos);
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Expected player standing after Jump Up move");
    }

    @Test
    public void jumpUpWhileInTackleZone() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(6).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void jumpUpAndBlockInSameActivation() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(6).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void jumpUpAndBlitzInSameActivation() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(12, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void canStandUpForFreeOnStandUpStep() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(10, 7), playerPos);
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding());
    }

    @Test
    public void jumpUpBlockFailsAgilityRoll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.block("home1", "away1"));

        Game game = state.getGame();
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
        assertEquals(PlayerState.PRONE, attackerState.getBase(),
                "Jump Up agility roll failure (1) must waste the block action and leave the player prone");
        assertEquals(new FieldCoordinate(7, 7),
                game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1")),
                "Failed Jump Up player must not move");
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "Defender must be untouched when the Jump Up roll fails");
    }

    @Test
    public void jumpUpBlockTeamReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        state.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        TestRolls.on(state).skill(6).block("pushback");
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.JUMP_UP, ReRollSources.TEAM_RE_ROLL));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Team reroll of the failed Jump Up roll (1 -> 6) must stand the player up for the block");
    }

    @Test
    public void jumpUpNegatraitInteraction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Jump Up")
                                .skill("Bone Head")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(2).skill(6).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "Bone Head roll (2+) must resolve before the Jump Up roll, then the successful Jump Up stands the player up");
    }
}
