package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DialogSelectLocalStoredProperties extends Dialog implements ActionListener {

	private final CommonPropertyCheckList fList;
	private final JButton fButtonSelect;
	private final JButton fButtonCancel;
	private List<CommonProperty> selectedProperties;

	public DialogSelectLocalStoredProperties(FantasyFootballClient client) {

		super(client, "Locally Stored Properties", false);

		fButtonSelect = new JButton(dimensionProvider(), "Select");
		fButtonSelect.setToolTipText("Select the checked properties");
		fButtonSelect.addActionListener(this);
		fButtonSelect.setMnemonic((int) 'S');
		fButtonSelect.setEnabled(true);

		fButtonCancel = new JButton(dimensionProvider(), "Cancel");
		fButtonCancel.setToolTipText("Do not change selection");
		fButtonCancel.addActionListener(this);
		fButtonCancel.setMnemonic((int) 'C');

		List<CommonProperty> properties = Arrays.stream(CommonProperty._SAVED_USER_SETTINGS)
			.filter(property -> !property.isStoredRemote()).collect(Collectors.toList());

		fList = new CommonPropertyCheckList(dimensionProvider(), properties, client.getLocallyStoredPropertyKeys());
		fList.setVisibleRowCount(Math.min(properties.size(), 20));

		JScrollPane listScroller = new JScrollPane(fList);
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		JLabel headerLabel = new JLabel(dimensionProvider(), "Select properties to be stored locally only");
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
		buttonPanel.add(fButtonSelect);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(fButtonCancel);

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

		fList.setSelectedIndex(0);
	}

	public DialogId getId() {
		return DialogId.STORE_PROPERTIES_LOCAL;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonCancel) {
			selectedProperties = null;
			closeDialog();
		}
		if (pActionEvent.getSource() == fButtonSelect) {
			selectedProperties = fList.getSelectedProperties();
			closeDialog();
		}
	}

	private void closeDialog() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public List<CommonProperty> getSelectedProperties() {
		return selectedProperties;
	}

}
