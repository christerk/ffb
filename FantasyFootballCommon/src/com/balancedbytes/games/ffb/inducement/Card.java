package com.balancedbytes.games.ffb.inducement;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.CardTarget;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.factory.InducementPhaseFactory;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;
import com.balancedbytes.games.ffb.modifiers.ArmorModifier;
import com.balancedbytes.games.ffb.modifiers.InjuryModifier;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.modifiers.TemporaryEnhancements;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public class Card implements INamedObject {

	private final String fName;
	private final String fShortName;
	private final CardType fType;
	private final CardTarget fTarget;
	private final boolean fRemainsInPlay;
	private final InducementPhase[] fPhases;
	private final InducementDuration fDuration;
	private final String fDescription;
	private final CardHandlerKey handlerKey;

	public Card(String pName, String pShortName, CardType pType, CardTarget pTarget, boolean pRemainsInPlay,
	            InducementPhase[] pPhases, InducementDuration pDuration, String pDescription) {
		this(pName, pShortName, pType, pTarget, pRemainsInPlay, pPhases, pDuration, pDescription, null);
	}

	public Card(String pName, String pShortName, CardType pType, CardTarget pTarget, boolean pRemainsInPlay,
	     InducementPhase[] pPhases, InducementDuration pDuration, String pDescription, CardHandlerKey handlerKey) {
		fName = pName;
		fShortName = pShortName;
		fType = pType;
		fTarget = pTarget;
		fRemainsInPlay = pRemainsInPlay;
		fPhases = pPhases;
		fDuration = pDuration;
		fDescription = pDescription;
		this.handlerKey = handlerKey;
	}

	public String getName() {
		return fName;
	}

	public String getShortName() {
		return fShortName;
	}

	public CardType getType() {
		return fType;
	}

	public CardTarget getTarget() {
		return fTarget;
	}

	public boolean isRemainsInPlay() {
		return fRemainsInPlay;
	}

	public InducementPhase[] getPhases() {
		return fPhases;
	}

	public InducementDuration getDuration() {
		return fDuration;
	}

	public String getDescription() {
		return fDescription;
	}

	public String getHtmlDescription() {
		return getDescription() +
			"<br>" +
			getDuration().getDescription();
	}

	public String getHtmlDescriptionWithPhases() {
		return getHtmlDescription() +
			"<br>" +
			new InducementPhaseFactory().getDescription(getPhases());
	}

	public Set<RollModifier<?>> rollModifiers() {
		return Collections.emptySet();
	}

	public Set<ArmorModifier> armourModifiers() {
		return Collections.emptySet();
	}

	public Set<InjuryModifier> injuryModifiers() {
		return Collections.emptySet();
	}

	public TemporaryEnhancements activationEnhancement() { return new TemporaryEnhancements(); }

	public TemporaryEnhancements deactivationEnhancement() { return new TemporaryEnhancements(); }

	public Optional<CardReport> cardReport(CardEffect effect, int roll) {
		return Optional.empty();
	}

	public Optional<CardHandlerKey> handlerKey() {
		return Optional.ofNullable(handlerKey);
	}

	public Set<ISkillProperty> globalProperties() { return Collections.emptySet(); }

	public boolean requiresBlockablePlayerSelection() { return false; }

	public static Comparator<Card> createComparator() {
		return Comparator.comparing(Card::getName);
	}
}
