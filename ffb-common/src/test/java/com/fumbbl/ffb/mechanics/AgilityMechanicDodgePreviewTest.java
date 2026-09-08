package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.skill.bb2025.BreakTackle;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies that only the BB2025 mechanic takes optional dodge modifiers into account when previewing move squares.
 */
class AgilityMechanicDodgePreviewTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(5, 5);
	private static final FieldCoordinate TO = new FieldCoordinate(6, 5);
	private static final DodgeModifier TACKLEZONE = new DodgeModifier("Tacklezone", 1, ModifierType.REGULAR, false);

	private Game game;
	private ActingPlayer actingPlayer;
	private Player<?> player;
	private final Set<Skill> skills = new LinkedHashSet<>();
	private final Set<Skill> usedSkills = new HashSet<>();

	@BeforeEach
	void setUp() {
		BreakTackle breakTackle = new BreakTackle();
		breakTackle.postConstruct();
		ConsummateProfessional consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();
		skills.add(breakTackle);
		skills.add(consummateProfessional);

		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(skills);
		when(player.getStrengthWithModifiers()).thenReturn(3);
		when(player.getAgilityWithModifiers()).thenReturn(4);

		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).then(invocation -> player);
		when(actingPlayer.isSkillUsed(any(Skill.class))).then(invocation -> usedSkills.contains(invocation.getArgument(0)));

		DodgeModifierFactory modifierFactory = mock(DodgeModifierFactory.class);
		when(modifierFactory.findModifiers(any(DodgeContext.class))).then(invocation -> {
			DodgeContext context = invocation.getArgument(0);
			Set<DodgeModifier> modifiers = new HashSet<>();
			modifiers.add(TACKLEZONE);
			for (Skill skill : skills) {
				skill.getDodgeModifiers().stream().filter(modifier -> modifier.appliesToContext(skill, context))
					.forEach(modifiers::add);
			}
			return modifiers;
		});

		game = mock(Game.class);
		when(game.<DodgeModifierFactory>getFactory(Factory.DODGE_MODIFIER)).thenReturn(modifierFactory);
	}

	private void assertPreviewIgnoresOptionalModifiers(AgilityMechanic mechanic) {
		int withoutOptionalModifiers =
			mechanic.minimumRollDodge(game, player, new HashSet<>(Collections.singletonList(TACKLEZONE)));
		assertEquals(withoutOptionalModifiers, mechanic.minimumRollDodgePreview(game, actingPlayer, FROM, TO));
	}

	@Test
	void bb2025PreviewShowsTheBestAchievableRoll() {
		// agility 4 plus one tacklezone is a 5+, both optional modifiers bring it down to a 3+
		assertEquals(3, new com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic()
			.minimumRollDodgePreview(game, actingPlayer, FROM, TO));
	}

	@Test
	void bb2025PreviewIgnoresUsedSkills() {
		usedSkills.addAll(skills);
		assertPreviewIgnoresOptionalModifiers(new com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic());
	}

	@Test
	void bb2020PreviewIgnoresOptionalModifiers() {
		assertPreviewIgnoresOptionalModifiers(new com.fumbbl.ffb.mechanics.bb2020.AgilityMechanic());
	}

	@Test
	void bb2016PreviewIgnoresOptionalModifiers() {
		assertPreviewIgnoresOptionalModifiers(new com.fumbbl.ffb.mechanics.bb2016.AgilityMechanic());
	}
}
