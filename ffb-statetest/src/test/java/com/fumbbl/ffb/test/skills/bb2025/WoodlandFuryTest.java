package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WoodlandFuryTest extends AbstractStateTest {

    private Skill woodlandFury(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Woodland Fury");
    }

    @Test
    public void woodlandFuryRerollsBlockDieWhenWouldBeKnockedDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 2, 3, 5, 8).skill("Woodland Fury")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("playerdown")
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.WOODLAND_FURY));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Woodland Fury reroll converts the playerdown die into a pushback");
    }

    @Test
    public void woodlandFuryCannotUseTwicePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 2, 3, 5, 8).skill("Woodland Fury")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("playerdown").block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.WOODLAND_FURY));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
        assertTrue(state.getGame().getPlayerById("home1").isUsed(woodlandFury(state)),
                "Woodland Fury is consumed for the game after the single once-per-game use");
    }

    @Test
    public void woodlandFuryRerollConvertsAttackerDownToPowPushback() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 2, 3, 5, 8).skill("Woodland Fury")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("playerdown").block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.WOODLAND_FURY));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId(),
                "The Woodland Fury reroll converts the Attacker Down die into a pushback");
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "The attacker is not knocked down - the Woodland Fury reroll replaced Attacker Down with pushback");
    }

    @Test
    public void woodlandFuryTriggersOnlyOnAttackerDownResult() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 2, 3, 5, 8).skill("Woodland Fury")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.singleBlockDieReRoll(0, ReRollSources.WOODLAND_FURY));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertNotNull(state.getCurrentStep());
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "The attacker stays standing after the Both Down die is rerolled to pushback");
    }

    @Test
    public void woodlandFuryDoesNotTriggerWhenBlockResultWouldNotKnockDown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 2, 3, 5, 8).skill("Woodland Fury")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
        assertFalse(state.getGame().getPlayerById("home1").isUsed(woodlandFury(state)),
                "Woodland Fury does not trigger on a pushback result - the skill is not consumed");
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "The attacker is not knocked down by a pushback result");
    }
}
