package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.util.UtilCards;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Ensures the legacy {@link UtilServerReRoll} overloads and the new {@link ReRollRequest} / {@link ReRollService}
 * api pass identical parameters to the {@link RollMechanic}.
 */
class ReRollApiEquivalenceTest {

	private static final ReRolledAction ACTION = ReRolledActions.DODGE;
	private static final int MINIMUM_ROLL = 4;

	private static class Fixture {
		private final GameState gameState = mock(GameState.class);
		private final Game game = mock(Game.class);
		private final RollMechanic mechanic = mock(RollMechanic.class);
		private final ReRollService reRollService = new ReRollService();
		private final ActingPlayer actingPlayer = mock(ActingPlayer.class);
		private final Player<?> actingPlayerPlayer = mock(Player.class);
		private final Player<?> otherPlayer = mock(Player.class);
		private final Skill modifyingSkill = mock(Skill.class);
		private final Skill explicitReRollSkill = mock(Skill.class);
		private final Skill resolvedReRollSkill = mock(Skill.class);
		private final Set<Skill> ignoreSkills = Collections.singleton(mock(Skill.class));
		private final List<String> messages = new ArrayList<>(Arrays.asList("one", "two"));

		private Fixture() {
			MechanicsFactory factory = mock(MechanicsFactory.class);
			when(gameState.getGame()).thenReturn(game);
			when(gameState.getReRollService()).thenReturn(reRollService);
			when(game.getFactory(FactoryType.Factory.MECHANIC)).thenReturn(factory);
			when(factory.forName(Mechanic.Type.ROLL.name())).thenReturn(mechanic);
			when(game.getActingPlayer()).thenReturn(actingPlayer);
			Mockito.<Player<?>>when(actingPlayer.getPlayer()).thenReturn(actingPlayerPlayer);
		}
	}

	private List<Object> capture(Function<Fixture, Boolean> call) {
		Fixture fixture = new Fixture();
		ReRollSource reRollSource = mock(ReRollSource.class);
		when(reRollSource.getSkill(fixture.game)).thenReturn(fixture.resolvedReRollSkill);

		try (MockedStatic<UtilCards> utilCards = Mockito.mockStatic(UtilCards.class)) {
			utilCards.when(() -> UtilCards.getUnusedRerollSource(any(), any(), any())).thenReturn(reRollSource);
			call.apply(fixture);
		}

		ArgumentCaptor<Player> player = ArgumentCaptor.forClass(Player.class);
		ArgumentCaptor<ReRolledAction> action = ArgumentCaptor.forClass(ReRolledAction.class);
		ArgumentCaptor<Integer> minimumRoll = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Boolean> fumble = ArgumentCaptor.forClass(Boolean.class);
		ArgumentCaptor<Skill> modificationSkill = ArgumentCaptor.forClass(Skill.class);
		ArgumentCaptor<Skill> reRollSkill = ArgumentCaptor.forClass(Skill.class);
		ArgumentCaptor<CommonProperty> menuProperty = ArgumentCaptor.forClass(CommonProperty.class);
		ArgumentCaptor<String> defaultValueKey = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<List> messages = ArgumentCaptor.forClass(List.class);

		verify(fixture.mechanic).askForReRollIfAvailable(eq(fixture.gameState), player.capture(), action.capture(),
			minimumRoll.capture(), fumble.capture(), modificationSkill.capture(), reRollSkill.capture(),
			menuProperty.capture(), defaultValueKey.capture(), messages.capture());

		return Arrays.asList(
			describe(fixture, player.getValue()), action.getValue(), minimumRoll.getValue(), fumble.getValue(),
			describe(fixture, modificationSkill.getValue()), describe(fixture, reRollSkill.getValue()),
			menuProperty.getValue(), defaultValueKey.getValue(), messages.getValue());
	}

	private String describe(Fixture fixture, Object value) {
		if (value == null) {
			return "null";
		}
		if (value == fixture.actingPlayerPlayer) {
			return "actingPlayerPlayer";
		}
		if (value == fixture.otherPlayer) {
			return "otherPlayer";
		}
		if (value == fixture.modifyingSkill) {
			return "modifyingSkill";
		}
		if (value == fixture.explicitReRollSkill) {
			return "explicitReRollSkill";
		}
		if (value == fixture.resolvedReRollSkill) {
			return "resolvedReRollSkill";
		}
		return "unexpected";
	}

