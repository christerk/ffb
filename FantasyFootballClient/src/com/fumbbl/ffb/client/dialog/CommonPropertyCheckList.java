package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CommonPropertyCheckList extends JList<CommonPropertyCheckListItem> {

	// Handles rendering cells in the list using a checkbox

	public CommonPropertyCheckList(DimensionProvider dimensionProvider, List<CommonProperty> properties, List<CommonProperty> selectedProperties) {

		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("Argument properties must not be empty or null.");
		}

		List<CommonPropertyCheckListItem> checkListItems = new ArrayList<>();
		for (CommonProperty property : properties) {
			CommonPropertyCheckListItem checkListItem = new CommonPropertyCheckListItem(property);
			checkListItem.setSelected(selectedProperties.contains(property));
			checkListItems.add(checkListItem);

		}
		setListData(checkListItems.toArray(new CommonPropertyCheckListItem[0]));

		// Use a CheckListRenderer (see below) to renderer list cells
		setCellRenderer(new CommonPropertyCheckListRenderer(dimensionProvider));
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Add a mouse listener to handle changing selection
		addMouseListener(new CommonPropertyCheckListMouseAdapter());
	}

	public List<CommonProperty> getSelectedProperties() {
		List<CommonProperty> selectedProperties = new ArrayList<>();
		for (int i = 0; i < getModel().getSize(); i++) {
			CommonPropertyCheckListItem item = getModel().getElementAt(i);
			if (item.isSelected()) {
				selectedProperties.add(item.getProperty());
			}
		}
		return selectedProperties;
	}

	private static class CommonPropertyCheckListRenderer extends JPanel implements ListCellRenderer<CommonPropertyCheckListItem> {

		private final JCheckBox fCheckBox;
		private final JLabel fLabel;

		public CommonPropertyCheckListRenderer(DimensionProvider dimensionProvider) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			fCheckBox = new JCheckBox(dimensionProvider);
			add(fCheckBox);
			fLabel = new JLabel(dimensionProvider);
			add(fLabel);
		}

		public Component getListCellRendererComponent(JList<? extends CommonPropertyCheckListItem> pList,
																									CommonPropertyCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
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

	private static class CommonPropertyCheckListMouseAdapter extends MouseAdapter {

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent event) {
			JList<CommonPropertyCheckListItem> list = (JList<CommonPropertyCheckListItem>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			CommonPropertyCheckListItem selectedItem = list.getModel().getElementAt(index);
			selectedItem.setSelected(!selectedItem.isSelected());
			list.repaint(list.getCellBounds(index, index));
		}

	}

}
