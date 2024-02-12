package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportEvent;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.EVENT)
public class EventMessage extends ReportMessageBase<ReportEvent> {
	@Override
	protected void render(ReportEvent report) {
		println(getIndent() + 1, TextStyle.NONE, report.getEventMessage());
	}
}
