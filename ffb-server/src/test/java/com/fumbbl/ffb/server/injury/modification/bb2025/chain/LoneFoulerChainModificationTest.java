package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
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

class LoneFoulerChainModificationTest {

	private ChainModificationFixture fixture;
	private StallingExtension stallingExtension;
	private MockedStatic<UtilPlayer> utilPlayer;
	private LoneFoulerChainModification modification;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		stallingExtension = mock(StallingExtension.class);

		INamedObjectFactory<?> mechanicFactory = mock(INamedObjectFactory.class);
		doReturn(mock(SkillMechanic.class)).when(mechanicFactory).forName(Mechanic.Type.SKILL.name());
		doReturn(mechanicFactory).when(fixture.game).getFactory(FactoryType.Factory.MECHANIC);

		utilPlayer = mockStatic(UtilPlayer.class);
		givenAssists(0, 0);

		modification = new LoneFoulerChainModification(stallingExtension, fixture.evaluationService, fixture.copyService);
		modification.setSkill(fixture.skill("Lone Fouler", 0, 0));
	}

	@AfterEach
	void tearDown() {
		utilPlayer.close();
	}

	private void givenAssists(int offensive, int defensive) {
		utilPlayer.when(() -> UtilPlayer.findOffensiveFoulAssists(any(), any(), any(), any())).thenReturn(offensive);
		utilPlayer.when(() -> UtilPlayer.findDefensiveFoulAssists(any(), any(), any())).thenReturn(defensive);
	}

	@Test
	void offeredWhenArmourHolds() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 2, 3)));
	}

	@Test
	void notOfferedForOtherInjuryTypes() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void offeredWhenArmourBreaksWithDouble() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 5)));
	}

	@Test
	void notOfferedWhenArmourBreaksWithoutDouble() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 6)));
	}

	@Test
	void offeredWhenArmourBreaksWithoutDoubleButTeamMateWouldBeStalling() {
		when(stallingExtension.wouldEndOfTurnTriggerStallingRoll(fixture.game, fixture.attacker)).thenReturn(true);

		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 6)));
	}

	@Test
	void notOfferedWithOffensiveAssist() {
		givenAssists(1, 0);

		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 2, 3)));
	}

	@Test
	void notOfferedWithDefensiveAssistEvenWhenArmourBreaksWithDouble() {
		givenAssists(0, 1);

		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 5)));
	}
}
