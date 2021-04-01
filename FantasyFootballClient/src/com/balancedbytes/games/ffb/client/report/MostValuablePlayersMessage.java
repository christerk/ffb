package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportMostValuablePlayers;

@ReportMessageType(ReportId.MOST_VALUABLE_PLAYERS)
@RulesCollection(Rules.COMMON)
public class MostValuablePlayersMessage extends ReportMessageBase<ReportMostValuablePlayers> {

    public MostValuablePlayersMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportMostValuablePlayers report) {
  		reportGameEnd();

  		println(getIndent(), TextStyle.BOLD, "Most Valuable Players");

  		for (String playerId : report.getPlayerIdsHome()) {
  			Player<?> player = game.getPlayerById(playerId);
  			print(getIndent() + 1, TextStyle.NONE, "The jury voted ");
  			print(getIndent() + 1, TextStyle.HOME, player.getName());
  			print(getIndent() + 1, TextStyle.NONE, " the most valuable player of ");
  			print(getIndent() + 1, TextStyle.NONE, player.getPlayerGender().getGenitive());
  			println(getIndent() + 1, TextStyle.NONE, " team.");
  		}

  		for (String playerId : report.getPlayerIdsAway()) {
  			Player<?> player = game.getPlayerById(playerId);
  			print(getIndent() + 1, TextStyle.NONE, "The jury voted ");
  			print(getIndent() + 1, TextStyle.AWAY, player.getName());
  			print(getIndent() + 1, TextStyle.NONE, " the most valuable player of ");
  			print(getIndent() + 1, TextStyle.NONE, player.getPlayerGender().getGenitive());
  			println(getIndent() + 1, TextStyle.NONE, " team.");
  		}
    }
    
  	private void reportGameEnd() {

  		setIndent(0);

  		GameResult gameResult = game.getGameResult();
  		int scoreDiffHome = gameResult.getTeamResultHome().getScore() - gameResult.getTeamResultAway().getScore();

  		StringBuilder status = new StringBuilder();
  		if (gameResult.getTeamResultHome().hasConceded()) {
  			status.append("Coach ").append(game.getTeamHome().getCoach()).append(" concedes the game.");
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
  		} else if (gameResult.getTeamResultAway().hasConceded()) {
  			status.append("Coach ").append(game.getTeamAway().getCoach()).append(" concedes the game.");
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
  		} else if (scoreDiffHome > 0) {
  			status.append(game.getTeamHome().getName()).append(" win the game.");
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
  		} else if (scoreDiffHome < 0) {
  			status.append(game.getTeamAway().getName()).append(" win the game.");
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
  		} else {
  			status.append("The game ends in a tie.");
  			println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, status.toString());
  		}

  	}    
}
