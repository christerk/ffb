package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.List;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportBombExplodesAfterCatch implements IReport {
	private String catcherId;
	private boolean explodes;
	private int roll;

	public ReportBombExplodesAfterCatch() {
	}

	public ReportBombExplodesAfterCatch(String catcherId, boolean explodes, int roll) {
		this.catcherId = catcherId;
		this.explodes = explodes;
		this.roll = roll;
	}

	@Override
	public ReportBombExplodesAfterCatch initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		roll = IJsonOption.ROLL.getFrom(source, jsonObject);
		catcherId = IJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		explodes = IJsonOption.EXPLODES.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.ROLL.addTo(jsonObject, roll);
		IJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IJsonOption.EXPLODES.addTo(jsonObject, explodes);
		return jsonObject;
	}

	@Override
	public ReportId getId() {
		return ReportId.BOMB_EXPLODES_AFTER_CATCH;
	}

	@Override
	public IReport transform(IFactorySource source) {
		return new ReportBombExplodesAfterCatch(catcherId, explodes, roll);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.OPPONENT_TEAM_FOR_PLAYER, catcherId, roll, 4, getId(), explodes));
	}

	public String getCatcherId() {
		return catcherId;
	}

	public boolean explodes() {
		return explodes;
	}

	public int getRoll() {
		return roll;
	}
}
