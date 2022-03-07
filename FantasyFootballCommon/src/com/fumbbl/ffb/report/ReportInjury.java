package com.fumbbl.ffb.report;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.report.logcontrol.SkipInjuryParts;

public interface ReportInjury extends IReport {
	ReportInjury init(InjuryContext injuryContext, SkipInjuryParts skip);
}
