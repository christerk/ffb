package com.fumbbl.ffb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps between the re-roll sources sent by the team re-roll dialogs and their parts (Pro, mascot and team re-roll)
 * and determines which team re-roll options a coach has to choose from.
 */
public class ReRollSourceComposer {

	private static final Map<ReRollSource, ReRollSourceComposition> COMPOSITIONS;

	static {
		Map<ReRollSource, ReRollSourceComposition> compositions = new LinkedHashMap<>();
		compositions.put(ReRollSources.TEAM_RE_ROLL, new ReRollSourceComposition(false, false, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.SINGLE_USE_RE_ROLL,
			new ReRollSourceComposition(false, false, ReRollSources.SINGLE_USE_RE_ROLL));

		compositions.put(ReRollSources.MASCOT, new ReRollSourceComposition(false, true, null));
		compositions.put(ReRollSources.MASCOT_TRR, new ReRollSourceComposition(false, true, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.MASCOT_SINGLE_USE,
			new ReRollSourceComposition(false, true, ReRollSources.SINGLE_USE_RE_ROLL));

		compositions.put(ReRollSources.PRO, new ReRollSourceComposition(true, false, null));
		compositions.put(ReRollSources.PRO_TRR, new ReRollSourceComposition(true, false, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.PRO_SINGLE_USE,
			new ReRollSourceComposition(true, false, ReRollSources.SINGLE_USE_RE_ROLL));

		compositions.put(ReRollSources.PRO_MASCOT, new ReRollSourceComposition(true, true, null));
		compositions.put(ReRollSources.PRO_MASCOT_TRR,
			new ReRollSourceComposition(true, true, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.PRO_MASCOT_SINGLE_USE,
			new ReRollSourceComposition(true, true, ReRollSources.SINGLE_USE_RE_ROLL));
		COMPOSITIONS = Collections.unmodifiableMap(compositions);
	}

	/**
	 * @param teamReRollSource null, {@link ReRollSources#TEAM_RE_ROLL}, {@link ReRollSources#LEADER} or
	 *                         {@link ReRollSources#SINGLE_USE_RE_ROLL}
	 * @return the source to send to the server or null if the parts do not form a re-roll
	 */
	public ReRollSource compose(boolean pro, boolean mascot, ReRollSource teamReRollSource) {
		ReRollSource normalizedTeamSource =
			teamReRollSource == ReRollSources.LEADER ? ReRollSources.TEAM_RE_ROLL : teamReRollSource;
		return COMPOSITIONS.entrySet().stream()
			.filter(entry -> entry.getValue().matches(pro, mascot, normalizedTeamSource))
			.map(Map.Entry::getKey)
			.findFirst().orElse(null);
	}

	/**
	 * @return the parts of the given source or null if it is not composed of Pro, mascot or team re-rolls
	 */
	public ReRollSourceComposition decompose(ReRollSource reRollSource) {
		return COMPOSITIONS.get(reRollSource);
	}

	/**
	 * @return the team re-roll options a coach can choose from, in the order they are offered: Mascot, single use
	 * re-roll, Leader and regular team re-roll. Leader and single use re-rolls replace the regular team re-roll option.
	 */
	public List<ReRollSource> teamReRollSources(HasReRollProperties properties, boolean includeMascot) {
		List<ReRollSource> sources = new ArrayList<>();
		if (includeMascot && properties.hasProperty(ReRollProperty.MASCOT)) {
			sources.add(ReRollSources.MASCOT);
		}
		if (properties.hasProperty(ReRollProperty.TRR)) {
			boolean singleUse = properties.hasProperty(ReRollProperty.SINGLE_USE);
			boolean leader = properties.hasProperty(ReRollProperty.LEADER);
			if (singleUse) {
				sources.add(ReRollSources.SINGLE_USE_RE_ROLL);
			}
			if (leader) {
				sources.add(ReRollSources.LEADER);
			}
			if (!singleUse && !leader) {
				sources.add(ReRollSources.TEAM_RE_ROLL);
			}
		}
		return sources;
	}
}
