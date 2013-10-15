package com.balancedbytes.games.ffb.model.change.old;

import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public interface IModelChange extends IByteArraySerializable {
  
  public ModelChangeIdOld getId();
  
  public void applyTo(Game pGame);
  
  public IModelChange transform();

}
