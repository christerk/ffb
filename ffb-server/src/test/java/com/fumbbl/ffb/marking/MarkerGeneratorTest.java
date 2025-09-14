package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.bb2020.SeriousInjury;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.mechanics.bb2020.StatsMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.marking.ApplyTo;
import com.fumbbl.ffb.server.marking.AutoMarkingConfig;
import com.fumbbl.ffb.server.marking.AutoMarkingRecord;
import com.fumbbl.ffb.server.marking.MarkerGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@MockitoSettings(strictness = Strictness.LENIENT)
class MarkerGeneratorTest {

	private static final String BLOCK_MARKING = "B";
	private static final String BLODGE_MARKING = "X";
	private static final String DODGE_MARKING = "D";
	private static final String BLACKLE_MARKING = "Y";
	private static final String WRECKLE_MARKING = "Q";
	private static final String MA_MARKING = "Ma";
	private static final String AG_MARKING = "Ag";
	private static final String NI_MARKING = "Ni";
	private static final String TACKLE_MARKING = "T";
	private static final String WRESTLE_MARKING = "W";
	public static final String OTHER_MARKING = "O";
	private static final String SNEAKY_GIT = "sneaky git";
	private static final String BLOCK = "block";
	private static final String DODGE = "dodge";
	private static final String TACKLE = "tackle";
	private static final String WRESTLE = "wrestle";
	private static final String UNKNOWN = "unknown";
	private static final String SEPARATOR = ", ";

	private static final int MOVE = 6;
	private static final int STRENGTH = 3;
	private static final int AGILITY = 3;
	private static final int PASSING = 4;
	private static final int ARMOUR = 8;

	private final MarkerGenerator generator = new MarkerGenerator();
	private AutoMarkingConfig config;
	private AutoMarkingRecord.Builder builder;
	private MockSkillFactory skillFactory;
	private List<AutoMarkingRecord> markings;
	@Mock
	private Player<Position> player;
	@Mock
	private Position position;
	@Mock
	private Game game;
	@Mock
	private GameResult gameResult;
	@Mock
	private PlayerResult playerResult;
	@Mock
	private MechanicsFactory mechanicsFactory;

	@BeforeEach
	public void setUp() {
		skillFactory = new MockSkillFactory();
		config = new AutoMarkingConfig();
		builder = new AutoMarkingRecord.Builder(skillFactory);
		markings = config.getMarkings();

		given(game.getFactory(FactoryType.Factory.MECHANIC)).willReturn(mechanicsFactory);
		given(game.getFactory(FactoryType.Factory.SKILL)).willReturn(skillFactory);
		given(mechanicsFactory.forName(anyString())).willReturn(new StatsMechanic());

		Set<Skill> gainedSkills = new HashSet<Skill>() {{
			add(skillFactory.forName(BLOCK));
			add(skillFactory.forName(DODGE));
		}};
		given(player.getSkillsIncludingTemporaryOnes()).willReturn(gainedSkills);

		Skill[] baseSkills = {skillFactory.forName(WRESTLE), skillFactory.forName(TACKLE)};
		given(player.getPosition()).willReturn(position);
		given(position.getSkills()).willReturn(baseSkills);

		given(player.getMovementWithModifiers(game)).willReturn(MOVE - 2);
		given(player.getStrengthWithModifiers(game)).willReturn(STRENGTH);
		given(player.getAgilityWithModifiers(game)).willReturn(AGILITY + 1);
		given(player.getPassingWithModifiers(game)).willReturn(PASSING);
		given(player.getArmourWithModifiers(game)).willReturn(ARMOUR);

		given(position.getMovement()).willReturn(MOVE);
		given(position.getStrength()).willReturn(STRENGTH);
		given(position.getAgility()).willReturn(AGILITY);
		given(position.getPassing()).willReturn(PASSING);
		given(position.getArmour()).willReturn(ARMOUR);

		given(player.getLastingInjuries()).willReturn(new SeriousInjury[]{SeriousInjury.HEAD_INJURY, SeriousInjury.SERIOUS_INJURY});
		given(game.getGameResult()).willReturn(gameResult);
		given(gameResult.getPlayerResult(player)).willReturn(playerResult);
	}

	@Test
	public void generate() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateWithoutSorting() {
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());

		config.setSortMode(SortMode.NONE);

		String marking = generator.generate(game, player, config, true);

