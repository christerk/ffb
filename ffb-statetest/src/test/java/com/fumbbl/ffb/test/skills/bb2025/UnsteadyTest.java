package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UnsteadyTest extends AbstractStateTest {

    private GameState buildWithBall(int opponents) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withBallAt(8, 7)
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Unsteady")));
        builder.withTeam(false, t -> {
            t.player("a1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8));
            if (opponents >= 2) {
                t.player("a2", p -> p.at(7, 9).stats(6, 3, 3, 5, 8));
            }
        });
        return builder.build();
    }

    private void pickUpBall(GameState state) {
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.MOVE));
        TestRolls.on(state).skill(6);
        StepEngine.respond(state, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(8, 7)));
    }

    @Test
    public void unsteadyPreventsSecureBall() {
        GameState g = buildWithBall(1);
        this.gameState = g;
        pickUpBall(g);
        assertEquals(new FieldCoordinate(8, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Unsteady blocks the SECURE_THE_BALL action but not a normal pickup: h1 dodges into the tackle zone and picks up the ball at (8,7) (PICK_UP roll consumed)");
    }

    @Test
    public void unsteadyAllowsOtherActions() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Unsteady")))
                .withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
        TestRolls.on(g).block("pushback");
        StepEngine.respond(g, Commands.block("h1", "a1"));
        assertNotNull(g.getCurrentStep(),
                "Unsteady does not block other actions: a BLOCK action resolves normally with a pushback");
    }

    @Test
    public void unsteadyPreventsSecureTheBallInMultipleOpponentTacklezones() {
        GameState g = buildWithBall(2);
        this.gameState = g;
        pickUpBall(g);
        assertEquals(new FieldCoordinate(8, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "Even with two opponent tackle zones the Unsteady player can still pick up via a normal MOVE; only SECURE_THE_BALL is prevented");
    }

    @Test
    public void unsteadyStillAllowsBlockBlitzAndMove() {
        GameState g = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
                        .stats(6, 3, 3, 5, 8)
                        .skill("Unsteady")))
                .withTeam(false, t -> t.player("a1", p -> p.at(14, 1)
                        .stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = g;
        StepEngine.start(g);
        StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.MOVE));
        StepEngine.respond(g, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 8)));
        assertEquals(new FieldCoordinate(7, 8),
                g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("h1")),
                "An Unsteady player can move freely - only the SECURE_THE_BALL action is blocked");
    }

    @Test
    public void unsteadyPreventsSecureBallEvenWhenOnlyOneOpponentTz() {
        GameState g = buildWithBall(1);
        this.gameState = g;
        pickUpBall(g);
        assertEquals(new FieldCoordinate(8, 7), g.getGame().getFieldModel().getBallCoordinate(),
                "With a single opponent tackle zone the Unsteady player still picks up via a normal MOVE (PICK_UP roll consumed); SECURE_THE_BALL stays blocked");
    }
}
