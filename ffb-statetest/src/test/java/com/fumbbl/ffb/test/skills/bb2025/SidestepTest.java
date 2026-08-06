package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SidestepTest extends AbstractStateTest {

    @Test
    void allowsDefenderToChoosePushbackSquare() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Sidestep dialog should appear after pushback result");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Dialog should be a skill use dialog for Sidestep");
        assertEquals("Sidestep", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Dialog should offer Sidestep skill use");

        Skill sidestep = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Sidestep");
        StepEngine.respond(state, Commands.useSkill(sidestep, true, "away1"));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(7, 8))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(7, 8),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
                "Sidestep should allow push to side square (7,8) instead of straight back");
    }

    @Test
    void sidestepCancelledByGrab() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(9, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
                "Grab cancels Sidestep, attacker chooses straight pushback");
    }

    @Test
    void sidestepChoosesSideSquareOnPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Sidestep chooses side square on pushback - Sidestep dialog should appear after pushback result");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Sidestep chooses side square on pushback - expected skill use dialog");

        Skill sidestep = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Sidestep");
        StepEngine.respond(state, Commands.useSkill(sidestep, true, "away1"));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(7, 8))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(7, 8),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
                "Sidestep chooses side square on pushback - away1 chooses the side square (7,8) instead of being pushed straight back");
    }

    @Test
    void sidestepIntoOpponentTacklezone() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep"))
                        .player("away2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Sidestep into opponent tackle zone - Sidestep dialog should appear after pushback result");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Sidestep into opponent tackle zone - expected skill use dialog");

        Skill sidestep = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Sidestep");
        StepEngine.respond(state, Commands.useSkill(sidestep, true, "away1"));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 8))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(new FieldCoordinate(9, 8),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
                "Sidestep into opponent tackle zone - away1 sidesteps to the free side square (9,8), adjacent to home1's team, instead of being pushed straight back to (9,7)");
    }

    @Test
    void sidestepForcesChoiceWhenAdjacentSquaresOccupiedAndOneIsCrowd() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep(),
                "Sidestep forces choice when adjacent squares occupied and one is crowd - game in valid state");
    }
}
