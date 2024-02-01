package com.fumbbl.ffb.client.report;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.fumbbl.ffb.report.ReportId;

@Retention(RUNTIME)
public @interface ReportMessageType {
	ReportId value() default ReportId.NONE;
}
