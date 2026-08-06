package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShadowingTest extends AbstractStateTest {

    @Test
    public void followsDodger() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 4, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(5).skill(5);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing follows dodger - a1 shadows h1 into the vacated square (7,7) after a successful shadowing roll (5 >= 4)");
    }

    @Test
    public void shadowingFailsToFollow() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 5, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(6).skill(1);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 8),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing fails to follow - a1 stays at (7,8) when the shadowing roll (1) is below the 4+ threshold");
    }

    @Test
    public void shadowingSucceedsWithMaAdvantage() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 5, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing succeeds with MA advantage - a1 (MA5 > h1 MA3) shadows h1 into (7,7)");
    }

    @Test
    public void shadowingFailsAgainstMuchFasterOpponent() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 8, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4).skill(1);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 8),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing fails against much faster opponent - a1 stays at (7,8) when the shadowing roll (1) fails");
    }

    @Test
    public void shadowingFollowsIntoOpponentTacklezone() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(7, 5).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 5, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4).skill(6);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing follows into opponent tackle zone - a1 shadows h1 into (7,7) even though it is adjacent to h2 at (7,5)");
    }

    @Test
    public void shadowingDoesNotTriggerWhenStayingInTzNormalMove() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 5, 3, 5, 8)
                        .skill("Shadowing")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(g, Commands.shadowing(g.getGame().getPlayerById("a1")));

        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Shadowing does not trigger when staying in TZ normal move - a1 shadows h1 into (7,7)");
    }
}
