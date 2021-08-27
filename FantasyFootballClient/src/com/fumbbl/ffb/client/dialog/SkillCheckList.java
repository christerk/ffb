package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.model.skill.Skill;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SkillCheckList extends JList<SkillCheckListItem> {

	// Handles rendering cells in the list using a check box

	public SkillCheckList(List<Skill> skills, int minSelects,
	                      int maxSelects, boolean preSelected, JButton selectButton) {

		if (skills == null || skills.isEmpty()) {
			throw new IllegalArgumentException("Argument skills must not be empty or null.");
		}

		List<SkillCheckListItem> checkListItems = new ArrayList<>();
		for (Skill skill : skills) {
			SkillCheckListItem checkListItem = new SkillCheckListItem(skill);
			checkListItem.setSelected((skills.size() == 1) || preSelected);
			checkListItems.add(checkListItem);

		}
		setListData(checkListItems.toArray(new SkillCheckListItem[0]));

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new SkillCheckListRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection
		addMouseListener(new SkillCheckListMouseAdapter(minSelects, maxSelects, selectButton));
	}

	public Skill getSkillAtIndex(int pIndex) {
		SkillCheckListItem checkListItem = getModel().getElementAt(pIndex);
		if (checkListItem != null) {
			return checkListItem.getSkill();
		} else {
			return null;
		}
	}

	public Skill[] getSelectedSkills() {
		List<Skill> selectedSkills = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			SkillCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedSkills.add(item.getSkill());
			}
		}
		return selectedSkills.toArray(new Skill[0]);
	}

	public List<Integer> getSelectedIndexes() {
		List<Integer> selectedIndexes = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			SkillCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedIndexes.add(i);
			}
		}

		return selectedIndexes;
	}

	private int findNrOfSelectedItems() {
		int nrOfSelectedItems = 0;
		for (int i = 0; i < getModel().getSize(); i++) {
			SkillCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				nrOfSelectedItems++;
			}
		}
		return nrOfSelectedItems;
	}

	private static class SkillCheckListRenderer extends JPanel implements ListCellRenderer<SkillCheckListItem> {

		private final JCheckBox fCheckBox;
		private final JLabel fLabel;

		public SkillCheckListRenderer() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			fCheckBox = new JCheckBox();
			add(fCheckBox);
			fLabel = new JLabel();
			add(fLabel);
		}

		public Component getListCellRendererComponent(JList<? extends SkillCheckListItem> pList,
		                                              SkillCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
			setEnabled(pList.isEnabled());
			setFont(pList.getFont());
			setBackground(pList.getBackground());
			setForeground(pList.getForeground());
			fCheckBox.setBackground(pList.getBackground());
			fCheckBox.setSelected(pValue.isSelected());
			fLabel.setText(pValue.getText());
			return this;
		}
	}

	private class SkillCheckListMouseAdapter extends MouseAdapter {

		private final int fMinSelects;
		private final int fMaxSelects;
		private final JButton fSelectButton;

		public SkillCheckListMouseAdapter(int minSelects, int maxSelects, JButton selectButton) {
			fMinSelects = minSelects;
			fMaxSelects = maxSelects;
			fSelectButton = selectButton;
		}

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent event) {
			JList<SkillCheckListItem> list = (JList<SkillCheckListItem>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			SkillCheckListItem selectedItem = list.getModel().getElementAt(index);
			if (!selectedItem.isSelected()) {
				if (fMaxSelects > 1) {
					int nrOfSelectedItems = findNrOfSelectedItems();
					if (nrOfSelectedItems < fMaxSelects) {
						selectedItem.setSelected(true);
					}
				} else {
					for (int i = 0; i < list.getModel().getSize(); i++) {
						SkillCheckListItem item = list.getModel().getElementAt(i);
						if (item.isSelected()) {
							item.setSelected(false);
							list.repaint(list.getCellBounds(i, i));
						}
					}
					selectedItem.setSelected(true);
				}
			} else {
				selectedItem.setSelected(false);
			}
			int nrOfSelectedItems = findNrOfSelectedItems();
			if (fMinSelects > 0) {
				fSelectButton.setEnabled(nrOfSelectedItems >= fMinSelects);
			} else {
				fSelectButton.setEnabled(nrOfSelectedItems > 0);
			}
			list.repaint(list.getCellBounds(index, index));
		}

	}

}
