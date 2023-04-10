package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.client.ui.swing.JTable;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;

/**
 * 
 * @author Kalimar
 */
public class UtilClientJTable {

	// sets the width of the visible column specified to be just wide enugh
	// to show the column head and the widest cell in the column + (2 * pMargin)
	// pixels.
	public static void packTableColumn(JTable pTable, int pColIndex, int pMargin) {

		DefaultTableColumnModel colModel = (DefaultTableColumnModel) pTable.getColumnModel();
		TableColumn col = colModel.getColumn(pColIndex);
		int width = 0;

		// get width of the column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null) {
			renderer = pTable.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(pTable, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// get maximum width of the column data
		for (int row = 0; row < pTable.getRowCount(); row++) {
			renderer = pTable.getCellRenderer(row, pColIndex);
			comp = renderer.getTableCellRendererComponent(pTable, pTable.getValueAt(row, pColIndex), false, false, row,
					pColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// add the margin left and right
		width += 2 * pMargin;

		// set the width and lock it
		col.setPreferredWidth(width);
		col.setMinWidth(width);
		col.setMaxWidth(width);

	}

}
