package com.fumbbl.ffb.report.bb2025;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReportIdFactory;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportBretonnianPastriesRollTest {

	private IFactorySource source;

	@BeforeEach
	void setUp() {
		source = mock(IFactorySource.class);
		when(source.forContext(any())).thenReturn(source);
		when(source.getFactory(FactoryType.Factory.REPORT_ID)).thenReturn(new ReportIdFactory());
	}

	@Test
	void successfulRollSurvivesJsonRoundTrip() {
		ReportBretonnianPastriesRoll report = roundTrip(new ReportBretonnianPastriesRoll("team1", "player1", 4, true));

		assertEquals(ReportId.BRETONNIAN_PASTRIES_ROLL, report.getId());
		assertEquals("team1", report.getTeamId());
		assertEquals("player1", report.getPlayerId());
		assertEquals(4, report.getRoll());
		assertTrue(report.isSuccessful());
	}

	@Test
	void failedRollSurvivesJsonRoundTrip() {
		ReportBretonnianPastriesRoll report = roundTrip(new ReportBretonnianPastriesRoll("team2", "player2", 1, false));

		assertEquals("team2", report.getTeamId());
		assertEquals("player2", report.getPlayerId());
		assertEquals(1, report.getRoll());
		assertFalse(report.isSuccessful());
	}

	@Test
	void jsonUsesExpectedKeys() {
		JsonObject json = (JsonObject) new ReportBretonnianPastriesRoll("team1", "player1", 6, true).toJsonValue();

		assertEquals("bretonnianPastriesRoll", json.get("reportId").asString());
		assertEquals("team1", json.get("teamId").asString());
		assertEquals("player1", json.get("playerId").asString());
		assertEquals(6, json.get("roll").asInt());
		assertTrue(json.get("successful").asBoolean());
	}

	@Test
	void transformKeepsValues() {
		ReportBretonnianPastriesRoll report =
			(ReportBretonnianPastriesRoll) new ReportBretonnianPastriesRoll("team1", "player1", 2, true).transform(source);

		assertEquals("player1", report.getPlayerId());
		assertEquals(2, report.getRoll());
		assertTrue(report.isSuccessful());
	}

	@Test
	void rollIsTrackedAsSingleD6WithMinimumTwo() {
		List<DieStat<?>> stats = new ArrayList<>();
		new ReportBretonnianPastriesRoll("team1", "player1", 3, true).addStats(null, stats);

		assertEquals(1, stats.size());
		SingleDieStat stat = (SingleDieStat) stats.get(0);
		assertEquals(DieBase.D6, stat.getBase());
		assertEquals(2, stat.getMinimumRoll());
		assertEquals(ReportId.BRETONNIAN_PASTRIES_ROLL, stat.getReportId());
		assertTrue(stat.isSuccessful());
	}

	@Test
	void reportIsRegisteredForBb2025Only() {
		assertTrue(reportsFor("BB2025").stream().anyMatch(report -> report instanceof ReportBretonnianPastriesRoll));
		assertFalse(reportsFor("BB2020").stream().anyMatch(report -> report instanceof ReportBretonnianPastriesRoll));
	}

	private Collection<IReport> reportsFor(String rulesVersion) {
		GameOptions options = new GameOptions(null);
		options.addOption(new GameOptionString(GameOptionId.RULESVERSION).setValue(rulesVersion));
		return new Scanner<>(IReport.class).getInstancesImplementing(options);
	}

	private ReportBretonnianPastriesRoll roundTrip(ReportBretonnianPastriesRoll report) {
		JsonValue json = report.toJsonValue();
		return (ReportBretonnianPastriesRoll) new ReportBretonnianPastriesRoll().initFrom(source, Json.parse(json.toString()));
	}
}
