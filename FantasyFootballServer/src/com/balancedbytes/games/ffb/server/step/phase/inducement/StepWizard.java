package com.balancedbytes.games.ffb.server.step.phase.inducement;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.SpecialEffectFactory;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.TurnModeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogWizardSpellParameter;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandWizardSpell;
import com.balancedbytes.games.ffb.report.ReportWizardUse;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.util.UtilDialog;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.server.util.UtilInducementUse;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * Step in inducement sequence to handle wizard.
 * 
 * May push SpellEffect sequences onto the stack.
 *
 * @author Kalimar
 */
public final class StepWizard extends AbstractStep {

	private SpecialEffect fWizardSpell;
	private FieldCoordinate fTargetCoordinate;
	private boolean fEndInducement;
	private TurnMode fOldTurnMode;
	
	public StepWizard(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.WIZARD;
	}
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			Game game = getGameState().getGame();
			switch (pNetCommand.getId()) {
				case CLIENT_WIZARD_SPELL:
					ClientCommandWizardSpell wizardSpellCommand = (ClientCommandWizardSpell) pNetCommand;
					if (wizardSpellCommand.getWizardSpell() == null) { // cancel spellcasting
						fEndInducement = true;
					} else {
						fWizardSpell = wizardSpellCommand.getWizardSpell();
						if (game.isHomePlaying()) {
							fTargetCoordinate = wizardSpellCommand.getTargetCoordinate();
						} else {
							fTargetCoordinate = wizardSpellCommand.getTargetCoordinate().transform();
						}
					}
          commandStatus = StepCommandStatus.EXECUTE_STEP;
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
		Game game = getGameState().getGame();
		if (fEndInducement) {
			// cancel spellcasting, no spell effect sequences added to the stack
			if (fOldTurnMode != null) {
				game.setTurnMode(fOldTurnMode);
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else if ((fWizardSpell != null) && (fTargetCoordinate != null) && (game.getTurnMode() == TurnMode.WIZARD)) {
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();
			getResult().addReport(new ReportWizardUse(team.getId(), fWizardSpell));
			UtilInducementUse.useInducement(getGameState(), team, InducementType.WIZARD, 1);
	    List<Player> affectedPlayers = new ArrayList<Player>();
			if (fWizardSpell == SpecialEffect.LIGHTNING) {
				getResult().setAnimation(new Animation(AnimationType.SPELL_LIGHTNING, fTargetCoordinate));
				addToAffectedPlayers(affectedPlayers, game.getFieldModel().getPlayer(fTargetCoordinate));
			}
			if (fWizardSpell == SpecialEffect.FIREBALL) {
				getResult().setAnimation(new Animation(AnimationType.SPELL_FIREBALL, fTargetCoordinate));
				FieldCoordinate[] targetCoordinates = game.getFieldModel().findAdjacentCoordinates(fTargetCoordinate, FieldCoordinateBounds.FIELD, 1, true);
				for (int i = targetCoordinates.length - 1; i >= 0; i--) {
					addToAffectedPlayers(affectedPlayers, game.getFieldModel().getPlayer(targetCoordinates[i]));
				}
			}
			if (fOldTurnMode != null) {
				game.setTurnMode(fOldTurnMode);
			}
			UtilGame.syncGameModel(this);
			PlayerState bloodSpotInjury = new PlayerState((fWizardSpell == SpecialEffect.FIREBALL) ? PlayerState.HIT_BY_FIREBALL : PlayerState.HIT_BY_LIGHTNING); 
			game.getFieldModel().add(new BloodSpot(fTargetCoordinate, bloodSpotInjury));
			for (Player affectedPlayer : affectedPlayers) {
				SequenceGenerator.getInstance().pushSpecialEffectSequence(getGameState(), fWizardSpell, affectedPlayer.getId(), true);
			}
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			if (game.getTurnMode() != (TurnMode.WIZARD)) {
				fOldTurnMode = game.getTurnMode();
			}
			game.setTurnMode(TurnMode.WIZARD);
			UtilDialog.showDialog(getGameState(), new DialogWizardSpellParameter());
		}
  }
  
	private void addToAffectedPlayers(List<Player> pAffectedPlayers, Player pPlayer) {
		Game game = getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if ((pPlayer != null) && (playerState != null) && (playerState.getBase() != PlayerState.PRONE) && (playerState.getBase() != PlayerState.STUNNED)) {
			pAffectedPlayers.add(pPlayer);
		}
	}
	
	// ByteArray serialization
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addByte((byte) ((fWizardSpell != null) ? fWizardSpell.getId() : 0));
  	pByteList.addFieldCoordinate(fTargetCoordinate);
  	pByteList.addBoolean(fEndInducement);
  	pByteList.addByte((byte) ((fOldTurnMode != null) ? fOldTurnMode.getId() : 0));
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fWizardSpell = new SpecialEffectFactory().forId(pByteArray.getByte());
  	fTargetCoordinate = pByteArray.getFieldCoordinate();
  	fEndInducement = pByteArray.getBoolean();
  	fOldTurnMode = new TurnModeFactory().forId(pByteArray.getByte());
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = super.toJsonValue();
    IServerJsonOption.WIZARD_SPELL.addTo(jsonObject, fWizardSpell);
    IServerJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
    IServerJsonOption.END_INDUCEMENT.addTo(jsonObject, fEndInducement);
    IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, fOldTurnMode);
    return jsonObject;
  }
  
  @Override
  public StepWizard initFrom(JsonValue pJsonValue) {
    super.initFrom(pJsonValue);
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fWizardSpell = (SpecialEffect) IServerJsonOption.WIZARD_SPELL.getFrom(jsonObject);
    fTargetCoordinate = IServerJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
    fEndInducement = IServerJsonOption.END_INDUCEMENT.getFrom(jsonObject);
    fOldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(jsonObject);
    return this;
  }

}
