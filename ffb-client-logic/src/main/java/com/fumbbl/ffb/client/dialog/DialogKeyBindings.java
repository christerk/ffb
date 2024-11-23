package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;

public class DialogKeyBindings extends Dialog {

	private final String fontBoldOpen;
	private final String _FONT_BOLD_CLOSE = "</b></font>";
	private final String fontMediumBoldOpen;
	private final String fontOpen;
	private final String _FONT_CLOSE = "</font>";

	public DialogKeyBindings(FantasyFootballClient pClient) {

		super(pClient, "Key Bindings", true);

		fontBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider().scale(9, RenderContext.UI) + "px\"><b>";
		fontMediumBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider().scale(11, RenderContext.UI) + "px\"><b>";
		fontOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider().scale(9, RenderContext.UI) + "px\">";

		JScrollPane keyBindingsPane = new JScrollPane(createKeyBindingsEditorPane());
		keyBindingsPane.setPreferredSize(new Dimension(keyBindingsPane.getPreferredSize().width + dimensionProvider().scale(20, RenderContext.UI),
			dimensionProvider().scale(500, RenderContext.UI)));

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(keyBindingsPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		pack();

		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.KEY_BINDINGS;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JEditorPane createKeyBindingsEditorPane() {

		JEditorPane keyBindingsPane = new JEditorPane();
		keyBindingsPane.setEditable(false);
		keyBindingsPane.setContentType("text/html");

		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<body>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		addTableHeader(html, "Client Resizing", 3);
		addDescription(html, "Increase Size", IClientProperty.KEY_RESIZE_LARGER);
		addDescription(html, "Reset Size", IClientProperty.KEY_RESIZE_RESET);
		addDescriptionWithAlternativeProperty(html, "Decrease Size", IClientProperty.KEY_RESIZE_SMALLER, IClientProperty.KEY_RESIZE_SMALLER2);
		html.append("</table>\n");

		html.append("<br>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		addTableHeader(html, "Player Moves", 3);
		addDescriptionWithAlternative(html, "Move North", IClientProperty.KEY_PLAYER_MOVE_NORTH, "NUMPAD 8");
		addDescriptionWithAlternative(html, "Move Northeast", IClientProperty.KEY_PLAYER_MOVE_NORTHEAST, "NUMPAD 9");
		addDescriptionWithAlternative(html, "Move East", IClientProperty.KEY_PLAYER_MOVE_EAST, "NUMPAD 6");
		addDescriptionWithAlternative(html, "Move Southeast", IClientProperty.KEY_PLAYER_MOVE_SOUTHEAST, "NUMPAD 3");
		addDescriptionWithAlternative(html, "Move South", IClientProperty.KEY_PLAYER_MOVE_SOUTH, "NUMPAD 2");
		addDescriptionWithAlternative(html, "Move Southwest", IClientProperty.KEY_PLAYER_MOVE_SOUTHWEST, "NUMPAD 1");
		addDescriptionWithAlternative(html, "Move West", IClientProperty.KEY_PLAYER_MOVE_WEST, "NUMPAD 4");
		addDescriptionWithAlternative(html, "Move Northwest", IClientProperty.KEY_PLAYER_MOVE_NORTHWEST, "NUMPAD 7");
		html.append("</table>\n");

		html.append("<br>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		addTableHeader(html, "Player Selection", 2);
		addDescription(html, "Select Current Player", IClientProperty.KEY_PLAYER_SELECT);
		addDescription(html, "Select Cycle Left", IClientProperty.KEY_PLAYER_CYCLE_LEFT);
		addDescription(html, "Select Cycle Right", IClientProperty.KEY_PLAYER_CYCLE_RIGHT);
		html.append("</table>\n");

		html.append("<br>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		addTableHeader(html, "Player Actions", 2);
		addDescription(html, "Action Block", IClientProperty.KEY_PLAYER_ACTION_BLOCK);
		addDescription(html, "Action Blitz", IClientProperty.KEY_PLAYER_ACTION_BLITZ);
		addDescription(html, "Action Move", IClientProperty.KEY_PLAYER_ACTION_MOVE);
		addDescription(html, "Action Foul", IClientProperty.KEY_PLAYER_ACTION_FOUL);
		addDescription(html, "Action Stand Up", IClientProperty.KEY_PLAYER_ACTION_STAND_UP);
		addDescription(html, "Action Hand Over", IClientProperty.KEY_PLAYER_ACTION_HAND_OVER);
		addDescription(html, "Action Pass", IClientProperty.KEY_PLAYER_ACTION_PASS);
		addDescription(html, "Action Stab", IClientProperty.KEY_PLAYER_ACTION_STAB);
		addDescription(html, "Action Gaze", IClientProperty.KEY_PLAYER_ACTION_GAZE);
		addDescription(html, "Action End Move", IClientProperty.KEY_PLAYER_ACTION_END_MOVE);

		addDescription(html, "Action Stab", IClientProperty.KEY_PLAYER_ACTION_STAB);
		addDescription(html, "Action Chainsaw", IClientProperty.KEY_PLAYER_ACTION_CHAINSAW);
		addDescription(html, "Action Projectile Vomit", IClientProperty.KEY_PLAYER_ACTION_PROJECTILE_VOMIT);
		addDescription(html, "Action Gaze", IClientProperty.KEY_PLAYER_ACTION_GAZE);
		addDescription(html, "Action Fumblerooskie", IClientProperty.KEY_PLAYER_ACTION_FUMBLEROOSKIE);
		addDescription(html, "Action Range Grid", IClientProperty.KEY_PLAYER_ACTION_RANGE_GRID);
		addDescription(html, "Action Hail Mary Pass", IClientProperty.KEY_PLAYER_ACTION_HAIL_MARY_PASS);
		addDescription(html, "Action Multiple Block", IClientProperty.KEY_PLAYER_ACTION_MULTIPLE_BLOCK);
		addDescription(html, "Action Frenzied Rush", IClientProperty.KEY_PLAYER_ACTION_FRENZIED_RUSH);
		addDescription(html, "Action Shot to Nothing", IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING);
		addDescription(html, "Action Shot to Nothing Bomb", IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING_BOMB);
		addDescription(html, "Action Treacherous", IClientProperty.KEY_PLAYER_ACTION_TREACHEROUS);
		addDescription(html, "Action Wisdom White Dwarf", IClientProperty.KEY_PLAYER_ACTION_WISDOM);
		addDescription(html, "Action Beer Barrel Bash", IClientProperty.KEY_PLAYER_ACTION_BEER_BARREL_BASH);
		html.append("</table>\n");

		html.append("<br>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		addTableHeader(html, "Toolbar &amp; Menu Shortcuts", 2);
		addDescription(html, "End Turn", IClientProperty.KEY_TOOLBAR_TURN_END);
		addDescription(html, "Load Team Setup", IClientProperty.KEY_MENU_SETUP_LOAD);
		addDescription(html, "Save Team Setup", IClientProperty.KEY_MENU_SETUP_SAVE);
		html.append("</table>\n");

		html.append("<br>\n");

		html.append("<table border=\"1\" cellspacing=\"0\">\n");
		html.append("<tr>\n");
		html.append("  <td>").append(fontMediumBoldOpen).append("Dialogs").append(_FONT_BOLD_CLOSE).append("</td>\n");
		html.append("</tr>\n");
		html.append("<tr>\n");
		html.append("  <td>").append(fontOpen).append(
				"In all dialogs buttons can be activated by the first<br>letter of their label. So &lt;Y&gt; for Yes or &lt;N&gt; for No.<br>Block dices are numbered 1, 2, 3 from left to right<br>and can be activated this way.")
			.append(_FONT_CLOSE).append("</td>\n");
		html.append("</tr>\n");
		html.append("</table>\n");
		html.append("</body>\n");
		html.append("</html>");

		keyBindingsPane.setText(html.toString());
		keyBindingsPane.setCaretPosition(0);

		return keyBindingsPane;

	}

	private void addDescription(StringBuilder html, String text, String property) {
		html.append("<tr>\n");
		html.append("  <td>").append(fontOpen).append(text).append(_FONT_CLOSE).append("</td>\n");
		html.append("  <td>").append(fontBoldOpen).append(getClient().getProperty(property))
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		html.append("</tr>\n");
	}

	@SuppressWarnings("SameParameterValue")
	private void addDescriptionWithAlternativeProperty(StringBuilder html, String text, String property, String alternateProperty) {
		addDescriptionWithAlternative(html, text, property, getClient().getProperty(alternateProperty));
	}

	private void addDescriptionWithAlternative(StringBuilder html, String text, String property, String key) {
		html.append("<tr>\n");
		html.append("  <td>").append(fontOpen).append(text).append(_FONT_CLOSE).append("</td>\n");
		html.append("  <td>").append(fontBoldOpen).append(getClient().getProperty(property))
			.append(_FONT_BOLD_CLOSE).append("</td>\n");
		html.append("  <td>").append(fontBoldOpen).append(key).append(_FONT_BOLD_CLOSE).append("</td>\n");
		html.append("</tr>\n");
	}

	private void addTableHeader(StringBuilder html, String header, int colspan) {
		html.append("<tr>\n");
		html.append("  <td colspan=\"").append(colspan).append("\">").append(fontMediumBoldOpen).append(header).append(_FONT_BOLD_CLOSE)
			.append("</td>\n");
		html.append("</tr>\n");
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		// Dimension menuBarSize = getClient().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}

}
