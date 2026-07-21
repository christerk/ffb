package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;

import java.awt.Dimension;

class ResolvedLayout {

	final ClientLayout layout;
	final Dimension field;
	final Dimension fieldBase;
	final Dimension sidebar;
	final Dimension box;
	final Dimension score;
	final Dimension log;
	final Dimension chat;
	final Dimension bottomPanel;
	final Dimension rightColumn;

	ResolvedLayout(ClientLayout layout, Dimension field, Dimension fieldBase, Dimension sidebar,
			Dimension box, Dimension score, Dimension log, Dimension chat, Dimension bottomPanel,
			Dimension rightColumn) {
		this.layout = layout;
		this.field = new Dimension(field);
		this.fieldBase = new Dimension(fieldBase);
		this.sidebar = new Dimension(sidebar);
		this.box = new Dimension(box);
		this.score = new Dimension(score);
		this.log = new Dimension(log);
		this.chat = new Dimension(chat);
		this.bottomPanel = new Dimension(bottomPanel);
		this.rightColumn = new Dimension(rightColumn);
	}

	Dimension configuredContentSize() {
		switch (layout) {
			case PORTRAIT:
				int mainWidth = sidebar.width + field.width + sidebar.width;
				return new Dimension(Math.max(mainWidth, bottomPanel.width),
					Math.max(sidebar.height, field.height) + bottomPanel.height);

			case SQUARE:
				int squareMainWidth = sidebar.width + field.width + sidebar.width;
				int squareMainHeight = Math.max(sidebar.height, field.height);
				return new Dimension(squareMainWidth + rightColumn.width,
					Math.max(squareMainHeight, rightColumn.height));

			default:
				int centerWidth = Math.max(field.width, bottomPanel.width);
				return new Dimension(sidebar.width + centerWidth + sidebar.width,
					Math.max(sidebar.height, field.height + bottomPanel.height));
		}
	}
}