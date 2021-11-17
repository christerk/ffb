package com.fumbbl.ffb.model.skill;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.ISkillBehaviour;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerModifier;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.CatchModifier;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.modifiers.RightStuffModifier;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Skill implements INamedObject {

	private final String name;
	private final SkillCategory category;
	private final List<PlayerModifier> playerModifiers = new ArrayList<>();
	private final List<PassModifier> passModifiers = new ArrayList<>();
	private final List<PickupModifier> pickupModifiers = new ArrayList<>();
	private final List<DodgeModifier> dodgeModifiers = new ArrayList<>();
	private final List<JumpModifier> jumpModifiers = new ArrayList<>();
	private final List<JumpUpModifier> jumpUpModifiers = new ArrayList<>();
	private final List<InterceptionModifier> interceptionModifiers = new ArrayList<>();
	private final List<InjuryModifier> injuryModifiers = new ArrayList<>();
	private final List<ArmorModifier> armorModifiers = new ArrayList<>();
	private final List<CatchModifier> catchModifiers = new ArrayList<>();
	private final List<GazeModifier> gazeModifiers = new ArrayList<>();
	private final List<GoForItModifier> goForItModifiers = new ArrayList<>();
	private final List<RightStuffModifier> rightStuffModifiers = new ArrayList<>();
	private final List<CasualtyModifier> casualtyModifiers = new ArrayList<>();
	private ISkillBehaviour<? extends Skill> behaviour;
	private final List<ISkillProperty> skillProperties = new ArrayList<>();
	private final Map<ReRolledAction, ReRollSource> rerollSources = new HashMap<>();
	private final int defaultSkillValue;
	private final List<ISkillProperty> conflictingProperties = new ArrayList<>();
	private final SkillUsageType skillUsageType;

	public Skill(String name, SkillCategory category) {
		this(name, category, 0);
	}

	public Skill(String name, SkillCategory category, SkillUsageType skillUsageType) {
		this(name, category, 0, skillUsageType);
	}

	public Skill(String name, SkillCategory category, int defaultSkillValue) {
		this(name, category, defaultSkillValue, SkillUsageType.REGULAR);
	}

	public Skill(String name, SkillCategory category, int defaultSkillValue, SkillUsageType skillUsageType) {
		this.name = name;
		this.category = category;
		this.defaultSkillValue = defaultSkillValue;
		this.skillUsageType = skillUsageType;
	}

	public void postConstruct() {
	}

	@Override
	public String getName() {
		return name;
	}

	public SkillCategory getCategory() {
		return category;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Skill skill = (Skill) o;
		return Objects.equals(name, skill.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public static Comparator<Skill> getComparator() {
		return Comparator.comparing(Skill::getName);
	}

	protected void registerModifier(JumpModifier modifier) {
		jumpModifiers.add(modifier);
	}

	protected void registerModifier(JumpUpModifier modifier) {
		jumpUpModifiers.add(modifier);
	}

	protected void registerModifier(PassModifier modifier) {
		passModifiers.add(modifier);
	}

	protected void registerModifier(PickupModifier modifier) {
		pickupModifiers.add(modifier);
	}

	protected void registerModifier(DodgeModifier modifier) {
		dodgeModifiers.add(modifier);
	}

	protected void registerModifier(PlayerModifier modifier) {
		playerModifiers.add(modifier);
	}

	protected void registerModifier(InterceptionModifier modifier) {
		interceptionModifiers.add(modifier);
	}

	protected void registerModifier(ArmorModifier modifier) {
		armorModifiers.add(modifier);
		modifier.setRegisteredTo(this);
	}

	protected void registerModifier(InjuryModifier modifier) {
		injuryModifiers.add(modifier);
		modifier.setRegisteredTo(this);
	}

	protected void registerModifier(CatchModifier modifier) {
		catchModifiers.add(modifier);
	}

	protected void registerModifier(GazeModifier modifier) { gazeModifiers.add(modifier); }

	protected void registerModifier(GoForItModifier modifier) { goForItModifiers.add(modifier); }

	protected void registerModifier(RightStuffModifier modifier) { rightStuffModifiers.add(modifier); }

	protected void registerModifier(CasualtyModifier modifier) {
		casualtyModifiers.add(modifier);
	}

	protected void registerProperty(ISkillProperty property) {
		skillProperties.add(property);
	}

	protected void registerRerollSource(ReRolledAction action, ReRollSource source) {
		rerollSources.put(action, source);
	}

	protected void registerConflictingProperty(ISkillProperty property) {
		conflictingProperties.add(property);
	}

	public ISkillBehaviour<? extends Skill> getSkillBehaviour() {
		return behaviour;
	}

	public List<PlayerModifier> getPlayerModifiers() {
		return playerModifiers;
	}

	public List<PassModifier> getPassModifiers() {
		return passModifiers;
	}

	public List<PickupModifier> getPickupModifiers() {
		return pickupModifiers;
	}

	public List<DodgeModifier> getDodgeModifiers() {
		return dodgeModifiers;
	}

	public List<JumpModifier> getJumpModifiers() {
		return jumpModifiers;
	}

	public List<JumpUpModifier> getJumpUpModifiers() {
		return jumpUpModifiers;
	}

	public List<InterceptionModifier> getInterceptionModifiers() {
		return interceptionModifiers;
	}

	public List<CatchModifier> getCatchModifiers() {
		return catchModifiers;
	}

	public List<ArmorModifier> getArmorModifiers() {
		return armorModifiers;
	}

	public List<InjuryModifier> getInjuryModifiers() {
		return injuryModifiers;
	}

	public List<GazeModifier> getGazeModifiers() {
		return gazeModifiers;
	}

	public List<GoForItModifier> getGoForItModifiers() {
		return goForItModifiers;
	}

	public List<RightStuffModifier> getRightStuffModifiers() {
		return rightStuffModifiers;
	}

	public List<CasualtyModifier> getCasualtyModifiers() {
		return casualtyModifiers;
	}

	public SkillUsageType getSkillUsageType() {
		return skillUsageType;
	}

	public void setBehaviour(ISkillBehaviour<? extends Skill> behaviour) {
		this.behaviour = behaviour;
	}

	public int getCost(Player<?> player) {
		Position position = player.getPosition();
		if (position.hasSkill(this)) {
			return 0;
		}
		if (position.isDoubleCategory(category)) {
			return 30000;
		} else {
			return 20000;
		}
	}

	public String[] getSkillUseDescription() {
		return null;
	}

	public boolean canCancel(Skill otherSkill) {
		for (ISkillProperty skillProperty : skillProperties) {
			if (skillProperty instanceof CancelSkillProperty
					&& ((CancelSkillProperty) skillProperty).cancelsSkill(otherSkill)) {
				return true;
			}
		}
		return false;
	}

	public boolean canCancel(ISkillProperty otherProperty) {
		for (ISkillProperty skillProperty : skillProperties) {
			if (skillProperty instanceof CancelSkillProperty
				&& ((CancelSkillProperty) skillProperty).cancelsProperty(otherProperty)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSkillProperty(ISkillProperty property) {
		return skillProperties.contains(property);
	}

	public String getConfusionMessage() {
		return "is confused";
	}

	public ReRollSource getRerollSource(ReRolledAction action) {
		if (rerollSources.containsKey(action)) {
			return rerollSources.get(action);
		}
		return null;
	}

	public List<ISkillProperty> getSkillProperties() {
		return skillProperties;
	}

	public int getDefaultSkillValue() {
		return defaultSkillValue;
	}

	public SkillValueEvaluator evaluator() {
		return SkillValueEvaluator.DEFAULT;
	}

	@Override
	public String toString() {
		return getName();
	}

	protected boolean meetsRequirements(Player<?> player) {
		return true;
	}

	public boolean canBeAssignedTo(Player<?> player) {
		return meetsRequirements(player) && conflictingProperties.stream().noneMatch(player::hasSkillProperty);
	}
}
