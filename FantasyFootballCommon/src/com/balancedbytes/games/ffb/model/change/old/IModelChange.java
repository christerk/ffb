package com.balancedbytes.games.ffb.model.change.old;

import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.change.ModelChange;

/**
 * 
 * @author Kalimar
 */
public interface IModelChange extends IByteArrayReadable {
  
  public ModelChangeIdOld getId();
  
  public void applyTo(Game pGame);
  
  public IModelChange transform();

  public ModelChange convert();
  
}
