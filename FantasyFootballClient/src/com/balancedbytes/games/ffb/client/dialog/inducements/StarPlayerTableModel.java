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
import com.balancedbytes.games.ffb.model.Position;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.RosterPosition;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionInt;
import com.balancedbytes.games.ffb.util.StringTool;

@SuppressWarnings("serial")
public class StarPlayerTableModel extends AbstractTableModel {

	private String[] fColumnNames;
	private Object[][] fRowData;
	private int fNrOfCheckedRows;
	private int fMaxNrOfStars;
	private DialogBuyInducements fDialog;

	public StarPlayerTableModel(DialogBuyInducements pDialog, GameOptions gameOptions) {
		fDialog = pDialog;
		fColumnNames = new String[] { "", "Icon", "Name", "Gold" };
		fRowData = buildRowData();
		fMaxNrOfStars = ((GameOptionInt)gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_STARS_MAX)).getValue();
	}

	public int getColumnCount() {
		return 4;
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
		return (pColumnIndex == 0);
	}

	public int getCheckedRows() {
		int noBoughtStarPlayers = 0;
		for (int i = 0; i < getRowCount(); i++) {
			if ((Boolean) getValueAt(i, 0)) {
				noBoughtStarPlayers++;
			}
		}
		return noBoughtStarPlayers;
	}
	
	public void setMaxNrOfStars(int amount) {
		fMaxNrOfStars = amount;
	}

	public void setValueAt(Object pValue, int pRowIndex, int pColumnIndex) {
		if (pColumnIndex == 0) {
			Player player = (Player) fRowData[pRowIndex][4];
			int cost = player.getPosition().getCost();
			String teamWithPositionId = player.getPosition().getTeamWithPositionId(); 
			if (StringTool.isProvided(teamWithPositionId)) {
				int partnerRowId = -1;
				for (int i = 0; i < getRowCount(); i++) {
					Player rowPlayer = (Player) fRowData[i][4];
					if (teamWithPositionId.equals(rowPlayer.getPositionId())) {
						partnerRowId = i;
						break;
					}
				}
				if (partnerRowId >= 0) {
					if ((Boolean) pValue) {
						cost += ((Player) fRowData[partnerRowId][4]).getPosition().getCost();
						if ((cost <= fDialog.getAvailableGold()) && (fNrOfCheckedRows < fMaxNrOfStars) && (fDialog.getFreeSlotsInRoster() > 1)) {
							fRowData[pRowIndex][pColumnIndex] = true;
							fireTableCellUpdated(pRowIndex, pColumnIndex);
							fRowData[partnerRowId][pColumnIndex] = true;
							fireTableCellUpdated(partnerRowId, pColumnIndex);
							setMaxNrOfStars(fMaxNrOfStars + 1);
							fDialog.recalculateGold();
							fNrOfCheckedRows = getCheckedRows();
						}
					} else {
						fRowData[pRowIndex][pColumnIndex] = false;
						fireTableCellUpdated(pRowIndex, pColumnIndex);
						fRowData[partnerRowId][pColumnIndex] = false;
						fireTableCellUpdated(partnerRowId, pColumnIndex);
						setMaxNrOfStars(fMaxNrOfStars - 1);
						fDialog.recalculateGold();
						fNrOfCheckedRows = getCheckedRows();
					}
				}
			} else {
				if ((Boolean) pValue) {
					if ((cost <= fDialog.getAvailableGold()) && (fNrOfCheckedRows < fMaxNrOfStars) && (fDialog.getFreeSlotsInRoster() > 0)) {
						fRowData[pRowIndex][pColumnIndex] = pValue;
						fireTableCellUpdated(pRowIndex, pColumnIndex);
						fDialog.recalculateGold();
						fNrOfCheckedRows = getCheckedRows();
					}
				} else {
					fRowData[pRowIndex][pColumnIndex] = pValue;
					fireTableCellUpdated(pRowIndex, pColumnIndex);
					fDialog.recalculateGold();
					fNrOfCheckedRows = getCheckedRows();
				}
			}
		}
	}

	private Object[][] buildRowData() {
		PlayerIconFactory playerIconFactory = fDialog.getClient().getUserInterface().getPlayerIconFactory();
		List<Object[]> starPlayerList = new ArrayList<Object[]>();
		for (RosterPosition pos : fDialog.getRoster().getPositions()) {
			if (PlayerType.STAR == pos.getType()) {
				RosterPlayer player = new RosterPlayer();
				player.updatePosition(pos);
				player.setName(pos.getName());
				Object[] star = new Object[5];
				star[0] = new Boolean(false);
				star[1] = new ImageIcon(playerIconFactory.getBasicIcon(fDialog.getClient(), player, true, false, false, false));
				star[2] = pos.getName();
				star[3] = fDialog.formatGold(pos.getCost());
				star[4] = player;
				starPlayerList.add(star);
			}
		}
		Object[][] starPlayers = starPlayerList.toArray(new Object[starPlayerList.size()][]);
		Arrays.sort(starPlayers, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				Position position1 = ((Player) o1[4]).getPosition();
				Position position2 = ((Player) o2[4]).getPosition();
				return position1.getCost() - position2.getCost();
			}
		});
		return starPlayers;
	}

}
