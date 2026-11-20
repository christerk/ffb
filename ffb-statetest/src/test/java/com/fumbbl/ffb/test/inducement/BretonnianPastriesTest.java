package com.fumbbl.ffb.test.inducement;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.net.commands.ClientCommandUseApothecary;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.report.bb2025.ReportBretonnianPastriesRoll;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BretonnianPastriesTest {

	private static final String DEFENDER = "away1";

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void successfulRollMovesKnockedOutPlayerToReserves() {
		GameState state = setUpGame(0, 1, 0);

		knockOutDefender(state);
		assertPastriesOffered(state);

		TestRolls.on(state).general(2);
		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{DEFENDER}));

		assertEquals(PlayerState.RESERVE, defenderState(state));
		assertEquals(Arrays.asList("used:" + state.getGame().getTeamAway().getId() + ":1", "roll:away1:2"), pastriesReports(state));
		assertFalse(inducementSet(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void naturalOneKeepsPlayerKnockedOutAndUsesPastries() {
		GameState state = setUpGame(0, 1, 0);

		knockOutDefender(state);
		assertPastriesOffered(state);

		TestRolls.on(state).general(1);
		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{DEFENDER}));

		assertEquals(PlayerState.KNOCKED_OUT, defenderState(state));
		assertEquals(Arrays.asList("used:" + state.getGame().getTeamAway().getId() + ":1", "roll:away1:1"), pastriesReports(state));
		assertFalse(inducementSet(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void decliningKeepsPlayerKnockedOutAndPastriesAvailable() {
		GameState state = setUpGame(0, 1, 0);

		knockOutDefender(state);
		assertPastriesOffered(state);

		StepEngine.respond(state, new ClientCommandUseInducement((InducementType) null));

		assertEquals(PlayerState.KNOCKED_OUT, defenderState(state));
		assertTrue(inducementSet(state).hasUsesLeft(pastries(state)));
		assertFalse(isUseInducementDialog(state.getGame().getDialogParameter()));
		assertTrue(pastriesReports(state).isEmpty());
	}

	@Test
	public void pastriesAreNotOfferedWithoutUsesLeft() {
		GameState state = setUpGame(0, 1, 1);

		knockOutDefender(state);

		assertFalse(isUseInducementDialog(state.getGame().getDialogParameter()));
		assertEquals(PlayerState.KNOCKED_OUT, defenderState(state));
	}

	@Test
	public void pastriesAreNotOfferedWithoutInducement() {
		GameState state = setUpGame(0, 0, 0);

		knockOutDefender(state);

		assertFalse(isUseInducementDialog(state.getGame().getDialogParameter()));
		assertEquals(PlayerState.KNOCKED_OUT, defenderState(state));
	}

	@Test
	public void pastriesAreOfferedAfterApothecaryIsDeclined() {
		GameState state = setUpGame(1, 1, 0);

		knockOutDefender(state);
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		assertNotNull(dialogParameter);
		assertEquals(DialogId.USE_APOTHECARY, dialogParameter.getId());

		StepEngine.respond(state, new ClientCommandUseApothecary(DEFENDER, false, null, null));
		assertPastriesOffered(state);

		TestRolls.on(state).general(5);
		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{DEFENDER}));

		assertEquals(PlayerState.RESERVE, defenderState(state));
		assertEquals(1, state.getGame().getTurnDataAway().getApothecaries());
		assertFalse(inducementSet(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void pastriesAreNotOfferedWhenApothecaryAlreadySavedThePlayer() {
		GameState state = setUpGame(1, 1, 0);

		knockOutDefender(state);
		assertEquals(DialogId.USE_APOTHECARY, state.getGame().getDialogParameter().getId());

		StepEngine.respond(state, new ClientCommandUseApothecary(DEFENDER, true, null, null));

		assertFalse(isUseInducementDialog(state.getGame().getDialogParameter()));
		assertEquals(PlayerState.RESERVE, defenderState(state));
		assertEquals(0, state.getGame().getTurnDataAway().getApothecaries());
		assertTrue(inducementSet(state).hasUsesLeft(pastries(state)));
	}

	@Test
	public void turnTimerIsStoppedWhileTheOpponentDecidesAfterDecliningTheApothecary() {
		GameState state = setUpGame(1, 1, 0);
		state.getGame().setStarted(new Date());

		knockOutDefender(state);
		assertEquals(DialogId.USE_APOTHECARY, state.getGame().getDialogParameter().getId());

		StepEngine.respond(state, new ClientCommandUseApothecary(DEFENDER, false, null, null));
		assertPastriesOffered(state);
		assertTrue(state.getGame().isWaitingForOpponent());
		assertEquals(0, state.getTurnTimeStarted());

		TestRolls.on(state).general(3);
		StepEngine.respond(state, new ClientCommandUseInducement(pastries(state), new String[]{DEFENDER}));

		assertFalse(state.getGame().isWaitingForOpponent());
		assertNotEquals(0, state.getTurnTimeStarted());
	}

	private GameState setUpGame(int apothecaries, int pastriesBought, int pastriesUsed) {
		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 1).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player(DEFENDER, p -> p.at(7, 0).stats(6, 3, 3, 5, 8)))
			.build();

		state.getGame().getTurnDataAway().setApothecaries(apothecaries);
		if (pastriesBought > 0) {
			Inducement inducement = new Inducement(pastries(state), pastriesBought);
			inducement.setUses(pastriesUsed);
			inducementSet(state).addInducement(inducement);
		}
		return state;
	}

	private void knockOutDefender(GameState state) {
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		// pushed into the crowd, injury roll of 9 knocks the player out
		TestRolls.on(state)
			.block("pushback")
			.injury(4, 5);
		StepEngine.respond(state, Commands.block("home1", DEFENDER));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		if (step != null && step.getId() == StepId.FOLLOWUP) {
			StepEngine.respond(state, Commands.followup(false));
		}
	}

	private void assertPastriesOffered(GameState state) {
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		assertTrue(isUseInducementDialog(dialogParameter), "Expected use inducement dialog, was " + dialogParameter);
		DialogUseInducementParameter parameter = (DialogUseInducementParameter) dialogParameter;
		assertEquals(state.getGame().getTeamAway().getId(), parameter.getTeamId());
		assertEquals(DEFENDER, parameter.getPlayerId());
		assertArrayEquals(new InducementType[]{pastries(state)}, parameter.getInducementTypes());
		assertEquals(StepId.APOTHECARY, state.getCurrentStep().getId());
	}

	private boolean isUseInducementDialog(IDialogParameter dialogParameter) {
		return dialogParameter != null && dialogParameter.getId() == DialogId.USE_INDUCEMENT;
	}

	private int defenderState(GameState state) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(DEFENDER)).getBase();
	}

	private InducementSet inducementSet(GameState state) {
		return state.getGame().getTurnDataAway().getInducementSet();
	}

	private List<String> pastriesReports(GameState state) {
		return Arrays.stream(state.getGameLog().getServerCommands())
			.filter(command -> command instanceof ServerCommandModelSync)
			.flatMap(command -> Arrays.stream(((ServerCommandModelSync) command).getReportList().getReports()))
			.map(report -> describe(report, pastries(state)))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	static String describe(IReport report, InducementType pastries) {
		if (report instanceof ReportInducement && ((ReportInducement) report).getInducementType() != null
			&& pastries.getName().equals(((ReportInducement) report).getInducementType().getName())) {
			ReportInducement inducement = (ReportInducement) report;
			return "used:" + inducement.getTeamId() + ":" + inducement.getValue();
		}
		if (report instanceof ReportBretonnianPastriesRoll) {
			ReportBretonnianPastriesRoll roll = (ReportBretonnianPastriesRoll) report;
			return "roll:" + roll.getPlayerId() + ":" + roll.getRoll();
		}
		return null;
	}

	private InducementType pastries(GameState state) {
		InducementTypeFactory factory = state.getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return factory.forName("bretonnianPastries");
	}
}
