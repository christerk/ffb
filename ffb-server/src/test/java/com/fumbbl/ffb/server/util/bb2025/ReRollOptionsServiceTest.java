package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.skill.bb2025.Dodge;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReRollOptionsServiceTest {

	private final ReRollOptionsService service = new ReRollOptionsService();

	@Test
	void keepsOptionsWithoutReRollSkill() {
		ReRollOptions options =
			new ReRollOptions(Arrays.asList(ReRollProperty.TRR, ReRollProperty.PRO), null);

		assertSame(options, service.withoutTeamReRollForSkillAvailableEveryTurn(options));
	}

	@Test
	void keepsOptionsForSkillThatIsNotAvailableEveryTurn() {
		ReRollOptions options =
			new ReRollOptions(Arrays.asList(ReRollProperty.TRR, ReRollProperty.PRO), new ConsummateProfessional());

		assertSame(options, service.withoutTeamReRollForSkillAvailableEveryTurn(options));
	}

	@Test
	void removesTeamReRollForSkillThatIsAvailableEveryTurn() {
		Dodge dodge = new Dodge();
		ReRollOptions options = new ReRollOptions(
			Arrays.asList(ReRollProperty.TRR, ReRollProperty.MASCOT, ReRollProperty.BRILLIANT_COACHING,
				ReRollProperty.PUMP_UP_THE_CROWD, ReRollProperty.SHOW_STAR, ReRollProperty.LONER, ReRollProperty.PRO),
			dodge);

		ReRollOptions reduced = service.withoutTeamReRollForSkillAvailableEveryTurn(options);

		assertEquals(1, reduced.getProperties().size());
		assertTrue(reduced.hasProperty(ReRollProperty.PRO));
		assertSame(dodge, reduced.getReRollSkill());
		assertTrue(reduced.canActuallyReRoll());
	}
}
