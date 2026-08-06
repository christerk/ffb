package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.AbstractStateTest;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementIncreaseTest extends AbstractStateTest {

    @Test
    void plusMAAllowsExtraMoveWithoutGfi() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(7, 3, 3, 5, 8).skill("+MA")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(14, 7)));

        assertEquals(new FieldCoordinate(14, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "+MA 7 should reach (14,7) from (7,7) without GFI");
    }

    @Test
    void plusMAWithSprintAndSureFeetMaxGfiRange() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("+MA").skill("Sprint").skill("Sure Feet")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(15, 7)));

        assertEquals(new FieldCoordinate(15, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "+MA with Sprint and Sure Feet should allow reaching (15,7)");
    }

    @Test
    void plusMAGfiThresholdBoundary() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(9, 3, 3, 5, 8).skill("+MA").skill("+MA")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(16, 7)));

        assertEquals(new FieldCoordinate(16, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Two +MA should allow reaching (16,7) from (7,7) without GFI");
    }

    @Test
    void twoPlusMAStackingForExtendedNonGfiMovement() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("+MA").skill("+MA")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(15, 7)));

        assertEquals(new FieldCoordinate(15, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "Two +MA should allow reaching (15,7) from (7,7) without GFI");
    }
}
