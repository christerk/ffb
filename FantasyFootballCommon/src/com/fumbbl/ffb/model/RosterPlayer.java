package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.PlayerGenderFactory;
import com.fumbbl.ffb.factory.PlayerTypeFactory;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kalimar
 */
public class RosterPlayer extends Player<RosterPosition> {

	static final String XML_TAG = "player";
	private static final String KIND = "rosterPlayer";
	private static final String _XML_ATTRIBUTE_DISPLAY_VALUE = "displayValueAs";
	private static final String _XML_ATTRIBUTE_VALUE = "value";

	private String fId;
	private int fNr;
	private Team fTeam;
	private String fName;
	private PlayerType fPlayerType;
	private PlayerStatus playerStatus;
	private PlayerGender fPlayerGender;
	private int fMovement;
	private int fStrength;
	private int fAgility;
	private int fPassing;
	private int fArmour;

	private String fUrlPortrait;
	private String fUrlIconSet;
	private int fNrOfIcons;

	private String fPositionId;
	private transient int fIconSetIndex;

	private final List<Skill> fSkills;
	private final List<SeriousInjury> fLastingInjuries;
	private SeriousInjury fRecoveringInjury;

	private transient RosterPosition fPosition;
	private transient int fCurrentSpps;
	private Map<String, Set<TemporaryStatModifier>> temporaryModifiers = new HashMap<>();
	private Map<String, Set<SkillWithValue>> temporarySkills = new HashMap<>();
	private Map<String, Set<ISkillProperty>> temporaryProperties = new HashMap<>();
	private Map<Skill, String> skillValues;
	private Map<Skill, String> displayValues;

	// attributes used for parsing
	private transient boolean fInsideSkillList;
	private transient boolean fInsideInjuryList;
	private transient boolean fInjuryCurrent;
	private transient boolean fInsidePlayerStatistics;
	private transient String fCurrentSkillValue;
	private transient String currentDisplayValue;

