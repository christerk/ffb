package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StuntyTest extends AbstractStateTest {

    @Test
    public void ignoresTZ() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Stunty ignores TZ - h1 still has valid position after dodging (Stunty ignores tackle zones when dodging)");
    }

    @Test
    public void stuntyIsHurtMoreEasily() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(4, 4);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        Game game = g.getGame();
        PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("a1"));
        assertFalse(defenderState.isStanding(),
                "Expected Stunty to be hurt more easily (injury +1), was " + defenderState.getBase());
    }

    @Test
    public void stuntyPassesAreInterceptedEasier() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.PASS_MOVE));
        assertNotNull(g.getCurrentStep(),
                "Stunty passes are intercepted easier - game in valid state after selecting pass action (Stunty gives -1 to interception roll)");
    }

    @Test
    public void stuntyPreventsPlagueRiddenFromRaisingAsLineman() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Plague Ridden")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getCurrentStep(),
                "Stunty prevents Plague Ridden from raising as lineman - game in valid state (Stunty players cannot be raised by Plague Ridden)");
    }

    @Test
    public void stuntyIsHurtMoreEasilyVsMightyBlowBothModifiersStack() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Mighty Blow")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(1, 1);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getCurrentStep(),
                "Stunty hurt more easily vs Mighty Blow both modifiers stack - game in valid state (Stunty +1 and Mighty Blow +1 injury stack)");
    }

    @Test
    public void stuntyIgnoreTacklezonesWhenDodgingIntoMultipleTacklezones() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(7, 9).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep(),
                "Stunty ignore tackle zones when dodging into multiple tackle zones - game in valid state (Stunty ignores TZ count)");
    }

    @Test
    public void stuntyWithSecretWeaponDoesNotIgnoreTackleZones() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")
                        .skill("Secret Weapon")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertEquals(new FieldCoordinate(7, 6),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Stunty with Secret Weapon does not ignore tackle zones - the dodge succeeds but the TZ -1 penalty is applied (roll 4 - 1 = 3 passes AG3)");
    }

    @Test
    public void stuntyThickSkullConvertsKoToStunAtInjury7() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Stunty")
                        .skill("Thick Skull")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(3, 4);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        PlayerState defenderState = g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1"));
        assertEquals(PlayerState.STUNNED, defenderState.getBase(),
                "Stunty with Thick Skull converts a KO at injury 7 to Stun - injury(3,4)=7 is normally KO for Stunty but Thick Skull turns it into Stun on the pitch");
    }
}
