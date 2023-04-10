package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.ui.swing.JTable;

import javax.swing.table.TableCellEditor;

public class StarPlayerTable extends JTable {

	public StarPlayerTable(DimensionProvider dimensionProvider, StarPlayerTableModel ab) {
		super(dimensionProvider, ab);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);
	}

}
