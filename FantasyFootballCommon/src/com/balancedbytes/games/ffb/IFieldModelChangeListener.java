package com.balancedbytes.games.ffb;

import java.util.EventListener;

/**
 * 
 * @author Kalimar
 */
public interface IFieldModelChangeListener extends EventListener {

	public void fieldModelChanged(FieldModelChangeEvent pChangeEvent);

}
