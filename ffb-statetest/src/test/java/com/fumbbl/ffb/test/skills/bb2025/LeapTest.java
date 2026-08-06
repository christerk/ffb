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

public class LeapTest extends AbstractStateTest {

    @Test
    public void leapAllowsJumpingOverPlayers() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
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
                "Leap allows jumping over players - expected player to leap over opponent to (9,7), was at " + playerPos);
    }

    @Test
    public void leapFailsOnRollOfOne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
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
                "Leap fails on roll of 1 - game in valid state after failed leap attempt");
    }

    @Test
    public void leapWithVeryLongLegs() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Leap").skill("Very Long Legs")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Leap with Very Long Legs - game in valid state after successful leap with improved roll requirement");
    }

    @Test
    public void leapIntoEndzoneScoresTouchdown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(23, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(23, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE, true));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(23, 7), new FieldCoordinate(25, 7)));

        assertNotNull(state.getCurrentStep(),
                "Leap into endzone scores touchdown - game in valid state after player leaps to endzone with ball");
    }
}
