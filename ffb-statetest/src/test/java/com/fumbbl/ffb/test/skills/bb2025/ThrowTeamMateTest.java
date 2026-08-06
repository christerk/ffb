package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ThrowTeamMateTest extends AbstractStateTest {

    private GameState build(String extraThrowerSkills) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("thrower", p -> {
                    p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate");
                    if (extraThrowerSkills != null) {
                        p.skill(extraThrowerSkills);
                    }
                }).player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)));
        return builder.build();
    }

    private void selectThrower(GameState state) {
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
    }

    private void throwTo(GameState state, FieldCoordinate target) {
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", target));
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    @Test
    void throwTeamMateActionWorks() {
        GameState state = build(null);
        this.gameState = state;
        selectThrower(state);
        TestRolls.on(state).skill(6).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        throwTo(state, new FieldCoordinate(9, 7));
        assertEquals(new FieldCoordinate(11, 5), position(state, "flinger"),
                "Accurate TTM pass to (9,7) then three scatter rolls (N, NE, E) land the thrown player at (11,5)");
    }

    @Test
    void throwTeamMateFumbleCase() {
        GameState state = build(null);
        this.gameState = state;
        selectThrower(state);
        TestRolls.on(state).skill(1).scatterDirection(1).skill(1).armour(1, 1);
        throwTo(state, new FieldCoordinate(9, 7));
        assertNotNull(state.getCurrentStep(),
                "TTM fumble (pass roll 1) is processed without throwing the player; the game remains in a valid state");
    }

    @Test
    void ttmInaccuratePassScatters3SquaresFromTarget() {
        GameState state = build(null);
        this.gameState = state;
        selectThrower(state);
        TestRolls.on(state).skill(2).scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        throwTo(state, new FieldCoordinate(9, 7));
        assertEquals(new FieldCoordinate(11, 5), position(state, "flinger"),
                "An inaccurate TTM pass (roll 2 < PA5) still scatters the thrown player three squares from the target (9,7): N, NE, E to land at (11,5)");
    }

    @Test
    void ttmSuperbThrowAccurateLandingNoScatter() {
        GameState state = build("Bullseye");
        this.gameState = state;
        selectThrower(state);
        TestRolls.on(state).skill(6).skill(6);
        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill bullseye = skillFactory.forName("Bullseye");
        throwTo(state, new FieldCoordinate(9, 7));
        StepEngine.respond(state, Commands.useSkill(bullseye, true, "thrower"));
        assertEquals(new FieldCoordinate(9, 7), position(state, "flinger"),
                "A superb throw (roll 6) with Bullseye skips the scatter entirely and the thrown player lands exactly on the target (9,7)");
    }

    @Test
    void ttmThrownPlayerLandingOnOpponentLethalFlightInjuryRoll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"))
                        .player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(11, 5).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;
        selectThrower(state);
        TestRolls.on(state).skill(2).scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .armour(6, 6).injury(2, 2).scatterDirection(6).armour(6, 6).injury(2, 2);
        throwTo(state, new FieldCoordinate(9, 7));

        assertEquals(new FieldCoordinate(10, 6), position(state, "flinger"),
                "After hitting the opponent at (11,5) the thrown player re-scatters SOUTHWEST (6) one square to (10,6)");
        assertEquals(new FieldCoordinate(11, 5),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away1")),
                "The opponent at (11,5) was hit by the landing thrown player and takes the Lethal Flight armour/injury roll in place");
    }
}
