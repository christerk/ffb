package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DwarfenGritTest extends AbstractStateTest {

    @Test
    public void dwarfenGritActivatesOncePerGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dwarfen Grit")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(3, 3);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Dwarfen Grit activates once per game - game in valid state after use");
    }

    @Test
    public void dwarfenGritConsumedForGame() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dwarfen Grit")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(3, 3);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Dwarfen Grit consumed for game - game in valid state after use");
    }

    @Test
    public void dwarfenGritRerollsArmorSuccessfully() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dwarfen Grit")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(3, 3);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Dwarfen Grit rerolls armor successfully - game in valid state after armor reroll");
    }

    @Test
    public void dwarfenGritRerollsInjuryAtApothecaryMultiple() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dwarfen Grit")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pow")
                .armour(4, 4)
                .injury(2, 2);

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep(),
                "Dwarfen Grit rerolls injury at apothecary multiple - game in valid state");
    }
}
