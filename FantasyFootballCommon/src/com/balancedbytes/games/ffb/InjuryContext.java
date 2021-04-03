package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.bb2020.CasualtyModifier;

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
	public SendToBoxReason fSendToBoxReason;
	public int fSendToBoxTurn;
	public int fSendToBoxHalf;
	public SoundId fSound;
	public PlayerState fSufferedInjury;
	public ApothecaryMode fApothecaryMode;
	public ApothecaryStatus fApothecaryStatus;
	public Set<CasualtyModifier> casualtyModifiers;

	public InjuryContext() {
		fArmorModifiers = new HashSet<>();
		fInjuryModifiers = new HashSet<>();
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

	public void addArmorModifiers(Set<ArmorModifier> pArmorModifiers) {
		fArmorModifiers.addAll(pArmorModifiers);
	}

	public boolean hasArmorModifier(ArmorModifier pArmorModifier) {
		return fArmorModifiers.contains(pArmorModifier);
	}

	public int getArmorModifierTotal(Game game) {
		int totalModifiers = 0;
		for (ArmorModifier armorModifier : fArmorModifiers) {
			totalModifiers += armorModifier.getModifier(game.getPlayerById(fAttackerId));
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

	public void addInjuryModifiers(Set<InjuryModifier> pInjuryModifiers) {
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

	public void setCasualtyModifiers(Set<CasualtyModifier> casualtyModifiers) {
		this.casualtyModifiers = casualtyModifiers;
	}
}
