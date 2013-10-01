package com.balancedbytes.games.ffb.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.client.UserInterface;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailPlayerPortrait extends JPanel {
  
  public static final int WIDTH = 114;
  public static final int HEIGHT = 177;
  
  private PlayerDetailComponent fPlayerDetailComponent;
  private BufferedImage fImage;
  
  public PlayerDetailPlayerPortrait(PlayerDetailComponent pPlayerDetailComponent) {
    fPlayerDetailComponent = pPlayerDetailComponent;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
  }
  
  public void refresh(Player pPlayer) {
    
    Graphics2D g2d = getImage().createGraphics();

    BufferedImage playerPortrait = null;
    if (pPlayer != null) {
      String portraitUrl = PlayerIconFactory.getPlayerPortraitUrl(pPlayer);
      playerPortrait = getUserInterface().getIconCache().getIconByUrl(portraitUrl);
    }
    if (playerPortrait != null) {
      g2d.drawImage(playerPortrait, 0, 0, null);
    } else {
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, WIDTH, HEIGHT);
    }

    g2d.dispose();
    repaint();
    
  }
  
  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
  }
  
  public PlayerDetailComponent getPlayerDetailComponent() {
    return fPlayerDetailComponent;
  }
  
  private UserInterface getUserInterface() {
    return getPlayerDetailComponent().getUserInterface();
  }
  
  public BufferedImage getImage() {
    return fImage;
  }
    
}
