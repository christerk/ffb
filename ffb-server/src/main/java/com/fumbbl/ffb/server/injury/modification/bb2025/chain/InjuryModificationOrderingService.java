package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.Comparator;

/**
 * Orders candidates by usage frequency (more frequently usable first), restriction (most restricted first) and
 * skill name.
 */
public class InjuryModificationOrderingService {

	public Comparator<InjuryModificationCandidate> comparator() {
		return Comparator.<InjuryModificationCandidate>comparingInt(candidate -> usageRank(candidate.getSkill()))
			.thenComparingInt(candidate -> candidate.getModification().restriction().type().ordinal())
			.thenComparing(candidate -> skillName(candidate.getSkill()));
	}

	int usageRank(Skill skill) {
		SkillUsageType usageType = skill != null ? skill.getSkillUsageType() : null;
		if (usageType == null) {
			return Integer.MAX_VALUE;
		}
		switch (usageType) {
			case REGULAR:
				return 0;
			case ONCE_PER_TURN:
			case ONCE_PER_TURN_BY_TEAM_MATE:
			case SPECIAL:
				return 1;
			case ONCE_PER_DRIVE:
				return 2;
			case ONCE_PER_HALF:
				return 3;
			case ONCE_PER_GAME:
				return 4;
			default:
				return Integer.MAX_VALUE;
		}
	}

	private String skillName(Skill skill) {
		return skill != null && skill.getName() != null ? skill.getName() : "";
	}
}
