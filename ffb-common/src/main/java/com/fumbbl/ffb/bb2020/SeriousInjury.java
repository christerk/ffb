package com.fumbbl.ffb.bb2020;

import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.RulesCollection;

@RulesCollection(RulesCollection.Rules.BB2020)
public enum SeriousInjury implements com.fumbbl.ffb.SeriousInjury {

	SERIOUSLY_HURT("Seriously Hurt (MNG)", "Seriously Hurt (Miss next game)", "is seriously hurt (Miss next game)",
			"has been seriously hurt in the previous game (Miss Next Game)", null, false),
	SERIOUS_INJURY("Serious Injury (NI)", "Serious Injury (Niggling Injury)", "is seriously injured (Niggling Injury)",
			"has been seriously injured in the previous game (Niggling Injury)", InjuryAttribute.NI, false),
	HEAD_INJURY("Head Injury (-AV)", "Head Injury (-1 AV)", "suffered a head injury (-1 AV)",
		"is recovering from a head injury (-1 AV)", InjuryAttribute.AV, true),
	SMASHED_KNEE("Smashed Knee (-MA)", "Smashed Knee (-1 MA)", "suffered a smashed knee (-1 MA)",
			"is recovering from a smashed knee (-1 MA)", InjuryAttribute.MA, true),
	BROKEN_ARM("Broken Arm (-PA)", "Broken Arm (-1 PA)", "suffered a broken arm (-1 PA)",
			"is recovering from a broken arm (-1 PA)", InjuryAttribute.PA, true),
	NECK_INJURY("Neck Injury (-AG)", "Neck Injury (-1 AG)", "suffered a neck injury (-1 AG)",
			"is recovering from a neck injury (-1 AG)", InjuryAttribute.AG, true),
	DISLOCATED_SHOULDER("Dislocated Shoulder (-ST)", "Dislocated Shoulder (-1 ST)",
			"suffered a dislocated shoulder (-1 ST)", "is recovering from a dislocated shoulder (-1 ST)",
		InjuryAttribute.ST, true),
	DEAD("Dead (RIP)", "Dead (RIP)", "is dead", "is dead", null, false);

	private final String fName;
	private final String fButtonText;
	private final String fDescription;
	private final String fRecovery;
	private final boolean showSiRoll;
	private final InjuryAttribute fInjuryAttribute;

	SeriousInjury(String pName, String pButtonText, String pDescription, String pRecovery,
								InjuryAttribute pInjuryAttribute, boolean showSiRoll) {
		fName = pName;
		fButtonText = pButtonText;
		fDescription = pDescription;
		fRecovery = pRecovery;
		fInjuryAttribute = pInjuryAttribute;
		this.showSiRoll = showSiRoll;
	}

	public String getName() {
		return fName;
	}

	public String getButtonText() {
		return fButtonText;
	}

	public String getDescription() {
		return fDescription;
	}

	public String getRecovery() {
		return fRecovery;
	}

	public InjuryAttribute getInjuryAttribute() {
		return fInjuryAttribute;
	}

	@Override
	public boolean isDead() {
		return this == DEAD;
	}

	@Override
	public boolean isPoison() {
		return false;
	}

	@Override
	public boolean showSiRoll() {
		return showSiRoll;
	}


}
