package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TentaclesTest extends AbstractStateTest {

    private GameState build(int dodgerStrength, String dodgerSkill, int tentacleStrength) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
                .withTeam(true, t -> t.player("h1", p -> {
                    p.at(7, 7).stats(6, dodgerStrength, 3, 5, 8);
                    if (dodgerSkill != null) {
                        p.skill(dodgerSkill);
                    }
                }))
                .withTeam(false, t -> t.player("a1", p -> p.at(7, 8).stats(6, tentacleStrength, 3, 5, 8)
                        .skill("Tentacles")));
        return builder.build();
    }

    private void startMove(GameState state) {
        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("h1", PlayerAction.MOVE));
    }

    private void chooseTentaclesPlayer(GameState state) {
        Player<?> tentaclesPlayer = state.getGame().getPlayerById("a1");
        StepEngine.respond(state,
                new ClientCommandPlayerChoice(PlayerChoiceMode.TENTACLES, new Player<?>[]{tentaclesPlayer}));
    }

    private void attemptEscapeHeld(GameState state, int tentaclesRoll) {
        TestRolls.on(state).skill(tentaclesRoll);
        StepEngine.respond(state, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        chooseTentaclesPlayer(state);
    }

    private void attemptEscapeSuccessful(GameState state, int tentaclesRoll, int dodgeRoll) {
        TestRolls.on(state).skill(tentaclesRoll).skill(dodgeRoll);
        StepEngine.respond(state, Commands.move("h1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        chooseTentaclesPlayer(state);
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    @Test
    public void holdsPlayersInTZ() {
        GameState g = build(3, null, 5);
        this.gameState = g;
        startMove(g);
        attemptEscapeHeld(g, 4);
        assertEquals(new FieldCoordinate(7, 7), position(g, "h1"),
                "h1 (ST3) should be held in place at (7,7) when the ST5 Tentacles player rolls 4+ (minimum 4) and wins the opposed roll");
    }

    @Test
    public void tentaclesFailsToHoldPlayer() {
        GameState g = build(5, null, 3);
        this.gameState = g;
        startMove(g);
        attemptEscapeSuccessful(g, 1, 6);
        assertEquals(new FieldCoordinate(7, 6), position(g, "h1"),
                "h1 (ST5) should escape to (7,6) when the ST3 Tentacles player needs 8+ (impossible) and cannot hold the dodger");
    }

    @Test
    public void tentaclesHoldsPlayerWithEqualSt() {
        GameState g = build(3, null, 3);
        this.gameState = g;
        startMove(g);
        attemptEscapeHeld(g, 6);
        assertEquals(new FieldCoordinate(7, 7), position(g, "h1"),
                "h1 (ST3) should be held at (7,7) when the ST3 Tentacles player rolls a perfect 6 (minimum 6 for equal strength)");
    }

    @Test
    public void tentaclesHoldsLowStPlayerHardToEscape() {
        GameState g = build(2, null, 5);
        this.gameState = g;
        startMove(g);
        attemptEscapeHeld(g, 3);
        assertEquals(new FieldCoordinate(7, 7), position(g, "h1"),
                "h1 (ST2) should be held at (7,7) when the ST5 Tentacles player rolls 3+ (minimum 3) - low ST dodger is hard to escape from");
    }

    @Test
    public void tentaclesVsStuntyTriggersBeforeDodge() {
        GameState g = build(2, "Stunty", 5);
        this.gameState = g;
        startMove(g);
        attemptEscapeHeld(g, 3);
        assertEquals(new FieldCoordinate(7, 7), position(g, "h1"),
                "The Tentacles check (held on roll 3) fires before any dodge roll; the Stunty dodger stays at (7,7) and no dodge die is consumed");
    }

    @Test
    public void tentaclesTieResultDodgerEscapes() {
        GameState g = build(3, null, 3);
        this.gameState = g;
        startMove(g);
        attemptEscapeSuccessful(g, 3, 6);
        assertEquals(new FieldCoordinate(7, 6), position(g, "h1"),
                "With equal ST the Tentacles player needs a 6 to hold; a roll of 3 lets the dodger escape to (7,6) - equal totals go to the dodger");
    }

    @Test
    public void tentaclesTieScenarioWithDifferentSTAdvantage() {
        GameState g = build(3, null, 5);
        this.gameState = g;
        startMove(g);
        attemptEscapeSuccessful(g, 1, 6);
        assertEquals(new FieldCoordinate(7, 6), position(g, "h1"),
                "Even with a 2-point ST advantage the ST5 Tentacles player can fail (roll 1 < minimum 4); the ST3 dodger escapes to (7,6)");
    }
}
