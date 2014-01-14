package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;

@SuppressWarnings("serial")
public class MercenaryTable extends JTable {

	public MercenaryTable(MercenaryTableModel ab) {
		super(ab);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 4) {
			Player player = (Player) getModel().getValueAt(row, 5);
			ArrayList<SkillCategory> cat = new ArrayList<SkillCategory>(Arrays.asList(player.getPosition().getSkillCategories(false)));
			ArrayList<String> skills = new ArrayList<String>();
			skills.add("");
			for (Skill skill : Skill.values()) {
				if (cat.contains(skill.getCategory())) {
					if (!player.hasSkill(skill)) {
						skills.add(skill.getName());
					}
				}
			}
			JComboBox<String> box = new JComboBox<String>(skills.toArray(new String[skills.size()]));
			return new DefaultCellEditor(box);
		}
		return super.getCellEditor(row, column);
	}

}
