package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.report.ReportId;

public class SingleDieStat extends DieStat<Integer> {

	private final int minimumRoll;
	private final ReportId reportId;
	private final boolean successful;

	public SingleDieStat(DieBase base, TeamMapping mapping, String id, Integer value, int minimumRoll, ReportId reportId, boolean successful) {
		this(base, mapping, id, value, minimumRoll, reportId, successful, true);
	}

	public SingleDieStat(DieBase base, TeamMapping mapping, String id, Integer value, int minimumRoll, ReportId reportId, boolean successful, boolean duringGame) {
		super(base, mapping, id, value, duringGame);
		this.minimumRoll = minimumRoll;
		this.reportId = reportId;
		this.successful = successful;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public ReportId getReportId() {
		return reportId;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
