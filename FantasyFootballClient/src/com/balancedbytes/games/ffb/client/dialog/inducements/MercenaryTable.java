package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;

@SuppressWarnings("serial")
public class MercenaryTable extends JTable {

	public MercenaryTable(MercenaryTableModel ab) {
		super(ab);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 4) {
			Player<?> player = (Player) getModel().getValueAt(row, 5);
			List<SkillCategory> cat = new ArrayList<>(
					Arrays.asList(player.getPosition().getSkillCategories(false)));
			List<String> skills = new ArrayList<>();
			skills.add("");

			try {
				Field[] fields = SkillConstants.class.getFields();
				for (Field field : fields) {
					int modifiers = field.getModifiers();
					if (Modifier.isStatic(modifiers) && Skill.class.isAssignableFrom(field.getType())) {
						Skill skill = (Skill) field.get(null);
						if (cat.contains(skill.getCategory()) && !player.hasSkill(skill)) {
							skills.add(skill.getName());
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JComboBox<String> box = new JComboBox<String>(skills.toArray(new String[skills.size()]));
			return new DefaultCellEditor(box);
		}
		return super.getCellEditor(row, column);
	}

}
