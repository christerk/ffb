package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;

import javax.swing.table.TableModel;

public class JTable extends javax.swing.JTable {
	public JTable(DimensionProvider dimensionProvider, TableModel dm) {
		super(dm);
		dimensionProvider.scaleFont(getTableHeader());
		dimensionProvider.scaleFont(this);
	}
}
