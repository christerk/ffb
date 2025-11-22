package com.fumbbl.ffb.server.step.bb2020.multiblock;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandPlayerChoice;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.mixed.SingleReRollUseState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.HasIdForSingleUseReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractStepMultiple extends AbstractStep implements HasIdForSingleUseReRoll {

	public AbstractStepMultiple(GameState pGameState) {
		super(pGameState);
	}

	public AbstractStepMultiple(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	protected abstract SingleReRollUseState state();

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getId() == NetCommandId.CLIENT_PLAYER_CHOICE) {
				ClientCommandPlayerChoice commandPlayerChoice = (ClientCommandPlayerChoice) pReceivedCommand.getCommand();
				if (commandPlayerChoice.getPlayerChoiceMode() == PlayerChoiceMode.LORD_OF_CHAOS) {
					state().setId(commandPlayerChoice.getPlayerId());
					commandStatus = StepCommandStatus.EXECUTE_STEP;
				}
			}
		}

		return commandStatus;
	}

	@Override
	public String idForSingleUseReRoll() {
		return state().getId();
	}

	protected boolean reRollSourceSuccessfully(ReRollSource reRollSource) {
		if (reRollSource == ReRollSources.LORD_OF_CHAOS) {
			state().setReRollSource(reRollSource);
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
				state().setId(lords.get(0));
			}

			return true;

		} else {
			state().setReRollSource(reRollSource);
			return true;
		}
	}

}
