package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.kickoff.KickoffResult;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2016.ReportKickoffExtraReRoll;
import com.fumbbl.ffb.util.UtilPlayer;

@ReportMessageType(ReportId.KICKOFF_EXTRA_REROLL)
@RulesCollection(Rules.BB2016)
public class KickoffExtraReRollMessage extends ReportMessageBase<ReportKickoffExtraReRoll> {

    @Override
    protected void render(ReportKickoffExtraReRoll report) {
  		GameResult gameResult = game.getGameResult();
  		KickoffResult kickoffResult = report.getKickoffResult();
  		int fanFavouritesHome = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamHome(),
  			NamedProperties.increasesTeamsFame).length;
  		int fanFavouritesAway = UtilPlayer.findPlayersOnPitchWithProperty(game, game.getTeamAway(),
  			NamedProperties.increasesTeamsFame).length;
  		StringBuilder status = new StringBuilder();
  		if (kickoffResult.isFanReRoll()) {
  			status.append("Cheering Fans Roll Home Team [ ").append(report.getRollHome()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome
  				+ game.getTeamHome().getCheerleaders();
  			status = new StringBuilder();
  			status.append("Rolled ").append(report.getRollHome());
  			status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
  			status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
  			status.append(" + ").append(game.getTeamHome().getCheerleaders()).append(" Cheerleaders");
  			status.append(" = ").append(totalHome).append(".");
  			println(getIndent() + 1, status.toString());
  			status = new StringBuilder();
  			status.append("Cheering Fans Roll Away Team [ ").append(report.getRollAway()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway
  				+ game.getTeamAway().getCheerleaders();
  			status = new StringBuilder();
  			status.append("Rolled ").append(report.getRollAway());
  			status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
  			status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
  			status.append(" + ").append(game.getTeamAway().getCheerleaders()).append(" Cheerleaders");
  			status.append(" = ").append(totalAway).append(".");
  			println(getIndent() + 1, status.toString());
  		}
  		if (kickoffResult.isCoachReRoll()) {
  			boolean homeBanned = game.getTurnDataHome().isCoachBanned();
  			boolean awayBanned = game.getTurnDataAway().isCoachBanned();

  			status.append("Brilliant Coaching Roll Home Team [ ").append(report.getRollHome()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFame() + fanFavouritesHome
  				+ game.getTeamHome().getAssistantCoaches() - (homeBanned ? 1 : 0);
  			status = new StringBuilder();
  			status.append("Rolled ").append(report.getRollHome());
  			status.append(" + ").append(gameResult.getTeamResultHome().getFame()).append(" FAME");
  			status.append(" + ").append(fanFavouritesHome).append(" Fan Favourites");
  			status.append(" + ").append(game.getTeamHome().getAssistantCoaches()).append(" Assistant Coaches");
  			status.append(" ").append(homeBanned ? "- 1 Banned" : " + 0 Head").append(" Coach");
  			status.append(" = ").append(totalHome).append(".");
  			println(getIndent() + 1, status.toString());
  			status = new StringBuilder();
  			status.append("Brilliant Coaching Roll Away Team [ ").append(report.getRollAway()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFame() + fanFavouritesAway
  				+ game.getTeamAway().getAssistantCoaches() - (awayBanned ? 1 : 0);
  			status = new StringBuilder();
  			status.append("Rolled ").append(report.getRollAway());
  			status.append(" + ").append(gameResult.getTeamResultAway().getFame()).append(" FAME");
  			status.append(" + ").append(fanFavouritesAway).append(" Fan Favourites");
  			status.append(" + ").append(game.getTeamAway().getAssistantCoaches()).append(" Assistant Coaches");
  			status.append(" ").append(awayBanned ? "- 1 Banned" : " + 0 Head").append(" Coach");
  			status.append(" = ").append(totalAway).append(".");
  			println(getIndent() + 1, status.toString());
  		}
  		if (report.isHomeGainsReRoll()) {
  			print(getIndent(), "Team ");
  			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
  			println(getIndent(), " gains a Re-Roll.");
  		}
  		if (report.isAwayGainsReRoll()) {
  			print(getIndent(), "Team ");
  			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
  			println(getIndent(), " gains a Re-Roll.");
  		}
    }
}
