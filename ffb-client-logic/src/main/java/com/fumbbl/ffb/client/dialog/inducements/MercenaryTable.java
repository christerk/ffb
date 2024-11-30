package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JComboBox;
import com.fumbbl.ffb.client.ui.swing.JTable;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MercenaryTable extends JTable {

	private final DimensionProvider dimensionProvider;

	public MercenaryTable(DimensionProvider dimensionProvider, MercenaryTableModel ab) {
		super(dimensionProvider, ab);
		this.dimensionProvider = dimensionProvider;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 4) {
			Player<?> player = (Player<?>) getModel().getValueAt(row, 5);
			List<SkillCategory> cat = new ArrayList<>(
				Arrays.asList(player.getPosition().getSkillCategories(false)));
			List<String> skills = new ArrayList<>();
			skills.add("");

			SkillFactory factory = ((MercenaryTableModel) getModel()).getGame().getFactory(FactoryType.Factory.SKILL);
			factory.getSkills().stream().filter(skill ->
					skill.eligible() && cat.contains(skill.getCategory()) && !player.getPosition().hasSkill(skill))
				.map(Skill::getName)
				.forEach(skills::add);

			JComboBox<String> box = new JComboBox<>(dimensionProvider, skills.toArray(new String[0]));
			return new DefaultCellEditor(box);
		}
		return super.getCellEditor(row, column);
	}

}
