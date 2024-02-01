package com.fumbbl.ffb.inducement;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.InducementTypeFactory;
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
public class Inducement implements IXmlSerializable, IJsonSerializable {

	public static final String XML_TAG = "inducement";

	private static final String _XML_ATTRIBUTE_TYPE = "type";
	private static final String _XML_ATTRIBUTE_VALUE = "value";
	private static final String _XML_ATTRIBUTE_USES = "uses";

	private InducementType fType;
	private int fValue;
	private int fUses;

	public Inducement() {
		super();
	}

	public Inducement(InducementType pType, int pValue) {
		fType = pType;
		setValue(pValue);
	}

	public InducementType getType() {
		return fType;
	}

	public int getValue() {
		return fValue;
	}

	public void setValue(int pValue) {
		fValue = pValue;
	}

	public int getUses() {
		return fUses;
	}

	public void setUses(int pCurrent) {
		fUses = pCurrent;
	}

	public int getUsesLeft() {
		return Math.max(0, getValue() - getUses());
	}

	// XML serialization

	public void addToXml(TransformerHandler pHandler) {
		AttributesImpl attributes = new AttributesImpl();
		String typeName = (fType != null) ? fType.getName() : null;
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_TYPE, typeName);
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_VALUE, fValue);
		UtilXml.addAttribute(attributes, _XML_ATTRIBUTE_USES, fUses);
		UtilXml.addEmptyElement(pHandler, XML_TAG, attributes);
	}

	public String toXml(boolean pIndent) {
		return UtilXml.toXml(this, pIndent);
	}

	public IXmlReadable startXmlElement(Game game, String pXmlTag, Attributes pXmlAttributes) {
		InducementTypeFactory inducementTypeFactory = game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		if (XML_TAG.equals(pXmlTag)) {
			String typeName = UtilXml.getStringAttribute(pXmlAttributes, _XML_ATTRIBUTE_TYPE);
			fType = inducementTypeFactory.forName(typeName);
			fValue = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_VALUE);
			fUses = UtilXml.getIntAttribute(pXmlAttributes, _XML_ATTRIBUTE_USES);
		}
		return this;
	}

	public boolean endXmlElement(Game game, String pXmlTag, String pValue) {
		return XML_TAG.equals(pXmlTag);
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.INDUCEMENT_TYPE.addTo(jsonObject, fType);
		IJsonOption.VALUE.addTo(jsonObject, fValue);
		IJsonOption.USES.addTo(jsonObject, fUses);
		return jsonObject;
	}

	public Inducement initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fType = (InducementType) IJsonOption.INDUCEMENT_TYPE.getFrom(source, jsonObject);
		fValue = IJsonOption.VALUE.getFrom(source, jsonObject);
		fUses = IJsonOption.USES.getFrom(source, jsonObject);
		return this;
	}

}
