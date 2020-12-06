package com.balancedbytes.games.ffb.client.dialog;

import java.awt.Dimension;
import java.awt.Point;

import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
public interface IDialog {

	public DialogId getId();

	public void showDialog(IDialogCloseListener pCloseListener);

	public void hideDialog();

	public void setLocation(Point pLocation);

	public Dimension getSize();

	public boolean isVisible();

}
