package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.HeatExhaustion;
import com.fumbbl.ffb.KnockoutRecovery;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportTurnEnd;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.List;

@ReportMessageType(ReportId.TURN_END)
@RulesCollection(Rules.BB2020)
public class TurnEndMessage extends ReportMessageBase<ReportTurnEnd> {

    @Override
    protected void render(ReportTurnEnd report) {
  		setIndent(0);
  		Player<?> touchdownPlayer = game.getPlayerById(report.getPlayerIdTouchdown());
  		if (touchdownPlayer != null) {
  			print(getIndent(), true, touchdownPlayer);
  			println(getIndent() + 1, TextStyle.BOLD, " scores a touchdown.");
  		}
  		KnockoutRecovery[] knockoutRecoveries = report.getKnockoutRecoveries();
  		if (ArrayTool.isProvided(knockoutRecoveries)) {
  			for (KnockoutRecovery knockoutRecovery : knockoutRecoveries) {
  				StringBuilder status = new StringBuilder();
  				status.append("Knockout Recovery Roll [ ").append(knockoutRecovery.getRoll()).append(" ] ");
  				if (knockoutRecovery.getBloodweiserBabes() > 0) {
  					status.append(" + ").append(knockoutRecovery.getBloodweiserBabes()).append(" Bloodweiser Kegs");
  				}
  				println(getIndent(), TextStyle.ROLL, status.toString());
  				Player<?> player = game.getPlayerById(knockoutRecovery.getPlayerId());
  				print(getIndent() + 1, false, player);
  				if (knockoutRecovery.isRecovering()) {
  					println(getIndent() + 1, " is regaining consciousness.");
  				} else {
  					println(getIndent() + 1, " stays unconscious.");
  				}
  			}
  		}
	    println(getIndent(), TextStyle.ROLL, "Heat Exhaustion Roll [ " + report.getHeatRoll() + " ] ");
  		HeatExhaustion[] heatExhaustions = report.getHeatExhaustions();
  		if (ArrayTool.isProvided(heatExhaustions)) {
  			for (HeatExhaustion heatExhaustion : heatExhaustions) {
  				Player<?> player = game.getPlayerById(heatExhaustion.getPlayerId());
  				print(getIndent() + 1, false, player);
  				if (heatExhaustion.isExhausted()) {
  					println(getIndent() + 1, " is suffering from heat exhaustion.");
  				} else {
  					println(getIndent() + 1, " is unaffected.");
  				}
  			}
  		}
  		List<Player<?>> unzappedPlayers = report.getUnzappedPlayers();
  		if (unzappedPlayers != null) {
  			for (Player<?> player : unzappedPlayers) {
  				print(getIndent(), true, player);
  				println(getIndent(), " recovers from Zap! spell effect.");
  			}
  		}
  		if (TurnMode.REGULAR == game.getTurnMode()) {
  			StringBuilder status = new StringBuilder();
  			if (game.isHomePlaying()) {
  				status.append(game.getTeamHome().getName()).append(" start turn ").append(game.getTurnDataHome().getTurnNr())
  					.append(".");
  				println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_HOME, status.toString());
  			} else {
  				status.append(game.getTeamAway().getName()).append(" start turn ").append(game.getTurnDataAway().getTurnNr())
  					.append(".");
  				println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN_AWAY, status.toString());
  			}
  		}
    }
}
