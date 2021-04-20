package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.DIALOG_ID)
@RulesCollection(Rules.COMMON)
public class DialogIdFactory implements INamedObjectFactory {

	public DialogId forName(String pName) {
		for (DialogId dialogId : DialogId.values()) {
			if (dialogId.getName().equalsIgnoreCase(pName)) {
				return dialogId;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
