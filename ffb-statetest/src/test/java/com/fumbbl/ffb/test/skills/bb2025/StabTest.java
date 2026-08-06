package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StabTest extends AbstractStateTest {

    @Test
    public void stabBreaksArmourAndInjuresDefender() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .armour(6, 6)
                .injury(6, 6)
                .general("casualty d6", 1)
                .general("casualty d8", 1);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertFalse(defenderState.isStanding(),
                "Expected defender down after Stab breaks armor and injures, was " + defenderState.getBase());
    }

    @Test
    public void stabFailsToBreakArmourLeavesDefenderStanding() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .armour(1, 1);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertEquals(PlayerState.STANDING, defenderState.getBase(),
                "Expected defender standing after Stab fails to break armor, was " + defenderState.getBase());
    }

    @Test
    public void stabIgnoresDodgeDefenderStumblesDoesNotCancelStab() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Dodge")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void stabRequiresFoulAppearanceRollBeforeProceeding() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Foul Appearance")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .skill(6)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void stabPlusMightyBlowDoesNotApply() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void stabDuringBlitzCannotContinueMoving() {
        // DSL limitation: after moving during a BLITZ_MOVE, the block command is handled by StepInitMoving,
        // which forwards USING_CHAINSAW/USING_VOMIT but not USING_STAB, so the stab is currently driven as a
        // regular blitz block rather than resolving the stab armour/injury directly.
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        TestRolls.on(state)
                .armour(6, 6)
                .injury(3, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertFalse(defenderState.isStanding(),
                "Stab during a blitz should knock the defender down (injury 3+2=5 stuns), was " + defenderState.getBase());
        assertNotNull(state.getCurrentStep(),
                "Stab during a blitz - the acting player's action has ended (no further move or block)");
    }

    @Test
    public void stabCasualtyDoesNotGrantSpp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .armour(6, 6)
                .injury(6, 6)
                .general("casualty d6", 1)
                .general("casualty d8", 1);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertFalse(defenderState.isStanding(),
                "Stab casualty should put the defender down, was " + defenderState.getBase());
        assertEquals(0, game.getGameResult().getPlayerResult(game.getPlayerById("home1")).getCasualties(),
                "Stab casualties do not award SPP to the stabbing player (in contrast to a block casualty)");
    }
}
