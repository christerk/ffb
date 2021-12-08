package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.model.ChangeList;
import com.fumbbl.ffb.client.model.VersionChangeList;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

public class DialogChangeList extends Dialog {

	public DialogChangeList(FantasyFootballClient pClient) {

		super(pClient, "What's new?", true);

		JScrollPane mainPane = new JScrollPane(createEditorPane());

		Dimension clientDimension = getClient().getUserInterface().getSize();
		mainPane.setPreferredSize(new Dimension(clientDimension.width - 150, clientDimension.height - 150));

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.add(mainPane);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(infoPanel);

		pack();

		setLocationToCenter();

	}

	public DialogId getId() {
		return DialogId.CHANGE_LIST;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private JEditorPane createEditorPane() {

		JEditorPane contentPane = new JEditorPane();
		contentPane.setEditable(false);
		contentPane.setContentType("text/html");

		String text = new ChangeList().getVersions().stream()
			.filter(VersionChangeList::hasEntries)
			.map(this::renderVersionChanges)
			.collect(Collectors.joining());

		contentPane.setText(text);
		contentPane.setCaretPosition(0);

		return contentPane;

	}

	private String renderVersionChanges(VersionChangeList list) {
		StringBuilder builder = new StringBuilder();

		builder.append("<h1>").append(list.getVersion()).append("</h1>");

		if (list.hasFeatures()) {
			builder.append(renderEntries("Features", list.getFeatures()));
		}

		if (list.hasImprovements()) {
			builder.append(renderEntries("Improvements", list.getImprovements()));
		}

		if (list.hasBugfixes()) {
			builder.append(renderEntries("Bugfixes", list.getBugfixes()));
		}

		return builder.toString();
	}

	private String renderEntries(String title, List<String> entries) {
		StringBuilder builder = new StringBuilder();

		builder.append("<h2>").append(title).append("</h2>").append("<ul>");

		entries.stream().map(entry -> "<li>" + entry + "</li>").forEach(builder::append);

		builder.append("</ul>");
		return builder.toString();
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		Dimension menuBarSize = getClient().getUserInterface().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2,
			((frameSize.height - dialogSize.height) / 2) - menuBarSize.height);
	}

}
