package com.fumbbl.ffb.factory.bb2016;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.JumpModifierCollection;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.util.Scanner;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.JUMP_MODIFIER)
@RulesCollection(Rules.BB2016)
public class JumpModifierFactory extends com.fumbbl.ffb.factory.JumpModifierFactory {

	private JumpModifierCollection jumpModifierCollection;

	public JumpModifier forName(String name) {
		return Stream.concat(
				jumpModifierCollection.getModifiers().stream(),
				modifierAggregator.getJumpModifiers().stream())
				.filter(modifier -> modifier.getName().equals(name))
				.findFirst()
				.orElse(null);	}


	@Override
	protected Scanner<JumpModifierCollection> getScanner() {
		return new Scanner<>(JumpModifierCollection.class);
	}

	@Override
	protected JumpModifierCollection getModifierCollection() {
		return jumpModifierCollection;
	}

	@Override
	protected void setModifierCollection(JumpModifierCollection modifierCollection) {
		this.jumpModifierCollection = modifierCollection;
	}

	@Override
	protected Collection<JumpModifier> getModifier(Skill skill) {
		return skill.getJumpModifiers();
	}

	@Override
	protected Optional<JumpModifier> checkClass(RollModifier<?> modifier) {
		return modifier instanceof JumpModifier ? Optional.of((JumpModifier) modifier) : Optional.empty();
	}

	@Override
	protected boolean isAffectedByDisturbingPresence(JumpContext context) {
		return false;
	}

	@Override
	protected boolean isAffectedByTackleZones(JumpContext context) {
		return false;
	}
}
