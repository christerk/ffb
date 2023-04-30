package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

/**
 * @author Kalimar
 */
public abstract class Dialog extends JInternalFrame implements IDialog, MouseListener, InternalFrameListener {

	private IDialogCloseListener fCloseListener;
	private final FantasyFootballClient fClient;
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
		setLocation((frameSize.width - dialogSize.width) / 2, (fClient.getUserInterface().getDimensionProvider().dimension(DimensionProvider.Component.FIELD).height - dialogSize.height) / 2);
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

	protected void addMenuPanel(Container contentPane, CommonProperty menuProperty, String defaultValueKey) {

		if (!StringTool.isProvided(menuProperty)) {
			return;
		}

		GameMenuBar gameMenuBar = getClient().getUserInterface().getGameMenuBar();
		String name = gameMenuBar.menuName(menuProperty);
		Map<String, String> entries = gameMenuBar.menuEntries(menuProperty);

		String selectedValue = entries.get(getClient().getProperty(menuProperty));
		if (!StringTool.isProvided(selectedValue)) {
			selectedValue = entries.get(defaultValueKey);
		}

		JComboBox<String> box = new JComboBox<>(dimensionProvider(), entries.values().toArray(new String[0]));
		box.setSelectedItem(selectedValue);
		box.addActionListener(event -> {
			String newValue = box.getItemAt(box.getSelectedIndex());
			entries.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(newValue)).map(Map.Entry::getKey).findFirst().ifPresent(
				key -> {
					getClient().setProperty(menuProperty, key);
					getClient().saveUserSettings(true);
				}
			);
		});

		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		boxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		boxPanel.add(new JLabel(dimensionProvider(), name));
		boxPanel.add(Box.createHorizontalStrut(5));
		boxPanel.add(box);

		contentPane.add(new JSeparator());
		boxPanel.add(Box.createVerticalStrut(5));
		contentPane.add(new JSeparator());
		contentPane.add(boxPanel);

	}

	protected DimensionProvider dimensionProvider() {
		return getClient().getUserInterface().getDimensionProvider();
	}

	protected FontCache fontCache() {
		return getClient().getUserInterface().getFontCache();
	}
}
