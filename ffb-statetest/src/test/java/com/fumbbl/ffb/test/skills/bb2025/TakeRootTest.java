package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TakeRootTest extends AbstractStateTest {

    @Test
    public void becomesImmovable() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(2).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")));
    }

    @Test
    public void takeRootFailsAndCannotMove() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(2).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void takeRootPreventsMovementButStillAllowsBlockAndBlitz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void takeRootWithAdjacentTeammateReducesFailureChance() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root"))
                        .player("h2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(2);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void takeRootFailsRootsPlayerCannotMove() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        PlayerState playerState = g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1"));
        assertTrue(playerState.isRooted(),
                "A failed Take Root roll (D6=1 below minimum 2+) roots the player (changeRooted(true))");
        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "The failed Take Root roll cancels the MOVE action, so h1 cannot move and stays at (7,7)");
    }

    @Test
    public void takeRootRootedPlayerRefusesPushback() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Take Root")
                        .state(new PlayerState(PlayerState.STANDING).changeActive(true).changeRooted(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));

        assertEquals(new FieldCoordinate(8, 7), g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "The rooted defender auto-refuses the pushback (like Stand Firm), so away1 stays at (8,7)");
        assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isRooted(),
                "The rooted state is preserved after refusing the pushback");
    }

    @Test
    public void takeRootRerollViaTeamReroll() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)
                        .skill("Take Root")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        g.getGame().getTurnDataHome().setReRolls(1);

        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(1);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        TestRolls.on(g).skill(4);
        StepEngine.respond(g, new ClientCommandUseReRoll(ReRolledActions.TAKE_ROOT, ReRollSources.TEAM_RE_ROLL));

        assertEquals(0, g.getGame().getTurnDataHome().getReRolls(),
                "The team reroll is consumed when rerolling the failed Take Root roll");
        assertEquals(new FieldCoordinate(7, 6), g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "The Take Root reroll (D6=4 >= 2) succeeds, so h1 moves normally to (7,6)");
        assertNotNull(g.getCurrentStep());
    }
}
