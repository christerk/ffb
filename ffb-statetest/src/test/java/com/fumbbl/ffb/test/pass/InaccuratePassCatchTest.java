package com.fumbbl.ffb.test.pass;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.net.commands.ServerCommandModelSync;
import com.fumbbl.ffb.report.ReportCatchRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.Commands;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestRolls;
import com.fumbbl.ffb.test.TestServer;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InaccuratePassCatchTest {

	private static final FieldCoordinate TARGET = new FieldCoordinate(13, 7);
	// scatter rolls north, south, north end one square north of the target
	private static final FieldCoordinate LANDING = new FieldCoordinate(13, 6);

	private TestServer server;

	@BeforeEach
	void setUp() throws Exception {
		server = new TestServer();
	}

	@Test
	void catchingInaccuratePassAppliesNoModifier() {
		GameState state = inaccuratePass(LANDING);
		// pass roll 3 (inaccurate), scatter 1, 5, 1, catch 3
		ReportCatchRoll catchRoll = singleCatchRoll(state, 3, 1, 5, 1, 3);

		assertEquals(3, catchRoll.getMinimumRoll());
		assertEquals(0, catchRoll.getRollModifiers().length);
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("receiver")));
	}

	@Test
	void catchingBallBouncingOffInaccuratePassAppliesMinusOne() {
		GameState state = inaccuratePass(TARGET);
		// pass roll 3 (inaccurate), scatter 1, 5, 1 onto the empty landing square, bounce 5 onto the receiver, catch 4
		ReportCatchRoll catchRoll = singleCatchRoll(state, 3, 1, 5, 1, 5, 4);

		assertEquals(4, catchRoll.getMinimumRoll());
		assertTrue(UtilPlayer.hasBall(state.getGame(), state.getGame().getPlayerById("receiver")));
	}

	@Test
	void catchingInaccurateBombAppliesNoModifier() {
		GameState state = new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(20, 1)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(10, 7).stats(6, 3, 3, 4, 8).skill("Bombardier"))
				.player("receiver", player -> player.at(LANDING.getX(), LANDING.getY()).stats(6, 3, 3, 4, 8)))
			.withTeam(false, team -> team.player("opponent", player -> player.at(20, 1).stats(6, 3, 3, 5, 8)))
			.build();
		// pass roll 3 (inaccurate), scatter 1, 5, 1, catch 3
		TestRolls.on(state).general(3, 1, 5, 1, 3);
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.THROW_BOMB));
		StepEngine.respond(state, Commands.pass("thrower", TARGET));

		ReportCatchRoll catchRoll = singleCatchRoll(state);
		assertEquals(3, catchRoll.getMinimumRoll());
		assertEquals(0, catchRoll.getRollModifiers().length);
	}

	private GameState inaccuratePass(FieldCoordinate receiverCoordinate) {
		return new GameStateBuilder(server.getGameState())
			.withRule("BB2025").withWeather(Weather.NICE).withBallAt(10, 7)
			.withTeam(true, team -> team
				.player("thrower", player -> player.at(10, 7).stats(6, 3, 3, 4, 8))
				.player("receiver", player -> player.at(receiverCoordinate.getX(), receiverCoordinate.getY())
					.stats(6, 3, 3, 4, 8)))
			.withTeam(false, team -> team.player("opponent", player -> player.at(20, 1).stats(6, 3, 3, 5, 8)))
			.build();
	}

	private ReportCatchRoll singleCatchRoll(GameState state, int... rolls) {
		TestRolls.on(state).general(rolls);
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("thrower", PlayerAction.PASS_MOVE));
		StepEngine.respond(state, Commands.pass("thrower", TARGET));
		return singleCatchRoll(state);
	}

	private ReportCatchRoll singleCatchRoll(GameState state) {
		List<ReportCatchRoll> catchRolls = Arrays.stream(state.getGameLog().getServerCommands())
			.filter(command -> command instanceof ServerCommandModelSync)
			.flatMap(command -> Arrays.stream(((ServerCommandModelSync) command).getReportList().getReports()))
			.filter(report -> report instanceof ReportCatchRoll)
			.map(report -> (ReportCatchRoll) report)
			.collect(Collectors.toList());
		assertEquals(1, catchRolls.size());
		return catchRolls.get(0);
	}
}
