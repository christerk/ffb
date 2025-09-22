package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


/**
 * Dialog for displaying the full license text of a credit entry.
 *
 * Loads a bundled license resource into a scrollable text area and
 * shows it with monospace font scaling.
 *
 * @author Garcangel
 */
public class DialogLicense extends Dialog {

	public DialogLicense(FantasyFootballClient client, DialogCredits.CreditEntry entry) {
		super(client, entry.name + " License", true);

		DimensionProvider dimensionProvider = client.getUserInterface().getUiDimensionProvider();

		String licenseText = loadLicense(entry.licenseResource);

		JTextArea textArea = new JTextArea(licenseText);
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, dimensionProvider.scale(10)));

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(
			dimensionProvider.scale(500),
			dimensionProvider.scale(400))
		);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		pack();
		setLocationToCenter();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private String loadLicense(String resourcePath) {
		try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
			if (is == null) {
				return "License file not found: " + resourcePath;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				return reader.lines().collect(Collectors.joining("\n"));
			}
		} catch (Exception e) {
			return "Error loading license: " + e.getMessage();
		}
	}

	public DialogId getId() {
		return DialogId.CREDITS_LICENSE;
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}
}
