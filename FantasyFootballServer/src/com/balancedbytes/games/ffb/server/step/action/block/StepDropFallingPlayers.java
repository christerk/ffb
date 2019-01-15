package com.balancedbytes.games.ffb.server.step.action.block;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.dialog.DialogPilingOnParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseSkill;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPilingOn;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.InjuryResult;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.util.UtilServerDialog;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;
import com.balancedbytes.games.ffb.server.util.UtilServerReRoll;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in block sequence to drop falling players and handle the skill
 * PILING_ON.
 * 
 * Expects stepParameter OLD_DEFENDER_STATE to be set by a preceding step.
 * 
 * Sets stepParameter CATCH_SCATTER_THROWIN_MODE for all steps on the stack.
 * Sets stepParameter INJURY_RESULT for all steps on the stack.
 * Sets stepParameter USING_PILING_ON for all steps on the stack.
 * 
 * @author Kalimar
 */
public class StepDropFallingPlayers extends AbstractStep {

  private InjuryResult fInjuryResultDefender;
  private Boolean fUsingPilingOn;
  private PlayerState fOldDefenderState;

  public StepDropFallingPlayers(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.DROP_FALLING_PLAYERS;
  }

  @Override
  public boolean setParameter(StepParameter pParameter) {
    if ((pParameter != null) && !super.setParameter(pParameter)) {
      switch (pParameter.getKey()) {
        case OLD_DEFENDER_STATE:
          fOldDefenderState = (PlayerState) pParameter.getValue();
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
    if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
      switch (pReceivedCommand.getId()) {
        case CLIENT_USE_SKILL:
          ClientCommandUseSkill useSkillCommand = (ClientCommandUseSkill) pReceivedCommand.getCommand();
          if (Skill.PILING_ON == useSkillCommand.getSkill()) {
            fUsingPilingOn = useSkillCommand.isSkillUsed();
            commandStatus = StepCommandStatus.EXECUTE_STEP;
          }
          break;
        default:
          break;
      }
    }
    if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
      executeStep();
    }
    return commandStatus;
  }

