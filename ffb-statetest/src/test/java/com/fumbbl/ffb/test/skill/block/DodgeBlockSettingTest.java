package com.fumbbl.ffb.test.skill.block;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DodgeBlockSettingTest {

	private static final int OPEN_FIELD_Y = 7;
	private static final int SIDELINE_Y = 1;
	private static final int WIDE_ZONE_Y = 4;

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void missingSettingAsksInOpenField() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void alwaysAsksInOpenField() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void dialogOffersSettingInline() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);

		stumble(state, "home1", "away1");

		DialogSkillUseParameter parameter = (DialogSkillUseParameter) state.getGame().getDialogParameter();
		assertEquals(CommonProperty.SETTING_DODGE_BLOCK, parameter.getMenuProperty());
		assertEquals(CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS, parameter.getDefaultValueKey());
	}

	@Test
	public void declinedDodgeMakesDefenderFall() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		stumble(state, "home1", "away1");

		IStep step = StepEngine.respond(state, useDodge(state, false));

		assertEquals(StepId.PUSHBACK, step.getId());
		assertEquals(PlayerState.FALLING, defenderState(state, "away1").getBase());
	}

	@Test
	public void acceptedDodgeKeepsDefenderStanding() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		stumble(state, "home1", "away1");

		IStep step = StepEngine.respond(state, useDodge(state, true));

		assertEquals(StepId.PUSHBACK, step.getId());
		assertEquals(PlayerState.STANDING, defenderState(state, "away1").getBase());
	}

	@Test
	public void neverUsesDodgeWithoutAskingEvenOnCriticalPush() {
		GameState state = buildState("BB2025", SIDELINE_Y, true, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER);

		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void situationalDoesNotAskInOpenField() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL);

		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void situationalAsksOnChainPush() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, true, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void situationalAsksNextToSideline() {
		GameState state = buildState("BB2025", SIDELINE_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void situationalDoesNotAskInInnerWideZoneRows() {
		GameState state = buildState("BB2025", WIDE_ZONE_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_SITUATIONAL);

		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void wideZoneAsksInInnerWideZoneRows() {
		GameState state = buildState("BB2025", WIDE_ZONE_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void wideZoneDoesNotAskInOpenField() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE);

		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void wideZoneAsksOnChainPush() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, true, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void settingOfAttackingCoachIsIgnored() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		state.setGameplaySetting(true, CommonProperty.SETTING_DODGE_BLOCK, CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER);

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void changedSettingIsUsedForLaterBlocks() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(15, 7).stats(6, 3, 3, 5, 8))
				.player("home2", p -> p.at(15, 10).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(16, 7).stats(6, 3, 3, 5, 8).skill("Dodge"))
				.player("away2", p -> p.at(16, 10).stats(6, 3, 3, 5, 8).skill("Dodge")))
			.build();
		TestRolls.on(state).block("stumble");

		assertDialogShown(state, stumble(state, "home1", "away1"));

		// the coach changes the setting inline in the dialog before answering it
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER);
		StepEngine.respond(state, useDodge(state, true));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(17, 7))));
		StepEngine.respond(state, Commands.followup(false));

		TestRolls.on(state).block("stumble");
		StepEngine.respond(state, Commands.selectPlayer("home2", PlayerAction.BLOCK));
		StepEngine.respond(state, Commands.block("home2", "away2"));
		IStep step = StepEngine.respond(state, Commands.blockChoice(0));

		assertNoDialogAndDodgeUsed(state, step, "away2");
	}

	@Test
	public void noDialogWithoutTackleZones() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, false);

		IStep step = stumble(state, "home1", "away1");

		assertFalse(state.getGame().getDialogParameter() instanceof DialogSkillUseParameter);
		assertEquals(StepId.PUSHBACK, step.getId());
		assertEquals(PlayerState.FALLING, defenderState(state, "away1").getBase());
	}

	@Test
	public void bb2020UsesDodgeInOpenFieldWithoutAsking() {
		GameState state = buildState("BB2020", OPEN_FIELD_Y, false, true);

		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void bb2020IgnoresSetting() {
		GameState state = buildState("BB2020", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_ALWAYS);

		assertNull(state.getGameplaySetting(false, CommonProperty.SETTING_DODGE_BLOCK));
		assertNoDialogAndDodgeUsed(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void gameplaySettingsSurviveSerialization() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE);

		GameState copy = new GameState(testServer.getServer());
		copy.initFrom(state.getGame().getRules(), state.toJsonValue());

		assertEquals(CommonPropertyValue.SETTING_DODGE_BLOCK_WIDE_ZONE,
			copy.getGameplaySetting(false, CommonProperty.SETTING_DODGE_BLOCK));
		assertNull(copy.getGameplaySetting(true, CommonProperty.SETTING_DODGE_BLOCK));
	}

	@Test
	public void userSettingsWithoutValueResetToAlways() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);
		setting(state, CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER);

		state.setGameplaySettings(false, Collections.emptyMap());

		assertDialogShown(state, stumble(state, "home1", "away1"));
	}

	@Test
	public void userSettingsUpdateOnlyGivenSide() {
		GameState state = buildState("BB2025", OPEN_FIELD_Y, false, true);

		state.setGameplaySettings(true,
			Collections.singletonMap(CommonProperty.SETTING_DODGE_BLOCK, CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER));

		assertEquals(CommonPropertyValue.SETTING_DODGE_BLOCK_NEVER,
			state.getGameplaySetting(true, CommonProperty.SETTING_DODGE_BLOCK));
		assertNull(state.getGameplaySetting(false, CommonProperty.SETTING_DODGE_BLOCK));
	}

	private GameState buildState(String rules, int y, boolean chainPush, boolean defenderHasTackleZones) {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule(rules)
			.withTeam(true, t -> t
				.player("home1", p -> p.at(15, y).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> {
				t.player("away1", p -> {
					p.at(16, y).stats(6, 3, 3, 5, 8).skill("Dodge");
					if (!defenderHasTackleZones) {
						p.state(new PlayerState(PlayerState.STANDING).changeConfused(true));
					}
				});
				if (chainPush) {
					// a chain push is only forced when every pushback square is occupied
					t.player("away2", p -> p.at(17, y - 1).stats(6, 3, 3, 5, 8))
						.player("away3", p -> p.at(17, y).stats(6, 3, 3, 5, 8))
						.player("away4", p -> p.at(17, y + 1).stats(6, 3, 3, 5, 8));
				}
			})
			.build();
		TestRolls.on(state).block("stumble");
		return state;
	}

	private void setting(GameState state, String value) {
		state.setGameplaySetting(false, CommonProperty.SETTING_DODGE_BLOCK, value);
	}

	private IStep stumble(GameState state, String attackerId, String defenderId) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer(attackerId, PlayerAction.BLOCK));
		StepEngine.respond(state, Commands.block(attackerId, defenderId));
		return StepEngine.respond(state, Commands.blockChoice(0));
	}

	private ClientCommandUseSkill useDodge(GameState state, boolean use) {
		Skill dodge = state.getGame().getRules().getSkillFactory().forName("Dodge");
		return new ClientCommandUseSkill(dodge, use, state.getGame().getDefenderId(), null, false);
	}

	private PlayerState defenderState(GameState state, String playerId) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(playerId));
	}

	private void assertDialogShown(GameState state, IStep step) {
		assertEquals(StepId.BLOCK_DODGE, step.getId());
		assertTrue(state.getGame().getDialogParameter() instanceof DialogSkillUseParameter,
			"Expected Dodge dialog, was " + state.getGame().getDialogParameter());
	}

	private void assertNoDialogAndDodgeUsed(GameState state, IStep step) {
		assertNoDialogAndDodgeUsed(state, step, "away1");
	}

	private void assertNoDialogAndDodgeUsed(GameState state, IStep step, String defenderId) {
		assertFalse(state.getGame().getDialogParameter() instanceof DialogSkillUseParameter, "Expected no Dodge dialog");
		assertEquals(StepId.PUSHBACK, step.getId());
		assertEquals(PlayerState.STANDING, defenderState(state, defenderId).getBase());
	}
}
