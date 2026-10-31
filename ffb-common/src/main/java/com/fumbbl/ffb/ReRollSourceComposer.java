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

	private static final Map<ReRollProperty, ReRollSource> SINGLE_USE_SOURCES;

	private static final Map<ReRollSource, ReRollSourceComposition> COMPOSITIONS;

	static {
		Map<ReRollProperty, ReRollSource> singleUseSources = new LinkedHashMap<>();
		singleUseSources.put(ReRollProperty.BRILLIANT_COACHING, ReRollSources.BRILLIANT_COACHING);
		singleUseSources.put(ReRollProperty.PUMP_UP_THE_CROWD, ReRollSources.PUMP_UP_THE_CROWD);
		singleUseSources.put(ReRollProperty.SHOW_STAR, ReRollSources.SHOW_STAR);
		SINGLE_USE_SOURCES = Collections.unmodifiableMap(singleUseSources);

		Map<ReRollSource, ReRollSourceComposition> compositions = new LinkedHashMap<>();
		compositions.put(ReRollSources.TEAM_RE_ROLL, new ReRollSourceComposition(false, false, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.BRILLIANT_COACHING,
			new ReRollSourceComposition(false, false, ReRollSources.BRILLIANT_COACHING));
		compositions.put(ReRollSources.PUMP_UP_THE_CROWD,
			new ReRollSourceComposition(false, false, ReRollSources.PUMP_UP_THE_CROWD));
		compositions.put(ReRollSources.SHOW_STAR, new ReRollSourceComposition(false, false, ReRollSources.SHOW_STAR));

		compositions.put(ReRollSources.MASCOT, new ReRollSourceComposition(false, true, null));
		compositions.put(ReRollSources.MASCOT_TRR, new ReRollSourceComposition(false, true, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.MASCOT_BRILLIANT_COACHING,
			new ReRollSourceComposition(false, true, ReRollSources.BRILLIANT_COACHING));
		compositions.put(ReRollSources.MASCOT_PUMP_UP_THE_CROWD,
			new ReRollSourceComposition(false, true, ReRollSources.PUMP_UP_THE_CROWD));
		compositions.put(ReRollSources.MASCOT_SHOW_STAR,
			new ReRollSourceComposition(false, true, ReRollSources.SHOW_STAR));

		compositions.put(ReRollSources.PRO, new ReRollSourceComposition(true, false, null));
		compositions.put(ReRollSources.PRO_TRR, new ReRollSourceComposition(true, false, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.PRO_BRILLIANT_COACHING,
			new ReRollSourceComposition(true, false, ReRollSources.BRILLIANT_COACHING));
		compositions.put(ReRollSources.PRO_PUMP_UP_THE_CROWD,
			new ReRollSourceComposition(true, false, ReRollSources.PUMP_UP_THE_CROWD));
		compositions.put(ReRollSources.PRO_SHOW_STAR, new ReRollSourceComposition(true, false, ReRollSources.SHOW_STAR));

		compositions.put(ReRollSources.PRO_MASCOT, new ReRollSourceComposition(true, true, null));
		compositions.put(ReRollSources.PRO_MASCOT_TRR,
			new ReRollSourceComposition(true, true, ReRollSources.TEAM_RE_ROLL));
		compositions.put(ReRollSources.PRO_MASCOT_BRILLIANT_COACHING,
			new ReRollSourceComposition(true, true, ReRollSources.BRILLIANT_COACHING));
		compositions.put(ReRollSources.PRO_MASCOT_PUMP_UP_THE_CROWD,
			new ReRollSourceComposition(true, true, ReRollSources.PUMP_UP_THE_CROWD));
		compositions.put(ReRollSources.PRO_MASCOT_SHOW_STAR,
			new ReRollSourceComposition(true, true, ReRollSources.SHOW_STAR));
		COMPOSITIONS = Collections.unmodifiableMap(compositions);
	}

	/**
	 * @param teamReRollSource null, {@link ReRollSources#TEAM_RE_ROLL}, {@link ReRollSources#LEADER} or a single use
	 *                         re-roll source
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
	 * re-rolls, Leader and regular team re-rolls. Leader and single use re-rolls replace the regular team re-roll option.
	 */
	public List<ReRollSource> teamReRollSources(HasReRollProperties properties, boolean includeMascot) {
		List<ReRollSource> sources = new ArrayList<>();
		if (includeMascot && properties.hasProperty(ReRollProperty.MASCOT)) {
			sources.add(ReRollSources.MASCOT);
		}
		if (properties.hasProperty(ReRollProperty.TRR)) {
			SINGLE_USE_SOURCES.forEach((property, source) -> {
				if (properties.hasProperty(property)) {
					sources.add(source);
				}
			});
			if (properties.hasProperty(ReRollProperty.LEADER)) {
				sources.add(ReRollSources.LEADER);
			}
			if (SINGLE_USE_SOURCES.keySet().stream().noneMatch(properties::hasProperty)
				&& !properties.hasProperty(ReRollProperty.LEADER)) {
				sources.add(ReRollSources.TEAM_RE_ROLL);
			}
		}
		return sources;
	}

	/**
	 * @return the single use team re-roll sources mapped by the property signalling their availability
	 */
	public Map<ReRollProperty, ReRollSource> singleUseSources() {
		return SINGLE_USE_SOURCES;
	}
}
