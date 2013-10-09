package com.balancedbytes.games.ffb.report;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.helpers.AttributesImpl;

import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.UtilXml;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;




/**
 * 
 * @author Kalimar
 */
public class ReportFumbblResultUpload implements IReport {
  
  private static final String _XML_ATTRIBUTE_SUCCESSFUL = "successful";
  private static final String _XML_TAG_STATUS = "status";
  
  private boolean fSuccessful;
  private String fUploadStatus;
  
  public ReportFumbblResultUpload() {
    super();
  }
  
  public ReportFumbblResultUpload(boolean pSuccessful, String pStatus) {
    fSuccessful = pSuccessful;
    fUploadStatus = pStatus;
  }
  
  public ReportId getId() {
    return ReportId.FUMBBL_RESULT_UPLOAD;
  }
  
  public boolean isSuccessful() {
    return fSuccessful;
  }
  
  public String getUploadStatus() {
    return fUploadStatus;
  }
  
  // transformation
  
  public IReport transform() {
    return new ReportFumbblResultUpload(isSuccessful(), getUploadStatus());
  }

  // XML serialization
  
  public void addToXml(TransformerHandler pHandler) {
    AttributesImpl attributes = new AttributesImpl();
    UtilXml.addAttribute(attributes, XML_ATTRIBUTE_ID, getId().getName());
    UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_SUCCESSFUL, isSuccessful());
    UtilXml.startElement(pHandler, XML_TAG, attributes);
    if (StringTool.isProvided(getUploadStatus())) {
      UtilXml.addValueElement(pHandler, _XML_TAG_STATUS, getUploadStatus());
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
    pByteList.addSmallInt(getId().getId());
    pByteList.addSmallInt(getByteArraySerializationVersion());
    pByteList.addBoolean(isSuccessful());
    pByteList.addString(getUploadStatus());
  }
  
  public int initFrom(ByteArray pByteArray) {
    UtilReport.validateReportId(this, new ReportIdFactory().forId(pByteArray.getSmallInt()));
    int byteArraySerializationVersion = pByteArray.getSmallInt();
    fSuccessful = pByteArray.getBoolean();
    fUploadStatus = pByteArray.getString();
    return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.REPORT_ID.addTo(jsonObject, getId());
    IJsonOption.SUCCESSFUL.addTo(jsonObject, fSuccessful);
    IJsonOption.UPLOAD_STATUS.addTo(jsonObject, fUploadStatus);
    return jsonObject;
  }
  
  public ReportFumbblResultUpload initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilReport.validateReportId(this, (ReportId) IJsonOption.REPORT_ID.getFrom(jsonObject));
    fSuccessful = IJsonOption.SUCCESSFUL.getFrom(jsonObject);
    fUploadStatus = IJsonOption.UPLOAD_STATUS.getFrom(jsonObject);
    return this;
  }
  
}
