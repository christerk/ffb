package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LoneFoulerModificationTest {

	private static final String DEFENDER_ID = "defender";

	private GameState gameState;
	private Game game;
	private Player<?> attacker;
	private Player<?> defender;
	private StallingExtension stallingExtension;
	private MockedStatic<UtilPlayer> utilPlayer;
	private LoneFoulerModification modification;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		game = mock(Game.class);
		attacker = mock(Player.class);
		defender = mock(Player.class);
		stallingExtension = mock(StallingExtension.class);

		ActingPlayer actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).thenAnswer(invocation -> attacker);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.getPlayerById(DEFENDER_ID)).thenAnswer(invocation -> defender);
		when(gameState.getGame()).thenReturn(game);

		INamedObjectFactory<?> mechanicFactory = mock(INamedObjectFactory.class);
		doReturn(mock(SkillMechanic.class)).when(mechanicFactory).forName(Mechanic.Type.SKILL.name());
		doReturn(mechanicFactory).when(game).getFactory(FactoryType.Factory.MECHANIC);

		utilPlayer = mockStatic(UtilPlayer.class);
		givenAssists(0, 0);

		modification = new LoneFoulerModification(stallingExtension);
	}

	@AfterEach
	void tearDown() {
		utilPlayer.close();
	}

	private void givenAssists(int offensive, int defensive) {
		utilPlayer.when(() -> UtilPlayer.findOffensiveFoulAssists(any(), any(), any(), any())).thenReturn(offensive);
		utilPlayer.when(() -> UtilPlayer.findDefensiveFoulAssists(any(), any(), any())).thenReturn(defensive);
	}

	private ModificationParams params(boolean armourBroken, int... armourRoll) {
		ModifiedInjuryContext context = new ModifiedInjuryContext();
		context.fDefenderId = DEFENDER_ID;
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
	void notOfferedWithOffensiveAssist() {
		givenAssists(1, 0);

		assertFalse(modification.tryArmourRollModification(params(false, 2, 3)));
	}

	@Test
	void notOfferedWithDefensiveAssistEvenWhenArmourBreaksWithDouble() {
		givenAssists(0, 1);

		assertFalse(modification.tryArmourRollModification(params(true, 5, 5)));
	}

	@Test
	void notOfferedWithAssistsEvenWhenTeamMateWouldBeStalling() {
		givenAssists(1, 1);
		when(stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker)).thenReturn(true);

		assertFalse(modification.tryArmourRollModification(params(false, 2, 3)));
	}

	@Test
	void notOfferedWhenFoulBreaksArmourWithoutRoll() {
		when(game.isActive(NamedProperties.foulBreaksArmourWithoutRoll)).thenReturn(true);
		when(stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker)).thenReturn(true);

		assertFalse(modification.tryArmourRollModification(params(true)));
	}
}
