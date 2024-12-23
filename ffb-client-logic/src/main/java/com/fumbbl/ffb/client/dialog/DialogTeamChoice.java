package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.TeamList;
import com.fumbbl.ffb.TeamListEntry;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JTable;
import com.fumbbl.ffb.client.util.UtilClientJTable;
import com.fumbbl.ffb.client.util.UtilClientReflection;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DialogTeamChoice extends Dialog {

	private final TeamList fTeamList;
	private int fSelectedIndex;
	private final JTable fTable;

	private final JButton fButtonOk;

	public DialogTeamChoice(FantasyFootballClient pClient, TeamList pTeamList) {

		super(pClient, "Select Team", false);

		fTeamList = pTeamList;

		String[] columnNames = new String[] { "Div", "Team Name", "Race", "TV", "Treasury" };
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, fTeamList.size()) {
			public Class<?> getColumnClass(int pColumnIndex) {
				Object o = getValueAt(0, pColumnIndex);
				if (o == null) {
					return Object.class;
				} else {
					return o.getClass();
				}
			}
		};
		TeamListEntry[] teamListEntries = fTeamList.getTeamListEntries();
		for (int i = 0; i < teamListEntries.length; i++) {
			String division = teamListEntries[i].getDivision();
			if ("1".equals(division)) {
				division = "[R]";
			}
			if ("5".equals(division)) {
				division = "[L]";
			}
			if ("7".equals(division)) {
				division = "[A]";
			}
			if ("8".equals(division)) {
				division = "[U]";
			}
			if ("9".equals(division)) {
				division = "[F]";
			}
			if ("10".equals(division)) {
				division = "[B]";
			}
			tableModel.setValueAt(division, i, 0);
			tableModel.setValueAt(teamListEntries[i].getTeamName(), i, 1);
			tableModel.setValueAt(teamListEntries[i].getRace(), i, 2);
			tableModel.setValueAt(StringTool.formatThousands(teamListEntries[i].getTeamValue() / 1000) + "k", i, 3);
			tableModel.setValueAt(StringTool.formatThousands(teamListEntries[i].getTreasury() / 1000) + "k", i, 4);
		}

		fTable = new JTable(dimensionProvider(), tableModel);
		UtilClientReflection.setFillsViewportHeight(fTable, true);
		UtilClientReflection.setAutoCreateRowSorter(fTable, true);
		fTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fTable.getTableHeader().setReorderingAllowed(false);
		fTable.getColumnModel().getColumn(0).setCellRenderer(new MyTableCellRenderer(SwingConstants.RIGHT));
		fTable.getColumnModel().getColumn(3).setCellRenderer(new MyTableCellRenderer(SwingConstants.RIGHT));
		fTable.getColumnModel().getColumn(4).setCellRenderer(new MyTableCellRenderer(SwingConstants.RIGHT));
		fTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				int viewRow = fTable.getSelectedRow();
				if (viewRow < 0) {
					fButtonOk.setEnabled(false);
				} else {
					fButtonOk.setEnabled(true);
					fSelectedIndex = UtilClientReflection.convertRowIndexToModel(fTable, viewRow);
				}
			}
		});

		for (int column = 0; column < fTable.getColumnCount(); column++) {
			UtilClientJTable.packTableColumn(fTable, column, 5);
		}

		// int height = fTable.getTableHeader().getHeight();
		int nrOfVisibleRows = Math.min(10, fTeamList.size());
		int height = (nrOfVisibleRows + 1) * fTable.getRowHeight();
		fTable.setPreferredScrollableViewportSize(new Dimension(fTable.getPreferredSize().width, height));

		KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Action enterKeyAction = new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				String actionCommand = ae.getActionCommand();
				if (actionCommand.equals("EnterKey")) {
					checkAndCloseDialog(false);
				}
			}
		};
		InputMap inputMap = fTable.getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.remove(enterKeyStroke);
		fTable.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
		fTable.unregisterKeyboardAction(enterKeyStroke);
		fTable.registerKeyboardAction(enterKeyAction, "EnterKey", enterKeyStroke, JComponent.WHEN_FOCUSED);

		JScrollPane scrollPane;
		if (nrOfVisibleRows < fTeamList.size()) {
			scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		} else {
			scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}
		scrollPane.setViewportView(fTable);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 1));
		inputPanel.add(scrollPane);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		JButton fButtonCancel = new JButton(dimensionProvider(), "Cancel");
		fButtonCancel.addActionListener(pActionEvent -> {
			fSelectedIndex = -1;
			checkAndCloseDialog(true);
		});

		fButtonOk = new JButton(dimensionProvider(), "Play");
		fButtonOk.addActionListener(pActionEvent -> checkAndCloseDialog(false));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		buttonPanel.add(fButtonOk);
		buttonPanel.add(fButtonCancel);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inputPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setLocationToCenter();

	}

	private static class MyTableCellRenderer extends DefaultTableCellRenderer {
		public MyTableCellRenderer(int pHorizontalAlignment) {
			super();
			setHorizontalAlignment(pHorizontalAlignment);
		}

		@SuppressWarnings("unused")
		public Component getTableCellRendererComponent(JTable pTable, Object pValue, boolean pIsSelected, boolean pHasFocus,
													   int pRow, int pColumn) {
			return super.getTableCellRendererComponent(pTable, pValue, pIsSelected, false, pRow, pColumn);
		}
	}

	public void showDialog(IDialogCloseListener pCloseListener) {
		fTable.getSelectionModel().setSelectionInterval(0, 0);
		super.showDialog(pCloseListener);
	}

	private void checkAndCloseDialog(boolean pCancelSelected) {
		if (pCancelSelected || (fSelectedIndex >= 0)) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public DialogId getId() {
		return DialogId.TEAM_CHOICE;
	}

	public TeamListEntry getSelectedTeamEntry() {
		if (fSelectedIndex >= 0) {
			return fTeamList.getTeamListEntries()[fSelectedIndex];
		} else {
			return null;
		}
	}

}