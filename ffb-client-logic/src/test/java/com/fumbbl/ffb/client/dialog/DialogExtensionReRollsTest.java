package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.TurnData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DialogExtensionReRollsTest {

	private final DialogExtensionReRolls extension = new DialogExtensionReRolls();
	private Game game;
	private TurnData turnData;

	@BeforeEach
	void setUp() {
		game = mock(Game.class);
		turnData = mock(TurnData.class);
		when(game.getTurnDataHome()).thenReturn(turnData);
	}

	@Test
	void driveBonusLabelNamesBrilliantCoachingFirst() {
		when(turnData.getReRollsBrilliantCoachingOneDrive()).thenReturn(1);
		when(turnData.getReRollsPumpUpTheCrowdOneDrive()).thenReturn(1);
		when(turnData.getReRollShowStarOneDrive()).thenReturn(1);

		assertEquals("Brilliant Coaching", extension.label(ReRollSources.DRIVE_BONUS_RE_ROLL, game));
	}

	@Test
	void driveBonusLabelNamesPumpUpTheCrowd() {
		when(turnData.getReRollsPumpUpTheCrowdOneDrive()).thenReturn(1);
		when(turnData.getReRollShowStarOneDrive()).thenReturn(1);

		assertEquals("Pump up the Crowd", extension.label(ReRollSources.DRIVE_BONUS_RE_ROLL, game));
	}

	@Test
	void driveBonusLabelNamesStarOfTheShow() {
		when(turnData.getReRollShowStarOneDrive()).thenReturn(1);

		assertEquals("Star of the Show", extension.label(ReRollSources.DRIVE_BONUS_RE_ROLL, game));
	}

	@Test
	void driveBonusLabelFallsBackToGenericNameIfNoneIsAvailable() {
		assertEquals("Drive Bonus Re-Roll", extension.label(ReRollSources.DRIVE_BONUS_RE_ROLL, game));
	}
}
