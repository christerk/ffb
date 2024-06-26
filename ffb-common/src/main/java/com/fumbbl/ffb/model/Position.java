package com.fumbbl.ffb.model;

import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.xml.IXmlSerializable;

import java.util.List;

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

	String getDisplayValue(Skill pSkill);

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

	List<Keyword> getKeywords();

	boolean isDwarf();
}
