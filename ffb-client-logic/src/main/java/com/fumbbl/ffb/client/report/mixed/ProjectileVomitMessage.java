package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportProjectileVomit;

@ReportMessageType(ReportId.PROJECTILE_VOMIT)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class ProjectileVomitMessage extends ReportMessageBase<ReportProjectileVomit> {

    @Override
    protected void render(ReportProjectileVomit report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Projectile Vomit Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			print(getIndent() + 1, TextStyle.NONE, " vomits on ");
  			print(getIndent() + 1, false, game.getPlayerById(report.getDefenderId()));
  			println(getIndent() + 1, TextStyle.NONE, ".");
  		} else {
  			status = new StringBuilder();
  			status.append(" vomits on ").append(player.getPlayerGender().getSelf()).append(".");
			  println(getIndent() + 1, status.toString());
  		}
    }
}
