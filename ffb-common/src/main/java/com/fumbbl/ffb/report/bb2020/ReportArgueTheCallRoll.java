package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.UtilReport;
import com.fumbbl.ffb.stats.DieBase;
import com.fumbbl.ffb.stats.DieStat;
import com.fumbbl.ffb.stats.SingleDieStat;
import com.fumbbl.ffb.stats.TeamMapping;

import java.util.List;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportArgueTheCallRoll implements IReport {

	private String fPlayerId;
	private boolean fSuccessful;
	private boolean fCoachBanned;
	private boolean staysOnPitch, friendsWithRef;
	private int fRoll;
	private int biasedRefs;

	public ReportArgueTheCallRoll() {
		super();
	}

	public ReportArgueTheCallRoll(String playerId, boolean successful, boolean coachBanned, int roll, boolean staysOnPitch,
																boolean friendsWithRef, int biasedRefs) {
		fPlayerId = playerId;
		fSuccessful = successful;
		fCoachBanned = coachBanned;
		fRoll = roll;
		this.staysOnPitch = staysOnPitch;
		this.friendsWithRef = friendsWithRef;
		this.biasedRefs = biasedRefs;
	}

	public ReportId getId() {
		return ReportId.ARGUE_THE_CALL;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	public boolean isSuccessful() {
		return fSuccessful;
	}

	public boolean isCoachBanned() {
		return fCoachBanned;
	}

	public int getRoll() {
		return fRoll;
	}

	public boolean isStaysOnPitch() {
		return staysOnPitch;
	}

	public boolean isFriendsWithRef() {
		return friendsWithRef;
	}

	public int getBiasedRefs() {
		return biasedRefs;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportArgueTheCallRoll(getPlayerId(), isSuccessful(), isCoachBanned(), getRoll(), staysOnPitch, friendsWithRef, biasedRefs);
	}

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
		int minimumRoll = 6 - biasedRefs;
		if (friendsWithRef) {
			minimumRoll--;
		}
		diceStats.add(new SingleDieStat(DieBase.D6, TeamMapping.TEAM_FOR_PLAYER, fPlayerId, fRoll, minimumRoll, getId(), fRoll >= minimumRoll));
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.REPORT_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
		IJsonOption.COACH_BANNED.addTo(jsonObject, fCoachBanned);
		IJsonOption.ROLL.addTo(jsonObject, fRoll);
		IJsonOption.STAYS_ON_PITCH.addTo(jsonObject, staysOnPitch);
		IJsonOption.FRIENDS_WITH_REF.addTo(jsonObject, friendsWithRef);
		IJsonOption.BIASED_REFS.addTo(jsonObject, biasedRefs);
		return jsonObject;
	}

	public ReportArgueTheCallRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fSuccessful = IJsonOption.SUCCESSFUL.getFrom(source, jsonObject);
		fCoachBanned = IJsonOption.COACH_BANNED.getFrom(source, jsonObject);
		fRoll = IJsonOption.ROLL.getFrom(source, jsonObject);
		staysOnPitch = IJsonOption.STAYS_ON_PITCH.getFrom(source, jsonObject);
		friendsWithRef = IJsonOption.FRIENDS_WITH_REF.getFrom(source, jsonObject);
		biasedRefs = IJsonOption.BIASED_REFS.getFrom(source, jsonObject);
		return this;
	}

}
