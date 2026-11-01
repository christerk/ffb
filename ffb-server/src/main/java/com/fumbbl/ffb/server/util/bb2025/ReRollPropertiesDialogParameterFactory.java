package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.ReRollOptions;
import com.fumbbl.ffb.dialog.DialogReRollPropertiesParameter;
import com.fumbbl.ffb.server.util.ReRollDialogParameterFactory;
import com.fumbbl.ffb.server.util.ReRollRequest;

/**
 * Builds the standard re-roll dialog of the 2025 ruleset.
 */
public class ReRollPropertiesDialogParameterFactory implements ReRollDialogParameterFactory {

	@Override
	public IDialogParameter create(ReRollRequest request, ReRollOptions reRollOptions) {
		if (!reRollOptions.canActuallyReRoll() && request.getModifyingSkill() == null) {
			return null;
		}
		return new DialogReRollPropertiesParameter(request.getPlayer().getId(), request.getReRolledAction(),
			request.getMinimumRoll(), reRollOptions.getProperties(), request.isFumble(), reRollOptions.getReRollSkill(),
			request.getModifyingSkill(), request.getMenuProperty(), request.getDefaultValueKey(), request.getMessages());
	}
}
