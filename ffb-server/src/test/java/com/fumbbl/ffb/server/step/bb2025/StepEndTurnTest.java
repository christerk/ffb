package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StepEndTurnTest {

	@Test
	void onlyPlayersStillEligibleForBanAreOfferedForSecretWeaponHandling() throws Exception {
		Game game = Mockito.mock(Game.class);
		GameResult gameResult = Mockito.mock(GameResult.class);
		FieldModel fieldModel = Mockito.mock(FieldModel.class);
		Team team = Mockito.mock(Team.class);
		Player<?> knockedOut = secretWeaponCandidate("knockedOut", game, gameResult, fieldModel, PlayerState.KNOCKED_OUT);
		Player<?> reserve = secretWeaponCandidate("reserve", game, gameResult, fieldModel, PlayerState.RESERVE);
		Player<?> badlyHurt = secretWeaponCandidate("badlyHurt", game, gameResult, fieldModel, PlayerState.BADLY_HURT);
		Player<?> banned = secretWeaponCandidate("banned", game, gameResult, fieldModel, PlayerState.BANNED);
		Player<?> notUsed = player("notUsed", game, gameResult, fieldModel, PlayerState.KNOCKED_OUT, false);

		Mockito.when(game.getGameResult()).thenReturn(gameResult);
		Mockito.when(game.getFieldModel()).thenReturn(fieldModel);
		Mockito.when(team.getPlayers()).thenReturn(new Player<?>[]{knockedOut, reserve, badlyHurt, banned, notUsed});

		List<String> playerIds = getPlayerIds(team, game);

		Assertions.assertEquals(2, playerIds.size());
		Assertions.assertTrue(playerIds.contains(knockedOut.getId()));
		Assertions.assertTrue(playerIds.contains(reserve.getId()));
		Assertions.assertFalse(playerIds.contains(badlyHurt.getId()));
		Assertions.assertFalse(playerIds.contains(banned.getId()));
		Assertions.assertFalse(playerIds.contains(notUsed.getId()));
	}

	private Player<?> secretWeaponCandidate(String playerId, Game game, GameResult gameResult, FieldModel fieldModel,
	                                        int playerState) {
		return player(playerId, game, gameResult, fieldModel, playerState, true);
	}

	@SuppressWarnings("unchecked")
	private Player<?> player(String playerId, Game game, GameResult gameResult, FieldModel fieldModel, int playerState,
	                         boolean hasUsedSecretWeapon) {
		Player<?> player = Mockito.mock(Player.class);
		PlayerResult playerResult = Mockito.mock(PlayerResult.class);
		Mockito.when(player.getId()).thenReturn(playerId);
		Mockito.when(player.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.<Skill>emptySet());
		Mockito.when(gameResult.getPlayerResult(player)).thenReturn(playerResult);
		Mockito.when(fieldModel.getPlayerState(player)).thenReturn(new PlayerState(playerState));
		Mockito.when(playerResult.hasUsedSecretWeapon()).thenReturn(hasUsedSecretWeapon);
		return player;
	}

	@SuppressWarnings("unchecked")
	private List<String> getPlayerIds(Team team, Game game) throws Exception {
		Method getPlayerIds = StepEndTurn.class.getDeclaredMethod("getPlayerIds", Team.class, Game.class);
		getPlayerIds.setAccessible(true);
		return (List<String>) getPlayerIds.invoke(new StepEndTurn(Mockito.mock(GameState.class)), team, game);
	}

}
