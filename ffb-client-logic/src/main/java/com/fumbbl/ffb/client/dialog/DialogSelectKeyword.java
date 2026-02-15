package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Keyword;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DialogSelectKeyword extends Dialog implements ActionListener {

	private final KeywordCheckList list;
	private final JButton buttonSelect;
	private final JButton buttonCancel;
	private final int minSelects;
	private Keyword[] selectedKeywords;

	public DialogSelectKeyword(FantasyFootballClient client, String header, List<Keyword> keywords,
	                         int minSelects, int maxSelects, boolean preSelected) {

		super(client, "Keyword Choice", false);
		this.minSelects = minSelects;

		buttonSelect = new JButton(dimensionProvider(), "Select");
		buttonSelect.setToolTipText("Select the checked keyword(s)");
		buttonSelect.addActionListener(this);
		buttonSelect.setMnemonic((int) 'S');
		buttonSelect.setEnabled((keywords.size() == 1) || preSelected);

		buttonCancel = new JButton(dimensionProvider(), "Cancel");
		buttonCancel.setToolTipText("Do not select any keyword");
		buttonCancel.addActionListener(this);
		buttonCancel.setMnemonic((int) 'C');

		list = new KeywordCheckList(dimensionProvider(), keywords, minSelects, maxSelects, preSelected, buttonSelect);
		list.setVisibleRowCount(Math.min(keywords.size(), 5));

		JScrollPane listScroller = new JScrollPane(list);
		// listScroller.setPreferredSize(new Dimension(200, 100));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		JLabel headerLabel = new JLabel(dimensionProvider(), header);
		headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, headerLabel.getFont().getSize()));
		headerPanel.add(headerLabel);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.add(listScroller);
		listPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(buttonSelect);
		if (minSelects == 0) {
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(buttonCancel);
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
		centerPane.add(headerPanel);
		centerPane.add(listPanel);
		centerPane.add(buttonPanel);

		getContentPane().add(centerPane, BorderLayout.CENTER);

		pack();
		setLocationToCenter();

		addMouseListener(this);

		list.setSelectedIndex(0);
	}

	public DialogId getId() {
		return DialogId.SELECT_KEYWORD;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == buttonCancel) {
			selectedKeywords = new Keyword[0];
			closeDialog();
		}
		if (pActionEvent.getSource() == buttonSelect) {
			selectedKeywords = list.getSelectedKeywords();
			if (selectedKeywords.length >= minSelects) {
				closeDialog();
			}
		}
	}

	private void closeDialog() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public Keyword[] getSelectedKeywords() {
		return selectedKeywords;
	}

}
