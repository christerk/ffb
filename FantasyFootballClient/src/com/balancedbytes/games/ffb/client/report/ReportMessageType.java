package com.balancedbytes.games.ffb.client.report;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.balancedbytes.games.ffb.report.ReportId;

@Retention(RUNTIME)
public @interface ReportMessageType {
	ReportId value() default ReportId.NONE;
}
