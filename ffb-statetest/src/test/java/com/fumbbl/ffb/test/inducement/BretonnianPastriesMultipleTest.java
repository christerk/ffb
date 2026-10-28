package com.fumbbl.ffb.test.inducement;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandUseInducement;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.bb2025.mutliblock.StepApothecaryMultiple;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BretonnianPastriesMultipleTest {

	private TestServer testServer;
	private GameState state;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
		state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, t -> t
				.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8))
				.player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)))
			.withTeam(false, t -> t
				.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8))
				.player("away2", p -> p.at(8, 8).stats(6, 3, 3, 5, 8)))
			.build();
		state.getGame().setHomePlaying(true);
		addPastries(state.getGame().getTurnDataHome().getInducementSet());
		addPastries(state.getGame().getTurnDataAway().getInducementSet());
	}

	@Test
	public void pastriesAreOfferedForTheFirstKnockedOutPlayer() {
		StepApothecaryMultiple step = defenderStep(knockedOut("away1"), knockedOut("away2"));
		step.start();

		DialogUseInducementParameter parameter = pastriesDialog();
		assertEquals("awayTeam", parameter.getTeamId());
		assertEquals(pastries(), parameter.getInducementTypes()[0]);
		assertEquals("away1", parameter.getPlayerId());
	}

	@Test
	public void successfulRollMovesOnlyTheChosenPlayerToReserves() {
		StepApothecaryMultiple step = defenderStep(knockedOut("away1"), knockedOut("away2"));
		step.start();

		TestRolls.on(state).general(3);
		accept(step, "away1");

		assertEquals(Arrays.asList("used:awayTeam:1", "roll:away1:3"), pastriesReports());
		assertEquals(PlayerState.RESERVE, playerState("away1"));
		assertEquals(PlayerState.KNOCKED_OUT, playerState("away2"));
		assertFalse(awayInducements().hasUsesLeft(pastries()));
	}

	@Test
	public void decliningOffersPastriesForTheNextKnockedOutPlayer() {
		StepApothecaryMultiple step = defenderStep(knockedOut("away1"), knockedOut("away2"));
		step.start();

		decline(step);

		assertEquals("away2", pastriesDialog().getPlayerId());

		TestRolls.on(state).general(1);
		accept(step, "away2");

		assertEquals(Arrays.asList("used:awayTeam:1", "roll:away2:1"), pastriesReports());
		assertEquals(PlayerState.KNOCKED_OUT, playerState("away1"));
		assertEquals(PlayerState.KNOCKED_OUT, playerState("away2"));
		assertFalse(awayInducements().hasUsesLeft(pastries()));
	}

	@Test
	public void turnTimerIsStoppedWhileTheOpponentDecides() {
		Game game = state.getGame();
		game.setStarted(new Date());
		state.setTurnTimeStarted(System.currentTimeMillis() - 1000);

		StepApothecaryMultiple step = defenderStep(knockedOut("away1"), knockedOut("away2"));
		step.start();

		pastriesDialog();
		assertTrue(game.isWaitingForOpponent());
		assertEquals(0, state.getTurnTimeStarted());

		TestRolls.on(state).general(4);
		accept(step, "away1");

		assertFalse(game.isWaitingForOpponent());
		assertNotEquals(0, state.getTurnTimeStarted());
	}

	@Test
	public void attackerKnockedOutTwiceIsPlacedInReservesAfterSuccessfulPastries() {
		falling("home1");
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), knockedOut("home1"));
		step.start();

		TestRolls.on(state).general(6);
		accept(step, "home1");

		assertEquals(Arrays.asList("used:homeTeam:1", "roll:home1:6"), pastriesReports());
		assertInReserves("home1");
		assertFalse(homeInducements().hasUsesLeft(pastries()));
	}

	@Test
	public void attackerKnockedOutAndStunnedIsPlacedInReservesAfterSuccessfulPastries() {
		falling("home1");
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), stunned("home1"));
		step.start();

		TestRolls.on(state).general(2);
		accept(step, "home1");

		assertEquals(Arrays.asList("used:homeTeam:1", "roll:home1:2"), pastriesReports());
		assertInReserves("home1");
		assertFalse(homeInducements().hasUsesLeft(pastries()));
	}

	@Test
	public void attackerStunnedAndKnockedOutIsPlacedInReservesAfterSuccessfulPastries() {
		falling("home1");
		StepApothecaryMultiple step = attackerStep(stunned("home1"), knockedOut("home1"));
		step.start();

		TestRolls.on(state).general(5);
		accept(step, "home1");

		assertEquals(Arrays.asList("used:homeTeam:1", "roll:home1:5"), pastriesReports());
		assertInReserves("home1");
	}

	@Test
	public void attackerKnockedOutTwiceStaysKnockedOutAfterFailedPastries() {
		falling("home1");
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), knockedOut("home1"));
		step.start();

		TestRolls.on(state).general(1);
		accept(step, "home1");

		assertEquals(Arrays.asList("used:homeTeam:1", "roll:home1:1"), pastriesReports());
		assertEquals(PlayerState.KNOCKED_OUT, playerState("home1"));
	}

	@Test
	public void attackerKnockedOutTwiceIsOfferedPastriesOnlyOnceWhenDeclined() {
		addPastries(homeInducements());
		falling("home1");
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), knockedOut("home1"));
		step.start();

		assertEquals("home1", pastriesDialog().getPlayerId());
		decline(step);

		assertNotEquals(DialogId.USE_INDUCEMENT, dialogId());
		assertTrue(pastriesReports().isEmpty());
		assertEquals(PlayerState.KNOCKED_OUT, playerState("home1"));
		assertEquals(0, homeInducements().get(pastries()).getUses());
	}

	@Test
	public void attackerKnockedOutTwiceIsOfferedPastriesOnlyOnceWhenTheRollFails() {
		addPastries(homeInducements());
		falling("home1");
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), knockedOut("home1"));
		step.start();

		TestRolls.on(state).general(1);
		accept(step, "home1");

		assertNotEquals(DialogId.USE_INDUCEMENT, dialogId());
		assertEquals(Arrays.asList("used:homeTeam:1", "roll:home1:1"), pastriesReports());
		assertEquals(PlayerState.KNOCKED_OUT, playerState("home1"));
		assertTrue(homeInducements().hasUsesLeft(pastries()));
		assertEquals(1, homeInducements().get(pastries()).getUses());
	}

	@Test
	public void successfulPastriesDoNotOverrideCasualtyWhenKnockOutComesFirst() {
		StepApothecaryMultiple step = attackerStep(knockedOut("home1"), badlyHurt("home1"));
		step.start();

		TestRolls.on(state).general(6);
		accept(step, "home1");

		assertEquals(PlayerState.BADLY_HURT, playerState("home1"));
		assertFalse(homeInducements().hasUsesLeft(pastries()));
	}

	@Test
	public void successfulPastriesDoNotOverrideCasualtyWhenCasualtyComesFirst() {
		StepApothecaryMultiple step = attackerStep(badlyHurt("home1"), knockedOut("home1"));
		step.start();

		TestRolls.on(state).general(6);
		accept(step, "home1");

		assertEquals(PlayerState.BADLY_HURT, playerState("home1"));
		assertFalse(homeInducements().hasUsesLeft(pastries()));
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

	private void accept(IStep step, String playerId) {
		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement(pastries(), new String[]{playerId}), null));
	}

	private void decline(IStep step) {
		step.handleCommand(new ReceivedCommand(new ClientCommandUseInducement((InducementType) null), null));
	}

	private DialogUseInducementParameter pastriesDialog() {
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		assertEquals(DialogId.USE_INDUCEMENT, dialogParameter.getId());
		return (DialogUseInducementParameter) dialogParameter;
	}

	private InjuryResult knockedOut(String playerId) {
		return injury(playerId, PlayerState.KNOCKED_OUT);
	}

	private InjuryResult stunned(String playerId) {
		return injury(playerId, PlayerState.STUNNED);
	}

	private InjuryResult badlyHurt(String playerId) {
		return injury(playerId, PlayerState.BADLY_HURT);
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

	private void addPastries(InducementSet inducementSet) {
		Inducement inducement = inducementSet.getInducementMapping().get(pastries());
		inducementSet.addInducement(new Inducement(pastries(), inducement == null ? 1 : inducement.getValue() + 1));
	}

	private void falling(String playerId) {
		Game game = state.getGame();
		Player<?> player = game.getPlayerById(playerId);
		game.getFieldModel().setPlayerState(player, game.getFieldModel().getPlayerState(player).changeBase(PlayerState.FALLING));
	}

	private void assertInReserves(String playerId) {
		Game game = state.getGame();
		Player<?> player = game.getPlayerById(playerId);
		assertEquals(PlayerState.RESERVE, playerState(playerId));
		assertTrue(game.getFieldModel().getPlayerCoordinate(player).isBoxCoordinate());
	}

	private DialogId dialogId() {
		IDialogParameter dialogParameter = state.getGame().getDialogParameter();
		return dialogParameter == null ? null : dialogParameter.getId();
	}

	private int playerState(String playerId) {
		Game game = state.getGame();
		return game.getFieldModel().getPlayerState(game.getPlayerById(playerId)).getBase();
	}

	private InducementSet homeInducements() {
		return state.getGame().getTurnDataHome().getInducementSet();
	}

	private InducementSet awayInducements() {
		return state.getGame().getTurnDataAway().getInducementSet();
	}

	private List<String> pastriesReports() {
		return Arrays.stream(state.getGameLog().getServerCommands())
			.filter(command -> command instanceof ServerCommandModelSync)
			.flatMap(command -> Arrays.stream(((ServerCommandModelSync) command).getReportList().getReports()))
			.map(report -> BretonnianPastriesTest.describe(report, pastries()))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private InducementType pastries() {
		InducementTypeFactory factory = state.getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		return factory.forName("bretonnianPastries");
	}
}
