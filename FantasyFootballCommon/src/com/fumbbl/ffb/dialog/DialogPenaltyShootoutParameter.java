package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DialogPenaltyShootoutParameter extends DialogWithoutParameter {

	private List<Integer> homeRolls = new ArrayList<>();
	private List<Integer> awayRolls = new ArrayList<>();
	private List<Boolean> homeWon = new ArrayList<>();
	private int homeScore;
	private int awayScore;

	private boolean homeTeamWins;

	public DialogPenaltyShootoutParameter() {
		super();
	}

	public DialogPenaltyShootoutParameter(List<Integer> homeRolls, List<Integer> awayRolls, List<Boolean> homeWon, int homeScore, int awayScore, boolean homeTeamWins) {
		this.homeRolls = homeRolls;
		this.awayRolls = awayRolls;
		this.homeWon = homeWon;
		this.homeScore = homeScore;
		this.awayScore = awayScore;
		this.homeTeamWins = homeTeamWins;
	}

	public DialogId getId() {
		return DialogId.PENALTY_SHOOTOUT;
	}

	public void addShootout(int home, int away, boolean homeWin) {
		homeRolls.add(home);
		awayRolls.add(away);
		homeWon.add(homeWin);
	}

	public boolean homeTeamWins() {
		return homeTeamWins;
	}

	public List<Integer> getHomeRolls() {
		return homeRolls;
	}

	public List<Integer> getAwayRolls() {
		return awayRolls;
	}

	public List<Boolean> getHomeWon() {
		return homeWon;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}

// transformation

	public IDialogParameter transform() {
		return new DialogPenaltyShootoutParameter(awayRolls, homeRolls, homeWon.stream().map(win -> !win).collect(Collectors.toList()), awayScore, homeScore, !homeTeamWins);
	}

	// JSON serialization

	public DialogPenaltyShootoutParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		homeRolls.addAll(Arrays.stream(IJsonOption.ROLLS_HOME.getFrom(source, jsonObject)).boxed().collect(Collectors.toList()));
		awayRolls.addAll(Arrays.stream(IJsonOption.ROLLS_AWAY.getFrom(source, jsonObject)).boxed().collect(Collectors.toList()));
		boolean[] primitiveWins = IJsonOption.PENALTY_WINS.getFrom(source, jsonObject);
		for (boolean win : primitiveWins) {
			homeWon.add(win);
		}
		homeScore = IJsonOption.PENALTY_SCORE_HOME.getFrom(source, jsonObject);
		awayScore = IJsonOption.PENALTY_SCORE_AWAY.getFrom(source, jsonObject);
		homeTeamWins = IJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ROLLS_HOME.addTo(jsonObject, homeRolls);
		IJsonOption.ROLLS_AWAY.addTo(jsonObject, awayRolls);
		IJsonOption.PENALTY_SCORE_HOME.addTo(jsonObject, homeScore);
		IJsonOption.PENALTY_SCORE_AWAY.addTo(jsonObject, awayScore);
		IJsonOption.PENALTY_WINS.addTo(jsonObject, homeWon);
		IJsonOption.HOME_TEAM.addTo(jsonObject, homeTeamWins);
		return jsonObject;
	}
}
