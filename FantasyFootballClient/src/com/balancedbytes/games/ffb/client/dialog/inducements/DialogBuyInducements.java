package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.client.dialog.Dialog;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.*;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.util.UtilInducements;
import com.balancedbytes.games.ffb.util.StringTool;

@SuppressWarnings("serial")
public class DialogBuyInducements extends Dialog implements ActionListener, KeyListener {

	private final int mercExtraCost;
	private final int mercSkillCost;

	private Set<DropDownPanel> fPanels = new HashSet<DropDownPanel>();
	private int fAvailableGold = 0;
	private Roster fRoster = null;
	private int fStartGold = 0;
	private JPanel fGoldPanel = null;
	private JLabel fGoldLabelAmount = null;
	private JButton fButtonReset = null;
	private JButton fButtonOK = null;
	private String fTeamId = null;
	private JTable fTableStarPlayers;
	private StarPlayerTableModel fTableModelStarPlayers;
	private JTable fTableMercenaries;
	private MercenaryTableModel fTableModelMercenaries;
	private Team fTeam;

	public DialogBuyInducements(FantasyFootballClient client, String teamId, int availableGold) {

		super(client, "Buy Inducements", true);

		fTeamId = teamId;

		if (client.getGame().getTeamHome().getId().equals(fTeamId)) {
			fRoster = client.getGame().getTeamHome().getRoster();
			fTeam = client.getGame().getTeamHome();
		} else {
			fRoster = client.getGame().getTeamAway().getRoster();
			fTeam = client.getGame().getTeamAway();
		}
		fStartGold = fAvailableGold = availableGold;
		
		fGoldPanel = new JPanel();
		fGoldPanel.setLayout(new BoxLayout(fGoldPanel, BoxLayout.X_AXIS));
		
		JLabel goldLabel = new JLabel("Available Gold:");
		goldLabel.setFont(new Font("Sans Serif", Font.BOLD, 12));

		fGoldPanel.add(goldLabel);
		fGoldPanel.add(Box.createHorizontalStrut(10));

		fGoldLabelAmount = new JLabel(formatGold(fAvailableGold));
		fGoldLabelAmount.setFont(new Font("Sans Serif", Font.BOLD, 12));
		
		fGoldPanel.add(fGoldLabelAmount);
		fGoldPanel.add(Box.createHorizontalGlue());

		fGoldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		GameOptions gameOptions = client.getGame().getOptions();

		mercExtraCost = ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST)).getValue();
		mercSkillCost = ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_SKILL_COST)).getValue();

		JPanel leftPanel = buildLeftPanel(gameOptions);
		JPanel rightPanel = buildRightPanel(gameOptions);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		centerPanel.add(leftPanel);
		centerPanel.add(Box.createHorizontalStrut(10));
		centerPanel.add(rightPanel);

		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fButtonReset = new JButton("Reset");
		fButtonReset.addActionListener(this);
		fButtonReset.addKeyListener(this);
		fButtonReset.setMnemonic((int) 'R');

		fButtonOK = new JButton("Buy");
		fButtonOK.addActionListener(this);
		fButtonOK.addKeyListener(this);
		fButtonOK.setMnemonic((int) 'B');

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(fButtonOK);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(fButtonReset);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(fGoldPanel);
		getContentPane().add(centerPanel);
		getContentPane().add(buttonPanel);

		pack();
		setLocationToCenter();
		Point p = getLocation();
		setLocation(p.x, 10);
		
	}

	
	public int getFreeSlotsInRoster() {
		int freeSlots = 16 - fTeam.getNrOfAvailablePlayers();
		freeSlots -= fTableModelStarPlayers.getCheckedRows();
		freeSlots -= fTableModelMercenaries.getCheckedRows();
		if (freeSlots < 0) {
			freeSlots = 0;
		}
		return freeSlots;
	}

	private JPanel buildLeftPanel(GameOptions gameOptions) {

		int verticalStrut = 10;

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(new JLabel("Inducements:"));
		labelPanel.add(Box.createHorizontalGlue());

		leftPanel.add(labelPanel);
		leftPanel.add(Box.createVerticalStrut(10));

		createPanel(InducementType.BLOODWEISER_KEGS, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.BRIBES, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.EXTRA_TEAM_TRAINING, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.MASTER_CHEF, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.IGOR, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.WANDERING_APOTHECARIES, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.WIZARD, leftPanel, verticalStrut, gameOptions);
		createPanel(InducementType.RIOTOUS_ROOKIES, leftPanel, verticalStrut, gameOptions);


		leftPanel.add(Box.createVerticalGlue());

		return leftPanel;

	}

	public String getTeamId() {
		return fTeamId;
	}

	public InducementSet getSelectedInducements() {
		InducementSet indSet = new InducementSet();
		for (DropDownPanel pan : fPanels) {
			if (pan.getSelectedAmount() > 0) {
				indSet.addInducement(new Inducement(pan.getInducementType(), pan.getSelectedAmount()));
			}
		}
		String[] starPlayerIds = getSelectedStarPlayerIds();
		if (starPlayerIds.length > 0) {
			indSet.addInducement(new Inducement(InducementType.STAR_PLAYERS, starPlayerIds.length));
		}
		String[] mercenaryIds = getSelectedMercenaryIds();
		if (mercenaryIds.length > 0) {
			indSet.addInducement(new Inducement(InducementType.MERCENARIES, mercenaryIds.length));
		}
		return indSet;
	}

	public String[] getSelectedStarPlayerIds() {
		List<String> starPlayerPositionIds = new ArrayList<String>();
		for (int i = 0; i < fTableModelStarPlayers.getRowCount(); i++) {
			if ((Boolean) fTableModelStarPlayers.getValueAt(i, 0)) {
				Player starPlayer = (Player) fTableModelStarPlayers.getValueAt(i, 4);
				starPlayerPositionIds.add(starPlayer.getPositionId());
			}
		}
		return starPlayerPositionIds.toArray(new String[starPlayerPositionIds.size()]);
	}

	public String[] getSelectedMercenaryIds() {
		List<String> mercenaryPositionIds = new ArrayList<String>();
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
				Player mercenary = (Player) fTableModelMercenaries.getValueAt(i, 5);
				mercenaryPositionIds.add(mercenary.getPositionId());
			}
		}
		return mercenaryPositionIds.toArray(new String[mercenaryPositionIds.size()]);
	}

	public Skill[] getSelectedMercenarySkills() {
		List<Skill> mercenarySkills = new ArrayList<Skill>();
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
				Skill mercenarySkill = new SkillFactory().forName((String) fTableModelMercenaries.getValueAt(i, 4));
				mercenarySkills.add(mercenarySkill);
			}
		}
		return mercenarySkills.toArray(new Skill[mercenarySkills.size()]);
	}

	public int getAvailableGold() {
		return fAvailableGold;
	}

	public Team getTeam() {
		return fTeam;
	}
	
	public Roster getRoster() {
		return fRoster;
	}
	
	private JPanel buildRightPanel(GameOptions gameOptions) {

		// Right Panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		fTableModelStarPlayers = new StarPlayerTableModel(this, gameOptions);
		int maxStars = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_STARS_MAX)).getValue();

		if (maxStars > 0) {

			fTableStarPlayers = new StarPlayerTable(fTableModelStarPlayers);
			fTableStarPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fTableStarPlayers.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent pE) {
						if (!pE.getValueIsAdjusting()) {
							int selectedRowIndex = fTableStarPlayers.getSelectionModel().getLeadSelectionIndex();
							if (selectedRowIndex >= 0) {
								getClient().getClientData().setSelectedPlayer((Player) fTableModelStarPlayers.getValueAt(selectedRowIndex, 4));
								getClient().getUserInterface().refreshSideBars();
							}
						}
					}
				}
			);
			DefaultTableCellRenderer rightAlignedRenderer = new DefaultTableCellRenderer();
			rightAlignedRenderer.setHorizontalAlignment(JLabel.RIGHT);
			fTableStarPlayers.getColumnModel().getColumn(3).setCellRenderer(rightAlignedRenderer);
			fTableStarPlayers.getColumnModel().getColumn(0).setPreferredWidth(30);
			fTableStarPlayers.getColumnModel().getColumn(1).setPreferredWidth(50);
			fTableStarPlayers.getColumnModel().getColumn(2).setPreferredWidth(270);
			fTableStarPlayers.getColumnModel().getColumn(3).setPreferredWidth(100);
			fTableStarPlayers.setRowHeight(PlayerIconFactory.MAX_ICON_HEIGHT + 2);
			fTableStarPlayers.setPreferredScrollableViewportSize(new Dimension(350, 148));
			JScrollPane scrollPaneStarPlayer = new JScrollPane(fTableStarPlayers);

			JPanel starLabel = new JPanel();
			starLabel.setLayout(new BoxLayout(starLabel, BoxLayout.X_AXIS));
			starLabel.add(new JLabel("Star Players (varying Gold 0-2):"));
			starLabel.add(Box.createHorizontalGlue());

			rightPanel.add(starLabel);
			rightPanel.add(Box.createVerticalStrut(10));
			rightPanel.add(scrollPaneStarPlayer);
			rightPanel.add(Box.createVerticalGlue());
		}

		fTableModelMercenaries = new MercenaryTableModel(this, gameOptions);

		int maxMercs = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_MAX)).getValue();
		if (maxMercs > 0) {
			fTableMercenaries = new MercenaryTable(fTableModelMercenaries);
			fTableMercenaries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fTableMercenaries.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent pE) {
						if (!pE.getValueIsAdjusting()) {
							int selectedRowIndex = fTableMercenaries.getSelectionModel().getLeadSelectionIndex();
							if (selectedRowIndex >= 0) {
								getClient().getClientData().setSelectedPlayer((Player) fTableModelMercenaries.getValueAt(selectedRowIndex, 5));
								getClient().getUserInterface().refreshSideBars();
							}
						}
					}
				}
			);
			DefaultTableCellRenderer mercAlignedRenderer = new DefaultTableCellRenderer();
			mercAlignedRenderer.setHorizontalAlignment(JLabel.RIGHT);
			fTableMercenaries.getColumnModel().getColumn(3).setCellRenderer(mercAlignedRenderer);
			fTableMercenaries.getColumnModel().getColumn(0).setPreferredWidth(30);
			fTableMercenaries.getColumnModel().getColumn(1).setPreferredWidth(50);
			fTableMercenaries.getColumnModel().getColumn(2).setPreferredWidth(150);
			fTableMercenaries.getColumnModel().getColumn(3).setPreferredWidth(100);
			fTableMercenaries.getColumnModel().getColumn(4).setPreferredWidth(120);
			fTableMercenaries.setRowHeight(PlayerIconFactory.MAX_ICON_HEIGHT + 2);
			fTableMercenaries.setPreferredScrollableViewportSize(new Dimension(350, 148));
			JScrollPane scrollPaneMec = new JScrollPane(fTableMercenaries);
			JPanel mecLabel = new JPanel();
			mecLabel.setLayout(new BoxLayout(mecLabel, BoxLayout.X_AXIS));
			mecLabel.add(new JLabel("Mercenaries (varying Gold):"));
			mecLabel.add(Box.createHorizontalGlue());

			rightPanel.add(Box.createVerticalStrut(10));

			rightPanel.add(mecLabel);
			rightPanel.add(Box.createVerticalStrut(10));
			rightPanel.add(scrollPaneMec);
			rightPanel.add(Box.createVerticalGlue());
		}
		return rightPanel;

	}

	public DialogId getId() {
		return DialogId.BUY_INDUCEMENTS;
	}

	private void setGoldValue(int pValue) {
		fGoldLabelAmount.setText(formatGold(pValue));
	}

	public String formatGold(int pAmount) {
		StringBuilder buf = new StringBuilder(Integer.toString(pAmount)).reverse();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < buf.length(); i++) {
			result.insert(0, buf.charAt(i));
			if ((i + 1) % 3 == 0 && i + 1 < buf.length()) {
				result.insert(0, ",");
			}
		}
		return result.toString();
	}

	private void createPanel(InducementType pInducementType, JPanel pAddToPanel, int pVertStrut, GameOptions gameOptions) {
		int maxCount = UtilInducements.findInducementsAvailable(fRoster, pInducementType, gameOptions);
		if (maxCount <= 0) {
			return;
		}
		int cost = UtilInducements.findInducementCost(fRoster, pInducementType, gameOptions);
		DropDownPanel panel = new DropDownPanel(pInducementType, maxCount, pInducementType.getDescription(), cost, this, fAvailableGold);
		pAddToPanel.add(panel);
		if (pVertStrut > 0) {
			pAddToPanel.add(Box.createVerticalStrut(pVertStrut));
		}
		fPanels.add(panel);
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if ((pActionEvent.getSource() == fButtonOK)) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		} else if (pActionEvent.getSource() == fButtonReset) {
			resetPanels();
		} else {
			recalculateGold();
		}
	}

	public void recalculateGold() {
		int cost = 0;
		for (DropDownPanel pan : fPanels) {
			cost += pan.getCurrentCost();
		}
		for (int i = 0; i < fTableModelStarPlayers.getRowCount(); i++) {
			if ((Boolean) fTableModelStarPlayers.getValueAt(i, 0)) {
				cost += ((Player) fTableModelStarPlayers.getValueAt(i, 4)).getPosition().getCost();
			}
		}
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
				cost += ((Player) fTableModelMercenaries.getValueAt(i, 5)).getPosition().getCost();
				cost += mercExtraCost;
				String skillSlot = ((String) fTableModelMercenaries.getValueAt(i, 4));
				if (StringTool.isProvided(skillSlot)) {
					cost += mercSkillCost;
				}
			}
		}
		fAvailableGold = fStartGold - cost;
		for (DropDownPanel pan1 : fPanels) {
			pan1.availableGoldChanged(fAvailableGold);
		}
		setGoldValue(fAvailableGold);
	}

	public void resetPanels() {
		for (int i = 0; i < fTableStarPlayers.getRowCount(); i++) {
			fTableModelStarPlayers.setValueAt(false, i, 0);
		}
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			fTableModelMercenaries.setValueAt(false, i, 0);
		}
		fAvailableGold = fStartGold;
		for (DropDownPanel pan : fPanels) {
			pan.reset(fStartGold);
		}
	}

	public void keyPressed(KeyEvent pKeyEvent) {
	}

	public void keyReleased(KeyEvent pKeyEvent) {
		boolean keyHandled = false;
		switch (pKeyEvent.getKeyCode()) {
		case KeyEvent.VK_R:
			resetPanels();
			break;
		case KeyEvent.VK_O:
			keyHandled = true;
			break;
		default:
			keyHandled = false;
			break;
		}
		if (keyHandled) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		}
	}

	public void keyTyped(KeyEvent pKeyEvent) {
	}

}