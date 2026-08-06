package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DivingTackleTest extends AbstractStateTest {

    @Test
    void canTackleDodgingPlayer() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Diving Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(5);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "Diving Tackle can tackle dodging player - player should have a coordinate after dodge");
    }

    @Test
    void divingTacklePutsUserProne() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Diving Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(4).armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        Player<?> away1 = state.getGame().getPlayerById("away1");
        StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.DIVING_TACKLE,
                new Player<?>[]{away1}));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(away1).isStanding(),
                "Diving Tackle user should be prone after using Diving Tackle");
    }

    @Test
    void divingTackleCancelsLeap() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8).skill("Leap")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8).skill("Diving Tackle"))
                        .player("away2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7),
                new FieldCoordinate(9, 7)));

        assertNotNull(state.getCurrentStep(),
                "Diving Tackle can cancel Leap - game should be in valid state");
    }

    @Test
    void divingTackleVsAgOnePlusDodger() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 1, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Diving Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(3);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));

        assertNotNull(state.getCurrentStep(),
                "Diving Tackle vs AG1+ dodger (always fails on 1, Diving Tackle makes dodge harder) - game in valid state");
    }

    @Test
    void divingTackleWithTwoTackleZonePenalties() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Diving Tackle"))
                        .player("away2", p -> p.at(8, 6).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(5).armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        Player<?> away1 = state.getGame().getPlayerById("away1");
        StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.DIVING_TACKLE,
                new Player<?>[]{away1}));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(away1).isStanding(),
                "Diving Tackle with two TZ penalties - the DT user should be prone after diving");
        assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Diving Tackle with two TZ penalties - the dodger should be down because the two TZ penalties raised the dodge minimum (roll 5 < 6) and Diving Tackle was used");
    }

    @Test
    void divingTackleUserLandsInVacatedSquare() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 4, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("Diving Tackle")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        TestRolls.on(state).skill(4).armour(6, 6).injury(2, 2);
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        Player<?> away1 = state.getGame().getPlayerById("away1");
        StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.DIVING_TACKLE,
                new Player<?>[]{away1}));

        Game game = state.getGame();
        assertFalse(game.getFieldModel().getPlayerState(away1).isStanding(),
                "Diving Tackle user lands in vacated square - the DT user should be prone after diving");
        assertEquals(new FieldCoordinate(7, 7), game.getFieldModel().getPlayerCoordinate(away1),
                "Diving Tackle user lands in vacated square - the DT user lands in (7,7), the square the dodging player vacated");
    }
}
