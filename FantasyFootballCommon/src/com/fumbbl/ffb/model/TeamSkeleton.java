package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.UtilXml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * 
 * @author Kalimar
 */
public class TeamSkeleton extends Team {

	public static final String XML_TAG = "team";

	private static final String _XML_ATTRIBUTE_ID = "id";

	private static final String _XML_TAG_NAME = "name";
	private static final String _XML_TAG_TEAM_VALUE = "teamValue";
	private static final String _XML_TAG_COACH = "coach";

	private String fId;
	private String fName;
	private int fTeamValue;
	private String fCoach;
	private String xmlContent;

	public TeamSkeleton(IFactorySource game) {
		super(game);
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	public void setId(String pId) {
		fId = pId;
	}

	public String getId() {
		return fId;
	}

	public String getName() {
		return fName;
	}

	public void setName(String pName) {
		fName = pName;
	}

	public void setCoach(String coach) {
		fCoach = coach;
	}

	public String getCoach() {
		return fCoach;
	}

	public int getTeamValue() {
		return fTeamValue;
	}

	public void setTeamValue(int pTeamValue) {
		fTeamValue = pTeamValue;
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_ID, getId());
		UtilXml.startElement(pHandler, XML_TAG, attributes);

		UtilXml.addValueElement(pHandler, _XML_TAG_COACH, getCoach());
		UtilXml.addValueElement(pHandler, _XML_TAG_NAME, getName());
		UtilXml.addValueElement(pHandler, _XML_TAG_TEAM_VALUE, getTeamValue());

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (XML_TAG.equals(pXmlTag)) {
			setId(UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_ID));
		}
		// when reading XML only
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		boolean complete = XML_TAG.equals(pXmlTag);
		if (!complete) {
			if (_XML_TAG_NAME.equals(pXmlTag)) {
				fName = pValue;
			}
			if (_XML_TAG_COACH.equals(pXmlTag)) {
				setCoach(pValue);
			}
			if (_XML_TAG_TEAM_VALUE.equals(pXmlTag)) {
				setTeamValue(Integer.parseInt(pValue));
			}
		}
		return complete;
	}

	// JSON serialization

	public JsonObject toJsonValue() {

		JsonObject jsonObject = new JsonObject();

		IJsonOption.TEAM_ID.addTo(jsonObject, fId);
		IJsonOption.TEAM_NAME.addTo(jsonObject, fName);
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.TEAM_VALUE.addTo(jsonObject, fTeamValue);

		return jsonObject;
	}

	public TeamSkeleton initFrom(IFactorySource game, JsonValue pJsonValue) {

		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);

		fId = IJsonOption.TEAM_ID.getFrom(game, jsonObject);
		fName = IJsonOption.TEAM_NAME.getFrom(game, jsonObject);
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fTeamValue = IJsonOption.TEAM_VALUE.getFrom(game, jsonObject);

		return this;

	}

}
