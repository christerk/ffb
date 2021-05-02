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
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.skill.Dodge;
import com.fumbbl.ffb.skill.NoHands;
import com.fumbbl.ffb.skill.Titchy;
import com.fumbbl.ffb.skill.bb2016.Leap;
import com.fumbbl.ffb.skill.bb2016.Stunty;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.UtilXml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.ArrayList;
import java.util.List;

public class ZappedPosition implements Position {

	public static final String XML_TAG = "zappedPosition";

	private int move = 5;
	private int strength = 1;
	private int agility = 4;
	private int passing = 0;
	private int armour = 4;
	private List<Skill> skills = new ArrayList<>();

	private String race = "Transmogrified Frog";
	private String shortHand = "zf";

	private RosterPosition originalPosition;

	public ZappedPosition(RosterPosition originalPosition, IFactorySource game) {
		this.originalPosition = originalPosition;
		SkillFactory factory = game.getFactory(FactoryType.Factory.SKILL);
		skills.add(factory.forClass(Dodge.class));
		skills.add(factory.forClass(NoHands.class));
		skills.add(factory.forClass(Titchy.class));
		skills.add(factory.forClass(Stunty.class));
		skills.add(factory.forName("Very Long Legs"));
		skills.add(factory.forClass(Leap.class));
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
		return move;
	}

	@Override
	public int getStrength() {
		return strength;
	}
	
	@Override
	public int getAgility() {
		return agility;
	}
	
	@Override
	public int getPassing() {
		return passing;
	}

	@Override
	public int getArmour() {
		return armour;
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
		return shortHand;
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
		return race;
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

	public ZappedPosition initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		originalPosition = new RosterPosition().initFrom(game, IJsonOption.ROSTER_POSITION.getFrom(game, jsonObject));
		return this;
	}
}