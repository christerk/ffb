package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.ui.swing.JTable;

import javax.swing.table.TableCellEditor;

public class StarPlayerTable extends JTable {

	public StarPlayerTable(DimensionProvider dimensionProvider, StarPlayerTableModel ab, RenderContext renderContext) {
		super(dimensionProvider, ab, renderContext);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);
	}

}
