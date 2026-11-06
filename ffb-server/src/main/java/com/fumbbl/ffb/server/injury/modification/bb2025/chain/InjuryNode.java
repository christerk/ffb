package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.ArmorModifierFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InjuryModifierFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.InjuryModificationOption;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.InjuryModifier;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.ArrayList;
import java.util.List;

/**
 * One fully evaluated outcome of an injury. Its result is always recomputed from its dice and the modifications
 * applied on its path (see {@link InjuryNodeEvaluationService}).
 */
public class InjuryNode implements IJsonSerializable {

	private int id;
	private int parentId = -1;
	private InjuryPhase phase = InjuryPhase.ARMOUR;
	private boolean armourReRolled;
	private boolean injuryReRolled;
	private int[] armourDice;
	private int[] injuryDice;
	private final List<ArmorModifier> removedArmourModifiers = new ArrayList<>();
	private final List<ArmorModifier> skillArmourModifiers = new ArrayList<>();
	private final List<InjuryModifier> skillInjuryModifiers = new ArrayList<>();
	private final List<AppliedModification> appliedModifications = new ArrayList<>();
	private InjuryModificationOption option;
	private final ReportList edgeReports = new ReportList();
	private InjuryContext context;
	private boolean spotted;
	private boolean expanded;
	private final List<DecisionGroup> groups = new ArrayList<>();

	public InjuryNode() {
	}

	public InjuryNode(int id) {
		this.id = id;
	}

