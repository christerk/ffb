package com.fumbbl.ffb.server.step.bb2025;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.mixed.ReportSkillWasted;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeStab;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Optional;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepTreacherous extends AbstractStep {

	private boolean endPlayerAction, endTurn;
	private String goToLabelOnFailure;

	public StepTreacherous(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.TREACHEROUS;
	}

	@Override
	public void init(StepParameterSet pParameterSet) {
		super.init(pParameterSet);
		if (pParameterSet != null) {
			Arrays.stream(pParameterSet.values()).forEach(parameter -> {
				if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_FAILURE) {
					goToLabelOnFailure = (String) parameter.getValue();
				}
			});
		}
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case END_TURN:
					endTurn = toPrimitive((Boolean) parameter.getValue());
					return true;
				case END_PLAYER_ACTION:
					endPlayerAction = toPrimitive((Boolean) parameter.getValue());
					return true;
				default:
					break;
			}
		}

		return super.setParameter(parameter);
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {

		getResult().setNextAction(StepAction.NEXT_STEP);

		Game game = getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		Skill skill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canStabTeamMateForBall);
		if (skill != null) {

			markActionUsed(game, actingPlayer);

			if (endTurn || endPlayerAction) {
				getResult().addReport(new ReportSkillWasted(actingPlayer.getPlayerId(), skill));
				getResult().setNextAction(StepAction.GOTO_LABEL, goToLabelOnFailure);
				actingPlayer.markSkillUsed(skill);
				return;
			}

			treacherousTarget(game, actingPlayer).ifPresent(player -> {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				game.getFieldModel().setBallCoordinate(playerCoordinate);
				getResult().addReport(new ReportSkillUse(actingPlayer.getPlayerId(), skill, true, SkillUse.TREACHEROUS));

				getResult().setSound(SoundId.STAB);
				FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				InjuryResult injuryResultDefender = UtilServerInjury.handleInjury(this, new InjuryTypeStab(true, true),
					actingPlayer.getPlayer(), player, defenderCoordinate, null, null, ApothecaryMode.DEFENDER);

				publishParameter(new StepParameter(StepParameterKey.DROP_PLAYER_CONTEXT,
					new DropPlayerContext(injuryResultDefender, false, false, null,
						player.getId(), ApothecaryMode.DEFENDER, false)));
			});

			actingPlayer.markSkillUsed(skill);
		}
	}

	private void markActionUsed(Game game, ActingPlayer actingPlayer) {
		switch (actingPlayer.getPlayerAction()) {
			case BLITZ:
			case BLITZ_MOVE:
			case KICK_EM_BLITZ:
				game.getTurnData().setBlitzUsed(true);
				break;
			case KICK_TEAM_MATE:
			case KICK_TEAM_MATE_MOVE:
				game.getTurnData().setKtmUsed(true);
				break;
			case PASS:
			case PASS_MOVE:
				game.getTurnData().setPassUsed(true);
				break;
			case THROW_TEAM_MATE:
			case THROW_TEAM_MATE_MOVE:
				game.getTurnData().setTtmUsed(true);
				break;
			case HAND_OVER:
			case HAND_OVER_MOVE:
				game.getTurnData().setHandOverUsed(true);
				break;
			case FOUL:
			case FOUL_MOVE:
				if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.allowsAdditionalFoul)) {
					game.getTurnData().setFoulUsed(true);
				}
				break;
			case PUNT:
			case PUNT_MOVE:
				game.getTurnData().setPuntUsed(true);
				break;
			default:
				break;
		}
	}

	private Optional<Player<?>> treacherousTarget(Game game, ActingPlayer actingPlayer) {
		if (!actingPlayer.hasActedIgnoringNegativeTraits() || actingPlayer.justStoodUp()) {
			return Arrays.stream(UtilPlayer.findAdjacentBlockablePlayers(game, game.getActingTeam(), game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())))
				.filter(adjacentPlayer -> UtilPlayer.hasBall(game, adjacentPlayer)).findFirst();
		}
		return Optional.empty();
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		return jsonObject;
	}

	@Override
	public AbstractStep initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		return this;
	}
}
