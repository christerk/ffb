package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.report.ReportId;

public class SingleDieStat extends DieStat<Integer>{

	private final int minimumRoll;
	private final ReportId reportId;

	public SingleDieStat(DieBase base, TeamMapping mapping, String id, Integer value, int minimumRoll, ReportId reportId) {
		super(base, mapping, id, value);
		this.minimumRoll = minimumRoll;
		this.reportId = reportId;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public ReportId getReportId() {
		return reportId;
	}
}
