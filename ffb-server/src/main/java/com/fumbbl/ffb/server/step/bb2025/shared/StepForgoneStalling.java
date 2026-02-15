package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepForgoneStalling extends AbstractStep {

	private final StallingExtension stallingExtension = new StallingExtension();

	private boolean checkForgo;

	public StepForgoneStalling(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.FORGONE_STALLING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		for (StepParameter parameter : pParameterSet.values()) {
			switch (parameter.getKey()) {
				case CHECK_FORGO:
					checkForgo = parameter.getValue() != null && (boolean) parameter.getValue();
					break;
				default:
					break;
			}
		}
	}


	@Override
	public void start() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		Game game = getGameState().getGame();
		if (game.getTurnMode() == TurnMode.REGULAR && checkForgo &&
			UtilGameOption.isOptionEnabled(game, GameOptionId.ENABLE_STALLING_CHECK)) {
			Arrays.stream(game.getActingTeam().getPlayers()).filter(pl -> UtilPlayer.hasBall(game, pl) &&
				game.getFieldModel().getPlayerState(pl).isActive()).findFirst().ifPresent(
				player -> {
					if (stallingExtension.isConsideredStalling(game, player)) {
						getResult().addReport(new ReportPlayerEvent(player.getId(), "is stalling"));
						stallingExtension.handleStaller(this, player);
					}
				}
			);
		}
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();

		IServerJsonOption.CHECK_FORGO.addTo(jsonObject, checkForgo);

		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);

		checkForgo = IServerJsonOption.CHECK_FORGO.getFrom(source, UtilJson.toJsonObject(jsonValue));

		return this;
	}
}
