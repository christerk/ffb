package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ReportList implements IJsonSerializable {

	private List<IReport> fReports;

	private ReportList(int pInitialCapacity) {
		fReports = new ArrayList<>(pInitialCapacity);
	}

	public ReportList() {
		this(20);
	}

	public void add(IReport pReport) {
		fReports.add(pReport);
	}

	public boolean hasReport(ReportId pReportId) {
		boolean reportFound = false;
		for (IReport report : fReports) {
			if (report.getId() == pReportId) {
				reportFound = true;
			}
		}
		return reportFound;
	}

	public void add(ReportList pReportList) {
		if (pReportList != null) {
			for (IReport report : pReportList.getReports()) {
				add(report);
			}
		}
	}

	public IReport[] getReports() {
		return fReports.toArray(new IReport[fReports.size()]);
	}

	public void clear() {
		fReports.clear();
	}

	public int size() {
		return fReports.size();
	}

	public ReportList copy() {
		ReportList copiedList = new ReportList(size());
		for (IReport report : fReports) {
			copiedList.add(report);
		}
		return copiedList;
	}

	// transformation

	public ReportList transform(IFactorySource source) {
		ReportList transformedList = new ReportList(size());
		for (IReport report : fReports) {
			transformedList.add(report.transform(source));
		}
		return transformedList;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray reportArray = new JsonArray();
		for (IReport report : fReports) {
			reportArray.add(report.toJsonValue());
		}
		IJsonOption.REPORTS.addTo(jsonObject, reportArray);
		return jsonObject;
	}

	public ReportList initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		JsonArray reportArray = IJsonOption.REPORTS.getFrom(source, jsonObject);
		if (reportArray != null) {
			ReportFactory reportFactory = new ReportFactory();
			for (int i = 0; i < reportArray.size(); i++) {
				add(reportFactory.forJsonValue(source, reportArray.get(i)));
			}
		}
		return this;
	}

}
