package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportApothecaryChoice;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.APOTHECARY_CHOICE)
@RulesCollection(Rules.COMMON)
public class ApothecaryChoiceMessage extends ReportMessageBase<ReportApothecaryChoice> {

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
