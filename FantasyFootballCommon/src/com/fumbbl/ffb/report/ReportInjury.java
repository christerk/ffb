package com.fumbbl.ffb.report;

import com.fumbbl.ffb.InjuryContext;

public interface ReportInjury extends IReport {
	ReportInjury init(InjuryContext injuryContext);
}
