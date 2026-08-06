package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThickSkullTest extends AbstractStateTest {

    @Test
    public void thickSkullConvertsKoToStunOnInjuryRollOfEight() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Thick Skull")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(3, 5);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(PlayerState.STUNNED, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Injury total 8 with Thick Skull converts KO to Stun - a1 is STUNNED (not KO)");
    }

    @Test
    public void thickSkullDoesNotPreventKOOnInjuryNot8() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Thick Skull")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(4, 4);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(PlayerState.STUNNED, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Injury total 8 (from 4+4) with Thick Skull converts KO to Stun - a1 is STUNNED");
    }

    @Test
    public void thickSkullPlusMightyBlowStillConvertsKoToStun() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Mighty Blow")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Thick Skull")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        // armour(6,6)=12 already breaks, so Mighty Blow's +1 is applied to the INJURY roll (not armour). injury
        // 3+4=7 + Mighty Blow(+1) = 8, so Thick Skull (convert at exactly 8) converts the KO to Stun.
        TestRolls.on(g).block("pow").armour(6, 6).injury(3, 4);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(PlayerState.STUNNED, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Injury 7 + Mighty Blow +1 = 8, Thick Skull converts KO to Stun - a1 is STUNNED");
    }

    @Test
    public void thickSkullOnInjuryRoll7StillKoNoConversion() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
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
        assertEquals(PlayerState.STUNNED, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Injury total 7 is Stun in BB2025 - Thick Skull only converts on exactly 8, so no conversion triggers");
    }

    @Test
    public void thickSkullOnInjuryRoll9AlreadyCasualtyNoEffect() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Thick Skull")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(5, 4);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertEquals(PlayerState.KNOCKED_OUT, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Injury total 9 is KO in BB2025 - Thick Skull only converts on exactly 8, so a1 is KO'd");
        assertTrue(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")).isBoxCoordinate(),
                "The KO'd player is placed in the KO box");
    }

    @Test
    public void thickSkullOnStuntyPlayerConvertsKoToStunAtInjury7() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
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
        assertEquals(PlayerState.STUNNED, g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).getBase(),
                "Stunty injury total 7 is normally KO, but Thick Skull converts it to Stun");
    }
}
