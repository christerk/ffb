package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpUpContext;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.JumpUpModifierCollection;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FactoryType(FactoryType.Factory.JUMP_UP_MODIFIER)
@RulesCollection(Rules.COMMON)
public class JumpUpModifierFactory extends GenerifiedModifierFactory<JumpUpContext, JumpUpModifier, JumpUpModifierCollection> {

	private JumpUpModifierCollection jumpUpModifierCollection;

	public JumpUpModifier forName(String name) {
		return Stream.concat(
			jumpUpModifierCollection.getModifiers().stream(),
			modifierAggregator.getJumpUpModifiers().stream())
			.filter(modifier -> modifier.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Set<JumpUpModifier> forType(ModifierType type) {
		return Stream.concat(
			modifierAggregator.getJumpUpModifiers().stream().filter(jumpUpModifier -> jumpUpModifier.getType() == type),
			jumpUpModifierCollection.getModifiers(type).stream())
			.collect(Collectors.toSet());
	}

	@Override
	protected Scanner<JumpUpModifierCollection> getScanner() {
		return new Scanner<>(JumpUpModifierCollection.class);
	}

	@Override
	protected JumpUpModifierCollection getModifierCollection() {
		return jumpUpModifierCollection;
	}

	@Override
	protected void setModifierCollection(JumpUpModifierCollection modifierCollection) {
		jumpUpModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<JumpUpModifier> getModifier(Skill skill) {
		return skill.getJumpUpModifiers();
	}

	@Override
	protected Optional<JumpUpModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof JumpUpModifier ? Optional.of((JumpUpModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(JumpUpContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(JumpUpContext context) {
		return false;
	}
}
