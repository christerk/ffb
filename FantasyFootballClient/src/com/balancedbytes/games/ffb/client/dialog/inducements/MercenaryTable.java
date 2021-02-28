package com.balancedbytes.games.ffb.client.dialog.inducements;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MercenaryTable extends JTable {

	public MercenaryTable(MercenaryTableModel ab) {
		super(ab);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 4) {
			Player<?> player = (Player<?>) getModel().getValueAt(row, 5);
			List<SkillCategory> cat = new ArrayList<>(
					Arrays.asList(player.getPosition().getSkillCategories(false)));
			List<String> skills = new ArrayList<>();
			skills.add("");

			SkillFactory factory = ((MercenaryTableModel)getModel()).getGame().getFactory(FactoryType.Factory.SKILL);
			factory.getSkills().stream().filter(skill -> cat.contains(skill.getCategory()) && !player.getPosition().hasSkill(skill)).map(Skill::getName).forEach(skills::add);

			JComboBox<String> box = new JComboBox<String>(skills.toArray(new String[0]));
			return new DefaultCellEditor(box);
		}
		return super.getCellEditor(row, column);
	}

}
