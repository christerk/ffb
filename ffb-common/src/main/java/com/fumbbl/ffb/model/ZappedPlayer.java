package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
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
	public Skill[] getSkills() {
		return getPosition().getSkills();
	}

	@Override
	public String getSkillValueExcludingTemporaryOnes(Skill pSkill) {
		return null;
	}

	@Override
	public String getDisplayValueExcludingTemporaryOnes(Skill skill) {
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
	public void updatePosition(RosterPosition pPosition, boolean updateStats, IFactorySource game, long gameId) {
		position = new ZappedPosition(pPosition, game);
		originalPlayer.updatePosition(pPosition, updateStats, game, gameId);
	}

	@Override
	public void updatePosition(RosterPosition pPosition, IFactorySource game, long gameId) {
		updatePosition(pPosition, true, game, gameId);
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
	public void applyPlayerModifiersFromBehaviours(IFactorySource game, long gameId) {
		originalPlayer.applyPlayerModifiersFromBehaviours(game, gameId);
	}

	@Override
	public Map<String, Set<TemporaryStatModifier>> getTemporaryModifiers() {
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
	public Set<String> getEnhancementSources() {
		return originalPlayer.getEnhancementSources();
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
	public PlayerStatus getPlayerStatus() {
		return originalPlayer.getPlayerStatus();
	}

	@Override
	public boolean isJourneyman() {
		return originalPlayer.isJourneyman();
	}

	@Override
	public ZappedPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		originalPlayer = new RosterPlayer().initFrom(source, IJsonOption.PLAYER.getFrom(source, (JsonObject) jsonValue));
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ZappedPlayer that = (ZappedPlayer) o;

		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean isUsed(Skill skill) {
		return originalPlayer.isUsed(skill);
	}

	@Override
	public void markUsed(Skill skill, Game game) {
		originalPlayer.markUsed(skill, game);
	}

	@Override
	public void markUnused(Skill skill, Game game) {
		originalPlayer.markUnused(skill, game);
	}

	@Override
	public void resetUsedSkills(SkillUsageType type, Game game) {
		originalPlayer.resetUsedSkills(type, game);
	}
}
