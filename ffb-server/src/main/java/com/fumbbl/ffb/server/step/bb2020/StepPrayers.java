package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PrayerFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
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

import java.util.Collections;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPrayers extends AbstractStep {
	private int tvHome, tvAway;

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
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {

		Game game = getGameState().getGame();
		int maxPrayers = ((GameOptionInt) game.getOptions().getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_MAX)).getValue();
		int prayerAmount = Math.abs(tvAway - tvHome) / ((GameOptionInt) game.getOptions().getOptionWithDefault(GameOptionId.INDUCEMENT_PRAYERS_COST)).getValue();

		if (maxPrayers > 0) {
			prayerAmount = Math.min(maxPrayers, prayerAmount);
		}

		if (prayerAmount > 0) {
			boolean homeTeamReceivesPrayers = tvHome < tvAway;
			PrayerFactory prayerFactory = game.getFactory(FactoryType.Factory.PRAYER);
			List<Integer> availablePrayerRolls;
			if (homeTeamReceivesPrayers) {
				availablePrayerRolls = prayerFactory.availablePrayerRolls(game.getTurnDataHome().getInducementSet(), game.getTurnDataAway().getInducementSet());
			} else {
				availablePrayerRolls = prayerFactory.availablePrayerRolls(game.getTurnDataAway().getInducementSet(), game.getTurnDataHome().getInducementSet());
			}
			prayerAmount = Math.min(prayerAmount, availablePrayerRolls.size());
			getResult().addReport(new ReportPrayerAmount(tvHome, tvAway, prayerAmount, homeTeamReceivesPrayers));

			Team prayingTeam = homeTeamReceivesPrayers ? game.getTeamHome() : game.getTeamAway();

			Sequence sequence = new Sequence(getGameState());
			while (prayerAmount-- > 0) {
				Collections.shuffle(availablePrayerRolls);
				int roll = availablePrayerRolls.remove(0);
				sequence.add(StepId.PRAYER,
					StepParameter.from(StepParameterKey.PRAYER_ROLL, roll),
					StepParameter.from(StepParameterKey.TEAM_ID, prayingTeam.getId())
				);
			}

			getGameState().getStepStack().push(sequence.getSequence());
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.TEAM_VALUE.addTo(jsonObject, tvHome);
		IServerJsonOption.OPPONENT_TEAM_VALUE.addTo(jsonObject, tvAway);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		tvHome = IServerJsonOption.TEAM_VALUE.getFrom(source, jsonObject);
		tvAway = IServerJsonOption.OPPONENT_TEAM_VALUE.getFrom(source, jsonObject);
		return this;
	}
}
