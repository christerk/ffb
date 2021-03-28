package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;

public interface Position extends IXmlSerializable, IJsonSerializable {
	PlayerType getType();

	PlayerGender getGender();

	int getMovement();
	int getStrength();
	int getAgility();
	int getPassing();
	int getArmour();

	int getCost();

	String getName();

	String getShorthand();

	boolean hasSkill(Skill pSkill);

	Skill[] getSkills();

	String getSkillValue(Skill pSkill);

	int getSkillIntValue(Skill skill);

	String getUrlPortrait();

	void setUrlPortrait(String pUrlPortrait);

	String getUrlIconSet();

	int getQuantity();

	Roster getRoster();

	String getId();

	int getNrOfIcons();

	int findNextIconSetIndex();

	String getDisplayName();

	String getRace();

	boolean isUndead();

	boolean isThrall();

	String getTeamWithPositionId();

	boolean isDoubleCategory(SkillCategory category);

	SkillCategory[] getSkillCategories(boolean b);
}
