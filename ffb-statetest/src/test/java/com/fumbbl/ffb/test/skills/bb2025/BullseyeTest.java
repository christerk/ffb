package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BullseyeTest extends AbstractStateTest {

    @Test
    void bullseyeControlsTtmScatter() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7)
                                .stats(6, 5, 3, 5, 8)
                                .skill("Throw Team-Mate")
                                .skill("Bullseye"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, Commands.throwTeammate("thrower", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getDialogParameter(),
                "Bullseye is offered on the superb (accurate) throw");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill bullseye = skillFactory.forName("Bullseye");

        TestRolls.on(state).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.useSkill(bullseye, false, "thrower", true));

        assertEquals(new FieldCoordinate(12, 5),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "Declining Bullseye (never use) leaves the normal 3-square scatter intact: directions"
                        + " NORTH, NORTHEAST, EAST (1,2,3) from the target (10,7) land the thrown player"
                        + " at (12,5), so Bullseye's scatter skip is not automatic");
    }

    @Test
    void bullseyeWithSwoopScatterInteraction() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8)
                                .skill("Throw Team-Mate").skill("Bullseye").skill("Swoop"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, Commands.throwTeammate("thrower", new FieldCoordinate(10, 7)));

        assertNotNull(state.getGame().getDialogParameter(),
                "Bullseye dialog is offered on the superb throw even with Swoop present on the thrower");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill bullseye = skillFactory.forName("Bullseye");

        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.useSkill(bullseye, true, "thrower"));

        assertEquals(new FieldCoordinate(10, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "Accepting Bullseye with Swoop present skips the scatter entirely: the thrown player"
                        + " lands exactly in the target square (10,7)");
    }

    @Test
    void bullseyeSkipsTtmScatterOnSuperbThrow() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8)
                                .skill("Throw Team-Mate").skill("Bullseye"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, Commands.throwTeammate("thrower", new FieldCoordinate(10, 7)));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Expected Bullseye skill use dialog on superb throw");

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill bullseye = skillFactory.forName("Bullseye");

        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.useSkill(bullseye, true, "thrower"));

        assertEquals(new FieldCoordinate(10, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "Bullseye on a superb throw skips the TTM scatter entirely: the thrown player lands"
                        + " exactly in the target square (10,7)");
    }

    @Test
    void bullseyeDoesNotSkipScatterOnAccurateThrow() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8)
                                .skill("Throw Team-Mate").skill("Bullseye"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(4).scatterDirection(2).scatterDirection(2).scatterDirection(2).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, Commands.throwTeammate("thrower", new FieldCoordinate(10, 7)));

        assertNotEquals(new FieldCoordinate(10, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "On a non-ACCURATE TTM throw Bullseye is not offered (no dialog, no use): the thrown player"
                        + " scatters normally instead of landing in the target square");
        assertEquals(new FieldCoordinate(13, 4),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("flinger")),
                "TTM roll 4 is inaccurate, so the player scatters NORTHEAST, NORTHEAST, NORTHEAST (2,2,2)"
                        + " from the target (10,7) to (13,4) and then lands there");
    }
}
