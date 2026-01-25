package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.IInjuryContextModification;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.model.skill.SkillWithValue;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
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

/**
 * @author Kalimar
 */
public abstract class Player<T extends Position> implements IXmlSerializable, IJsonSerializable {

	static final String _XML_ATTRIBUTE_ID = "id";
	static final String _XML_ATTRIBUTE_STATUS = "status";

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

	public abstract void updatePosition(RosterPosition pPosition, IFactorySource game, long gameId);

	public abstract void updatePosition(RosterPosition pPosition, boolean updateStats, IFactorySource game, long gameId);

	public abstract Team getTeam();

	public abstract void setTeam(Team pTeam);

	public abstract String getId();

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

	public abstract void applyPlayerModifiersFromBehaviours(IFactorySource game, long gameId);

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
		return getStatWithModifiers(PlayerStatKey.AG, getAgility());
	}

	public int getMovementWithModifiers() {
		return getStatWithModifiers(PlayerStatKey.MA, getMovement());
	}

	public int getStrengthWithModifiers() {
		return getStatWithModifiers(PlayerStatKey.ST, getStrength());
	}

	public int getPassingWithModifiers() {
		return getStatWithModifiers(PlayerStatKey.PA, getPassing());
	}

	public int getArmourWithModifiers() {
		return getStatWithModifiers(PlayerStatKey.AV, getArmour());
	}

	private int getStatWithModifiers(PlayerStatKey stat, int baseValue) {
		int sum =
			getTemporaryModifiers().values().stream().flatMap(Collection::stream).filter(modifier -> modifier.appliesTo(stat))
				.map(modifier -> modifier.apply(0)).reduce(baseValue, Integer::sum);

		Optional<PlayerStatLimit> limit =
			getTemporaryModifiers().values().stream().flatMap(Collection::stream).filter(modifier -> modifier.appliesTo(stat))
				.map(TemporaryStatModifier::getLimit).findFirst();

		if (limit.isPresent() && limit.get().getMax() != 0) {
			sum = Math.min(limit.get().getMax(), sum);
		}

		if (limit.isPresent() && limit.get().getMin() != 0) {
			sum = Math.max(baseValue == 0 ? 0 : limit.get().getMin(), sum);
		}
		return sum;
	}

	private int findNewStatDecreases(PlayerResult pPlayerResult, InjuryAttribute pInjuryAttribute) {
		int decreases = 0;
		if (pPlayerResult != null) {
			if ((pPlayerResult.getSeriousInjury() != null)
				&& (pPlayerResult.getSeriousInjury().getInjuryAttribute() == pInjuryAttribute)) {
				decreases++;
			}
			if ((pPlayerResult.getSeriousInjuryDecay() != null)
				&& (pPlayerResult.getSeriousInjuryDecay().getInjuryAttribute() == pInjuryAttribute)) {
				decreases++;
			}
		}
		return decreases;
	}

	private int getStatWithModifiers(PlayerStatKey stat, Game game, int baseValue) {
		StatsMechanic mechanic = (StatsMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.STAT.name());
		PlayerResult playerResult = game.getGameResult().getPlayerResult(this);
		int decreases = findNewStatDecreases(playerResult, InjuryAttribute.forStatKey(stat));
		switch (stat) {
			case AG:
				return mechanic.applyInGameAgilityInjury(getAgilityWithModifiers(), decreases);
			case PA:
				int paValue = getStatWithModifiers(stat, baseValue);
				return paValue > 0 ? paValue + decreases : paValue;
			default:
				return getStatWithModifiers(stat, baseValue) - decreases;
		}
	}

	public int getAgilityWithModifiers(Game game) {
		return getStatWithModifiers(PlayerStatKey.AG, game, getAgility());
	}

	public int getMovementWithModifiers(Game game) {
		return getStatWithModifiers(PlayerStatKey.MA, game, getMovement());
	}

	public int getStrengthWithModifiers(Game game) {
		return getStatWithModifiers(PlayerStatKey.ST, game, getStrength());
	}

	public int getPassingWithModifiers(Game game) {
		return getStatWithModifiers(PlayerStatKey.PA, game, getPassing());
	}

	public int getArmourWithModifiers(Game game) {
		return getStatWithModifiers(PlayerStatKey.AV, game, getArmour());
	}

	public abstract Map<String, Set<TemporaryStatModifier>> getTemporaryModifiers();

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

	public abstract Set<String> getEnhancementSources();

	public boolean hasActiveEnhancement(Skill skill) {
		return hasActiveEnhancement(skill.getName());
	}

	public boolean hasActiveEnhancement(String name) {
		return getEnhancementSources().contains(name);
	}

	public boolean hasActiveEnhancement(ISkillProperty property) {
		Skill skill = getSkillWithProperty(property);
		return skill != null && hasActiveEnhancement(skill);
	}

	public abstract void addTemporarySkills(String source, Set<SkillWithValue> skills);

	public abstract void removeTemporarySkills(String source);

	public boolean hasSkillProperty(ISkillProperty property) {
		return Stream.concat(
			getSkillsIncludingTemporaryOnes().stream().flatMap(skill -> skill.getSkillProperties().stream()),
			getTemporaryProperties().values().stream().flatMap(Collection::stream)
		).anyMatch(prop -> prop.equals(property));
	}

	public boolean hasUnusedSkillProperty(ISkillProperty property) {
		return Stream.concat(
			getSkillsIncludingTemporaryOnes().stream().filter(skill -> !this.isUsed(skill))
				.flatMap(skill -> skill.getSkillProperties().stream()),
			getTemporaryProperties().values().stream().flatMap(Collection::stream)
		).anyMatch(prop -> prop.equals(property));
	}

	public boolean hasUsableSkillProperty(ISkillProperty property, PlayerState state) {
		return hasSkillProperty(property)	&& state.isStanding() && !state.isDistracted();
	}

	public boolean hasUsableSkillProperty(ISkillProperty property, Game game) {
		return hasUsableSkillProperty(property, game.getFieldModel().getPlayerState(this));
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
		if (!hasActiveEnhancement(name)) {
			addTemporaryModifiers(name, enhancements.getModifiers());
			addTemporaryProperties(name, enhancements.getProperties());
			addTemporarySkills(name, enhancements.getSkills().stream()
				.map(scwv -> new SkillWithValue(factory.forClass(scwv.getSkill()), scwv.getValue().orElse(null)))
				.collect(Collectors.toSet()));
		}
	}

	public String getSource(ISkillProperty property) {
		return getTemporaryProperties().entrySet().stream().filter(entry -> entry.getValue().contains(property))
			.map(Map.Entry::getKey).findFirst().orElse(null);
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

	public boolean canBeThrown() {
		return hasSkillProperty(NamedProperties.canBeThrown) ||
			(hasSkillProperty(NamedProperties.canBeThrownIfStrengthIs3orLess) && getStrengthWithModifiers() <= 3);
	}

	public abstract PlayerStatus getPlayerStatus();

	public abstract boolean isJourneyman();

	public boolean isUsed(ISkillProperty property) {
		Optional<Skill> skill =
			getSkillsIncludingTemporaryOnes().stream().filter(s -> s.hasSkillProperty(property)).findFirst();

		return skill.isPresent() && isUsed(skill.get());
	}

	public boolean has(Skill skill) {
		return getSkillsIncludingTemporaryOnes().contains(skill);
	}

	public boolean hasUnused(Skill skill) {
		return has(skill) && !isUsed(skill);
	}

	public abstract boolean isUsed(Skill skill);

	public abstract void markUsed(Skill skill, Game game);

	public abstract void markUnused(Skill skill, Game game);

	public abstract void resetUsedSkills(SkillUsageType type, Game game);

	public Optional<IInjuryContextModification> getUnusedInjuryModification(InjuryType injuryType) {
		return getSkillsIncludingTemporaryOnes().stream()
			.filter(skill -> !isUsed(skill) && skill.getSkillBehaviour() != null &&
				skill.getSkillBehaviour().hasInjuryModifier(injuryType))
			.map(skill -> skill.getSkillBehaviour().getInjuryContextModification()).findFirst();
	}

	public boolean canDeclareSkillAction(ISkillProperty property, PlayerState playerState) {
		return Stream.concat(
			getSkillsIncludingTemporaryOnes().stream(),
			getTemporarySkills().values().stream().flatMap(set -> set.stream().map(SkillWithValue::getSkill))
		).anyMatch(skill -> !this.isUsed(skill) && skill.hasSkillProperty(property) &&
			skill.getDeclareCondition().fulfilled(playerState));
	}
}
