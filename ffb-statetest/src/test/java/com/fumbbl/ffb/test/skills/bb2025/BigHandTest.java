package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.AbstractStateTest;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BigHandTest extends AbstractStateTest {

    @Test
    public void bigHandAllowsPickupInTackleZones() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Big Hand"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));

        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(8, 7)));

        Game game = state.getGame();
        FieldCoordinate playerPos = game.getFieldModel().getPlayerCoordinate(game.getPlayerById("home1"));
        assertEquals(new FieldCoordinate(8, 7), playerPos,
                "Expected Big Hand player to pick up ball in TZ, was at " + playerPos);
    }

    @Test
    public void bigHandWorksInRain() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.POURING_RAIN)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Big Hand"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void bigHandPickupIgnoresSwelteringHeat() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.SWELTERING_HEAT)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Big Hand"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep(),
                "BigHand pickup ignores Sweltering Heat via ignoreWeatherWhenPickingUp");
    }

    @Test
    public void bigHandPickupIgnoresBlizzard() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withWeather(Weather.BLIZZARD)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Big Hand"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(state.getCurrentStep(),
                "BigHand pickup ignores Blizzard via ignoreWeatherWhenPickingUp");
    }

    @Test
    public void bigHandPickupSucceedsInTackleZonesWithRealRoll() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Big Hand"))
                        .player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;
        state.getGame().getFieldModel().setBallMoving(true);

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(3);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("home1")),
                "BigHand pickup: home1 moved onto the ball square (8,7)");
        assertEquals(new FieldCoordinate(8, 7),
                state.getGame().getFieldModel().getBallCoordinate(),
                "BigHand pickup: ball was picked up - BigHand suppresses the tackle-zone penalty, so the plain AG 3+"
                        + " pickup succeeded on roll 3 and home1 holds the ball");
    }
}
