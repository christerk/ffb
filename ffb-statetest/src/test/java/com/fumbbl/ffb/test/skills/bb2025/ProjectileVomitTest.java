package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProjectileVomitTest extends AbstractStateTest {

    @Test
    public void blockAlternative() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Projectile Vomit")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.vomit("h1", "a1"));
        assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding());
    }

    @Test
    public void projectileVomitArmorFails() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Projectile Vomit")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(1).armour(1, 1);
        StepEngine.respond(g, Commands.vomit("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void vomitBreaksArmorAndRollsInjury() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Projectile Vomit")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.vomit("h1", "a1"));
        assertFalse(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("a1")).isStanding());
    }

    @Test
    public void projectileVomitAtRangeOnProjectileVomitStep() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Projectile Vomit")))
                .withTeam(false, t -> t.player("a1", p -> p.at(9, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(6).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.vomit("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void projectileVomitPlusMightyBlowDoesNotApply() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Projectile Vomit")
                        .skill("Mighty Blow")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).skill(1).armour(1, 1);
        StepEngine.respond(g, Commands.vomit("h1", "a1"));
        assertNotNull(g.getCurrentStep());
    }
}
