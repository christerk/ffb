package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GrabTest extends AbstractStateTest {

    @Test
    public void grabAllowsPushToSideSquare() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState s = gameState;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(7, 8))));
        StepEngine.respond(s, Commands.followup(false));
        assertEquals(new FieldCoordinate(7, 8),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("a1")),
                "Grab auto-uses and allows push to side square (7,8)");
    }

    @Test
    public void grabCancelsSidestep() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Sidestep")))
                .build();
        GameState s = gameState;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));
        assertEquals(new FieldCoordinate(9, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("a1")),
                "Grab cancels Sidestep, attacker chooses pushback");
    }

    @Test
    public void grabCanPushBackToAnySquareOnBlockDodge() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState s = gameState;

        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(7, 8))));
        StepEngine.respond(s, Commands.followup(false));

        assertEquals(new FieldCoordinate(7, 8),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("a1")),
                "Grab allows push to side square (7,8)");
    }

    @Test
    public void grabWithFrenzySecondBlockTriggers() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab").skill("Frenzy")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState s = gameState;

        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback").block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(7, 6))));
        StepEngine.respond(s, Commands.followup(true));

        assertNotNull(s.getCurrentStep());
    }

    @Test
    public void grabVsStandFirmPrecedence() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Grab")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Stand Firm")))
                .build();
        GameState s = gameState;

        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(7, 8))));
        StepEngine.respond(s, Commands.followup(false));

        assertNotNull(s.getCurrentStep());
    }
}
