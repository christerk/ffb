package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;
import com.balancedbytes.games.ffb.model.skill.Skill;
import com.balancedbytes.games.ffb.model.skill.SkillWithValue;
import com.balancedbytes.games.ffb.modifiers.TemporaryStatModifier;
import com.balancedbytes.games.ffb.xml.IXmlSerializable;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Map;
import java.util.Set;

public class ZappedPlayer extends Player<ZappedPosition> {

	static final String XML_TAG = "zappedPlayer";
	public static final String KIND = "zappedPlayer";

	private RosterPlayer originalPlayer;
	private ZappedPosition position;

	public RosterPlayer getOriginalPlayer() {
		return originalPlayer;
	}

	@Override
	public String getName() {
		return originalPlayer.getName();
	}

	@Override
	public PlayerType getPlayerType() {
		return originalPlayer.getPlayerType();
	}

	@Override
	public void setType(PlayerType pType) {
		originalPlayer.setType(pType);
	}

	@Override
	public int getNr() {
		return originalPlayer.getNr();
	}

	@Override
	public int getAgility() {
		return getPosition().getAgility();
	}

	@Override
	public int getPassing() {
		return getPosition().getPassing();
	}

	@Override
	public void setAgility(int pAgility) {
		// NOOP
	}

	@Override
	public void setPassing(int pPassing) {
		// NOOP
	}

	@Override
	public int getArmour() {
		return getPosition().getArmour();
	}

	@Override
	public void setArmour(int pArmour) {
		// NOOP
	}

	@Override
	public int getMovement() {
		return getPosition().getMovement();
	}

	@Override
	public void setMovement(int pMovement) {
		// NOOP
	}

	@Override
	public int getStrength() {
		return getPosition().getStrength();
	}

	@Override
	public void setStrength(int pStrength) {
		// NOOP
	}

	@Override
	public void addLastingInjury(SeriousInjury pLastingInjury) {
		// NOOP
	}

	@Override
	public SeriousInjury[] getLastingInjuries() {
		return originalPlayer.getLastingInjuries();
	}

	@Override
	public void addSkill(Skill pSkill) {
		// NOOP
	}

	@Override
	public boolean removeSkill(Skill pSkill) {
		return false;
	}

	@Override
	public Skill[] getSkills() {
		return getPosition().getSkills();
	}

	@Override
	public String getSkillValueExcludingTemporaryOnes(Skill pSkill) {
		return null;
	}

	@Override
	public String getUrlPortrait() {
		return originalPlayer.getUrlPortrait();
	}

	@Override
	public void setUrlPortrait(String pUrlPortrait) {
		// NOOP
	}

	@Override
	public String getUrlIconSet() {
		return originalPlayer.getUrlIconSet();
	}

	@Override
	public void setUrlIconSet(String pUrlIconSet) {
		// NOOP
	}

	@Override
	public int getNrOfIcons() {
		return 1;
	}

	@Override
	public void setNrOfIcons(int pNrOfIcons) {
		// NOOP
	}

	@Override
	public ZappedPosition getPosition() {
		return position;
	}

	@Override
	public void updatePosition(RosterPosition pPosition, boolean updateStats, IFactorySource game) {
		position = new ZappedPosition(pPosition, game);
		originalPlayer.updatePosition(pPosition, updateStats, game);
	}

	@Override
	public void updatePosition(RosterPosition pPosition, IFactorySource game) {
		updatePosition(pPosition, true, game);
	}

	@Override
	public Team getTeam() {
		return originalPlayer.getTeam();
	}

	@Override
	public void setTeam(Team pTeam) {
		originalPlayer.setTeam(pTeam);
	}

	@Override
	public String getId() {
		return originalPlayer.getId();
	}

	@Override
	public void setId(String pId) {
		// NOOP
	}

	@Override
	public PlayerGender getPlayerGender() {
		return originalPlayer.getPlayerGender();
	}

	@Override
	public SeriousInjury getRecoveringInjury() {
		return null;
	}

	@Override
	public void setRecoveringInjury(SeriousInjury pCurrentInjury) {
		// NOOP
	}

	@Override
	public int getCurrentSpps() {
		return originalPlayer.getCurrentSpps();
	}

	@Override
	public void setCurrentSpps(int pCurrentSpps) {
		// NOOP
	}

	@Override
	public void setName(String name) {
		// NOOP
	}

	@Override
	public void setGender(PlayerGender gender) {
		// NOOP
	}

	@Override
	public void setNr(int nr) {
		// NOOP
	}

	@Override
	public int getIconSetIndex() {
		return 0;
	}

	@Override
	public String getPositionId() {
		return originalPlayer.getPositionId();
	}

	@Override
	public void setPositionId(String pPositionId) {
		// NOOP
	}

	@Override
	public String getRace() {
		return originalPlayer.getRace();
	}

	@Override
	public void init(RosterPlayer pPlayer, IFactorySource game) {
		this.originalPlayer = pPlayer;
		this.position = new ZappedPosition(pPlayer.getPosition(), game);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_KIND.addTo(jsonObject, KIND);
		IJsonOption.PLAYER.addTo(jsonObject, originalPlayer.toJsonValue());
		return jsonObject;
	}

	@Override
	public void applyPlayerModifiers() {
		originalPlayer.applyPlayerModifiers();
	}

	@Override
	protected Map<String, Set<TemporaryStatModifier>> getTemporaryModifiers() {
		return originalPlayer.getTemporaryModifiers();
	}

	@Override
	public void addTemporaryModifiers(String source, Set<TemporaryStatModifier> modifiers) {
		originalPlayer.addTemporaryModifiers(source, modifiers);
	}

	@Override
	public void removeTemporaryModifiers(String source) {
		originalPlayer.removeTemporaryModifiers(source);
	}

	@Override
	protected Map<String, Set<SkillWithValue>> getTemporarySkills() {
		return originalPlayer.getTemporarySkills();
	}

	@Override
	public void addTemporarySkills(String source, Set<SkillWithValue> skills) {
		originalPlayer.addTemporarySkills(source, skills);
	}

	@Override
	public void removeTemporarySkills(String source) {
		originalPlayer.removeTemporarySkills(source);
	}

	@Override
	protected Map<String, Set<ISkillProperty>> getTemporaryProperties() {
		return originalPlayer.getTemporaryProperties();
	}

	@Override
	public void addTemporaryProperties(String source, Set<ISkillProperty> properties) {
		originalPlayer.addTemporaryProperties(source, properties);
	}

	@Override
	public void removeTemporaryProperties(String source) {
		originalPlayer.removeTemporaryProperties(source);
	}

	@Override
	public ZappedPlayer initFrom(IFactorySource game, JsonValue pJsonValue) {
		originalPlayer = new RosterPlayer().initFrom(game, IJsonOption.PLAYER.getFrom(game, (JsonObject) pJsonValue));
		return this;
	}

	@Override
	public IXmlSerializable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlSerializable xmlElement = this;
		if (RosterPlayer.XML_TAG.equals(pXmlTag)) {
			RosterPlayer player = new RosterPlayer();
			player.startXmlElement(game, pXmlTag, pXmlAttributes);
			init(player, game.getRules());
		}
		return xmlElement;
	}

	@Override
	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	@Override
	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();

		UtilXml.startElement(pHandler, XML_TAG, attributes);

		originalPlayer.addToXml(pHandler);

		UtilXml.endElement(pHandler, XML_TAG);

	}

	@Override
	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}
}
