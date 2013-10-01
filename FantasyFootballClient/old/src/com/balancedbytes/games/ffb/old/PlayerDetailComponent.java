package com.balancedbytes.games.ffb.old;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailComponent extends JPanel {
  
  // public static final int WIDTH = PlayerDetailPlayerStats.WIDTH + PlayerDetailPlayerPortrait.WIDTH + PlayerDetailPlayerSkills.WIDTH + 4;
  public static final int WIDTH = PlayerDetailPlayerPortrait.WIDTH + PlayerDetailPlayerSkills.WIDTH + 2;
  // public static final int HEIGHT = PlayerDetailPlayerPortrait.HEIGHT + PlayerDetailPlayerName.HEIGHT + 2;
  public static final int HEIGHT = PlayerDetailPlayerPortrait.HEIGHT + PlayerDetailPlayerStats.HEIGHT + PlayerDetailPlayerName.HEIGHT + 4;
  
  private FantasyFootballClient fClient;
  private Player fPlayer;
  private boolean fActingPayerDetail;
  private PlayerDetailPlayerPortrait fPlayerPortrait;
  private PlayerDetailPlayerName fPlayerName;
  // private PlayerDetailPlayerStats fPlayerStats;
  private PlayerDetailPlayerStats fPlayerStats;
  private PlayerDetailPlayerSkills fPlayerSkills;
  private JPanel fDetailPanel;
  private JPanel fHelpPanel;
  
  public PlayerDetailComponent(FantasyFootballClient pClient, boolean pActingPayerDetail) {
    
    fClient = pClient;
    fActingPayerDetail = pActingPayerDetail;
    fPlayerPortrait = new PlayerDetailPlayerPortrait(this);
    fPlayerName = new PlayerDetailPlayerName(this);
    // fPlayerStats = new PlayerDetailPlayerStats(this);
    fPlayerStats = new PlayerDetailPlayerStats(this);
    fPlayerSkills = new PlayerDetailPlayerSkills(this);
    
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
//    innerPanel.add(fPlayerStats);
//    innerPanel.add(Box.createHorizontalStrut(2));
    innerPanel.add(fPlayerPortrait);
    innerPanel.add(Box.createHorizontalStrut(2));
    innerPanel.add(fPlayerSkills);
    
    fDetailPanel = new JPanel();
    fDetailPanel.setLayout(new BoxLayout(fDetailPanel, BoxLayout.Y_AXIS));
    fDetailPanel.add(fPlayerName);
    fDetailPanel.add(Box.createVerticalStrut(2));
    fDetailPanel.add(fPlayerStats);
    fDetailPanel.add(Box.createVerticalStrut(2));
    fDetailPanel.add(innerPanel);
    
    fHelpPanel = new PlayerDetailHelp(this, pActingPayerDetail);
    
    setLayout(new BorderLayout());
    add(fDetailPanel, BorderLayout.CENTER);
    
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    
    refresh();
    
  }
  
  public boolean isActingPayerDetail() {
    return fActingPayerDetail;
  }
  
  public void refresh() {
    if (fPlayer != null) {
      remove(fHelpPanel);
      add(fDetailPanel, BorderLayout.CENTER);
      fPlayerName.refresh(fPlayer);
      fPlayerStats.refresh(fPlayer);
      fPlayerPortrait.refresh(fPlayer);
      fPlayerSkills.refresh(fPlayer);
    } else {
      remove(fDetailPanel);
      add(fHelpPanel, BorderLayout.CENTER);
    }
    validate();
    repaint();
  }
  
  public void refreshStats() {
    if (getPlayer() != null) {
      fPlayerStats.refresh(getPlayer());
      repaint();
    }
  }
  
  public Player getPlayer() {
    return fPlayer;
  }
  
  public void setPlayer(Player pPlayer) {
    fPlayer = pPlayer;
  }
  
  protected UserInterface getUserInterface() {
    return getClient().getUserInterface(); 
  }
  
  protected Game getGame() {
    return getClient().getGame();
  }
 
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
}
