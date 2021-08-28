package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.modifiers.PlayerStatLimit;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;
import com.fumbbl.ffb.xml.IXmlSerializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fumbbl.ffb.model.skill.SkillValueEvaluator.ANIMOSITY_TO_ALL;

/**
 * @author Kalimar
 */
public abstract class Player<T extends Position> implements IXmlSerializable, IJsonSerializable {

	static final String _XML_ATTRIBUTE_ID = "id";

	static final String _XML_ATTRIBUTE_NR = "nr";
	static final String _XML_ATTRIBUTE_SIZE = "size";

	static final String _XML_TAG_NAME = "name";
	static final String _XML_TAG_TYPE = "type";
	static final String _XML_TAG_GENDER = "gender";
	static final String _XML_TAG_POSITION_ID = "positionId";

	static final String _XML_TAG_SKILL_LIST = "skillList";
	static final String _XML_TAG_SKILL = "skill";

	static final String _XML_TAG_ICON_SET = "iconSet";
	static final String _XML_TAG_PORTRAIT = "portrait";

	static final String _XML_TAG_INJURY_LIST = "injuryList";
	static final String _XML_TAG_INJURY = "injury";
	static final String _XML_ATTRIBUTE_RECOVERING = "recovering";

	static final String _XML_TAG_PLAYER_STATISTICS = "playerStatistics";
	static final String _XML_ATTRIBUTE_CURRENT_SPPS = "currentSpps";

	static final String _XML_TAG_MOVEMENT = "movement";
	static final String _XML_TAG_STRENGTH = "strength";
	static final String _XML_TAG_AGILITY = "agility";
	static final String _XML_TAG_PASSING = "passing";
	static final String _XML_TAG_ARMOUR = "armour";
	static final String _XML_TAG_SHORTHAND = "shorthand";
	static final String _XML_TAG_RACE = "race";

	public abstract String getName();

	public abstract PlayerType getPlayerType();

	abstract void setType(PlayerType pType);

	public abstract int getNr();

	public abstract int getAgility();

	public abstract void setAgility(int pAgility);

	public abstract int getPassing();

	public abstract void setPassing(int pPassing);

	public abstract int getArmour();

	public abstract void setArmour(int pArmour);

	public abstract int getMovement();

	public abstract void setMovement(int pMovement);

	public abstract int getStrength();

	public abstract void setStrength(int pStrength);

	abstract void addLastingInjury(SeriousInjury pLastingInjury);

	public abstract SeriousInjury[] getLastingInjuries();

	abstract void addSkill(Skill pSkill);

	abstract boolean removeSkill(Skill pSkill);

	public boolean hasSkillExcludingTemporaryOnes(Skill pSkill) {
		return Arrays.asList(getSkills()).contains(pSkill);
	}

	public abstract Skill[] getSkills();

	public abstract String getSkillValueExcludingTemporaryOnes(Skill pSkill);

	public int getSkillIntValue(Skill skill) {
		Set<String> values = temporarySkillValues(skill);
		values.add(getSkillValueExcludingTemporaryOnes(skill));
		Integer intValue = skill.evaluator().intValue(values);
		return intValue != null ? intValue : skill.getDefaultSkillValue();
	}

	public int getSkillIntValue(ISkillProperty property) {
		return getSkillIntValue(getSkillWithProperty(property));
	}

	public abstract String getDisplayValueExcludingTemporaryOnes(Skill skill);

	public abstract String getUrlPortrait();

	abstract void setUrlPortrait(String pUrlPortrait);

	public abstract String getUrlIconSet();

	abstract void setUrlIconSet(String pUrlIconSet);

	abstract int getNrOfIcons();

	abstract void setNrOfIcons(int pNrOfIcons);

	public abstract T getPosition();

	public abstract void updatePosition(RosterPosition pPosition, IFactorySource game);

	public abstract void updatePosition(RosterPosition pPosition, boolean updateStats, IFactorySource game);

	public abstract Team getTeam();

	public abstract void setTeam(Team pTeam);

