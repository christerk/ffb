package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportDoubleHiredStaff;

@ReportMessageType(ReportId.DOUBLE_HIRED_STAFF)
@RulesCollection(Rules.BB2020)
public class DoubleHiredStaffMessage extends ReportMessageBase<ReportDoubleHiredStaff> {

	@Override
	protected void render(ReportDoubleHiredStaff report) {
		String status = "Inamous Coaching Staff " + report.getStaffName() +
			" takes money from both teams and plays for neither.";
		println(getIndent(), TextStyle.BOLD, status);
	}
}
