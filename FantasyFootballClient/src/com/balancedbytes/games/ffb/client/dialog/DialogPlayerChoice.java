package com.balancedbytes.games.ffb.client.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Player;

public class DialogPlayerChoice extends Dialog implements ActionListener {

	private PlayerCheckList fList;
	private JButton fButtonSelect;
	private JButton fButtonCancel;
	private Player<?>[] fSelectedPlayers;
	private int fMinSelects;

	public DialogPlayerChoice(FantasyFootballClient client, String header, String[] playerIds, String[] descriptions,
			int minSelects, int maxSelects, FieldCoordinate playerCoordinate, boolean preSelected) {

		super(client, "Player Choice", false);
		fMinSelects = minSelects;

		fButtonSelect = new JButton("Select");
		fButtonSelect.setToolTipText("Select the checked player(s)");
		fButtonSelect.addActionListener(this);
		fButtonSelect.setMnemonic((int) 'S');
		fButtonSelect.setEnabled((playerIds.length == 1) || preSelected);

		fButtonCancel = new JButton("Cancel");
		fButtonCancel.setToolTipText("Do not select any player");
		fButtonCancel.addActionListener(this);
		fButtonCancel.setMnemonic((int) 'C');

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
		JLabel headerLabel = new JLabel(header);
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

		if (playerCoordinate != null) {
			int x = SideBarComponent.WIDTH + ((playerCoordinate.getX() + 1) * FieldLayer.FIELD_SQUARE_SIZE);
			int y = (playerCoordinate.getY() + 1) * FieldLayer.FIELD_SQUARE_SIZE;
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

}
