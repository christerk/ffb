package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.swing.JButton;
import com.fumbbl.ffb.client.ui.swing.JLabel;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Player;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class DialogPlayerChoice extends Dialog implements ActionListener {

	private final PlayerCheckList fList;
	private final JButton fButtonSelect;
	private final JButton fButtonCancel;
	private Player<?>[] fSelectedPlayers;
	private final int fMinSelects;

	public DialogPlayerChoice(FantasyFootballClient client, String header, String[] playerIds, String[] descriptions,
	                          int minSelects, int maxSelects, FieldCoordinate playerCoordinate, boolean preSelected) {

		super(client, "Player Choice", false);
		fMinSelects = minSelects;

		fButtonSelect = new JButton(dimensionProvider(), "Select");
		fButtonSelect.setToolTipText("Select the checked player(s)");
		fButtonSelect.addActionListener(this);
		fButtonSelect.setMnemonic((int) 'S');
		fButtonSelect.setEnabled((playerIds.length == 1) || preSelected);

		fButtonCancel = new JButton(dimensionProvider(), "Skip");
		fButtonCancel.setToolTipText("Do not select any player");
		fButtonCancel.addActionListener(this);
		fButtonCancel.setMnemonic((int) 'i');

		fList = new PlayerCheckList(client, playerIds, descriptions, minSelects, maxSelects, preSelected, fButtonSelect);
		fList.setVisibleRowCount(Math.min(playerIds.length, 5));
		fList.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent pMouseEvent) {
				int index = fList.locationToIndex(pMouseEvent.getPoint());
				Player<?> player = fList.getPlayerAtIndex(index);
				if (player != null) {
					FieldCoordinate playerCoordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(player);
					getClient().getClientState().hideSelectSquare();
					getClient().getClientState().showSelectSquare(playerCoordinate);
					if (player != getClient().getClientData().getSelectedPlayer()) {
						getClient().getClientData().setSelectedPlayer(player);
						getClient().getUserInterface().refreshSideBars();
					}
				}
			}
		});

		JScrollPane listScroller = new JScrollPane(fList);
		// listScroller.setPreferredSize(new Dimension(200, 100));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		JLabel headerLabel = new JLabel(dimensionProvider(), header);
		headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, headerLabel.getFont().getSize()));
		headerPanel.add(headerLabel);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.add(listScroller);
		listPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButtonSelect);
		if (minSelects == 0) {
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(fButtonCancel);
		}
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
		centerPane.add(headerPanel);
		centerPane.add(listPanel);
		centerPane.add(buttonPanel);

		getContentPane().add(centerPane, BorderLayout.CENTER);

		pack();

		if (playerCoordinate != null && !playerCoordinate.isBoxCoordinate()) {
			int offsetX = 1, offsetY = 1;
			DimensionProvider dimensionProvider = client.getUserInterface().getDimensionProvider();

			if (dimensionProvider.isPitchPortrait()) {
				offsetX = -1;
			}

			Dimension sidebarSize = dimensionProvider.dimension(DimensionProvider.Component.SIDEBAR);
			Dimension onPitch = dimensionProvider.mapToLocal(playerCoordinate.getX() + offsetX, playerCoordinate.getY() + offsetY, false);
			int x = sidebarSize.width + onPitch.width;
			int y = onPitch.height;
			setLocation(x, y);
		} else {
			setLocationToCenter();
		}

		addMouseListener(this);

		fList.setSelectedIndex(0);

	}

	public DialogId getId() {
		return DialogId.PLAYER_CHOICE;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if (pActionEvent.getSource() == fButtonCancel) {
			fSelectedPlayers = new Player[0];
			closeDialog();
		}
		if (pActionEvent.getSource() == fButtonSelect) {
			fSelectedPlayers = fList.getSelectedPlayers();
			if (fSelectedPlayers.length >= fMinSelects) {
				closeDialog();
			}
		}
	}

	private void closeDialog() {
		if (getCloseListener() != null) {
			getCloseListener().dialogClosed(this);
		}
	}

	public Player<?>[] getSelectedPlayers() {
		return fSelectedPlayers;
	}

	public List<Integer> getSelectedIndexes() {
		return fList.getSelectedIndexes();
	}
}
