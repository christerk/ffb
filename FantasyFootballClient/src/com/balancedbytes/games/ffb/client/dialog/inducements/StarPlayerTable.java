package com.balancedbytes.games.ffb.client.dialog.inducements;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class StarPlayerTable extends JTable {

	public StarPlayerTable(StarPlayerTableModel ab) {
		super(ab);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);
	}

}
