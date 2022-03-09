package com.fumbbl.ffb.server.step.bb2020.move;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TrackNumber;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.ServerUtilBlock;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Optional;

/**
 * Step in move sequence to update player position (actually move).
 * <p>
 * Expects stepParameter COORDINATE_FROM to be set by a preceding step. Expects
 * stepParameter COORDINATE_TO to be set by a preceding step. Expects
 * stepParameter MOVE_STACK to be set by a preceding step.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class StepMove extends AbstractStep {

	private FieldCoordinate fCoordinateFrom;
	private FieldCoordinate fCoordinateTo;
	private int fMoveStackSize;

	public StepMove(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.MOVE;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if ((parameter != null) && !super.setParameter(parameter)) {
			switch (parameter.getKey()) {
				case COORDINATE_FROM:
					fCoordinateFrom = (FieldCoordinate) parameter.getValue();
					return true;
				case COORDINATE_TO:
					fCoordinateTo = (FieldCoordinate) parameter.getValue();
					return true;
				case MOVE_STACK:
					FieldCoordinate[] moveStack = (FieldCoordinate[]) parameter.getValue();
					fMoveStackSize = ((moveStack != null) ? moveStack.length : 0);
					return true;
				default:
					break;
			}
		}
		return false;
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

	private void executeStep() {
		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		PlayerState playerState = game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer());
		if (!playerState.isRooted()) {
			TrackNumber trackNumber = new TrackNumber(fCoordinateFrom, actingPlayer.getCurrentMove());
			actingPlayer.setCurrentMove(game.getActingPlayer().getCurrentMove() + (actingPlayer.isJumping() ? 2 : 1));
			int possibleFreeRushes = 2;
			if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canMakeAnExtraGfi)) {
				possibleFreeRushes++;
			}

			Optional<Skill> extraGfiOnceSkill = UtilCards.getSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canMakeAnExtraGfiOnce);

			if (actingPlayer.getCurrentMove() > actingPlayer.getPlayer().getMovementWithModifiers() + possibleFreeRushes && extraGfiOnceSkill.isPresent()) {
				actingPlayer.markSkillUsed(extraGfiOnceSkill.get());
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), extraGfiOnceSkill.get(), true, SkillUse.RUSH_ADDITIONAL_SQUARE_ONCE));
			}

			game.getFieldModel().add(trackNumber);
			boolean ballPositionUpdated = game.getFieldModel().updatePlayerAndBallPosition(actingPlayer.getPlayer(),
				fCoordinateTo);
			if (ballPositionUpdated) {
				PlayerResult playerResult = game.getGameResult().getPlayerResult(game.getActingPlayer().getPlayer());
				int deltaX = 0;
				if (game.isHomePlaying()) {
					deltaX = fCoordinateTo.getX() - fCoordinateFrom.getX();
				} else {
					deltaX = fCoordinateFrom.getX() - fCoordinateTo.getX();
				}
				playerResult.setRushing(playerResult.getRushing() + deltaX);
			}
			actingPlayer.setGoingForIt(UtilPlayer.isNextMoveGoingForIt(game)); // auto go-for-it
			if (fMoveStackSize == 0) {
				UtilServerPlayerMove.updateMoveSquares(getGameState(), false);
			}
			ServerUtilBlock.updateDiceDecorations(game);
			getResult().setSound(actingPlayer.isDodging() ? SoundId.DODGE : SoundId.STEP);
			publishParameter(StepParameter.from(StepParameterKey.PLAYER_ENTERING_SQUARE, actingPlayer.getPlayerId()));
		}
		getResult().setNextAction(StepAction.NEXT_STEP);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.COORDINATE_FROM.addTo(jsonObject, fCoordinateFrom);
		IServerJsonOption.COORDINATE_TO.addTo(jsonObject, fCoordinateTo);
		IServerJsonOption.MOVE_STACK_SIZE.addTo(jsonObject, fMoveStackSize);
		return jsonObject;
	}

	@Override
	public StepMove initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fCoordinateFrom = IServerJsonOption.COORDINATE_FROM.getFrom(game, jsonObject);
		fCoordinateTo = IServerJsonOption.COORDINATE_TO.getFrom(game, jsonObject);
		fMoveStackSize = IServerJsonOption.MOVE_STACK_SIZE.getFrom(game, jsonObject);
		return this;
	}

}
