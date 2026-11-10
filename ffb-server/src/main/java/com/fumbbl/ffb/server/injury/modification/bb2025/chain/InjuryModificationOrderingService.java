package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

import java.util.Comparator;
import java.util.List;

/**
 * Rule 4 ordering: usage (most frequently available first), then restriction (most restricted first), then name.
 */
public class InjuryModificationOrderingService {

	public Comparator<InjuryModificationCandidate> candidateComparator() {
		return Comparator.<InjuryModificationCandidate>comparingInt(candidate -> usageRank(candidate.getSkill()))
			.thenComparingInt(candidate -> candidate.getModification().restriction().ordinal())
			.thenComparing(candidate -> candidate.getSkill().getName());
	}

	/**
	 * Options by number of skills, then skill by skill (skills are already in candidate order), then label.
	 */
	public Comparator<List<InjuryModificationCandidate>> combinationComparator() {
		Comparator<InjuryModificationCandidate> candidateComparator = candidateComparator();
		return (first, second) -> {
			int result = Integer.compare(first.size(), second.size());
			for (int i = 0; result == 0 && i < first.size(); i++) {
				result = candidateComparator.compare(first.get(i), second.get(i));
			}
			if (result == 0) {
				result = label(first).compareTo(label(second));
			}
			return result;
		};
	}

	public int usageRank(Skill skill) {
		SkillUsageType usageType = skill.getSkillUsageType();
		if (usageType == null) {
			return 0;
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
			default:
				return 4;
		}
	}

	private String label(List<InjuryModificationCandidate> candidates) {
		StringBuilder builder = new StringBuilder();
		candidates.forEach(candidate -> builder.append(candidate.getSkill().getName()).append('+'));
		return builder.toString();
	}
}
