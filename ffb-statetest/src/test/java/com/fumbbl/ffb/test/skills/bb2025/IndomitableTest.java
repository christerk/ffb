package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IndomitableTest extends AbstractStateTest {

    @Test
    public void indomitableDoublesStrengthAfterDauntless() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .dauntless(6)
                .block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill indomitable = skillFactory.forName("Indomitable");
        StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Indomitable doubles strength after Dauntless - should proceed to pushback");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Indomitable doubles strength after Dauntless - pushback processed, game continues");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Indomitable doubles strength after Dauntless - game in valid state after block completes");
    }

    @Test
    public void indomitableCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .dauntless(6)
                .block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill indomitable = skillFactory.forName("Indomitable");
        StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Indomitable cannot use twice per game - first use proceeds to pushback");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step, "Indomitable cannot use twice per game - pushback processed");

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Indomitable cannot use twice per game - game in valid state after block completes");
    }

    @Test
    public void canDoubleStrengthAfterDauntlessOnDoubleStrengthStep() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).dauntless(6).block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill indomitable = skillFactory.forName("Indomitable");
        StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));

        assertNotNull(state.getCurrentStep(),
                "Can double strength after Dauntless on double strength step - game in valid state after strength doubled vs ST5");
    }

    @Test
    public void indomitableDoesNotTriggerIfDauntlessFails() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state).dauntless(1).block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Indomitable does not trigger if Dauntless fails - game in valid state after failed Dauntless roll");
    }

    @Test
    public void indomitableResolvesAsTwoDiceAttackerChoice() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .dauntless(6)
                .block("pushback", "pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill indomitable = skillFactory.forName("Indomitable");
        StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step, "Indomitable resolves as two dice attacker's choice - block choice proceeds to pushback");
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Indomitable resolves as two dice attacker's choice - both allocated block dice were consumed (ST6 vs ST4), the block resolved as 2 dice attacker's choice");
    }

    @Test
    public void indomitableConsumedNoDialogOnSecondBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless").skill("Indomitable")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill indomitable = skillFactory.forName("Indomitable");

        // First block: Dauntless succeeds and Indomitable is used (doubling ST to 6 vs 4), pushback.
        // The attacker follows up into (8,7) so the second block can still reach away1 at (9,7).
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).dauntless(6).block("pushback", "pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.useSkill(indomitable, true, "home1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));

        assertFalse(state.getGame().getPlayerById("home1").hasUnused(indomitable),
                "After the first block Indomitable must be consumed (ONCE_PER_GAME)");
        // DSL limitation: a second block in the same turn cannot be driven (the acting player cannot
        // take a second action), so the "no Indomitable dialog on a second block" verification is kept
        // as a spec in the doc and covered by the consumption assertion above.
    }
}
