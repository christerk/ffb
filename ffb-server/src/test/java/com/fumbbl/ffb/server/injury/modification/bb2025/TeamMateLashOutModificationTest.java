package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.OldProModificationParams;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
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

/**
 * Team-mates hurt by Squig Rage are treated like the victims of Animal Savagery.
 */
class TeamMateLashOutModificationTest {

	private GameState gameState;
	private Game game;
	private Team team;
	private Player<?> defender;
	private MockedStatic<UtilServerReRoll> utilServerReRoll;

	@BeforeEach
	void setUp() {
		gameState = mock(GameState.class);
		game = mock(Game.class);
		team = mock(Team.class);
		Player<?> attacker = mock(Player.class);
		defender = mock(Player.class);
		FieldModel fieldModel = mock(FieldModel.class);
		ActingPlayer actingPlayer = mock(ActingPlayer.class);

		when(gameState.getGame()).thenReturn(game);
		when(game.getPlayerById("attacker")).thenAnswer(invocation -> attacker);
		when(game.getPlayerById("defender")).thenAnswer(invocation -> defender);
		when(attacker.getTeam()).thenReturn(team);
		when(defender.getTeam()).thenReturn(team);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		doReturn(attacker).when(actingPlayer).getPlayer();
		when(game.getFieldModel()).thenReturn(fieldModel);
		when(fieldModel.getPlayerState(attacker)).thenReturn(new PlayerState(PlayerState.STANDING).changeActive(true));

		utilServerReRoll = mockStatic(UtilServerReRoll.class);
		utilServerReRoll.when(() -> UtilServerReRoll.isProReRollAvailable(any(), any(), any())).thenReturn(true);
	}

	@AfterEach
	void tearDown() {
		utilServerReRoll.close();
	}

	@Test
	void oldProDoesNotTreatLashOutAtTeamMateAsSelfInflicted() {
		assertFalse(oldProSelfInflicted(ApothecaryMode.ANIMAL_SAVAGERY));
		assertFalse(oldProSelfInflicted(ApothecaryMode.SQUIG_RAGE));
	}

	@Test
	void oldProTreatsOtherTeamMateInjuriesAsSelfInflicted() {
		assertTrue(oldProSelfInflicted(ApothecaryMode.DEFENDER));
		assertTrue(oldProSelfInflicted(ApothecaryMode.ATTACKER));
	}

	@Test
	void savageMaulingTreatsLashOutCasualtyLikeOpponentCasualty() {
		assertFalse(savageMaulingTried(ApothecaryMode.ANIMAL_SAVAGERY));
		assertFalse(savageMaulingTried(ApothecaryMode.SQUIG_RAGE));
	}

	@Test
	void savageMaulingIsTriedForOtherTeamMateCasualties() {
		assertTrue(savageMaulingTried(ApothecaryMode.DEFENDER));
		assertTrue(savageMaulingTried(ApothecaryMode.ATTACKER));
	}

	@Test
	void opponentCasualtyIsNotReRolled() {
		when(defender.getTeam()).thenReturn(mock(Team.class));

		assertFalse(savageMaulingTried(ApothecaryMode.DEFENDER));
	}

	private boolean oldProSelfInflicted(ApothecaryMode mode) {
		ModifiedInjuryContext context = context(mode);
		context.fArmorRoll = new int[]{3, 4};
		OldProModificationParams params = new OldProModificationParams(gameState, context, new Block());

		new OldProModification().tryArmourRollModification(params);

		return params.isSelfInflicted();
	}

	private boolean savageMaulingTried(ApothecaryMode mode) {
		return new SavageMaulingModification().tryInjuryModification(game, context(mode), new Block());
	}

	private ModifiedInjuryContext context(ApothecaryMode mode) {
		ModifiedInjuryContext context = new ModifiedInjuryContext();
		context.setAttackerId("attacker");
		context.setDefenderId("defender");
		context.setApothecaryMode(mode);
		context.fInjury = new PlayerState(PlayerState.BADLY_HURT);
		return context;
	}
}
