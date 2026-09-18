package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.DodgeModifierFactory;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.bb2025.AgilityMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.ModifierChoiceOption;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameRules;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.DodgeContext;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.skill.bb2025.BreakTackle;
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

class DodgeModifierSelectionServiceTest {

	private static final FieldCoordinate FROM = new FieldCoordinate(5, 5);
	private static final FieldCoordinate TO = new FieldCoordinate(6, 5);
	private static final DodgeModifier TACKLEZONE = new DodgeModifier("Tacklezone", 1, ModifierType.REGULAR, false);

	private final DodgeModifierSelectionService service = new DodgeModifierSelectionService();

	private Game game;
	private ActingPlayer actingPlayer;
	private Player<?> player;
	private final Set<Skill> skills = new LinkedHashSet<>();
	private final Set<Skill> usedSkills = new HashSet<>();

	private BreakTackle breakTackle;
	private ConsummateProfessional consummateProfessional;

	@BeforeEach
	void setUp() {
		breakTackle = new BreakTackle();
		breakTackle.postConstruct();
		consummateProfessional = new ConsummateProfessional();
		consummateProfessional.postConstruct();

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

		MechanicsFactory mechanicsFactory = mock(MechanicsFactory.class);
		when(mechanicsFactory.forName(Mechanic.Type.AGILITY.name())).thenReturn(new AgilityMechanic());
		GameRules gameRules = mock(GameRules.class);
		when(gameRules.<MechanicsFactory>getFactory(Factory.MECHANIC)).thenReturn(mechanicsFactory);

		game = mock(Game.class);
		when(game.getRules()).thenReturn(gameRules);
		when(game.<DodgeModifierFactory>getFactory(Factory.DODGE_MODIFIER)).thenReturn(modifierFactory);
	}

	private List<ModifierChoiceOption> findOptions(int dodgeRoll) {
		return service.findOptions(game, actingPlayer, FROM, TO, Collections.emptySet(), dodgeRoll);
	}

	private List<String> labels(List<ModifierChoiceOption> options) {
		return options.stream().map(ModifierChoiceOption::getLabel).collect(Collectors.toList());
	}

	@Test
	void noOptionalSkillsYieldNoOptions() {
		assertTrue(findOptions(4).isEmpty());
	}

	@Test
	void breakTackleAloneRescuesTheRoll() {
		skills.add(breakTackle);
		// agility 4, tacklezone +1 => 5+ needed, break tackle at strength 3 gives -1
		List<ModifierChoiceOption> options = findOptions(4);
		assertEquals(Collections.singletonList("Break Tackle"), labels(options));
		assertEquals(4, options.get(0).getMinimumRoll());
		assertEquals(1, options.get(0).getSkills().size());
	}

	@Test
	void consummateProfessionalAloneRescuesTheRoll() {
		skills.add(consummateProfessional);
		List<ModifierChoiceOption> options = findOptions(4);
		assertEquals(Collections.singletonList("Consummate Professional"), labels(options));
		assertEquals(4, options.get(0).getMinimumRoll());
	}

	@Test
	void combinationIsOfferedWhenSingleSkillsAreNotEnough() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		// a 3 on the die needs both modifiers to reach a 3+
		List<ModifierChoiceOption> options = findOptions(3);
		assertEquals(Collections.singletonList("Break Tackle + Consummate Professional"), labels(options));
		assertEquals(3, options.get(0).getMinimumRoll());
	}

	@Test
	void onlyTheSkillUsableMoreOftenIsOfferedForTheSameBonus() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		// both skills give the same bonus at strength 3, consummate professional is the once per game one
		List<ModifierChoiceOption> options = findOptions(4);
		assertEquals(Collections.singletonList("Break Tackle"), labels(options));
	}

	@Test
	void bothSkillsAreOfferedWhenTheirBonusDiffers() {
		when(player.getStrengthWithModifiers()).thenReturn(4);
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		// break tackle gives -2 at strength 4, so the skills are no longer interchangeable
		assertEquals(Arrays.asList("Break Tackle", "Consummate Professional"), labels(findOptions(4)));
	}

	@Test
	void usedSkillsAreNotOffered() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		usedSkills.add(breakTackle);
		assertEquals(Collections.singletonList("Consummate Professional"), labels(findOptions(4)));
	}

	@Test
	void extraModifiersShiftTheThreshold() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		DodgeModifier divingTackle = new DodgeModifier("Diving Tackle", 2, ModifierType.DIVING_TACKLE, false);
		List<ModifierChoiceOption> options = service.findOptions(game, actingPlayer, FROM, TO,
			new HashSet<>(Collections.singletonList(divingTackle)), 5);
		// 5+ with diving tackle becomes 7+, both modifiers are needed to get back to a 5+
		assertEquals(Collections.singletonList("Break Tackle + Consummate Professional"), labels(options));
		assertEquals(5, options.get(0).getMinimumRoll());
	}

	@Test
	void aRollThatCannotBeRescuedYieldsNoOptions() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));
		assertTrue(findOptions(2).isEmpty());
	}

	@Test
	void noOptionalSkillsYieldNoCombinations() {
		assertTrue(findCombinations().isEmpty());
	}

	@Test
	void allCombinationsAreListedWithTheRollTheyNeed() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));

		List<ModifierChoiceOption> combinations = findCombinations();

		// consummate professional alone is not listed, break tackle gives the same bonus and can be used more often
		assertEquals(Arrays.asList("Break Tackle + Consummate Professional", "Break Tackle"), labels(combinations));
		assertEquals(3, combinations.get(0).getMinimumRoll());
		assertEquals(4, combinations.get(1).getMinimumRoll());
	}

	@Test
	void combinationsThatDoNotImproveOnASubsetAreNotListed() {
		when(player.getAgilityWithModifiers()).thenReturn(2);
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));

		// agility 2 with a tacklezone needs a 3+, a single modifier already caps the roll at 2+
		assertEquals(Collections.singletonList("Break Tackle"), labels(findCombinations()));
	}

	@Test
	void combinationsAreIndependentOfTheRolledDie() {
		skills.addAll(Arrays.asList(breakTackle, consummateProfessional));

		// nothing rescues a 2, but the coach still learns what a re-roll could achieve
		assertTrue(findOptions(2).isEmpty());
		assertEquals(2, findCombinations().size());
	}

	private List<ModifierChoiceOption> findCombinations() {
		return service.findCombinations(game, actingPlayer, FROM, TO, Collections.emptySet());
	}
}
