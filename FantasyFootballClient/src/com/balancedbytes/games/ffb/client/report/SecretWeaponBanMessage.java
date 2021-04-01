package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSecretWeaponBan;
import com.balancedbytes.games.ffb.util.ArrayTool;

@ReportMessageType(ReportId.SECRET_WEAPON_BAN)
@RulesCollection(Rules.COMMON)
public class SecretWeaponBanMessage extends ReportMessageBase<ReportSecretWeaponBan> {

    public SecretWeaponBanMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSecretWeaponBan report) {
  		reportSecretWeaponBan(report, game.getTeamHome());
  		reportSecretWeaponBan(report, game.getTeamAway());
    }
    
  	private void reportSecretWeaponBan(ReportSecretWeaponBan pReport, Team pTeam) {
  		String[] playerIds = pReport.getPlayerIds();
  		if (ArrayTool.isProvided(playerIds)) {
  			int[] rolls = pReport.getRolls();
  			boolean[] banned = pReport.getBans();
  			for (int i = 0; i < playerIds.length; i++) {
  				Player<?> player = game.getPlayerById(playerIds[i]);
  				if (pTeam.hasPlayer(player)) {
  					if (banned[i]) {
  						print(getIndent(), "The ref bans ");
  						print(getIndent(), false, player);
  						println(getIndent(), " for using a Secret Weapon.");
  					} else {
  						print(getIndent(), "The ref overlooks ");
  						print(getIndent(), false, player);
  						println(getIndent(), " using a Secret Weapon.");
  					}
  					int secretWeaponValue = player.getSkillIntValue(player.getSkillWithProperty(NamedProperties.getsSentOffAtEndOfDrive));
  					if (rolls[i] > 0) {
  						String penalty = "Penalty roll was " + rolls[i] +
  							", banned on a " + secretWeaponValue + "+";
  						println(getIndent() + 1, TextStyle.NEEDED_ROLL, penalty);
  					}
  				}
  			}
  		}
  	}
}
