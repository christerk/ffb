package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * 
 * @author Kalimar
 */
public class RosterSkeleton implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "roster";

	private static final String _XML_ATTRIBUTE_ID = "id";

	private String fId;


	public String getId() {
		return fId;
	}

	public void setId(String pId) {
		fId = pId;
	}

// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
		UtilXml.startElement(pHandler, XML_TAG);

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlSerializable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		if (XML_TAG.equals(pXmlTag)) {
			if (StringTool.isProvided(pXmlAttributes.getValue(_XML_ATTRIBUTE_ID))) {
				fId = pXmlAttributes.getValue(_XML_ATTRIBUTE_ID).trim();
			}
		}
		return this;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.ROSTER_ID.addTo(jsonObject, fId);
		return jsonObject;
	}

	public RosterSkeleton initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fId = IJsonOption.ROSTER_ID.getFrom(game, jsonObject);
		return this;
	}
}