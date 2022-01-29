package com.fumbbl.ffb;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.xml.IXmlReadable;
import com.fumbbl.ffb.xml.IXmlSerializable;
import com.fumbbl.ffb.xml.UtilXml;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * 
 * @author Kalimar
 */
public class RangeRuler implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "rangeRuler";

	private static final String _XML_ATTRIBUTE_THROWER_ID = "throwerId";
	private static final String _XML_ATTRIBUTE_MINIMUM_ROLL = "minimumRoll";
	private static final String _XML_ATTRIBUTE_THROW_TEAM_MATE = "throwTeamMate";

	private static final String _XML_TAG_TARGET_COORDINATE = "targetCoordinate";
	private static final String _XML_ATTRIBUTE_X = "x";
	private static final String _XML_ATTRIBUTE_Y = "y";

	private String fThrowerId;
	private FieldCoordinate fTargetCoordinate;
	private int fMinimumRoll;
	private boolean fThrowTeamMate;

	public RangeRuler() {
		super();
	}

	public RangeRuler(String pThrowerId, FieldCoordinate pTargetCoordinate, int pMinimumRoll, boolean pThrowTeamMate) {
		fThrowerId = pThrowerId;
		fTargetCoordinate = pTargetCoordinate;
		fMinimumRoll = pMinimumRoll;
		fThrowTeamMate = pThrowTeamMate;
	}

	public String getThrowerId() {
		return fThrowerId;
	}

	public FieldCoordinate getTargetCoordinate() {
		return fTargetCoordinate;
	}

	public String getMinimumRoll() {
		if (fMinimumRoll == 0) {
			return "--";
		} else if (fMinimumRoll < 0) {
			return "";
		}else if (fMinimumRoll < 6) {
			return fMinimumRoll + "+";
		} else {
			return "6";
		}
	}

	public boolean isThrowTeamMate() {
		return fThrowTeamMate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RangeRuler that = (RangeRuler) o;

		if (fMinimumRoll != that.fMinimumRoll) return false;
		if (fThrowTeamMate != that.fThrowTeamMate) return false;
		if (fThrowerId != null ? !fThrowerId.equals(that.fThrowerId) : that.fThrowerId != null) return false;
		return fTargetCoordinate != null ? fTargetCoordinate.equals(that.fTargetCoordinate) : that.fTargetCoordinate == null;
	}

	@Override
	public int hashCode() {
		int result = fThrowerId != null ? fThrowerId.hashCode() : 0;
		result = 31 * result + (fTargetCoordinate != null ? fTargetCoordinate.hashCode() : 0);
		result = 31 * result + fMinimumRoll;
		result = 31 * result + (fThrowTeamMate ? 1 : 0);
		return result;
	}

	// transformation

	public RangeRuler transform() {
		return new RangeRuler(getThrowerId(), FieldCoordinate.transform(getTargetCoordinate()), fMinimumRoll,
			isThrowTeamMate());
	}

	public static RangeRuler transform(RangeRuler pTrackNumber) {
		return (pTrackNumber != null) ? pTrackNumber.transform() : null;
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {

		AttributesImpl attributes = new AttributesImpl();
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_THROWER_ID, getThrowerId());
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_MINIMUM_ROLL, fMinimumRoll);
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_THROW_TEAM_MATE, isThrowTeamMate());
		UtilXml.startElement(pHandler, XML_TAG, attributes);

		if (getTargetCoordinate() != null) {
			attributes = new AttributesImpl();
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_X, getTargetCoordinate().getX());
			UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_Y, getTargetCoordinate().getY());
			UtilXml.startElement(pHandler, _XML_TAG_TARGET_COORDINATE, attributes);
			UtilXml.endElement(pHandler, _XML_TAG_TARGET_COORDINATE);
		}

		UtilXml.endElement(pHandler, XML_TAG);

	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		IXmlReadable xmlElement = this;
		if (XML_TAG.equals(pXmlTag)) {
			fThrowerId = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_THROWER_ID);
			fMinimumRoll = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_MINIMUM_ROLL);
			fThrowTeamMate = UtilXml.getBooleanAttribute(pXmlAttributes, _XML_ATTRIBUTE_THROW_TEAM_MATE);
		}
		if (_XML_TAG_TARGET_COORDINATE.equals(pXmlTag)) {
			int x = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_X);
			int y = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_Y);
			fTargetCoordinate = new FieldCoordinate(x, y);
		}
		return xmlElement;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
		IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		IJsonOption.MINIMUM_ROLL.addTo(jsonObject, fMinimumRoll);
		IJsonOption.THROW_TEAM_MATE.addTo(jsonObject, fThrowTeamMate);
		return jsonObject;
	}

	public RangeRuler initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrowerId = IJsonOption.THROWER_ID.getFrom(game, jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(game, jsonObject);
		fMinimumRoll = IJsonOption.MINIMUM_ROLL.getFrom(game, jsonObject);
		fThrowTeamMate = IJsonOption.THROW_TEAM_MATE.getFrom(game, jsonObject);
		return this;
	}

}
