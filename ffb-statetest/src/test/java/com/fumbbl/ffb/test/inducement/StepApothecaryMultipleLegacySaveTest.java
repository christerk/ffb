package com.fumbbl.ffb.test.inducement;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandKeywordSelection;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.mutliblock.StepApothecaryMultiple;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StepApothecaryMultiple saved before its injury lists were stored as indexes into the main injury list must keep
 * every pending injury when it is loaded, saved again and reloaded.
 */
public class StepApothecaryMultipleLegacySaveTest {

	private GameState state;

	@BeforeEach
	public void setUp() throws Exception {
		TestServer testServer = new TestServer();
		state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
				.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8).skill("Regeneration")))
			.build();
		state.getGame().setHomePlaying(true);
	}

	@Test
	public void pendingPastriesDialogSurvivesALegacySaveAndStillResolves() {
		addInducement(false, "bretonnianPastries");
		StepApothecaryMultiple step = defenderStep(injury("away1", PlayerState.KNOCKED_OUT),
			injury("away2", PlayerState.KNOCKED_OUT));
		step.start();
		assertEquals("away1", ((DialogUseInducementParameter) dialog()).getPlayerId());

		StepApothecaryMultiple restored = legacyRoundTrip(step, new int[]{0, 1}, new int[0], new int[0]);

		TestRolls.on(state).general(3);
		restored.handleCommand(new ReceivedCommand(
			new ClientCommandUseInducement(inducement("bretonnianPastries"), new String[]{"away1"}), null));

		assertEquals(PlayerState.RESERVE, playerState("away1"));
		assertEquals(FieldCoordinate.RSV_AWAY_X, coordinate("away1").getX());
		assertEquals(PlayerState.KNOCKED_OUT, playerState("away2"));
	}

	@Test
	public void pendingRegenerationDialogSurvivesALegacySaveAndStillResolves() {
		addInducement(false, "mortuaryAssistant");
		StepApothecaryMultiple step = defenderStep(injury("away2", PlayerState.BADLY_HURT));
		TestRolls.on(state).general(1);
		step.start();
		assertEquals(DialogId.USE_INDUCEMENT, dialog().getId());
		assertEquals("away2", ((DialogUseInducementParameter) dialog()).getPlayerId());

		StepApothecaryMultiple restored = legacyRoundTrip(step, new int[]{0}, new int[0], new int[0]);

		TestRolls.on(state).general(6);
		restored.handleCommand(new ReceivedCommand(
			new ClientCommandUseInducement(inducement("mortuaryAssistant"), new String[]{"away2"}), null));

		// a stale copy in the main list would apply the player as Badly Hurt
		assertEquals(PlayerState.RESERVE, playerState("away2"));
		assertEquals(FieldCoordinate.RSV_AWAY_X, coordinate("away2").getX());
	}

	@Test
	public void pendingGettingEvenSelectionSurvivesALegacySaveAndStillResolves() {
		StepApothecaryMultiple step = attackerStep(injury("home1", PlayerState.SERIOUS_INJURY));
		JsonObject legacy = step.toJsonValue();
		JsonArray injuries = legacy.get(IServerJsonOption.INJURY_RESULTS.getKey()).asArray();
		legacy.set(IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.getKey(), copy(injuries));
		legacy.set(IServerJsonOption.INJURY_RESULTS_GETTING_EVEN.getKey(), copy(injuries));
		legacy.set(IServerJsonOption.AVAILABLE_KEYWORDS_MAP.getKey(),
			new JsonObject().add("0", new JsonArray().add(Keyword.LINEMAN.getName()).add(Keyword.BLOCKER.getName())));
		stripIndexes(legacy);

		StepApothecaryMultiple restored = reloadTwice(legacy, new int[]{0}, new int[0], new int[]{0});

		StepCommandStatus status = restored.handleCommand(new ReceivedCommand(
			new ClientCommandKeywordSelection("home1", Collections.singletonList(Keyword.LINEMAN)), null));

		assertEquals(StepCommandStatus.SKIP_STEP, status);
		assertEquals(StepId.GETTING_EVEN, state.getStepStack().peek().getId());
	}

	@Test
	public void pendingRaiseDeadEntrySurvivesALegacySave() {
		StepApothecaryMultiple step = attackerStep(injury("home1", PlayerState.RIP));
		JsonObject legacy = step.toJsonValue();
		JsonArray injuries = legacy.get(IServerJsonOption.INJURY_RESULTS.getKey()).asArray();
		legacy.set(IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.getKey(), copy(injuries));
		legacy.set(IServerJsonOption.INJURY_RESULTS_DEAD.getKey(), copy(injuries));
		stripIndexes(legacy);

		reloadTwice(legacy, new int[]{0}, new int[]{0}, new int[0]);
	}

	@Test
	public void legacyCopyWithAdvancedStateReplacesTheMainInjuryAndUnmatchedCopiesAreKept() {
		StepApothecaryMultiple step = defenderStep(injury("away1", PlayerState.KNOCKED_OUT),
			injury("away1", PlayerState.KNOCKED_OUT));
		JsonObject legacy = step.toJsonValue();
		JsonArray injuries = legacy.get(IServerJsonOption.INJURY_RESULTS.getKey()).asArray();
		// the second copy moved on after the lists lost their shared objects in an earlier reload
		JsonArray regenerationFailed = copy(injuries);
		InjuryResult advanced = new InjuryResult().initFrom(state.getGame().getRules(), regenerationFailed.get(1));
		advanced.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_NOT_USE_PASTRIES);
		regenerationFailed.set(1, advanced.toJsonValue());
		legacy.set(IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.getKey(), regenerationFailed);
		// a copy of an injury the main list does not know
		legacy.set(IServerJsonOption.INJURY_RESULTS_DEAD.getKey(),
			new JsonArray().add(injury("away2", PlayerState.RIP).toJsonValue()));
		stripIndexes(legacy);

		JsonObject saved = load(legacy).toJsonValue();

		assertArrayEquals(new int[]{0, 1}, indexes(saved, IServerJsonOption.INJURY_RESULT_INDEXES_REGENERATION_FAILED));
		assertArrayEquals(new int[]{2}, indexes(saved, IServerJsonOption.INJURY_RESULT_INDEXES_DEAD));
		JsonArray mainInjuries = saved.get(IServerJsonOption.INJURY_RESULTS.getKey()).asArray();
		assertEquals(3, mainInjuries.size());
		assertEquals(ApothecaryStatus.DO_NOT_USE_PASTRIES,
			new InjuryResult().initFrom(state.getGame().getRules(), mainInjuries.get(1)).injuryContext()
				.getApothecaryStatus());
		assertEquals(ApothecaryStatus.NO_APOTHECARY,
			new InjuryResult().initFrom(state.getGame().getRules(), mainInjuries.get(0)).injuryContext()
				.getApothecaryStatus());
	}

	@Test
	public void newSavesStillCarryTheListsInThePreviousFormat() {
		addInducement(false, "bretonnianPastries");
		StepApothecaryMultiple step = defenderStep(injury("away1", PlayerState.KNOCKED_OUT));
		step.start();

		JsonObject saved = step.toJsonValue();

		assertEquals(1, saved.get(IServerJsonOption.INJURY_RESULTS_REGENERATION_FAILED.getKey()).asArray().size());
		assertTrue(saved.get(IServerJsonOption.INJURY_RESULTS_DEAD.getKey()).asArray().isEmpty());
		assertTrue(saved.get(IServerJsonOption.INJURY_RESULTS_GETTING_EVEN.getKey()).asArray().isEmpty());
	}

	/**
	 * Converts a current save into the previous format, loads it, saves it again in the current format and loads that.
	 */
	private StepApothecaryMultiple legacyRoundTrip(StepApothecaryMultiple step, int[] regenerationFailed, int[] dead,
		int[] gettingEven) {
		JsonObject legacy = step.toJsonValue();
		stripIndexes(legacy);
		return reloadTwice(legacy, regenerationFailed, dead, gettingEven);
	}

	private StepApothecaryMultiple reloadTwice(JsonObject legacy, int[] regenerationFailed, int[] dead,
		int[] gettingEven) {
		JsonObject resaved = load(legacy).toJsonValue();
		assertArrayEquals(regenerationFailed,
			indexes(resaved, IServerJsonOption.INJURY_RESULT_INDEXES_REGENERATION_FAILED));
		assertArrayEquals(dead, indexes(resaved, IServerJsonOption.INJURY_RESULT_INDEXES_DEAD));
		assertArrayEquals(gettingEven, indexes(resaved, IServerJsonOption.INJURY_RESULT_INDEXES_GETTING_EVEN));
		StepApothecaryMultiple restored = load(resaved);
		assertEquals(resaved, restored.toJsonValue());
		return restored;
	}

	private StepApothecaryMultiple load(JsonObject jsonObject) {
		return new StepApothecaryMultiple(state).initFrom(state.getGame().getRules(), Json.parse(jsonObject.toString()));
	}

	private void stripIndexes(JsonObject jsonObject) {
		jsonObject.remove(IServerJsonOption.INJURY_RESULT_INDEXES_REGENERATION_FAILED.getKey());
		jsonObject.remove(IServerJsonOption.INJURY_RESULT_INDEXES_DEAD.getKey());
		jsonObject.remove(IServerJsonOption.INJURY_RESULT_INDEXES_GETTING_EVEN.getKey());
		jsonObject.remove(IServerJsonOption.TEAM_ID.getKey());
	}

	private int[] indexes(JsonObject jsonObject, com.fumbbl.ffb.json.JsonIntArrayOption option) {
		return option.getFrom(state.getGame().getRules(), jsonObject);
	}

	private JsonArray copy(JsonArray array) {
		return Json.parse(array.toString()).asArray();
	}

	private StepApothecaryMultiple defenderStep(InjuryResult... injuryResults) {
		return step(false, injuryResults);
	}

	private StepApothecaryMultiple attackerStep(InjuryResult... injuryResults) {
		return step(true, injuryResults);
	}

	private StepApothecaryMultiple step(boolean actingTeam, InjuryResult... injuryResults) {
		StepApothecaryMultiple step = new StepApothecaryMultiple(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.ACTING_TEAM, actingTeam));
		step.init(parameters);
		for (InjuryResult injuryResult : injuryResults) {
			assertTrue(step.setParameter(StepParameter.from(StepParameterKey.INJURY_RESULT, injuryResult)));
		}
		return step;
	}

	private InjuryResult injury(String playerId, int base) {
		Game game = state.getGame();
		String attackerId = playerId.startsWith("home") ? "away1" : "home1";
		InjuryResult injuryResult = new InjuryResult();
		injuryResult.injuryContext().setInjuryType(new Block());
		injuryResult.injuryContext().setDefenderId(playerId);
		injuryResult.injuryContext().setAttackerId(attackerId);
		injuryResult.injuryContext()
			.setDefenderCoordinate(game.getFieldModel().getPlayerCoordinate(game.getPlayerById(playerId)));
		injuryResult.injuryContext().setApothecaryMode(ApothecaryMode.DEFENDER);
		injuryResult.injuryContext().setArmorBroken(true);
		injuryResult.injuryContext().setInjury(new PlayerState(base));
		injuryResult.injuryContext().setSufferedInjury(new PlayerState(base));
		injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
		return injuryResult;
	}

	private void addInducement(boolean home, String name) {
		Game game = state.getGame();
		(home ? game.getTurnDataHome() : game.getTurnDataAway()).getInducementSet()
			.addInducement(new Inducement(inducement(name), 1));
	}

	private InducementType inducement(String name) {
		InducementTypeFactory factory = state.getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return factory.forName(name);
	}

	private IDialogParameter dialog() {
		return state.getGame().getDialogParameter();
	}

	private int playerState(String playerId) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(playerId)).getBase();
	}

	private FieldCoordinate coordinate(String playerId) {
		Game game = state.getGame();
		Player<?> player = game.getPlayerById(playerId);
		return game.getFieldModel().getPlayerCoordinate(player);
	}
}
