package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.util.StringTool;

@SuppressWarnings("serial")
public class MercenaryTableModel extends AbstractTableModel {

	private final int mercExtraCost;
	private final int mercSkillCost;
	private String[] fColumnNames;
	private Object[][] fRowData;
	private DialogBuyInducements fDialog;
	private int checkedRows = 0;
	private int maxMercs;


	public MercenaryTableModel(DialogBuyInducements pDialog, GameOptions gameOptions) {
		mercExtraCost = ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST)).getValue();
		mercSkillCost = ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_SKILL_COST)).getValue();
		maxMercs =  ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_MAX)).getValue();

		fDialog = pDialog;
		fColumnNames = new String[] { "", "Icon", "Name", "Gold", "Skill" };
		fRowData = buildRowData();
	}

	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		return fRowData.length;
	}

	public Class<?> getColumnClass(int pColumnIndex) {
		return getValueAt(0, pColumnIndex).getClass();
	}

	public String getColumnName(int pColumnIndex) {
		return fColumnNames[pColumnIndex];
	}

	public Object getValueAt(int pRowIndex, int pColumnIndex) {
		return fRowData[pRowIndex][pColumnIndex];
	}

	public boolean isCellEditable(int pRowIndex, int pColumnIndex) {
		return (pColumnIndex == 0 || pColumnIndex == 4);
	}

	public int getCheckedRows() {
		int noBoughtMercs = 0;
		for (int i = 0; i < getRowCount(); i++) {
			if ((Boolean) getValueAt(i, 0)) {
				noBoughtMercs++;
			}
		}
		return noBoughtMercs;
	}

	public void setValueAt(Object pValue, int pRowIndex, int pColumnIndex) {
		Player player = (Player) fRowData[pRowIndex][5];
		int playerCost = player.getPosition().getCost() + mercExtraCost;
		if (pColumnIndex == 0) {
			int skillCost = StringTool.isProvided(fRowData[pRowIndex][4]) ? mercSkillCost : 0;
			if ((Boolean) pValue) {
				if ((playerCost + skillCost <= fDialog.getAvailableGold()) && (fDialog.getFreeSlotsInRoster() > 0) && checkedRows < maxMercs) {
					fRowData[pRowIndex][pColumnIndex] = pValue;
					fireTableCellUpdated(pRowIndex, pColumnIndex);
					checkedRows = getCheckedRows();
				}
			} else {
				fRowData[pRowIndex][pColumnIndex] = pValue;
				fireTableCellUpdated(pRowIndex, pColumnIndex);
				checkedRows = getCheckedRows();
			}
			fDialog.recalculateGold();
		}
		if (pColumnIndex == 4) {
			fRowData[pRowIndex][pColumnIndex] = pValue;
			fireTableCellUpdated(pRowIndex, pColumnIndex);
			int skillCost = StringTool.isProvided(fRowData[pRowIndex][4]) ? mercSkillCost : 0;
			setValueAt(fDialog.formatGold(playerCost + skillCost), pRowIndex, 3);
			if ((Boolean) fRowData[pRowIndex][0]) {
				if (skillCost > fDialog.getAvailableGold()) {
					fRowData[pRowIndex][0] = false;
					fireTableCellUpdated(pRowIndex, 0);
				}
			}
			fDialog.recalculateGold();
		}
		if (pColumnIndex == 3) {
			fRowData[pRowIndex][pColumnIndex] = pValue;
		}
	}

	private Object[][] buildRowData() {
		PlayerIconFactory playerIconFactory = fDialog.getClient().getUserInterface().getPlayerIconFactory();
		List<Object[]> mercenaryList = new ArrayList<Object[]>();
		for (RosterPosition pos : fDialog.getRoster().getPositions()) {
			if (PlayerType.STAR != pos.getType()) {
				int playerInPosition = fDialog.getTeam().getNrOfAvailablePlayersInPosition(pos);
				for (int i = 0; i < pos.getQuantity() - playerInPosition; i++) {
					Player player = new RosterPlayer();
					player.updatePosition(pos);
					player.setName(pos.getName());
					Object[] mecenary = new Object[6];
					mecenary[0] = new Boolean(false);
					mecenary[1] = new ImageIcon(playerIconFactory.getBasicIcon(fDialog.getClient(), player, true, false, false, false));
					mecenary[2] = pos.getName();
					mecenary[3] = fDialog.formatGold(pos.getCost() + mercExtraCost);
					mecenary[4] = "";
					mecenary[5] = player;
					mercenaryList.add(mecenary);
				}
			}
		}
		Object[][] mercenaries = mercenaryList.toArray(new Object[mercenaryList.size()][]);
		Arrays.sort(mercenaries, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				RosterPosition position1 = ((Player) o1[5]).getPosition();
				RosterPosition position2 = ((Player) o2[5]).getPosition();
				return position1.getCost() - position2.getCost();
			}
		});
		return mercenaries;
	}

}

