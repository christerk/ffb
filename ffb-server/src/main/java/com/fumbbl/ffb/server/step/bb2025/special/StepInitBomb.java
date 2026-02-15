package com.fumbbl.ffb.server.step.bb2025.special;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.option.GameOptionBoolean;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.report.ReportBombOutOfBounds;
import com.fumbbl.ffb.report.ReportScatterBall;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepException;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.util.UtilServerCatchScatterThrowIn;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;


/**
 * Initialization step of the pass sequence. May push SpecialEffect sequences
 * onto the stack.
 * <p>
 * Needs to be initialized with stepParameter CATCHER_ID. Needs to be
 * initialized with stepParameter GOTO_LABEL_ON_END. May be initialized with
 * stepParameter OLD_TURN_MODE. Needs to be initialized with stepParameter
 * PASS_FUMBLE.
 * <p>
 * Sets stepParameter CATCHER_ID for all steps on the stack. Sets stepParameter
 * END_PLAYER_ACTION for all steps on the stack. Sets stepParameter
 * OLD_TURN_MODE for all steps on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepInitBomb extends AbstractStep {

    private String fGotoLabelOnEnd;
    private String fCatcherId;
    private boolean fPassFumble;
    private FieldCoordinate fBombCoordinate;
    private boolean fBombOutOfBounds;
    private boolean dontDropFumble;
    private Boolean explodeSkillUsed;

    public StepInitBomb(GameState pGameState) {
        super(pGameState);
    }

    public StepId getId() {
        return StepId.INIT_BOMB;
    }

    @Override
    public void init(StepParameterSet pParameterSet) {
        if (pParameterSet != null) {
            for (StepParameter parameter : pParameterSet.values()) {
                switch (parameter.getKey()) {
                    // optional
                    case CATCHER_ID:
                        fCatcherId = (String) parameter.getValue();
                        break;
                    // mandatory
                    case GOTO_LABEL_ON_END:
                        fGotoLabelOnEnd = (String) parameter.getValue();
                        break;
                    // mandatory
                    case PASS_FUMBLE:
                        fPassFumble = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
                        break;
                    case BOMB_OUT_OF_BOUNDS:
                        fBombOutOfBounds = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
                        break;
                    case DONT_DROP_FUMBLE:
                        dontDropFumble = toPrimitive((Boolean) parameter.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
        if (!StringTool.isProvided(fGotoLabelOnEnd)) {
            throw new StepException("StepParameter " + StepParameterKey.GOTO_LABEL_ON_END + " is not initialized.");
        }
    }

    @Override
    public boolean setParameter(StepParameter parameter) {
        if ((parameter != null) && !super.setParameter(parameter)) {
            if (parameter.getKey() == StepParameterKey.BOMB_OUT_OF_BOUNDS) {
                fBombOutOfBounds = parameter.getValue() != null ? (Boolean) parameter.getValue() : false;
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

    @Override
    public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
        StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
        if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
            if (pReceivedCommand.getId() == NetCommandId.CLIENT_USE_SKILL) {
                ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
                if (clientCommandUseSkill.getSkill().hasSkillProperty(NamedProperties.canForceBombExplosion)) {
                    explodeSkillUsed = clientCommandUseSkill.isSkillUsed();
                    getResult().addReport(new ReportSkillUse(clientCommandUseSkill.getPlayerId(), clientCommandUseSkill.getSkill(), clientCommandUseSkill.isSkillUsed(), SkillUse.FORCE_BOMB_EXPLOSION));
                    if (explodeSkillUsed) {
                        getGameState().getGame().getActingPlayer().markSkillUsed(clientCommandUseSkill.getSkill());
                    }
                    commandStatus = StepCommandStatus.EXECUTE_STEP;
                }
            }
        }

        if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
            executeStep();
        }
        return commandStatus;
    }

    private void executeStep() {
        Game game = getGameState().getGame();
        game.getTurnData().setBombUsed(true);

        game.getFieldModel().setRangeRuler(null);
        if (fPassFumble) {
            fCatcherId = null;
        }
        if (fBombOutOfBounds) {
            fCatcherId = null;
        }

        if (fCatcherId != null) {
            ActingPlayer actingPlayer = game.getActingPlayer();
            Skill explodeSkill = UtilCards.getUnusedSkillWithProperty(actingPlayer, NamedProperties.canForceBombExplosion);
            if (explodeSkill != null) {
                if (explodeSkillUsed == null) {
                    UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(actingPlayer.getPlayerId(), explodeSkill, 0), false);
                    return;
                }
            } else if (explodeSkillUsed == null) {
                explodeSkillUsed = false;
            }
            if (explodeSkillUsed) {
                fCatcherId = null;
            }
        }

        if (fCatcherId == null) {
            fBombCoordinate = game.getFieldModel().getBombCoordinate();
            boolean bombOut = false;

            if (fBombCoordinate == null) {
                if (!dontDropFumble) {
                    bombOut = true;
                }
            } else {
                GameOptionBoolean bounceOption = 
                    (GameOptionBoolean) game.getOptions().getOptionWithDefault(GameOptionId.BOMB_BOUNCES_ON_EMPTY_SQUARES);

                if (!fPassFumble && bounceOption.isEnabled() && game.getFieldModel().getPlayer(fBombCoordinate) == null) {
                    int scatterRoll = getGameState().getDiceRoller().rollScatterDirection();
                    Direction direction = DiceInterpreter.getInstance().interpretScatterDirectionRoll(game, scatterRoll);
                    FieldCoordinate bounceTo = UtilServerCatchScatterThrowIn.findScatterCoordinate(fBombCoordinate, direction, 1);
                    getResult().addReport(new ReportScatterBall(new Direction[]{direction}, new int[]{scatterRoll}, false));

                    if (!FieldCoordinateBounds.FIELD.isInBounds(bounceTo)) {
                        bombOut = true;
                    } else if (game.getFieldModel().getPlayer(bounceTo) != null) {
                        game.getFieldModel().setBombCoordinate(bounceTo);
                        game.getFieldModel().setBombMoving(true);
                        publishParameter(new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.CATCH_BOMB));
                    } else { // empty bounce square
                        fBombCoordinate = bounceTo;
                        game.getFieldModel().setBombCoordinate(bounceTo);
                        game.getFieldModel().setBombMoving(false);
                    }
                }
            }

            if (bombOut) {
                game.getFieldModel().setBombCoordinate(null);
                game.getFieldModel().setBombMoving(false);
                getResult().addReport(new ReportBombOutOfBounds());
            }

            leaveStep(null);
        } else {
            leaveStep(fGotoLabelOnEnd);
        }
    }

    private void leaveStep(String pGotoLabel) {
        publishParameter(new StepParameter(StepParameterKey.CATCHER_ID, fCatcherId));
        if (StringTool.isProvided(pGotoLabel)) {
            getResult().setNextAction(StepAction.GOTO_LABEL, pGotoLabel);
        } else {
            getResult().setNextAction(StepAction.NEXT_STEP);
        }
    }

    // JSON serialization

    @Override
    public JsonObject toJsonValue() {
        JsonObject jsonObject = super.toJsonValue();
        IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, fGotoLabelOnEnd);
        IServerJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
        IServerJsonOption.PASS_FUMBLE.addTo(jsonObject, fPassFumble);
        IServerJsonOption.BOMB_COORDINATE.addTo(jsonObject, fBombCoordinate);
        IServerJsonOption.DONT_DROP_FUMBLE.addTo(jsonObject, dontDropFumble);
        IServerJsonOption.SKILL_USED.addTo(jsonObject, explodeSkillUsed);
        return jsonObject;
    }

    @Override
    public StepInitBomb initFrom(IFactorySource source, JsonValue jsonValue) {
        super.initFrom(source, jsonValue);
        JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
        fGotoLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(source, jsonObject);
        fCatcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
        fPassFumble = IServerJsonOption.PASS_FUMBLE.getFrom(source, jsonObject);
        fBombCoordinate = IServerJsonOption.BOMB_COORDINATE.getFrom(source, jsonObject);
        dontDropFumble = toPrimitive(IServerJsonOption.DONT_DROP_FUMBLE.getFrom(source, jsonObject));
        explodeSkillUsed = IServerJsonOption.SKILL_USED.getFrom(source, jsonObject);
        return this;
    }

}
