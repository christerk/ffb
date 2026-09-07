package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adjusts the re-roll options that are offered together with optional modifiers.
 */
public class ReRollOptionsService {

	private static final List<ReRollProperty> TEAM_RE_ROLL_PROPERTIES =
		Arrays.asList(ReRollProperty.TRR, ReRollProperty.MASCOT, ReRollProperty.BRILLIANT_COACHING,
			ReRollProperty.PUMP_UP_THE_CROWD, ReRollProperty.SHOW_STAR, ReRollProperty.LONER);

	/**
	 * A re-roll skill that is available in every turn costs the team nothing, so the coach must not be tempted to
	 * spend a team re-roll instead. In that case all options feeding the team re-roll button are removed.
	 */
	public ReRollOptions withoutTeamReRollForSkillAvailableEveryTurn(ReRollOptions reRollOptions) {
		Skill reRollSkill = reRollOptions.getReRollSkill();
		if (reRollSkill == null || !isAvailableEveryTurn(reRollSkill)) {
			return reRollOptions;
		}

		List<ReRollProperty> properties = reRollOptions.getProperties().stream()
			.filter(property -> !TEAM_RE_ROLL_PROPERTIES.contains(property)).collect(Collectors.toList());

		return new ReRollOptions(properties, reRollSkill);
	}

	private boolean isAvailableEveryTurn(Skill skill) {
		SkillUsageType usageType = skill.getSkillUsageType();
		return usageType == SkillUsageType.REGULAR || usageType == SkillUsageType.ONCE_PER_TURN;
	}
}
