package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.Player;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author Kalimar
 */
public class SideBarComponent extends JPanel implements MouseMotionListener {

	private final FantasyFootballClient fClient;
	private final boolean fHomeSide;
	private final PlayerDetailComponent fPlayerDetail;
	private final BoxComponent fBoxComponent;
	private final BoxButtonComponent fBoxButtons;
	private final ResourceComponent fResourceComponent;
	private final TurnDiceStatusComponent fTurnDiceStatusComponent;

	public SideBarComponent(FantasyFootballClient pClient, boolean pHomeSide, DimensionProvider dimensionProvider,
													StyleProvider styleProvider) {
		fClient = pClient;
		fHomeSide = pHomeSide;
		fPlayerDetail = new PlayerDetailComponent(this, dimensionProvider, styleProvider);
		fBoxComponent = new BoxComponent(this, dimensionProvider, styleProvider);
		fBoxButtons = new BoxButtonComponent(this, dimensionProvider, styleProvider);
		fResourceComponent = new ResourceComponent(this, dimensionProvider, styleProvider);
		fTurnDiceStatusComponent = new TurnDiceStatusComponent(this, dimensionProvider, styleProvider);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponents();
		fPlayerDetail.addMouseMotionListener(this);
		fBoxComponent.addMouseMotionListener(this);
		fBoxButtons.addMouseMotionListener(this);
		fResourceComponent.addMouseMotionListener(this);
		fTurnDiceStatusComponent.addMouseMotionListener(this);
	}

	public void initLayout(DimensionProvider dimensionProvider) {
		fPlayerDetail.initLayout();
		fBoxComponent.initLayout();
		fBoxButtons.initLayout();
		fResourceComponent.initLayout();
		fTurnDiceStatusComponent.initLayout();

		Dimension size = dimensionProvider.dimension(DimensionProvider.Component.SIDEBAR);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public BoxComponent getBoxComponent() {
		return fBoxComponent;
	}

	public TurnDiceStatusComponent getTurnDiceStatusComponent() {
		return fTurnDiceStatusComponent;
	}

	public boolean isHomeSide() {
		return fHomeSide;
	}

	public BoxType getOpenBox() {
		return fBoxComponent.getOpenBox();
	}

	public void openBox(BoxType pBox) {
		fBoxButtons.openBox(pBox);
		if (isBoxOpen()) {
			fBoxComponent.openBox(pBox);
		} else {
			fBoxComponent.openBox(pBox);
			removeAll();
			addComponents();
			revalidate();
		}
		fBoxComponent.refresh();
		// close other side (only one side may be open @ any time)
		UserInterface userInterface = getClient().getUserInterface();
		SideBarComponent otherSideBar = isHomeSide() ? userInterface.getSideBarAway() : userInterface.getSideBarHome();
		otherSideBar.closeBox();
	}

	public void closeBox() {
		fBoxButtons.closeBox();
		if (isBoxOpen()) {
			fBoxComponent.closeBox();
			removeAll();
			addComponents();
			revalidate();
		} else {
			fBoxComponent.closeBox();
		}
		fPlayerDetail.refresh();
	}

	private void addComponents() {
		if (isBoxOpen()) {
			add(fBoxComponent);
		} else {
			add(fPlayerDetail);
		}
		add(fBoxButtons);
		add(fTurnDiceStatusComponent);
		add(fResourceComponent);
	}

	public boolean isBoxOpen() {
		return (fBoxComponent.getOpenBox() != null);
	}

	public void init() {
		fBoxButtons.refresh();
		if (isBoxOpen()) {
			fBoxComponent.refresh();
		} else {
			fPlayerDetail.refresh();
		}
		fBoxComponent.initObserver();
		fResourceComponent.init();
		fTurnDiceStatusComponent.init();
	}

	public void refresh() {
		fBoxButtons.refresh();
		if (isBoxOpen()) {
			fBoxComponent.refresh();
		} else {
			fPlayerDetail.refresh();
		}
		fResourceComponent.refresh();
		fTurnDiceStatusComponent.refresh();
	}

	public Player<?> getPlayer() {
		return fPlayerDetail.getPlayer();
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

}
