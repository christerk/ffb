package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ClientCommandPileDriver;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PileDriverTest extends AbstractStateTest {

    @Test
    public void pileDriverFoulsAfterBlockKnockdown() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(2, 2).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));
        StepEngine.respond(state, new ClientCommandPileDriver("away1"));
        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
                "Defender should be down after pile driver block+foul, state=" + game.getFieldModel()
                        .getPlayerState(game.getPlayerById("away1"))
                        .getBase());
    }

    @Test
    public void pileDriverAfterPushbackDoesNotFoul() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void canFoulAfterBlockArmorBreakSucceeds() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 6).injury(3, 2).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));
        StepEngine.respond(state, new ClientCommandPileDriver("away1"));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding());
    }

    @Test
    public void canFoulAfterBlockInjuryRollSucceedsAfterArmorBreak() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 6).injury(3, 2).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));
        StepEngine.respond(state, new ClientCommandPileDriver("away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void pileDriverCannotFoulAfterBlockIfSentOffSecretWeapon() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver").skill("Secret Weapon")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(2, 2).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));
        StepEngine.respond(state, new ClientCommandPileDriver("away1"));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void pileDriverPlusDirtyPlayerOnFollowUpFoul() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025").withBallAt(7, 1)
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pile Driver").skill("Dirty Player")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();

        this.gameState = state;
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(6, 6).injury(3, 2).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(true));
        StepEngine.respond(state, new ClientCommandPileDriver("away1"));

        assertNotNull(state.getCurrentStep());
    }
}
