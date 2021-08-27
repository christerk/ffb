package com.fumbbl.ffb.client.dialog;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import com.fumbbl.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public class DialogAboutHandler extends DialogHandler {

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
		fCloseTimer = new Timer();
		fCloseTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				fCloseTimer = null;
				dialogClosed(getDialog());
			}
		}, 5 * 1000);
		getClient().startClient();
	}

	public void dialogClosed(IDialog pDialog) {
		if (getDialog().isVisible()) {
			hideDialog();
			getClient().getUserInterface().getGlassPane().setVisible(false);
		}
	}

}
