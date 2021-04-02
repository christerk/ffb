package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.HeatExhaustion;
import com.balancedbytes.games.ffb.KnockoutRecovery;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportTurnEnd;
import com.balancedbytes.games.ffb.util.ArrayTool;

import java.util.List;

@ReportMessageType(ReportId.TURN_END)
@RulesCollection(Rules.COMMON)
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
  		HeatExhaustion[] heatExhaustions = report.getHeatExhaustions();
  		if (ArrayTool.isProvided(heatExhaustions)) {
  			for (HeatExhaustion heatExhaustion : heatExhaustions) {
  				StringBuilder status = new StringBuilder();
  				status.append("Heat Exhaustion Roll [ ").append(heatExhaustion.getRoll()).append(" ] ");
  				println(getIndent(), TextStyle.ROLL, status.toString());
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
