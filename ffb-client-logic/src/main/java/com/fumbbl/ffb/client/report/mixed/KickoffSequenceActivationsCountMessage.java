package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportKickoffSequenceActivationsCount;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
@ReportMessageType(ReportId.KICKOFF_SEQUENCE_ACTIVATIONS_COUNT)
public class KickoffSequenceActivationsCountMessage extends ReportMessageBase<ReportKickoffSequenceActivationsCount> {
	@Override
	protected void render(ReportKickoffSequenceActivationsCount report) {
		String remain = report.getAvailable() == 1 ? "remains" : "remain";
		String builder = "Max " +
			report.getLimit() +
			" open players can be used - " +
			report.getAmount() + " used (" +
			report.getAvailable() + " " + remain + " open).";
		println(getIndent() + 1, TextStyle.EXPLANATION, builder);
	}
}
