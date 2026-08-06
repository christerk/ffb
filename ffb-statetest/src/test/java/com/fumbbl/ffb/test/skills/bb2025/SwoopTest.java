package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.net.commands.ClientCommandSwoop;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwoopTest extends AbstractStateTest {

    private GameState ttmState(String extraThrowerSkills) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("thrower", p -> {
                    p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate");
                    if (extraThrowerSkills != null) {
                        p.skill(extraThrowerSkills);
                    }
                }).player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff").skill("Swoop")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)));
        return builder.build();
    }

    private GameState ktmState() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("kicker", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Kick Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff").skill("Swoop")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    private Skill swoopSkill(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Swoop");
    }

    private void assertSwoopDialog(GameState state) {
        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Swoop is offered to the thrown player via a skill use dialog");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Swoop skill use dialog");
        assertEquals("Swoop", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Dialog should offer Swoop");
    }

    @Test
    void swoopControlsTtmScatterDirection() {
        GameState state = ttmState(null);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertSwoopDialog(state);
        assertEquals(new FieldCoordinate(7, 7), position(state, "thrower"),
                "Thrower stays at (7,7) after initiating the TTM action");

        Player<?> flinger = state.getGame().getPlayerById("flinger");
        assertTrue(flinger.hasSkillProperty(NamedProperties.ttmScattersInSingleDirection),
                "Swoop registers the ttmScattersInSingleDirection property on the thrown player");

        TestRolls.on(state).throwInDirection(4).general("swoopDistance", 3).skill(6);
        StepEngine.respond(state, Commands.useSkill(swoopSkill(state), true, "flinger"));
        StepEngine.respond(state, new ClientCommandSwoop("flinger", new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(12, 7), position(state, "flinger"),
                "Swoop controls the TTM scatter: from the pass coordinate (9,7) the throw-in template points EAST"
                        + " (roll 4) and the distance roll (3) lands the thrown player at (12,7) instead of scattering randomly");
        assertTrue(state.getGame().getFieldModel().getPlayerState(flinger).isStanding(),
                "Thrown player lands standing after the controlled Swoop scatter");
    }

    @Test
    void swoopChoosesSingleScatterDirectionOnTtmLanding() {
        GameState state = ttmState(null);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertSwoopDialog(state);

        TestRolls.on(state).throwInDirection(4).general("swoopDistance", 3).skill(6);
        StepEngine.respond(state, Commands.useSkill(swoopSkill(state), true, "flinger"));
        StepEngine.respond(state, new ClientCommandSwoop("flinger", new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(12, 7), position(state, "flinger"),
                "Swoop reduces the normal 3 random scatters to a single controlled direction: the thrown player travels"
                        + " EAST by 3 from (9,7) to (12,7); without Swoop the three scatter rolls would land elsewhere");
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("flinger")).isStanding(),
                "Single-direction Swoop scatter is followed by a successful landing roll");
    }

    @Test
    void swoopPlusBullseyeOnSuperbThrow() {
        GameState state = ttmState("Bullseye");
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(10, 7)));

        IDialogParameter dialog = state.getGame().getDialogParameter();
        assertNotNull(dialog, "Bullseye is offered on the superb throw even with a Swoop flinger");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Expected Bullseye skill use dialog");

        Skill bullseye = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Bullseye");
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.useSkill(bullseye, true, "thrower"));

        assertEquals(new FieldCoordinate(10, 7), position(state, "flinger"),
                "With Bullseye accepted the superb throw skips the scatter entirely (no SWOOP step runs) and the thrown"
                        + " player lands exactly on the target (10,7)");
    }

    @Test
    void swoopOnKickTeamMateScatterInChosenDirection() {
        GameState state = ktmState();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("kicker", PlayerAction.KICK_TEAM_MATE_MOVE));
        StepEngine.respond(state, Commands.throwTeammate("kicker", "flinger"));
        TestRolls.on(state).skill(4);
        StepEngine.respond(state, new ClientCommandThrowTeamMate("kicker", new FieldCoordinate(9, 7)));

        assertSwoopDialog(state);

        TestRolls.on(state).throwInDirection(4).general("swoopDistance", 3).skill(6);
        StepEngine.respond(state, Commands.useSkill(swoopSkill(state), true, "flinger"));
        StepEngine.respond(state, new ClientCommandSwoop("flinger", new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(12, 7), position(state, "flinger"),
                "Swoop controls the Kick Team-Mate scatter identically to Throw Team-Mate: EAST by 3 from (9,7) to (12,7)");
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("flinger")).isStanding(),
                "Kicked player lands standing after the Swoop-controlled scatter");
    }

    @Test
    void swoopOncePerTurnByTeammate() {
        GameState state = ttmState(null);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertSwoopDialog(state);

        // DSL limitation: only one TTM action can be driven per turn (the TTM used flag and the acting player
        // constraint prevent a second Throw Team-Mate action in the same turn), so the once-per-team enforcement
        // is verified through the skill's usage type and its consumption when the landing reroll is used below.
        assertEquals(SkillUsageType.ONCE_PER_TURN_BY_TEAM_MATE, swoopSkill(state).getSkillUsageType(),
                "Swoop is only usable once per turn by the team (ONCE_PER_TURN_BY_TEAM_MATE)");

        TestRolls.on(state).throwInDirection(4).general("swoopDistance", 3).skill(1).skill(6);
        StepEngine.respond(state, Commands.useSkill(swoopSkill(state), true, "flinger"));
        StepEngine.respond(state, new ClientCommandSwoop("flinger", new FieldCoordinate(10, 7)));

        Player<?> flinger = state.getGame().getPlayerById("flinger");
        assertTrue(flinger.isUsed(swoopSkill(state)),
                "The Swoop landing reroll consumes the skill for the team, enforcing ONCE_PER_TURN_BY_TEAM_MATE");
        assertEquals(new FieldCoordinate(12, 7), position(state, "flinger"),
                "The landing reroll (6) succeeds and the thrown player lands safely at (12,7)");
        assertTrue(state.getGame().getFieldModel().getPlayerState(flinger).isStanding(),
                "Thrown player is standing after the Swoop rerolled landing");
    }

    @Test
    void swoopRerollsLandingRollViaRightStuff() {
        GameState state = ttmState(null);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));

        assertSwoopDialog(state);

        // Landing roll 1 fails and Swoop automatically rerolls the Right Stuff landing roll via
        // ReRollSources.SWOOP for ReRolledActions.RIGHT_STUFF; reroll 4 >= AG3 passes.
        TestRolls.on(state).throwInDirection(4).general("swoopDistance", 3).skill(1).skill(4);
        StepEngine.respond(state, Commands.useSkill(swoopSkill(state), true, "flinger"));
        StepEngine.respond(state, new ClientCommandSwoop("flinger", new FieldCoordinate(10, 7)));

        Player<?> flinger = state.getGame().getPlayerById("flinger");
        assertEquals(new FieldCoordinate(12, 7), position(state, "flinger"),
                "After the Swoop-controlled scatter the thrown player lands at (12,7) once the landing reroll succeeds");
        assertTrue(state.getGame().getFieldModel().getPlayerState(flinger).isStanding(),
                "Swoop rerolls the failed landing roll (1 -> 4) and the thrown player lands safely standing");
        assertTrue(flinger.isUsed(swoopSkill(state)),
                "The Swoop reroll for RIGHT_STUFF is a separate reroll capability from the direction control");
    }
}
