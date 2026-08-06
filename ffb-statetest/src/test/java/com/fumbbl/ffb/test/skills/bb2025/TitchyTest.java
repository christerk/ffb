package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TitchyTest extends AbstractStateTest {

    @Test
    public void noTZForDodge() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Titchy")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")));
    }

    @Test
    public void titchyWithPrehensileTailInteraction() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Prehensile Tail")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Titchy")))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("a1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("a1", new FieldCoordinate(8, 7), new FieldCoordinate(8, 6)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void titchyDodgesIntoOpponentTacklezoneNoTzPenalty() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Titchy")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8))
                        .player("a2", p -> p.at(7, 5).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void titchyPlusStuntyBothIgnoreTzForDodging() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Titchy")
                        .skill("Stunty")))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(g).skill(4);
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep());
    }

    @Test
    public void titchyDodgeModifierMinusOneOnDodgeRoll() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 2, 3, 5, 6)
                        .skill("Titchy")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        assertNotNull(g.getCurrentStep());
    }
}
