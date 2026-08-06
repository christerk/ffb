package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PutTheBootInTest extends AbstractStateTest {

    @Test
    void canAlwaysAssistFouls() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Put the Boot In")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(4, 3).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Put the Boot In can always assist fouls - away1 is stunned because home2's foul assist pushes the armour roll (4+3+1=8) exactly to AV8");
    }

    @Test
    void defensiveCancelsPutTheBootIn() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Put the Boot In")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true)))
                        .player("away2", p -> p.at(8, 9).stats(6, 3, 3, 5, 8).skill("Defensive")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(4, 3);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Defensive cancels Put the Boot In - away2's tackle zone cancels home2's normal assist and away2's Defensive cancels home2's Put the Boot In, so the armour roll (4+3=7) stays below AV8 and away1 remains prone");
    }

    @Test
    void canAlwaysAssistFoulsWhileInOpponentTackleZone() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Put the Boot In")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true)))
                        .player("away2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(4, 3).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Can always assist fouls while in opponent tackle zone - home2 is in away2's tackle zone but Put the Boot In still provides the assist, so armour (4+3+1=8) breaks");
    }

    @Test
    void canAlwaysAssistFoulsPlusGuardStacking() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("home2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Put the Boot In").skill("Guard")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(4, 3).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Can always assist fouls plus Guard stacking - home2 with Put the Boot In and Guard assists the foul so the armour roll (4+3+1=8) breaks AV8");
    }

    @Test
    void putTheBootInPlusDirtyPlayerFoulModifierCombined() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Dirty Player").skill("Put the Boot In")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.FOUL_MOVE));
        TestRolls.on(state).armour(4, 3).injury(3, 2);
        StepEngine.respond(state, Commands.foul("home1", "away1"));

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Put the Boot In plus Dirty Player foul modifier combined - Dirty Player's +1 modifier pushes the armour roll (4+3+1=8) exactly to AV8, breaking it and stunning away1");
    }
}
