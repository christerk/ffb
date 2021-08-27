package com.fumbbl.ffb.client.dialog;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayer;

/**
 * 
 * @author Kalimar
 */
public abstract class Dialog extends JInternalFrame implements IDialog, MouseListener, InternalFrameListener {

	private IDialogCloseListener fCloseListener;
	private FantasyFootballClient fClient;
	private boolean fChatInputFocus;

	public Dialog(FantasyFootballClient pClient, String pTitle, boolean pCloseable) {
		super(pTitle, false, pCloseable);
		fClient = pClient;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addMouseListener(this);
		addInternalFrameListener(this);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void showDialog(IDialogCloseListener pCloseListener) {
		fCloseListener = pCloseListener;
		UserInterface userInterface = getClient().getUserInterface();
		fChatInputFocus = userInterface.getChat().hasChatInputFocus();
		userInterface.getDesktop().add(Dialog.this);
		setVisible(true);
		moveToFront();
		if (fChatInputFocus) {
			userInterface.getChat().requestChatInputFocus();
		}
	}

	public void hideDialog() {
		if (isVisible()) {
			setVisible(false);
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getDesktop().remove(this);
			if (fChatInputFocus) {
				userInterface.getChat().requestChatInputFocus();
			}
		}
	}

	public IDialogCloseListener getCloseListener() {
		return fCloseListener;
	}

	protected void setLocationToCenter() {
		Dimension dialogSize = getSize();
		Dimension frameSize = getClient().getUserInterface().getSize();
		// Dimension menuBarSize = getClient().getGameMenuBar().getSize();
		setLocation((frameSize.width - dialogSize.width) / 2, (FieldLayer.FIELD_IMAGE_HEIGHT - dialogSize.height) / 2);
	}

	public void mouseEntered(MouseEvent pMouseEvent) {
		if (getClient().getClientState() != null) {
			getClient().getClientState().hideSelectSquare();
		}
	}

	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	public void mouseExited(MouseEvent pMouseEvent) {
	}

	public void mousePressed(MouseEvent pMouseEvent) {
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
	}

	public void internalFrameActivated(InternalFrameEvent pE) {
		if (getClient().getClientState() != null) {
			getClient().getClientState().hideSelectSquare();
		}
	}

	public void internalFrameClosed(InternalFrameEvent pE) {
	}

	public void internalFrameClosing(InternalFrameEvent pE) {
	}

	public void internalFrameDeactivated(InternalFrameEvent pE) {
	}

	public void internalFrameDeiconified(InternalFrameEvent pE) {
	}

	public void internalFrameIconified(InternalFrameEvent pE) {
	}

	public void internalFrameOpened(InternalFrameEvent pE) {
	}

}
