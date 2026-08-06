package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GuardTest extends AbstractStateTest {

    @Test
    public void assistInTZ() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(8, 8)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Guard")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                                .stats(6, 4, 3, 5, 8))
                        .player("a2", p -> p.at(9, 8)
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
        assertEquals(new FieldCoordinate(9, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Guard assist in TZ - defender pushed back to (9,7)");
    }

    @Test
    public void guardCancelledByDefensive() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Guard"))
                        .player("h2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8).skill("Defensive"))
                        .player("a2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(new FieldCoordinate(9, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Guard cancelled by Defensive - despite h1 having Guard, Defensive neutralizes it, defender pushed back to (9,7)");
    }

    @Test
    public void guardProvidesAssistWhileInOpponentTz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Guard")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8))
                        .player("a2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(new FieldCoordinate(9, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Guard provides assist while in opponent TZ - h2 Guard helps h1 against stronger a1, defender pushed to (9,7)");
    }

    @Test
    public void guardAssistsMultipleBlock() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Multiple Block"))
                        .player("h2", p -> p.at(8, 6).stats(6, 3, 3, 5, 8).skill("Guard")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback", "pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.pushback(new Pushback("a2", new FieldCoordinate(9, 8))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(new FieldCoordinate(9, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Guard assists Multiple Block - a1 pushed to (9,7)");
    }

    @Test
    public void guardWhileInMultipleOpponentTz() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
                        .player("h2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Guard")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8))
                        .player("a2", p -> p.at(9, 8).stats(6, 3, 3, 5, 8))
                        .player("a3", p -> p.at(9, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 6))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(new FieldCoordinate(9, 6),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Guard while in multiple opponent TZ - h2 Guard works even from inside multiple opponent TZs, a1 pushed to (9,6)");
    }
}
