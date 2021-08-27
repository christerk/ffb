package com.fumbbl.ffb.server.step.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PathFinderWithPassBlockSupport;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.bb2020.ReportStallerDetected;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepCheckStalling extends AbstractStep {
	private boolean ignoreActedFlag = true;

	private final Set<ISkillProperty> rollAtActivation = new HashSet<ISkillProperty>() {{
		add(NamedProperties.appliesConfusion);
		add(NamedProperties.needsToRollForActionBlockingIsEasier);
		add(NamedProperties.needsToRollForActionButKeepsTacklezone);
		add(NamedProperties.becomesImmovable);
	}};

	public StepCheckStalling(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.CHECK_STALLING;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			if (parameter.getKey() == StepParameterKey.IGNORE_ACTED_FLAG) {
				ignoreActedFlag = (boolean) parameter.getValue();
			}
		}
	}

	@Override
	public void start() {
		if (performCheck()) {
			Player<?> stallingPlayer = findStallingPlayer();
			if (stallingPlayer != null) {
				getResult().addReport(new ReportStallerDetected(stallingPlayer.getId()));
				getGameState().getPrayerState().addStaller(stallingPlayer);
			}
		}

		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	private Player<?> findStallingPlayer() {
		Player<?> player = findStallingSuspect();

		return player != null && isConsideredStalling(getGameState().getGame(), player) ? player : null;
	}

	private boolean isConsideredStalling(Game game, Player<?> player) {
		return game.getActingPlayer().getPlayer() != player
			&& player.getSkillsIncludingTemporaryOnes().stream().flatMap(skill -> skill.getSkillProperties().stream())
			.noneMatch(rollAtActivation::contains)
			&& game.getFieldModel().getPlayerState(player).isActive()
			&& !ArrayTool.isProvided(UtilPlayer.findAdjacentPlayersWithTacklezones(game, game.getOtherTeam(player.getTeam()), game.getFieldModel().getPlayerCoordinate(player), false))
			&& hasOpenPathToEndzone(game, player);
	}

	private boolean hasOpenPathToEndzone(Game game, Player<?> player) {
		FieldCoordinateBounds endzoneBounds = game.getTeamHome().hasPlayer(player) ? FieldCoordinateBounds.ENDZONE_AWAY : FieldCoordinateBounds.ENDZONE_HOME;

		Set<FieldCoordinate> endZoneCoordinates = Arrays.stream(endzoneBounds.fieldCoordinates()).collect(Collectors.toSet());

		return ArrayTool.isProvided(PathFinderWithPassBlockSupport.getShortestPath(game, endZoneCoordinates, player, 0));
	}

	private Player<?> findStallingSuspect() {
		Game game = getGameState().getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> ballCarrier = fieldModel.getPlayer(fieldModel.getBallCoordinate());

		if (UtilPlayer.hasBall(game, ballCarrier)
			&& game.getActingTeam().hasPlayer(ballCarrier)
			&& !getGameState().getPrayerState().isStalling(ballCarrier)) {
			return ballCarrier;
		}

		return null;
	}

	private boolean performCheck() {
		return ((GameOptionBoolean) getGameState().getGame().getOptions().getOptionWithDefault(GameOptionId.ENABLE_STALLING_CHECK)).isEnabled()
			&& getGameState().getPrayerState().shouldNotStall(getGameState().getGame().getActingTeam())
			&& (ignoreActedFlag || getGameState().getGame().getActingPlayer().hasActed());
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.IGNORE_ACTED_FLAG.addTo(jsonObject, ignoreActedFlag);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		ignoreActedFlag = IServerJsonOption.IGNORE_ACTED_FLAG.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}
