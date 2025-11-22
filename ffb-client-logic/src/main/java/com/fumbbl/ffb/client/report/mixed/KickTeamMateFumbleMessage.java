package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportKickTeamMateFumble;

@ReportMessageType(ReportId.KICK_TEAM_MATE_FUMBLE)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class KickTeamMateFumbleMessage extends ReportMessageBase<ReportKickTeamMateFumble> {

	@Override
	protected void render(ReportKickTeamMateFumble report) {
		println(getIndent() + 2, TextStyle.EXPLANATION, "Fumbled Kick Team-Mate always removes kicked player and causes at least a KO.");
	}

}
