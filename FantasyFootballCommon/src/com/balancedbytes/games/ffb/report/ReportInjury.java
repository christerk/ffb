package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.InjuryContext;

public interface ReportInjury extends IReport {
	ReportInjury init(InjuryContext injuryContext);
}
