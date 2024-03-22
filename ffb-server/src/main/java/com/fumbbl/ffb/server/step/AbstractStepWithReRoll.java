package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseReRoll;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public abstract class AbstractStepWithReRoll extends AbstractStep implements HasIdForSingleUseReRoll {

	private ReRolledAction fReRolledAction;
	private ReRollSource fReRollSource;
	private String playerIdForSingleUseReRoll;

	public AbstractStepWithReRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pReceivedCommand.getId()) {
				case CLIENT_USE_RE_ROLL:
					ClientCommandUseReRoll useReRollCommand = (ClientCommandUseReRoll) pReceivedCommand.getCommand();
					setReRolledAction(useReRollCommand.getReRolledAction());
					if (reRollSourceSuccessfully(useReRollCommand.getReRollSource())) {
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_PLAYER_CHOICE:
					ClientCommandPlayerChoice commandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
					if (commandPlayerChoice.getPlayerChoiceMode() == PlayerChoiceMode.LORD_OF_CHAOS) {
						playerIdForSingleUseReRoll = commandPlayerChoice.getPlayerId();
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				case CLIENT_USE_SKILL:
					ClientCommandUseSkill commandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
					if (commandUseSkill.isSkillUsed() && commandUseSkill.getSkill().hasSkillProperty(NamedProperties.canRerollSingleDieOncePerPeriod)) {
						setReRolledAction(commandUseSkill.getReRolledAction());
						setReRollSource(commandUseSkill.getSkill().getRerollSource(ReRolledActions.SINGLE_DIE));
						commandStatus = StepCommandStatus.EXECUTE_STEP;
					}
					break;
				default:
					break;
			}
		}
		return commandStatus;
	}

	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}

	public void setReRolledAction(ReRolledAction pReRolledAction) {
		fReRolledAction = pReRolledAction;
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	public void setReRollSource(ReRollSource pReRollSource) {
		fReRollSource = pReRollSource;
	}

	public String idForSingleUseReRoll() {
		return playerIdForSingleUseReRoll;
	}

	private boolean reRollSourceSuccessfully(ReRollSource reRollSource) {
		if (reRollSource == ReRollSources.LORD_OF_CHAOS) {
			setReRollSource(reRollSource);
			List<String> lords = Arrays.stream(getGameState().getGame().getActingTeam().getPlayers())
				.filter(player -> UtilCards.hasUnusedSkillWithProperty(player, NamedProperties.grantsSingleUseTeamRerollWhenOnPitch))
				.map(Player::getId)
				.collect(Collectors.toList());

			if (lords.size() > 1) {
				UtilServerDialog.showDialog(getGameState(),
					new DialogPlayerChoiceParameter(getGameState().getGame().getActingTeam().getId(), PlayerChoiceMode.LORD_OF_CHAOS,
						lords.toArray(new String[0]), null, 1, 1), false);
				return false;
			}

			if (lords.size() == 1) {
				playerIdForSingleUseReRoll = lords.get(0);
			}

			return true;

		} else {
			setReRollSource(reRollSource);
			return true;
		}
	}

	protected ReRollSource findSkillReRollSource(ReRolledAction reRolledAction) {
		Game game = getGameState().getGame();
		ReRollSource skillRerollSource = null;
		if (TurnMode.REGULAR == game.getTurnMode()) {
			skillRerollSource = UtilCards.getUnusedRerollSource(game.getActingPlayer(), reRolledAction);
		}
		return skillRerollSource;
	}

// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
		IServerJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
		IServerJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.addTo(jsonObject, playerIdForSingleUseReRoll);
		return jsonObject;
	}

	@Override
	public AbstractStepWithReRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fReRolledAction = (ReRolledAction) IServerJsonOption.RE_ROLLED_ACTION.getFrom(source, jsonObject);
		fReRollSource = (ReRollSource) IServerJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
		playerIdForSingleUseReRoll = IServerJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.getFrom(source, jsonObject);
		return this;
	}

}
