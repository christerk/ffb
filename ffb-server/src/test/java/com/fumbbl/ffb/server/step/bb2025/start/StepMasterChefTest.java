package com.fumbbl.ffb.server.step.bb2025.start;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepMasterChefTest {

	@Test
	void startProcessesKickoffOnlyOncePerHalf() {
		GameState gameState = mock(GameState.class);
		Game game = mock(Game.class);
		Team home = mock(Team.class);
		Team away = mock(Team.class);
		FieldModel fieldModel = mock(FieldModel.class);
		@SuppressWarnings("unchecked")
		Player<?> leader = mock(Player.class);

		when(gameState.getGame()).thenReturn(game);
		when(game.getHalf()).thenReturn(1);
		when(gameState.markKickoffHalfProcessed(1)).thenReturn(true, false);
		when(game.getTeamHome()).thenReturn(home);
		when(game.getTeamAway()).thenReturn(away);
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(home.getPlayers()).thenReturn(new Player[]{leader});
		when(away.getPlayers()).thenReturn(new Player[0]);
		when(leader.hasSkillProperty(NamedProperties.grantsTeamReRollWhenOnPitch)).thenReturn(true);
		when(fieldModel.getPlayerCoordinate(leader)).thenReturn(new FieldCoordinate(1, 1));

		try (MockedStatic<UtilServerGame> utilServerGame = mockStatic(UtilServerGame.class)) {
			StepMasterChef step = new StepMasterChef(gameState);

			step.start();
			step.start();

			utilServerGame.verify(() -> UtilServerGame.handleChefRolls(step, game), times(1));
			verify(gameState, times(1)).addLeader(leader);
			verify(gameState, times(2)).markKickoffHalfProcessed(1);
		}
	}

	@Test
	void startSkipsProcessedHalf() {
		GameState gameState = mock(GameState.class);
		Game game = mock(Game.class);

		when(gameState.getGame()).thenReturn(game);
		when(game.getHalf()).thenReturn(1);
		when(gameState.markKickoffHalfProcessed(1)).thenReturn(false);

		try (MockedStatic<UtilServerGame> utilServerGame = mockStatic(UtilServerGame.class)) {
			new StepMasterChef(gameState).start();

			utilServerGame.verifyNoInteractions();
			verify(gameState, never()).addLeader(any());
		}
	}
}
