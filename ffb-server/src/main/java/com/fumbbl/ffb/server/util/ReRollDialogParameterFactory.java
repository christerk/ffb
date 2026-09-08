package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollOptions;

/**
 * Builds the dialog parameter a re-roll request ends up showing, so that callers can ask for a dialog other than
 * the default one of the ruleset.
 */
@FunctionalInterface
public interface ReRollDialogParameterFactory {

	/**
	 * @param request      the request with player and re-roll skill resolved
	 * @param reRollOptions the re-roll options the ruleset found for the request
	 * @return the parameter of the dialog to show or {@code null} when there is nothing to ask the coach
	 */
	IDialogParameter create(ReRollRequest request, ReRollOptions reRollOptions);
}
