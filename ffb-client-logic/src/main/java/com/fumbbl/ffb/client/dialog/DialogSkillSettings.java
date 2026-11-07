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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class DialogSkillSettings extends Dialog implements ActionListener {

	private final Map<String, String> reRollBallAndChainEntries;
	private final Map<String, String> dodgeBlockEntries;
	private final JComboBox<String> reRollBallAndChainBox;
	private final JComboBox<String> dodgeBlockBox;
	private final JButton saveButton;
	private final JButton cancelButton;
	private final JLabel reRollBallAndChainLabel;
	private final JLabel dodgeBlockLabel;
	private final Color defaultLabelBackground;
	private String reRollBallAndChainSetting;
	private String dodgeBlockSetting;
	private boolean saved;

	public DialogSkillSettings(FantasyFootballClient client, Map<String, String> reRollBallAndChainEntries,
	                           Map<String, String> dodgeBlockEntries) {
		this(client, reRollBallAndChainEntries, dodgeBlockEntries, false);
	}

	public DialogSkillSettings(FantasyFootballClient client, Map<String, String> reRollBallAndChainEntries,
	                           Map<String, String> dodgeBlockEntries, boolean required) {
		super(client, "Skill settings", false);
		setBlockBackgroundInteraction(required);
		this.reRollBallAndChainEntries = reRollBallAndChainEntries;
		this.dodgeBlockEntries = dodgeBlockEntries;

		reRollBallAndChainBox = createComboBox(reRollBallAndChainEntries,
			client.getProperty(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN));
		dodgeBlockBox = createComboBox(dodgeBlockEntries, client.getProperty(CommonProperty.SETTING_DODGE_BLOCK));

		saveButton = new JButton(dimensionProvider(), "Save");
		saveButton.addActionListener(this);
		saveButton.setMnemonic((int) 'S');
		cancelButton = new JButton(dimensionProvider(), "Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setMnemonic((int) 'C');

		reRollBallAndChainLabel = new JLabel(dimensionProvider(),
			client.getUserInterface().getGameMenuBar().menuName(CommonProperty.SETTING_RE_ROLL_BALL_AND_CHAIN));
		dodgeBlockLabel = new JLabel(dimensionProvider(),
			client.getUserInterface().getGameMenuBar().menuName(CommonProperty.SETTING_DODGE_BLOCK));
		defaultLabelBackground = reRollBallAndChainLabel.getBackground();

		JLabel sectionLabel = new JLabel(dimensionProvider(), "Re-Roll settings");
		sectionLabel.setFont(new Font(sectionLabel.getFont().getName(), Font.BOLD, sectionLabel.getFont().getSize()));

		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		settingsPanel.add(sectionLabel);
		settingsPanel.add(createSettingRow(reRollBallAndChainLabel, reRollBallAndChainBox));
		settingsPanel.add(createSettingRow(dodgeBlockLabel, dodgeBlockBox));
		settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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

		reRollBallAndChainBox.addActionListener(event -> updateSaveState());
		dodgeBlockBox.addActionListener(event -> updateSaveState());
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

	private JPanel createSettingRow(JLabel label, JComboBox<String> comboBox) {
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.add(label);
		row.add(Box.createHorizontalStrut(5));
		row.add(comboBox);
		return row;
	}

	private void updateSaveState() {
		boolean reRollSet = reRollBallAndChainBox.getSelectedIndex() >= 0;
		boolean dodgeSet = dodgeBlockBox.getSelectedIndex() >= 0;
		highlight(reRollBallAndChainLabel, !reRollSet);
		highlight(dodgeBlockLabel, !dodgeSet);
		saveButton.setEnabled(reRollSet && dodgeSet);
	}

	private void highlight(JLabel label, boolean unset) {
		label.setOpaque(unset);
		label.setBackground(unset ? new Color(255, 235, 150) : defaultLabelBackground);
		label.setFont(label.getFont().deriveFont(unset ? Font.BOLD : Font.PLAIN));
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
			reRollBallAndChainSetting = selectedValue(reRollBallAndChainEntries, reRollBallAndChainBox);
			dodgeBlockSetting = selectedValue(dodgeBlockEntries, dodgeBlockBox);
			saved = reRollBallAndChainSetting != null && dodgeBlockSetting != null;
			if (saved) {
				closeDialog();
			}
		} else if (event.getSource() == cancelButton) {
			closeDialog();
		}
	}

	public String getReRollBallAndChainSetting() {
		return reRollBallAndChainSetting;
	}

	public String getDodgeBlockSetting() {
		return dodgeBlockSetting;
	}

	public boolean isSaved() {
		return saved;
	}
}
