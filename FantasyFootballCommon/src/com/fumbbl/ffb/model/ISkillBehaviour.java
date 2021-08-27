package com.fumbbl.ffb.model;

import java.util.List;

import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.model.skill.Skill;

public interface ISkillBehaviour<T extends Skill> extends IKeyedItem {

	List<PlayerModifier> getPlayerModifiers();
}
