package com.fumbbl.ffb.server.step.bb2025.start;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PrayerFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.report.mixed.ReportPrayerAmount;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPrayers extends AbstractStep {
	private int tvHome, tvAway, prayersBoughtHome, prayersBoughtAway;

	public StepPrayers(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PRAYERS;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case TV_AWAY:
					tvAway = (int) parameter.getValue();
					consume(parameter);
					return true;
				case TV_HOME:
					tvHome = (int) parameter.getValue();
					consume(parameter);
					return true;
				case PRAYERS_BOUGHT_HOME:
					prayersBoughtHome = (int) parameter.getValue();
					consume(parameter);
					return true;
				case PRAYERS_BOUGHT_AWAY:
					prayersBoughtAway = (int) parameter.getValue();
					consume(parameter);
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {

		Game game = getGameState().getGame();

		int additionalPrayerAmount = Math.abs(tvAway - tvHome) /
			((GameOptionInt) game.getOptions().getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_COST)).getValue();
		boolean addPrayersToUnderdog = ((GameOptionBoolean) getGameState().getGame().getOptions()
			.getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_AVAILABLE_FOR_UNDERDOG)).isEnabled();

		boolean homeTeamAdditionalReceivesPrayers = tvHome < tvAway;

		int prayersTotalHome = prayersBoughtHome;
		int prayersTotalAway = prayersBoughtAway;

		PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
		List<Integer> availablePrayerRolls = prayerFactory.allPrayerRolls();

		if (addPrayersToUnderdog && additionalPrayerAmount > 0) {
			int alreadyBoughtPrayers;
			if (homeTeamAdditionalReceivesPrayers) {
				alreadyBoughtPrayers = prayersBoughtHome;
			} else {
				alreadyBoughtPrayers = prayersBoughtAway;
			}
			additionalPrayerAmount = Math.min(additionalPrayerAmount, availablePrayerRolls.size() - alreadyBoughtPrayers);
			getResult().addReport(
				new ReportPrayerAmount(tvHome, tvAway, additionalPrayerAmount, homeTeamAdditionalReceivesPrayers));

			if (homeTeamAdditionalReceivesPrayers) {
				prayersTotalHome += additionalPrayerAmount;
			} else {
				prayersTotalAway += additionalPrayerAmount;
			}
		}

		if (prayersTotalAway + prayersTotalHome > 0) {
			Sequence sequence = new Sequence(getGameState());
			addPrayerSequences(sequence, game.getTeamHome(), prayersTotalHome, new ArrayList<>(availablePrayerRolls));
			addPrayerSequences(sequence, game.getTeamAway(), prayersTotalAway, new ArrayList<>(availablePrayerRolls));
			getGameState().getStepStack().push(sequence.getSequence());
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private void addPrayerSequences(Sequence sequence, Team team, int prayerAmount, List<Integer> availablePrayerRolls) {
		while (prayerAmount-- > 0) {
			Collections.shuffle(availablePrayerRolls);
			int roll = 4;//availablePrayerRolls.remove(0);
			sequence.add(StepId.PRAYER,
				StepParameter.from(StepParameterKey.PRAYER_ROLL, roll),
				StepParameter.from(StepParameterKey.TEAM_ID, team.getId())
			);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TEAM_VALUE.addTo(jsonObject, tvHome);
		IServerJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, tvAway);
		IServerJsonOption.PRAYERS_BOUGHT_HOME.addTo(jsonObject, prayersBoughtHome);
		IServerJsonOption.PRAYERS_BOUGHT_AWAY.addTo(jsonObject, prayersBoughtAway);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		tvHome = IServerJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		tvAway = IServerJsonOption.OPPONENT_TEAM_VALUE.getFrom(source, jsonObject);
		prayersBoughtHome = IServerJsonOption.PRAYERS_BOUGHT_HOME.getFrom(source, jsonObject);
		prayersBoughtAway = IServerJsonOption.PRAYERS_BOUGHT_AWAY.getFrom(source, jsonObject);
		return this;
	}
}
