package com.balancedbytes.games.ffb.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class ResourceComponent extends JPanel {
  
  public static final int WIDTH = 116;
  public static final int HEIGHT = 168;  // 256

  private SideBarComponent fSideBar;
  private BufferedImage fImage;
  private boolean fRefreshNecessary;
  private int fNrOfSlots;
  private ResourceSlot[] fSlots;

  private int fCurrentReRolls;
  private int fCurrentApothecaries;
  private int fCurrentIgor;
  private int fCurrentBribes;
  private int fCurrentBloodweiserBabes;
  private int fCurrentMasterChef;
  private int fCurrentWizard;
  private int fCurrentCards;
  
  private static final int _SLOT_HEIGHT = 40;
  private static final int _SLOT_WIDTH = 56;
  
  private static final Font _NUMBER_FONT = new Font("Sans Serif", Font.BOLD, 16);
  
  public ResourceComponent(SideBarComponent pSideBar) {
    fSideBar = pSideBar;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    fSlots = createResourceSlots();
    fRefreshNecessary = true;
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    ToolTipManager.sharedInstance().registerComponent(this);
  }
  
  private ResourceSlot[] createResourceSlots() {
    ResourceSlot[] resourceSlots = null;
    if (getSideBar().isHomeSide()) {
      resourceSlots = new ResourceSlot[] {
        new ResourceSlot(new Rectangle( 0, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 0, _SLOT_WIDTH, _SLOT_HEIGHT))
      };
    } else {
      resourceSlots = new ResourceSlot[] {
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, 3 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, 2 * (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, (_SLOT_HEIGHT + 2), _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle(_SLOT_WIDTH + 2, 0, _SLOT_WIDTH, _SLOT_HEIGHT)),
        new ResourceSlot(new Rectangle( 0, 0, _SLOT_WIDTH, _SLOT_HEIGHT))
      };
    }
    return resourceSlots;
  }
  
  public SideBarComponent getSideBar() {
    return fSideBar;
  }
  
  private void drawBackground() {
    Graphics2D g2d = fImage.createGraphics();
    IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
    BufferedImage background;
    if (getSideBar().isHomeSide()) {
      background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_RED); 
    } else {
      background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_RESOURCE_BLUE); 
    }
    g2d.drawImage(background, 0, 0, null);
    g2d.dispose();
  }

  private void drawSlot(ResourceSlot pSlot) {
    if ((pSlot != null) && (pSlot.getIconProperty() != null)) {
      IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
      Graphics2D g2d = fImage.createGraphics();
      int x = pSlot.getLocation().x;
      int y = pSlot.getLocation().y;
      BufferedImage resourceIcon = iconCache.getIconByProperty(pSlot.getIconProperty());
      if (getSideBar().isHomeSide()) {      
        x += pSlot.getLocation().width - resourceIcon.getWidth() - 1;
      } else {
      	x += 1;
      }
      y += (pSlot.getLocation().height - resourceIcon.getHeight() + 1) / 2;
      g2d.drawImage(resourceIcon, x, y, null);
      if (!pSlot.isEnabled()) {
        BufferedImage disabledIcon = iconCache.getIconByProperty(IIconProperty.DECORATION_STUNNED);
        x += (resourceIcon.getWidth() - disabledIcon.getWidth()) / 2;
        y += (resourceIcon.getHeight() - disabledIcon.getHeight()) / 2;
        g2d.drawImage(disabledIcon, x, y, null);
      }
      g2d.setFont(_NUMBER_FONT);
      String resourceValue = Integer.toString(pSlot.getValue());
      FontMetrics metrics = g2d.getFontMetrics();
      Rectangle2D bounds = metrics.getStringBounds(resourceValue, g2d);
      y = pSlot.getLocation().y + ((pSlot.getLocation().height + metrics.getHeight()) / 2) - metrics.getDescent();
      if (getSideBar().isHomeSide()) {
        x = pSlot.getLocation().x + 3;
      } else {
        x = pSlot.getLocation().x + pSlot.getLocation().width - (int) bounds.getWidth() - 3;
      }
      g2d.setColor(Color.BLACK);
      g2d.drawString(resourceValue, x + 1, y + 1);
      g2d.setColor(Color.WHITE);
      g2d.drawString(resourceValue, x, y);
      g2d.dispose();
    }
  }
  
  private void updateSlots() {
    
    int slotIndex = 0;
    Game game = getSideBar().getClient().getGame();
    TurnData turnData = getSideBar().isHomeSide() ? game.getTurnDataHome() : game.getTurnDataAway();
    Team team = getSideBar().isHomeSide() ? game.getTeamHome() : game.getTeamAway();
    
    fRefreshNecessary |= (turnData.getReRolls() != fCurrentReRolls);
    fCurrentReRolls = turnData.getReRolls(); 
    if ((team.getReRolls() > 0) || (turnData.getReRolls() > 0)) {
      ResourceSlot reRollSlot = fSlots[slotIndex++];
      reRollSlot.setType(ResourceSlot.TYPE_RE_ROLL);
      fRefreshNecessary |= (turnData.isReRollUsed() == reRollSlot.isEnabled());
      reRollSlot.setEnabled(!turnData.isReRollUsed());
      reRollSlot.setValue(fCurrentReRolls);
      reRollSlot.setIconProperty(IIconProperty.RESOURCE_RE_ROLL);
    }

    fRefreshNecessary |= (turnData.getApothecaries() != fCurrentApothecaries);
    fCurrentApothecaries = turnData.getApothecaries(); 
    if ((team.getApothecaries() > 0) || (turnData.getApothecaries() > 0)) {
      ResourceSlot apothecarySlot = fSlots[slotIndex++];
      apothecarySlot.setType(ResourceSlot.TYPE_APOTHECARY);
      apothecarySlot.setValue(fCurrentApothecaries);
      apothecarySlot.setIconProperty(IIconProperty.RESOURCE_APOTHECARY);
    }

    Inducement igor = turnData.getInducementSet().get(InducementType.IGOR);
    if (igor != null) {
	    fRefreshNecessary |= ((igor.getValue() - igor.getUses()) != fCurrentIgor);
	    fCurrentIgor = igor.getValue() - igor.getUses();
	    if (fCurrentApothecaries > 0) {
	      ResourceSlot igorSlot = fSlots[slotIndex++];
	      igorSlot.setType(ResourceSlot.TYPE_IGOR);
	      igorSlot.setValue(fCurrentIgor);
	      igorSlot.setIconProperty(IIconProperty.RESOURCE_IGOR);
	    }
    }
    
    Inducement bribes = turnData.getInducementSet().get(InducementType.BRIBES);
    if (bribes != null) {
	    fRefreshNecessary |= ((bribes.getValue() - bribes.getUses()) != fCurrentBribes);
	    fCurrentBribes = bribes.getValue() - bribes.getUses();
	    if (fCurrentBribes > 0) {
	      ResourceSlot bribesSlot = fSlots[slotIndex++];
	      bribesSlot.setType(ResourceSlot.TYPE_BRIBE);
	      bribesSlot.setValue(fCurrentBribes);
	      bribesSlot.setIconProperty(IIconProperty.RESOURCE_BRIBE);
	    }
    }

    Inducement bloodweiserBabes = turnData.getInducementSet().get(InducementType.BLOODWEISER_BABES);
    if (bloodweiserBabes != null) {
	    fRefreshNecessary |= ((bloodweiserBabes.getValue() - bloodweiserBabes.getUses()) != fCurrentBloodweiserBabes);
	    fCurrentBloodweiserBabes = bloodweiserBabes.getValue() - bloodweiserBabes.getUses();
	    if (fCurrentBloodweiserBabes > 0) {
	      ResourceSlot bloodweiserBabesSlot = fSlots[slotIndex++];
	      bloodweiserBabesSlot.setType(ResourceSlot.TYPE_BLOODWEISER_BABE);
	      bloodweiserBabesSlot.setValue(fCurrentBloodweiserBabes);
	      bloodweiserBabesSlot.setIconProperty(IIconProperty.RESOURCE_BLOODWEISER_BABE);
	    }
    }

    Inducement masterChef = turnData.getInducementSet().get(InducementType.MASTER_CHEF);
    if (masterChef != null) {
	    fRefreshNecessary |= ((masterChef.getValue() - masterChef.getUses()) != fCurrentMasterChef);
	    fCurrentMasterChef = masterChef.getValue() - masterChef.getUses();
	    if (fCurrentMasterChef > 0) {
	      ResourceSlot masterChefSlot = fSlots[slotIndex++];
	      masterChefSlot.setType(ResourceSlot.TYPE_MASTER_CHEF);
	      masterChefSlot.setValue(fCurrentMasterChef);
	      masterChefSlot.setIconProperty(IIconProperty.RESOURCE_MASTER_CHEF);
	    }
    }

    Inducement wizard = turnData.getInducementSet().get(InducementType.WIZARD);
    if (wizard != null) {
	    fRefreshNecessary |= ((wizard.getValue() - wizard.getUses()) != fCurrentWizard);
	    fCurrentWizard = wizard.getValue() - wizard.getUses();
	    if (fCurrentWizard > 0) {
	      ResourceSlot wizardSlot = fSlots[slotIndex++];
	      wizardSlot.setType(ResourceSlot.TYPE_WIZARD);
	      wizardSlot.setValue(fCurrentWizard);
	      wizardSlot.setIconProperty(IIconProperty.RESOURCE_WIZARD);
	    }
    }

    Card[] availableCards = turnData.getInducementSet().getAvailableCards();
    fRefreshNecessary |= (availableCards.length != fCurrentCards);
    fCurrentCards = availableCards.length;
    if (fCurrentCards > 0) {
      ResourceSlot bribesSlot = fSlots[slotIndex++];
      bribesSlot.setType(ResourceSlot.TYPE_CARD);
      bribesSlot.setValue(fCurrentCards);
      bribesSlot.setIconProperty(IIconProperty.RESOURCE_CARD);
    }
    
    fNrOfSlots = slotIndex;
    
  }
  
  
  public void init() {
  	fRefreshNecessary = true;
  	refresh();
  }
  
  public void refresh() {
    Game game = getSideBar().getClient().getGame();
    if (game.getHalf() > 0) {
      updateSlots();
      if (fRefreshNecessary) {
        drawBackground();
        for (int i = 0; i < fNrOfSlots; i++) {
          drawSlot(fSlots[i]);
        }
        repaint();
        fRefreshNecessary = false;
      }
    } else {
      drawBackground();
      repaint();
    }
  }
  
  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
  }
  
  public String getToolTipText(MouseEvent pMouseEvent) {
    String toolTip = null;
    for (int i = 0; (toolTip == null) && (i < fSlots.length); i++) {
      if (fSlots[i].getLocation().contains(pMouseEvent.getPoint())) {
        toolTip = fSlots[i].getToolTip();
      }
    }
    return toolTip;
  }
      
}
