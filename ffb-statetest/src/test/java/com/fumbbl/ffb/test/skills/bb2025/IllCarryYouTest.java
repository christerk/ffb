package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandFieldCoordinate;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IllCarryYouTest extends AbstractStateTest {

    private GameState build(boolean withPartner, int awayX, int awayY) {
        return new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> {
                    t.player("carrier", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("I'll Carry You", "carrier"));
                    if (withPartner) {
                        t.player("partner", p -> p.at(7, 8).stats(6, 3, 3, 5, 8).skill("I'll Carry You", "carried"));
                    }
                })
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(awayX, awayY).stats(6, 3, 3, 5, 8)))
                .build();
    }

    private Skill carrySkill(GameState state) {
        return (Skill) state.getGame().getFactory(FactoryType.Factory.SKILL).forName("I'll Carry You");
    }

    private void pickUpPartner(GameState state) {
        StepEngine.respond(state, Commands.useSkill(carrySkill(state), true, "carrier"));
        Player<?> partner = state.getGame().getPlayerById("partner");
        StepEngine.respond(state, new ClientCommandPlayerChoice(PlayerChoiceMode.ILL_CARRY_YOU, new Player<?>[]{partner}));
    }

    private FieldCoordinate position(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerCoordinate(state.getGame().getPlayerById(playerId));
    }

    private boolean standing(GameState state, String playerId) {
        return state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById(playerId)).isStanding();
    }

    @Test
    public void illCarryYouCarrierGrantsBreakTackleAndDodge() {
        GameState state = build(true, 8, 7);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        // DSL limitation: the doc's step list omits the skill-use + player-choice commands required to actually
        // pick up the partner; the engine only starts the ILL_CARRY_YOU step when the carrier activates the skill.
        pickUpPartner(state);

        assertNull(position(state, "partner"),
                "I'll Carry You carrier grants Break Tackle and Dodge - the partner is removed from the pitch while carried");

        // DSL limitation: the doc queues a single dodge(4), but away1 (8,7) covers BOTH (7,7) and (7,6) in its
        // tackle zone, so the engine consumes a dodge roll for each TZ exit ((7,7)->(7,6) and (7,6)->(7,5)).
        TestRolls.on(state).skill(4).skill(4);
        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 7), new FieldCoordinate(7, 6)));
        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 6), new FieldCoordinate(7, 5)));

        assertEquals(new FieldCoordinate(7, 5), position(state, "carrier"),
                "I'll Carry You carrier grants Break Tackle and Dodge - the carrier ends at (7,5)");
        assertTrue(standing(state, "carrier"),
                "I'll Carry You carrier grants Break Tackle and Dodge - the carrier is standing after dodging out of away1's tackle zone");
        assertEquals(new FieldCoordinate(8, 7), position(state, "away1"),
                "I'll Carry You carrier grants Break Tackle and Dodge - away1 is untouched at (8,7)");
        assertTrue(standing(state, "away1"),
                "I'll Carry You carrier grants Break Tackle and Dodge - away1 remains standing");
    }

    @Test
    public void illCarryYouCannotUseTwicePerHalf() {
        GameState state = build(true, 14, 1);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        pickUpPartner(state);
        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));
        // DSL limitation: the move ends with MA left, so the move action must be ended explicitly before the
        // PLACE_CARRIED_PLAYER step runs; the doc's step list omits this end-action command.
        StepEngine.respond(state, Commands.selectPlayer(null, null));
        // DSL limitation: the doc's step list omits the placement command, but the engine requires selecting an
        // adjacent square before the activation can end and the partner be placed back on the pitch.
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(10, 8)));

        assertFalse(state.getGame().getPlayerById("carrier").hasUnused(carrySkill(state)),
                "I'll Carry You cannot be used twice per half - the ONCE_PER_HALF skill is consumed after the first pickup");

        // DSL limitation: the harness cannot construct a turn transition, so the first activation's acting player
        // is manually re-activated to begin the second activation in the same half.
        state.getGame().getFieldModel().setPlayerState(state.getGame().getPlayerById("carrier"),
                new PlayerState(PlayerState.STANDING).changeActive(true));

        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        assertNotEquals(StepId.ILL_CARRY_YOU, state.getCurrentStep().getId(),
                "I'll Carry You cannot be used twice per half - the second activation produces no ILL_CARRY_YOU pickup step");

        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(10, 7), new FieldCoordinate(11, 7)));

        assertEquals(new FieldCoordinate(11, 7), position(state, "carrier"),
                "I'll Carry You cannot be used twice per half - the carrier moves without the partner on the second activation");
        assertEquals(new FieldCoordinate(10, 8), position(state, "partner"),
                "I'll Carry You cannot be used twice per half - the partner was placed at (10,8) at the end of the first activation and is not picked up again on the second activation");
    }

    @Test
    public void canCarryPartnerOnIllCarryYouStep() {
        GameState state = build(true, 14, 1);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        pickUpPartner(state);

        assertNull(position(state, "partner"),
                "Can carry partner on I'll Carry You step - the partner is immediately removed from the pitch after the pickup");

        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), position(state, "carrier"),
                "Can carry partner on I'll Carry You step - the carrier moves to (10,7) while carrying the partner");
    }

    @Test
    public void canCarryPartnerOnInitMoving() {
        GameState state = build(true, 14, 1);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        pickUpPartner(state);
        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertEquals(new FieldCoordinate(10, 7), position(state, "carrier"),
                "Can carry partner on init moving - the carrier reaches (10,7)");
        assertNull(position(state, "partner"),
                "Can carry partner on init moving - the partner stays off the pitch for the whole move");
    }

    @Test
    public void canCarryPartnerOnPlaceCarriedPlayer() {
        GameState state = build(true, 14, 1);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("carrier", PlayerAction.MOVE));
        pickUpPartner(state);
        StepEngine.respond(state, Commands.move("carrier", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));
        // DSL limitation: the move ends with MA left, so the move action must be ended explicitly before the
        // PLACE_CARRIED_PLAYER step runs; the doc's step list omits this end-action command.
        StepEngine.respond(state, Commands.selectPlayer(null, null));
        StepEngine.respond(state, new ClientCommandFieldCoordinate(new FieldCoordinate(10, 8)));

        assertEquals(new FieldCoordinate(10, 7), position(state, "carrier"),
                "Can carry partner on place carried player step - the carrier is at (10,7)");
        assertEquals(new FieldCoordinate(10, 8), position(state, "partner"),
                "Can carry partner on place carried player step - the partner is placed adjacent to the carrier at (10,8)");
        assertTrue(standing(state, "carrier"),
                "Can carry partner on place carried player step - the carrier is standing");
        assertTrue(standing(state, "partner"),
                "Can carry partner on place carried player step - the partner is standing after being placed");
    }
}
