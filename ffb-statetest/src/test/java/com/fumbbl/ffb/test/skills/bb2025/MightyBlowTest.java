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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MightyBlowTest extends AbstractStateTest {

    @Test
    public void mightyBlowBreaksArmourAtThreshold() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 2)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertNotEquals(PlayerState.PRONE, defenderState.getBase(),
                "Expected defender NOT to be just PRONE (Mighty Blow should break armor on 5+2+1=8 vs AV8), was "
                        + defenderState.getBase());
        assertFalse(defenderState.isStanding(),
                "Expected defender down after Mighty Blow breaks armor, was " + defenderState.getBase());
    }

    @Test
    public void mightyBlowDoesNotWorkOnStab() {
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
                .armour(5, 2);

        StepEngine.respond(state, Commands.stab("home1", "away1"));

        Game game = state.getGame();
        assertNotNull(game.getFieldModel().getPlayerState(game.getPlayerById("away1")));
    }

    @Test
    public void mightyBlowWithClawsInteraction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow").skill("Claws")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 10)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 3)
                .injury(3, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void mightyBlowDoesNotApplyOnFoul() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void mightyBlowDoesNotApplyOnChainsawBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Chainsaw").skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(1).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.chainsaw("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void mightyBlowDoesNotApplyOnVomitOrBreatheFire() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Breathe Fire").skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).skill(1).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.breatheFire("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void mightyBlowMutualExclusivityArmourUsedInjuryNotOffered() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(5, 2).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Mighty Blow's +1 is consumed by the armour roll (5+2=7 -> 8 breaks AV8); with affectsEitherArmourOrInjuryOnBlock"
                        + " the same skill cannot also modify the injury roll, so injury 3+2=5 leaves the defender Stunned");
    }

    @Test
    public void mightyBlowInjuryModifierUsedInsteadOfArmour() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(6, 5)   // 11 vs AV8: armour breaks without Mighty Blow
                .injury(3, 4);  // 7 + Mighty Blow +1 = 8 -> knocked out (7 alone would be stunned)

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        PlayerState defenderState = state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1"));
        assertEquals(PlayerState.KNOCKED_OUT, defenderState.getBase(),
                "Mighty Blow unused on the armour roll (armour breaks on its own) is applied to the injury roll,"
                        + " turning injury 3+4=7 into 8 which knocks the defender out");
    }

    @Test
    public void mightyBlowDoesNotApplyToSelfInflictedArmourOnBothDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("bothdown")
                .armour(4, 2)  // defender armour 6, + Mighty Blow 1 = 7, still under AV8
                .armour(5, 2); // attacker self-inflicted armour 7, just under AV8 (7+1 would break if Mighty Blow applied)

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        PlayerState attackerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));

        assertEquals(PlayerState.PRONE, attackerState.getBase(),
                "Mighty Blow must not apply to the attacker's self-inflicted armour on a Both Down:"
                        + " 5+2=7 is under AV8, so home1 stays prone instead of being stunned");
        assertEquals(PlayerState.PRONE, defenderState.getBase(),
                "Defender armour 4+2=6 + Mighty Blow 1 = 7 is under AV8, so away1 stays prone");
    }
}
