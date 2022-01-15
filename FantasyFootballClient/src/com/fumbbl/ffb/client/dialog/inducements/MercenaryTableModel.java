package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.model.Game;
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

public class MercenaryTableModel extends AbstractTableModel {

	private final int mercExtraCost;
	private final int mercSkillCost;
	private final String[] fColumnNames;
	private final Object[][] fRowData;
	private final AbstractBuyInducementsDialog fDialog;
	private int checkedRows = 0;
	private final int maxMercs;
	private final int bigGuysOnTeam;
	private int boughtBigGuys = 0;

	public MercenaryTableModel(AbstractBuyInducementsDialog pDialog, GameOptions gameOptions) {
		mercExtraCost = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_EXTRA_COST))
			.getValue();
		mercSkillCost = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_SKILL_COST))
			.getValue();
		maxMercs = ((GameOptionInt) gameOptions.getOptionWithDefault(GameOptionId.INDUCEMENT_MERCENARIES_MAX)).getValue();

		fDialog = pDialog;
		fColumnNames = new String[]{"", "Icon", "Name", "Gold", "Skill"};
		bigGuysOnTeam = (int) Arrays.stream(fDialog.getTeam().getPlayers()).filter(player -> player.getRecoveringInjury() == null && player.getPlayerType() == PlayerType.BIG_GUY).count();
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
		Player<?> player = (Player<?>) fRowData[pRowIndex][5];
		int playerCost = player.getPosition().getCost() + mercExtraCost;
		if (pColumnIndex == 0) {
			int skillCost = StringTool.isProvided(fRowData[pRowIndex][4]) ? mercSkillCost : 0;
			if ((Boolean) pValue) {
				if ((playerCost + skillCost <= fDialog.getAvailableGold()) && (fDialog.getFreeSlotsInRoster() > 0)
					&& checkedRows < maxMercs && (player.getPlayerType() != PlayerType.BIG_GUY || fDialog.getRoster().getMaxBigGuys() > bigGuysOnTeam + boughtBigGuys)) {
					fRowData[pRowIndex][pColumnIndex] = pValue;
					fireTableCellUpdated(pRowIndex, pColumnIndex);
					checkedRows = getCheckedRows();
					if (player.getPlayerType() == PlayerType.BIG_GUY) {
						boughtBigGuys++;
					}
				}
			} else {
				fRowData[pRowIndex][pColumnIndex] = pValue;
				fireTableCellUpdated(pRowIndex, pColumnIndex);
				checkedRows = getCheckedRows();
				if (player.getPlayerType() == PlayerType.BIG_GUY) {
					boughtBigGuys--;
				}
			}
			fDialog.recalculateGold();
		}
		if (pColumnIndex == 4) {
			fRowData[pRowIndex][pColumnIndex] = pValue;
			fireTableCellUpdated(pRowIndex, pColumnIndex);
			int skillCost = StringTool.isProvided(fRowData[pRowIndex][4]) ? mercSkillCost : 0;
			setValueAt(StringTool.formatThousands(playerCost + skillCost), pRowIndex, 3);
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
		List<Object[]> mercenaryList = new ArrayList<>();
		for (RosterPosition pos : fDialog.getRoster().getPositions()) {
			if (PlayerType.STAR != pos.getType()) {
				int playerInPosition = fDialog.getTeam().getNrOfAvailablePlayersInPosition(pos);
				for (int i = 0; i < pos.getQuantity() - playerInPosition; i++) {
					if (pos.getType() != PlayerType.BIG_GUY || fDialog.getRoster().getMaxBigGuys() > bigGuysOnTeam) {
						RosterPlayer player = new RosterPlayer();
						player.updatePosition(pos, fDialog.getClient().getGame().getRules(), getGame().getId());
						player.setName(pos.getName());
						Object[] mercenary = new Object[6];
						mercenary[0] = Boolean.FALSE;
						mercenary[1] = new ImageIcon(
							playerIconFactory.getBasicIcon(fDialog.getClient(), player, true, false, false, false));
						mercenary[2] = pos.getName();
						mercenary[3] = StringTool.formatThousands(pos.getCost() + mercExtraCost);
						mercenary[4] = "";
						mercenary[5] = player;
						mercenaryList.add(mercenary);
					}
				}
			}
		}
		Object[][] mercenaries = mercenaryList.toArray(new Object[mercenaryList.size()][]);
		Arrays.sort(mercenaries, (o1, o2) -> {
			Position position1 = ((Player<?>) o1[5]).getPosition();
			Position position2 = ((Player<?>) o2[5]).getPosition();
			return position1.getCost() - position2.getCost();
		});
		return mercenaries;
	}
	public Game getGame() {
		return fDialog.getClient().getGame();
	}
}
