package com.fumbbl.ffb.model;

import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.List;

public interface ISkillBehaviour<T extends Skill> extends IKeyedItem {

	IInjuryContextModification getInjuryContextModification();

	List<PlayerModifier> getPlayerModifiers();

	boolean hasInjuryModifier(InjuryType injuryType);
}
