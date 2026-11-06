package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.mechanics.InjuryMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.IRegistrationAwareModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * BB2025: regular skills that can be declined by game option. Their modifiers are excluded from the base evaluation
 * of the injury types and offered as chain modifications instead. Without injury modification chains or with the
 * option disabled nothing changes.
 */
public class OptionalSkillModifierService {

	private static final Map<ISkillProperty, GameOptionId> OPTIONS = new LinkedHashMap<>();

	static {
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryOnBlock, GameOptionId.MIGHTY_BLOW_OPTIONAL);
		OPTIONS.put(NamedProperties.reducesArmourToFixedValue, GameOptionId.CLAWS_OPTIONAL);
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryOnFoul, GameOptionId.DIRTY_PLAYER_OPTIONAL);
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryWithPartner, GameOptionId.SNEAKY_PAIR_OPTIONAL);
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryOnTtm, GameOptionId.LETHAL_FLIGHT_OPTIONAL);
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryOnDodge, GameOptionId.ARM_BAR_OPTIONAL);
		OPTIONS.put(NamedProperties.affectsEitherArmourOrInjuryOnJump, GameOptionId.ARM_BAR_OPTIONAL);
	}

	public boolean isOptional(Game game, Skill skill) {
		if (game == null || skill == null || !usesChains(game)) {
			return false;
		}
		return OPTIONS.entrySet().stream()
			.anyMatch(entry -> skill.hasSkillProperty(entry.getKey()) && UtilGameOption.isOptionEnabled(game, entry.getValue()));
	}

	public boolean isOptional(Game game, IRegistrationAwareModifier modifier) {
		return modifier != null && isOptional(game, modifier.getRegisteredTo());
	}

	/**
	 * Removes the modifiers of optional skills from the given base modifiers.
	 */
	public <T extends IRegistrationAwareModifier> void removeOptional(Game game, Collection<T> modifiers) {
		if (modifiers != null && usesChains(game)) {
			modifiers.removeIf(modifier -> isOptional(game, modifier));
		}
	}

	/**
	 * The skill unless it is optional, for injury types that apply a skill's modifiers directly.
	 */
	public Optional<Skill> unlessOptional(Game game, Skill skill) {
		return Optional.ofNullable(skill).filter(present -> !isOptional(game, present));
	}

	private boolean usesChains(Game game) {
		InjuryMechanic mechanic =
			(InjuryMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.INJURY.name());
		return mechanic != null && mechanic.usesInjuryModificationChains();
	}
}
