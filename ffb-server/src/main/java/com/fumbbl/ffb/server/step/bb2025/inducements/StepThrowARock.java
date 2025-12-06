package com.fumbbl.ffb.server.step.bb2025.inducements;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2025.ReportThrowAtPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThrowARockStalling;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.DropPlayerContextBuilder;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInducementUse;
import com.fumbbl.ffb.server.util.UtilServerInjury;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepThrowARock extends AbstractStep {
	private boolean homeTeam;

	public StepThrowARock(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.THROW_A_ROCK;
	}


	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			switch (parameter.getKey()) {
				case HOME_TEAM:
					homeTeam = toPrimitive((Boolean) parameter.getValue());
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

		Team team = homeTeam ? game.getTeamAway() : game.getTeamHome();

		List<Player<?>> players = Arrays.stream(team.getPlayers())
			.filter(player -> FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player))
				&& !game.getFieldModel().getPlayerState(player).isProneOrStunned())
			.collect(Collectors.toList());

		if (players.isEmpty()) {
			return;
		}

		InducementTypeFactory factory = game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE);

		Optional<InducementType> type = factory.allTypes().stream().filter(indType -> indType.hasUsage(Usage.THROW_ROCK)).findFirst();

		if (!type.isPresent()) {
			return;
		}

		InducementSet inducementSet = homeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		UtilServerInducementUse.useInducement(type.get(), 1, inducementSet);
		inducementSet.removePrayer(Prayer.THROW_A_ROCK);

		Collections.shuffle(players);
		Player<?> player = players.get(0);

		int roll = getGameState().getDiceRoller().rollDice(6);

		boolean successful = roll >= 4;

		getResult().addReport(new ReportThrowAtPlayer(player.getId(), roll, successful));

		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

		FieldCoordinate startCoordinate;
		if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
			startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 0);
		} else {
			startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 14);
		}

		getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate));
		UtilServerGame.syncGameModel(this);

		if (successful) {
			InjuryResult injuryResult = UtilServerInjury.handleInjury(this,
				new InjuryTypeThrowARockStalling(), null, player, playerCoordinate, null, null, ApothecaryMode.DEFENDER);
			DropPlayerContext dropPlayerContext = DropPlayerContextBuilder.builder().injuryResult(injuryResult).playerId(player.getId()).apothecaryMode(ApothecaryMode.DEFENDER).eligibleForSafePairOfHands(true).build();
			publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT, new SteadyFootingContext(dropPlayerContext)));
		}

	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.HOME_TEAM.addTo(jsonObject, homeTeam);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		homeTeam = IServerJsonOption.HOME_TEAM.getFrom(source, jsonObject);
		return this;
	}
}
