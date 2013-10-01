package com.balancedbytes.games.ffb.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputListener;

import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.layer.FieldLayerBloodspots;
import com.balancedbytes.games.ffb.client.layer.FieldLayerField;
import com.balancedbytes.games.ffb.client.layer.FieldLayerMarker;
import com.balancedbytes.games.ffb.client.layer.FieldLayerOverPlayers;
import com.balancedbytes.games.ffb.client.layer.FieldLayerPlayers;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeGrid;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeRuler;
import com.balancedbytes.games.ffb.client.layer.FieldLayerTeamLogo;
import com.balancedbytes.games.ffb.client.layer.FieldLayerUnderPlayers;
import com.balancedbytes.games.ffb.client.state.ClientState;

/**
 * @author j129340
 * 
 */
@SuppressWarnings("serial")
public class FieldComponent extends JPanel implements MouseInputListener {

	private FantasyFootballClient fClient;

	private FieldLayerField fLayerField;
  private FieldLayerTeamLogo fLayerTeamLogo;
	private FieldLayerBloodspots fLayerBloodspots;
  private FieldLayerRangeGrid fLayerRangeGrid;
  private FieldLayerMarker fLayerMarker;
	private FieldLayerUnderPlayers fLayerUnderPlayers;
	private FieldLayerPlayers fLayerPlayers;
	private FieldLayerOverPlayers fLayerOverPlayers;
	private FieldLayerRangeRuler fLayerRangeRuler;
	private BufferedImage fImage;

	public FieldComponent(FantasyFootballClient pClient) {

		fClient = pClient;
		fLayerField = new FieldLayerField(pClient);
    fLayerTeamLogo = new FieldLayerTeamLogo(pClient);
		fLayerBloodspots = new FieldLayerBloodspots(pClient);
		fLayerRangeGrid = new FieldLayerRangeGrid(pClient);
		fLayerMarker = new FieldLayerMarker(pClient);
		fLayerUnderPlayers = new FieldLayerUnderPlayers(pClient);
		fLayerPlayers = new FieldLayerPlayers(pClient);
		fLayerOverPlayers = new FieldLayerOverPlayers(pClient);
		fLayerRangeRuler = new FieldLayerRangeRuler(pClient);

		fImage = new BufferedImage(FieldLayer.FIELD_IMAGE_WIDTH, FieldLayer.FIELD_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		Dimension size = new Dimension(FieldLayer.FIELD_IMAGE_WIDTH, FieldLayer.FIELD_IMAGE_HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		addMouseListener(this);
		addMouseMotionListener(this);
		
    ToolTipManager.sharedInstance().registerComponent(this);

		refresh();

	}

	public FieldLayerField getLayerField() {
		return fLayerField;
	}

	public FieldLayerTeamLogo getLayerTeamLogo() {
    return fLayerTeamLogo;
  }

  public FieldLayerBloodspots getLayerBloodspots() {
    return fLayerBloodspots;
  }
  
  public FieldLayerRangeGrid getLayerRangeGrid() {
    return fLayerRangeGrid;
  }
  
  public FieldLayerMarker getLayerMarker() {
    return fLayerMarker;
  }
  
	public FieldLayerUnderPlayers getLayerUnderPlayers() {
		return fLayerUnderPlayers;
	}

	public FieldLayerPlayers getLayerPlayers() {
		return fLayerPlayers;
	}

	public FieldLayerOverPlayers getLayerOverPlayers() {
		return fLayerOverPlayers;
	}

	public FieldLayerRangeRuler getLayerRangeRuler() {
    return fLayerRangeRuler;
  }
	
	public void refresh() {

		Rectangle updatedArea = combineRectangles(
		  new Rectangle[] {
		    getLayerField().fetchUpdatedArea(),
        getLayerTeamLogo().fetchUpdatedArea(),
				getLayerBloodspots().fetchUpdatedArea(),
				getLayerRangeGrid().fetchUpdatedArea(),
        getLayerMarker().fetchUpdatedArea(),
				getLayerUnderPlayers().fetchUpdatedArea(),
				getLayerPlayers().fetchUpdatedArea(),
				getLayerOverPlayers().fetchUpdatedArea(),
				getLayerRangeRuler().fetchUpdatedArea()
		  }
		);

		if (updatedArea != null) {
		  refresh(updatedArea);
		}

	}

	public void refresh(Rectangle pUpdatedArea) {

    Graphics2D g2d = fImage.createGraphics();

    if (pUpdatedArea != null) {
      g2d.setClip(pUpdatedArea.x, pUpdatedArea.y, pUpdatedArea.width, pUpdatedArea.height);
		}

		g2d.drawImage(getLayerField().getImage(), 0, 0, null);
    g2d.drawImage(getLayerTeamLogo().getImage(), 0, 0, null);
		g2d.drawImage(getLayerBloodspots().getImage(), 0, 0, null);
    g2d.drawImage(getLayerRangeGrid().getImage(), 0, 0, null);
    g2d.drawImage(getLayerMarker().getImage(), 0, 0, null);
		g2d.drawImage(getLayerUnderPlayers().getImage(), 0, 0, null);
		g2d.drawImage(getLayerPlayers().getImage(), 0, 0, null);
		g2d.drawImage(getLayerOverPlayers().getImage(), 0, 0, null);
    g2d.drawImage(getLayerRangeRuler().getImage(), 0, 0, null);

//    g2d.setColor(Color.RED);
//    g2d.drawRect(pUpdatedArea.x, pUpdatedArea.y, pUpdatedArea.width - 1, pUpdatedArea.height - 1);

    g2d.dispose();
    
    if (pUpdatedArea != null) {
      repaint(pUpdatedArea);
    } else {
      repaint();
		}

	}
	
	public void init() {
	  getLayerField().init();
    getLayerTeamLogo().init();
	  getLayerBloodspots().init();
	  getLayerRangeGrid().init();
    getLayerMarker().init();
	  getLayerUnderPlayers().init();
	  getLayerPlayers().init();
	  getLayerOverPlayers().init();
	  getLayerRangeRuler().init();
	  refresh();
	}

	private Rectangle combineRectangles(Rectangle[] pRectangles) {
		Rectangle result = null;
		for (int i = 0; i < pRectangles.length; i++) {
			if (pRectangles[i] != null) {
				if (result != null) {
					result.add(pRectangles[i]);
				} else {
					result = pRectangles[i];
				}
			}
		}
		return result;
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}
	
	// MouseMotionListener
	public void mouseMoved(MouseEvent pMouseEvent) {
	  getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseMoved(pMouseEvent);
		}
	}

	// MouseMotionListener
	public void mouseDragged(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseDragged(pMouseEvent);
		}
	}

	// MouseListener
	public void mouseClicked(MouseEvent pMouseEvent) {
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseClicked(pMouseEvent);
		}
	}

	// MouseListener
	public void mouseEntered(MouseEvent pMouseEvent) {
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseEntered(pMouseEvent);
		}
	}

	// MouseListener
	public void mouseExited(MouseEvent pMouseEvent) {
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseExited(pMouseEvent);
		}
	}

	// MouseListener
	public void mousePressed(MouseEvent pMouseEvent) {
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mousePressed(pMouseEvent);
		}
	}

	// MouseListener
	public void mouseReleased(MouseEvent pMouseEvent) {
		ClientState uiState = getClient().getClientState();
		if (uiState != null) {
			uiState.mouseReleased(pMouseEvent);
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}
	
	public BufferedImage getImage() {
		return fImage;
	}
	
}
