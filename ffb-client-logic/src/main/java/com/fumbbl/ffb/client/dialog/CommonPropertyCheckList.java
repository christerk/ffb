package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JCheckBox;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonPropertyCheckList extends JList<CommonPropertyCheckListItem> {

	// Handles rendering cells in the list using a checkbox

	public CommonPropertyCheckList(DimensionProvider dimensionProvider, List<CommonProperty> properties, List<CommonProperty> selectedProperties) {

		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("Argument properties must not be empty or null.");
		}

		List<CommonPropertyCheckListItem> checkListItems = new ArrayList<>();
		properties.stream().collect(Collectors.groupingBy(CommonProperty::getCategory)).entrySet().stream()
			.sorted(Map.Entry.comparingByKey()).forEach(
				entry -> {
					checkListItems.add(new CommonPropertyCheckListItem(entry.getKey()));
					entry.getValue().stream().sorted(Comparator.comparing(CommonProperty::getDialogValue)).map(property -> {
						CommonPropertyCheckListItem item = new CommonPropertyCheckListItem(property);
						item.setSelected(selectedProperties.contains(property));
						return item;
					}).forEach(checkListItems::add);
				}
			);

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

		private final DimensionProvider dimensionProvider;
		private final JLabel fLabel;
		private final JLabel categoryLabel;


		public CommonPropertyCheckListRenderer(DimensionProvider dimensionProvider) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.dimensionProvider = dimensionProvider;
			fLabel = new JLabel(dimensionProvider, RenderContext.ON_PITCH);
			categoryLabel = new JLabel(dimensionProvider, RenderContext.ON_PITCH);
			Font oldFont = categoryLabel.getFont();
			categoryLabel.setFont(new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize()));
		}

		public Component getListCellRendererComponent(JList<? extends CommonPropertyCheckListItem> pList,
																									CommonPropertyCheckListItem pValue, int pIndex, boolean pIsSelected, boolean pCellHasFocus) {
			setFont(pList.getFont());
			setBackground(pList.getBackground());
			setForeground(pList.getForeground());
			removeAll();

			if (pValue.getProperty() != null) {
				setEnabled(pList.isEnabled());
				JCheckBox fCheckBox = new JCheckBox(dimensionProvider, RenderContext.ON_PITCH);
				add(fCheckBox);
				fCheckBox.setBackground(pList.getBackground());
				fCheckBox.setSelected(pValue.isSelected());
				add(fLabel);
				fLabel.setText(pValue.getText());
			} else {
				setEnabled(false);
				add(Box.createHorizontalGlue());
				add(categoryLabel);
				categoryLabel.setText("----- " + pValue.getCategory() + " -----");
				add(Box.createHorizontalGlue());
			}
			return this;
		}
	}

	private static class CommonPropertyCheckListMouseAdapter extends MouseAdapter {

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent event) {
			JList<CommonPropertyCheckListItem> list = (JList<CommonPropertyCheckListItem>) event.getSource();
			int index = list.locationToIndex(event.getPoint());
			CommonPropertyCheckListItem selectedItem = list.getModel().getElementAt(index);
			if (selectedItem.getProperty() != null) {
				selectedItem.setSelected(!selectedItem.isSelected());
				list.repaint(list.getCellBounds(index, index));
			}
		}

	}

}
