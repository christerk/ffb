package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ArmourIncreaseTest extends AbstractStateTest {

    @Test
    void plusAVRaisesArmourBreakThreshold() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 9).skill("+AV")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(PlayerState.PRONE,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).getBase(),
                "+AV 9 vs armor roll 8 holds, defender PRONE after failed armour break");
    }

    @Test
    void twoPlusAVStacksForHigherArmour() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Mighty Blow")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 10).skill("+AV").skill("+AV")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 4).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Two +AV stack: AV10+2=12, Mighty Blow armor(6,4)+1=11 does not break AV12, defender standing after pushback");
    }

    @Test
    void plusAVVsChainsawArmorModifier() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Chainsaw")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 7).skill("+AV")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).chainsaw(3).armour(2, 3).injury(3, 2);
        StepEngine.respond(state, Commands.chainsaw("home1", "away1"));

        assertNotNull(state.getCurrentStep(),
                "+AV vs Chainsaw +3 armor modifier (AV 7+AV=8, Chainsaw armor(2,3)+3=8 breaks)");
    }

    @Test
    void plusAVVsClawsArmorCap() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Claws")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 7).skill("+AV")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(4, 4).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "+AV vs Claws armor cap (AV 7+AV=8, Claws caps at 8+, armor(4,4)=8 still breaks)");
    }
}
