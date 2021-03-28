package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IKeyedItem;
import com.balancedbytes.games.ffb.model.skill.Skill;

import java.util.List;

public interface ISkillBehaviour<T extends Skill> extends IKeyedItem {

	List<PlayerModifier> getPlayerModifiers();
}
