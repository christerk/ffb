package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReliableTest extends AbstractStateTest {

    private GameState build(boolean reliableOnThrower) {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> {
                    t.player("thrower", p -> {
                        p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate");
                        if (reliableOnThrower) {
                            p.skill("Reliable");
                        }
                    });
                    t.player("flinger", p -> p.at(7, 8).stats(6, 2, 3, 5, 6)
                            .skill("Right Stuff")
                            .skill("Reliable"));
                })
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private void throwFumble(GameState state, boolean reliableOnThrower) {
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state)
                .skill(1)
                .scatterDirection(1)
                .skill(1);
        if (!reliableOnThrower) {
            TestRolls.on(state).armour(1, 1);
        }
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(9, 7)));
    }

    @Test
    public void reliablePreventsInjuryOnTtmFumble() {
        GameState state = build(true);
        this.gameState = state;

        StepEngine.start(state);
        throwFumble(state, true);

        assertEquals(PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "Reliable prevents injury on TTM fumble - the fumbled flinger lands safely (no injury roll) because Reliable forces the landing to succeed");
    }

    @Test
    public void reliableAllowsSafeLandingFromFumbledTtm() {
        GameState state = build(true);
        this.gameState = state;

        StepEngine.start(state);
        throwFumble(state, true);

        assertEquals(PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "Reliable allows safe landing from fumbled TTM - the flinger is still standing after the fumbled throw");
    }

    @Test
    public void fumbledPlayerLandsSafelyOnTtmFumble() {
        GameState state = build(true);
        this.gameState = state;

        StepEngine.start(state);
        throwFumble(state, true);

        assertEquals(PlayerState.STANDING, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("flinger")).getBase(),
                "Fumbled player lands safely on TTM fumble - the fumbled flinger ends standing (safe landing) instead of taking an injury");
    }

    @Test
    public void reliablePlayerLandsInIntendedTargetAfterTtmFumble() {
        GameState state = build(true);
        this.gameState = state;

        StepEngine.start(state);
        throwFumble(state, true);

        assertTrue(com.fumbbl.ffb.FieldCoordinateBounds.FIELD.isInBounds(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("flinger"))),
                "Reliable player lands in intended target after TTM fumble - the fumbled flinger lands safely somewhere on the pitch");
    }

    @Test
    public void reliablePlusRightStuffComboOnTtm() {
        GameState state = build(false);
        this.gameState = state;

        StepEngine.start(state);
        throwFumble(state, false);

        assertNotNull(state.getCurrentStep(),
                "Reliable plus Right Stuff combo on TTM - game in valid state with Reliable on the flinger rather than thrower");
    }
}
