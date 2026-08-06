package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandUseFumblerooskie;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FumblerooskiTest extends AbstractStateTest {

    @Test
    public void canDropBall() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Fumblerooski")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Fumblerooski player should have a coordinate after block action");
    }

    @Test
    public void dropBallWithOpponentInTackleZone() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Fumblerooski")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        // DSL limitation: the doc's step list omits the Fumblerooski activation command; without it the
        // fumblerooskiePending flag is never set and the ball follows the carrier instead of staying at the start.
        StepEngine.respond(g, new ClientCommandUseFumblerooskie());
        TestRolls.on(g).skill(6);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Fumblerooski drop ball with opponent in tackle zone - the ball is dropped at the starting position (7,7)");
        assertEquals(new FieldCoordinate(7, 6),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Fumblerooski drop ball with opponent in tackle zone - h1 moved to (7,6)");
    }

    @Test
    public void opponentPicksUpBallDroppedByFumblerooski() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Fumblerooski")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        // DSL limitation: the doc's step list omits the Fumblerooski activation command; without it the
        // fumblerooskiePending flag is never set and the ball follows the carrier instead of staying at the start.
        StepEngine.respond(g, new ClientCommandUseFumblerooskie());
        TestRolls.on(g).skill(6);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Opponent picks up ball dropped by Fumblerooski - the ball is dropped at (7,7) for the adjacent opponent a1 to pick up");
        assertEquals(new FieldCoordinate(7, 6),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Opponent picks up ball dropped by Fumblerooski - h1 moved to (7,6)");
    }

    @Test
    public void myBallCancelsFumblerooski() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Fumblerooski")
                        .skill("My Ball")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(6);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertEquals(new FieldCoordinate(7, 6), g.getGame().getFieldModel().getBallCoordinate(),
                "My Ball cancels Fumblerooski - the ball moves with the player to (7,6) instead of being dropped");
        assertNotEquals(new FieldCoordinate(7, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "My Ball cancels Fumblerooski - the ball is NOT dropped at the starting position (7,7)");
    }
}
