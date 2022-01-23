package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogConcedeGameParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandConcedeGame;
import com.fumbbl.ffb.net.commands.ClientCommandSetupPlayer;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.report.ReportTimeoutEnforced;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.step.generator.EndGame;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public abstract class AbstractStep implements IStep {

	private final GameState fGameState;
	private StepResult fStepResult;
	private String fLabel;

	public AbstractStep(GameState pGameState) {
		fGameState = pGameState;
		setStepResult(new StepResult());
	}

	public AbstractStep(GameState pGameState, StepAction defaultStepResult) {
		this(pGameState);
		fStepResult.setNextAction(defaultStepResult);
	}

	@Override
	public String getName() {
		return getId().getName();
	}

	public void setLabel(String pLabel) {
		fLabel = pLabel;
	}

	public String getLabel() {
		return fLabel;
	}

	public GameState getGameState() {
		return fGameState;
	}

	private void setStepResult(StepResult pStepResult) {
		fStepResult = pStepResult;
	}

	public StepResult getResult() {
		return fStepResult;
	}

	public void init(StepParameterSet pParameterSet) {
		// do nothing, override in subclass if needed
	}

	public void start() {
		// do nothing, override in subclass if needed
	}

	public void repeat() {
		// This prevents endless loops being caused by incomplete implementations
		getResult().setNextAction(StepAction.CONTINUE);
	}

	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		switch (pReceivedCommand.getId()) {
			case CLIENT_CONCEDE_GAME:
				commandStatus = handleConcedeGame(pReceivedCommand);
				break;
			case CLIENT_ILLEGAL_PROCEDURE:
				commandStatus = handleIllegalProcedure(pReceivedCommand);
				break;
			case CLIENT_SETUP_PLAYER:
				commandStatus = handleSetupCommand(pReceivedCommand);
				break;
			default:
				break;
		}
		return commandStatus;
	}

	private StepCommandStatus handleSetupCommand(ReceivedCommand command) {
		// Network lag during some kick off events (e.g. quick snack 2020) might result in a client sending a setup command
		// even if the allowed amount of players is already exhausted

		// This is a hacky try to fix this. Commands sent during the lag period arrive while we are still in the KICKOFF turn mode.

		Game game = getGameState().getGame();
		if (game.getTurnMode() != TurnMode.KICKOFF) {
			return StepCommandStatus.UNHANDLED_COMMAND;
		}

		// Try to fix the issue by resetting the player position on client side with the server side position
		ClientCommandSetupPlayer commandSetupPlayer = (ClientCommandSetupPlayer) command.getCommand();
		Player<?> player = game.getPlayerById(commandSetupPlayer.getPlayerId());
		if (player != null) {
			game.getFieldModel().sendPosition(player);
		}

		return StepCommandStatus.SKIP_STEP;
	}

	protected StepCommandStatus handleSkillCommand(ClientCommandUseSkill useSkillCommand, Object state) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		Skill usedSkill = useSkillCommand.getSkill();
		if (usedSkill != null) {
			SkillBehaviour<? extends Skill> behaviour = (SkillBehaviour<? extends Skill>) usedSkill.getSkillBehaviour();
			List<StepModifier<? extends IStep, ?>> modifiers = behaviour.getStepModifiers();

			String modifiersString = modifiers.stream().map(modifier -> modifier.getClass().getName()).collect(Collectors.joining(", "));
			getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(),
				"Handle skill command: Modifiers for step " + getName().toUpperCase() + " are: " + modifiersString);

			for (StepModifier<?, ?> modifier : modifiers) {
				if (modifier.appliesTo(this)) {
					StepCommandStatus newStatus = modifier.handleCommand(this, state, useSkillCommand);
					getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(),
						"Handle skill command: Modifier " + modifier.getClass().getName() + " for step " + getName().toUpperCase() + " returns " + (newStatus == null ? "no status" : newStatus.name()));
					if (newStatus != null) {
						commandStatus = newStatus;
					}
				} else {
					getGameState().getServer().getDebugLog().log(IServerLogLevel.DEBUG, getGameState().getGame().getId(),
						"Handle skill command: Modifier " + modifier.getClass().getName() + " does not apply for step " + getName().toUpperCase());
				}
			}
		}
		return commandStatus;
	}

	public boolean setParameter(StepParameter parameter) {
		// do nothing, override in subclass if needed
		return false;
	}

	public void publishParameter(StepParameter pParameter) {
		if (pParameter != null) {
			DebugLog debugLog = getGameState().getServer().getDebugLog();
			if (debugLog.isLogging(IServerLogLevel.TRACE)) {
				String trace = getId() + " publishes " + pParameter.getKey() + "=" +
					pParameter.getValue();
				debugLog.log(IServerLogLevel.TRACE, fGameState.getGame().getId(), trace);
			}
			setParameter(pParameter);
			getGameState().getStepStack().publishStepParameter(pParameter);
		}
	}

	public void publishParameters(StepParameterSet pParameterSet) {
		if (pParameterSet != null) {
			for (StepParameter parameter : pParameterSet.values()) {
				publishParameter(parameter);
			}
		}
	}

	public void consume(StepParameter pParameter) {
		DebugLog debugLog = fGameState.getServer().getDebugLog();
		if (debugLog.isLogging(IServerLogLevel.TRACE)) {
			debugLog.log(IServerLogLevel.TRACE, fGameState.getGame().getId(), getId() + " consumes " + pParameter.getKey() + "=" + pParameter.getValue());
		}
		pParameter.consume();
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.STEP_ID.addTo(jsonObject, getId());
		IServerJsonOption.LABEL.addTo(jsonObject, fLabel);
		IServerJsonOption.STEP_RESULT.addTo(jsonObject, fStepResult.toJsonValue());
		return jsonObject;
	}

	public AbstractStep initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilServerSteps.validateStepId(this, (StepId) IServerJsonOption.STEP_ID.getFrom(source, jsonObject));
		fLabel = IServerJsonOption.LABEL.getFrom(source, jsonObject);
		fStepResult = null;
		JsonObject stepResultObject = IServerJsonOption.STEP_RESULT.getFrom(source, jsonObject);
		if (stepResultObject != null) {
			fStepResult = new StepResult().initFrom(source, stepResultObject);
		}
		return this;
	}

	// Helper methods

	private StepCommandStatus handleConcedeGame(ReceivedCommand pReceivedCommand) {
		ClientCommandConcedeGame concedeGameCommand = (ClientCommandConcedeGame) pReceivedCommand.getCommand();
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		Game game = getGameState().getGame();
		GameResult gameResult = game.getGameResult();
		boolean allowConcessions = ((GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.ALLOW_CONCESSIONS)).isEnabled();
		if (concedeGameCommand.getConcedeGameStatus() != null && allowConcessions) {
			SessionManager sessionManager = getGameState().getServer().getSessionManager();
			boolean homeCommand = (sessionManager.getSessionOfHomeCoach(getGameState().getId()) == pReceivedCommand
				.getSession());
			boolean awayCommand = (sessionManager.getSessionOfAwayCoach(getGameState().getId()) == pReceivedCommand
				.getSession());
			switch (concedeGameCommand.getConcedeGameStatus()) {
				case REQUESTED:
					if (game.isConcessionPossible()
						&& ((game.isHomePlaying() && homeCommand) || (!game.isHomePlaying() && awayCommand))) {
						UtilServerDialog.showDialog(getGameState(), new DialogConcedeGameParameter(), false);
					}
					break;
				case CONFIRMED:
					game.setConcessionPossible(false);
					gameResult.getTeamResultHome().setConceded(game.isHomePlaying() && homeCommand);
					gameResult.getTeamResultAway().setConceded(!game.isHomePlaying() && awayCommand);
					GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
					boolean isLegal = mechanic.isLegalConcession(game, game.getActingTeam());
					game.setConcededLegally(isLegal);
					break;
				case DENIED:
					UtilServerDialog.hideDialog(getGameState());
					break;
			}
			if (gameResult.getTeamResultHome().hasConceded() || gameResult.getTeamResultAway().hasConceded()) {
				getGameState().getStepStack().clear();
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((EndGame) factory.forName(SequenceGenerator.Type.EndGame.name()))
					.pushSequence(new EndGame.SequenceParams(getGameState(), false));
				getResult().setNextAction(StepAction.NEXT_STEP);
			}
			commandStatus = StepCommandStatus.SKIP_STEP;
		}
		return commandStatus;
	}

	private StepCommandStatus handleIllegalProcedure(ReceivedCommand pReceivedCommand) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		Game game = getGameState().getGame();
		if (game.isTimeoutPossible()) {
			ReportList reports = new ReportList();
			FantasyFootballServer server = getGameState().getServer();
			String coach = server.getSessionManager().getCoachForSession(pReceivedCommand.getSession());
			reports.add(new ReportTimeoutEnforced(coach));
			game.setTimeoutEnforced(true);
			game.setTimeoutPossible(false);
			UtilServerGame.syncGameModel(getGameState(), reports, null, SoundId.WHISTLE);
		}
		return commandStatus;
	}

	protected boolean toPrimitive(Boolean bool) {
		return bool != null && bool;
	}
}
