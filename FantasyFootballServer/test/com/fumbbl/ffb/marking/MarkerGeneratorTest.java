package com.fumbbl.ffb.marking;

import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.skill.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MarkerGeneratorTest {

	private static final String BLOCK_MARKING = "B";
	private static final String BLODGE_MARKING = "X";
	private static final String DODGE_MARKING = "D";
	private static final String BLACKLE_MARKING = "Y";
	private static final String TACKLE_MARKING = "T";
	private static final String WRESTLE_MARKING = "W";

	private final MarkerGenerator generator = new MarkerGenerator();
	private AutoMarkingConfig config;
	private AutoMarkingRecord.Builder builder;
	private SkillFactory skillFactory;
	@Mock
	private Player<Position> player;
	@Mock
	private Position position;

	@BeforeEach
	public void setUp() {
		skillFactory = new MockSkillFactory();
		config = new AutoMarkingConfig();
		builder = new AutoMarkingRecord.Builder(skillFactory);
	}


	@Test
	public void generateMarkingsForOverlappingConfigs() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withSkill("dodge").withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill("block").withSkill("tackle").withMarking(BLACKLE_MARKING).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge"), skillFactory.forName("tackle")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLACKLE_MARKING + BLODGE_MARKING, marking);
	}

	@Test
	public void ignoreSubsets() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withSkill("dodge").withMarking(BLODGE_MARKING).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigs() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING + DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponent() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, false);

		assertEquals(BLOCK_MARKING + DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithMatchingApplyTo() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndMatchingApplyTo() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithGainedOnly() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndGainedOnly() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}

	@Test
	public void ignoreMatchingConfigsWithGainedOnlyForBaseSkills() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getPosition()).willReturn(position);
		given(position.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertTrue(marking.isEmpty());
	}

	@Test
	public void ignoreMatchingConfigsWithOpponentAndGainedOnlyForBaseSkills() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withGainedOnly(true).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getPosition()).willReturn(position);
		given(position.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, false);

		assertTrue(marking.isEmpty());
	}


	@Test
	public void generateForAllMatchingConfigsWithMatchingGainedAndApplyTo() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("wrestle").withMarking(WRESTLE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());
		markings.add(builder.withSkill("tackle").withMarking(TACKLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OPPONENT).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge"),
			skillFactory.forName("tackle"), skillFactory.forName("wrestle")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateForAllMatchingConfigsWithOpponentAndMatchingGainedAndApplyTo() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OWN).build());
		markings.add(builder.withSkill("wrestle").withMarking(WRESTLE_MARKING).withApplyTo(ApplyTo.OPPONENT).build());
		markings.add(builder.withSkill("tackle").withMarking(TACKLE_MARKING).withGainedOnly(true).withApplyTo(ApplyTo.OPPONENT).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge"),
			skillFactory.forName("tackle"), skillFactory.forName("wrestle")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, false);

		assertEquals(DODGE_MARKING, marking);
	}


	@Test
	public void generateOnlyForMatchingConfig() {
		Set<AutoMarkingRecord> markings = config.getMarkings();
		markings.add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());
		markings.add(builder.withSkill("dodge").withMarking(DODGE_MARKING).build());

		Skill[] skills = {skillFactory.forName("block")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateOnlyDefinedSkill() {
		config.getMarkings().add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());

		Skill[] skills = {skillFactory.forName("block"), skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generate() {
		config.getMarkings().add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());

		Skill[] skills = {skillFactory.forName("block")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertEquals(BLOCK_MARKING, marking);
	}

	@Test
	public void generateNoMarking() {
		config.getMarkings().add(builder.withSkill("block").withMarking(BLOCK_MARKING).build());

		Skill[] skills = {skillFactory.forName("dodge")};
		given(player.getSkills()).willReturn(skills);

		String marking = generator.generate(player, config, true);

		assertTrue(marking.isEmpty());
	}

	private static class MockSkillFactory extends SkillFactory {

		private final Map<String, Skill> skills = new HashMap<>();

		@Override
		public Skill forName(String name) {
			name = name.toLowerCase();
			if (!skills.containsKey(name)) {
				Skill skill = mock(Skill.class);
//				given(skill.getName()).willReturn(name);
				skills.put(name, skill);
			}

			return skills.get(name);
		}
	}
}