package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportKickTeamMateFumble;

@ReportMessageType(ReportId.KICK_TEAM_MATE_FUMBLE)
@RulesCollection(Rules.BB2020)
public class KickTeamMateFumbleMessage extends ReportMessageBase<ReportKickTeamMateFumble> {

	@Override
	protected void render(ReportKickTeamMateFumble report) {
		println(getIndent() + 2, TextStyle.EXPLANATION, "Fumbled Kick Team-Mate always removes kicked player and causes at least a KO.");
	}

}
