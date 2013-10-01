package com.balancedbytes.games.ffb.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.client.util.UtilGraphics;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailHelp extends JPanel {
  
  public static final int HEIGHT = PlayerDetailComponent.HEIGHT;
  public static final int WIDTH = PlayerDetailComponent.WIDTH;
  
  private PlayerDetailComponent fPlayerDetailComponent;
  private BufferedImage fImage;
  
  public PlayerDetailHelp(PlayerDetailComponent pPlayerDetailComponent, boolean pActingPlayerDetail) {
    
    fPlayerDetailComponent = pPlayerDetailComponent;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    
    setLayout(null);
    
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    
    init(pActingPlayerDetail);
    
  }
  
  private void init(boolean pActingPlayer) {
    
    Graphics2D g2d = getImage().createGraphics();
    
    g2d.setColor(Color.GRAY);
    g2d.fillRect(0, 0, WIDTH, HEIGHT);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(1, 1, WIDTH - 2, HEIGHT - 2);
    
    Font font = new Font("Sans Serif", Font.PLAIN, 12);
    
    if (pActingPlayer) {
      UtilGraphics.drawText(g2d, WIDTH / 2, HEIGHT / 2, font, Color.BLACK, UtilGraphics.ALIGN_CENTER, "Select player to show details.");
    } else {
      UtilGraphics.drawText(g2d, WIDTH / 2, HEIGHT / 2, font, Color.BLACK, UtilGraphics.ALIGN_CENTER, "Mouse over Player to show details.");
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
    
}
