package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.common.Dodge;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZappedPosition implements Position {

	public static final String XML_TAG = "zappedPosition";


	private PlayerStats playerStats;
	private final List<Skill> skills = new ArrayList<>();

	private RosterPosition originalPosition;

	public ZappedPosition(RosterPosition originalPosition, IFactorySource game) {
		this.originalPosition = originalPosition;
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		skills.add(factory.forClass(Dodge.class));
		skills.add(factory.forProperty(NamedProperties.preventHoldBall)); // No Hands or No Ball, per ruleset
		skills.add(factory.forName("Titchy"));
		skills.add(factory.forName("Stunty"));
		skills.add(factory.forName("Very Long Legs"));
		skills.add(factory.forName("Leap"));
		playerStats = ((GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name())).zappedPlayerStats();
	}

	@Override
	public PlayerType getType() {
		return PlayerType.REGULAR;
	}

	@Override
	public PlayerGender getGender() {
		return originalPosition.getGender();
	}

	@Override
	public int getMovement() {
		return playerStats.move();
	}

	@Override
	public int getStrength() {
		return playerStats.strength();
	}
	
	@Override
	public int getAgility() {
		return playerStats.agility();
	}
	
	@Override
	public int getPassing() {
		return playerStats.passing();
	}

	@Override
	public int getArmour() {
		return playerStats.armour();
	}

	@Override
	public int getCost() {
		return originalPosition.getCost();
	}

	@Override
	public String getName() {
		return originalPosition.getName();
	}

	@Override
	public String getShorthand() {
		return "zf";
	}

	@Override
	public boolean hasSkill(Skill pSkill) {
		return skills.contains(pSkill);
	}

	@Override
	public Skill[] getSkills() {
		return skills.toArray(new Skill[0]);
	}

	@Override
	public String getSkillValue(Skill pSkill) {
		return null;
	}

	@Override
	public String getDisplayValue(Skill pSkill) {
		return null;
	}

	@Override
	public int getSkillIntValue(Skill skill) {
		return skill.getDefaultSkillValue();
	}

	@Override
	public String getUrlPortrait() {
		return originalPosition.getUrlPortrait();
	}

	@Override
	public void setUrlPortrait(String pUrlPortrait) {
		// NOOP
	}

	@Override
	public String getUrlIconSet() {
		return originalPosition.getUrlIconSet();
	}

	@Override
	public int getQuantity() {
		return 0;
	}

	@Override
	public Roster getRoster() {
		return originalPosition.getRoster();
	}

	@Override
	public String getId() {
		return originalPosition.getId();
	}

	@Override
	public int getNrOfIcons() {
		return originalPosition.getNrOfIcons();
	}

	@Override
	public int findNextIconSetIndex() {
		return originalPosition.findNextIconSetIndex();
	}

	@Override
	public String getDisplayName() {
		return originalPosition.getDisplayName();
	}

	@Override
	public String getRace() {
		return "Transmogrified Frog";
	}

	@Override
	public boolean isUndead() {
		return false;
	}

	@Override
	public boolean isThrall() {
		return false;
	}

	@Override
	public boolean isDwarf() {
		return false;
	}

	@Override
	public String getTeamWithPositionId() {
		return originalPosition.getTeamWithPositionId();
	}

	@Override
	public boolean isDoubleCategory(SkillCategory category) {
		return false;
	}

	@Override
	public SkillCategory[] getSkillCategories(boolean b) {
		return new SkillCategory[0];
	}

	@Override
	public List<Keyword> getKeywords() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getRawKeywords() {
		return Collections.emptyList();
	}

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.startElement(pHandler, XML_TAG, attributes);
		originalPosition.addToXml(pHandler);
		UtilXml.endElement(pHandler, XML_TAG);
	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {

		if (RosterPosition.XML_TAG.equals(pXmlTag)) {
			originalPosition = new RosterPosition();
			originalPosition.startXmlElement(game, pXmlTag, pXmlAttributes);
		}
		return this;
	}

	public boolean endXmlElement(Game game, String pTag, String pValue) {
		return XML_TAG.equals(pTag);
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.ROSTER_POSITION.addTo(jsonObject, originalPosition.toJsonValue());
		return jsonObject;
	}

	public ZappedPosition initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		originalPosition = new RosterPosition().initFrom(source, IJsonOption.ROSTER_POSITION.getFrom(source, jsonObject));
		playerStats = ((GameMechanic) source.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name())).zappedPlayerStats();
		return this;
	}
}
