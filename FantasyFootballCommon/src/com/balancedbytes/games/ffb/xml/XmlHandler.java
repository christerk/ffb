package com.balancedbytes.games.ffb.xml;

import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.balancedbytes.games.ffb.FantasyFootballException;

/**
 * 
 * @author Kalimar
 */
public class XmlHandler extends DefaultHandler {

	private StringBuilder fValue;
	private IXmlReadable fParsedElement;
	private Stack<IXmlReadable> fXmlElementStack;

	/**
	 * Default constructor.
	 */
	public XmlHandler(IXmlReadable pParsedElement) {
		fValue = new StringBuilder();
		fParsedElement = pParsedElement;
		fXmlElementStack = new Stack<IXmlReadable>();
		fXmlElementStack.push(fParsedElement);
	}

	public IXmlReadable getParsedElement() {
		return fParsedElement;
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char, int, int)
	 */
	public void characters(char[] ch, int start, int length) {
		fValue.append(new String(ch, start, length));
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) {
		String value = fValue.toString().trim();
		while (!fXmlElementStack.empty()) {
			IXmlReadable currentElement = fXmlElementStack.pop();
			if (!currentElement.endXmlElement(qName, value)) {
				fXmlElementStack.push(currentElement);
				break;
			}
		}
		fValue = new StringBuilder();
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		if (!fXmlElementStack.empty()) {
			IXmlReadable currentElement = fXmlElementStack.peek();
			IXmlReadable newElement = currentElement.startXmlElement(qName, atts);
			if (currentElement != newElement) {
				fXmlElementStack.push(newElement);
			}
		}
	}

	public static void parse(InputSource pXmlSource, IXmlReadable pParsedElement) {
		SAXParserFactory xmlParserFactory = SAXParserFactory.newInstance();
		xmlParserFactory.setNamespaceAware(false);
		XmlHandler xmlHandler = new XmlHandler(pParsedElement);
		XMLReader xmlReader = null;
		try {
			xmlReader = xmlParserFactory.newSAXParser().getXMLReader();
			xmlReader.setContentHandler(xmlHandler);
		} catch (Exception e) {
			throw new FantasyFootballException("Unable to initialize parser.", e);
		}
		try {
			xmlReader.parse(pXmlSource);
		} catch (Exception e) {
			throw new FantasyFootballException("Parsing error.", e);
		}
	}

}
