package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.IKeyedItem;

import java.util.List;

public interface ISkillBehaviour<T extends Skill> extends IKeyedItem {

	List<PlayerModifier> getPlayerModifiers();
}
