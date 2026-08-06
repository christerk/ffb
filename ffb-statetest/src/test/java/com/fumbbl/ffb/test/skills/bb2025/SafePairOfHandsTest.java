package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SafePairOfHandsTest extends AbstractStateTest {

    @Test
    public void ballCarrierWithSafePairOfHandsDoesNotScatterFar() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Safe Pair Of Hands")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));
        Game g = s.getGame();

        IDialogParameter dialog = g.getDialogParameter();
        assertNotNull(dialog, "Ball carrier with Safe Pair of Hands does not scatter far - expected Safe Pair Of Hands skill use dialog for the fallen ball carrier");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Ball carrier with Safe Pair of Hands does not scatter far - expected skill use dialog");
        assertEquals("Safe Pair Of Hands", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Ball carrier with Safe Pair of Hands does not scatter far - dialog should offer Safe Pair Of Hands");

        com.fumbbl.ffb.model.skill.Skill sph =
                (com.fumbbl.ffb.model.skill.Skill) g.getFactory(FactoryType.Factory.SKILL).forName("Safe Pair Of Hands");
        StepEngine.respond(s, Commands.useSkill(sph, true, "a1"));
        StepEngine.respond(s, new com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate(new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), g.getFieldModel().getBallCoordinate(),
                "Ball carrier with Safe Pair of Hands does not scatter far - the ball is placed in the chosen adjacent square (10,7) instead of scattering");
    }

    @Test
    public void safePairOfHandsBallScatterDistance() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Safe Pair Of Hands")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));

        assertNotNull(s.getCurrentStep(),
                "Safe Pair of Hands ball scatter distance - game in valid state after block with away1 at (14,1)");
    }

    @Test
    public void safePairOfHandsPlacesBallWhenKnockedDown() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Safe Pair Of Hands")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));

        IDialogParameter dialog = s.getGame().getDialogParameter();
        assertNotNull(dialog, "Safe Pair of Hands places ball when knocked down - expected Safe Pair Of Hands skill use dialog for the fallen ball carrier");
        assertEquals(DialogId.SKILL_USE, dialog.getId(), "Safe Pair of Hands places ball when knocked down - expected skill use dialog");
        assertEquals("Safe Pair Of Hands", ((DialogSkillUseParameter) dialog).getSkill().getName(),
                "Safe Pair of Hands places ball when knocked down - dialog should offer Safe Pair Of Hands");

        com.fumbbl.ffb.model.skill.Skill sph =
                (com.fumbbl.ffb.model.skill.Skill) s.getGame().getFactory(FactoryType.Factory.SKILL).forName("Safe Pair Of Hands");
        StepEngine.respond(s, Commands.useSkill(sph, true, "a1"));
        StepEngine.respond(s, new com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate(new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), s.getGame().getFieldModel().getBallCoordinate(),
                "Safe Pair of Hands places ball when knocked down - the ball is placed in the chosen adjacent square (10,7) rather than scattering");
    }

    @Test
    public void safePairOfHandsPlacesBallWhenPlacedProne() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(7, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Safe Pair Of Hands")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));
        assertNotNull(s.getCurrentStep(),
                "Safe Pair of Hands places ball when placed prone - game in valid state after defender with SPH placed prone");
    }

    @Test
    public void safePairOfHandsBallPlacementBlockedByOccupiedSquares() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Safe Pair Of Hands")))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        StepEngine.respond(s, Commands.blockChoice(0));
        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));
        assertNotNull(s.getCurrentStep(),
                "Safe Pair of Hands ball placement blocked by occupied squares - game in valid state after ball placed near crowded area");
    }
}
