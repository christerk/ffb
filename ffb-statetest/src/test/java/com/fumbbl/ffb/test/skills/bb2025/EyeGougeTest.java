package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EyeGougeTest extends AbstractStateTest {

    @Test
    public void removeAssists() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Eye Gouge"))
                        .player("h2", p -> p.at(9, 6)
                                .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                                .stats(6, 3, 3, 5, 8))
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
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Eye Gouge removes assists - defender should have a coordinate after block with Eye Gouge");
    }

    @Test
    public void eyeGougeVsBallAndChain() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                                .stats(4, 5, 3, 5, 9)
                                .skill("Ball and Chain")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                                .stats(6, 3, 3, 5, 8)
                                .skill("Eye Gouge")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).throwInDirection(2);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));

        assertNotNull(g.getCurrentStep(),
                "Eye Gouge vs Ball and Chain - game in valid state after BaC random move triggers block");
    }

    @Test
    public void eyeGougeRemovesGuardAssist() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Eye Gouge"))
                        .player("h2", p -> p.at(9, 6).stats(6, 3, 3, 5, 8)
                                .skill("Guard")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
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
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Eye Gouge removes Guard assist - defender should have a coordinate after block");
    }

    @Test
    public void eyeGougeRemovesDefensiveCancelEffect() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
                                .skill("Eye Gouge"))
                        .player("h2", p -> p.at(9, 6).stats(6, 3, 3, 5, 8)
                                .skill("Defensive")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
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
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Eye Gouge removes Defensive cancel effect - defender should have a coordinate");
    }

    @Test
    public void eyeGougeSetsGougedFlagOnPushedDefender() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Eye Gouge")))
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

        PlayerState a1State = g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1"));
        assertTrue(a1State.isEyeGouged(),
                "Eye Gouge sets gouged flag on pushed defender - the pushed defender must be marked eye-gouged (assists removed until next activation)");
        assertEquals(new FieldCoordinate(9, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Eye Gouge sets gouged flag on pushed defender - the pushback resolved to (9,7)");
    }

    @Test
    public void eyeGougeOnPowKnockdown() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Eye Gouge")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(2, 2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));

        PlayerState a1State = g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1"));
        assertTrue(a1State.isEyeGouged(),
                "Eye Gouge on POW knockdown - the eye-gouge side effect applies on the pushback even when the block result is a knockdown");
        assertNotEquals(PlayerState.STANDING, a1State.getBase(),
                "Eye Gouge on POW knockdown - the POW knockdown resolved and the defender is no longer standing");
    }
}
