package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 * @author Kalimar
 */
public class DialogAboutHandler extends DialogHandler {

	private static final int CLOSE_DELAY_IN_MILLIS = 5000;

	private Timer fCloseTimer;

	// used only when showing the AboutDialog on client startup

	private class MyGlassPane extends JPanel implements KeyListener, MouseListener {
		public MyGlassPane() {
			setOpaque(false);
			addMouseListener(this);
			addKeyListener(this);
		}

		public void keyPressed(KeyEvent pE) {
			dialogClosed(getDialog());
		}

		public void keyReleased(KeyEvent pE) {
		}

		public void keyTyped(KeyEvent pE) {
		}

		public void mouseClicked(MouseEvent pE) {
		}

		public void mouseEntered(MouseEvent pE) {
		}

		public void mouseExited(MouseEvent pE) {
		}

		public void mousePressed(MouseEvent pE) {
			dialogClosed(getDialog());
		}

		public void mouseReleased(MouseEvent pE) {
		}
	}

	public DialogAboutHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {
		setDialog(new DialogAbout(getClient()));
		getDialog().showDialog(this);
		getClient().getUserInterface().setGlassPane(new MyGlassPane());
		getClient().getUserInterface().getGlassPane().setVisible(true);
		getClient().getUserInterface().getGlassPane().requestFocus();
		// a swing timer fires on the event dispatch thread, so hiding the dialog does not race the user interface
		fCloseTimer = new Timer(CLOSE_DELAY_IN_MILLIS, event -> {
			fCloseTimer = null;
			dialogClosed(getDialog());
		});
		fCloseTimer.setRepeats(false);
		fCloseTimer.start();
	}

	public void dialogClosed(IDialog pDialog) {
		if (fCloseTimer != null) {
			fCloseTimer.stop();
			fCloseTimer = null;
		}
		if (getDialog().isVisible()) {
			hideDialog();
			getClient().getUserInterface().getGlassPane().setVisible(false);
		}
	}

}
