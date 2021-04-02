package com.balancedbytes.games.ffb.report.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.UtilReport;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportCardsAndInducementsBought implements IReport {

	private String teamId;
	private int cards, inducements, stars, mercenaries, gold, newTv;

	public ReportCardsAndInducementsBought() {
		super();
	}

	public ReportCardsAndInducementsBought(String teamId, int cards, int inducements, int stars, int mercenaries, int gold, int newTv) {
		this.teamId = teamId;
		this.inducements = inducements;
		this.stars = stars;
		this.mercenaries = mercenaries;
		this.gold = gold;
		this.cards = cards;
		this.newTv = newTv;
	}

	public ReportId getId() {
		return ReportId.CARDS_AND_INDUCEMENTS_BOUGHT;
	}

	public String getTeamId() {
		return teamId;
	}

	public int getInducements() {
		return inducements;
	}

	public int getStars() {
		return stars;
	}

	public int getMercenaries() {
		return mercenaries;
	}

	public int getGold() {
		return gold;
	}

	public int getCards() {
		return cards;
	}

	public int getNewTv() {
		return newTv;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCardsAndInducementsBought(getTeamId(), cards, getInducements(), getStars(), getMercenaries(),
				getGold(), newTv);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IJsonOption.NR_OF_INDUCEMENTS.addTo(jsonObject, inducements);
		IJsonOption.NR_OF_STARS.addTo(jsonObject, stars);
		IJsonOption.NR_OF_MERCENARIES.addTo(jsonObject, mercenaries);
		IJsonOption.GOLD.addTo(jsonObject, gold);
		IJsonOption.TEAM_VALUE.addTo(jsonObject, newTv);
		IJsonOption.NR_OF_CARDS.addTo(jsonObject, cards);
		return jsonObject;
	}

	public ReportCardsAndInducementsBought initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(game, jsonObject));
		teamId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		inducements = IJsonOption.NR_OF_INDUCEMENTS.getFrom(game, jsonObject);
		stars = IJsonOption.NR_OF_STARS.getFrom(game, jsonObject);
		mercenaries = IJsonOption.NR_OF_MERCENARIES.getFrom(game, jsonObject);
		gold = IJsonOption.GOLD.getFrom(game, jsonObject);
		cards = IJsonOption.NR_OF_CARDS.getFrom(game, jsonObject);
		newTv = IJsonOption.TEAM_VALUE.getFrom(game, jsonObject);
		return this;
	}

}