	public abstract String getId();

	abstract void setId(String pId);

	public abstract PlayerGender getPlayerGender();

	public abstract SeriousInjury getRecoveringInjury();

	abstract void setRecoveringInjury(SeriousInjury pCurrentInjury);

	public abstract int getCurrentSpps();

	abstract void setCurrentSpps(int pCurrentSpps);

	abstract void setName(String name);

	abstract void setGender(PlayerGender gender);

	abstract void setNr(int nr);

	public abstract int getIconSetIndex();

	public abstract String getPositionId();

	abstract void setPositionId(String pPositionId);

	public abstract String getRace();

	public abstract void init(RosterPlayer pPlayer, IFactorySource source);

	public abstract JsonObject toJsonValue();

	public abstract void applyPlayerModifiers();

	public static Player<?> getFrom(IFactorySource source, JsonValue jsonValue) {
		Player<?> player = createPlayer(source, jsonValue);
		player.initFrom(source, jsonValue);
		return player;
	}

	private static Player<?> createPlayer(IFactorySource source, JsonValue jsonValue) {
		if (jsonValue instanceof JsonObject
			&& ZappedPlayer.KIND.equals(IJsonOption.PLAYER_KIND.getFrom(source, (JsonObject) jsonValue))) {
			return new ZappedPlayer();
		}
		return new RosterPlayer();
	}

	public Skill getSkillWithProperty(ISkillProperty property) {
		for (Skill playerSkill : getSkillsIncludingTemporaryOnes()) {
			if (playerSkill.hasSkillProperty(property)) {
				return playerSkill;
			}
		}
		return null;
	}

	public int getAgilityWithModifiers() {
		return getStatWithModifiers(TemporaryStatModifier.PlayerStatKey.AG, getAgility());
	}

	public int getMovementWithModifiers() {
		return getStatWithModifiers(TemporaryStatModifier.PlayerStatKey.MA, getMovement());
	}

	public int getStrengthWithModifiers() {
		return getStatWithModifiers(TemporaryStatModifier.PlayerStatKey.ST, getStrength());
	}

	public int getPassingWithModifiers() {
		return getStatWithModifiers(TemporaryStatModifier.PlayerStatKey.PA, getPassing());
	}

	public int getArmourWithModifiers() {
		return getStatWithModifiers(TemporaryStatModifier.PlayerStatKey.AV, getArmour());
	}

	private int getStatWithModifiers(TemporaryStatModifier.PlayerStatKey stat, int baseValue) {
		int sum = getTemporaryModifiers().values().stream().flatMap(Collection::stream).filter(modifier -> modifier.appliesTo(stat))
			.map(modifier -> modifier.apply(0)).reduce(baseValue, Integer::sum);

		Optional<PlayerStatLimit> limit = getTemporaryModifiers().values().stream().flatMap(Collection::stream).filter(modifier -> modifier.appliesTo(stat)).map(TemporaryStatModifier::getLimit).findFirst();

		if (limit.isPresent() && limit.get().getMax() != 0) {
			sum = Math.min(limit.get().getMax(), sum);
		}

		if (limit.isPresent() && limit.get().getMin() != 0) {
			sum = Math.max(baseValue == 0 ? 0 : limit.get().getMin(), sum);
		}
		return sum;
	}

	protected abstract Map<String, Set<TemporaryStatModifier>> getTemporaryModifiers();

	public abstract void addTemporaryModifiers(String source, Set<TemporaryStatModifier> modifiers);

	public abstract void removeTemporaryModifiers(String source);

	public List<Skill> getSkillsIncludingTemporaryOnesWithDuplicates() {
		return Stream.concat(
			getTemporarySkills().values().stream().flatMap(Collection::stream).map(SkillWithValue::getSkill),
			Arrays.stream(getSkills())
		).collect(Collectors.toList());
	}

	public Set<Skill> getSkillsIncludingTemporaryOnes() {
		return new HashSet<>(getSkillsIncludingTemporaryOnesWithDuplicates());
	}

