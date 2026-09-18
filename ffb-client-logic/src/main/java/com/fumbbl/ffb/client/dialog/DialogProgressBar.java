package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.client.ui.swing.JProgressBar;
import com.fumbbl.ffb.client.util.UiDispatcher;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Kalimar
 */
public class DialogProgressBar extends Dialog implements ActionListener {

	private final JLabel fMessageLabel;
	private final JProgressBar fProgressBar;
	private final UiDispatcher uiDispatcher = new UiDispatcher();
	// progress updates are coalesced, background threads must never wait for the event dispatch thread here
	private final AtomicReference<Progress> pendingProgress = new AtomicReference<>();
	private final AtomicBoolean updateScheduled = new AtomicBoolean();
	private volatile int minimum;
	private volatile int maximum;

	public DialogProgressBar(FantasyFootballClient pClient, String pTitle) {
		this(pClient, pTitle, 0, 0);
	}

	public DialogProgressBar(FantasyFootballClient pClient, String pTitle, int pMinValue, int pMaxValue) {

		super(pClient, pTitle, false);

		JButton fButton = new JButton(dimensionProvider(), "Cancel");
		fButton.addActionListener(this);

		minimum = pMinValue;
		maximum = pMaxValue;
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
		return minimum;
	}

	public void setMinimum(final int pMinimum) {
		if (pMinimum != minimum) {
			minimum = pMinimum;
			uiDispatcher.runOnUiThreadWithoutWaiting(() -> fProgressBar.setMinimum(pMinimum));
		}
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(final int pMaximum) {
		if (pMaximum != maximum) {
			maximum = pMaximum;
			uiDispatcher.runOnUiThreadWithoutWaiting(() -> fProgressBar.setMaximum(pMaximum));
		}
	}

	public void updateProgress(final int pProgress, final String pMessage) {
		pendingProgress.set(new Progress(pProgress, pMessage));
		if (updateScheduled.compareAndSet(false, true)) {
			// progress updates come from background threads, so this coalesces multiple updates into one repaint,
			// callers on the event dispatch thread apply their update immediately instead
			uiDispatcher.runOnUiThreadWithoutWaiting(this::applyPendingProgress);
		}
	}

	private void applyPendingProgress() {
		updateScheduled.set(false);
		Progress progress = pendingProgress.getAndSet(null);
		if (progress == null) {
			return;
		}
		fProgressBar.setValue(progress.value);
		if (StringTool.isProvided(progress.message)) {
			fMessageLabel.setText(progress.message);
			// relayouting for every single step stalls the event dispatch thread, only do it when the text grows
			if (fMessageLabel.getPreferredSize().width > fMessageLabel.getWidth()) {
				pack();
			}
		}
	}

	private static class Progress {
		private final int value;
		private final String message;

		private Progress(int value, String message) {
			this.value = value;
			this.message = message;
		}
	}

}
