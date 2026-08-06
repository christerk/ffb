package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PogoTest extends AbstractStateTest {

    @Test
    public void pogoAllowsLeaping() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(6);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(9, 7), playerPos,
                "Pogo allows leaping - expected Pogo player to leap over opponent to (9,7), was at " + playerPos);
    }

    @Test
    public void pogoCancelsPrehensileTail() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Prehensile Tail"))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(6);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pogo cancels Prehensile Tail - game in valid state after Pogo leap ignores Prehensile Tail penalty");
    }

    @Test
    public void canLeapIntoEmptySquareTwoSquaresAway() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(6);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(9, 7), playerPos,
                "Can leap into empty square two squares away - Pogo player at (9,7) after leap over empty square");
    }

    @Test
    public void ignoreTacklezonesWhenJumpingLeapFromTZWithNoPenalty() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(6);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Ignore tackle zones when jumping - Pogo leap from TZ has no dodge penalty - game in valid state");
    }

    @Test
    public void cancelDivingTackleOnPogoLeap() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Diving Tackle"))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(6);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Cancel Diving Tackle on Pogo leap - game in valid state (Pogo negates Diving Tackle during leap)");
    }

    @Test
    public void pogoLeapFailsOnNaturalOneTurnover() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Pogo")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));

        TestRolls.on(state)
                .skill(1)
                .armour(1, 1);

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pogo leap fails on natural 1 - game in valid state after failed Pogo leap (natural 1 is always a failure)");
    }
}
