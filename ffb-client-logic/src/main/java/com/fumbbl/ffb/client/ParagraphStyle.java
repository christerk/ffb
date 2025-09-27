package com.fumbbl.ffb.client;

/**
 * @author Dominic Schabel
 */
public enum ParagraphStyle {

	INDENT_0("indent0"), INDENT_1("indent1"), INDENT_2("indent2"), INDENT_3("indent3"), INDENT_4("indent4"),
	INDENT_5("indent5"), INDENT_6("indent6"), SPACE_ABOVE("spaceAbove"), SPACE_BELOW("spaceBelow"),
	SPACE_ABOVE_BELOW("spaceAboveBelow"), CHAT_BODY("chatBody");

	private String fName;

	private ParagraphStyle(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
