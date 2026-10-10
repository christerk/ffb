package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.JumpModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.JumpContext;
import com.fumbbl.ffb.modifiers.JumpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.skill.bb2025.Leap;
import com.fumbbl.ffb.skill.bb2025.Pogo;
import com.fumbbl.ffb.skill.bb2025.VeryLongLegs;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JumpModifierSelectionServiceTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(5, 5);
	private static final FieldCoordinate TO = new FieldCoordinate(7, 5);
	private static final JumpModifier TACKLEZONES = new JumpModifier("Tacklezones", 2, ModifierType.REGULAR, false);

	private final JumpModifierSelectionService service = new JumpModifierSelectionService();

	private Game game;
	private ActingPlayer actingPlayer;
	private Player<?> player;
	private final Set<Skill> skills = new LinkedHashSet<>();
	private final Set<Skill> usedSkills = new HashSet<>();

	private ConsummateProfessional consummateProfessional;
	private VeryLongLegs veryLongLegs;
	private Leap leap;
	private Pogo pogo;

	@BeforeEach
	void setUp() {
		consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();
		veryLongLegs = new VeryLongLegs();
		veryLongLegs.postConstruct();
		leap = new Leap();
		leap.postConstruct();
		pogo = new Pogo();
		pogo.postConstruct();

		player = mock(Player.class);
		when(player.getSkillsIncludingTemporaryOnes()).thenReturn(skills);
		when(player.getAgilityWithModifiers()).thenReturn(4);

		actingPlayer = mock(ActingPlayer.class);
		when(actingPlayer.getPlayer()).then(invocation -> player);
		when(actingPlayer.isSkillUsed(any(Skill.class))).then(invocation -> usedSkills.contains(invocation.getArgument(0)));

		JumpModifierFactory modifierFactory = mock(JumpModifierFactory.class);
		when(modifierFactory.findModifiers(any(JumpContext.class))).then(invocation -> {
			JumpContext context = invocation.getArgument(0);
			Set<JumpModifier> modifiers = new HashSet<>();
			if (!ignoresTacklezones(context)) {
				modifiers.add(TACKLEZONES);
			}
			collect(context, modifiers, false);
			int sum = modifiers.stream().mapToInt(JumpModifier::getModifier).sum();
			context.setAccumulatedModifiers(sum + context.getAccumulatedModifiers());
			context.addModifierCount(modifiers.size());
			collect(context, modifiers, true);
			return modifiers;
		});

		game = mock(Game.class);
		when(game.getActingPlayer()).thenReturn(actingPlayer);
		when(game.<AgilityMechanic>getMechanic(Mechanic.Type.AGILITY)).thenReturn(new AgilityMechanic());
		when(game.<JumpModifierFactory>getFactory(Factory.JUMP_MODIFIER)).thenReturn(modifierFactory);
	}

	private void collect(JumpContext context, Set<JumpModifier> modifiers, boolean dependingOnOthers) {
		for (Skill skill : skills) {
			skill.getJumpModifiers().stream()
				.filter(modifier -> (modifier.getType() == ModifierType.DEPENDS_ON_SUM_OF_OTHERS) == dependingOnOthers)
				.filter(modifier -> modifier.appliesToContext(skill, context))
				.forEach(modifiers::add);
		}
	}

	private boolean ignoresTacklezones(JumpContext context) {
		return skills.stream().filter(skill -> skill.hasSkillProperty(NamedProperties.ignoreTacklezonesWhenJumping))
			.anyMatch(skill -> !context.areFreeModifiersOptional() || context.isSkillSelected(skill));
	}

	private List<ModifierChoiceOption> findOptions(int roll) {
		return findOptions(roll, false);
	}

	private List<ModifierChoiceOption> findOptions(int roll, boolean freeModifiersOptional) {
		return findOptions(roll, freeModifiersOptional, Collections.emptySet());
	}

	private List<ModifierChoiceOption> findOptions(int roll, boolean freeModifiersOptional, Set<Skill> selectedSkills) {
		return service.findOptions(game, actingPlayer, FROM, TO, selectedSkills, Collections.emptySet(),
			freeModifiersOptional, roll);
	}

	private List<String> findFreeSkills(boolean freeModifiersOptional, int roll) {
		return service.findFreeSkills(game, actingPlayer, FROM, TO, Collections.emptySet(), Collections.emptySet(),
			freeModifiersOptional, roll).stream().map(Skill::getName).collect(Collectors.toList());
	}

	private List<ModifierChoiceOption> findCombinations(boolean freeModifiersOptional) {
		return service.findCombinations(game, actingPlayer, FROM, TO, Collections.emptySet(), Collections.emptySet(),
			freeModifiersOptional);
	}

	private List<String> labels(List<ModifierChoiceOption> options) {
		return options.stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList());
	}

	@Test
	void noOptionalSkillsYieldNoOptions() {
		assertTrue(findOptions(5).isEmpty());
	}

	@Test
	void consummateProfessionalRescuesTheRoll() {
		skills.add(consummateProfessional);
		// agility 4+, two tacklezones => 6+ needed, consummate professional gives -1
		List<ModifierChoiceOption> options = findOptions(5);
		assertEquals(Collections.singletonList("Consummate Professional"), labels(options));
		assertEquals(5, options.get(0).getMinimumRoll());
		assertEquals(-1, options.get(0).getTotalModifier());
	}

	@Test
	void usedSkillsAreNotOffered() {
		skills.add(consummateProfessional);
		usedSkills.add(consummateProfessional);
		assertTrue(findOptions(5).isEmpty());
	}

	@Test
	void aRollThatCannotBeRescuedYieldsNoOptions() {
		skills.add(consummateProfessional);
		assertTrue(findOptions(3).isEmpty());
	}

	@Test
	void freeModifiersAreNotOfferedWhileTheyApplyAutomatically() {
		skills.add(veryLongLegs);
		// very long legs is already part of the roll, so there is nothing left to pick
		assertTrue(findOptions(5).isEmpty());
		assertTrue(findFreeSkills(false, 5).isEmpty());
	}

	@Test
	void freeModifiersAreOfferedSeparatelyWhenTheyMayBeDeclined() {
		skills.add(veryLongLegs);
		// free skills are asked for with a skill use dialog, so they are kept out of the modifier choice
		assertTrue(findOptions(5, true).isEmpty());
		assertEquals(Collections.singletonList("Very Long Legs"), findFreeSkills(true, 5));
	}

	@Test
	void declinableLeapIsOfferedOnTopOfAnotherModifier() {
		skills.addAll(Arrays.asList(leap, consummateProfessional));
		assertEquals(Collections.singletonList("Leap"), findFreeSkills(true, 4));
		// leap only applies once the other modifiers add up, so it is worth picking together with them
		List<ModifierChoiceOption> options =
			findOptions(4, true, new HashSet<>(Collections.singletonList(leap)));
		assertEquals(Collections.singletonList("Consummate Professional"), labels(options));
		assertEquals(4, options.get(0).getMinimumRoll());
	}

	@Test
	void declinablePogoIsOfferedAlthoughItIsNoModifier() {
		skills.add(pogo);
		assertTrue(findOptions(4, true).isEmpty());
		assertEquals(Collections.singletonList("Pogo"), findFreeSkills(true, 4));
	}

	@Test
	void freeModifiersAreNotOfferedWhenTheyCannotRescueTheRoll() {
		skills.add(veryLongLegs);
		// very long legs only brings the needed roll down to 5+, so a 2 fails either way
		assertTrue(findFreeSkills(true, 2).isEmpty());
	}

	@Test
	void freeModifiersAreNotOfferedWhenEvenEveryModifierTogetherFails() {
		skills.addAll(Arrays.asList(leap, consummateProfessional));
		// leap and consummate professional together still need a 4+
		assertTrue(findFreeSkills(true, 3).isEmpty());
	}

	@Test
	void combinationsAreListedWithTheRollTheyNeed() {
		skills.addAll(Arrays.asList(veryLongLegs, consummateProfessional));

		List<ModifierChoiceOption> combinations = findCombinations(true);

		assertEquals(Collections.singletonList("Consummate Professional"), labels(combinations));
		assertEquals(5, combinations.get(0).getMinimumRoll());
	}

	@Test
	void combinationsAreIndependentOfTheRolledDie() {
		skills.addAll(Arrays.asList(veryLongLegs, consummateProfessional));

		assertTrue(findOptions(2, true).isEmpty());
		assertEquals(1, findCombinations(true).size());
	}
}
