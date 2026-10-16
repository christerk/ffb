package com.fumbbl.ffb.test;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.change.ModelChangeList;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.report.ReportPlayerAction;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.step.StepId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ActingPlayerActionReportTest {

	private TestServer testServer;

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
	}

	@Test
	public void reportsActionChangeForAlreadySelectedPlayer() {
		List<ReportList> sentReports = new ArrayList<>();
		ServerCommunication communication = mock(ServerCommunication.class);
		doAnswer(invocation -> {
			sentReports.add(((ReportList) invocation.getArguments()[2]).copy());
			return null;
		}).when(communication).sendModelSync(any(GameState.class), any(ModelChangeList.class), any(ReportList.class),
			nullable(Animation.class), nullable(SoundId.class), anyLong(), anyLong());
		testServer.getServer().setCommunication(communication);

		GameState state = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, team -> team
				.player("home1", player -> player.at(7, 7).stats(6, 3, 3, 5, 8)))
			.withTeam(false, team -> team
				.player("away1", player -> player.at(8, 7).stats(6, 3, 3, 5, 8)))
			.build();

		StepEngine.start(state);
		assertEquals(StepId.INIT_SELECTING, state.getCurrentStep().getId());
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
		assertEquals(StepId.INIT_SELECTING, state.getCurrentStep().getId());
		sentReports.clear();

		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		assertTrue(sentReports.stream()
			.flatMap(reportList -> Arrays.stream(reportList.getReports()))
			.filter(ReportPlayerAction.class::isInstance)
			.map(ReportPlayerAction.class::cast)
			.anyMatch(report -> "home1".equals(report.getActingPlayerId())
				&& report.getPlayerAction() == PlayerAction.BLOCK));
	}
}
