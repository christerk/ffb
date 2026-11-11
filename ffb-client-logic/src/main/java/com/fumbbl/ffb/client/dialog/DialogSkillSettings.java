package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DialogSkillSettings extends Dialog implements ActionListener {

	public static final List<CommonProperty> SETTING_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
		CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN,
		CommonProperty.SETTING_DODGE_BLOCK));

	private final Map<CommonProperty, Map<String, String>> settingEntries;
	private final Map<CommonProperty, JComboBox<String>> settingBoxes = new LinkedHashMap<>();
	private final Map<CommonProperty, JLabel> settingLabels = new LinkedHashMap<>();
	private final Map<CommonProperty, Border> defaultBoxBorders = new LinkedHashMap<>();
	private final JButton saveButton;
	private final JButton cancelButton;
	private final boolean closeIfConfigured;
	private final Map<CommonProperty, String> selectedSettings = new LinkedHashMap<>();
	private boolean saved;

	public DialogSkillSettings(FantasyFootballClient client, Map<CommonProperty, Map<String, String>> settingEntries) {
		this(client, settingEntries, false);
	}

	public DialogSkillSettings(FantasyFootballClient client,
	                           Map<CommonProperty, Map<String, String>> settingEntries, boolean required) {
		super(client, "Skill settings", false);
		setBlockBackgroundInteraction(required);
		this.settingEntries = new LinkedHashMap<>();
		for (CommonProperty property : SETTING_PROPERTIES) {
			Map<String, String> entries = settingEntries.get(property);
			this.settingEntries.put(property, entries == null ? Collections.emptyMap() : new LinkedHashMap<>(entries));
		}
		closeIfConfigured = required && this.settingEntries.entrySet().stream().allMatch(entry ->
			isConfigured(entry.getValue(), client.getProperty(entry.getKey())));

		saveButton = new JButton(dimensionProvider(), "Save");
		saveButton.addActionListener(this);
		saveButton.setMnemonic((int) 'S');
		cancelButton = new JButton(dimensionProvider(), "Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic((int) 'C');

		JLabel sectionLabel = new JLabel(dimensionProvider(), "Re-Roll settings");
		sectionLabel.setFont(new Font(sectionLabel.getFont().getName(), Font.BOLD, sectionLabel.getFont().getSize()));
		sectionLabel.setHorizontalAlignment(JLabel.CENTER);

		for (Map.Entry<CommonProperty, Map<String, String>> entry : this.settingEntries.entrySet()) {
			CommonProperty property = entry.getKey();
			JComboBox<String> settingBox = createComboBox(entry.getValue(), client.getProperty(property));
			JLabel settingLabel = new JLabel(dimensionProvider(),
				client.getUserInterface().getGameMenuBar().menuName(property));
			settingBoxes.put(property, settingBox);
			settingLabels.put(property, settingLabel);
			defaultBoxBorders.put(property, settingBox.getBorder());
			settingBox.addActionListener(event -> updateSaveState());
		}

		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints sectionConstraints = new GridBagConstraints();
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 0;
		sectionConstraints.gridwidth = 2;
		sectionConstraints.fill = GridBagConstraints.HORIZONTAL;
		sectionConstraints.weightx = 1;
		settingsPanel.add(sectionLabel, sectionConstraints);
		int row = 1;
		for (CommonProperty property : settingLabels.keySet()) {
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridx = 0;
			labelConstraints.gridy = row;
			labelConstraints.anchor = GridBagConstraints.LINE_START;
			labelConstraints.insets = new Insets(5, 0, 5, 5);
			settingsPanel.add(settingLabels.get(property), labelConstraints);

			GridBagConstraints boxConstraints = new GridBagConstraints();
			boxConstraints.gridx = 1;
			boxConstraints.gridy = row;
			boxConstraints.anchor = GridBagConstraints.LINE_START;
			boxConstraints.fill = GridBagConstraints.HORIZONTAL;
			boxConstraints.weightx = 1;
			boxConstraints.insets = new Insets(5, 0, 5, 0);
			settingsPanel.add(settingBoxes.get(property), boxConstraints);
			row++;
		}
		settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(saveButton);
		if (!required) {
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(cancelButton);
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(settingsPanel);
		contentPanel.add(buttonPanel);
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		updateSaveState();

		pack();
		setLocationToCenter();
		addMouseListener(this);
	}

	private JComboBox<String> createComboBox(Map<String, String> entries, String selectedValue) {
		JComboBox<String> box = new JComboBox<>(dimensionProvider(), entries.values().toArray(new String[0]));
		box.setSelectedItem(entries.get(selectedValue));
		return box;
	}

	private boolean isConfigured(Map<String, String> entries, String value) {
		return value != null && entries.containsKey(value);
	}

	@Override
	public void showDialog(IDialogCloseListener closeListener) {
		if (closeIfConfigured) {
			if (closeListener != null) {
				closeListener.dialogClosed(this);
			}
			return;
		}
		super.showDialog(closeListener);
	}

	private void updateSaveState() {
		boolean allSettingsSet = !settingBoxes.isEmpty();
		for (Map.Entry<CommonProperty, JComboBox<String>> entry : settingBoxes.entrySet()) {
			boolean settingSet = entry.getValue().getSelectedIndex() >= 0;
			allSettingsSet &= settingSet;
			highlight(entry.getKey(), !settingSet);
		}
		saveButton.setEnabled(allSettingsSet);
	}

	private void highlight(CommonProperty property, boolean unset) {
		JComboBox<String> settingBox = settingBoxes.get(property);
		settingBox.setBorder(unset
			? BorderFactory.createLineBorder(Color.RED) : defaultBoxBorders.get(property));
	}

	private void closeDialog() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	private String selectedValue(Map<String, String> entries, JComboBox<String> comboBox) {
		String selectedLabel = comboBox.getSelectedItem();
		return entries.entrySet().stream().filter(entry -> entry.getValue().equals(selectedLabel))
			.map(Map.Entry::getKey).findFirst().orElse(null);
	}

	@Override
	public DialogId getId() {
		return DialogId.SKILL_SETTINGS;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == saveButton) {
			for (Map.Entry<CommonProperty, JComboBox<String>> entry : settingBoxes.entrySet()) {
				selectedSettings.put(entry.getKey(), selectedValue(settingEntries.get(entry.getKey()), entry.getValue()));
			}
			saved = selectedSettings.size() == settingEntries.size()
				&& selectedSettings.values().stream().allMatch(value -> value != null);
			if (saved) {
				closeDialog();
			}
		} else if (event.getSource() == cancelButton) {
			closeDialog();
		}
	}

	public String getSetting(CommonProperty property) {
		return selectedSettings.get(property);
	}

	public boolean isSaved() {
		return saved;
	}
}
