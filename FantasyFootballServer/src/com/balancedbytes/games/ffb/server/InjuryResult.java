package com.balancedbytes.games.ffb.server;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.ArmorModifier;
import com.balancedbytes.games.ffb.ArmorModifierFactory;
import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.InjuryModifier;
import com.balancedbytes.games.ffb.InjuryModifierFactory;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.InjuryTypeFactory;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.SoundFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.report.ReportInjury;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryMode;
import com.balancedbytes.games.ffb.server.step.action.common.ApothecaryModeFactory;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class InjuryResult implements IByteArrayReadable, IJsonSerializable {

  private InjuryType fInjuryType;
  private String fDefenderId;
  private FieldCoordinate fDefenderPosition;
  private String fAttackerId;
  private Set<ArmorModifier> fArmorModifiers;
  private int[] fArmorRoll;
  private boolean fArmorBroken;
  private Set<InjuryModifier> fInjuryModifiers;
  private int[] fInjuryRoll;
  private int[] fCasualtyRoll;
  private int[] fCasualtyRollDecay;
  private PlayerState fInjury;
  private PlayerState fInjuryDecay;
  private SeriousInjury fSeriousInjury;
  private SeriousInjury fSeriousInjuryDecay;
  private ApothecaryStatus fApothecaryStatus;
  private SendToBoxReason fSendToBoxReason;
  private int fSendToBoxTurn;
  private int fSendToBoxHalf;
  private Sound fSound;
  private PlayerState fSufferedInjury;
  private ApothecaryMode fApothecaryMode;

  public InjuryResult() {
    fArmorModifiers = new HashSet<ArmorModifier>();
    fInjuryModifiers = new HashSet<InjuryModifier>();
  }

  public void setInjuryType(InjuryType pInjuryType) {
    fInjuryType = pInjuryType;
  }

  public InjuryType getInjuryType() {
    return fInjuryType;
  }

  public void setDefenderId(String pPlayerId) {
    fDefenderId = pPlayerId;
  }

  public String getDefenderId() {
    return fDefenderId;
  }

  public void setDefenderCoordinate(FieldCoordinate pDefenderCoordinate) {
    fDefenderPosition = pDefenderCoordinate;
  }

  public FieldCoordinate getDefenderPosition() {
    return fDefenderPosition;
  }

  public void setAttackerId(String pSendToBoxByPlayerId) {
    fAttackerId = pSendToBoxByPlayerId;
  }

  public String getAttackerId() {
    return fAttackerId;
  }

  public void addArmorModifier(ArmorModifier pArmorModifier) {
    if (pArmorModifier != null) {
      fArmorModifiers.add(pArmorModifier);
    }
  }

  public boolean hasArmorModifier(ArmorModifier pArmorModifier) {
    return fArmorModifiers.contains(pArmorModifier);
  }

  public int getArmorModifierTotal() {
    int totalModifiers = 0;
    for (ArmorModifier armorModifier : fArmorModifiers) {
      totalModifiers += armorModifier.getModifier();
    }
    return totalModifiers;
  }

  public ArmorModifier[] getArmorModifiers() {
    return new ArmorModifierFactory().toArray(fArmorModifiers);
  }
  
  public void clearArmorModifiers() {
  	fArmorModifiers.clear();
  }

  public void addInjuryModifier(InjuryModifier pInjuryModifier) {
    if (pInjuryModifier != null) {
      fInjuryModifiers.add(pInjuryModifier);
    }
  }

  public boolean hasInjuryModifier(InjuryModifier pInjuryModifier) {
    return fInjuryModifiers.contains(pInjuryModifier);
  }

  public int getInjuryModifierTotal() {
    int totalModifiers = 0;
    for (InjuryModifier injuryModifier : fInjuryModifiers) {
      totalModifiers += injuryModifier.getModifier();
    }
    return totalModifiers;
  }

  public InjuryModifier[] getInjuryModifiers() {
    return new InjuryModifierFactory().toArray(fInjuryModifiers);
  }
  
  public void clearInjuryModifiers() {
  	fInjuryModifiers.clear();
  }

  public int[] getArmorRoll() {
    return fArmorRoll;
  }

  public void setArmorRoll(int[] pArmorRoll) {
    fArmorRoll = pArmorRoll;
  }

  public void setArmorBroken(boolean pArmorBroken) {
    fArmorBroken = pArmorBroken;
  }

  public boolean isArmorBroken() {
    return fArmorBroken;
  }

  public int[] getInjuryRoll() {
    return fInjuryRoll;
  }

  public void setInjuryRoll(int[] pInjuryRoll) {
    fInjuryRoll = pInjuryRoll;
  }

  public int[] getCasualtyRoll() {
    return fCasualtyRoll;
  }

  public void setCasualtyRoll(int[] pCasualtyRoll) {
    fCasualtyRoll = pCasualtyRoll;
  }

  public PlayerState getInjury() {
    return fInjury;
  }

  public void setInjury(PlayerState pInjury) {
    fInjury = pInjury;
  }

  public PlayerState getInjuryDecay() {
    return fInjuryDecay;
  }

  public void setInjuryDecay(PlayerState pInjuryDecay) {
    fInjuryDecay = pInjuryDecay;
  }

  public PlayerState getPlayerState() {
    if ((getInjuryDecay() != null) && (getInjury() != null)) {
      if (getInjuryDecay().getId() > getInjury().getId()) {
        return getInjuryDecay();
      } else {
        return getInjury();
      }
    } else {
      return getInjury();
    }
  }

  public SeriousInjury getSeriousInjury() {
    return fSeriousInjury;
  }

  public void setSeriousInjury(SeriousInjury pSeriousInjury) {
    fSeriousInjury = pSeriousInjury;
  }

  public boolean isCasualty() {
    return ((getPlayerState() != null) && getPlayerState().isCasualty());
  }

  public boolean isKnockedOut() {
    return ((getPlayerState() != null) && (getPlayerState().getBase() == PlayerState.KNOCKED_OUT));
  }

  public boolean isReserve() {
    return ((getPlayerState() != null) && (getPlayerState().getBase() == PlayerState.RESERVE));
  }

  public boolean isSeriousInjury() {
    return ((getPlayerState() != null) && (getPlayerState().getBase() == PlayerState.SERIOUS_INJURY));
  }

  public void setApothecaryStatus(ApothecaryStatus pApothecaryStatus) {
    fApothecaryStatus = pApothecaryStatus;
  }

  public ApothecaryStatus getApothecaryStatus() {
    return fApothecaryStatus;
  }

  public void setSendToBoxReason(SendToBoxReason pSendToBoxReason) {
    fSendToBoxReason = pSendToBoxReason;
  }

  public SendToBoxReason getSendToBoxReason() {
    return fSendToBoxReason;
  }

  public void setSendToBoxTurn(int pSendToBoxTurn) {
    fSendToBoxTurn = pSendToBoxTurn;
  }

  public int getSendToBoxTurn() {
    return fSendToBoxTurn;
  }

  public void setSendToBoxHalf(int pSendToBoxHalf) {
    fSendToBoxHalf = pSendToBoxHalf;
  }

  public int getSendToBoxHalf() {
    return fSendToBoxHalf;
  }

  public void setSound(Sound pSound) {
    fSound = pSound;
  }

  public Sound getSound() {
    return fSound;
  }

  public int[] getCasualtyRollDecay() {
    return fCasualtyRollDecay;
  }

  public void setCasualtyRollDecay(int[] pCasualtyRollDecay) {
    fCasualtyRollDecay = pCasualtyRollDecay;
  }

  public SeriousInjury getSeriousInjuryDecay() {
    return fSeriousInjuryDecay;
  }

  public void setSeriousInjuryDecay(SeriousInjury pSeriousInjuryDecay) {
    fSeriousInjuryDecay = pSeriousInjuryDecay;
  }

  public void setSufferedInjury(PlayerState pSufferedInjury) {
    fSufferedInjury = pSufferedInjury;
  }

  public PlayerState getSufferedInjury() {
    return fSufferedInjury;
  }
  
  public ApothecaryMode getApothecaryMode() {
		return fApothecaryMode;
	}
  
  public void setApothecaryMode(ApothecaryMode pApothecaryMode) {
		fApothecaryMode = pApothecaryMode;
	}

  public void applyTo(IStep pStep) {
    Game game = pStep.getGameState().getGame();
    GameResult gameResult = game.getGameResult();
    Player defender = game.getPlayerById(getDefenderId());

    PlayerResult playerResult = gameResult.getPlayerResult(defender);
    if (UtilCards.hasSkill(game, defender, Skill.SECRET_WEAPON)) {
      playerResult.setHasUsedSecretWeapon(true);
    }

    InjuryType injuryType = getInjuryType();
    boolean isCausedByOpponent = false;
    if (injuryType == InjuryType.BLOCK || injuryType == InjuryType.FOUL || injuryType == InjuryType.STAB || injuryType == InjuryType.PILING_ON_INJURY || injuryType == InjuryType.PILING_ON_ARMOR
        || (injuryType == InjuryType.CHAINSAW && getAttackerId() != null)) {
      isCausedByOpponent = true;
    }
    
    PlayerState oldPlayerState = game.getFieldModel().getPlayerState(defender);
    if (getPlayerState() != null) {
      // Make sure the player isn't converted from a stun to prone (for example when fouling a stunned player)
      if ((getPlayerState().getBase() != PlayerState.PRONE) || (oldPlayerState.getBase() != PlayerState.STUNNED)) {
        PlayerState playerState = game.getFieldModel().getPlayerState(defender);
        game.getFieldModel().setPlayerState(defender, playerState.changeBase(getPlayerState().getBase()));
        if ((getPlayerState().getBase() == PlayerState.STUNNED)
          && (((defender.getTeam() == game.getTeamHome()) && game.isHomePlaying()) || ((defender.getTeam() == game.getTeamAway()) && !game.isHomePlaying()))) {
          game.getFieldModel().setPlayerState(defender, game.getFieldModel().getPlayerState(defender).changeActive(false));
        }
      }
      if (isCasualty() || isKnockedOut() || isReserve()) {
        UtilBox.putPlayerIntoBox(game, defender);
        UtilServerGame.updateLeaderReRolls(pStep);
      }
    }
    // death is also a serious injury
    if ((getPlayerState() != null) && (getPlayerState().getBase() == PlayerState.RIP)) {
      playerResult.setSeriousInjury(SeriousInjury.DEAD);
      playerResult.setSeriousInjuryDecay(null);
    } else {
      playerResult.setSeriousInjury(getSeriousInjury());
      playerResult.setSeriousInjuryDecay(getSeriousInjuryDecay());
    }
    if (getSendToBoxReason() != null) {
      playerResult.setSendToBoxReason(getSendToBoxReason());
      playerResult.setSendToBoxTurn(getSendToBoxTurn());
      playerResult.setSendToBoxHalf(getSendToBoxHalf());
      playerResult.setSendToBoxByPlayerId(getAttackerId());
    }
    if (getSufferedInjury() != null) {
      if (isCausedByOpponent) {
        if ((fApothecaryStatus == ApothecaryStatus.RESULT_CHOICE) && (getPlayerState().getBase() == PlayerState.RESERVE)) {
          if (game.getTeamHome().hasPlayer(defender)) {
            gameResult.getTeamResultHome().sufferInjury(new PlayerState(PlayerState.BADLY_HURT));
          } else {
            gameResult.getTeamResultAway().sufferInjury(new PlayerState(PlayerState.BADLY_HURT));
          }
        } else {
          if (game.getTeamHome().hasPlayer(defender)) {
            gameResult.getTeamResultHome().sufferInjury(getPlayerState());
          } else {
            gameResult.getTeamResultAway().sufferInjury(getPlayerState());
          }
        }
        Player attacker = game.getPlayerById(getAttackerId());
        if (getSufferedInjury().isCasualty() && getInjuryType().isWorthSpps() && (attacker.getTeam() != defender.getTeam())) {
          PlayerResult attackerResult = gameResult.getPlayerResult(attacker);
          attackerResult.setCasualties(attackerResult.getCasualties() + 1);
        }
      }
      game.getFieldModel().add(new BloodSpot(getDefenderPosition(), getSufferedInjury()));
    }
  }

  public void report(IStep pStep) {
    pStep.getResult().addReport(
    		new ReportInjury(
    				getDefenderId(),
    				getInjuryType(),
    				isArmorBroken(),
    				getArmorModifiers(),
    				getArmorRoll(),
    				getInjuryModifiers(),
            getInjuryRoll(),
            getCasualtyRoll(),
            getSeriousInjury(),
            getCasualtyRollDecay(),
            getSeriousInjuryDecay(),
            getInjury(),
            getInjuryDecay(),
            getAttackerId()
        )
    );
    pStep.getResult().setSound(getSound());
  }

  // ByteArray serialization

  public int getByteArraySerializationVersion() {
    return 2;
  }

  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addByte((byte) ((getInjuryType() != null) ? getInjuryType().getId() : 0));
    pByteList.addString(getDefenderId());
    pByteList.addFieldCoordinate(getDefenderPosition());
    pByteList.addString(getAttackerId());
    ArmorModifier[] armorModifiers = getArmorModifiers();
    pByteList.addByte((byte) armorModifiers.length);
    for (ArmorModifier armorModifier : armorModifiers) {
      pByteList.addByte((byte) ((armorModifier != null) ? armorModifier.getId() : 0));
    }
    pByteList.addByteArray(getArmorRoll());
    pByteList.addBoolean(isArmorBroken());
    InjuryModifier[] injuryModifiers = getInjuryModifiers();
    pByteList.addByte((byte) injuryModifiers.length);
    for (InjuryModifier injuryModifier : injuryModifiers) {
      pByteList.addByte((byte) ((injuryModifier != null) ? injuryModifier.getId() : 0));
    }
    pByteList.addByteArray(getInjuryRoll());
    pByteList.addSmallInt(((getInjury() != null) ? getInjury().getId() : 0));
    pByteList.addSmallInt(((getInjuryDecay() != null) ? getInjuryDecay().getId() : 0));
    pByteList.addByteArray(getCasualtyRoll());
    pByteList.addByte((byte) ((getSeriousInjury() != null) ? getSeriousInjury().getId() : 0));
    pByteList.addByteArray(getCasualtyRollDecay());
    pByteList.addByte((byte) ((getSeriousInjuryDecay() != null) ? getSeriousInjuryDecay().getId() : 0));
    pByteList.addByte((byte) ((getApothecaryStatus() != null) ? getApothecaryStatus().getId() : 0));
    pByteList.addByte((byte) ((getSendToBoxReason() != null) ? getSendToBoxReason().getId() : 0));
    pByteList.addByte((byte) getSendToBoxTurn());
    pByteList.addByte((byte) getSendToBoxHalf());
    pByteList.addByte((byte) ((getSound() != null) ? getSound().getId() : 0));
    pByteList.addByte((byte) ((getApothecaryMode() != null) ? getApothecaryMode().getId() : 0));
  }

  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    setInjuryType(new InjuryTypeFactory().forId(pByteArray.getByte()));
    setDefenderId(pByteArray.getString());
    setDefenderCoordinate(pByteArray.getFieldCoordinate());
    setAttackerId(pByteArray.getString());
    ArmorModifierFactory armorModifierFactory = new ArmorModifierFactory();
    int nrOfArmorModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfArmorModifiers; i++) {
      addArmorModifier(armorModifierFactory.forId(pByteArray.getByte()));
    }
    setArmorRoll(pByteArray.getByteArrayAsIntArray());
    setArmorBroken(pByteArray.getBoolean());
    InjuryModifierFactory injuryModifierFactory = new InjuryModifierFactory();
    int nrOfInjuryModifiers = pByteArray.getByte();
    for (int i = 0; i < nrOfInjuryModifiers; i++) {
      addInjuryModifier(injuryModifierFactory.forId(pByteArray.getByte()));
    }
    setInjuryRoll(pByteArray.getByteArrayAsIntArray());
    setInjury(new PlayerState(pByteArray.getSmallInt()));
    setInjuryDecay(new PlayerState(pByteArray.getSmallInt()));
    setCasualtyRoll(pByteArray.getByteArrayAsIntArray());
    setSeriousInjury(new SeriousInjuryFactory().forId(pByteArray.getByte()));
    setCasualtyRollDecay(pByteArray.getByteArrayAsIntArray());
    setSeriousInjuryDecay(new SeriousInjuryFactory().forId(pByteArray.getByte()));
    setApothecaryStatus(new ApothecaryStatusFactory().forId(pByteArray.getByte()));
    setSendToBoxReason(new SendToBoxReasonFactory().forId(pByteArray.getByte()));
    setSendToBoxTurn(pByteArray.getByte());
    setSendToBoxHalf(pByteArray.getByte());
    setSound(new SoundFactory().forId(pByteArray.getByte()));
    if (byteArraySerializationVersion > 1) {
    	setApothecaryMode(new ApothecaryModeFactory().forId(pByteArray.getByte()));
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {

    JsonObject jsonObject = new JsonObject();
    
    IServerJsonOption.INJURY_TYPE.addTo(jsonObject, fInjuryType);
    IServerJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
    IServerJsonOption.DEFENDER_POSITION.addTo(jsonObject, fDefenderPosition);
    IServerJsonOption.ATTACKER_ID.addTo(jsonObject, fAttackerId);
    IServerJsonOption.ARMOR_ROLL.addTo(jsonObject, fArmorRoll);
    IServerJsonOption.ARMOR_BROKEN.addTo(jsonObject, fArmorBroken);
    IServerJsonOption.INJURY_ROLL.addTo(jsonObject, fInjuryRoll);
    IServerJsonOption.INJURY.addTo(jsonObject, fInjury);
    IServerJsonOption.INJURY_DECAY.addTo(jsonObject, fInjuryDecay);
    IServerJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
    IServerJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
    IServerJsonOption.CASUALTY_ROLL_DECAY.addTo(jsonObject, fCasualtyRollDecay);
    IServerJsonOption.SERIOUS_INJURY_DECAY.addTo(jsonObject, fSeriousInjuryDecay);
    IServerJsonOption.APOTHECARY_STATUS.addTo(jsonObject, fApothecaryStatus);
    IServerJsonOption.SEND_TO_BOX_REASON.addTo(jsonObject, fSendToBoxReason);
    IServerJsonOption.SEND_TO_BOX_TURN.addTo(jsonObject, fSendToBoxTurn);
    IServerJsonOption.SEND_TO_BOX_HALF.addTo(jsonObject, fSendToBoxHalf);
    IServerJsonOption.SOUND.addTo(jsonObject, fSound);
    IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, fApothecaryMode);

    JsonArray armorModifiers = new JsonArray();
    for (ArmorModifier armorModifier : getArmorModifiers()) {
      armorModifiers.add(UtilJson.toJsonValue(armorModifier));
    }
    IServerJsonOption.ARMOR_MODIFIERS.addTo(jsonObject, armorModifiers);

    JsonArray injuryModifiers = new JsonArray();
    for (InjuryModifier injuryModifier : getInjuryModifiers()) {
      injuryModifiers.add(UtilJson.toJsonValue(injuryModifier));
    }
    IServerJsonOption.INJURY_MODIFIERS.addTo(jsonObject, injuryModifiers);

    return jsonObject;
    
  }
  
  public InjuryResult initFrom(JsonValue pJsonValue) {
    
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

    fInjuryType = (InjuryType) IServerJsonOption.INJURY_TYPE.getFrom(jsonObject);
    fDefenderId = IServerJsonOption.DEFENDER_ID.getFrom(jsonObject);
    fDefenderPosition = IServerJsonOption.DEFENDER_POSITION.getFrom(jsonObject);
    fAttackerId = IServerJsonOption.ATTACKER_ID.getFrom(jsonObject);
    fArmorRoll = IServerJsonOption.ARMOR_ROLL.getFrom(jsonObject);
    fArmorBroken = IServerJsonOption.ARMOR_BROKEN.getFrom(jsonObject);
    fInjuryRoll = IServerJsonOption.INJURY_ROLL.getFrom(jsonObject);
    fInjury = IServerJsonOption.INJURY.getFrom(jsonObject);
    fInjuryDecay = IServerJsonOption.INJURY_DECAY.getFrom(jsonObject);
    fCasualtyRoll = IServerJsonOption.CASUALTY_ROLL.getFrom(jsonObject);
    fSeriousInjury = (SeriousInjury) IServerJsonOption.SERIOUS_INJURY.getFrom(jsonObject);
    fCasualtyRollDecay = IServerJsonOption.CASUALTY_ROLL_DECAY.getFrom(jsonObject);
    fSeriousInjuryDecay = (SeriousInjury) IServerJsonOption.SERIOUS_INJURY_DECAY.getFrom(jsonObject);
    fApothecaryStatus = (ApothecaryStatus) IServerJsonOption.APOTHECARY_STATUS.getFrom(jsonObject);
    fSendToBoxReason = (SendToBoxReason) IServerJsonOption.SEND_TO_BOX_REASON.getFrom(jsonObject);
    fSendToBoxTurn = IServerJsonOption.SEND_TO_BOX_TURN.getFrom(jsonObject);
    fSendToBoxHalf = IServerJsonOption.SEND_TO_BOX_HALF.getFrom(jsonObject);
    fSound = (Sound) IServerJsonOption.SOUND.getFrom(jsonObject);
    fApothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(jsonObject);

    fArmorModifiers.clear();
    ArmorModifierFactory armorModifierFactory = new ArmorModifierFactory();
    JsonArray armorModifiers = IServerJsonOption.ARMOR_MODIFIERS.getFrom(jsonObject);
    for (int i = 0; i < armorModifiers.size(); i++) {
      fArmorModifiers.add((ArmorModifier) UtilJson.toEnumWithName(armorModifierFactory, armorModifiers.get(i)));
    }

    fInjuryModifiers.clear();
    InjuryModifierFactory injuryModifierFactory = new InjuryModifierFactory();
    JsonArray injuryModifiers = IServerJsonOption.INJURY_MODIFIERS.getFrom(jsonObject);
    for (int i = 0; i < injuryModifiers.size(); i++) {
      fInjuryModifiers.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, injuryModifiers.get(i)));
    }
    
    return this;
    
  }

}
