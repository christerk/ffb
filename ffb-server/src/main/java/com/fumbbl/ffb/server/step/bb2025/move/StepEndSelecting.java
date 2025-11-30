package com.fumbbl.ffb.server.step.bb2025.move;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.StepParameterSet;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.BalefulHex;
import com.fumbbl.ffb.server.step.generator.BlackInk;
import com.fumbbl.ffb.server.step.generator.BlitzBlock;
import com.fumbbl.ffb.server.step.generator.BlitzMove;
import com.fumbbl.ffb.server.step.generator.Block;
import com.fumbbl.ffb.server.step.generator.CatchOfTheDay;
import com.fumbbl.ffb.server.step.generator.EndPlayerAction;
import com.fumbbl.ffb.server.step.generator.Foul;
import com.fumbbl.ffb.server.step.generator.FuriousOutburst;
import com.fumbbl.ffb.server.step.generator.LookIntoMyEyes;
import com.fumbbl.ffb.server.step.generator.Move;
import com.fumbbl.ffb.server.step.generator.Pass;
import com.fumbbl.ffb.server.step.generator.RadingParty;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SelectBlitzTarget;
import com.fumbbl.ffb.server.step.generator.SelectGazeTarget;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.step.generator.ThenIStartedBlastin;
import com.fumbbl.ffb.server.step.generator.ThrowKeg;
import com.fumbbl.ffb.server.step.generator.ThrowTeamMate;
import com.fumbbl.ffb.server.step.generator.bb2025.MultiBlock;
import com.fumbbl.ffb.server.step.generator.bb2025.Treacherous;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Last step in select sequence. Consumes all expected stepParameters.
 * <p>
 * Expects stepParameter BLOCK_DEFENDER_ID to be set by a preceding step.
 * Expects stepParameter DISPATCH_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_PLAYER_ACTION to be set by a preceding step.
 * Expects stepParameter END_TURN to be set by a preceding step. Expects
 * stepParameter FOUL_DEFENDER_ID to be set by a preceding step. Expects
 * stepParameter GAZE_VICTIM_ID to be set by a preceding step. Expects
 * stepParameter HAIL_MARY_PASS to be set by a preceding step. Expects
 * stepParameter MOVE_STACK to be set by a preceding step. Expects stepParameter
 * TARGET_COORDINATE to be set by a preceding step. Expects stepParameter
 * THROWN_PLAYER_ID to be set by a preceding step. Expects stepParameter
 * USING_STAB to be set by a preceding step.
 * <p>
 * Will push a new sequence on the stack.
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepEndSelecting extends AbstractStep {

  private boolean fEndTurn;
  private boolean fEndPlayerAction;
  private PlayerAction fDispatchPlayerAction;
  private PlayerAction bloodlustAction;
  // moveSequence
  private FieldCoordinate[] fMoveStack;
  private FieldCoordinate moveStart;
  private String fGazeVictimId;
  // blockSequence
  private String fBlockDefenderId;
  private Boolean fUsingStab;
  private boolean usingChainsaw, usingVomit, usingBreatheFire;
  // foulSequence
  private String fFoulDefenderId;
  // passSequence + throwTeamMateSequence
  private FieldCoordinate fTargetCoordinate;
  private boolean fHailMaryPass, kicked;
  private String fThrownPlayerId;
  private String fKickedPlayerId;
  private int fNumDice;
  private List<BlockTarget> blockTargets = new ArrayList<>();
  private String targetPlayerId;
  private String ballAndChainRrSetting;

  public StepEndSelecting(GameState pGameState) {
    super(pGameState);
  }

  public StepId getId() {
    return StepId.END_SELECTING;
  }

  @Override
  public void start() {
    super.start();
    executeStep();
  }

  @Override
  public void init(StepParameterSet pParameterSet) {
    super.init(pParameterSet);
    if (pParameterSet != null) {
      Arrays.stream(pParameterSet.values()).forEach(parameter -> {
        if (parameter.getKey() == StepParameterKey.BLOCK_TARGETS) {
          //noinspection unchecked
          blockTargets = (List<BlockTarget>) parameter.getValue();
        }
      });
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean setParameter(StepParameter parameter) {
    if ((parameter != null) && !super.setParameter(parameter)) {
      switch (parameter.getKey()) {
        case BLOCK_DEFENDER_ID:
          fBlockDefenderId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case DISPATCH_PLAYER_ACTION:
          fDispatchPlayerAction = (PlayerAction) parameter.getValue();
          consume(parameter);
          return true;
        case END_PLAYER_ACTION:
          fEndPlayerAction = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case END_TURN:
          fEndTurn = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case FOUL_DEFENDER_ID:
          fFoulDefenderId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case GAZE_VICTIM_ID:
          fGazeVictimId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case HAIL_MARY_PASS:
          fHailMaryPass = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case MOVE_START:
          moveStart = (FieldCoordinate) parameter.getValue();
          consume(parameter);
          return true;
        case MOVE_STACK:
          fMoveStack = (FieldCoordinate[]) parameter.getValue();
          consume(parameter);
          return true;
        case TARGET_COORDINATE:
          fTargetCoordinate = (FieldCoordinate) parameter.getValue();
          consume(parameter);
          return true;
        case THROWN_PLAYER_ID:
          fThrownPlayerId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case KICKED_PLAYER_ID:
          fKickedPlayerId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case NR_OF_DICE:
          fNumDice = (parameter.getValue() != null) ? (Integer) parameter.getValue() : 0;
          consume(parameter);
          return true;
        case USING_STAB:
          fUsingStab = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case USING_CHAINSAW:
          usingChainsaw = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case USING_VOMIT:
          usingVomit = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
				case USING_BREATHE_FIRE:
          usingBreatheFire = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case BLOCK_TARGETS:
          blockTargets = (List<BlockTarget>) parameter.getValue();
          consume(parameter);
          return true;
        case IS_KICKED_PLAYER:
          kicked = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
          consume(parameter);
          return true;
        case TARGET_PLAYER_ID:
          targetPlayerId = (String) parameter.getValue();
          consume(parameter);
          return true;
        case BLOOD_LUST_ACTION:
          bloodlustAction = (PlayerAction) parameter.getValue();
          consume(parameter);
          return true;
        case BALL_AND_CHAIN_RE_ROLL_SETTING:
          ballAndChainRrSetting = (String) parameter.getValue();
          consume(parameter);
          return true;
        default:
          break;
      }
    }
    return false;
  }

  private void executeStep() {
    UtilServerDialog.hideDialog(getGameState());
    Game game = getGameState().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (fEndTurn || fEndPlayerAction) {
      game.getFieldModel().clearMultiBlockTargets();

      SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
      ((EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name()))
        .pushSequence(new EndPlayerAction.SequenceParams(getGameState(), true, true, fEndTurn));
    } else if (actingPlayer.isSufferingBloodLust()) {
      if (fDispatchPlayerAction != null || bloodlustAction != null) {
        if (bloodlustAction != null) {
          fDispatchPlayerAction = bloodlustAction;
          if (bloodlustAction == PlayerAction.MOVE) {
            UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), bloodlustAction, false);
          }
        } else if (fDispatchPlayerAction == PlayerAction.BLITZ) {
          UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), fDispatchPlayerAction, actingPlayer.isJumping());
        }

        dispatchPlayerAction(fDispatchPlayerAction, bloodlustAction == null || !fDispatchPlayerAction.isMoving());
      } else {
        if ((actingPlayer.getPlayerAction() != null) && !actingPlayer.getPlayerAction().isMoving()) {
          UtilServerSteps.changePlayerAction(this, actingPlayer.getPlayerId(), PlayerAction.MOVE,
            actingPlayer.isJumping());
        }
        dispatchPlayerAction(actingPlayer.getPlayerAction(), false);
      }
    } else if (fDispatchPlayerAction != null) {
      dispatchPlayerAction(fDispatchPlayerAction, true);
    } else {
      dispatchPlayerAction(actingPlayer.getPlayerAction(), false);
    }
    getResult().setNextAction(StepAction.NEXT_STEP);
  }

  private void dispatchPlayerAction(PlayerAction pPlayerAction, boolean pWithParameter) {
    Game game = getGameState().getGame();
    SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);

    PlayerState playerState = game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer());

    if (pPlayerAction == null || (pPlayerAction == PlayerAction.MOVE && playerState.isRooted() && UtilPlayer.canGaze(game, game.getActingPlayer().getPlayer()))) {
      game.getFieldModel().clearMultiBlockTargets();
      ((Select) factory.forName(SequenceGenerator.Type.Select.name()))
        .pushSequence(new Select.SequenceParams(getGameState(), false));
      return;
    }
    Pass passGenerator = (Pass) factory.forName(SequenceGenerator.Type.Pass.name());
    ThrowTeamMate ttmGenerator = (ThrowTeamMate) factory.forName(SequenceGenerator.Type.ThrowTeamMate.name());
    Block blockGenerator = (Block) factory.forName(SequenceGenerator.Type.Block.name());
    Foul foulGenerator = (Foul) factory.forName(SequenceGenerator.Type.Foul.name());
    Move moveGenerator = (Move) factory.forName(SequenceGenerator.Type.Move.name());
    EndPlayerAction endGenerator = (EndPlayerAction) factory.forName(SequenceGenerator.Type.EndPlayerAction.name());
    EndPlayerAction.SequenceParams endParams = new EndPlayerAction.SequenceParams(getGameState(), true, true, false);
    BlitzMove blitzMoveGenerator = (BlitzMove) factory.forName(SequenceGenerator.Type.BlitzMove.name());
    BlitzBlock blitzBlockGenerator = (BlitzBlock) factory.forName(SequenceGenerator.Type.BlitzBlock.name());
    SelectBlitzTarget selectBlitzTarget = (SelectBlitzTarget) factory.forName(SequenceGenerator.Type.SelectBlitzTarget.name());
    SelectGazeTarget selectGazeTarget = (SelectGazeTarget) factory.forName(SequenceGenerator.Type.SelectGazeTarget.name());
    MultiBlock multiBlock = (MultiBlock) factory.forName(SequenceGenerator.Type.MultiBlock.name());
    Select.SequenceParams selectParams = new Select.SequenceParams(getGameState(), true, blockTargets);
    Select selectGenerator = (Select) factory.forName(SequenceGenerator.Type.Select.name());

    ActingPlayer actingPlayer = game.getActingPlayer();
    switch (pPlayerAction) {
      case BLITZ_SELECT:
        selectBlitzTarget.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
        break;
      case GAZE_SELECT:
        selectGazeTarget.pushSequence(new SequenceGenerator.SequenceParams(getGameState()));
        break;
      case PASS:
      case HAIL_MARY_PASS:
      case THROW_BOMB:
      case HAIL_MARY_BOMB:
      case HAND_OVER:
        if (pWithParameter) {
          passGenerator.pushSequence(new Pass.SequenceParams(getGameState(), fTargetCoordinate));
        } else {
          passGenerator.pushSequence(new Pass.SequenceParams(getGameState()));
        }
        break;
      case THROW_TEAM_MATE:
      case KICK_TEAM_MATE:
        if (pWithParameter) {
          ttmGenerator.pushSequence(new ThrowTeamMate.SequenceParams(getGameState(), fThrownPlayerId, fTargetCoordinate, kicked));
        } else {
          ttmGenerator.pushSequence(new ThrowTeamMate.SequenceParams(getGameState()));
        }
        break;
      case BLITZ:
        if (pWithParameter) {
          blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState(), fBlockDefenderId, fUsingStab, usingChainsaw, usingVomit, usingBreatheFire));
        } else {
          blitzBlockGenerator.pushSequence(new BlitzBlock.SequenceParams(getGameState()));
        }
        break;
      case BLOCK:
        if (pWithParameter) {
          blockGenerator.pushSequence(new Block.Builder(getGameState()).withDefenderId(fBlockDefenderId)
            .useStab(fUsingStab).useChainsaw(usingChainsaw).useVomit(usingVomit).useBreatheFire(usingBreatheFire).build());
        } else {
          blockGenerator.pushSequence(new Block.Builder(getGameState()).build());
        }
        break;
      case MULTIPLE_BLOCK:
        if (pWithParameter) {
          multiBlock.pushSequence(new MultiBlock.SequenceParams(getGameState(), blockTargets));
        } else {
          multiBlock.pushSequence(new MultiBlock.SequenceParams(getGameState(), Collections.emptyList()));
        }
        break;
      case FOUL:
        if (pWithParameter) {
          foulGenerator.pushSequence(new Foul.SequenceParams(getGameState(), fFoulDefenderId, usingChainsaw));
        } else {
          foulGenerator.pushSequence(new Foul.SequenceParams(getGameState()));
        }
        break;
      case MOVE:
        if (game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).isRooted()) {
          endGenerator.pushSequence(endParams);
          break;
        }
        // fall through
      case FOUL_MOVE:
      case PASS_MOVE:
      case THROW_TEAM_MATE_MOVE:
      case KICK_TEAM_MATE_MOVE:
      case HAND_OVER_MOVE:
      case GAZE:
        if (pWithParameter) {
          moveGenerator.pushSequence(new Move.SequenceParams(getGameState(), fMoveStack, fGazeVictimId, moveStart, ballAndChainRrSetting));
        } else {
          moveGenerator.pushSequence(new Move.SequenceParams(getGameState()));
        }
        break;
      case BLITZ_MOVE:
      case KICK_EM_BLITZ:
        if (pWithParameter) {
          blitzMoveGenerator.pushSequence(new BlitzMove.SequenceParams(getGameState(), fMoveStack, fGazeVictimId, moveStart));
        } else {
          blitzMoveGenerator.pushSequence(new BlitzMove.SequenceParams(getGameState()));
        }
        break;
      case REMOVE_CONFUSION:
        actingPlayer.setHasMoved(true);
        endGenerator.pushSequence(endParams);
        break;
      case STAND_UP:
        endGenerator.pushSequence(endParams);
        break;
      case STAND_UP_BLITZ:
        game.getTurnData().setBlitzUsed(true);
        endGenerator.pushSequence(endParams);
        break;
      case TREACHEROUS:
        selectGenerator.pushSequence(selectParams);
        Treacherous.SequenceParams treacherousParams = new Treacherous.SequenceParams(getGameState(), IStepLabel.END_SELECTING);
        Treacherous treacherousGenerator = (Treacherous) factory.forName(SequenceGenerator.Type.Treacherous.name());
        treacherousGenerator.pushSequence(treacherousParams);
        break;
      case RAIDING_PARTY:
        selectGenerator.pushSequence(selectParams);
        RadingParty.SequenceParams raidingParams = new RadingParty.SequenceParams(getGameState(), IStepLabel.END_SELECTING, null);
        RadingParty raidingGenerator = (RadingParty) factory.forName(SequenceGenerator.Type.RaidingParty.name());
        raidingGenerator.pushSequence(raidingParams);
        break;
      case WISDOM_OF_THE_WHITE_DWARF:
        selectGenerator.pushSequence(selectParams);
        Sequence sequence = new Sequence(getGameState());
        sequence.add(StepId.WISDOM_OF_THE_WHITE_DWARF);
        getGameState().getStepStack().push(sequence.getSequence());
        break;
      case THROW_KEG:
        ThrowKeg throwKegGenerator = (ThrowKeg) factory.forName(SequenceGenerator.Type.ThrowKeg.name());
        throwKegGenerator.pushSequence(new ThrowKeg.SequenceParams(getGameState(), targetPlayerId));
        break;
      case LOOK_INTO_MY_EYES:
        LookIntoMyEyes lookIntoMyEyes = (LookIntoMyEyes) factory.forName(SequenceGenerator.Type.LookIntoMyEyes.name());
        lookIntoMyEyes.pushSequence(new LookIntoMyEyes.SequenceParams(getGameState(), true, null));
        break;
      case BALEFUL_HEX:
        selectGenerator.pushSequence(selectParams);
        BalefulHex.SequenceParams balefulParams = new BalefulHex.SequenceParams(getGameState(), IStepLabel.END_SELECTING);
        BalefulHex balefulGenerator = (BalefulHex) factory.forName(SequenceGenerator.Type.BalefulHex.name());
        balefulGenerator.pushSequence(balefulParams);
        break;
      case BLACK_INK:
        selectGenerator.pushSequence(selectParams);
        BlackInk.SequenceParams blackInkParams = new BlackInk.SequenceParams(getGameState(), IStepLabel.END_SELECTING, playerState);
        BlackInk blackInkGenerator = (BlackInk) factory.forName(SequenceGenerator.Type.BlackInk.name());
        blackInkGenerator.pushSequence(blackInkParams);
        break;
      case CATCH_OF_THE_DAY:
        selectGenerator.pushSequence(selectParams);
        CatchOfTheDay.SequenceParams cotdParams = new CatchOfTheDay.SequenceParams(getGameState(), IStepLabel.END_SELECTING);
        CatchOfTheDay cotdGenerator = (CatchOfTheDay) factory.forName(SequenceGenerator.Type.CatchOfTheDay.name());
        cotdGenerator.pushSequence(cotdParams);
        break;
      case THEN_I_STARTED_BLASTIN:
        selectGenerator.pushSequence(selectParams);
        ThenIStartedBlastin.SequenceParams tisbParams = new ThenIStartedBlastin.SequenceParams(getGameState());
        ThenIStartedBlastin tisbGenerator = (ThenIStartedBlastin) factory.forName(SequenceGenerator.Type.ThenIStartedBlastin.name());
        tisbGenerator.pushSequence(tisbParams);
        break;
      case FURIOUS_OUTPBURST:
        SequenceGenerator.SequenceParams foParams = new SequenceGenerator.SequenceParams(getGameState());
        FuriousOutburst furiousGenerator = (FuriousOutburst) factory.forName(SequenceGenerator.Type.FuriousOutburst.name());
        furiousGenerator.pushSequence(foParams);
        break;
      default:
        throw new IllegalStateException("Unhandled player action " + pPlayerAction.getName() + ".");
    }
  }

  // JSON serialization

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.END_TURN.addTo(jsonObject, fEndTurn);
    IServerJsonOption.END_PLAYER_ACTION.addTo(jsonObject, fEndPlayerAction);
    IServerJsonOption.DISPATCH_PLAYER_ACTION.addTo(jsonObject, fDispatchPlayerAction);
    IServerJsonOption.MOVE_STACK.addTo(jsonObject, fMoveStack);
    IServerJsonOption.GAZE_VICTIM_ID.addTo(jsonObject, fGazeVictimId);
    IServerJsonOption.BLOCK_DEFENDER_ID.addTo(jsonObject, fBlockDefenderId);
    IServerJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
    IServerJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
    IServerJsonOption.USING_VOMIT.addTo(jsonObject, usingVomit);
		IServerJsonOption.USING_BREATHE_FIRE.addTo(jsonObject, usingBreatheFire);
    IServerJsonOption.FOUL_DEFENDER_ID.addTo(jsonObject, fFoulDefenderId);
    IServerJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
    IServerJsonOption.HAIL_MARY_PASS.addTo(jsonObject, fHailMaryPass);
    IServerJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
    IServerJsonOption.KICKED_PLAYER_ID.addTo(jsonObject, fKickedPlayerId);
    IServerJsonOption.NR_OF_DICE.addTo(jsonObject, fNumDice);
    JsonArray jsonArray = new JsonArray();
    blockTargets.stream().map(BlockTarget::toJsonValue).forEach(jsonArray::add);
    IServerJsonOption.SELECTED_BLOCK_TARGETS.addTo(jsonObject, jsonArray);
    IServerJsonOption.TARGET_PLAYER_ID.addTo(jsonObject, targetPlayerId);
    IServerJsonOption.PLAYER_ACTION.addTo(jsonObject, bloodlustAction);
    return jsonObject;
  }

  @Override
  public StepEndSelecting initFrom(IFactorySource source, JsonValue jsonValue) {
    super.initFrom(source, jsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
    fEndTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
    fEndPlayerAction = IServerJsonOption.END_PLAYER_ACTION.getFrom(source, jsonObject);
    fDispatchPlayerAction = (PlayerAction) IServerJsonOption.DISPATCH_PLAYER_ACTION.getFrom(source, jsonObject);
    fMoveStack = IServerJsonOption.MOVE_STACK.getFrom(source, jsonObject);
    fGazeVictimId = IServerJsonOption.GAZE_VICTIM_ID.getFrom(source, jsonObject);
    fBlockDefenderId = IServerJsonOption.BLOCK_DEFENDER_ID.getFrom(source, jsonObject);
    usingChainsaw = IServerJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
    usingVomit = IServerJsonOption.USING_VOMIT.getFrom(source, jsonObject);
    fUsingStab = IServerJsonOption.USING_STAB.getFrom(source, jsonObject);
    fFoulDefenderId = IServerJsonOption.FOUL_DEFENDER_ID.getFrom(source, jsonObject);
    fTargetCoordinate = IServerJsonOption.TARGET_COORDINATE.getFrom(source, jsonObject);
    fHailMaryPass = IServerJsonOption.HAIL_MARY_PASS.getFrom(source, jsonObject);
    fThrownPlayerId = IServerJsonOption.THROWN_PLAYER_ID.getFrom(source, jsonObject);
    fKickedPlayerId = IServerJsonOption.KICKED_PLAYER_ID.getFrom(source, jsonObject);
    fNumDice = IServerJsonOption.NR_OF_DICE.getFrom(source, jsonObject);
    JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(source, jsonObject);
    blockTargets.clear();
    jsonArray.values().stream()
      .map(value -> new BlockTarget().initFrom(source, value))
      .forEach(value -> blockTargets.add(value));
    targetPlayerId = IServerJsonOption.TARGET_PLAYER_ID.getFrom(source, jsonObject);
    bloodlustAction = (PlayerAction) IServerJsonOption.PLAYER_ACTION.getFrom(source, jsonObject);
		usingBreatheFire = toPrimitive(IServerJsonOption.USING_BREATHE_FIRE.getFrom(source, jsonObject));
    return this;
  }

}
