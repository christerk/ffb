package com.fumbbl.ffb.server.step.bb2020.gaze;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.dialog.DialogSelectBlitzTargetParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.net.commands.ClientCommandTargetSelected;
import com.fumbbl.ffb.report.ReportSelectBlitzTarget;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerDialog;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepSelectGazeTarget extends AbstractStep {

    private String gotoLabelOnEnd;
    private String selectedPlayerId;
    private boolean endPlayerAction, endTurn;

    public StepSelectGazeTarget(GameState pGameState) {
        super(pGameState);
    }

    public StepSelectGazeTarget(GameState pGameState, StepAction defaultStepResult) {
        super(pGameState, defaultStepResult);
    }

    @Override
    public StepId getId() {
        return StepId.SELECT_GAZE_TARGET;
    }

    @Override
    public void init(StepParameterSet pParameterSet) {
        if (pParameterSet != null) {
            super.init(pParameterSet);
            for (StepParameter parameter : pParameterSet.values()) {
                if (parameter.getKey() == StepParameterKey.GOTO_LABEL_ON_END) {
                    gotoLabelOnEnd = (String) parameter.getValue();
                }
            }
        }
    }

    @Override
    public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
        StepCommandStatus status = super.handleCommand(pReceivedCommand);
        if (status == StepCommandStatus.UNHANDLED_COMMAND) {
            switch (pReceivedCommand.getId()) {
                case CLIENT_TARGET_SELECTED:
                    selectedPlayerId = ((ClientCommandTargetSelected) pReceivedCommand.getCommand()).getTargetPlayerId();
                    status = StepCommandStatus.EXECUTE_STEP;
                    break;
                case CLIENT_END_TURN:
                    if (UtilServerSteps.checkCommandIsFromCurrentPlayer(getGameState(), pReceivedCommand)) {
                        endTurn = true;
                        status = StepCommandStatus.EXECUTE_STEP;
                    }
                    break;
                default:
                    break;
            }
        }
        if (status == StepCommandStatus.EXECUTE_STEP) {
            executeStep();
        }
        return status;
    }

    @Override
    public boolean setParameter(StepParameter pParameter) {
        if ((pParameter != null) && !super.setParameter(pParameter)) {
            if (pParameter.getKey() == StepParameterKey.END_PLAYER_ACTION) {
                endPlayerAction = (pParameter.getValue() != null) ? (Boolean) pParameter.getValue() : false;
                consume(pParameter);
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        executeStep();
    }

    private void executeStep() {
        Game game = getGameState().getGame();
        if (endPlayerAction || endTurn) {
            game.setTurnMode(game.getLastTurnMode());
            SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
            EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
            endGenerator.pushSequence(new EndPlayerAction.SequenceParams(getGameState(), false, true, endTurn));
            getResult().setNextAction(StepAction.NEXT_STEP);
        } else if (selectedPlayerId == null) {
            game.setTurnMode(TurnMode.SELECT_GAZE_TARGET);
            UtilServerDialog.showDialog(getGameState(), new DialogSelectBlitzTargetParameter(), false);
            getResult().setSound(SoundId.CLICK);
        } else {
            game.setTurnMode(game.getLastTurnMode());
            if (selectedPlayerId.equals(game.getActingPlayer().getPlayerId())) {
                game.getFieldModel().setTargetSelectionState(new TargetSelectionState().cancel());
                getResult().setNextAction(StepAction.GOTO_LABEL, gotoLabelOnEnd);
            } else if (!game.getActingTeam().hasPlayer(game.getPlayerById(selectedPlayerId))) {
                Player<?> targetPlayer = game.getPlayerById(selectedPlayerId);
                PlayerState newState = game.getFieldModel().getPlayerState(targetPlayer).changeSelectedGazeTarget(true);
                game.getFieldModel().setPlayerState(targetPlayer, newState);
                game.getFieldModel().setTargetSelectionState(new TargetSelectionState(selectedPlayerId).select());
                getResult().setSound(SoundId.CLICK);
                getResult().addReport(new ReportSelectBlitzTarget(game.getActingPlayer().getPlayerId(), selectedPlayerId));
                getResult().setNextAction(StepAction.NEXT_STEP);
            } else {
                getResult().setNextAction(StepAction.NEXT_STEP);
            }

        }
    }

    @Override
    public JsonObject toJsonValue() {
        JsonObject jsonObject = super.toJsonValue();
        IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, gotoLabelOnEnd);
        IServerJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
        IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, endPlayerAction);
        IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
        return jsonObject;
    }

    @Override
    public StepSelectGazeTarget initFrom(IFactorySource source, JsonValue pJsonValue) {
        JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
        super.initFrom(source, jsonObject);
        gotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
        selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
        endPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
        endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
        return this;
    }

}
