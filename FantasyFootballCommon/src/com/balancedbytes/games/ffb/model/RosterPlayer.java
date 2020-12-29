package com.balancedbytes.games.ffb.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.factory.PlayerGenderFactory;
import com.balancedbytes.games.ffb.factory.PlayerTypeFactory;
import com.balancedbytes.games.ffb.factory.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class RosterPlayer extends Player<RosterPosition> {

	static final String XML_TAG = "player";
	public static final String KIND = "rosterPlayer";

	private String fId;
	private int fNr;
	private Team fTeam;
	private String fName;
	private PlayerType fPlayerType;
	private PlayerGender fPlayerGender;
	private int fMovement;
	private int fStrength;
	private int fAgility;
	private int fArmour;

	private String fUrlPortrait;
	private String fUrlIconSet;
	private int fNrOfIcons;

	private String fPositionId;
	private transient int fIconSetIndex;

	private List<Skill> fSkills;
	private List<SeriousInjury> fLastingInjuries;
	private SeriousInjury fRecoveringInjury;

	private transient RosterPosition fPosition;
	private transient int fCurrentSpps;

	// attributes used for parsing
	private transient boolean fInsideSkillList;
	private transient boolean fInsideInjuryList;
	private transient boolean fInjuryCurrent;

	public RosterPlayer() {
		fLastingInjuries = new ArrayList<SeriousInjury>();
		fSkills = new ArrayList<Skill>();
		setGender(PlayerGender.MALE);
		fIconSetIndex = 0;
		fPosition = new RosterPosition(null);
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public PlayerType getPlayerType() {
		return fPlayerType;
	}

	@Override
	public void setType(PlayerType pType) {
		fPlayerType = pType;
	}

	@Override
	public int getNr() {
		return fNr;
	}

	@Override
	public int getAgility() {
		return fAgility;
	}

	@Override
	public void setAgility(int pAgility) {
		fAgility = pAgility;
	}

	@Override
	public int getArmour() {
		return fArmour;
	}

	@Override
	public void setArmour(int pArmour) {
		fArmour = pArmour;
	}

	@Override
	public int getMovement() {
		return fMovement;
	}

	@Override
	public void setMovement(int pMovement) {
		fMovement = pMovement;
	}

	@Override
	public int getStrength() {
		return fStrength;
	}

	@Override
	public void setStrength(int pStrength) {
		fStrength = pStrength;
	}

	@Override
	public void addLastingInjury(SeriousInjury pLastingInjury) {
		if (pLastingInjury != null) {
			fLastingInjuries.add(pLastingInjury);
		}
	}

	@Override
	public SeriousInjury[] getLastingInjuries() {
		return fLastingInjuries.toArray(new SeriousInjury[fLastingInjuries.size()]);
	}

	@Override
	public void addSkill(Skill pSkill) {
		if ((pSkill != null) && ((pSkill.getCategory() == SkillCategory.STAT_INCREASE)
				|| (pSkill.getCategory() == SkillCategory.STAT_DECREASE) || !fSkills.contains(pSkill))) {
			fSkills.add(pSkill);
		}
	}

	@Override
	public boolean removeSkill(Skill pSkill) {
		return fSkills.remove(pSkill);
	}

	@Override
	public boolean hasSkill(Skill pSkill) {
		return fSkills.contains(pSkill);
	}

	@Override
	public Skill[] getSkills() {
		return fSkills.toArray(new Skill[fSkills.size()]);
	}

	@Override
	public String getUrlPortrait() {
		return fUrlPortrait;
	}

	@Override
	public void setUrlPortrait(String pUrlPortrait) {
		fUrlPortrait = pUrlPortrait;
	}

	@Override
	public String getUrlIconSet() {
		return fUrlIconSet;
	}

	@Override
	public void setUrlIconSet(String pUrlIconSet) {
		fUrlIconSet = pUrlIconSet;
	}

	@Override
	public int getNrOfIcons() {
		return fNrOfIcons;
	}

	@Override
	public void setNrOfIcons(int pNrOfIcons) {
		fNrOfIcons = pNrOfIcons;
	}

	@Override
	public RosterPosition getPosition() {
		return fPosition;
	}

	@Override
	public void updatePosition(RosterPosition pPosition) {
		fPosition = pPosition;
		if (fPosition != null) {
			setPositionId(fPosition.getId());
			if (getPlayerType() == null) {
				setType(fPosition.getType());
			}
			setMovement(fPosition.getMovement());
			setStrength(fPosition.getStrength());
			setAgility(fPosition.getAgility());
			setArmour(fPosition.getArmour());
			fIconSetIndex = pPosition.findNextIconSetIndex();
			for (Skill skill : fPosition.getSkills()) {
				addSkill(skill);
			}
			for (Skill skill : getSkills()) {
				if (skill != null) {
					for (PlayerModifier modifier : skill.getPlayerModifiers()) {
						modifier.apply(this);
					}
				}
			}
			int oldMovement = getMovement();
			int oldArmour = getArmour();
			int oldAgility = getAgility();
			int oldStrength = getStrength();
			for (SeriousInjury injury : getLastingInjuries()) {
				switch (injury) {
				case SMASHED_HIP:
				case SMASHED_ANKLE:
					if ((fMovement > 1) && ((oldMovement - fMovement) < 2)) {
						fMovement--;
					}
					break;
				case SERIOUS_CONCUSSION:
				case FRACTURED_SKULL:
					if ((fArmour > 1) && ((oldArmour - fArmour) < 2)) {
						fArmour--;
					}
					break;
				case BROKEN_NECK:
					if ((fAgility > 1) && ((oldAgility - fAgility) < 2)) {
						fAgility--;
					}
					break;
				case SMASHED_COLLAR_BONE:
					if ((fStrength > 1) && ((oldStrength - fStrength) < 2)) {
						fStrength--;
					}
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public Team getTeam() {
		return fTeam;
	}

	@Override
	public void setTeam(Team pTeam) {
		fTeam = pTeam;
	}

	@Override
	public String getId() {
		return fId;
	}

	@Override
	public void setId(String pId) {
		fId = pId;
	}

	@Override
	public PlayerGender getPlayerGender() {
		return fPlayerGender;
	}

	@Override
	public SeriousInjury getRecoveringInjury() {
		return fRecoveringInjury;
	}

	@Override
	public void setRecoveringInjury(SeriousInjury pCurrentInjury) {
		fRecoveringInjury = pCurrentInjury;
	}

	@Override
	public int getCurrentSpps() {
		return fCurrentSpps;
	}

	@Override
	public void setCurrentSpps(int pCurrentSpps) {
		fCurrentSpps = pCurrentSpps;
	}

	@Override
	public void setName(String name) {
		fName = name;
	}

	@Override
	public void setGender(PlayerGender gender) {
		fPlayerGender = gender;
	}

	@Override
	public void setNr(int nr) {
		fNr = nr;
	}

	@Override
	public int getIconSetIndex() {
		return fIconSetIndex;
	}

	@Override
	public String getPositionId() {
		return fPositionId;
	}

	@Override
	public void setPositionId(String pPositionId) {
		fPositionId = pPositionId;
	}

	@Override
	public String getRace() {
		return getPosition().getRace();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Player<?> other = (Player<?>) obj;
		return getId().equals(other.getId());
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_NR, getNr());
		UtilXml.startElement(pHandler, XML_TAG, attributes);

		UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
		UtilXml.addValueElement(pHandler, _XML_TAG_GENDER,
				(getPlayerGender() != null) ? getPlayerGender().getName() : null);
		UtilXml.addValueElement(pHandler, _XML_TAG_POSITION_ID, getPositionId());
		UtilXml.addValueElement(pHandler, _XML_TAG_TYPE, (getPlayerType() != null) ? getPlayerType().getName() : null);

		UtilXml.addValueElement(pHandler, _XML_TAG_PORTRAIT, getUrlPortrait());

		attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SIZE, getNrOfIcons());
		UtilXml.startElement(pHandler, _XML_TAG_ICON_SET, attributes);
		UtilXml.addCharacters(pHandler, getUrlIconSet());
		UtilXml.endElement(pHandler, _XML_TAG_ICON_SET);

		UtilXml.startElement(pHandler, _XML_TAG_SKILL_LIST);
		if (fSkills.size() > 0) {
			for (Skill skill : fSkills) {
				UtilXml.addValueElement(pHandler, _XML_TAG_SKILL, skill.getName());
			}
		}
		UtilXml.endElement(pHandler, _XML_TAG_SKILL_LIST);

		UtilXml.startElement(pHandler, _XML_TAG_INJURY_LIST);
		if (fLastingInjuries.size() > 0) {
			for (SeriousInjury lastingInjury : fLastingInjuries) {
				UtilXml.addValueElement(pHandler, _XML_TAG_INJURY, lastingInjury.getName());
			}
		}
		UtilXml.endElement(pHandler, _XML_TAG_INJURY_LIST);

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlSerializable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlSerializable xmlElement = this;
		if (fInsideInjuryList) {
			if (_XML_TAG_INJURY.equals(pXmlTag)) {
				fInjuryCurrent = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_RECOVERING);
			}
		} else {
			if (XML_TAG.equals(pXmlTag)) {
				fId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
				setNr(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NR));
			}
			if (_XML_TAG_INJURY_LIST.equals(pXmlTag)) {
				fInsideInjuryList = true;
			}
			if (_XML_TAG_ICON_SET.equals(pXmlTag)) {
				setNrOfIcons(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_SIZE));
			}
			if (_XML_TAG_SKILL_LIST.equals(pXmlTag)) {
				fInsideSkillList = true;
			}
			if (_XML_TAG_PLAYER_STATISTICS.equals(pXmlTag)) {
				setCurrentSpps(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_CURRENT_SPPS));
			}
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		boolean complete = XML_TAG.equals(pXmlTag);
		if (!complete) {
			if (fInsideSkillList) {
				if (_XML_TAG_SKILL_LIST.equals(pXmlTag)) {
					fInsideSkillList = false;
				}
				if (_XML_TAG_SKILL.equals(pXmlTag)) {
					Skill skill = game.getRules().<SkillFactory>getFactory(Factory.skill).forName(pValue);
					if (skill != null) {
						fSkills.add(skill);
					}
				}
			} else if (fInsideInjuryList) {
				if (_XML_TAG_INJURY_LIST.equals(pXmlTag)) {
					fInsideInjuryList = false;
				}
				if (_XML_TAG_INJURY.equals(pXmlTag)) {
					SeriousInjury injury = new SeriousInjuryFactory().forName(pValue);
					if (injury != null) {
						fLastingInjuries.add(injury);
						if (fInjuryCurrent) {
							fRecoveringInjury = injury;
						}
					}
				}
			} else {
				if (_XML_TAG_PORTRAIT.equals(pXmlTag)) {
					setUrlPortrait(pValue);
				}
				if (_XML_TAG_ICON_SET.equals(pXmlTag)) {
					setUrlIconSet(pValue);
					if (getNrOfIcons() < 1) {
						setNrOfIcons(1);
					}
				}
				if (_XML_TAG_NAME.equals(pXmlTag)) {
					setName(pValue);
				}
				if (_XML_TAG_GENDER.equals(pXmlTag)) {
					setGender(new PlayerGenderFactory().forName(pValue));
					if (getPlayerGender() == null) {
						setGender(PlayerGender.MALE);
					}
				}
				if (_XML_TAG_POSITION_ID.equals(pXmlTag)) {
					setPositionId(pValue);
				}
				if (_XML_TAG_TYPE.equals(pXmlTag)) {
					setType(new PlayerTypeFactory().forName(pValue));
				}
				// attributes for special player definitions (without rosterPosition)
				if (_XML_TAG_MOVEMENT.equals(pXmlTag)) {
					setMovement(Integer.parseInt(pValue));
				}
				if (_XML_TAG_STRENGTH.equals(pXmlTag)) {
					setStrength(Integer.parseInt(pValue));
				}
				if (_XML_TAG_AGILITY.equals(pXmlTag)) {
					setAgility(Integer.parseInt(pValue));
				}
				if (_XML_TAG_ARMOUR.equals(pXmlTag)) {
					setArmour(Integer.parseInt(pValue));
				}
				if (_XML_TAG_RACE.equals(pXmlTag)) {
					getPosition().setRace(pValue);
				}
				if (_XML_TAG_SHORTHAND.equals(pXmlTag)) {
					getPosition().setShorthand(pValue);
				}
			}
		}
		return complete;
	}

	@Override
	public void init(RosterPlayer pPlayer) {

		if (pPlayer == null) {
			return;
		}

		setMovement(pPlayer.getMovement());
		setStrength(pPlayer.getStrength());
		setAgility(pPlayer.getAgility());
		setArmour(pPlayer.getArmour());

		fLastingInjuries.clear();
		for (SeriousInjury injury : pPlayer.getLastingInjuries()) {
			addLastingInjury(injury);
		}
		setRecoveringInjury(pPlayer.getRecoveringInjury());

		setUrlPortrait(pPlayer.getUrlPortrait());
		setUrlIconSet(pPlayer.getUrlIconSet());
		setNrOfIcons(pPlayer.getNrOfIcons());

		fSkills.clear();
		for (Skill skill : pPlayer.getSkills()) {
			addSkill(skill);
		}

	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_KIND.addTo(jsonObject, KIND);

		IJsonOption.PLAYER_ID.addTo(jsonObject, fId);
		IJsonOption.PLAYER_NR.addTo(jsonObject, fNr);
		IJsonOption.POSITION_ID.addTo(jsonObject, fPositionId);
		IJsonOption.PLAYER_NAME.addTo(jsonObject, fName);
		IJsonOption.PLAYER_GENDER.addTo(jsonObject, fPlayerGender);
		IJsonOption.PLAYER_TYPE.addTo(jsonObject, fPlayerType);

		IJsonOption.MOVEMENT.addTo(jsonObject, fMovement);
		IJsonOption.STRENGTH.addTo(jsonObject, fStrength);
		IJsonOption.AGILITY.addTo(jsonObject, fAgility);
		IJsonOption.ARMOUR.addTo(jsonObject, fArmour);

		JsonArray lastingInjuries = new JsonArray();
		for (SeriousInjury injury : fLastingInjuries) {
			lastingInjuries.add(UtilJson.toJsonValue(injury));
		}
		IJsonOption.LASTING_INJURIES.addTo(jsonObject, lastingInjuries);
		IJsonOption.RECOVERING_INJURY.addTo(jsonObject, fRecoveringInjury);

		IJsonOption.URL_PORTRAIT.addTo(jsonObject, fUrlPortrait);
		IJsonOption.URL_ICON_SET.addTo(jsonObject, fUrlIconSet);
		IJsonOption.NR_OF_ICONS.addTo(jsonObject, fNrOfIcons);
		IJsonOption.POSITION_ICON_INDEX.addTo(jsonObject, fIconSetIndex);

		JsonArray skillArray = new JsonArray();
		for (Skill skill : fSkills) {
			skillArray.add(UtilJson.toJsonValue(skill));
		}
		IJsonOption.SKILL_ARRAY.addTo(jsonObject, skillArray);

		return jsonObject;

	}

	public RosterPlayer initFrom(Game game, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		fId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		fNr = IJsonOption.PLAYER_NR.getFrom(game, jsonObject);
		fPositionId = IJsonOption.POSITION_ID.getFrom(game, jsonObject);
		fName = IJsonOption.PLAYER_NAME.getFrom(game, jsonObject);
		fPlayerGender = (PlayerGender) IJsonOption.PLAYER_GENDER.getFrom(game, jsonObject);
		fPlayerType = (PlayerType) IJsonOption.PLAYER_TYPE.getFrom(game, jsonObject);

		fMovement = IJsonOption.MOVEMENT.getFrom(game, jsonObject);
		fStrength = IJsonOption.STRENGTH.getFrom(game, jsonObject);
		fAgility = IJsonOption.AGILITY.getFrom(game, jsonObject);
		fArmour = IJsonOption.ARMOUR.getFrom(game, jsonObject);

		SeriousInjuryFactory seriousInjuryFactory = new SeriousInjuryFactory();

		fLastingInjuries.clear();
		JsonArray lastingInjuries = IJsonOption.LASTING_INJURIES.getFrom(game, jsonObject);
		for (int i = 0; i < lastingInjuries.size(); i++) {
			fLastingInjuries.add((SeriousInjury) UtilJson.toEnumWithName(seriousInjuryFactory, lastingInjuries.get(i)));
		}
		fRecoveringInjury = (SeriousInjury) IJsonOption.RECOVERING_INJURY.getFrom(game, jsonObject);

		fUrlPortrait = IJsonOption.URL_PORTRAIT.getFrom(game, jsonObject);
		fUrlIconSet = IJsonOption.URL_ICON_SET.getFrom(game, jsonObject);
		fNrOfIcons = IJsonOption.NR_OF_ICONS.getFrom(game, jsonObject);
		fIconSetIndex = IJsonOption.POSITION_ICON_INDEX.getFrom(game, jsonObject);

		SkillFactory skillFactory = game.getRules().getSkillFactory();

		fSkills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(game, jsonObject);
		for (int i = 0; i < skillArray.size(); i++) {
			fSkills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
		}

		return this;

	}

}
