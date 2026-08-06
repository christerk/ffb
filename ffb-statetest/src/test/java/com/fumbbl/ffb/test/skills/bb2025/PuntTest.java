package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PuntTest extends AbstractStateTest {

    private void selectPunt(GameState state) {
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PUNT_MOVE));
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PUNT));
    }

    @Test
    void puntActionWorks() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Punt")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        selectPunt(state);
        TestRolls.on(state)
                .throwInDirection(3)
                .general("punt distance", 3)
                .scatterDirection(1);
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(9, 7)));

        assertTrue(state.getGame().getFieldModel().isBallInPlay(),
                "Punt action works - ball is in play after the punt sequence completes");
    }

    @Test
    void canPuntScatterDirectionAndDistanceAfterInitPunt() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Punt")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        selectPunt(state);
        TestRolls.on(state)
                .throwInDirection(3)
                .general("punt distance", 3)
                .scatterDirection(1);
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(9, 7)));

        assertEquals(new FieldCoordinate(10, 6),
                state.getGame().getFieldModel().getBallCoordinate(),
                "Can punt - direction EAST (roll 3) from target (9,7) with distance 3 lands at (10,7), then bounces NORTH (scatter 1) to (10,6)");
    }

    @Test
    void canPuntLandsOutOfBoundsTriggersThrowIn() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 2).withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 2).stats(6, 3, 3, 5, 8).skill("Punt")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        selectPunt(state);
        TestRolls.on(state)
                .throwInDirection(1)
                .general("punt distance", 6)
                .throwInDirection(3)
                .throwInDistance(2, 4)
                .scatterDirection(1);
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(9, 2)));

        assertTrue(state.getGame().getFieldModel().isBallInPlay(),
                "Can punt - ball lands out of bounds and is thrown back in, ball stays in play");
    }

    @Test
    void canPuntBallRecoveredByOpposingTeam() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Punt")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8))
                        .player("away2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        selectPunt(state);
        TestRolls.on(state)
                .throwInDirection(3)
                .general("punt distance", 3)
                .skill(6);
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(9, 7)));

        assertEquals(new FieldCoordinate(10, 7),
                state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById("away2")),
                "Can punt - ball lands at (10,7) where away2 catches it (punt lands on the opposing team's player)");
    }

    @Test
    void canPuntPlusKickRerollsPuntDistance() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Punt").skill("Kick")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        selectPunt(state);
        TestRolls.on(state)
                .throwInDirection(3)
                .general("punt distance", 1)
                .general("punt distance reroll", 6)
                .scatterDirection(1);
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(9, 7)));
        Skill kick = (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("Kick");
        StepEngine.respond(state, Commands.useSkill(kick, false, "home1"));
        StepEngine.respond(state, Commands.useSkill(kick, true, "home1"));

        assertEquals(new FieldCoordinate(13, 6),
                state.getGame().getFieldModel().getBallCoordinate(),
                "Can punt - Kick skill rerolls the punt distance die: first distance roll 1 is discarded, reroll 6 kicks the ball EAST 6 squares to (13,7), then bounces NORTH (scatter 1) to (13,6)");
    }
}
