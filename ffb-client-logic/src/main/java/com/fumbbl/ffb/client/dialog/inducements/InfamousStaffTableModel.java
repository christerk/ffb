package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfamousStaffTableModel extends AbstractTableModel {

	private final String[] fColumnNames;
	private final Object[][] fRowData;
	private final AbstractBuyInducementsDialog fDialog;
	private int fNrOfCheckedRows;
	private final int maxStaff;

	public InfamousStaffTableModel(AbstractBuyInducementsDialog pDialog, GameOptions gameOptions) {
		fDialog = pDialog;
		fColumnNames = new String[]{"", "Icon", "Name", "Gold"};
		fRowData = buildRowData();
		maxStaff = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_STAFF_MAX)).getValue();
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
		int boughtStaff = 0;
		for (int i = 0; i < getRowCount(); i++) {
			if ((Boolean) getValueAt(i, 0)) {
				boughtStaff++;
			}
		}
		return boughtStaff;
	}

	public void setValueAt(Object pValue, int pRowIndex, int pColumnIndex) {
		if (pColumnIndex == 0) {
			Player<?> player = (Player<?>) fRowData[pRowIndex][4];
			int cost = player.getPosition().getCost();
			if ((Boolean) pValue) {
				if ((cost <= fDialog.getAvailableGold()) && (fNrOfCheckedRows < maxStaff)) {
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

	private Object[][] buildRowData() {
		PlayerIconFactory playerIconFactory = fDialog.getClient().getUserInterface().getPlayerIconFactory();
		List<Object[]> staffList = new ArrayList<>();
		for (RosterPosition pos : fDialog.getRoster().getPositions()) {
			if (PlayerType.INFAMOUS_STAFF == pos.getType()) {
				RosterPlayer player = new RosterPlayer();
				player.updatePosition(pos, fDialog.getClient().getGame().getRules(), fDialog.getClient().getGame().getId());
				player.setName(pos.getName());
				Object[] staff = new Object[5];
				staff[0] = Boolean.FALSE;
				staff[1] = new ImageIcon(playerIconFactory.getBasicIcon(fDialog.getClient(), player, true, false, false, false, RenderContext.ON_PITCH));
				staff[2] = pos.getName();
				staff[3] = StringTool.formatThousands(pos.getCost());
				staff[4] = player;
				staffList.add(staff);
			}
		}
		Object[][] starPlayers = staffList.toArray(new Object[staffList.size()][]);
		Arrays.sort(starPlayers, (o1, o2) -> {
			Position position1 = ((Player<?>) o1[4]).getPosition();
			Position position2 = ((Player<?>) o2[4]).getPosition();
			return position1.getCost() - position2.getCost();
		});
		return starPlayers;
	}

}
