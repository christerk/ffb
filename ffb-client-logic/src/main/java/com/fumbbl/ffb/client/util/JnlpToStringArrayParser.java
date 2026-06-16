package com.fumbbl.ffb.client.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.stream.IntStream;

public class JnlpToStringArrayParser {

    public static String[] parseJnlpArguments(File jnlpFile) {
        try {
            // 1. Initialize standard JDK XML DOM Parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(jnlpFile);

            document.getDocumentElement().normalize();

            // 2. Locate the <application-desc> element
            NodeList appDescList = document.getElementsByTagName("application-desc");
            if (appDescList.getLength() > 0) {
                Element appDescElement = (Element) appDescList.item(0);

                // 3. Get all <argument> tags specifically inside <application-desc>
                NodeList argumentNodes = appDescElement.getElementsByTagName("argument");

                // 4. Use Java 8 Streams to cleanly map the nodes to a String array
                return IntStream.range(0, argumentNodes.getLength())
                        .mapToObj(argumentNodes::item)
                        .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                        .map(Node::getTextContent)
                        .map(String::trim)
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse the jnlp file.");
        }

        // Return an empty array instead of null if something goes wrong
        return new String[0];
    }
}
