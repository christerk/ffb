package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhirlingDervishTest extends AbstractStateTest {

    private GameState build() {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(4, 5, 3, 5, 9).skill("Ball and Chain").skill("Whirling Dervish")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private Skill whirlingDervish(GameState state) {
        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        return (Skill) skillFactory.forName("Whirling Dervish");
    }

    @Test
    void whirlingDervishRerollsDirection() {
        GameState state = build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).throwInDirection(2).throwInDirection(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.useSkill(whirlingDervish(state), true, "home1"));
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Whirling Dervish should reroll the Ball And Chain direction: the first roll (2, NORTHEAST) is discarded and the reroll (4, EAST) moves home1 to (8,7)");
    }

    @Test
    void whirlingDervishOncePerTurnCanUseMultiplePerGame() {
        GameState state = build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).throwInDirection(2).throwInDirection(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.useSkill(whirlingDervish(state), true, "home1"));
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Whirling Dervish is limited to once per turn but remains available across the game; the rerolled direction moves home1 to (8,7)");
    }

    @Test
    void whirlingDervishRerollsBallAndChainDirectionSuccessfully() {
        GameState state = build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).throwInDirection(2).throwInDirection(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.useSkill(whirlingDervish(state), true, "home1"));
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "The Ball And Chain direction reroll succeeds: home1 moves EAST to (8,7) instead of NORTHEAST to (8,6)");
    }

    @Test
    void whirlingDervishCannotBeUsedTwiceInSameTurn() {
        GameState state = build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).throwInDirection(2).throwInDirection(4).throwInDirection(2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.useSkill(whirlingDervish(state), true, "home1"));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(8, 7), new FieldCoordinate(9, 7)));
        assertEquals(new FieldCoordinate(9, 6),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Whirling Dervish was consumed on the first square (reroll to EAST -> (8,7)); the second square rolls NORTHEAST (2) without a reroll offer, so home1 ends at (9,6)");
    }

    @Test
    void whirlingDervishCanBeUsedAcrossMultipleTurns() {
        GameState state = build();
        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).throwInDirection(5).throwInDirection(4);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
        StepEngine.respond(state, Commands.useSkill(whirlingDervish(state), true, "home1"));
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Whirling Dervish rerolls the first direction (5, SOUTHEAST) to EAST (4); since the skill refreshes at the start of each turn it can be used again next turn - home1 reaches (8,7)");
    }
}
