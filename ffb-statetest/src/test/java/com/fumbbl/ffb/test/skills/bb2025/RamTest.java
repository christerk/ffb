package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RamTest extends AbstractStateTest {

    @Test
    public void ramArmourModifierApplies() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 2)
                .injury(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram armor modifier applies - block choice processed");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Ram armor modifier applies - expected pushback step after Pow block choice");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram armor modifier applies - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertFalse(defenderState.isStanding(),
                "Ram armor modifier applies - expected defender down after Ram (+1 armor) breaks armor (5+2+1=8 = AV8), was " + defenderState.getBase());
    }

    @Test
    public void ramCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 2)
                .injury(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram cannot use twice per game - first block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram cannot use twice per game - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Ram cannot use twice per game - game in valid state (Ram consumed after first use)");
    }

    @Test
    public void ramStaticInjuryModifierPlusOneAppliesOnInjuryRoll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(6, 6)
                .injury(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram static injury modifier +1 applies on injury roll - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram static injury modifier +1 applies on injury roll - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Ram static injury modifier +1 applies on injury roll - game in valid state after armor broken and injury(1,1)=2+1=3 stunned");
    }

    @Test
    public void ramBothArmourAndInjuryModifiersOnSameBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 2)
                .injury(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram both armor and injury modifiers on same block - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram both armor and injury modifiers on same block - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Ram both armor and injury modifiers on same block - game in valid state with both +1 armor and +1 injury applied");
    }

    @Test
    public void ramConsumedAfterOneUseSecondBlockNoModifiers() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(5, 2)
                .injury(1, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram consumed after one use - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram consumed after one use - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Ram consumed after one use, second block has no modifiers - game in valid state (Ram cannot be used again this game)");
    }

    @Test
    public void ramInjuryModifierNotEnoughForCasualty() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(6, 6)
                .injury(5, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram injury modifier not enough for casualty - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram injury modifier not enough for casualty - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Ram injury modifier not enough for casualty - expected Ram skill use dialog for the injury modifier");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Ram injury modifier not enough for casualty - expected skill use dialog");
        assertEquals("Ram", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Ram injury modifier not enough for casualty - dialog should offer Ram");

        Skill ram = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Ram");
        StepEngine.respond(state, Commands.useSkill(ram, true, "home1"));

        Game game = state.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
        assertEquals(PlayerState.KNOCKED_OUT, defenderState.getBase(),
                "Ram injury modifier not enough for casualty - injury (5+2=7, +1 Ram = 8) reaches KO but stays below the casualty threshold (>8), was " + defenderState.getBase());
    }

    @Test
    public void ramPlusMightyBlowStackingOnArmorBreak() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Ram").skill("Mighty Blow")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(4, 1);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Ram plus Mighty Blow stacking on armor break - block choice processed");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Ram plus Mighty Blow stacking on armor break - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Ram plus Mighty Blow stacking on armor break - game in valid state with armor(4,1)=5+1 Ram+1 MB=7 vs AV8, armor holds");
    }
}
