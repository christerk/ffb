package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * 
 * @author Kalimar
 */
public class ReportList implements IByteArraySerializable {
  
  private List<IReport> fReports;
  
  private ReportList(int pInitialCapacity) {
    fReports = new ArrayList<IReport>(pInitialCapacity);
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
  
  public ReportList transform() {
    ReportList transformedList = new ReportList(size());
    for (IReport report : fReports) {
      transformedList.add(report.transform());
    }
    return transformedList;
  }

  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
  public void addTo(ByteList pByteList) {
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addSmallInt(fReports.size());
    Iterator<IReport> reportIterator = fReports.iterator();
    while (reportIterator.hasNext()) {
      IReport report = reportIterator.next();
      report.addTo(pByteList);
    }
  }
  
  public int initFrom(ByteArray pByteArray) {
    int byteArraySerializationVersion = pByteArray.getSmallInt();  // byteArraySerializationVersion
    int size = pByteArray.getSmallInt();
    ReportIdFactory reportIdFactory = new ReportIdFactory();
    for (int i = 0; i < size; i++) {
      ReportId reportId = reportIdFactory.forId((pByteArray.getByte(pByteArray.getPosition()) & 0xff) * 256 + (pByteArray.getByte(pByteArray.getPosition() + 1) & 0xff));
      if (reportId != null) {
        IReport report = reportId.createReport();
        report.initFrom(pByteArray);
        add(report);
      }
    }
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    JsonArray reportArray = new JsonArray();
    for (IReport report : fReports) {
      reportArray.add(report.toJsonValue());
    }
    IJsonOption.REPORTS.addTo(jsonObject, reportArray);
    return jsonObject;
  }
  
  public ReportList initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    JsonArray reportArray = IJsonOption.REPORTS.getFrom(jsonObject);
    if (reportArray != null) {
      ReportFactory reportFactory = new ReportFactory();
      for (int i = 0; i < reportArray.size(); i++) {
        add(reportFactory.forJsonValue(reportArray.get(i)));
      }
    }
    return this;
  }  
  
}
