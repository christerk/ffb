package com.fumbbl.ffb.factory;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PickupContext;
import com.fumbbl.ffb.modifiers.PickupModifier;
import com.fumbbl.ffb.modifiers.PickupModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PICKUP_MODIFIER)
@RulesCollection(Rules.COMMON)
public class PickupModifierFactory extends GenerifiedModifierFactory<PickupContext, PickupModifier, PickupModifierCollection> {

	private PickupModifierCollection pickupModifierCollection = new PickupModifierCollection();

	public PickupModifier forName(String name) {
		return Stream.concat(
			pickupModifierCollection.getModifiers().stream(),
			modifierAggregator.getPickupModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected Scanner<PickupModifierCollection> getScanner() {
		return new Scanner<>(PickupModifierCollection.class);
	}

	@Override
	protected PickupModifierCollection getModifierCollection() {
		return pickupModifierCollection;
	}

	@Override
	protected void setModifierCollection(PickupModifierCollection modifierCollection) {
		this.pickupModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<PickupModifier> getModifier(Skill skill) {
		return skill.getPickupModifiers();
	}

	@Override
	protected Optional<PickupModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof PickupModifier ? Optional.of((PickupModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(PickupContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(PickupContext context) {
		return !context.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenPickingUp);
	}

}
