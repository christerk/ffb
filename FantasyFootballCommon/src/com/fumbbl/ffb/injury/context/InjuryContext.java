package com.fumbbl.ffb.injury.context;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.ApothecaryStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifierFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InjuryContext {
	public InjuryType fInjuryType;
	public String fDefenderId;
	public FieldCoordinate fDefenderPosition;
	public String fAttackerId;
	public Set<ArmorModifier> fArmorModifiers;
	public int[] fArmorRoll;
	public boolean fArmorBroken;
	public Set<InjuryModifier> fInjuryModifiers;
	public int[] fInjuryRoll;
	public int[] fCasualtyRoll;
	public int[] fCasualtyRollDecay;
	public PlayerState fInjury;
	public PlayerState fInjuryDecay;
	public SeriousInjury fSeriousInjury;
	public SeriousInjury fSeriousInjuryDecay;
	public SeriousInjury originalSeriousInjury;
	public SendToBoxReason fSendToBoxReason;
	public int fSendToBoxTurn;
	public int fSendToBoxHalf;
	public SoundId fSound;
	public PlayerState fSufferedInjury;
	public ApothecaryMode fApothecaryMode;
	public ApothecaryStatus fApothecaryStatus;
	public Set<CasualtyModifier> casualtyModifiers;

	private ModifiedInjuryContext modifiedInjuryContext;

	public InjuryContext() {
		fArmorModifiers = new HashSet<>();
		fInjuryModifiers = new HashSet<>();
		casualtyModifiers = new HashSet<>();
	}

	public ModifiedInjuryContext getModifiedInjuryContext() {
		return modifiedInjuryContext;
	}

	public void setModifiedInjuryContext(ModifiedInjuryContext modifiedInjuryContext) {
		this.modifiedInjuryContext = modifiedInjuryContext;
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

	public void addArmorModifiers(Collection<ArmorModifier> pArmorModifiers) {
		fArmorModifiers.addAll(pArmorModifiers);
	}

	public boolean hasArmorModifier(ArmorModifier pArmorModifier) {
		return fArmorModifiers.contains(pArmorModifier);
	}

	public int getArmorModifierTotal(Game game) {
		int totalModifiers = 0;
		for (ArmorModifier armorModifier : fArmorModifiers) {
			totalModifiers += armorModifier.getModifier(game.getPlayerById(fAttackerId), game.getPlayerById(fDefenderId));
		}
		return totalModifiers;
	}

	public ArmorModifier[] getArmorModifiers() {
		return fArmorModifiers.toArray(new ArmorModifier[0]);
	}

	public void clearArmorModifiers() {
		fArmorModifiers.clear();
	}

	public void addInjuryModifier(InjuryModifier pInjuryModifier) {
		if (pInjuryModifier != null) {
			fInjuryModifiers.add(pInjuryModifier);
		}
	}

	public void addInjuryModifiers(Collection<InjuryModifier> pInjuryModifiers) {
		fInjuryModifiers.addAll(pInjuryModifiers);
	}

	public boolean hasInjuryModifier(InjuryModifier pInjuryModifier) {
		return fInjuryModifiers.contains(pInjuryModifier);
	}

	public int getInjuryModifierTotal(Game game) {
		int totalModifiers = 0;
		for (InjuryModifier injuryModifier : fInjuryModifiers) {
			totalModifiers += injuryModifier.getModifier(game.getPlayerById(fAttackerId), game.getPlayerById(fDefenderId));
		}
		return totalModifiers;
	}

	public InjuryModifier[] getInjuryModifiers() {
		return fInjuryModifiers.toArray(new InjuryModifier[0]);
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

	public ApothecaryMode getApothecaryMode() {
		return fApothecaryMode;
	}

	public void setApothecaryMode(ApothecaryMode pApothecaryMode) {
		fApothecaryMode = pApothecaryMode;
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

	public boolean isBadlyHurt() {
		return ((getPlayerState() != null) && (getPlayerState().getBase() == PlayerState.BADLY_HURT));
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

	public void setSound(SoundId pSound) {
		fSound = pSound;
	}

	public SoundId getSound() {
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

	public Set<CasualtyModifier> getCasualtyModifiers() {
		return casualtyModifiers;
	}

	public void addCasualtyModifiers(Set<CasualtyModifier> casualtyModifiers) {
		this.casualtyModifiers.addAll(casualtyModifiers);
	}

	public SeriousInjury getOriginalSeriousInjury() {
		return originalSeriousInjury;
	}

	public void setOriginalSeriousInjury(SeriousInjury originalSeriousInjury) {
		this.originalSeriousInjury = originalSeriousInjury;
	}

	public void toJsonValue(JsonObject jsonObject) {

		IJsonOption.INJURY_TYPE.addTo(jsonObject, fInjuryType);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.DEFENDER_POSITION.addTo(jsonObject, fDefenderPosition);
		IJsonOption.ATTACKER_ID.addTo(jsonObject, fAttackerId);
		IJsonOption.ARMOR_ROLL.addTo(jsonObject, fArmorRoll);
		IJsonOption.ARMOR_BROKEN.addTo(jsonObject, fArmorBroken);
		IJsonOption.INJURY_ROLL.addTo(jsonObject, fInjuryRoll);
		IJsonOption.INJURY.addTo(jsonObject, fInjury);
		IJsonOption.INJURY_DECAY.addTo(jsonObject, fInjuryDecay);
		IJsonOption.CASUALTY_ROLL.addTo(jsonObject, fCasualtyRoll);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, fSeriousInjury);
		IJsonOption.SERIOUS_INJURY_OLD.addTo(jsonObject, originalSeriousInjury);
		IJsonOption.CASUALTY_ROLL_DECAY.addTo(jsonObject, fCasualtyRollDecay);
		IJsonOption.SERIOUS_INJURY_DECAY.addTo(jsonObject, fSeriousInjuryDecay);
		IJsonOption.APOTHECARY_STATUS.addTo(jsonObject, fApothecaryStatus);
		IJsonOption.SEND_TO_BOX_REASON.addTo(jsonObject, fSendToBoxReason);
		IJsonOption.SEND_TO_BOX_TURN.addTo(jsonObject, fSendToBoxTurn);
		IJsonOption.SEND_TO_BOX_HALF.addTo(jsonObject, fSendToBoxHalf);
		IJsonOption.SOUND.addTo(jsonObject, fSound);
		IJsonOption.APOTHECARY_MODE.addTo(jsonObject, fApothecaryMode);

		JsonArray armorModifiers = new JsonArray();
		for (ArmorModifier armorModifier : getArmorModifiers()) {
			armorModifiers.add(UtilJson.toJsonValue(armorModifier));
		}
		IJsonOption.ARMOR_MODIFIERS.addTo(jsonObject, armorModifiers);

		JsonArray injuryModifiers = new JsonArray();
		for (InjuryModifier injuryModifier : getInjuryModifiers()) {
			injuryModifiers.add(UtilJson.toJsonValue(injuryModifier));
		}
		IJsonOption.INJURY_MODIFIERS.addTo(jsonObject, injuryModifiers);

		JsonArray casualtyModifiers = new JsonArray();
		getCasualtyModifiers().forEach(modifier -> casualtyModifiers.add(UtilJson.toJsonValue(modifier)));
		IJsonOption.CASUALTY_MODIFIERS.addTo(jsonObject, casualtyModifiers);

		if (modifiedInjuryContext != null) {
			JsonObject alternateContext = new JsonObject();
			modifiedInjuryContext.toJsonValue(alternateContext);
			IJsonOption.MODIFIED_INJURY_CONTEXT.addTo(jsonObject, alternateContext);
		}

	}


	public void initFrom(IFactorySource source, JsonObject jsonObject) {

		fInjuryType = (InjuryType) IJsonOption.INJURY_TYPE.getFrom(source, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fDefenderPosition = IJsonOption.DEFENDER_POSITION.getFrom(source, jsonObject);
		fAttackerId = IJsonOption.ATTACKER_ID.getFrom(source, jsonObject);
		fArmorRoll = IJsonOption.ARMOR_ROLL.getFrom(source, jsonObject);
		fArmorBroken = IJsonOption.ARMOR_BROKEN.getFrom(source, jsonObject);
		fInjuryRoll = IJsonOption.INJURY_ROLL.getFrom(source, jsonObject);
		fInjury = IJsonOption.INJURY.getFrom(source, jsonObject);
		fInjuryDecay = IJsonOption.INJURY_DECAY.getFrom(source, jsonObject);
		fCasualtyRoll = IJsonOption.CASUALTY_ROLL.getFrom(source, jsonObject);
		fSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		fCasualtyRollDecay = IJsonOption.CASUALTY_ROLL_DECAY.getFrom(source, jsonObject);
		originalSeriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY_OLD.getFrom(source, jsonObject);
		fSeriousInjuryDecay = (SeriousInjury) IJsonOption.SERIOUS_INJURY_DECAY.getFrom(source, jsonObject);
		fApothecaryStatus = (ApothecaryStatus) IJsonOption.APOTHECARY_STATUS.getFrom(source, jsonObject);
		fSendToBoxReason = (SendToBoxReason) IJsonOption.SEND_TO_BOX_REASON.getFrom(source, jsonObject);
		fSendToBoxTurn = IJsonOption.SEND_TO_BOX_TURN.getFrom(source, jsonObject);
		fSendToBoxHalf = IJsonOption.SEND_TO_BOX_HALF.getFrom(source, jsonObject);
		fSound = (SoundId) IJsonOption.SOUND.getFrom(source, jsonObject);
		fApothecaryMode = (ApothecaryMode) IJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);

		fArmorModifiers.clear();
		ArmorModifierFactory armorModifierFactory = source.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
		JsonArray armorModifiers = IJsonOption.ARMOR_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < armorModifiers.size(); i++) {
			fArmorModifiers
				.add((ArmorModifier) UtilJson.toEnumWithName(armorModifierFactory, armorModifiers.get(i)));
		}

		fInjuryModifiers.clear();
		InjuryModifierFactory injuryModifierFactory = source.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		JsonArray injuryModifiers = IJsonOption.INJURY_MODIFIERS.getFrom(source, jsonObject);
		for (int i = 0; i < injuryModifiers.size(); i++) {
			fInjuryModifiers
				.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, injuryModifiers.get(i)));
		}

		casualtyModifiers.clear();
		CasualtyModifierFactory casualtyModifierFactory = source.getFactory(FactoryType.Factory.CASUALTY_MODIFIER);
		JsonArray casualtyModifiersArray = IJsonOption.CASUALTY_MODIFIERS.getFrom(source, jsonObject);
		casualtyModifiersArray.values().forEach(jsonValue -> casualtyModifiers
			.add((CasualtyModifier) UtilJson.toEnumWithName(casualtyModifierFactory, jsonValue)));

		JsonObject alternateContext = IJsonOption.MODIFIED_INJURY_CONTEXT.getFrom(source, jsonObject);

		if (alternateContext != null) {
			modifiedInjuryContext = new ModifiedInjuryContext();
			modifiedInjuryContext.initFrom(source, alternateContext);
		}

	}

}
