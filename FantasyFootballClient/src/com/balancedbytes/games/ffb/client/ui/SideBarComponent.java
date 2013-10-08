package com.balancedbytes.games.ffb.client.ui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class SideBarComponent extends JPanel implements MouseMotionListener {
  
  public static final int WIDTH = 116;
  public static final int HEIGHT = BoxComponent.HEIGHT + BoxButtonComponent.HEIGHT + ResourceComponent.HEIGHT + TurnDiceStatusComponent.HEIGHT;  // 708 
  
  private FantasyFootballClient fClient;
  private boolean fHomeSide;
  private PlayerDetailComponent fPlayerDetail;
  private BoxComponent fBoxComponent;
  private BoxButtonComponent fBoxButtons;
  private ResourceComponent fResourceComponent;
  private TurnDiceStatusComponent fTurnDiceStatusComponent;
  
  public SideBarComponent(FantasyFootballClient pClient, boolean pHomeSide) {
    fClient = pClient;
    fHomeSide = pHomeSide;
    fPlayerDetail = new PlayerDetailComponent(this);
    fBoxComponent = new BoxComponent(this);
    fBoxButtons = new BoxButtonComponent(this);
    fResourceComponent = new ResourceComponent(this);
    fTurnDiceStatusComponent = new TurnDiceStatusComponent(this);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    addComponents();
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    fPlayerDetail.addMouseMotionListener(this);
    fBoxComponent.addMouseMotionListener(this);
    fBoxButtons.addMouseMotionListener(this);
    fResourceComponent.addMouseMotionListener(this);
    fTurnDiceStatusComponent.addMouseMotionListener(this);
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
  
  public void updatePlayer(Player pPlayer) {
    fPlayerDetail.setPlayer(pPlayer);
    fPlayerDetail.refresh();
  }
  
  public Player getPlayer() {
    return fPlayerDetail.getPlayer();
  }
  
  public void mouseMoved(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent); 
  }
  
  public void mouseDragged(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent); 
  }
  
}
