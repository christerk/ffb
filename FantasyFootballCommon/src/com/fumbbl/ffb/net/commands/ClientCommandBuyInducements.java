package com.fumbbl.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandBuyInducements extends ClientCommand {

	private String fTeamId;
	private int fAvailableGold;
	private InducementSet fInducementSet;
	private List<String> fStarPlayerPositionIds;
	private List<String> fMercenaryPositionIds;
	private List<Skill> fMercenarySkills;

	public ClientCommandBuyInducements() {
		fStarPlayerPositionIds = new ArrayList<>();
		fMercenaryPositionIds = new ArrayList<>();
		fMercenarySkills = new ArrayList<>();
	}

	public ClientCommandBuyInducements(String pTeamId, int pAvailableGold, InducementSet pInducementSet,
			String[] pStarPlayerPositionIds, String[] pMercenaryPositionIds, Skill[] pMercenarySkills) {
		this();
		fTeamId = pTeamId;
		fAvailableGold = pAvailableGold;
		fInducementSet = pInducementSet;
		if (ArrayTool.isProvided(pStarPlayerPositionIds)) {
			for (String starPlayerPositionId : pStarPlayerPositionIds) {
				addStarPlayerPositionId(starPlayerPositionId);
			}
		}
		if (ArrayTool.isProvided(pMercenaryPositionIds) && ArrayTool.isProvided(pMercenarySkills)) {
			for (int i = 0; i < pMercenaryPositionIds.length; i++) {
				addMercenaryPosition(pMercenaryPositionIds[i], pMercenarySkills[i]);
			}
		}
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BUY_INDUCEMENTS;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public InducementSet getInducementSet() {
		return fInducementSet;
	}

	public String[] getStarPlayerPositionIds() {
		return fStarPlayerPositionIds.toArray(new String[fStarPlayerPositionIds.size()]);
	}

	public int getNrOfStarPlayerPositions() {
		return fStarPlayerPositionIds.size();
	}

	public void addStarPlayerPositionId(String pStarPlayerPositionId) {
		fStarPlayerPositionIds.add(pStarPlayerPositionId);
	}

	public void addMercenaryPosition(String pMercenaryPositionId, Skill pMercenarySkill) {
		fMercenaryPositionIds.add(pMercenaryPositionId);
		fMercenarySkills.add(pMercenarySkill);
	}

	public int getNrOfMercenaryPositions() {
		return fMercenaryPositionIds.size();
	}

	public String[] getMercenaryPositionIds() {
		return fMercenaryPositionIds.toArray(new String[fMercenaryPositionIds.size()]);
	}

	public Skill[] getMercenarySkills() {
		return fMercenarySkills.toArray(new Skill[fMercenarySkills.size()]);
	}

	public int getAvailableGold() {
		return fAvailableGold;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TEAM_ID.addTo(jsonObject, fTeamId);
		if (fInducementSet != null) {
			IJsonOption.INDUCEMENT_SET.addTo(jsonObject, fInducementSet.toJsonValue());
		}
		IJsonOption.STAR_PLAYER_POSTION_IDS.addTo(jsonObject, fStarPlayerPositionIds);
		IJsonOption.AVAILABLE_GOLD.addTo(jsonObject, fAvailableGold);
		IJsonOption.MERCENARY_POSTION_IDS.addTo(jsonObject, fMercenaryPositionIds);
		String[] mercenarySkillNames = new String[fMercenarySkills.size()];
		for (int i = 0; i < mercenarySkillNames.length; i++) {
			Skill skill = fMercenarySkills.get(i);
			mercenarySkillNames[i] = (skill != null) ? skill.getName() : "";
		}
		IJsonOption.MERCENARY_SKILLS.addTo(jsonObject, mercenarySkillNames);
		return jsonObject;
	}

	public ClientCommandBuyInducements initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fTeamId = IJsonOption.TEAM_ID.getFrom(source, jsonObject);
		fInducementSet = new InducementSet();
		JsonObject inducementSetObject = IJsonOption.INDUCEMENT_SET.getFrom(source, jsonObject);
		if (inducementSetObject != null) {
			fInducementSet.initFrom(source, inducementSetObject);
		}
		String[] starPlayerPositionIds = IJsonOption.STAR_PLAYER_POSTION_IDS.getFrom(source, jsonObject);
		for (String positionId : starPlayerPositionIds) {
			addStarPlayerPositionId(positionId);
		}
		fAvailableGold = IJsonOption.AVAILABLE_GOLD.getFrom(source, jsonObject);
		String[] mercenaryPositionIds = IJsonOption.MERCENARY_POSTION_IDS.getFrom(source, jsonObject);
		String[] mercenarySkillNames = IJsonOption.MERCENARY_SKILLS.getFrom(source, jsonObject);
		if (StringTool.isProvided(mercenaryPositionIds) && StringTool.isProvided(mercenarySkillNames)) {
			SkillFactory skillFactory = source.<SkillFactory>getFactory(Factory.SKILL);
			for (int i = 0; i < mercenaryPositionIds.length; i++) {
				addMercenaryPosition(mercenaryPositionIds[i], skillFactory.forName(mercenarySkillNames[i]));
			}
		}
		return this;
	}

}
