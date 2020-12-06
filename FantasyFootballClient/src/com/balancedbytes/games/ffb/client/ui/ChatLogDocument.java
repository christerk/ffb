package com.balancedbytes.games.ffb.client.ui;

import java.awt.Color;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.TextStyle;

/**
 * DefaultDocument subclass that supports batching inserts.
 */
@SuppressWarnings("serial")
public class ChatLogDocument extends DefaultStyledDocument {

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	public static final String DEFAULT_FONT_FAMILY = "Arial";
	public static final int DEFAULT_FONT_SIZE = 12;

	public ChatLogDocument() {

		// initStyles

		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(defaultStyle, DEFAULT_FONT_FAMILY);

		addStyle(TextStyle.NONE.getName(), defaultStyle);

		Style bold = addStyle(TextStyle.BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(bold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(bold, DEFAULT_FONT_SIZE);
		StyleConstants.setBold(bold, true);

		Style home = addStyle(TextStyle.HOME.getName(), defaultStyle);
		StyleConstants.setFontFamily(home, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(home, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(home, Color.RED);

		Style homeBold = addStyle(TextStyle.HOME_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(homeBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(homeBold, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(homeBold, Color.RED);
		StyleConstants.setBold(homeBold, true);

		Style away = addStyle(TextStyle.AWAY.getName(), defaultStyle);
		StyleConstants.setFontFamily(away, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(away, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(away, Color.BLUE);

		Style awayBold = addStyle(TextStyle.AWAY_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(awayBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(awayBold, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(awayBold, Color.BLUE);
		StyleConstants.setBold(awayBold, true);

		Style roll = addStyle(TextStyle.ROLL.getName(), defaultStyle);
		StyleConstants.setFontFamily(roll, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(roll, DEFAULT_FONT_SIZE);
		StyleConstants.setBold(roll, true);

		Style neededRoll = addStyle(TextStyle.NEEDED_ROLL.getName(), defaultStyle);
		StyleConstants.setFontFamily(neededRoll, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(neededRoll, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(neededRoll, Color.DARK_GRAY);

		Style explanation = addStyle(TextStyle.EXPLANATION.getName(), defaultStyle);
		StyleConstants.setItalic(explanation, true);
		StyleConstants.setFontFamily(explanation, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(explanation, DEFAULT_FONT_SIZE);

		Style spectator = addStyle(TextStyle.SPECTATOR.getName(), defaultStyle);
		StyleConstants.setFontFamily(spectator, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(spectator, DEFAULT_FONT_SIZE);
		StyleConstants.setForeground(spectator, new Color(0, 128, 0));

		Style turn = addStyle(TextStyle.TURN.getName(), defaultStyle);
		StyleConstants.setFontFamily(turn, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turn, DEFAULT_FONT_SIZE + 2);
		StyleConstants.setBold(turn, true);

		Style turnHome = addStyle(TextStyle.TURN_HOME.getName(), defaultStyle);
		StyleConstants.setFontFamily(turnHome, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turnHome, DEFAULT_FONT_SIZE + 2);
		StyleConstants.setBold(turnHome, true);
		StyleConstants.setForeground(turnHome, Color.RED);

		Style turnAway = addStyle(TextStyle.TURN_AWAY.getName(), defaultStyle);
		StyleConstants.setFontFamily(turnAway, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turnAway, DEFAULT_FONT_SIZE + 2);
		StyleConstants.setBold(turnAway, true);
		StyleConstants.setForeground(turnAway, Color.BLUE);

		// initindent

		Style indent0 = addStyle(ParagraphStyle.INDENT_0.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent0, 0);
		StyleConstants.setSpaceAbove(indent0, 0.0f);
		StyleConstants.setSpaceBelow(indent0, 0.0f);

		Style indent1 = addStyle(ParagraphStyle.INDENT_1.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent1, 12);
		StyleConstants.setSpaceAbove(indent1, 0.0f);
		StyleConstants.setSpaceBelow(indent1, 0.0f);

		Style indent2 = addStyle(ParagraphStyle.INDENT_2.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent2, 24);
		StyleConstants.setSpaceAbove(indent2, 0.0f);
		StyleConstants.setSpaceBelow(indent2, 0.0f);

		Style indent3 = addStyle(ParagraphStyle.INDENT_3.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent3, 36);
		StyleConstants.setSpaceAbove(indent3, 0.0f);
		StyleConstants.setSpaceBelow(indent3, 0.0f);

		Style indent4 = addStyle(ParagraphStyle.INDENT_4.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent4, 48);
		StyleConstants.setSpaceAbove(indent4, 0.0f);
		StyleConstants.setSpaceBelow(indent4, 0.0f);

		Style indent5 = addStyle(ParagraphStyle.INDENT_5.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent5, 60);
		StyleConstants.setSpaceAbove(indent5, 0.0f);
		StyleConstants.setSpaceBelow(indent5, 0.0f);

		Style indent6 = addStyle(ParagraphStyle.INDENT_6.getName(), defaultStyle);
		StyleConstants.setLeftIndent(indent6, 72);
		StyleConstants.setSpaceAbove(indent6, 0.0f);
		StyleConstants.setSpaceBelow(indent6, 0.0f);

		Style spaceAbove = addStyle(ParagraphStyle.SPACE_ABOVE.getName(), defaultStyle);
		StyleConstants.setLeftIndent(spaceAbove, 0);
		StyleConstants.setSpaceAbove(spaceAbove, 4.0f);
		StyleConstants.setSpaceBelow(spaceAbove, 0.0f);

		Style spaceBelow = addStyle(ParagraphStyle.SPACE_BELOW.getName(), defaultStyle);
		StyleConstants.setLeftIndent(spaceBelow, 0);
		StyleConstants.setSpaceAbove(spaceBelow, 0.0f);
		StyleConstants.setSpaceBelow(spaceBelow, 4.0f);

		Style spaceAboveBelow = addStyle(ParagraphStyle.SPACE_ABOVE_BELOW.getName(), defaultStyle);
		StyleConstants.setLeftIndent(spaceAboveBelow, 0);
		StyleConstants.setSpaceAbove(spaceAboveBelow, 4.0f);
		StyleConstants.setSpaceBelow(spaceAboveBelow, 4.0f);

	}

}
