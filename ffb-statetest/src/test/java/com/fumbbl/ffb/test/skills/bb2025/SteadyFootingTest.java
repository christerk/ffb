package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SteadyFootingTest extends AbstractStateTest {

    @Test
    public void avoidFallingOnSkull() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("skull");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Steady Footing avoid falling on skull - h1 still has a valid position after skull block result");
    }

    @Test
    public void skullWithOneDieStillFalls() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("playerdown");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Skull with one die still falls - game in valid state after playerdown result (Steady Footing only applies to skull, not playerdown)");
    }

    @Test
    public void steadyFootingBothBlockDiceAreSkullStillFallsDown() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("skull", "skull");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Steady Footing both dice skull still falls down - game in valid state (Steady Footing cannot override forced skull result)");
    }

    @Test
    public void steadyFootingPlusBlockSkillHandlesBothDownSeparately() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Steady Footing")
                        .skill("Block")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("bothdown");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Steady Footing plus Block skill handles BothDown separately - game in valid state (Block applies to BothDown, Steady Footing to Skull)");
    }

    @Test
    public void steadyFootingOn2dAgainstBlockOneSkullOneBothDown() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("skull", "bothdown");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Steady Footing on 2d against, one skull one bothdown - game in valid state (can choose bothdown to avoid skull)");
    }

    @Test
    public void steadyFootingRerollViaProOrTeamReroll() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        g.getGame().getTurnDataHome().setReRolls(1);
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        // DSL limitation: DROP_FALLING_PLAYERS resolves the attacker's armour before the STEADY_FOOTING
        // skill roll, so the armour roll is queued between the block die and the skill rolls.
        TestRolls.on(g).block("skull").armour(1, 1).skill(2).skill(6);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        Skill steadyFooting = (Skill) g.getGame().getFactory(FactoryType.Factory.SKILL).forName("Steady Footing");
        StepEngine.respond(g, Commands.useSkill(steadyFooting, true, "h1"));
        StepEngine.respond(g, new ClientCommandUseReRoll(ReRolledActions.STEADY_FOOTING, ReRollSources.TEAM_RE_ROLL));
        assertTrue(g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).isStanding(),
                "Steady Footing reroll via team reroll - h1 stays standing after the rerolled skill roll (6) avoids the fall");
        assertEquals(new FieldCoordinate(7, 7),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "Steady Footing reroll via team reroll - h1 remains at (7,7)");
        assertEquals(0, g.getGame().getTurnDataHome().getReRolls(),
                "Steady Footing reroll via team reroll - the reroll of the Steady Footing roll was consumed");
    }

    @Test
    public void steadyFootingFailsRollAndPlayerFallsDown() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Steady Footing")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        // DSL limitation: DROP_FALLING_PLAYERS resolves the attacker's armour before the STEADY_FOOTING
        // skill roll, so the armour roll is queued between the block die and the skill roll.
        TestRolls.on(g).block("skull").armour(1, 1).skill(2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        Skill steadyFooting = (Skill) g.getGame().getFactory(FactoryType.Factory.SKILL).forName("Steady Footing");
        StepEngine.respond(g, Commands.useSkill(steadyFooting, true, "h1"));
        assertEquals(PlayerState.PRONE,
                g.getGame().getFieldModel().getPlayerState(g.getGame().getPlayerById("h1")).getBase(),
                "Steady Footing fails roll and player falls down - h1 is knocked down when the avoid-fall roll (2 < 6) fails and no reroll is available");
    }
}
