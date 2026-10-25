package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoneFoulerModificationTest {

	private static final String ATTACKER_ID = "attacker";

	private GameState gameState;
	private Game game;
	private Player<?> attacker;
	private StallingExtension stallingExtension;
	private LoneFoulerModification modification;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		game = mock(Game.class);
		attacker = mock(Player.class);
		stallingExtension = mock(StallingExtension.class);
		when(gameState.getGame()).thenReturn(game);
		when(game.getPlayerById(ATTACKER_ID)).thenAnswer(invocation -> attacker);
		modification = new LoneFoulerModification(stallingExtension);
	}

	private ModificationParams params(boolean armourBroken, int... armourRoll) {
		ModifiedInjuryContext context = new ModifiedInjuryContext();
		context.fAttackerId = ATTACKER_ID;
		context.setArmorBroken(armourBroken);
		if (armourRoll.length > 0) {
			context.setArmorRoll(armourRoll);
		}
		return new ModificationParams(gameState, context, new Foul());
	}

	@Test
	void offeredWhenArmourHolds() {
		assertTrue(modification.tryArmourRollModification(params(false, 2, 3)));
	}

	@Test
	void offeredWhenArmourBreaksWithDouble() {
		assertTrue(modification.tryArmourRollModification(params(true, 5, 5)));
	}

	@Test
	void notOfferedWhenArmourBreaksWithoutDouble() {
		assertFalse(modification.tryArmourRollModification(params(true, 5, 6)));
	}

	@Test
	void offeredWhenArmourBreaksWithoutDoubleButTeamMateWouldBeStalling() {
		when(stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker)).thenReturn(true);

		assertTrue(modification.tryArmourRollModification(params(true, 5, 6)));
	}

	@Test
	void notOfferedWhenFoulBreaksArmourWithoutRoll() {
		when(game.isActive(NamedProperties.foulBreaksArmourWithoutRoll)).thenReturn(true);
		when(stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker)).thenReturn(true);

		assertFalse(modification.tryArmourRollModification(params(true)));
	}

	@Test
	void reRollDropsModifiersOfFirstRoll() {
		DiceRoller diceRoller = mock(DiceRoller.class);
		when(gameState.getDiceRoller()).thenReturn(diceRoller);
		when(diceRoller.rollArmour()).thenReturn(new int[]{4, 6});
		ModificationParams params = params(false, 2, 3);
		params.getNewContext().addArmorModifier(mock(ArmorModifier.class));

		assertTrue(modification.modifyArmourInternal(params));

		assertArrayEquals(new int[]{4, 6}, params.getNewContext().getArmorRoll());
		assertTrue(params.getNewContext().fArmorModifiers.isEmpty());
	}

	@Test
	void reRollIgnoresFoulAssists() {
		assertTrue(modification.ignoresFoulAssists());
	}
}
