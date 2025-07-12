package com.fumbbl.ffb.client.dialog;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class DebugDialog extends JInternalFrame {


	public DebugDialog() {
		super("Debug Dialog");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (int i = 0; i< 5; i++) {
			panel.add(new JComboBox<>(new Integer[]{1, 2, 3, 4, 5}));
		}
		validate();
		pack();
	}

	public void showDialog(JDesktopPane desktopPane) {
		desktopPane.add(this);
		setVisible(true);
		moveToFront();
	}

}
