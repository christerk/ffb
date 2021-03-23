package com.balancedbytes.games.ffb.factory.bb2016;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.JumpContext;
import com.balancedbytes.games.ffb.modifiers.JumpModifier;
import com.balancedbytes.games.ffb.modifiers.JumpModifierCollection;
import com.balancedbytes.games.ffb.modifiers.RollModifier;
import com.balancedbytes.games.ffb.util.Scanner;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.JUMP_MODIFIER)
@RulesCollection(Rules.BB2016)
public class JumpModifierFactory extends com.balancedbytes.games.ffb.factory.JumpModifierFactory {

	private JumpModifierCollection jumpModifierCollection = new JumpModifierCollection();

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
