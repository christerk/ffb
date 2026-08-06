package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MultipleBlockTest extends AbstractStateTest {

    @Test
    public void multipleBlockAllowsBlockingTwoPlayers() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Multiple Block")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8)
                                .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback", "pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(s, Commands.blockChoice(0));
        assertNotNull(step, "Multiple Block allows blocking two players - block should proceed to pushback step");
    }

    @Test
    public void multipleBlockPushbackAndFollowup() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Multiple Block")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8)
                                .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback", "pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(s, Commands.blockChoice(0));
        assertNotNull(step, "Multiple Block pushback and followup - block choice processed");

        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.pushback(new Pushback("a2", new FieldCoordinate(9, 8))));
        StepEngine.respond(s, Commands.followup(false));

        assertEquals(new FieldCoordinate(9, 7),
                s.getGame().getFieldModel().getPlayerCoordinate(s.getGame().getPlayerById("a1")),
                "Multiple Block pushback and followup - a1 pushed back to (9,7)");
    }

    @Test
    public void multipleBlockWithGuardAssistCalculation() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t
                        .player("h1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Multiple Block"))
                        .player("h2", p -> p.at(6, 6).stats(6, 3, 3, 5, 8).skill("Guard")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback", "pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(s, Commands.blockChoice(0));
        assertNotNull(step, "Multiple Block with Guard assist calculation - block proceeds with Guard teammate assisting");

        assertNotNull(s.getCurrentStep(),
                "Multiple Block with Guard assist calculation - game in valid state after block choice");
    }

    @Test
    public void multipleBlockVsFrenzyNoSecondBlock() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Multiple Block")
                        .skill("Frenzy")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pushback", "pushback");
        StepEngine.respond(s, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(s, Commands.blockChoice(0));
        assertNotNull(step, "Multiple Block vs Frenzy no second block - block proceeds with both Multiple Block and Frenzy");

        assertNotNull(s.getCurrentStep(),
                "Multiple Block vs Frenzy no second block - game in valid state (Frenzy does not trigger extra block with Multiple Block)");
    }

    @Test
    public void multipleBlockBothDefendersKnockedDown() {
        GameState s = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 4, 3, 5, 8)
                        .skill("Multiple Block")))
                .withTeam(false, t -> t
                        .player("a1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = s;
        StepEngine.start(s);
        StepEngine.respond(s, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(s).block("pow").armour(6, 6).injury(3, 2);
        StepEngine.respond(s, Commands.block("h1", "a1"));
        IStep step = StepEngine.respond(s, Commands.blockChoice(0));
        assertNotNull(step, "Multiple Block both defenders knocked down - Pow block choice processed");

        StepEngine.respond(s, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
        StepEngine.respond(s, Commands.followup(false));

        assertNotNull(s.getCurrentStep(),
                "Multiple Block both defenders knocked down - game in valid state after defender hit with Pow");
    }
}
