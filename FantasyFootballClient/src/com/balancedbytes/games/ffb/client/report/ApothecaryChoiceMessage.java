package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportApothecaryChoice;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.APOTHECARY_CHOICE)
@RulesCollection(Rules.COMMON)
public class ApothecaryChoiceMessage extends ReportMessageBase<ReportApothecaryChoice> {

    public ApothecaryChoiceMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportApothecaryChoice report) {
  		GameResult gameResult = game.getGameResult();
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		if ((report.getPlayerState() != null) && (report.getPlayerState().getBase() == PlayerState.RESERVE)) {
  			print(getIndent(), TextStyle.BOLD, "The apothecary patches ");
  			print(getIndent(), true, player);
  			println(getIndent(), TextStyle.BOLD, " up so " + player.getPlayerGender().getNominative() + " is able to play again.");
  		} else {
  			print(getIndent(), "Coach ");
  			if (game.getTeamHome().hasPlayer(player)) {
  				print(getIndent(), TextStyle.HOME, game.getTeamHome().getCoach());
  			} else {
  				print(getIndent(), TextStyle.AWAY, game.getTeamAway().getCoach());
  			}
  			PlayerState playerStateOld = game.getFieldModel().getPlayerState(player);
  			SeriousInjury seriousInjuryOld = gameResult.getPlayerResult(player).getSeriousInjury();
  			if ((report.getPlayerState() != playerStateOld) || (report.getSeriousInjury() != seriousInjuryOld)) {
  				println(getIndent(), " chooses the new injury result.");
  			} else {
  				println(getIndent(), " keeps the old injury result.");
  			}
  		}
    }
}