	public RosterPlayer() {
		fLastingInjuries = new ArrayList<>();
		fSkills = new ArrayList<>();
		setGender(PlayerGender.MALE);
		fIconSetIndex = 0;
		fPosition = new RosterPosition(null);
		skillValues = new LinkedHashMap<>();
		displayValues = new LinkedHashMap<>();
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
	public int getPassing() {
		return fPassing;
	}

	@Override
	public void setPassing(int pPassing) {
		fPassing = pPassing;
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
		return fLastingInjuries.toArray(new SeriousInjury[0]);
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
	public Skill[] getSkills() {
		return fSkills.toArray(new Skill[0]);
	}

	@Override
	public String getSkillValueExcludingTemporaryOnes(Skill skill) {
		return Optional.ofNullable(skillValues.get(skill)).orElse(getPosition().getSkillValue(skill));
	}

	@Override
	public String getDisplayValueExcludingTemporaryOnes(Skill skill) {
		return Optional.ofNullable(displayValues.get(skill)).orElse(getPosition().getDisplayValue(skill));
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
	public void updatePosition(RosterPosition pPosition, IFactorySource game, long gameId) {
		updatePosition(pPosition, true, game, gameId);
	}

	@Override
	public void updatePosition(RosterPosition pPosition, boolean updateStats, IFactorySource game, long gameId) {
		fPosition = pPosition;
		if (fPosition != null) {
			setPositionId(fPosition.getId());
			if (getPlayerType() == null) {
				setType(fPosition.getType());
			}
			fIconSetIndex = pPosition.findNextIconSetIndex();
			if (!updateStats) {
				return;
			}
			setMovement(fPosition.getMovement());
			setStrength(fPosition.getStrength());
			setAgility(fPosition.getAgility());
			setPassing(fPosition.getPassing());
			setArmour(fPosition.getArmour());
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
			applyPlayerModifiersFromBehaviours(game, gameId);
			int oldMovement = getMovement();
			int oldArmour = getArmour();
			int oldAgility = getAgility();
			int oldStrength = getStrength();
			int oldPassing = getPassing();
			StatsMechanic mechanic = (StatsMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.STAT.name());
			for (SeriousInjury injury : getLastingInjuries()) {
				if (injury.getInjuryAttribute() != null) {
					switch (injury.getInjuryAttribute()) {
						case MA:
							if ((fMovement > 1) && ((oldMovement - fMovement) < 2)) {
								fMovement = mechanic.applyLastingInjury(fMovement, PlayerStatKey.MA);
							}
							break;
						case AV:
							if ((fArmour > 1) && ((oldArmour - fArmour) < 2)) {
								fArmour = mechanic.applyLastingInjury(fArmour, PlayerStatKey.AV);
							}
							break;
						case AG:
							if ((fAgility > 1) && ((oldAgility - fAgility) < 2)) {
								fAgility = mechanic.applyLastingInjury(fAgility, PlayerStatKey.AG);
							}
							break;
						case ST:
							if ((fStrength > 1) && ((oldStrength - fStrength) < 2)) {
								fStrength = mechanic.applyLastingInjury(fStrength, PlayerStatKey.ST);
							}
							break;
						case PA:
							if ((fPassing > 1) && ((oldPassing - fPassing) < 2)) {
								fPassing = mechanic.applyLastingInjury(fPassing, PlayerStatKey.PA);
							}
							break;
						default:
							break;
					}
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
		if (playerStatus != null) {
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_STATUS, playerStatus.getName());
		}
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
				attributes = new AttributesImpl();
				if (StringTool.isProvided(getSkillValueExcludingTemporaryOnes(skill))) {
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, getSkillValueExcludingTemporaryOnes(skill));
				}
				if (StringTool.isProvided(getDisplayValueExcludingTemporaryOnes(skill))) {
					UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_DISPLAY_VALUE, getDisplayValueExcludingTemporaryOnes(skill));
				}
				UtilXml.startElement(pHandler, _XML_TAG_SKILL, attributes);
				UtilXml.addCharacters(pHandler, skill.getName());
				UtilXml.endElement(pHandler, _XML_TAG_SKILL);
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
		if (fInsideSkillList) {
			if (_XML_TAG_SKILL.equals(pXmlTag)) {
				String skillValue = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
				if (StringTool.isProvided(skillValue)) {
					fCurrentSkillValue = skillValue;
				} else {
					fCurrentSkillValue = null;
				}
				String displayValue = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_DISPLAY_VALUE);
				if (StringTool.isProvided(displayValue)) {
					currentDisplayValue = displayValue;
				} else {
					currentDisplayValue = null;
				}
			}
		} else if (fInsideInjuryList) {
			if (_XML_TAG_INJURY.equals(pXmlTag)) {
				fInjuryCurrent = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_RECOVERING);
			}
		} else {
			if (XML_TAG.equals(pXmlTag)) {
				fId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID);
				setNr(UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_NR));
				playerStatus = PlayerStatus.forName(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_STATUS));
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
				fInsidePlayerStatistics = true;
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
					Skill skill = game.getRules().<SkillFactory>getFactory(Factory.SKILL).forName(pValue);
					if (skill != null) {
						fSkills.add(skill);
						skillValues.put(skill, fCurrentSkillValue);
						displayValues.put(skill, currentDisplayValue);
					}
				}
			} else if (fInsideInjuryList) {
				if (_XML_TAG_INJURY_LIST.equals(pXmlTag)) {
					fInsideInjuryList = false;
				}
				if (_XML_TAG_INJURY.equals(pXmlTag)) {
					SeriousInjury injury = ((SeriousInjuryFactory) game.getFactory(Factory.SERIOUS_INJURY)).forName(pValue);
					if (injury != null) {
						fLastingInjuries.add(injury);
						if (fInjuryCurrent) {
							fRecoveringInjury = injury;
						}
					}
				}
			} else if (fInsidePlayerStatistics) {
				if (_XML_TAG_PLAYER_STATISTICS.equals(pXmlTag)) {
					fInsidePlayerStatistics = false;
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
				if (_XML_TAG_PASSING.equals(pXmlTag)) {
					setPassing(pValue != null && pValue.length() > 0 ? Integer.parseInt(pValue) : 0);
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
	public void init(RosterPlayer pPlayer, IFactorySource source) {

		if (pPlayer == null) {
			return;
		}

		setMovement(pPlayer.getMovement());
		setStrength(pPlayer.getStrength());
		setAgility(pPlayer.getAgility());
		setPassing(pPlayer.getPassing());
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

		playerStatus = pPlayer.playerStatus;
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
		IJsonOption.PASSING.addTo(jsonObject, fPassing);
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

		IJsonOption.TEMPORARY_SKILL_MAP.addTo(jsonObject, temporarySkills);
		IJsonOption.TEMPORARY_MODIFIERS_MAP.addTo(jsonObject, temporaryModifiers);
		IJsonOption.TEMPORARY_PROPERTIES_MAP.addTo(jsonObject, temporaryProperties);
		IJsonOption.SKILL_VALUES_MAP.addTo(jsonObject, skillValues);
		IJsonOption.SKILL_DISPLAY_VALUES_MAP.addTo(jsonObject, displayValues);

		if (playerStatus != null) {
			IJsonOption.PLAYER_STATUS.addTo(jsonObject, playerStatus.getName());
		}
		return jsonObject;

	}

	@Override
	public void applyPlayerModifiersFromBehaviours(IFactorySource game, long gameId) {
		fSkills.stream().map(Skill::getSkillBehaviour).filter(Objects::nonNull)
			.flatMap(behaviour -> behaviour.getPlayerModifiers().stream()).forEach(playerModifier -> {
				game.logDebug(gameId, "Player " + fId + ": Before applying " + playerModifier.getClass().getCanonicalName()
					+ " - MA: " + getMovementWithModifiers() + " ST: " + getStrengthWithModifiers() + " AG: " + getAgilityWithModifiers()
					+ " PA: " + getPassingWithModifiers() + " AV: " + getArmourWithModifiers());
				playerModifier.apply(this);

				game.logDebug(gameId, "Player " + fId + ": After  applying " + playerModifier.getClass().getCanonicalName()
					+ " - MA: " + getMovementWithModifiers() + " ST: " + getStrengthWithModifiers() + " AG: " + getAgilityWithModifiers()
					+ " PA: " + getPassingWithModifiers() + " AV: " + getArmourWithModifiers());
			});
	}

	public RosterPlayer initFrom(IFactorySource source, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		fId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		fNr = IJsonOption.PLAYER_NR.getFrom(source, jsonObject);
		fPositionId = IJsonOption.POSITION_ID.getFrom(source, jsonObject);
		fName = IJsonOption.PLAYER_NAME.getFrom(source, jsonObject);
		fPlayerGender = (PlayerGender) IJsonOption.PLAYER_GENDER.getFrom(source, jsonObject);
		fPlayerType = (PlayerType) IJsonOption.PLAYER_TYPE.getFrom(source, jsonObject);

		fMovement = IJsonOption.MOVEMENT.getFrom(source, jsonObject);
		fStrength = IJsonOption.STRENGTH.getFrom(source, jsonObject);
		fAgility = IJsonOption.AGILITY.getFrom(source, jsonObject);
		fPassing = IJsonOption.PASSING.getFrom(source, jsonObject);
		fArmour = IJsonOption.ARMOUR.getFrom(source, jsonObject);

		SeriousInjuryFactory seriousInjuryFactory = source.getFactory(Factory.SERIOUS_INJURY);

		fLastingInjuries.clear();
		JsonArray lastingInjuries = IJsonOption.LASTING_INJURIES.getFrom(source, jsonObject);
		for (int i = 0; i < lastingInjuries.size(); i++) {
			fLastingInjuries.add((SeriousInjury) UtilJson.toEnumWithName(seriousInjuryFactory, lastingInjuries.get(i)));
		}
		fRecoveringInjury = (SeriousInjury) IJsonOption.RECOVERING_INJURY.getFrom(source, jsonObject);

		fUrlPortrait = IJsonOption.URL_PORTRAIT.getFrom(source, jsonObject);
		fUrlIconSet = IJsonOption.URL_ICON_SET.getFrom(source, jsonObject);
		fNrOfIcons = IJsonOption.NR_OF_ICONS.getFrom(source, jsonObject);
		fIconSetIndex = IJsonOption.POSITION_ICON_INDEX.getFrom(source, jsonObject);

		SkillFactory skillFactory = source.getFactory(Factory.SKILL);

		fSkills.clear();
		JsonArray skillArray = IJsonOption.SKILL_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < skillArray.size(); i++) {
			fSkills.add((Skill) UtilJson.toEnumWithName(skillFactory, skillArray.get(i)));
		}

		temporaryModifiers = IJsonOption.TEMPORARY_MODIFIERS_MAP.getFrom(source, jsonObject);
		temporarySkills = IJsonOption.TEMPORARY_SKILL_MAP.getFrom(source, jsonObject);
		temporaryProperties = IJsonOption.TEMPORARY_PROPERTIES_MAP.getFrom(source, jsonObject);

		skillValues = IJsonOption.SKILL_VALUES_MAP.getFrom(source, jsonObject);
		displayValues = IJsonOption.SKILL_DISPLAY_VALUES_MAP.getFrom(source, jsonObject);

		playerStatus = PlayerStatus.forName(IJsonOption.PLAYER_STATUS.getFrom(source, jsonObject));
		return this;

	}

	@Override
	public Map<String, Set<TemporaryStatModifier>> getTemporaryModifiers() {
		return temporaryModifiers;
	}

	@Override
	public void addTemporaryModifiers(String source, Set<TemporaryStatModifier> modifiers) {
		temporaryModifiers.put(source, modifiers);
	}

	@Override
	public void removeTemporaryModifiers(String source) {
		temporaryModifiers.remove(source);
	}

	@Override
	protected Map<String, Set<SkillWithValue>> getTemporarySkills() {
		return temporarySkills;
	}

	@Override
	public Set<String> getEnhancementSources() {
		return new HashSet<String>() {{
			addAll(temporaryModifiers.keySet());
			addAll(temporarySkills.keySet());
			addAll(temporaryProperties.keySet());
		}};
	}

	@Override
	public void addTemporarySkills(String source, Set<SkillWithValue> skills) {
		temporarySkills.put(source, skills);
	}

	@Override
	public void removeTemporarySkills(String source) {
		temporarySkills.remove(source);
	}

	@Override
	protected Map<String, Set<ISkillProperty>> getTemporaryProperties() {
		return temporaryProperties;
	}

	@Override
	public void addTemporaryProperties(String source, Set<ISkillProperty> properties) {
		temporaryProperties.put(source, properties);
	}

	@Override
	public void removeTemporaryProperties(String source) {
		temporaryProperties.remove(source);
	}

	@Override
	public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	@Override
	public boolean isJourneyman() {
		return playerStatus == PlayerStatus.JOURNEYMAN;
	}
}
