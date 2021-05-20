package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.dialog.DialogConcedeGameParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.ClientCommandConcedeGame;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
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
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.EndGame;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;

/**
 * 
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
		// System.out.println("setLabel(" + pLabel + ")");
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
		default:
			break;
		}
		return commandStatus;
	}

	protected StepCommandStatus handleSkillCommand(ClientCommandUseSkill useSkillCommand, Object state) {
		StepCommandStatus commandStatus = StepCommandStatus.UNHANDLED_COMMAND;
		Skill usedSkill = useSkillCommand.getSkill();
		if (usedSkill != null) {
			SkillBehaviour<? extends Skill> behaviour = (SkillBehaviour<? extends Skill>) usedSkill.getSkillBehaviour();
			for (StepModifier<?, ?> modifier : behaviour.getStepModifiers()) {
				if (modifier.appliesTo(this)) {
					StepCommandStatus newStatus = modifier.handleCommand(this, state, useSkillCommand);
					if (newStatus != null) {
						commandStatus = newStatus;
					}
				}
			}
		}
		return commandStatus;
	}

	public boolean setParameter(StepParameter pParameter) {
		// do nothing, override in subclass if needed
		return false;
	}

	public void publishParameter(StepParameter pParameter) {
		if (pParameter != null) {
			DebugLog debugLog = getGameState().getServer().getDebugLog();
			if (debugLog.isLogging(IServerLogLevel.TRACE)) {
				String trace = getId() + " publishes " + pParameter.getKey() + "=" +
					pParameter.getValue();
				debugLog.log(IServerLogLevel.TRACE, trace);
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
			debugLog.log(IServerLogLevel.TRACE, getId() + " consumes " + pParameter.getKey() + "=" + pParameter.getValue());
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
		if (concedeGameCommand.getConcedeGameStatus() != null) {
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
				break;
			case DENIED:
				UtilServerDialog.hideDialog(getGameState());
				break;
			}
			if (gameResult.getTeamResultHome().hasConceded() || gameResult.getTeamResultAway().hasConceded()) {
				getGameState().getStepStack().clear();
				SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
				((EndGame)factory.forName(SequenceGenerator.Type.EndGame.name()))
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

}