	protected abstract Map<String, Set<SkillWithValue>> getTemporarySkills();

	public abstract void addTemporarySkills(String source, Set<SkillWithValue> skills);

	public abstract void removeTemporarySkills(String source);

	public boolean hasSkillProperty(ISkillProperty property) {
		return Stream.concat(
			getSkillsIncludingTemporaryOnes().stream().flatMap(skill -> skill.getSkillProperties().stream()),
			getTemporaryProperties().values().stream().flatMap(Collection::stream)
		).anyMatch(prop -> prop.equals(property));
	}

	public boolean hasSkill(ISkillProperty property) {
		return Stream.concat(
			getSkillsIncludingTemporaryOnes().stream().flatMap(skill -> skill.getSkillProperties().stream()),
			getTemporaryProperties().values().stream().flatMap(Collection::stream)
		).anyMatch(prop -> prop.equals(property));
	}

	protected abstract Map<String, Set<ISkillProperty>> getTemporaryProperties();

	public abstract void addTemporaryProperties(String source, Set<ISkillProperty> properties);

	public abstract void removeTemporaryProperties(String source);

	public void removeEnhancements(INamedObject namedObject) {
		removeEnhancements(namedObject.getName());
	}

	public void removeEnhancements(String sourceName) {
		removeTemporaryModifiers(sourceName);
		removeTemporaryProperties(sourceName);
		removeTemporarySkills(sourceName);
	}

	public void addActivationEnhancements(Card card, SkillFactory factory, StatsMechanic mechanic) {
		addEnhancement(card.getName(), card.activationEnhancement(mechanic), factory);
	}

	public void addDeactivationEnhancements(Card card, SkillFactory factory, StatsMechanic mechanic) {
		addEnhancement(card.getName(), card.deactivationEnhancement(mechanic), factory);
	}

	public void addEnhancement(String name, TemporaryEnhancements enhancements, SkillFactory factory) {
		addTemporaryModifiers(name, enhancements.getModifiers());
		addTemporaryProperties(name, enhancements.getProperties());
		addTemporarySkills(name, enhancements.getSkills().stream().map(scwv -> new SkillWithValue(factory.forClass(scwv.getSkill()), scwv.getValue().orElse(null))).collect(Collectors.toSet()));

	}

	public String getSource(ISkillProperty property) {
		return getTemporaryProperties().entrySet().stream().filter(entry -> entry.getValue().contains(property)).map(Map.Entry::getKey).findFirst().orElse(null);
	}

	public List<SkillDisplayInfo> skillInfos() {
		return getSkillsIncludingTemporaryOnesWithDuplicates().stream()
			.flatMap(s -> skillInfo(s).stream())
			.sorted(Comparator.comparing(SkillDisplayInfo::getInfo))
			.collect(Collectors.toList());
	}

	private Set<SkillDisplayInfo> skillInfo(Skill skill) {
		return skill.evaluator().info(skill, this);
	}

	public Set<String> temporarySkillValues(Skill skill) {
		return getTemporarySkills().values().stream().flatMap(Collection::stream)
			.filter(swv -> swv.getSkill() == skill)
			.map(swv -> swv.getValue().orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	public boolean hasAnimosityTowards(Player<?> player) {
		Skill animosity = getSkillWithProperty(NamedProperties.hasToRollToPassBallOn);
		if (animosity == null || !getTeam().getId().equals(player.getTeam().getId())) {
			return false;
		}

		Set<String> pattern = new HashSet<String>() {{
			add(ANIMOSITY_TO_ALL);
			add(player.getPositionId());
			add(player.getRace());
		}}.stream().filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet());

		return animosity.evaluator().values(animosity, this).stream().map(String::toLowerCase).anyMatch(pattern::contains);
	}

	public boolean canBeThrown(){
		return hasSkillProperty(NamedProperties.canBeThrown) || (hasSkillProperty(NamedProperties.canBeThrownIfStrengthIs3orLess) && getStrengthWithModifiers() <= 3);
	}
}
