package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PrayerFactory;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandSkillSelection;
import com.fumbbl.ffb.report.bb2025.ReportPrayerRoll;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.mixed.PrayerHandlerFactory;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PrayerDialogSelection;
import com.fumbbl.ffb.server.inducements.mixed.prayers.PrayerHandler;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPrayer extends AbstractStep {
	private int roll;
	private String teamId, playerId;
	private boolean firstRun = true;
	private Skill skill;

	public StepPrayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PRAYER;
	}

	@Override
	public void init(StepParameterSet parameterSet) {
		if (parameterSet != null) {
			for (StepParameter parameter : parameterSet.values()) {
				switch (parameter.getKey()) {
					case PRAYER_ROLL:
						roll = (int) parameter.getValue();
						break;
					case TEAM_ID:
						teamId = (String) parameter.getValue();
						break;
					default:
						break;
				}
			}
		}
		super.init(parameterSet);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus status = super.handleCommand(pReceivedCommand);
		if (status.equals(StepCommandStatus.UNHANDLED_COMMAND)) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_PRAYER_SELECTION:
					ClientCommandSkillSelection clientCommandPrayerSelection = (ClientCommandSkillSelection) pReceivedCommand.getCommand();
					playerId = clientCommandPrayerSelection.getPlayerId();
					skill = clientCommandPrayerSelection.getSkill();
					status = StepCommandStatus.EXECUTE_STEP;
					break;
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice clientCommandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					playerId = clientCommandPlayerChoice.getPlayerId();
					status = StepCommandStatus.EXECUTE_STEP;
					break;
				default:
					break;
			}
		}

		if (status.equals(StepCommandStatus.EXECUTE_STEP)) {
			executeStep();
		}

		return status;
	}

	@Override
	public void start() {
		executeStep();
	}

	private void executeStep() {
		UtilServerDialog.hideDialog(getGameState());
		PrayerFactory prayerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER);
		PrayerHandlerFactory handlerFactory = getGameState().getGame().getFactory(FactoryType.Factory.PRAYER_HANDLER);
		Optional<PrayerHandler> prayerHandler = handlerFactory.forPrayer(prayerFactory.forRoll(roll));

		if (firstRun) {
			firstRun = false;
			Game game = getGameState().getGame();
			String teamName = game.getTeamById(teamId).getName();
			getResult().addReport(new ReportPrayerRoll(teamName, roll, game.getTeamHome().getId().equals(teamId)));

			if (prayerHandler.isPresent()) {
				prayerHandler.get().initEffect(this, getGameState(), teamId);
			} else {
				getResult().setNextAction(StepAction.NEXT_STEP);
			}

		} else {
			prayerHandler.ifPresent(handler -> handler.applySelection(this, getGameState(), new PrayerDialogSelection(playerId, skill)));

			getResult().setNextAction(StepAction.NEXT_STEP);
		}


	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.ROLL.addTo(jsonObject, roll);
		IServerJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
		IServerJsonOption.TEAM_ID.addTo(jsonObject, teamId);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.SKILL.addTo(jsonObject, skill);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		roll = IServerJsonOption.ROLL.getFrom(source, jsonObject);
		firstRun = IServerJsonOption.FIRST_RUN.getFrom(source, jsonObject);
		teamId = IServerJsonOption.TEAM_ID.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		return this;
	}
}
