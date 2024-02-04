package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.PassContext;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.PassModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PASS_MODIFIER)
@RulesCollection(Rules.COMMON)
public class PassModifierFactory extends GenerifiedModifierFactory<PassContext, PassModifier, PassModifierCollection> {

	private PassModifierCollection passModifierCollection;

	public PassModifier forName(String name) {
		return Stream.concat(
			passModifierCollection.getModifiers().stream(),
			modifierAggregator.getPassModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected Collection<PassModifier> getModifier(Skill skill) {
		return skill.getPassModifiers();
	}

	@Override
	protected Optional<PassModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof PassModifier ? Optional.of((PassModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(PassContext context) {
		return true;
	}

	@Override
	protected boolean isAffectedByTackleZones(PassContext context) {
		return !context.getPlayer().hasSkillProperty(NamedProperties.ignoreTacklezonesWhenPassing);
	}

	@Override
	protected Scanner<PassModifierCollection> getScanner() {
		return new Scanner<>(PassModifierCollection.class);
	}

	@Override
	protected PassModifierCollection getModifierCollection() {
		return passModifierCollection;
	}

	@Override
	protected void setModifierCollection(PassModifierCollection modifierCollection) {
		this.passModifierCollection = modifierCollection;
	}

	@Override
	protected int numberOfTacklezones(PassContext context) {
		MechanicsFactory factory = context.getGame().getFactory(FactoryType.Factory.MECHANIC);
		PassMechanic mechanic = (PassMechanic) factory.forName(Mechanic.Type.PASS.name());
		return mechanic.passModifiers(context.getGame(), context.getPlayer());
	}
}
