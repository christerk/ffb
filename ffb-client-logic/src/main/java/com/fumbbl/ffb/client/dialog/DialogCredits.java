package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Dialog showing third-party credits and licenses.
 *
 * Renders an table of credit entries with links to license text
 * (shown via DialogLicense) and external project homepages.
 *
 * @author Garcangel
 */
public class DialogCredits extends Dialog {

	private final List<CreditEntry> entries = new ArrayList<>();

	public DialogCredits(FantasyFootballClient client) {
		super(client, "Credits", true);

		DimensionProvider dimensionProvider = client.getUserInterface().getUiDimensionProvider();

		addCredits();

		JScrollPane scrollPane = new JScrollPane(createEditorPane());
		scrollPane.setPreferredSize(new Dimension(
			dimensionProvider.scale(500),
			dimensionProvider.scale(400))
		);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(scrollPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(panel);

		pack();
		setLocationToCenter();
	}

	private void addCredits() {
		entries.add(new CreditEntry(
			"Noto Emoji",
			"SIL Open Font License 1.1",
			"https://github.com/googlefonts/noto-emoji",
			"/licenses/NotoEmoji-OFL.txt"
		));
	}

	private JEditorPane createEditorPane() {

		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();
		String fontOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(9) + "px\">";
		String fontBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(9) + "px\"><b>";
		String fontMediumBoldOpen = "<font face=\"Sans Serif\" style=\"font-size:" + dimensionProvider.scale(11) + "px\"><b>";
		String fontClose = "</font>";
		String fontBoldClose = "</b></font>";

		JEditorPane pane = new JEditorPane();
		pane.setEditable(false);
		pane.setContentType("text/html");

		StringBuilder html = new StringBuilder();
		html.append("<html><body>");

		// Heading
		html.append(fontMediumBoldOpen).append("Credits").append(fontBoldClose);
		html.append("<br>");

		// Intro text
		html.append("<p>").append(fontOpen)
		    .append("This software includes third-party components. Their licenses and homepages are listed below.")
		    .append(fontClose).append("</p><br>");

		// Table of credits
		html.append("<table border=\"0\" cellspacing=\"2\" width=\"100%\">");
		for (CreditEntry entry : entries) {
			html.append("<tr><td>")
				.append(fontBoldOpen).append(entry.name).append(fontBoldClose)
				.append(" &nbsp; <a href=\"license://").append(entry.name).append("\">show license</a>")
				.append(" - <a href=\"").append(entry.homepageUrl).append("\">homepage</a>")
				.append("</td></tr>");
		}
		html.append("</table>");

		html.append("</body></html>");

		pane.setText(html.toString());
		pane.setCaretPosition(0);

		pane.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				String desc = e.getDescription();
				if (desc.startsWith("license://")) {
					String name = desc.substring("license://".length());
					CreditEntry entry = findEntry(name);
					if (entry != null) {
						getClient().getUserInterface().showDialog(new DialogLicense(getClient(), entry), null);
					}
				} else if (desc.startsWith("http")) {
					try {
						Desktop.getDesktop().browse(new URI(desc));
					} catch (IOException | URISyntaxException ex) {
						getClient().logWithOutGameId(ex);
					}
				}
			}
		});

		return pane;
	}

	private CreditEntry findEntry(String name) {
		for (CreditEntry e : entries) {
			if (e.name.equals(name)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public DialogId getId() {
		return DialogId.CREDITS;
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}
}