	/**
	 * Creates a child carrying the dice, flags, modifiers and path of this node. Evaluation and groups are not copied.
	 */
	public InjuryNode createChild(int childId, InjuryPhase childPhase) {
		InjuryNode child = new InjuryNode(childId);
		child.parentId = id;
		child.phase = childPhase;
		child.armourReRolled = armourReRolled;
		child.injuryReRolled = injuryReRolled;
		child.armourDice = armourDice == null ? null : armourDice.clone();
		child.injuryDice = injuryDice == null ? null : injuryDice.clone();
		child.removedArmourModifiers.addAll(removedArmourModifiers);
		child.skillArmourModifiers.addAll(skillArmourModifiers);
		child.skillInjuryModifiers.addAll(skillInjuryModifiers);
		child.appliedModifications.addAll(appliedModifications);
		return child;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public InjuryPhase getPhase() {
		return phase;
	}

	public void setPhase(InjuryPhase phase) {
		this.phase = phase;
	}

	public boolean isArmourReRolled() {
		return armourReRolled;
	}

	public void setArmourReRolled(boolean armourReRolled) {
		this.armourReRolled = armourReRolled;
	}

	public boolean isInjuryReRolled() {
		return injuryReRolled;
	}

	public void setInjuryReRolled(boolean injuryReRolled) {
		this.injuryReRolled = injuryReRolled;
	}

	public boolean isReRolled(InjuryPhase rollPhase) {
		return rollPhase == InjuryPhase.ARMOUR ? armourReRolled : injuryReRolled;
	}

	public int[] getArmourDice() {
		return armourDice;
	}

	public void setArmourDice(int[] armourDice) {
		this.armourDice = armourDice;
	}

	public int[] getInjuryDice() {
		return injuryDice;
	}

	public void setInjuryDice(int[] injuryDice) {
		this.injuryDice = injuryDice;
	}

	public List<ArmorModifier> getRemovedArmourModifiers() {
		return removedArmourModifiers;
	}

	public List<ArmorModifier> getSkillArmourModifiers() {
		return skillArmourModifiers;
	}

	public List<InjuryModifier> getSkillInjuryModifiers() {
		return skillInjuryModifiers;
	}

	public List<AppliedModification> getAppliedModifications() {
		return appliedModifications;
	}

	/**
	 * The modifications applied on the edge from the given parent to this node.
	 */
	public List<AppliedModification> getEdgeModifications(InjuryNode parent) {
		int start = parent == null ? 0 : Math.min(parent.appliedModifications.size(), appliedModifications.size());
		return appliedModifications.subList(start, appliedModifications.size());
	}

	public boolean isApplied(Skill skill, String playerId) {
		return appliedModifications.stream().anyMatch(applied -> applied.matches(skill, playerId));
	}

	public InjuryModificationOption getOption() {
		return option;
	}

	public void setOption(InjuryModificationOption option) {
		this.option = option;
	}

	public ReportList getEdgeReports() {
		return edgeReports;
	}

	public void addEdgeReport(IReport report) {
		edgeReports.add(report);
	}

	public InjuryContext getContext() {
		return context;
	}

	public void setContext(InjuryContext context) {
		this.context = context;
	}

	public boolean isSpotted() {
		return spotted;
	}

	public void setSpotted(boolean spotted) {
		this.spotted = spotted;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public List<DecisionGroup> getGroups() {
		return groups;
	}

	public void addGroup(DecisionGroup group) {
		groups.add(group);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.NODE_ID.addTo(jsonObject, id);
		IServerJsonOption.PARENT_NODE_ID.addTo(jsonObject, parentId);
		IServerJsonOption.INJURY_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.ARMOUR_RE_ROLLED.addTo(jsonObject, armourReRolled);
		IServerJsonOption.INJURY_RE_ROLLED.addTo(jsonObject, injuryReRolled);
		IServerJsonOption.ARMOUR_DICE.addTo(jsonObject, armourDice);
		IServerJsonOption.INJURY_DICE.addTo(jsonObject, injuryDice);
		IServerJsonOption.REMOVED_ARMOUR_MODIFIERS.addTo(jsonObject, toArray(removedArmourModifiers));
		IServerJsonOption.SKILL_ARMOUR_MODIFIERS.addTo(jsonObject, toArray(skillArmourModifiers));
		JsonArray injuryModifiers = new JsonArray();
		skillInjuryModifiers.forEach(modifier -> injuryModifiers.add(UtilJson.toJsonValue(modifier)));
		IServerJsonOption.SKILL_INJURY_MODIFIERS.addTo(jsonObject, injuryModifiers);
		JsonArray applied = new JsonArray();
		appliedModifications.forEach(modification -> applied.add(modification.toJsonValue()));
		IServerJsonOption.APPLIED_MODIFICATIONS.addTo(jsonObject, applied);
		if (option != null) {
			IServerJsonOption.INJURY_MODIFICATION_OPTION.addTo(jsonObject, option.toJsonValue());
		}
		IServerJsonOption.REPORT_LIST.addTo(jsonObject, edgeReports.toJsonValue());
		if (context != null) {
			JsonObject contextObject = new JsonObject();
			context.toJsonValue(contextObject);
			IServerJsonOption.INJURY_CONTEXT.addTo(jsonObject, contextObject);
		}
		IServerJsonOption.SPOTTED.addTo(jsonObject, spotted);
		IServerJsonOption.EXPANDED.addTo(jsonObject, expanded);
		JsonArray groupArray = new JsonArray();
		groups.forEach(group -> groupArray.add(group.toJsonValue()));
		IServerJsonOption.DECISION_GROUPS.addTo(jsonObject, groupArray);
		return jsonObject;
	}

	private JsonArray toArray(List<ArmorModifier> modifiers) {
		JsonArray array = new JsonArray();
		modifiers.forEach(modifier -> array.add(UtilJson.toJsonValue(modifier)));
		return array;
	}

	@Override
	public InjuryNode initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		id = IServerJsonOption.NODE_ID.getFrom(source, jsonObject);
		parentId = IServerJsonOption.PARENT_NODE_ID.getFrom(source, jsonObject);
		phase = InjuryPhase.valueOf(IServerJsonOption.INJURY_PHASE.getFrom(source, jsonObject));
		armourReRolled = Boolean.TRUE.equals(IServerJsonOption.ARMOUR_RE_ROLLED.getFrom(source, jsonObject));
		injuryReRolled = Boolean.TRUE.equals(IServerJsonOption.INJURY_RE_ROLLED.getFrom(source, jsonObject));
		armourDice = IServerJsonOption.ARMOUR_DICE.getFrom(source, jsonObject);
		injuryDice = IServerJsonOption.INJURY_DICE.getFrom(source, jsonObject);
		ArmorModifierFactory armorModifierFactory = source.getFactory(FactoryType.Factory.ARMOUR_MODIFIER);
		readArmourModifiers(armorModifierFactory, IServerJsonOption.REMOVED_ARMOUR_MODIFIERS.getFrom(source, jsonObject),
			removedArmourModifiers);
		readArmourModifiers(armorModifierFactory, IServerJsonOption.SKILL_ARMOUR_MODIFIERS.getFrom(source, jsonObject),
			skillArmourModifiers);
		skillInjuryModifiers.clear();
		InjuryModifierFactory injuryModifierFactory = source.getFactory(FactoryType.Factory.INJURY_MODIFIER);
		JsonArray injuryModifiers = IServerJsonOption.SKILL_INJURY_MODIFIERS.getFrom(source, jsonObject);
		if (injuryModifiers != null) {
			injuryModifiers.values().forEach(value ->
				skillInjuryModifiers.add((InjuryModifier) UtilJson.toEnumWithName(injuryModifierFactory, value)));
		}
		appliedModifications.clear();
		JsonArray applied = IServerJsonOption.APPLIED_MODIFICATIONS.getFrom(source, jsonObject);
		if (applied != null) {
			applied.values().forEach(value -> appliedModifications.add(new AppliedModification().initFrom(source, value)));
		}
		JsonObject optionObject = IServerJsonOption.INJURY_MODIFICATION_OPTION.getFrom(source, jsonObject);
		option = optionObject != null ? new InjuryModificationOption().initFrom(source, optionObject) : null;
		edgeReports.clear();
		JsonObject reports = IServerJsonOption.REPORT_LIST.getFrom(source, jsonObject);
		if (reports != null) {
			edgeReports.initFrom(source, reports);
		}
		JsonObject contextObject = IServerJsonOption.INJURY_CONTEXT.getFrom(source, jsonObject);
		if (contextObject != null) {
			context = new InjuryContext();
			context.initFrom(source, contextObject);
		}
		spotted = Boolean.TRUE.equals(IServerJsonOption.SPOTTED.getFrom(source, jsonObject));
		expanded = Boolean.TRUE.equals(IServerJsonOption.EXPANDED.getFrom(source, jsonObject));
		groups.clear();
		JsonArray groupArray = IServerJsonOption.DECISION_GROUPS.getFrom(source, jsonObject);
		if (groupArray != null) {
			groupArray.values().forEach(value -> groups.add(new DecisionGroup().initFrom(source, value)));
		}
		return this;
	}

	private void readArmourModifiers(ArmorModifierFactory factory, JsonArray array, List<ArmorModifier> target) {
		target.clear();
		if (array != null) {
			array.values().forEach(value -> target.add((ArmorModifier) UtilJson.toEnumWithName(factory, value)));
		}
	}
}
