package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportFumbblResultUpload;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.FUMBBL_RESULT_UPLOAD)
@RulesCollection(Rules.COMMON)
public class FumbblResultUploadMessage extends ReportMessageBase<ReportFumbblResultUpload> {

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
