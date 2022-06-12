package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.dialog.Dialog;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractBuyInducementsDialog extends Dialog implements ActionListener, KeyListener {
	protected JButton resetButton;
	protected JButton okButton;
	protected int mercExtraCost;
	protected int mercSkillCost;
	private JTable fTableStarPlayers;
	private StarPlayerTableModel fTableModelStarPlayers;
	private JTable fTableMercenaries;
	private MercenaryTableModel fTableModelMercenaries;
	private final Set<DropDownPanel> fPanels = new HashSet<>();
	private final String fTeamId;
	private final Roster fRoster;
	private final Team fTeam;
	private int maximumGold;

	public AbstractBuyInducementsDialog(FantasyFootballClient client, String title, String teamId, int availableGold, boolean closeable) {
		super(client, title, closeable);
		maximumGold = availableGold;
		setAvailableGold(maximumGold);
		fTeamId = teamId;
		if (client.getGame().getTeamHome().getId().equals(fTeamId)) {
			fRoster = client.getGame().getTeamHome().getRoster();
			fTeam = client.getGame().getTeamHome();
		} else {
			fRoster = client.getGame().getTeamAway().getRoster();
			fTeam = client.getGame().getTeamAway();
		}

		GameOptions gameOptions = client.getGame().getOptions();

		mercExtraCost = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST))
			.getValue();
		mercSkillCost = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_SKILL_COST))
			.getValue();
	}

	protected JPanel buildInducementPanel(GameOptions gameOptions) {

		JPanel leftPanel = buildLeftPanel(gameOptions);
		JPanel rightPanel = buildRightPanel(gameOptions);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		centerPanel.add(leftPanel);
		centerPanel.add(Box.createHorizontalStrut(10));
		centerPanel.add(rightPanel);

		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return centerPanel;
	}

	protected JPanel buttonPanel() {
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		resetButton.addKeyListener(this);
		resetButton.setMnemonic((int) 'R');

		okButton = new JButton("Buy & Close");
		okButton.addActionListener(this);
		okButton.addKeyListener(this);
		okButton.setMnemonic((int) 'B');

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(resetButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return buttonPanel;
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

		((InducementTypeFactory) gameOptions.getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE)).allTypes().stream()
			.filter(type -> !Usage.REQUIRE_EXPLICIT_SELECTION.contains(type.getUsages()))
			.forEach(type -> createPanel(type, leftPanel, verticalStrut, gameOptions));

		leftPanel.add(Box.createVerticalGlue());

		return leftPanel;

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
			fTableStarPlayers.getSelectionModel().addListSelectionListener(pE -> {
				if (!pE.getValueIsAdjusting()) {
					int selectedRowIndex = fTableStarPlayers.getSelectionModel().getLeadSelectionIndex();
					if (selectedRowIndex >= 0) {
						getClient().getClientData()
							.setSelectedPlayer((Player<?>) fTableModelStarPlayers.getValueAt(selectedRowIndex, 4));
						getClient().getUserInterface().refreshSideBars();
					}
				}
			});
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

		int maxMercs = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_MAX))
			.getValue();
		if (maxMercs > 0) {
			fTableMercenaries = new MercenaryTable(fTableModelMercenaries);
			fTableMercenaries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fTableMercenaries.getSelectionModel().addListSelectionListener(pE -> {
				if (!pE.getValueIsAdjusting()) {
					int selectedRowIndex = fTableMercenaries.getSelectionModel().getLeadSelectionIndex();
					if (selectedRowIndex >= 0) {
						getClient().getClientData()
							.setSelectedPlayer((Player<?>) fTableModelMercenaries.getValueAt(selectedRowIndex, 5));
						getClient().getUserInterface().refreshSideBars();
					}
				}
			});
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


	private void createPanel(InducementType pInducementType, JPanel pAddToPanel, int pVertStrut,
	                         GameOptions gameOptions) {
		int maxCount = pInducementType.availability(fTeam, gameOptions);
		if (maxCount <= 0) {
			return;
		}
		int cost = findInducementCost(fTeam, pInducementType, gameOptions);
		DropDownPanel panel = new DropDownPanel(pInducementType, maxCount, pInducementType.getDescription(), cost, this,
			getAvailableGold());
		pAddToPanel.add(panel);
		if (pVertStrut > 0) {
			pAddToPanel.add(Box.createVerticalStrut(pVertStrut));
		}
		fPanels.add(panel);
	}

	public int findInducementCost(Team team, InducementType pInducement, GameOptions gameOptions) {

		IGameOption gameOption = gameOptions.getOptionWithDefault(pInducement.getActualCostId(team));

		if (gameOption instanceof GameOptionInt) {
			return ((GameOptionInt) gameOption).getValue();
		}

		return 0;
	}

	protected void recalculateGold() {
		int cost = 0;
		for (DropDownPanel pan : fPanels) {
			cost += pan.getCurrentCost();
		}
		for (int i = 0; i < fTableModelStarPlayers.getRowCount(); i++) {
			if ((Boolean) fTableModelStarPlayers.getValueAt(i, 0)) {
				cost += ((Player<?>) fTableModelStarPlayers.getValueAt(i, 4)).getPosition().getCost();
			}
		}
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
				cost += ((Player<?>) fTableModelMercenaries.getValueAt(i, 5)).getPosition().getCost();
				cost += mercExtraCost;
				String skillSlot = ((String) fTableModelMercenaries.getValueAt(i, 4));
				if (StringTool.isProvided(skillSlot)) {
					cost += mercSkillCost;
				}
			}
		}
		setAvailableGold(maximumGold - cost);
		for (DropDownPanel pan1 : fPanels) {
			pan1.availableGoldChanged(getAvailableGold());
		}
		updateGoldValue();
	}

	protected void resetPanels() {
		for (int i = 0; i < fTableStarPlayers.getRowCount(); i++) {
			fTableModelStarPlayers.setValueAt(false, i, 0);
		}
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			fTableModelMercenaries.setValueAt(false, i, 0);
		}
		setAvailableGold(maximumGold);
		for (DropDownPanel pan : fPanels) {
			pan.reset(getMaximumGold());
		}
	}


	protected int getMaximumGold() {
		return maximumGold;
	}

	public void setMaximumGold(int maximumGold) {
		this.maximumGold = maximumGold;
	}

	protected abstract int getAvailableGold();

	protected abstract void setAvailableGold(int availableGold);


	public String[] getSelectedStarPlayerIds() {
		List<String> starPlayerPositionIds = new ArrayList<>();
		if (fTableModelStarPlayers != null) {
			for (int i = 0; i < fTableModelStarPlayers.getRowCount(); i++) {
				if ((Boolean) fTableModelStarPlayers.getValueAt(i, 0)) {
					Player<?> starPlayer = (Player<?>) fTableModelStarPlayers.getValueAt(i, 4);
					starPlayerPositionIds.add(starPlayer.getPositionId());
				}
			}
		}
		return starPlayerPositionIds.toArray(new String[0]);
	}

	public String[] getSelectedMercenaryIds() {
		List<String> mercenaryPositionIds = new ArrayList<>();
		if (fTableMercenaries != null) {
			for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
				if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
					Player<?> mercenary = (Player<?>) fTableModelMercenaries.getValueAt(i, 5);
					mercenaryPositionIds.add(mercenary.getPositionId());
				}
			}
		}
		return mercenaryPositionIds.toArray(new String[0]);
	}

	public Skill[] getSelectedMercenarySkills() {
		List<Skill> mercenarySkills = new ArrayList<>();
		for (int i = 0; i < fTableModelMercenaries.getRowCount(); i++) {
			if ((Boolean) fTableModelMercenaries.getValueAt(i, 0)) {
				Skill mercenarySkill = getClient().getGame().getRules().<SkillFactory>getFactory(FactoryType.Factory.SKILL).forName((String) fTableModelMercenaries.getValueAt(i, 4));
				mercenarySkills.add(mercenarySkill);
			}
		}
		return mercenarySkills.toArray(new Skill[0]);
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

	public InducementSet getSelectedInducements() {
		InducementSet indSet = new InducementSet();
		for (DropDownPanel pan : fPanels) {
			if (pan.getSelectedAmount() > 0) {
				indSet.addInducement(new Inducement(pan.getInducementType(), pan.getSelectedAmount()));
			}
		}
		InducementTypeFactory factory = getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		factory.allTypes().stream().filter(type -> type.getUsages() == Usage.STAR).findFirst().ifPresent(type -> {
			String[] starPlayerIds = getSelectedStarPlayerIds();
			if (starPlayerIds.length > 0) {
				indSet.addInducement(new Inducement(type, starPlayerIds.length));
			}
		});
		factory.allTypes().stream().filter(type -> type.getUsages() == Usage.LONER).findFirst().ifPresent(type -> {
			String[] mercenaryIds = getSelectedMercenaryIds();
			if (mercenaryIds.length > 0) {
				indSet.addInducement(new Inducement(type, mercenaryIds.length));
			}
		});
		return indSet;
	}
	protected abstract void updateGoldValue();
	public String getTeamId() {
		return fTeamId;
	}
	public Team getTeam() {
		return fTeam;
	}

	public Roster getRoster() {
		return fRoster;
	}

	public void actionPerformed(ActionEvent pActionEvent) {
		if ((pActionEvent.getSource() == okButton)) {
			if (getCloseListener() != null) {
				getCloseListener().dialogClosed(this);
			}
		} else if (pActionEvent.getSource() == resetButton) {
			resetPanels();
		} else {
			recalculateGold();
		}
	}

}
