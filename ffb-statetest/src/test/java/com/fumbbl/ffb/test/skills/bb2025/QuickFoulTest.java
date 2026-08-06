package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;

import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuickFoulTest extends AbstractStateTest {

    @Test
    public void moveAfterFoul() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Quick Foul")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(2, 3);
        StepEngine.respond(g, Commands.foul("h1", "a1"));


        assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).isStanding(),
                "Quick Foul move after foul - h1 should be standing after fouling (armor 2+3=5 doesn't break AV8)");

        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 6),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Quick Foul move after foul - h1 should be able to move from (7,7) to (7,6) after the foul");
    }

    @Test
    public void quickFoulFoulThenGfiThenDodge() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(3, 3, 4, 5, 8)
                        .skill("Quick Foul")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true)))
                        .player("a2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(2, 3);
        StepEngine.respond(g, Commands.foul("h1", "a1"));

        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(5);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 6), new FieldCoordinate(7, 5)));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 5), new FieldCoordinate(7, 4)));
        TestRolls.on(g).goingForIt(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 4), new FieldCoordinate(7, 3)));

        assertEquals(new FieldCoordinate(7, 3),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Quick Foul foul then GFI then dodge - h1 should reach (7,3) after fouling, then dodging out of a2's tackle zone (skill 5) and GFI-ing the last square");
    }

    @Test
    public void quickFoulPerformsGfiAfterFouling() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(4, 3, 3, 5, 8)
                        .skill("Quick Foul")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        TestRolls.on(g).armour(2, 3);
        StepEngine.respond(g, Commands.foul("h1", "a1"));


        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).goingForIt(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6),
                new FieldCoordinate(7, 5), new FieldCoordinate(7, 4), new FieldCoordinate(7, 3),
                new FieldCoordinate(7, 2)));

        assertEquals(new FieldCoordinate(7, 2),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Quick Foul performs GFI after fouling - h1 should move 5 squares (MA4 + 1 GFI) from (7,7) to (7,2) after the foul");
    }

    @Test
    public void quickFoulCannotMoveAfterFoulIfNoMaRemaining() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Quick Foul")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        g.getGame().getActingPlayer().setCurrentMove(6);
        TestRolls.on(g).armour(2, 3);
        StepEngine.respond(g, Commands.foul("h1", "a1"));


        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Quick Foul cannot move after foul if no MA remaining - h1 stays at (7,7) with MA exhausted");
    }

    @Test
    public void quickFoulNoRemainingMoveStaysInPlace() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Quick Foul")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));
        g.getGame().getActingPlayer().setCurrentMove(6);
        TestRolls.on(g).armour(2, 3);
        StepEngine.respond(g, Commands.foul("h1", "a1"));

        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Quick Foul no remaining move stays in place - h1 with MA exhausted stays at (7,7) after the foul");
    }
}




