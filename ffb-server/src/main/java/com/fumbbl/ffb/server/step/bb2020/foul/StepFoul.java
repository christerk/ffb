package com.fumbbl.ffb.server.step.bb2020.foul;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.ReportFoul;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeFoul;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeFoulForSpp;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;

/**
 * Step in foul sequence to handle the actual foul.
 * <p>
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepFoul extends AbstractStep {

	private boolean usingChainsaw;

	public StepFoul(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.FOUL;
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			if (parameter.getKey() == StepParameterKey.USING_CHAINSAW) {
				usingChainsaw = parameter.getValue() != null && (boolean) parameter.getValue();
				consume(parameter);
				return true;
			}
		}
		return false;
	}


	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		getResult().addReport(new ReportFoul(game.getDefenderId()));
		if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksLikeChainsaw)) {
			getResult().setSound(SoundId.FOUL);
		}
		UtilServerGame.syncGameModel(this);
		FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		InjuryTypeServer<?> injuryTypeServer = getGameState().getPrayerState().hasFoulingFrenzy(game.getActingTeam())
			? new InjuryTypeFoulForSpp(usingChainsaw) : new InjuryTypeFoul(usingChainsaw);
		InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, injuryTypeServer,
			actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null, null, ApothecaryMode.DEFENDER);

		String chainsawOption = game.getOptions().getOptionWithDefault(GameOptionId.CHAINSAW_TURNOVER).getValueAsString();
		if (injuryResultDefender.injuryContext().isArmorBroken() && GameOptionString.CHAINSAW_TURNOVER_ALL_AV_BREAKS.equalsIgnoreCase(chainsawOption)) {
			publishParameter(StepParameter.from(StepParameterKey.END_TURN, true));
		}

		publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
			new DropPlayerContext(injuryResultDefender, game.getDefenderId(), ApothecaryMode.DEFENDER, true)));
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// ByteArray serialization

	public int getByteArraySerializationVersion() {
		return 1;
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public StepFoul initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		usingChainsaw = IJsonOption.USING_CHAINSAW.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}

}
