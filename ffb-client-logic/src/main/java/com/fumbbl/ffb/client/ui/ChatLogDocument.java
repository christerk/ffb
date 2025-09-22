package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.*;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * DefaultDocument subclass that supports batching inserts.
 */
public class ChatLogDocument extends DefaultStyledDocument {

	public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	public static final String DEFAULT_FONT_FAMILY = "Arial";

	public static final int CHAT_FONT_BASE_SIZE = 12;
	public static final int CHAT_FONT_BASE_SIZE_LARGE = 14;

	private final Style defaultStyle;

	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;

	public ChatLogDocument(StyleProvider styleProvider, DimensionProvider dimensionProvider) {
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;

		defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		setStyles();

	}

	public void setStyles() {

		Enumeration<?> styles = getStyleNames();

		List<String> names = new ArrayList<>();

		while (styles.hasMoreElements()) {
			Object style = styles.nextElement();
			if (style instanceof String) {
				names.add((String) style);
			}
		}

		names.forEach(this::removeStyle);

		// initStyles

		int defaultFontSize = dimensionProvider.scale(CHAT_FONT_BASE_SIZE);
		int largerSize = dimensionProvider.scale(CHAT_FONT_BASE_SIZE_LARGE);

		StyleConstants.setFontFamily(defaultStyle, DEFAULT_FONT_FAMILY);

		Style text = addStyle(TextStyle.NONE.getName(), defaultStyle);
		StyleConstants.setFontSize(text, defaultFontSize);
		StyleConstants.setForeground(text, styleProvider.getText());

		Style bold = addStyle(TextStyle.BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(bold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(bold, defaultFontSize);
		StyleConstants.setBold(bold, true);
		StyleConstants.setForeground(bold, styleProvider.getText());

		Style home = addStyle(TextStyle.HOME.getName(), defaultStyle);
		StyleConstants.setFontFamily(home, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(home, defaultFontSize);
		StyleConstants.setForeground(home, styleProvider.getHome());

		Style homeBold = addStyle(TextStyle.HOME_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(homeBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(homeBold, defaultFontSize);
		StyleConstants.setForeground(homeBold, styleProvider.getHome());
		StyleConstants.setBold(homeBold, true);

		Style away = addStyle(TextStyle.AWAY.getName(), defaultStyle);
		StyleConstants.setFontFamily(away, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(away, defaultFontSize);
		StyleConstants.setForeground(away, styleProvider.getAway());

		Style awayBold = addStyle(TextStyle.AWAY_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(awayBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(awayBold, defaultFontSize);
		StyleConstants.setForeground(awayBold, styleProvider.getAway());
		StyleConstants.setBold(awayBold, true);

		Style roll = addStyle(TextStyle.ROLL.getName(), defaultStyle);
		StyleConstants.setFontFamily(roll, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(roll, defaultFontSize);
		StyleConstants.setBold(roll, true);
		StyleConstants.setForeground(roll, styleProvider.getText());

		Style neededRoll = addStyle(TextStyle.NEEDED_ROLL.getName(), defaultStyle);
		StyleConstants.setFontFamily(neededRoll, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(neededRoll, defaultFontSize);
		StyleConstants.setForeground(neededRoll, styleProvider.getText());

		Style explanation = addStyle(TextStyle.EXPLANATION.getName(), defaultStyle);
		StyleConstants.setItalic(explanation, true);
		StyleConstants.setFontFamily(explanation, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(explanation, defaultFontSize);
		StyleConstants.setForeground(explanation, styleProvider.getText());

		Style spectator = addStyle(TextStyle.SPECTATOR.getName(), defaultStyle);
		StyleConstants.setFontFamily(spectator, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(spectator, defaultFontSize);
		StyleConstants.setForeground(spectator, styleProvider.getSpec());

		Style spectatorBold = addStyle(TextStyle.SPECTATOR_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(spectatorBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(spectatorBold, defaultFontSize);
		StyleConstants.setForeground(spectatorBold, styleProvider.getSpec());
		StyleConstants.setBold(spectatorBold, true);

		Style admin = addStyle(TextStyle.ADMIN.getName(), defaultStyle);
		StyleConstants.setFontFamily(admin, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(admin, defaultFontSize);
		StyleConstants.setForeground(admin, styleProvider.getAdmin());

		Style adminBold = addStyle(TextStyle.ADMIN_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(adminBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(adminBold, defaultFontSize);
		StyleConstants.setForeground(adminBold, styleProvider.getAdmin());
		StyleConstants.setBold(adminBold, true);

		Style dev = addStyle(TextStyle.DEV.getName(), defaultStyle);
		StyleConstants.setFontFamily(dev, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(dev, defaultFontSize);
		StyleConstants.setForeground(dev, styleProvider.getDev());

		Style devBold = addStyle(TextStyle.DEV_BOLD.getName(), defaultStyle);
		StyleConstants.setFontFamily(devBold, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(devBold, defaultFontSize);
		StyleConstants.setForeground(devBold, styleProvider.getDev());
		StyleConstants.setBold(devBold, true);

		Style turn = addStyle(TextStyle.TURN.getName(), defaultStyle);
		StyleConstants.setFontFamily(turn, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turn, largerSize);
		StyleConstants.setBold(turn, true);
		StyleConstants.setForeground(turn, styleProvider.getText());

		Style turnHome = addStyle(TextStyle.TURN_HOME.getName(), defaultStyle);
		StyleConstants.setFontFamily(turnHome, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turnHome, largerSize);
		StyleConstants.setBold(turnHome, true);
		StyleConstants.setForeground(turnHome, styleProvider.getHome());

		Style turnAway = addStyle(TextStyle.TURN_AWAY.getName(), defaultStyle);
		StyleConstants.setFontFamily(turnAway, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(turnAway, largerSize);
		StyleConstants.setBold(turnAway, true);
		StyleConstants.setForeground(turnAway, styleProvider.getAway());
		
		Style mention = addStyle(TextStyle.MENTION.getName(), defaultStyle);
		StyleConstants.setFontFamily(mention, DEFAULT_FONT_FAMILY);
		StyleConstants.setFontSize(mention, defaultFontSize);
		StyleConstants.setBold(mention, true);
		StyleConstants.setForeground(mention, styleProvider.getText());
		StyleConstants.setBackground(mention, styleProvider.getMentionBackground());

		// init indent

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

		Style chatBody = addStyle(ParagraphStyle.CHAT_BODY.getName(), defaultStyle);
		StyleConstants.setLeftIndent(chatBody, 12f);
		StyleConstants.setFirstLineIndent(chatBody, -12f);
		StyleConstants.setSpaceAbove(chatBody, 0f);
		StyleConstants.setSpaceBelow(chatBody, 0f);


	}

}
