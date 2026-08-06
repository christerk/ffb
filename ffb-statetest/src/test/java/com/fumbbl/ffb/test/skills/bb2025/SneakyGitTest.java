package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SneakyGitTest extends AbstractStateTest {

    @Test
    public void avoidEjection() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Sneaky Git")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(1, 1);
        StepEngine.respond(g, Commands.foul("h1", "a1"));
        assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).isStanding());
    }

    @Test
    public void sneakyGitEjectedOnDoubles() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025").withBallAt(7, 1).withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sneaky Git"))).withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).state(new PlayerState(PlayerState.PRONE).changeActive(true)))).build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.foul("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void dirtyPlayerAndSneakyGitFoulInteraction() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Dirty Player")
                        .skill("Sneaky Git")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.foul("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void sneakyGitPreventsEjectionOnNonDoubleArmorBreak() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Sneaky Git")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(1, 2);
        StepEngine.respond(g, Commands.foul("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void sneakyGitBribeOverridesEjectionAfterDoubles() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025").withBallAt(7, 1).withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Sneaky Git"))).withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).state(new PlayerState(PlayerState.PRONE).changeActive(true)))).build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(2, 2);
        StepEngine.respond(g, Commands.foul("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }
}
