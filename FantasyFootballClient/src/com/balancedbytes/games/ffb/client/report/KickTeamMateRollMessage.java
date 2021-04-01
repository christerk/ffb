package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportKickTeamMateRoll;

@ReportMessageType(ReportId.KICK_TEAM_MATE_ROLL)
@RulesCollection(Rules.COMMON)
public class KickTeamMateRollMessage extends ReportMessageBase<ReportKickTeamMateRoll> {

    public KickTeamMateRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportKickTeamMateRoll report) {
  		StringBuilder status = new StringBuilder();
  		Player<?> kicker = game.getActingPlayer().getPlayer();
  		Player<?> kickedPlayer = game.getPlayerById(report.getKickedPlayerId());
  		if (!report.isReRolled()) {
  			print(getIndent(), true, kicker);
  			print(getIndent(), TextStyle.BOLD, " tries to kick ");
  			print(getIndent(), true, kickedPlayer);
  			println(getIndent(), TextStyle.BOLD, ":");
  		}

  		int[] roll = report.getRoll();
  		if (roll.length > 1) {
  			status.append("Kick Team-Mate Roll [ ").append(roll[0]).append(" ][ ").append(roll[1]).append(" ]");
  		} else {
  			status.append("Kick Team-Mate Roll [ ").append(roll[0]).append(" ]");
  		}
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());

  		print(getIndent() + 2, false, kicker);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" kicks ").append(kicker.getPlayerGender().getGenitive()).append(" team-mate successfully.");
  			println(getIndent() + 2, status.toString());
  		} else {
  			println(getIndent() + 2, " is a bit too enthusiastic.");
  		}
    }
}
