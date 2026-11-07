package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.menu.GameMenuBar;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.TurnData;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientCommandHandlerGameStateTest {

	@Test
	void promptsForMissingSettingsWhileWaitingForTheFirstSetup() {
		FantasyFootballClient client = mock(FantasyFootballClient.class, RETURNS_DEEP_STUBS);
		ClientData clientData = mock(ClientData.class);
		Game game = mock(Game.class);
		GameOptions options = mock(GameOptions.class);
		TurnData homeTurn = mock(TurnData.class);
		TurnData awayTurn = mock(TurnData.class);
		UserInterface userInterface = mock(UserInterface.class, RETURNS_DEEP_STUBS);
		GameMenuBar menuBar = mock(GameMenuBar.class);

		when(client.getMode()).thenReturn(ClientMode.PLAYER);
		when(client.getClientData()).thenReturn(clientData);
		when(client.getGame()).thenReturn(game);
		when(client.getUserInterface()).thenReturn(userInterface);
		when(clientData.isUserSettingsLoaded()).thenReturn(true);
		when(game.getOptions()).thenReturn(options);
		when(options.getRulesVersion()).thenReturn(RulesCollection.Rules.BB2025);
		when(game.getHalf()).thenReturn(1);
		when(game.getTurnMode()).thenReturn(TurnMode.SETUP);
		when(game.getTurnDataHome()).thenReturn(homeTurn);
		when(game.getTurnDataAway()).thenReturn(awayTurn);
		when(homeTurn.getTurnNr()).thenReturn(0);
		when(awayTurn.getTurnNr()).thenReturn(0);
		when(userInterface.getGameMenuBar()).thenReturn(menuBar);
		when(menuBar.menuEntries(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN)).thenReturn(Collections.emptyMap());
		when(menuBar.menuEntries(CommonProperty.SETTING_DODGE_BLOCK)).thenReturn(Collections.emptyMap());

		new ClientCommandHandlerGameState(client).maybeShowSkillSettingsDialog(userInterface);

		verify(clientData).setSkillSettingsPromptShown(true);
		verify(menuBar).showSkillSettingsDialog(true);
	}
}
