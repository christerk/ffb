package com.balancedbytes.games.ffb.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class PlayerDetailPlayerStats extends JPanel {
  
  public static final int WIDTH = PlayerDetailPlayerName.WIDTH;
  public static final int HEIGHT = 32;
  
  private static final int _DOUBLE_STAT_BOX_WITDH = 58;
  private static final int _STAT_BOX_WIDTH = 40; 
  
  private PlayerDetailComponent fPlayerDetailComponent;
  private BufferedImage fImage;
  
  public PlayerDetailPlayerStats(PlayerDetailComponent pPlayerDetailComponent) {
    fPlayerDetailComponent = pPlayerDetailComponent;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
  }
  
  public void refresh(Player pPlayer) {
    
    Game game = getPlayerDetailComponent().getClient().getGame();
    Graphics2D g2d = getImage().createGraphics();

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, WIDTH, HEIGHT);

    if (pPlayer != null) {
      
      boolean moveIsRed = false;
      int moveLeft = pPlayer.getMovement();
      ActingPlayer actingPlayer = getPlayerDetailComponent().getClient().getGame().getActingPlayer();
      if (pPlayer == actingPlayer.getPlayer()) {
        moveLeft -= actingPlayer.getCurrentMove();
        if (actingPlayer.isGoingForIt() && (moveLeft <= 0)) {
          moveIsRed = true;
          if (pPlayer.hasSkill(Skill.SPRINT)) {
            moveLeft = 3 + moveLeft; 
          } else {
            moveLeft = 2 + moveLeft;
          }
        }
      }
      drawDoubleStatBox(0, "MA", pPlayer.getMovement(), moveIsRed, moveLeft);
      
      drawStatBox(_DOUBLE_STAT_BOX_WITDH + 2, "ST", pPlayer.getStrength());
      drawStatBox(_DOUBLE_STAT_BOX_WITDH + _STAT_BOX_WIDTH + 4, "AG", pPlayer.getAgility());
      drawStatBox(_DOUBLE_STAT_BOX_WITDH + (_STAT_BOX_WIDTH * 2) + 6, "AV", pPlayer.getArmour());
      
      PlayerResult statistics = game.getGameResult().getPlayerResult(pPlayer);
      drawDoubleStatBox(_DOUBLE_STAT_BOX_WITDH + (_STAT_BOX_WIDTH * 3) + 8, "SPP", statistics.getOldSpps(), false, statistics.totalEarnedSpps());
      
    }

    g2d.dispose();
    repaint();
    
  }
  
  private void drawDoubleStatBox(int pX, String pStat, int pValue, boolean pCurrentIsRed, int pCurrentValue) {
    Graphics2D g2d = getImage().createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.fillRect(pX, 0, _DOUBLE_STAT_BOX_WITDH, HEIGHT);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(pX + 2, 15, (_DOUBLE_STAT_BOX_WITDH - 6) / 2, 15);
    g2d.fillRect(pX + (_DOUBLE_STAT_BOX_WITDH / 2) + 1, 15, (_DOUBLE_STAT_BOX_WITDH - 6) / 2, 15);
    drawText(pX + (_DOUBLE_STAT_BOX_WITDH / 2), 12, Color.WHITE, pStat);
    drawNumber(pX + ((_DOUBLE_STAT_BOX_WITDH - 6) / 4) + 3, 27, Color.BLACK, pValue);
    Color currentColor = pCurrentIsRed ? Color.RED : Color.BLACK;
    drawNumber(pX + (((_DOUBLE_STAT_BOX_WITDH - 6) / 4) * 3) + 5, 27, currentColor, pCurrentValue);
    g2d.dispose();
  }

  private void drawStatBox(int pX, String pStat, int pValue) {
    Graphics2D g2d = getImage().createGraphics();
    g2d.setColor(Color.BLACK);
    g2d.fillRect(pX, 0, _STAT_BOX_WIDTH, HEIGHT);
    g2d.setColor(Color.WHITE);
    g2d.fillRect(pX + 2, 15, _STAT_BOX_WIDTH - 4, 15);
    drawText(pX + (_STAT_BOX_WIDTH / 2), 12, Color.WHITE, pStat);
    drawNumber(pX + (_STAT_BOX_WIDTH / 2), 27, Color.BLACK, pValue);
    g2d.dispose();
  }

  private void drawNumber(int pX, int pY, Color pColor, int pNumber) {
    drawText(pX, pY, pColor, Integer.toString(pNumber));
  }

  private void drawText(int pX, int pY, Color pColor, String pText) {
    Graphics2D g2d = getImage().createGraphics();
    g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
    FontMetrics metrics = g2d.getFontMetrics();
    Rectangle2D numberBounds = metrics.getStringBounds(pText, g2d);
    int x = (int) (pX - (numberBounds.getWidth() / 2));
    g2d.setColor(pColor);
    g2d.drawString(pText, x, pY);
    g2d.dispose();
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
