package com.fumbbl.ffb.client.ui.swing;

import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.RenderContext;

import javax.swing.table.TableModel;

public class JTable extends javax.swing.JTable {
	public JTable(DimensionProvider dimensionProvider, TableModel dm, RenderContext renderContext) {
		super(dm);
		dimensionProvider.scaleFont(getTableHeader(), renderContext);
		dimensionProvider.scaleFont(this, renderContext);
	}
}
