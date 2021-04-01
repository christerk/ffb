package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportFumbblResultUpload;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.FUMBBL_RESULT_UPLOAD)
@RulesCollection(Rules.COMMON)
public class FumbblResultUploadMessage extends ReportMessageBase<ReportFumbblResultUpload> {

	public FumbblResultUploadMessage(StatusReport statusReport) {
		super(statusReport);
	}

	@Override
	protected void render(ReportFumbblResultUpload report) {
		StringBuilder status = new StringBuilder();
		status.append("Fumbbl Result Upload ");
		if (report.isSuccessful()) {
			status.append("ok");
		} else {
			status.append("failed");
		}
		println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.BOLD, status.toString());
		println(getIndent() + 1, report.getUploadStatus());
	}

}
