package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JProgressBar;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kalimar
 */
public class DialogProgressBar extends Dialog implements ActionListener {

	private final JLabel fMessageLabel;
	private final JProgressBar fProgressBar;

	public DialogProgressBar(FantasyFootballClient pClient, String pTitle) {
		this(pClient, pTitle, 0, 0);
	}

	public DialogProgressBar(FantasyFootballClient pClient, String pTitle, int pMinValue, int pMaxValue) {

		super(pClient, pTitle, false);

		JButton fButton = new JButton(dimensionProvider(), "Cancel");
		fButton.addActionListener(this);

		fProgressBar = new JProgressBar(dimensionProvider(), pMinValue, pMaxValue);
		fProgressBar.setValue(pMinValue);
		fProgressBar.setStringPainted(true);

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
		fMessageLabel = new JLabel(dimensionProvider(), "Initializing.");
		messagePanel.add(fMessageLabel);
		messagePanel.add(Box.createHorizontalGlue());
		messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
		progressPanel.add(fProgressBar);
		progressPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(messagePanel);
		getContentPane().add(progressPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();

	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public DialogId getId() {
		return DialogId.PROGRESS_BAR;
	}

	public int getMinimum() {
		return fProgressBar.getMinimum();
	}

	public void setMinimum(final int pMinimum) {
		if (pMinimum != getMinimum()) {
			if (SwingUtilities.isEventDispatchThread()) {
				fProgressBar.setMinimum(pMinimum);
			} else {
				try {
					SwingUtilities.invokeAndWait(() -> fProgressBar.setMinimum(pMinimum));
				} catch (InterruptedException | InvocationTargetException ignored) {
				}
			}
		}
	}

	public int getMaximum() {
		return fProgressBar.getMaximum();
	}

	public void setMaximum(final int pMaximum) {
		if (pMaximum != getMaximum()) {
			if (SwingUtilities.isEventDispatchThread()) {
				fProgressBar.setMaximum(pMaximum);
			} else {
				try {
					SwingUtilities.invokeAndWait(() -> fProgressBar.setMaximum(pMaximum));
				} catch (InterruptedException | InvocationTargetException ignored) {
				}
			}
		}
	}

	public void updateProgress(final int pProgress, final String pMessage) {
		if (SwingUtilities.isEventDispatchThread()) {
			fProgressBar.setValue(pProgress);
			if (StringTool.isProvided(pMessage)) {
				fMessageLabel.setText(pMessage);
			}
		} else {
			try {
				SwingUtilities.invokeAndWait(() -> {
					fProgressBar.setValue(pProgress);
					if (StringTool.isProvided(pMessage)) {
						fMessageLabel.setText(pMessage);
					}
				});
			} catch (InterruptedException | InvocationTargetException ignored) {
			}
		}
	}

}
