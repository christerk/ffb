package com.fumbbl.ffb.test.inducement;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.mutliblock.StepApothecaryMultiple;
import com.fumbbl.ffb.server.step.bb2025.shared.StepApothecary;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Command handling scenarios for Bretonnian Pastries: stray and
 * misaddressed use inducement commands, a full game state save and reload while the single step pastries dialog is
 * open, and the injured player's team (not the acting team) owning the offer in the single step.
 */
public class BretonnianPastriesCommandTest {

	private TestServer testServer;
	private Session homeSession;
	private Session awaySession;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
		homeSession = mock(Session.class);
		awaySession = mock(Session.class);
	}

	// a stray command with no injury result pending

	@Test
	public void strayUseInducementReachingAStepApothecaryWithoutAnInjuryIsIgnored() {
		GameState state = singleBlockGame(0, 1);
		StepApothecary step = new StepApothecary(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		step.init(parameters);

		StepCommandStatus status = assertDoesNotThrow(() -> step.handleCommand(new ReceivedCommand(
			new ClientCommandUseInducement(pastries(state), new String[]{"away1"}), null)));

		assertNotEquals(StepCommandStatus.EXECUTE_STEP, status);
		assertTrue(awayInducements(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void pastriesSentWhileTheApothecaryDialogIsOpenAreIgnored() {
		GameState state = singleBlockGame(1, 1);
		knockOutAway1(state, null);
		assertEquals(DialogId.USE_APOTHECARY, dialogId(state));

		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{"away1"}));

		assertEquals(DialogId.USE_APOTHECARY, dialogId(state));
		assertTrue(awayInducements(state).hasUsesLeft(pastries(state)));
		assertTrue(pastriesReports(state).isEmpty());
		assertEquals(1, state.getGame().getTurnDataAway().getApothecaries());
	}

	// save/reload at every dialog pause: the single step, through the game state round trip the server uses

	@Test
	public void singleStepGameReloadedWhileThePastriesDialogIsOpenStillResolves() {
		GameState state = singleBlockGame(0, 1);
		knockOutAway1(state, null);
		assertPastriesOffered(state, "away1");

		GameState reloaded = reload(state);
		assertPastriesOffered(reloaded, "away1");
		assertEquals(StepId.APOTHECARY, reloaded.getCurrentStep().getId());

		TestRolls.on(reloaded).general(2);
		StepEngine.respond(reloaded, new ClientCommandUseInducement(pastries(reloaded), new String[]{"away1"}));

		assertEquals(PlayerState.RESERVE, playerState(reloaded, "away1"));
		assertFalse(awayInducements(reloaded).hasUsesLeft(pastries(reloaded)));
		assertEquals(Collections.singletonList("roll:away1:2"), lastRoll(reloaded));
	}

	@Test
	public void singleStepGameReloadedWhileThePastriesDialogIsOpenCanStillBeDeclined() {
		GameState state = singleBlockGame(0, 1);
		knockOutAway1(state, null);

		GameState reloaded = reload(state);
		StepEngine.respond(reloaded, new ClientCommandUseInducement((InducementType) null));

		assertEquals(PlayerState.KNOCKED_OUT, playerState(reloaded, "away1"));
		assertTrue(awayInducements(reloaded).hasUsesLeft(pastries(reloaded)));
		assertNotEquals(DialogId.USE_INDUCEMENT, dialogId(reloaded));
	}

	// duplicated offer/handle logic drifted on the report team: the single step must use the injured player's team

	@Test
	public void homePlayerKnockedOutOnTheOpponentsTurnUsesTheHomePastriesAndReportsTheHomeTeam() {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 0).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 1).stats(6, 3, 3, 5, 8)))
			.build();
		Game game = state.getGame();
		game.setHomePlaying(false);
		game.getTurnDataHome().setApothecaries(0);
		game.getTurnDataAway().setApothecaries(0);
		addPastries(state, game.getTurnDataHome().getInducementSet(), 1);
		addPastries(state, game.getTurnDataAway().getInducementSet(), 1);

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("away1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pushback").injury(4, 5);
		StepEngine.respond(state, Commands.block("away1", "home1"));
		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		if (step != null && step.getId() == StepId.FOLLOWUP) {
			StepEngine.respond(state, Commands.followup(false));
		}

		IDialogParameter dialogParameter = game.getDialogParameter();
		assertTrue(dialogParameter instanceof DialogUseInducementParameter, "Expected the pastries offer, was " + dialogParameter);
		assertEquals(game.getTeamHome().getId(), ((DialogUseInducementParameter) dialogParameter).getTeamId());

		TestRolls.on(state).general(4);
		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{"home1"}));

		assertEquals(PlayerState.RESERVE, playerState(state, "home1"));
		assertEquals(Arrays.asList("used:" + game.getTeamHome().getId() + ":1", "roll:home1:4"), pastriesReports(state));
		assertFalse(game.getTurnDataHome().getInducementSet().hasUsesLeft(pastries(state)));
		assertTrue(game.getTurnDataAway().getInducementSet().hasUsesLeft(pastries(state)));
	}

	// commands from the wrong coach, for the wrong player, or with no offer pending

	@Test
	public void opponentCannotAnswerThePastriesOfferInTheSingleStep() {
		GameState state = singleBlockGame(0, 1);
		registerSessions(state);
		knockOutAway1(state, homeSession);
		assertPastriesOffered(state, "away1");

		StepEngine.respond(state, new ClientCommandUseInducement((InducementType) null), homeSession);

		assertPastriesOffered(state, "away1");
		assertTrue(awayInducements(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void opponentCannotAnswerThePastriesOfferInTheMultiBlockStep() {
		GameState state = multiBlockGame(1);
		registerSessions(state);
		StepApothecaryMultiple step = multiStep(state, false, knockedOut(state, "away1"));
		step.start();
		assertPastriesOffered(state, "away1");

		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement((InducementType) null), homeSession));

		assertPastriesOffered(state, "away1");
		assertTrue(awayInducements(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void acceptNamingAnotherPlayerDoesNotResolveThePendingMultiBlockOffer() {
		GameState state = multiBlockGame(2);
		StepApothecaryMultiple step = multiStep(state, false, knockedOut(state, "away1"), knockedOut(state, "away2"));
		step.start();
		assertPastriesOffered(state, "away1");
		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement((InducementType) null), null));
		assertPastriesOffered(state, "away2");

		// a late duplicate of an accept for away1
		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement(pastries(state), new String[]{"away1"}),
			null));

		assertPastriesOffered(state, "away2");
		assertEquals(0, awayInducements(state).getInducementMapping().get(pastries(state)).getUses());
	}

	@Test
	public void strayPastriesWithoutAPendingOfferAreNotSpentByTheRegenerationBranch() {
		GameState state = multiBlockGame(1);
		state.getGame().getTurnDataAway().setApothecaries(1);
		InjuryResult knockedOut = knockedOut(state, "away1");
		knockedOut.injuryContext().setApothecaryStatus(ApothecaryStatus.DO_REQUEST);
		StepApothecaryMultiple step = multiStep(state, false, knockedOut);
		step.start();
		assertEquals(DialogId.USE_APOTHECARIES, dialogId(state));

		// the apothecary dialog is open, no pastries offer is pending
		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement(pastries(state), new String[]{"away1"}),
			null));

		assertEquals(0, awayInducements(state).getInducementMapping().get(pastries(state)).getUses());
		assertTrue(pastriesReports(state).isEmpty());
	}

	// helpers

	private GameState singleBlockGame(int awayApothecaries, int awayPastries) {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 1).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(7, 0).stats(6, 3, 3, 5, 8)))
			.build();
		state.getGame().setHomePlaying(true);
		state.getGame().getTurnDataAway().setApothecaries(awayApothecaries);
		addPastries(state, awayInducements(state), awayPastries);
		return state;
	}

	private GameState multiBlockGame(int awayPastries) {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
				.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
			.build();
		state.getGame().setHomePlaying(true);
		addPastries(state, awayInducements(state), awayPastries);
		return state;
	}

	private void addPastries(GameState state, InducementSet inducementSet, int count) {
		if (count > 0) {
			inducementSet.addInducement(new Inducement(pastries(state), count));
		}
	}

	private void registerSessions(GameState state) {
		SessionManager sessionManager = testServer.getServer().getSessionManager();
		sessionManager.addSession(homeSession, state.getId(), "homeCoach", ClientMode.PLAYER, true,
			Collections.emptyList());
		sessionManager.addSession(awaySession, state.getId(), "awayCoach", ClientMode.PLAYER, false,
			Collections.emptyList());
	}

	private void knockOutAway1(GameState state, Session actingSession) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK), actingSession);
		// pushed into the crowd, injury roll of 9 knocks the player out
		TestRolls.on(state).block("pushback").injury(4, 5);
		StepEngine.respond(state, Commands.block("home1", "away1"), actingSession);
		StepEngine.respond(state, Commands.blockChoice(0), actingSession);
		if (dialogId(state) == DialogId.FOLLOWUP_CHOICE) {
			StepEngine.respond(state, Commands.followup(false), actingSession);
		}
	}

	private GameState reload(GameState state) {
		JsonObject saved = state.toJsonValue();
		GameState reloaded = new GameState(testServer.getServer())
			.initFrom(testServer.getServer().getFactorySource(), Json.parse(saved.toString()));
		assertEquals(saved.toString(), reloaded.toJsonValue().toString());
		return reloaded;
	}

	private StepApothecaryMultiple multiStep(GameState state, boolean actingTeam, InjuryResult... injuryResults) {
		StepApothecaryMultiple step = new StepApothecaryMultiple(state);
		StepParameterSet parameters = new StepParameterSet();
		parameters.add(new StepParameter(StepParameterKey.ACTING_TEAM, actingTeam));
		step.init(parameters);
		for (InjuryResult injuryResult : injuryResults) {
			assertTrue(step.setParameter(StepParameter.from(StepParameterKey.INJURY_RESULT, injuryResult)));
		}
		return step;
	}

	private InjuryResult knockedOut(GameState state, String playerId) {
		Game game = state.getGame();
		InjuryResult injuryResult = new InjuryResult();
		injuryResult.injuryContext().setInjuryType(new Block());
		injuryResult.injuryContext().setDefenderId(playerId);
		injuryResult.injuryContext().setAttackerId(playerId.startsWith("home") ? "away1" : "home1");
		injuryResult.injuryContext()
			.setDefenderCoordinate(game.getFieldModel().getPlayerCoordinate(game.getPlayerById(playerId)));
		injuryResult.injuryContext().setApothecaryMode(ApothecaryMode.DEFENDER);
		injuryResult.injuryContext().setArmorBroken(true);
		injuryResult.injuryContext().setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		injuryResult.injuryContext().setSufferedInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		injuryResult.injuryContext().setApothecaryStatus(ApothecaryStatus.NO_APOTHECARY);
		return injuryResult;
	}

	private void assertPastriesOffered(GameState state, String playerId) {
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		assertTrue(dialogParameter instanceof DialogUseInducementParameter, "Expected the pastries offer, was " + dialogParameter);
		assertEquals(playerId, ((DialogUseInducementParameter) dialogParameter).getPlayerId());
	}

	private DialogId dialogId(GameState state) {
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		return dialogParameter == null ? null : dialogParameter.getId();
	}

	private int playerState(GameState state, String playerId) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(playerId)).getBase();
	}

	private InducementSet awayInducements(GameState state) {
		return state.getGame().getTurnDataAway().getInducementSet();
	}

	private List<String> pastriesReports(GameState state) {
		return Arrays.stream(state.getGameLog().getServerCommands())
			.filter(command -> command instanceof ServerCommandModelSync)
			.flatMap(command -> Arrays.stream(((ServerCommandModelSync) command).getReportList().getReports()))
			.map(report -> BretonnianPastriesTest.describe(report, pastries(state)))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private List<String> lastRoll(GameState state) {
		List<String> rolls = pastriesReports(state).stream().filter(entry -> entry.startsWith("roll:"))
			.collect(Collectors.toList());
		return rolls.isEmpty() ? rolls : Collections.singletonList(rolls.get(rolls.size() - 1));
	}

	private InducementType pastries(GameState state) {
		InducementTypeFactory factory = state.getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return factory.forName("bretonnianPastries");
	}
}
