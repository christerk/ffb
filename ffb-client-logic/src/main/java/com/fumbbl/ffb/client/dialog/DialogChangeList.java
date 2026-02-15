package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.model.ChangeList;
import com.fumbbl.ffb.client.model.VersionChangeList;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DialogChangeList extends Dialog {

	public DialogChangeList(FantasyFootballClient pClient) {

		super(pClient, "What's new?", true);

		JScrollPane mainPane = new JScrollPane(createEditorPane());

		Dimension clientDimension = getClient().getUserInterface().getSize();
		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();
		int offset = dimensionProvider.scale(150);
		mainPane.setPreferredSize(new Dimension(clientDimension.width - offset, clientDimension.height - offset));

		dimensionProvider.scaleFont(this);

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
		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();

		JEditorPane contentPane = new JEditorPane();
		contentPane.setEditable(false);
		contentPane.setContentType("text/html");

		String versionsText = ChangeList.INSTANCE.getVersions().stream()
			.filter(VersionChangeList::hasEntries)
			.map(this::renderVersionChanges)
			.collect(Collectors.joining());

		String info = "<div style=\"font-size:" + dimensionProvider.scale(11) + "px;padding-bottom:10px\">List of changes in recent versions. Only shows once at startup for each version, can be displayed again from the Help menu.</div>" +
			"<div style=\"font-size:" + dimensionProvider.scale(11) + "px;padding-bottom:" + dimensionProvider.scale(10) + "px\">Unless stated otherwise the changes apply to 2025 rules.</div>";

		contentPane.setText("<div style=\"padding:" + dimensionProvider.scale(10) + "px\"><div>" + info + "</div><div>" + versionsText + "</div></div>");
		contentPane.setCaretPosition(0);

		return contentPane;

	}

	private String renderVersionChanges(VersionChangeList list) {
		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();
		StringBuilder builder = new StringBuilder();

		builder.append("<font face=\"Sans Serif\" style=\"font-size:").append(dimensionProvider.scale(18)).append("px\"> <b>").append(list.getVersion()).append("</b> </font><br/>");

		if (list.hasDescription()) {
			builder.append("<div style=\"font-size:").append(dimensionProvider.scale(11)).append("px\">").append(list.getDescription()).append("</div>");
		}

		if (list.hasBehaviorChanges()) {
			builder.append(renderEntries("Behavior Changes", list.getBehaviorChanges()));
		}

		if (list.hasFeatures()) {
			builder.append(renderEntries("Features", list.getFeatures()));
		}

		if (list.hasRuleChanges()) {
			builder.append(renderEntries("Rule Changes", list.getRuleChanges()));
		}

		if (list.hasImprovements()) {
			builder.append(renderEntries("Improvements", list.getImprovements()));
		}

		if (list.hasBugfixes()) {
			builder.append(renderEntries("Bugfixes", list.getBugfixes()));
		}

		if (list.hasRemovals()) {
			builder.append(renderEntries("Removals", list.getRemovals()));
		}

		return builder.toString();
	}

	private String renderEntries(String title, List<String> entries) {
		DimensionProvider dimensionProvider = getClient().getUserInterface().getUiDimensionProvider();
		StringBuilder builder = new StringBuilder();

		builder.append("<font face=\"Sans Serif\" style=\"font-size:").append(dimensionProvider.scale(14)).append("px\"> <b>").append(title).append("</b> </font>").append("<ul>");

		entries.stream().map(entry -> "<li style=\"font-size:" + dimensionProvider.scale(11) + "px;padding-bottom::" + dimensionProvider.scale(3) + "px;list-style-type:decimal\">" + entry + "</li>").forEach(builder::append);

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
