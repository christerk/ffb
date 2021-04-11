package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.dialog.DialogId;

import java.awt.Dimension;
import java.awt.Point;

/**
 * 
 * @author Kalimar
 */
public interface IDialog {

	DialogId getId();

	void showDialog(IDialogCloseListener pCloseListener);

	void hideDialog();

	void setLocation(Point pLocation);

	Dimension getSize();

	boolean isVisible();

}
