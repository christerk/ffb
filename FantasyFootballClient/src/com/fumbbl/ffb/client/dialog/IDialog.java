package com.fumbbl.ffb.client.dialog;

import java.awt.Dimension;
import java.awt.Point;

import com.fumbbl.ffb.dialog.DialogId;

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
