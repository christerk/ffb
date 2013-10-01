package com.balancedbytes.games.ffb.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.Team;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailPlayerName extends JPanel {
  
  public static final int HEIGHT = 21;
  public static final int WIDTH = PlayerDetailComponent.WIDTH;
  
  private PlayerDetailComponent fPlayerDetailComponent;
  private BufferedImage fImage;
  
  public PlayerDetailPlayerName(PlayerDetailComponent pPlayerDetailComponent) {
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
    
    if (pPlayer != null) {
      
      setToolTipText(pPlayer.getName());
      
      if (isTeamHome(pPlayer.getTeam())) {
        g2d.setPaint(new GradientPaint(0, 0, new Color(128, 0, 0), WIDTH - 1, 0, Color.WHITE, false));
      } else {
        g2d.setPaint(new GradientPaint(0, 0, new Color(12, 20, 136), WIDTH - 1, 0, Color.WHITE, false));
      }
      g2d.fillRect(0, 0, WIDTH, HEIGHT);
      
      g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));

      g2d.setColor(Color.BLACK);
      g2d.drawString(pPlayer.getName(), 5, 16);

      if (isTeamHome(pPlayer.getTeam())) {
        g2d.setColor(new Color(255, 140, 120));
      } else {
        g2d.setColor(new Color(170, 255, 224));
      }
      g2d.drawString(pPlayer.getName(), 4, 15);

      g2d.setFont(new Font("Sans Serif", Font.PLAIN, 12));
      FontMetrics metrics = g2d.getFontMetrics ();
      Rectangle2D positionTitleBounds = metrics.getStringBounds(pPlayer.getPosition().getName(), g2d);
      g2d.setColor(Color.BLACK);
      
      int x = WIDTH - (int) positionTitleBounds.getWidth() - 5;
      g2d.drawString(pPlayer.getPosition().getName(), x, 15);
  		g2d.setColor(Color.RED);
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
  
  public BufferedImage getImage() {
    return fImage;
  }
  
  private boolean isTeamHome(Team pTeam) {
    return (getPlayerDetailComponent().getGame().getTeamHome() == pTeam);
  }
    
}
