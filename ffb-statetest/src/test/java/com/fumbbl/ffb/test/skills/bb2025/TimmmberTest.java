package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimmmberTest extends AbstractStateTest {

    private GameState buildWithAssists(int adjacentTeammates, boolean teammateHasTimmmber) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> {
                    t.player("home1", p -> p.at(7, 7).stats(2, 3, 4, 5, 8).skill("Timmm-ber!")
                            .state(new PlayerState(PlayerState.PRONE).changeActive(true)));
                    if (adjacentTeammates >= 1) {
                        t.player("home2", p -> {
                            p.at(7, 8).stats(6, 3, 3, 5, 8);
                            if (teammateHasTimmmber) {
                                p.skill("Timmm-ber!");
                            }
                        });
                    }
                    if (adjacentTeammates >= 2) {
                        t.player("home3", p -> p.at(7, 6).stats(6, 3, 3, 5, 8));
                    }
                })
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)));
        return builder.build();
    }

    private void standUp(GameState state, int standUpRoll) {
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(standUpRoll).goingForIt(2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
    }

    @Test
    void allowsStandUpAssists() {
        GameState state = buildWithAssists(0, false);
        this.gameState = state;
        standUp(state, 4);
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "A prone MA2 player with Timmm-ber should stand up on a 4+ roll (borderline: 4 passes, 3 fails without assists)");
    }

    @Test
    void multipleTimmmberStack() {
        GameState state = buildWithAssists(1, true);
        this.gameState = state;
        standUp(state, 3);
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "A prone MA2 player with Timmm-ber should stand up on a 3 with one adjacent Timmm-ber teammate assisting (+1)");
    }

    @Test
    void timmmberStandUpWith1AdjacentTeammate() {
        GameState state = buildWithAssists(1, false);
        this.gameState = state;
        standUp(state, 3);
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "A prone MA2 player with Timmm-ber should stand up on a 3 with one adjacent standing teammate assisting (+1); teammates do not need Timmm-ber themselves");
    }

    @Test
    void timmmberStandUpWith2PlusAdjacentTeammates() {
        GameState state = buildWithAssists(2, false);
        this.gameState = state;
        standUp(state, 2);
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "A prone MA2 player with Timmm-ber should stand up on a 2 with two adjacent standing teammates assisting (+2)");
    }

    @Test
    void timmmberStandUpWithNoTeammatesAdjacent() {
        GameState state = buildWithAssists(0, false);
        this.gameState = state;
        standUp(state, 4);
        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).isStanding(),
                "A prone MA2 player with Timmm-ber and no adjacent teammates stands up on the base 4+ (borderline: 4 passes, 3 fails)");
    }

    @Test
    void timmmberNaturalOneAlwaysFailsDespiteAssists() {
        GameState state = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> {
                    t.player("home1", p -> p.at(7, 7).stats(2, 3, 4, 5, 8).skill("Timmm-ber!")
                            .state(new PlayerState(PlayerState.PRONE).changeActive(true)));
                    t.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8));
                    t.player("home3", p -> p.at(7, 6).stats(6, 3, 3, 5, 8));
                    t.player("home4", p -> p.at(6, 7).stats(6, 3, 3, 5, 8));
                })
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(1);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertEquals(PlayerState.PRONE,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "A natural 1 always fails no matter how many players are helping (three assists) - home1 remains prone");
    }

    @Test
    void timmmberAssistExcludedWhenTeammateInEnemyTacklezone() {
        GameState state = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> {
                    t.player("home1", p -> p.at(7, 7).stats(2, 3, 4, 5, 8).skill("Timmm-ber!")
                            .state(new PlayerState(PlayerState.PRONE).changeActive(true)));
                    t.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8));
                    t.player("home3", p -> p.at(7, 6).stats(6, 3, 3, 5, 8));
                })
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 9).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertEquals(PlayerState.PRONE,
                state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("home1")).getBase(),
                "The teammate inside the enemy tackle zone does not add an assist (2+1=3 < 4) - home1 remains prone");
    }
}