	private void assertSameDelegation(Function<Fixture, Boolean> legacyCall, Function<Fixture, Boolean> newCall) {
		assertEquals(capture(legacyCall).toString(), capture(newCall).toString());
	}

	@Test
	void actingPlayerWithoutModifyingSkill() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL, true),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forActingPlayer(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL).fumble().build()));
	}

	@Test
	void actingPlayerWithModifyingSkill() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL, false,
				f.modifyingSkill),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forActingPlayer(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL)
					.modifyingSkill(f.modifyingSkill).build()));
	}

	@Test
	void actingPlayerWithIgnoredSkills() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL, true,
				f.modifyingSkill, f.ignoreSkills),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forActingPlayer(f.gameState, f.actingPlayer, ACTION, MINIMUM_ROLL).fumble()
					.modifyingSkill(f.modifyingSkill).ignoreSkills(f.ignoreSkills).build()));
	}

	@Test
	void actingPlayerPassesIgnoredSkillsToLookup() {
		Fixture fixture = new Fixture();
		try (MockedStatic<UtilCards> utilCards = Mockito.mockStatic(UtilCards.class)) {
			utilCards.when(() -> UtilCards.getUnusedRerollSource(any(), any(), any())).thenReturn(null);
			new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forActingPlayer(fixture.gameState, fixture.actingPlayer, ACTION, MINIMUM_ROLL)
					.ignoreSkills(fixture.ignoreSkills).build());
			utilCards.verify(() -> UtilCards.getUnusedRerollSource(eq(fixture.actingPlayer), eq(ACTION),
				eq(fixture.ignoreSkills)));
		}
	}

	@Test
	void playerWithModificationSkillResolvesReRollSkill() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, true,
				f.modifyingSkill),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).fumble()
					.modifyingSkill(f.modifyingSkill).build()));
	}

	@Test
	void playerWithExplicitReRollSkill() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, false,
				f.modifyingSkill, f.explicitReRollSkill),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL)
					.modifyingSkill(f.modifyingSkill).reRollSkill(f.explicitReRollSkill).build()));
	}

	@Test
	void playerWithMenuProperty() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, true,
				f.modifyingSkill, f.explicitReRollSkill, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, "key"),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).fumble()
					.modifyingSkill(f.modifyingSkill).reRollSkill(f.explicitReRollSkill)
					.menu(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, "key").build()));
	}

	@Test
	void playerWithAllParameters() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, true,
				f.modifyingSkill, f.explicitReRollSkill, CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, "key",
				f.messages),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).fumble(true)
					.modifyingSkill(f.modifyingSkill).reRollSkill(f.explicitReRollSkill)
					.menu(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN, "key").messages(f.messages).build()));
	}

	@Test
	void playerWithoutSkills() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, true),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).fumble().build()));
	}

	@Test
	void playerWithMessagesOnly() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, f.messages),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).messages(f.messages).build()));
	}

	@Test
	void playerWithFumbleAndMessages() {
		assertSameDelegation(
			f -> UtilServerReRoll.askForReRollIfAvailable(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL, true,
				f.messages),
			f -> new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forPlayer(f.gameState, f.otherPlayer, ACTION, MINIMUM_ROLL).fumble()
					.messages(f.messages).build()));
	}

	@Test
	void resolvedReRollSkillIsUsedWhenAvailable() {
		Fixture fixture = new Fixture();
		ReRollSource reRollSource = mock(ReRollSource.class);
		when(reRollSource.getSkill(fixture.game)).thenReturn(fixture.resolvedReRollSkill);
		try (MockedStatic<UtilCards> utilCards = Mockito.mockStatic(UtilCards.class)) {
			utilCards.when(() -> UtilCards.getUnusedRerollSource(any(), any(), any())).thenReturn(reRollSource);
			new ReRollService().askForReRollIfAvailable(
				ReRollRequest.forActingPlayer(fixture.gameState, fixture.actingPlayer, ACTION, MINIMUM_ROLL).build());
		}
		verify(fixture.mechanic).askForReRollIfAvailable(eq(fixture.gameState), eq(fixture.actingPlayerPlayer),
			eq(ACTION), anyInt(), anyBoolean(), eq(null), eq(fixture.resolvedReRollSkill), eq(null), eq(null), eq(null));
	}
}
