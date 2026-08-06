package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OnTheBallPassBlockIntegrationTest extends AbstractStateTest {

    @Test
    public void onTheBallTriggersPassBlockWhenOpponentDeclaresPass() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withWeather(Weather.NICE)
                .withBallAt(11, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)                .skill("On The Ball")))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        IStep step = StepEngine.start(state);
        assertNotNull(step, "Expected a step after start");

        step = StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        assertNotNull(step, "Expected a step after selectPlayer");

        step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));
        assertNotNull(step, "Expected a step after pass command");

        assertEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
                "Expected PASS_BLOCK turn mode after pass with On The Ball defender");
        assertFalse(game.isHomePlaying(),
                "Expected away team to be playing during PASS_BLOCK");
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isActive(),
                "Expected away player with On The Ball to be active");
    }

    @Test
    public void onTheBallDoesNotTriggerWhenDefenderLacksSkill() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withWeather(Weather.NICE)
                .withBallAt(11, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(6);
        IStep step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(step, "Expected a step after pass command");
        assertNotEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
                "Expected turn mode to NOT be PASS_BLOCK when defender lacks the skill");
    }

    @Test
    public void onTheBallDoesNotTriggerWhenDefenderHasNoTacklezones() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withWeather(Weather.NICE)
                .withBallAt(11, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(11, 7).stats(6, 3, 3, 2, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8)
                                .skill("On The Ball")
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).scatterDirection(6);
        IStep step = StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(step, "Expected a step after pass command");
        assertNotEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
                "Expected turn mode to NOT be PASS_BLOCK when defender has no tacklezones");
    }

    @Disabled("Requires removing DUMP_OFF guard in StepPassBlock.executeStep()")
    @Test
    public void blockOnThrowerWithDumpOffTriggersPassBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withWeather(Weather.NICE)
                .withBallAt(11, 7)
                .withTeam(true, t -> t
                        .player("home_blitzer", p -> p.at(10, 7).stats(6, 3, 3, 5, 8))
                        .player("home_otb", p -> p.at(16, 7).stats(6, 3, 3, 5, 8).skill("On The Ball")))
                .withTeam(false, t -> t
                        .player("away_thrower", p -> p.at(11, 7).stats(6, 3, 3, 2, 8).skill("Dump-Off")))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        StepEngine.start(state);

        StepEngine.respond(state, Commands.selectPlayer("home_blitzer", PlayerAction.BLOCK));
        StepEngine.respond(state, Commands.block("home_blitzer", "away_thrower"));

        Skill dumpOff = game.getPlayerById("away_thrower").getSkills()[0];
        StepEngine.respond(state, new ClientCommandUseSkill(dumpOff, true, "away_thrower", null, false));

        TestRolls.on(state).skill(6).scatterDirection(1).block("pushback");
        IStep step = StepEngine.respond(state, Commands.pass("away_thrower", new FieldCoordinate(14, 7)));
        assertNotNull(step, "Expected a step after pass command");

        StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(TurnMode.PASS_BLOCK, game.getTurnMode(),
                "PASS_BLOCK should be triggered during dump-off pass sequence");
    }

    @Test
    public void kickoffReturnMoveAfterScatter() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .initialState()
                .withOption(new GameOptionInt(GameOptionId.MIN_PLAYERS_ON_LOS).setValue(0))
                .withTeam(true, t -> t.player("home1", p -> p.at(11, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8).skill("On The Ball")))
                .build();
        this.gameState = state;

        Game game = state.getGame();
        assertEquals(StepId.KICKOFF, Kickoff.throughSetup(state).getId());

        TestRolls.on(state).scatterDirection(3).scatterDistance(3);
        IStep step = Kickoff.kick(state, new FieldCoordinate(10, 7));

        assertEquals(TurnMode.KICKOFF_RETURN, game.getTurnMode(),
                "On The Ball player on the receiving team triggers the kickoff return phase after the scatter");
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isActive(),
                "On The Ball player is active and can move during the kickoff return");
        assertNotNull(step);

        step = StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.MOVE));
        assertNotNull(step, "Expected a step after selecting the On The Ball player to move");
        StepEngine.respond(state, Commands.move("away1", new FieldCoordinate(16, 7), new FieldCoordinate(15, 7)));

        assertEquals(new FieldCoordinate(15, 7), game.getFieldModel().getPlayerCoordinate(game.getPlayerById("away1")),
                "On The Ball player moves up to three squares during the kickoff return");
    }
}
