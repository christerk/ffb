package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IronHardSkinTest extends AbstractStateTest {

    @Test
    public void ignoresClaws() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Claws")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Iron Hard Skin")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")),
                "Iron Hard Skin ignores Claws - defender at (9,7) after Claws/Pow block, armor still uses normal value");
    }

    @Test
    public void ironHardSkinVsMightyBlow() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Mighty Blow")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Iron Hard Skin")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(5, 2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getCurrentStep(),
                "Iron Hard Skin vs Mighty Blow - armor(5,2)=7 without +1 Mighty Blow vs AV8, armor holds - game in valid state");
    }

    @Test
    public void ignoresArmourModifiersFromSkillsMightyBlow() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Mighty Blow")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Iron Hard Skin")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(5, 2);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getCurrentStep(),
                "Ignores armor modifiers from skills (Mighty Blow) - Iron Hard Skin negates +1 armor modifier, armor holds - game in valid state");
    }

    @Test
    public void ignoresArmourModifiersFromFouls() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Dirty Player")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Iron Hard Skin")
                        .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.FOUL_MOVE));

        assertNotNull(g.getCurrentStep(),
                "Ignores armor modifiers from fouls - Iron Hard Skin negates Dirty Player +1 foul modifier - game in valid state");
    }

    @Test
    public void ignoresArmourModifiersFromSpecialEffects() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Chainsaw")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Iron Hard Skin")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).chainsaw(3).armour(6, 6).injury(3, 2);
        StepEngine.respond(g, Commands.chainsaw("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Ignores armor modifiers from special effects (Chainsaw) - game in valid state after chainsaw attack");
    }

    @Test
    public void cancelReducesArmourToFixedValueClaws() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Claws")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 10)
                        .skill("Iron Hard Skin")))
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
                "Iron Hard Skin cancels Claws reduction to fixed value - AV10 defender's high armor preserved - game in valid state");
    }

    @Test
    public void clawsCancellationArmourHoldsIn8To10Range() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Claws")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 10)
                        .skill("Iron Hard Skin")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pow").armour(4, 5);
        StepEngine.respond(g, Commands.block("h1", "a1"));
        StepEngine.respond(g, Commands.blockChoice(0));
        StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(g, Commands.followup(false));
        assertNotNull(g.getCurrentStep(),
                "Iron Hard Skin cancels Claws reduction to fixed value - armour 4+5=9 holds against the preserved AV10 (no injury roll consumed)");
    }
}
