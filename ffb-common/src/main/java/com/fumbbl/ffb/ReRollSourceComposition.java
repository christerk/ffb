package com.fumbbl.ffb;

/**
 * The parts a re-roll source offered by the team re-roll dialogs consists of: an optional Pro roll, an optional
 * mascot roll and an optional team re-roll used as last resort.
 */
public class ReRollSourceComposition {

	private final boolean pro;
	private final boolean mascot;
	private final ReRollSource teamReRollSource;

	public ReRollSourceComposition(boolean pro, boolean mascot, ReRollSource teamReRollSource) {
		this.pro = pro;
		this.mascot = mascot;
		this.teamReRollSource = teamReRollSource;
	}

	public boolean isPro() {
		return pro;
	}

	public boolean isMascot() {
		return mascot;
	}

	/**
	 * @return {@link ReRollSources#TEAM_RE_ROLL} (Leader is used first if available),
	 * {@link ReRollSources#SINGLE_USE_RE_ROLL} or null if no team re-roll is part of this composition
	 */
	public ReRollSource getTeamReRollSource() {
		return teamReRollSource;
	}

	boolean matches(boolean pro, boolean mascot, ReRollSource teamReRollSource) {
		return this.pro == pro && this.mascot == mascot && this.teamReRollSource == teamReRollSource;
	}
}
