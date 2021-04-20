package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportKickoffPitchInvasion;
import com.fumbbl.ffb.util.UtilPlayer;

@ReportMessageType(ReportId.KICKOFF_PITCH_INVASION)
@RulesCollection(Rules.COMMON)
public class KickoffPitchInvasionMessage extends ReportMessageBase<ReportKickoffPitchInvasion> {

    @Override
    protected void render(ReportKickoffPitchInvasion report) {
  		GameResult gameResult = game.getGameResult();
  		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
  			NamedProperties.increasesTeamsFame).length;
  		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
  			NamedProperties.increasesTeamsFame).length;
  		int[] rollsHome = report.getRollsHome();
  		boolean[] playersAffectedHome = report.getPlayersAffectedHome();
  		Player<?>[] homePlayers = game.getTeamHome().getPlayers();
  		for (int i = 0; i < homePlayers.length; i++) {
  			if (rollsHome[i] > 0) {
  				StringBuilder status = new StringBuilder();
  				status.append("Pitch Invasion Roll [ ").append(rollsHome[i]).append(" ]");
  				println(getIndent(), TextStyle.ROLL, status.toString());
  				print(getIndent() + 1, false, homePlayers[i]);
  				status = new StringBuilder();
  				if (playersAffectedHome[i]) {
  					status.append(" has been stunned.");
  				} else {
  					status.append(" is unaffected.");
  				}
  				int total = rollsHome[i] + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;
  				status.append(" (Roll ").append(rollsHome[i]);
  				status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" opposing FAME");
  				status.append(" + ").append(fanFavouritesAway).append(" opposing Fan Favourites");
  				status.append(" = ").append(total).append(" Total)");
  				println(getIndent() + 1, status.toString());
  			}
  		}
  		int[] rollsAway = report.getRollsAway();
  		boolean[] playersAffectedAway = report.getPlayersAffectedAway();
  		Player<?>[] awayPlayers = game.getTeamAway().getPlayers();
  		for (int i = 0; i < awayPlayers.length; i++) {
  			if (rollsAway[i] > 0) {
  				StringBuilder status = new StringBuilder();
  				status.append("Pitch Invasion Roll [ ").append(rollsAway[i]).append(" ]");
  				println(getIndent(), TextStyle.ROLL, status.toString());
  				print(getIndent() + 1, false, awayPlayers[i]);
  				status = new StringBuilder();
  				if (playersAffectedAway[i]) {
  					status.append(" has been stunned.");
  				} else {
  					status.append(" is unaffected.");
  				}
  				int total = rollsAway[i] + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
  				status.append(" (Roll ").append(rollsAway[i]);
  				status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" opposing FAME ");
  				status.append(" + ").append(fanFavouritesHome).append(" opposing Fan Favourites");
  				status.append(" = ").append(total).append(" Total)");
  				println(getIndent() + 1, status.toString());
  			}
  		}
    }
}
