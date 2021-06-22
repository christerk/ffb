package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.bb2020.ReportPlayerEvent;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerDialog;

import java.util.List;

public abstract class SelectPlayerPrayerHandler extends DialogPrayerHandler {

	protected abstract PlayerChoiceMode choiceMode();

	@Override
	protected void createDialog(List<Player<?>> players, GameState gameState, Team prayingTeam) {
		String[] playerIds = players.stream().map(Player::getId).toArray(String[]::new);
		UtilServerDialog.showDialog(gameState, new DialogPlayerChoiceParameter(prayingTeam.getId(), choiceMode(), playerIds,
			null, 1, 1), false);
	}

	@Override
	public void applySelection(Game game, PrayerDialogSelection selection) {
		Player<?> player = game.getPlayerById(selection.getPlayerId());
		game.getFieldModel().addPrayerEnhancements(player, handledPrayer());
		reports.add(new ReportPlayerEvent(player.getId(), handledPrayer().eventMessage()));
	}
}
