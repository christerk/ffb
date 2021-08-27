package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReportFactory;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.List;

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
		ReportFactory factory = source.getFactory(FactoryType.Factory.REPORT);
		if (reportArray != null) {
			for (int i = 0; i < reportArray.size(); i++) {
				JsonValue arrayElement = reportArray.get(i);
				add((IReport) factory.forId(getId(source, arrayElement)).initFrom(source, arrayElement));
			}
		}
		return this;
	}

	private ReportId getId(IFactorySource source, JsonValue jsonValue) {
		if ((jsonValue == null) || jsonValue.isNull()) {
			return null;
		}
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		return (ReportId) IJsonOption.REPORT_ID.getFrom(source, jsonObject);
	}
}
