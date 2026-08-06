package com.fumbbl.ffb.test.skills.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Bb2020SkillsTest extends AbstractStateTest {

    @Test
    void blockWorksInBb2020() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Block")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(state).block("bothdown").armour(2, 2);
        StepEngine.respond(state, Commands.block("h1", "a1"));
        StepEngine.respond(state, Commands.blockChoice(0));

        assertTrue(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("h1")).isStanding());
        assertFalse(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("a1")).isStanding());
    }

    @Test
    void fendWorksInBb2020() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Fend")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("h1", "a1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.useSkill(
                (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Fend"), true, "a1"));

        assertEquals(new FieldCoordinate(7, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("h1")));
    }

    @Test
    void dauntlessWorksInBb2020() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2020")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(state).dauntless(6).block("pushback");
        StepEngine.respond(state, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(StepId.PUSHBACK, step.getId());
    }
}
