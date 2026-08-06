package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlwaysHungryTest extends AbstractStateTest {

    @Test
    void alwaysHungryDoesNotTriggerOnBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Always Hungry")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        IStep step = StepEngine.respond(state, Commands.block("home1", "away1"));

        assertEquals(StepId.BLOCK_ROLL, step.getId(),
                "ALWAYS_HUNGRY is only wired into the ThrowTeamMate sequence; a Block action must not consume a skill roll and should stop at BLOCK_ROLL awaiting the block choice, exactly as a pushback block would without Always Hungry");
    }

    @Test
    void alwaysHungryFailureEatsTeammate() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Always Hungry"))
                        .player("home2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(1).skill(1);
        StepEngine.respond(state, Commands.throwTeammate("home1", "home2"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("home1", new FieldCoordinate(9, 7)));

        assertEquals(PlayerState.RIP, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("home2")).getBase(),
                "home2 should be eaten (RIP) when Always Hungry fails (roll 1) and the escape roll also fails (roll 1)");
    }

    @Test
    void alwaysHungryWithProReroll() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Always Hungry").skill("Pro").skill("Throw Team-Mate"))
                        .player("home2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(1).skill(6).skill(2).skill(6)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("home1", "home2"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("home1", new FieldCoordinate(9, 7)));
        StepEngine.respond(state, new ClientCommandUseReRoll(ReRolledActions.ALWAYS_HUNGRY, ReRollSources.PRO));

        assertEquals(PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("home2")).getBase(),
                "home2 should not be eaten (still STANDING, not RIP) when Pro reroll saves the failed Always Hungry check (1 -> reroll 2)");
    }

    @Test
    void alwaysHungrySucceedsAndThrowProceeds() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Always Hungry").skill("Throw Team-Mate"))
                        .player("home2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).skill(6)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3).skill(6);
        StepEngine.respond(state, Commands.throwTeammate("home1", "home2"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("home1", new FieldCoordinate(9, 7)));

        assertEquals(new FieldCoordinate(11, 5), state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home2")),
                "Always Hungry succeeds on 2+, so the throw proceeds: accurate roll to (9,7) then scatters"
                        + " NORTH, NORTHEAST, EAST (1,2,3) to land at (11,5)");
    }

    @Test
    void ttmFumbleTriggersAlwaysHungryCheck() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Always Hungry").skill("Throw Team-Mate"))
                        .player("home2", p -> p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6).skill(1).scatterDirection(1).skill(1).armour(1, 1);
        StepEngine.respond(state, Commands.throwTeammate("home1", "home2"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("home1", new FieldCoordinate(9, 7)));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("home2")).getBase(),
                "TTM fumble (roll 1) is processed after the Always Hungry check passes (roll 6);"
                        + " a failed landing roll (armour not broken) leaves home2 prone on the pitch");
    }
}
