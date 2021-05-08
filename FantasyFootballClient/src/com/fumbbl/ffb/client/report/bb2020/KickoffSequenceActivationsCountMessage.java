package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportKickoffSequenceActivationsCount;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.KICKOFF_SEQUENCE_ACTIVATIONS_COUNT)
public class KickoffSequenceActivationsCountMessage extends ReportMessageBase<ReportKickoffSequenceActivationsCount> {
	@Override
	protected void render(ReportKickoffSequenceActivationsCount report) {
		String builder = "Moved " +
			report.getAmount() +
			" of the allowed " +
			report.getLimit() +
			" players (" +
			report.getAvailable() +
			" still open).";
		println(getIndent() + 1, TextStyle.EXPLANATION, builder);
	}
}
