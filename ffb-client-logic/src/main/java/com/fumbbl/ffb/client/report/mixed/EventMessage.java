package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportEvent;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.EVENT)
@RulesCollection(RulesCollection.Rules.BB2025)
public class EventMessage extends ReportMessageBase<ReportEvent> {
	@Override
	protected void render(ReportEvent report) {
		println(getIndent() + 1, TextStyle.NONE, report.getEventMessage());
	}
}
