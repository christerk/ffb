package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandThrowTeamMate;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.AbstractStateTest;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LethalFlightTest extends AbstractStateTest {

    private GameState ttmState(String skills, int awayX, int awayY) {
        GameStateBuilder builder = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025");
        builder.withTeam(true, t -> {
            t.player("thrower", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Throw Team-Mate"));
            t.player("flinger", p -> {
                p.at(7, 8).stats(6, 2, 3, 5, 6).skill("Right Stuff");
                for (String skill : skills.split(",")) {
                    if (!skill.isEmpty()) {
                        p.skill(skill.trim());
                    }
                }
            });
        });
        builder.withTeam(false, t -> t.player("away1", p -> p.at(awayX, awayY).stats(6, 3, 3, 5, 8)));
        return builder.build();
    }

    private void throwFlinger(GameState state, int armourD1, int armourD2, int injuryD1, int injuryD2) {
        TestRolls.on(state).skill(6)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .armour(armourD1, armourD2).injury(injuryD1, injuryD2)
                .scatterDirection(1).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(11, 8)));
    }

    @Test
    void lethalFlightModifiesTtmInjury() {
        GameState state = ttmState("Lethal Flight", 13, 6);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        throwFlinger(state, 4, 3, 3, 2);

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "The thrown player with Lethal Flight lands on away1 at (13,6): armour 4+3=7 + 1(Lethal Flight) = 8 breaks AV8 and injury 3+2=5 stuns the defender");
    }

    @Test
    void lethalFlightArmorModifierOnTtmScatterHit() {
        GameState state = ttmState("Lethal Flight", 13, 6);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        throwFlinger(state, 4, 3, 3, 2);

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Lethal Flight armour modifier applied on the TTM scatter hit: 4+3=7 would hold vs AV8 but +1 breaks it to 8, injury 3+2=5 stuns");
    }

    @Test
    void lethalFlightInjuryModifierOnTtmScatterHit() {
        GameState state = ttmState("Lethal Flight", 13, 6);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        throwFlinger(state, 6, 4, 5, 2);

        assertEquals(PlayerState.KNOCKED_OUT, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Lethal Flight injury modifier applied independently of armour: armour 6+4=10 breaks naturally, injury 5+2=7 + 1 = 8 knocks the defender out");
    }

    @Test
    void lethalFlightMutualExclusivityArmourAndInjury() {
        GameState state = ttmState("Lethal Flight", 13, 6);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        throwFlinger(state, 4, 3, 3, 2);

        assertEquals(PlayerState.STUNNED, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "affectsEitherArmourOrInjuryOnTtm: the +1 was consumed by the armour roll (4+3=7 -> 8 breaks), so the injury roll 3+2=5 stays Stunned and does NOT receive +1 (which would have made it 6 = KO)");
    }

    @Test
    void sppGrantedWhenTtmPlayerHitsOpponent() {
        GameState state = ttmState("Lethal Flight", 13, 6);
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_TEAM_MATE_MOVE));
        TestRolls.on(state).skill(6)
                .scatterDirection(1).scatterDirection(2).scatterDirection(3)
                .armour(6, 6).injury(6, 6)
                .general("casualty d6", 1).general("casualty d8", 1)
                .scatterDirection(1).armour(6, 6).injury(3, 2);
        StepEngine.respond(state, Commands.throwTeammate("thrower", "flinger"));
        StepEngine.respond(state, new ClientCommandThrowTeamMate("thrower", new FieldCoordinate(11, 8)));

        assertEquals(1, state.getGame().getGameResult().getPlayerResult(
                state.getGame().getPlayerById("flinger")).getCasualties(),
                "grantsSppWhenHittingOpponentOnTtm: the thrown Lethal Flight player causes a casualty on the TTM landing hit (armour 12 breaks, injury 12 casualty) and is credited with the casualty SPP");
        assertEquals(PlayerState.BADLY_HURT, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "The defender hit by the TTM landing is badly hurt by the casualty");
    }

    @Test
    void lethalFlightDoesNotTriggerOnRegularBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Lethal Flight")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pow").armour(5, 2);
        StepEngine.respond(state, Commands.block("home1", "away1"));
        StepEngine.respond(state, Commands.blockChoice(0));
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertEquals(PlayerState.PRONE, state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).getBase(),
                "Lethal Flight modifiers only apply in a TTM context: on a regular block armour 5+2=7 holds vs AV8 and the defender is merely knocked prone (no +1 applied)");
    }

    @Test
    void lethalFlightCannotBeAssignedWithoutRightStuff() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        SkillFactory skillFactory = state.getGame().getFactory(FactoryType.Factory.SKILL);
        Skill lethalFlight = skillFactory.forName("Lethal Flight");
        assertNotNull(lethalFlight, "Lethal Flight skill should be resolvable from the SkillFactory");

        Player<?> home1 = state.getGame().getPlayerById("home1");
        assertFalse(lethalFlight.canBeAssignedTo(home1),
                "Lethal Flight requires canBeThrown (Right Stuff) on the assigned player - a plain player cannot receive it");

        Player<?> flinger = ttmState("", 13, 6).getGame().getPlayerById("flinger");
        assertTrue(lethalFlight.canBeAssignedTo(flinger),
                "A player with Right Stuff (canBeThrown) can be assigned Lethal Flight");
    }
}