		assertEquals(DODGE_MARKING + MA_MARKING + BLOCK_MARKING, marking);
	}


	@Test
	public void generateForSuperSetsWithoutSorting() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(DODGE_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withSkill(TACKLE).withMarking(BLACKLE_MARKING).build());

		config.setSortMode(SortMode.NONE);

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLACKLE_MARKING, marking);
	}

	@Test
	public void generateWithSeparator() {
		config.setSeparator(SEPARATOR);
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).withApplyRepeatedly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING + SEPARATOR + DODGE_MARKING + SEPARATOR + MA_MARKING + SEPARATOR + MA_MARKING, marking);
	}

	@Test
	public void ignoreUnknownSkills() {
		Skill[] gainedSkills = {skillFactory.forName(BLOCK), skillFactory.forName(DODGE), null};
		given(player.getSkills()).willReturn(gainedSkills);

		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(UNKNOWN).withMarking(OTHER_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateNoMarking() {
		markings.add(builder.withSkill(SNEAKY_GIT).withMarking(BLOCK_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertTrue(marking.isEmpty());
	}

	@Test
	public void generateOnlyForPresentSkills() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(SNEAKY_GIT).withMarking(DODGE_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateMarkingsForOverlappingConfigs() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withSkill(TACKLE).withMarking(BLACKLE_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLODGE_MARKING + BLACKLE_MARKING, marking);
	}

	@Test
	public void ignoreCombinedConfigsForGainedSkillsWithOnlyPartialMatch() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withSkill(TACKLE).withMarking(BLACKLE_MARKING).withGainedOnly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLODGE_MARKING, marking);
	}

	@Test
	public void ignoreSubsets() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLODGE_MARKING, marking);
	}

	@Test
	public void ignoreSubsetUnlessApplyToMakesDifference() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(BLODGE_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.BOTH).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(BLOCK_MARKING + DODGE_MARKING, marking);
	}

	@Test
	public void ignoreSubsetUnlessGainedOnlyMakesDifference() {
		markings.add(builder.withSkill(WRESTLE).withSkill(TACKLE).withMarking(WRECKLE_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(WRESTLE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigs() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING + DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponent() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(BLOCK_MARKING + DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithMatchingApplyTo() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndMatchingApplyTo() {
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithGainedOnly() {
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withGainedOnly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndGainedOnly() {
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withGainedOnly(true).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithMatchingGainedAndApplyTo() {
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());
		markings.add(builder.withSkill(TACKLE).withMarking(TACKLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OPPONENT).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndMatchingGainedAndApplyTo() {
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill(DODGE).withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());
		markings.add(builder.withSkill(TACKLE).withMarking(TACKLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OPPONENT).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void generateForSingleInjuryMarkings() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING + MA_MARKING, marking);
	}

	@Test
	public void ignoreGainedOnlyOnInjuryMarkings() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withMarking(AG_MARKING).withGainedOnly(true).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).withGainedOnly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING + MA_MARKING, marking);
	}

	@Test
	public void generateForMultiInjuryMarkings() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withInjury(InjuryAttribute.AG).withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void generateForSingleInjuryMarkingsOnlyForOwnPlayer() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withApplyTo(ApplyTo.OWN).withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withApplyTo(ApplyTo.OPPONENT).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING, marking);
	}

	@Test
	public void generateForSingleInjuryMarkingsOnlyForOpponent() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withApplyTo(ApplyTo.OWN).withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withApplyTo(ApplyTo.OPPONENT).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, false);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void generateForCombinedSkillAndInjuryMarkings() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.AV).withSkill(DODGE).withMarking(DODGE_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.AG).withSkill(SNEAKY_GIT).withMarking(AG_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void ignoreInjuryOnlyMarkingsIfTheyAreASubset() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withSkill(BLOCK).withMarking(BLOCK_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void ignoreCombinedSkillAndInjuryMarkingsIfGainedOnlyDoesNotMatch() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withSkill(WRESTLE).withGainedOnly(true).withMarking(WRESTLE_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void generateSingleMarkingForMultiStatIncreases() {
		Set<Skill> increases = new HashSet<Skill>() {{
			add(skillFactory.forName("+AG", SkillCategory.STAT_INCREASE));
			add(skillFactory.forName("+AG", SkillCategory.STAT_INCREASE));
		}};
		given(player.getSkillsIncludingTemporaryOnes()).willReturn(increases);
		given(player.getAgilityWithModifiers(game)).willReturn(AGILITY - 2);
		markings.add(builder.withSkill("+AG").withMarking(AG_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING, marking);
	}

	@Test
	public void generateMarkingMatchingForMultiStatIncreases() {
		Set<Skill> increases = new HashSet<Skill>() {{
			add(skillFactory.forName("+AG", SkillCategory.STAT_INCREASE));
			add(skillFactory.forName("+AG", SkillCategory.STAT_INCREASE));
		}};
		given(player.getSkillsIncludingTemporaryOnes()).willReturn(increases);
		given(player.getAgilityWithModifiers(game)).willReturn(AGILITY - 2);
		markings.add(builder.withSkill("+AG").withSkill("+AG").withMarking(AG_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING, marking);
	}

	@Test
	public void ignoreMatchingForMultiStatIncreasesIfOnlyOneIsPresent() {
		Set<Skill> increases = new HashSet<Skill>() {{
			add(skillFactory.forName("+AG", SkillCategory.STAT_INCREASE));
		}};
		given(player.getSkillsIncludingTemporaryOnes()).willReturn(increases);
		given(player.getAgilityWithModifiers(game)).willReturn(AGILITY - 1);
		markings.add(builder.withSkill("+AG").withSkill("+AG").withMarking(AG_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertTrue(marking.isEmpty());
	}

	@Test
	public void generateOnlyForNetStatIncreases() {
		Skill[] increases = new Skill[]{skillFactory.forName("+AG", SkillCategory.STAT_INCREASE), skillFactory.forName("+AG", SkillCategory.STAT_INCREASE)};
		given(player.getSkills()).willReturn(increases);
		markings.add(builder.withSkill("+AG").withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.AG).withMarking(AG_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING, marking);
	}

	@Test
	public void generateOnlyForNetInjuries() {
		Skill[] increases = new Skill[]{skillFactory.forName("+MA", SkillCategory.STAT_INCREASE)};
		given(player.getSkills()).willReturn(increases);
		markings.add(builder.withSkill("+MA").withMarking(MA_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void generateSingleMarkingForMultiInjuries() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void ignoreStatInjuries() {
		markings.add(builder.withInjury(InjuryAttribute.AV).withMarking("Some marking").build());

		String marking = generator.generate(game, player, config, true);

		assertTrue(marking.isEmpty());
	}

	@Test
	public void generateNigglingMarker() {
		markings.add(builder.withInjury(InjuryAttribute.NI).withMarking(NI_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(NI_MARKING, marking);
	}

	@Test
	public void generateCombinedInjuryMarkerWhenPlayerWasHurtDuringTheGame() {
		markings.add(builder.withInjury(InjuryAttribute.NI).withMarking(NI_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.AG).withMarking(AG_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());


		given(playerResult.getSeriousInjury()).willReturn(SeriousInjury.NECK_INJURY);
		given(playerResult.getSeriousInjuryDecay()).willReturn(SeriousInjury.SMASHED_KNEE);

		String marking = generator.generate(game, player, config, true);

		assertEquals(AG_MARKING + MA_MARKING + NI_MARKING, marking);
	}

	@Test
	public void generateMarkingMatchingForMultiInjuries() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void ignoreMatchingForMultiInjuriesIfOnlyOneIsPresent() {
		markings.add(builder.withInjury(InjuryAttribute.AG).withInjury(InjuryAttribute.AG).withMarking(MA_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertTrue(marking.isEmpty());
	}

	@Test
	public void sortInjuriesLastAndAlphabeticallyOtherwise() {
		markings.add(builder.withSkill(BLOCK).withSkill(DODGE).withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill(WRESTLE).withMarking(WRESTLE_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.AG).withSkill(TACKLE).withMarking(OTHER_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(OTHER_MARKING + WRESTLE_MARKING + BLODGE_MARKING + MA_MARKING, marking);
	}

	@Test
	public void ignoreIdenticalMarkingWithGainedOnly() {
		markings.add(builder.withSkill(BLOCK).withGainedOnly(true).withMarking(OTHER_MARKING).build());
		markings.add(builder.withSkill(BLOCK).withMarking(BLOCK_MARKING).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void ignoreIdenticalMarkingWithNoRepetition() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(OTHER_MARKING).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).withApplyRepeatedly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING + MA_MARKING, marking);
	}

	@Test
	public void generateRepeatedMarking() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).withApplyRepeatedly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING + MA_MARKING, marking);
	}

	@Test
	public void generateMultiInjuryMarkingOverRepeated() {
		markings.add(builder.withInjury(InjuryAttribute.MA).withMarking(OTHER_MARKING).withApplyRepeatedly(true).build());
		markings.add(builder.withInjury(InjuryAttribute.MA).withInjury(InjuryAttribute.MA).withMarking(MA_MARKING).withApplyRepeatedly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(MA_MARKING, marking);
	}

	@Test
	public void generateRepeatedMarkingOnlyOnceIfNotCompletelyApplicable() {
		markings.add(builder.withSkill(BLOCK).withInjury(InjuryAttribute.MA).withMarking(BLOCK_MARKING).withApplyRepeatedly(true).build());

		String marking = generator.generate(game, player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	private static class MockSkillFactory extends SkillFactory {

		private final Map<String, Skill> skills = new HashMap<>();

		@Override
		public Skill forName(String name) {
			return forName(name, null);
		}

		public Skill forName(String name, SkillCategory skillCategory) {
			if (UNKNOWN.equalsIgnoreCase(name)) {
				return null;
			}
			name = name.toLowerCase();
			if (!skills.containsKey(name) || (skillCategory != null && skills.get(name).getCategory() != skillCategory)) {
				Skill skill = mock(Skill.class);
				given(skill.getName()).willReturn(name);
				if (skillCategory != null) {
					given(skill.getCategory()).willReturn(skillCategory);
				}
				skills.put(name, skill);
			}

			return skills.get(name);
		}
	}
}