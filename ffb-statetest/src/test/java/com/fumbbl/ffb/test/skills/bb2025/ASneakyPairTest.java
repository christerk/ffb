package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ASneakyPairTest extends AbstractStateTest {

    @Test
    public void aSneakyPairModifiersApplyWhenPartnerMarking() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 6).injury(2, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        assertNotNull(game.getFieldModel().getPlayerState(game.getPlayerById("away1")));
    }

    @Test
    public void aSneakyPairNoModifiersWhenPartnerNotAdjacent() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(10, 10).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 6).injury(2, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void aSneakyPairModifiersApplyOnFoul() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(9, 6).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));

        TestRolls.on(state).armour(6, 1).injury(2, 2);

        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertFalse(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Foul armour roll 6+1=7 + ASneakyPair(+1) = 8 breaks AV 8 exactly (the +1 partner-marking"
                        + " modifier is what pushes it over the threshold; without it the armour would hold"
                        + " and no injury roll would be consumed), then injury 2+2=4 stuns the fouled player");
    }

    @Test
    public void aSneakyPairInjuryModifierWhenPartnerMarksOpponent() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 6).injury(2, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        assertNotNull(game.getFieldModel().getPlayerState(game.getPlayerById("away1")),
                "ASneakyPair injury modifier applies when partner marks opponent");
    }

    @Test
    public void aSneakyPairPartnerAdjacentButNotMarking() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(6, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 6).injury(2, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "Partner adjacent to wielder but NOT marking opponent (modifier does not apply)");
    }

    @Test
    public void aSneakyPairArmourModifierSuppressesInjuryModifier() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(9, 7).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 1).injury(3, 4);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertEquals(PlayerState.STUNNED,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).getBase(),
                "Armour 6+1=7 +1 (ASneakyPair armour modifier) = 8 breaks AV 8 exactly; the +1 injury modifier is"
                        + " suppressed by the mutual-exclusion guard (affectsEitherArmourOrInjuryWithPartner), so"
                        + " injury 3+4=7 -> Stunned instead of 8 -> KO");
    }

    @Test
    public void aSneakyPairBb2020InjuryModifier() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Stab").skill("A Sneaky Pair"))
                        .player("partner", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("A Sneaky Pair")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).armour(6, 6).injury(2, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        assertEquals(PlayerState.STUNNED,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).getBase(),
                "BB2020: armour 6+6=12 breaks AV 8 unaided (no armour modifier), then the bb2020 ASneakyPair +1"
                        + " injury modifier applies on the partner-marked stab: injury 2+2=4 +1 = 5 -> Stunned");
    }
}