  private void executeStep() {
    boolean doNextStep = true;
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState defenderState = game.getFieldModel().getPlayerState(game.getDefender());
    FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
    PlayerState attackerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING) && attackerState.isRooted()) {
      attackerState = attackerState.changeRooted(false);
    }
    if ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && defenderState.isRooted()) {
      defenderState = defenderState.changeRooted(false);
    }
    if (((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && (defenderCoordinate != null)) || (fUsingPilingOn != null)) {
      if (fUsingPilingOn != null) {
        boolean reRollInjury = fInjuryResultDefender.isArmorBroken();
        getResult().addReport(new ReportPilingOn(actingPlayer.getPlayerId(), fUsingPilingOn, reRollInjury));
        boolean usesATeamReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_USES_A_TEAM_REROLL);
        if (fUsingPilingOn && (!usesATeamReroll || UtilServerReRoll.useReRoll(this, ReRollSource.TEAM_RE_ROLL, actingPlayer.getPlayer()))) {
          actingPlayer.markSkillUsed(Skill.PILING_ON);
          publishParameters(UtilServerInjury.dropPlayer(this, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
          boolean rolledDouble;
          if (reRollInjury) {
            fInjuryResultDefender = UtilServerInjury.handleInjury(this, InjuryType.PILING_ON_INJURY, actingPlayer.getPlayer(), game.getDefender(),
                defenderCoordinate, fInjuryResultDefender, ApothecaryMode.DEFENDER);
            rolledDouble = DiceInterpreter.getInstance().isDouble(fInjuryResultDefender.getInjuryRoll());
          } else {
            fInjuryResultDefender = UtilServerInjury.handleInjury(this, InjuryType.PILING_ON_ARMOR, actingPlayer.getPlayer(), game.getDefender(),
                defenderCoordinate, null, ApothecaryMode.DEFENDER);
            rolledDouble = DiceInterpreter.getInstance().isDouble(fInjuryResultDefender.getArmorRoll());
          }
          if (rolledDouble && UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_TO_KO_ON_DOUBLE)) {
            publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, UtilServerInjury.handleInjury(this, InjuryType.PILING_ON_KNOCKED_OUT, null,
                actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER)));
          }
        }
      } else {
        publishParameters(UtilServerInjury.dropPlayer(this, game.getDefender(), ApothecaryMode.DEFENDER));
        if ((fOldDefenderState != null) && fOldDefenderState.isProne()) {
          fInjuryResultDefender = UtilServerInjury.handleInjury(this, InjuryType.BLOCK_PRONE, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate,
              null, ApothecaryMode.DEFENDER);
        } else {
          fInjuryResultDefender = UtilServerInjury.handleInjury(this, InjuryType.BLOCK, actingPlayer.getPlayer(), game.getDefender(), defenderCoordinate, null,
              ApothecaryMode.DEFENDER);
        }
        if (UtilCards.hasSkill(game, actingPlayer, Skill.WEEPING_DAGGER) && fInjuryResultDefender.isBadlyHurt()) {
          rollWeepingDagger();
        }
        boolean usesATeamReroll = UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_USES_A_TEAM_REROLL);
        if ((attackerState.getBase() != PlayerState.FALLING)
            && UtilCards.hasUnusedSkill(game, actingPlayer, Skill.PILING_ON)
            && (!usesATeamReroll || UtilServerReRoll.isTeamReRollAvailable(getGameState(), actingPlayer.getPlayer()))
            && attackerCoordinate.isAdjacent(defenderCoordinate)
            && !fInjuryResultDefender.isCasualty()
            && !attackerState.isRooted()
            && (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_INJURY_ONLY) || fInjuryResultDefender.isArmorBroken())
            && (!UtilGameOption.isOptionEnabled(game, GameOptionId.PILING_ON_ARMOR_ONLY) || !fInjuryResultDefender.isArmorBroken())
            && (!UtilCards.hasCard(game, game.getDefender(), Card.BELT_OF_INVULNERABILITY) || fInjuryResultDefender.isArmorBroken())
            && !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)
            && !UtilCards.hasCard(game, game.getDefender(), Card.GOOD_OLD_MAGIC_CODPIECE)) {
          fInjuryResultDefender.report(this);
          UtilServerDialog.showDialog(getGameState(), new DialogPilingOnParameter(actingPlayer.getPlayerId(), fInjuryResultDefender.isArmorBroken(),
              usesATeamReroll), false);
          doNextStep = false;
        }
      }
    }
    if (doNextStep) {
      if (fInjuryResultDefender != null) {
        publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, fInjuryResultDefender));
        // if (fOldDefenderState != null) {
        //   game.getFieldModel().setPlayerState(game.getDefender(), fOldDefenderState);
        // }
      }
      if (fUsingPilingOn != null) {
        publishParameter(new StepParameter(StepParameterKey.USING_PILING_ON, fUsingPilingOn));
      }
      // end turn if dropping a player of your own team
      if ((defenderState != null) && (defenderState.getBase() == PlayerState.FALLING) && (game.getDefender().getTeam() == actingPlayer.getPlayer().getTeam())
          && (fOldDefenderState != null) && !fOldDefenderState.isProne()) {
        publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
      }
      if ((attackerState != null) && (attackerState.getBase() == PlayerState.FALLING) && (attackerCoordinate != null)) {
        publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
        publishParameters(UtilServerInjury.dropPlayer(this, actingPlayer.getPlayer(), ApothecaryMode.ATTACKER));
        publishParameter(new StepParameter(
          StepParameterKey.INJURY_RESULT,
          UtilServerInjury.handleInjury(this, InjuryType.BLOCK, game.getDefender(), actingPlayer.getPlayer(), attackerCoordinate, null, ApothecaryMode.ATTACKER)
        ));
      }
      getResult().setNextAction(StepAction.NEXT_STEP);
    }
  }
  
  private void rollWeepingDagger() {
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    int minimumRoll = DiceInterpreter.getInstance().minimumRollWeepingDagger();
    int roll = getGameState().getDiceRoller().rollWeepingDagger();
    boolean successful = DiceInterpreter.getInstance().isSkillRollSuccessful(roll, minimumRoll);
    if (successful) {
      game.getFieldModel().addCardEffect(game.getDefender(), CardEffect.POISONED);
    }
    getResult().addReport(
      new ReportSkillRoll(ReportId.WEEPING_DAGGER_ROLL, actingPlayer.getPlayerId(), successful, roll, minimumRoll, false, null)
    );
    publishParameter(new StepParameter(StepParameterKey.POISONED, true));
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    if (fInjuryResultDefender != null) {
      IServerJsonOption.INJURY_RESULT_DEFENDER.addTo(jsonObject, fInjuryResultDefender.toJsonValue());
    }
    IServerJsonOption.USING_PILING_ON.addTo(jsonObject, fUsingPilingOn);
    IServerJsonOption.OLD_DEFENDER_STATE.addTo(jsonObject, fOldDefenderState);
    return jsonObject;
  }

  @Override
  public StepDropFallingPlayers initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fInjuryResultDefender = null;
    JsonObject injuryResultDefenderObject = IServerJsonOption.INJURY_RESULT_DEFENDER.getFrom(jsonObject);
    if (injuryResultDefenderObject != null) {
      fInjuryResultDefender = new InjuryResult().initFrom(injuryResultDefenderObject);
    }
    fUsingPilingOn = IServerJsonOption.USING_PILING_ON.getFrom(jsonObject);
    fOldDefenderState = IServerJsonOption.OLD_DEFENDER_STATE.getFrom(jsonObject);
    return this;
  }

}
