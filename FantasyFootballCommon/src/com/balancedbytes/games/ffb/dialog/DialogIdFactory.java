package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.INamedObjectFactory;

/**
 * 
 * @author Kalimar
 */
public class DialogIdFactory implements INamedObjectFactory {

	public DialogId forName(String pName) {
		for (DialogId dialogId : DialogId.values()) {
			if (dialogId.getName().equalsIgnoreCase(pName)) {
				return dialogId;
			}
		}
		return null;
	}

}
