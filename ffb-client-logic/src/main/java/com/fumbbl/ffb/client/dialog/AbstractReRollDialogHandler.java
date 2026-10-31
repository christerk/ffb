package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.HasReRollProperties;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSourceComposer;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import java.util.List;
import java.util.function.Consumer;

/**
 * Base for handlers of dialogs offering team re-rolls. If Pro or the mascot is selected and further team re-rolls
 * are available, follow-up dialogs ask which re-roll to use if Pro or the mascot fail. The final selection is
 * combined into a single re-roll source.
 */
public abstract class AbstractReRollDialogHandler extends DialogHandler {

	private final ReRollSourceComposer reRollSourceComposer = new ReRollSourceComposer();
	private final DialogExtensionMascot extension = new DialogExtensionMascot();
	private PendingSelection pendingSelection;

	public AbstractReRollDialogHandler(FantasyFootballClient client) {
		super(client);
	}

	/**
	 * @param selection the source selected in the dialog: Pro, Team Mascot, Leader, a single use re-roll or a team
	 *                  re-roll
	 * @param sender    sends the final, possibly combined, re-roll source
	 */
	protected void selectTeamReRoll(HasReRollProperties properties, ReRollSource selection,
		Consumer<ReRollSource> sender) {
		boolean pro = selection == ReRollSources.PRO;
		boolean mascot = selection == ReRollSources.MASCOT;
		select(new PendingSelection(properties, pro, mascot, sender), pro || mascot ? null : selection);
	}

	private void select(PendingSelection selection, ReRollSource teamReRollSource) {
		if (teamReRollSource == null && (selection.pro || selection.mascot)) {
			List<ReRollSource> fallbacks =
				extension.teamReRollSources(selection.properties, selection.pro && !selection.mascot);
			if (!fallbacks.isEmpty()) {
				pendingSelection = selection;
				ReRollSource fallbackFor = selection.mascot ? ReRollSources.MASCOT : ReRollSources.PRO;
				setDialog(new DialogReRollFallback(getClient(), fallbackFor, fallbacks));
				getDialog().showDialog(this);
				return;
			}
		}
		send(selection, teamReRollSource);
	}

	private void send(PendingSelection selection, ReRollSource teamReRollSource) {
		selection.sender.accept(reRollSourceComposer.compose(selection.pro, selection.mascot, teamReRollSource));
	}

	@Override
	public void dialogClosed(IDialog dialog) {
		if (testDialogHasId(dialog, DialogId.RE_ROLL_FALLBACK)) {
			hideDialog();
			PendingSelection selection = pendingSelection;
			pendingSelection = null;
			if (selection == null) {
				return;
			}
			ReRollSource fallback = ((DialogReRollFallback) dialog).getReRollSource();
			if (fallback == ReRollSources.MASCOT) {
				select(new PendingSelection(selection.properties, selection.pro, true, selection.sender), null);
			} else {
				send(selection, fallback);
			}
		} else {
			reRollDialogClosed(dialog);
		}
	}

	/**
	 * Handles closing of the dialog shown by this handler (other than the follow-up dialogs).
	 */
	protected abstract void reRollDialogClosed(IDialog dialog);

	private static class PendingSelection {
		private final HasReRollProperties properties;
		private final boolean pro;
		private final boolean mascot;
		private final Consumer<ReRollSource> sender;

		private PendingSelection(HasReRollProperties properties, boolean pro, boolean mascot,
			Consumer<ReRollSource> sender) {
			this.properties = properties;
			this.pro = pro;
			this.mascot = mascot;
			this.sender = sender;
		}
	}
}
