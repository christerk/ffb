package com.balancedbytes.games.ffb.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.xml.IXmlWriteable;
import com.balancedbytes.games.ffb.xml.UtilXml;


/**
 * 
 * @author Kalimar
 */
public class ReportList implements IByteArraySerializable, IXmlWriteable {
  
  public static final String XML_TAG = "reportList";

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

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    UtilXml.startElement(pHandler, XML_TAG);
    for (IReport report : fReports) {
      report.addToXml(pHandler);
    }
    UtilXml.endElement(pHandler, XML_TAG);
  }

  public String toXml(boolean pIndent) {
    return UtilXml.toXml(this, pIndent);
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
  
}
