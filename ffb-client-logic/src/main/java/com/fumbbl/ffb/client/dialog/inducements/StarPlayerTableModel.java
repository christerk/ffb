package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarPlayerTableModel extends AbstractTableModel {

	private final String[] fColumnNames;
	private final Object[][] fRowData;
	private int fNrOfCheckedRows;
	private int fMaxNrOfStars;
	private final AbstractBuyInducementsDialog fDialog;

	public StarPlayerTableModel(AbstractBuyInducementsDialog pDialog, GameOptions gameOptions) {
		fDialog = pDialog;
		fColumnNames = new String[]{"", "Icon", "Name", "Gold"};
		fRowData = buildRowData();
		fMaxNrOfStars = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_STARS_MAX)).getValue();
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
			Player<?> player = (Player<?>) fRowData[pRowIndex][4];
			int cost = player.getPosition().getCost();
			String teamWithPositionId = player.getPosition().getTeamWithPositionId();
			if (StringTool.isProvided(teamWithPositionId)) {
				int partnerRowId = -1;
				for (int i = 0; i < getRowCount(); i++) {
					Player<?> rowPlayer = (Player<?>) fRowData[i][4];
					if (teamWithPositionId.equals(rowPlayer.getPositionId())) {
						partnerRowId = i;
						break;
					}
				}
				if (partnerRowId >= 0) {
					if ((Boolean) pValue) {
						cost += ((Player<?>) fRowData[partnerRowId][4]).getPosition().getCost();
						boolean hasRoomForPair = fNrOfCheckedRows < fMaxNrOfStars;
						if ((cost <= fDialog.getAvailableGold()) && hasRoomForPair
							&& (fDialog.getFreeSlotsInRoster() > 1)) {
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
					if ((cost <= fDialog.getAvailableGold()) && (fNrOfCheckedRows < fMaxNrOfStars)
						&& (fDialog.getFreeSlotsInRoster() > 0)) {
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
		UserInterface userInterface = fDialog.getClient().getUserInterface();
		PlayerIconFactory playerIconFactory = userInterface.getPlayerIconFactory();
		DimensionProvider dimensionProvider = userInterface.getPitchDimensionProvider();
		List<Object[]> starPlayerList = new ArrayList<>();
		for (RosterPosition pos : fDialog.getRoster().getPositions()) {
			if (PlayerType.STAR == pos.getType()) {
				RosterPlayer player = new RosterPlayer();
				player.updatePosition(pos, fDialog.getClient().getGame().getRules(), fDialog.getClient().getGame().getId());
				player.setName(pos.getName());
				Object[] star = new Object[5];
				star[0] = Boolean.FALSE;
				star[1] = new ImageIcon(playerIconFactory.getBasicIcon(fDialog.getClient(), player, true, false, false, false, dimensionProvider));
				star[2] = pos.getName();
				star[3] = StringTool.formatThousands(pos.getCost());
				star[4] = player;
				starPlayerList.add(star);
			}
		}
		Object[][] starPlayers = starPlayerList.toArray(new Object[starPlayerList.size()][]);
		Arrays.sort(starPlayers, (o1, o2) -> {
			Position position1 = ((Player<?>) o1[4]).getPosition();
			Position position2 = ((Player<?>) o2[4]).getPosition();
			return position1.getCost() - position2.getCost();
		});
		return starPlayers;
	}

}
