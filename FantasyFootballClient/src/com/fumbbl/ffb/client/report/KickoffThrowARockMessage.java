package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportKickoffThrowARock;
import com.fumbbl.ffb.util.UtilPlayer;

@ReportMessageType(ReportId.KICKOFF_THROW_A_ROCK)
@RulesCollection(Rules.COMMON)
public class KickoffThrowARockMessage extends ReportMessageBase<ReportKickoffThrowARock> {

    @Override
    protected void render(ReportKickoffThrowARock report) {
  		GameResult gameResult = game.getGameResult();
  		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
  			NamedProperties.increasesTeamsFame).length;
  		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
  			NamedProperties.increasesTeamsFame).length;
  		StringBuilder status = new StringBuilder();
  		status.append("Throw a Rock Roll Home Team [ ").append(report.getRollHome()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome;
  		status = new StringBuilder();
  		status.append("Rolled ").append(report.getRollHome());
  		status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
  		status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
  		status.append(" = ").append(totalHome).append(".");
  		println(getIndent() + 1, status.toString());
  		status = new StringBuilder();
  		status.append("Throw a Rock Roll Away Team [ ").append(report.getRollAway()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway;
  		status = new StringBuilder();
  		status.append("Rolled ").append(report.getRollAway());
  		status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
  		status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
  		status.append(" = ").append(totalAway).append(".");
  		println(getIndent() + 1, status.toString());
  		for (String playerId : report.getPlayersHit()) {
  			Player<?> player = game.getPlayerById(playerId);
  			print(getIndent(), false, player);
  			println(getIndent(), " is hit by a rock.");
  		}
    }
}
