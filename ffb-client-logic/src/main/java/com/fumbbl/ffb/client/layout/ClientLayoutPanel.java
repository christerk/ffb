package com.fumbbl.ffb.client.layout;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutPanel extends JPanel {

	private final Component fieldComponent;
	private final Component homeSidebar;
	private final Component awaySidebar;
	private final Component scoreBar;
	private final Component log;
	private final Component chat;

	public ClientLayoutPanel(Component fieldComponent, Component homeSidebar, Component awaySidebar,
	                         Component scoreBar, Component log, Component chat) {
		super(null);
		this.fieldComponent = fieldComponent;
		this.homeSidebar = homeSidebar;
		this.awaySidebar = awaySidebar;
		this.scoreBar = scoreBar;
		this.log = log;
		this.chat = chat;

		add(homeSidebar);
		add(fieldComponent);
		add(awaySidebar);
		add(scoreBar);
		add(log);
		add(chat);
	}

	public void apply(ClientLayoutResult layoutResult) {
		Dimension preferredSize = layoutResult.preferredSize();
		setMinimumSize(preferredSize);
		setPreferredSize(preferredSize);
		setMaximumSize(preferredSize);
		setSize(preferredSize);

		applyBounds(fieldComponent, layoutResult.fieldBounds());
		applyBounds(homeSidebar, layoutResult.homeSidebarBounds());
		applyBounds(awaySidebar, layoutResult.awaySidebarBounds());
		applyBounds(scoreBar, layoutResult.scoreBarBounds());
		applyBounds(log, layoutResult.logBounds());
		applyBounds(chat, layoutResult.chatBounds());

		revalidate();
		repaint();
	}

	private void applyBounds(Component component, Rectangle bounds) {
		component.setBounds(bounds);
	}
}