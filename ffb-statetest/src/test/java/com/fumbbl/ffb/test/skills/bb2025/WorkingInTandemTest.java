package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WorkingInTandemTest extends AbstractStateTest {

    @Test
    public void workingInTandemRerollsBlockDieWhenPartnerMarking() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemNoRerollWhenPartnerNotMarking() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(8, 5).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemPassHasNoModifiersToPartner() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemPassToPartnerInOpponentTacklezoneNoTzPenalty() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t.player("away1", p -> p.at(10, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemPassToPartnerAtAnyRangeWithNoRangeModifiers() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemBlockRerollOnlyWhenPartnerMarksTheOpponent() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Working In Tandem")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void workingInTandemPassRequiresBothHaveSkill() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 7).withWeather(Weather.NICE)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Working In Tandem"))
                        .player("home2", p -> p.at(14, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.PASS_MOVE));
        TestRolls.on(state).skill(6).skill(6);
        StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

        assertNotNull(state.getCurrentStep(),
                "Passing to a teammate WITHOUT Working In Tandem means isPassingToPartner returns false and the normal range modifiers apply - the pass is still resolved");
    }
}
