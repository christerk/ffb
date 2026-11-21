package com.fumbbl.ffb.server.step.bb2025.shared;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.bb2025.SquigRage;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * A ball carrier with Squig Rage is only exempt from stalling while they have to roll for it at activation.
 */
class StallingExtensionSquigRageTest {
	private static final PlayerState STANDING = new PlayerState(PlayerState.STANDING).changeActive(true);

	private final StallingExtension stallingExtension = new StallingExtension();
	private Game game;
	private Player<?> squig;
	private Player<?> restrainer;
	private MockedStatic<UtilPlayer> utilPlayer;

	@BeforeEach
	void setUp() {
		SquigRage squigRage = new SquigRage();
		squigRage.postConstruct();
		game = mock(Game.class);
		FieldModel fieldModel = mock(FieldModel.class);
		Team team = mock(Team.class);
		squig = mock(Player.class);
		restrainer = mock(Player.class);

		when(game.getFieldModel()).thenReturn(fieldModel);
		when(game.getOtherTeam(team)).thenReturn(mock(Team.class));
		when(squig.getTeam()).thenReturn(team);
		when(squig.getSkillWithProperty(NamedProperties.requiresRageRollBeforeDeclaringAction)).thenReturn(squigRage);
		when(squig.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.<Skill>singleton(squigRage));
		when(restrainer.getTeam()).thenReturn(team);
		when(team.getPlayers()).thenReturn(new Player<?>[]{squig, restrainer});
		when(fieldModel.getPlayerCoordinate(squig)).thenReturn(new FieldCoordinate(10, 7));
		when(fieldModel.getPlayerCoordinate(restrainer)).thenReturn(new FieldCoordinate(11, 7));
		when(fieldModel.getPlayerState(restrainer)).thenReturn(STANDING);

		utilPlayer = mockStatic(UtilPlayer.class);
		utilPlayer.when(() -> UtilPlayer.hasBall(game, squig)).thenReturn(true);
		// a marking opponent ends the check right after the exemption, so no path finding is needed
		utilPlayer.when(() -> UtilPlayer.findAdjacentPlayersWithTacklezones(any(), any(), any(), anyBoolean()))
			.thenReturn(new Player<?>[]{mock(Player.class)});
	}

	@AfterEach
	void tearDown() {
		utilPlayer.close();
	}

	@Test
	void ballCarrierRollingForSquigRageIsExempt() {
		assertFalse(stallingExtension.isConsideredStalling(game, squig));

		utilPlayer.verify(() -> UtilPlayer.findAdjacentPlayersWithTacklezones(any(), any(), any(), anyBoolean()), never());
	}

	@Test
	void restrainedBallCarrierIsNotExempt() {
		when(restrainer.hasSkillProperty(NamedProperties.preventsRageRollOfNearbyTeamMates)).thenReturn(true);

		stallingExtension.isConsideredStalling(game, squig);

		utilPlayer.verify(() -> UtilPlayer.findAdjacentPlayersWithTacklezones(any(), any(), any(), anyBoolean()));
	}
}
