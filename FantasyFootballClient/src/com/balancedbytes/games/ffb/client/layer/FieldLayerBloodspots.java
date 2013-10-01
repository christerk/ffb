package com.balancedbytes.games.ffb.client.layer;

import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.FieldModelChangeEvent;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.FieldModel;



/**
 * 
 * @author Kalimar
 */
public class FieldLayerBloodspots extends FieldLayer {
  
	public FieldLayerBloodspots(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void drawBloodspot(BloodSpot pBloodspot) {
    BufferedImage icon = getClient().getUserInterface().getIconCache().getIcon(pBloodspot);
    draw(icon, pBloodspot.getCoordinate(), 1.0f);
  }

  public void fieldModelChanged(FieldModelChangeEvent pChangeEvent) {
    if (pChangeEvent.getType() == FieldModelChangeEvent.TYPE_BLOODSPOT) {
      if (pChangeEvent.isAdded()) {
        BloodSpot bloodspot = (BloodSpot) pChangeEvent.getNewValue();
        drawBloodspot(bloodspot);
      }
    }
  }
  
  public void init() {
    clear(true);
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    if (fieldModel != null) {
      BloodSpot[] bloodspots = fieldModel.getBloodSpots();
      for (int i = 0; i < bloodspots.length; i++) {
        drawBloodspot(bloodspots[i]);
      }
      fieldModel.addListener(this);
    }
  }
  
}
