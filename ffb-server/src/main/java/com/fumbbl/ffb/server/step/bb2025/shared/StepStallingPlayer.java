package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.mixed.ReportPlayerEvent;
import com.fumbbl.ffb.report.mixed.ReportThrowAtStallingPlayer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeThrowARockStalling;
import com.fumbbl.ffb.server.model.SteadyFootingContext;
import com.fumbbl.ffb.server.step.*;
import com.fumbbl.ffb.server.step.bb2025.command.DropPlayerCommand;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepStallingPlayer extends AbstractStep {

	private static final Set<PlayerAction> PREVENT_STALLING_ACTION = new HashSet<PlayerAction>() {{
		add(PlayerAction.PASS_MOVE);
		add(PlayerAction.HAND_OVER_MOVE);
	}};

	private String gotoLabelOnEnd;

	public StepStallingPlayer(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.STALLING_PLAYER;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		for (StepParameter parameter : pParameterSet.values()) {
			if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
				gotoLabelOnEnd = (String) parameter.getValue();
			}
		}
	}

	@Override
	public void start() {

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		PlayerAction playerAction = actingPlayer.getPlayerAction();
		boolean gotRid = gotRidOfBall(playerAction, game);
		boolean scored = UtilServerSteps.checkTouchdown(getGameState());
		boolean noStalling = !getGameState().isStalling() || gotRid || scored;


		getGameState().resetStalling();

		if (noStalling || game.getFieldModel().getPlayerState(player).isProneOrStunned()) {
			getResult().setNextAction(StepAction.NEXT_STEP);
			if (gotRid || scored) {
				getResult().addReport(new ReportPlayerEvent(player.getId(), "did not stall after all"));
			}
			return;
		}


		int roll = getGameState().getDiceRoller().rollDice(6);

		boolean successful = roll >= game.getTurnData().getTurnNr();

		getResult().addReport(new ReportThrowAtStallingPlayer(player.getId(), roll, successful));

		if (successful) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

			FieldCoordinate startCoordinate;
			if (FieldCoordinateBounds.UPPER_HALF.isInBounds(playerCoordinate)) {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 0);
			} else {
				startCoordinate = new FieldCoordinate(getGameState().getDiceRoller().rollXCoordinate(), 14);
			}

			getResult().setAnimation(new Animation(AnimationType.THROW_A_ROCK, startCoordinate, playerCoordinate));
			UtilServerGame.syncGameModel(this);

			InjuryResult injuryResult = UtilServerInjury.handleInjury(this,
				new InjuryTypeThrowARockStalling(), null, player, playerCoordinate, null, null, ApothecaryMode.HIT_PLAYER);
			publishParameter(new StepParameter(StepParameterKey.STEADY_FOOTING_CONTEXT,
				new SteadyFootingContext(injuryResult, Collections.singletonList(new DropPlayerCommand(player.getId(),
					ApothecaryMode.HIT_PLAYER, true)))));

			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
		}

	}

	private boolean gotRidOfBall(PlayerAction playerAction, Game game) {
		return PREVENT_STALLING_ACTION.contains(playerAction) &&
			!UtilPlayer.hasBall(game, game.getActingPlayer().getPlayer());
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
