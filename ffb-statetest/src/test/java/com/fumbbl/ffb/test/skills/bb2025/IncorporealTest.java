package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncorporealTest extends AbstractStateTest {

    private Skill incorporealSkill(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Incorporeal");
    }

    private void activateIncorporeal(GameState state) {
        // DSL limitation: the doc assumes the ignoreTacklezonesWhenMoving enhancement is applied automatically on
        // activation, but the engine applies it only when the coach explicitly activates the skill via CLIENT_USE_SKILL.
        StepEngine.respond(state, Commands.useSkill(incorporealSkill(state), true, "home1"));
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    private boolean standing(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById(playerId)).isStanding();
    }

    @Test
    public void incorporealAvoidsDodgingThroughTackleZones() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Incorporeal")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        activateIncorporeal(state);

        // No dodge dice are queued: leaving away1's tackle zone must consume no dodge roll because
        // ignoreTacklezonesWhenMoving suppresses the dodge checks for the whole activation.
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(7, 5)));

        assertEquals(new FieldCoordinate(7, 5), position(state, "home1"),
                "Incorporeal avoids dodging through tackle zones - home1 reaches (7,5) without a dodge roll");
        assertTrue(standing(state, "home1"),
                "Incorporeal avoids dodging through tackle zones - home1 is standing after moving through the tackle zone");
    }

    @Test
    public void incorporealCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Incorporeal")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        activateIncorporeal(state);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(7, 5)));

        assertFalse(state.getGame().getPlayerById("home1").hasUnused(incorporealSkill(state)),
                "Incorporeal cannot be used twice per game - the ONCE_PER_GAME skill is consumed on the first activation");

        // DSL limitation: the harness cannot construct a turn transition, so the first activation is ended
        // explicitly and the acting player is manually re-activated to begin the second activation.
        StepEngine.respond(state, Commands.selectPlayer(null, null));
        state.getGame().getFieldModel().setPlayerState(state.getGame().getPlayerById("home1"),
                new PlayerState(PlayerState.STANDING).changeActive(true));

        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        // The enhancement is no longer applied: leaving (7,6) (still in away1's tackle zone) now requires a dodge roll.
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 5), new FieldCoordinate(7, 6)));
        TestRolls.on(state).skill(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 6), new FieldCoordinate(8, 5)));

        assertEquals(new FieldCoordinate(8, 5), position(state, "home1"),
                "Incorporeal cannot be used twice per game - the second activation requires a dodge roll (D6 4 >= AG3) and home1 reaches (8,5)");
        assertTrue(standing(state, "home1"),
                "Incorporeal cannot be used twice per game - home1 is standing after the successful dodge");
    }

    @Test
    public void canAvoidDodgingOnInitSelecting() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Incorporeal")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(7, 6).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        activateIncorporeal(state);

        // Both (7,7) and (7,8) are covered by opponent tackle zones, yet no dodge dice are queued.
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 8)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 8), new FieldCoordinate(7, 9)));

        assertEquals(new FieldCoordinate(7, 9), position(state, "home1"),
                "Can avoid dodging on init selecting - home1 reaches (7,9) without a dodge roll despite two opponent tackle zones");
        assertTrue(standing(state, "home1"),
                "Can avoid dodging on init selecting - home1 is standing after moving through the two-tackle-zone cluster");
    }

    @Test
    public void canAvoidDodgingOnSelectBlitzTarget() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Incorporeal")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(12, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        // DSL limitation: on a blitz the enhancement is only applied when the skill is activated while the blitz
        // target is selected (StepSelectBlitzTarget stores the used skill and applies it on target confirmation).
        StepEngine.respond(state, Commands.useSkill(incorporealSkill(state), true, "home1"));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        // No dodge dice are queued during the blitz move.
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(11, 7)));

        assertEquals(new FieldCoordinate(11, 7), position(state, "home1"),
                "Can avoid dodging on select blitz target - home1 completes the blitz move to (11,7) without a dodge roll");
        assertTrue(standing(state, "home1"),
                "Can avoid dodging on select blitz target - home1 is standing after the blitz move");
    }

    @Test
    public void incorporealThroughMultipleTackleZones() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Incorporeal")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(7, 6).stats(6, 3, 3, 5, 8))
                        .player("away3", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        activateIncorporeal(state);

        // No dodge dice are queued despite three adjacent opponents covering the path.
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 9)));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 9), new FieldCoordinate(7, 10)));

        assertEquals(new FieldCoordinate(7, 10), position(state, "home1"),
                "Incorporeal through multiple tackle zones - home1 reaches (7,10) without a dodge roll despite three opponent tackle zones");
        assertTrue(standing(state, "home1"),
                "Incorporeal through multiple tackle zones - home1 is standing after moving through the three-tackle-zone cluster");
    }
}
