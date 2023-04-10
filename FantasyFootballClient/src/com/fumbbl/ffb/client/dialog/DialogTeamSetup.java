package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JTextField;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.ArrayTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kalimar
 */
public class DialogTeamSetup extends Dialog implements ActionListener, ListSelectionListener {

	public static final int CHOICE_LOAD = 1;
	public static final int CHOICE_SAVE = 2;
	public static final int CHOICE_CANCEL = 3;
	public static final int CHOICE_DELETE = 4;

	private final JButton fButtonLoadSave;
	private final JButton fButtonCancel;
	private final JButton fButtonDelete;
	private final JTextField fTextfieldSetupName;
	private final boolean fLoadDialog;
	private final JList<String> fSetupList;
	private int fUserChoice;
	private String fSetupName;

	public DialogTeamSetup(FantasyFootballClient pClient, boolean pLoadDialog, String[] pSetups) {

		super(pClient, (pLoadDialog ? "Load Team Setup" : "Save Team Setup"), false);

		fLoadDialog = pLoadDialog;

		if (isLoadDialog()) {
			fButtonLoadSave = new JButton(dimensionProvider(), "Load");
		} else {
			fButtonLoadSave = new JButton(dimensionProvider(), "Save");
		}
		fButtonLoadSave.addActionListener(this);

		fButtonCancel = new JButton(dimensionProvider(), "Cancel");
		fButtonCancel.addActionListener(this);

		BufferedImage deleteIcon = getClient().getUserInterface().getIconCache()
				.getIconByProperty(IIconProperty.GAME_DELETE);
		fButtonDelete = new JButton(dimensionProvider(), new ImageIcon(deleteIcon));
		fButtonDelete.addActionListener(this);

		DefaultListModel<String> fSetupListModel = new DefaultListModel<>();
		if (ArrayTool.isProvided(pSetups)) {
			for (String pSetup : pSetups) {
				fSetupListModel.addElement(pSetup);
			}
		}

		fSetupList = new JList<>(fSetupListModel);
		fSetupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fSetupList.setVisibleRowCount(Math.min(7, Math.max(3, pSetups.length)));
		fSetupList.addListSelectionListener(this);

		JScrollPane setupListScroller = new JScrollPane(fSetupList);
		setupListScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		listPanel.add(setupListScroller, BorderLayout.CENTER);
		listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButtonLoadSave);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(fButtonDelete);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(fButtonCancel);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		fTextfieldSetupName = new JTextField(dimensionProvider(), 20);
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
		editPanel.add(fTextfieldSetupName);
		editPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(listPanel);
		if (!isLoadDialog()) {
			getContentPane().add(editPanel);
		}
		getContentPane().add(buttonPanel);

		pack();
		int height = Math.max(getSize().height, 100);
		int width = Math.max(getSize().width, 200);
		setSize(width, height);

		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		hideDialog();
		if (pActionEvent.getSource() == fButtonLoadSave) {
			if (isLoadDialog()) {
				fUserChoice = CHOICE_LOAD;
			} else {
				fUserChoice = CHOICE_SAVE;
				fSetupName = fTextfieldSetupName.getText();
			}
		}
		if (pActionEvent.getSource() == fButtonCancel) {
			fUserChoice = CHOICE_CANCEL;
		}
		if (pActionEvent.getSource() == fButtonDelete) {
			fUserChoice = CHOICE_DELETE;
		}
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public void valueChanged(ListSelectionEvent pSelectionEvent) {
		if (!pSelectionEvent.getValueIsAdjusting()) {
			if (fSetupList.getSelectedIndex() == -1) {
				fButtonLoadSave.setEnabled(false);
			} else {
				fButtonLoadSave.setEnabled(true);
				fSetupName = fSetupList.getSelectedValue();
				fTextfieldSetupName.setText(fSetupName);
			}
		}
	}

	public DialogId getId() {
		return DialogId.TEAM_SETUP;
	}

	public int getUserChoice() {
		return fUserChoice;
	}

	public String getSetupName() {
		return fSetupName;
	}

	public boolean isLoadDialog() {
		return fLoadDialog;
	}

}
