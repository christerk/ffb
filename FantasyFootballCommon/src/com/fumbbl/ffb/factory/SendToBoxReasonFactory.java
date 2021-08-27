package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SEND_TO_BOX_REASON)
@RulesCollection(Rules.COMMON)
public class SendToBoxReasonFactory implements INamedObjectFactory {

	public SendToBoxReason forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (SendToBoxReason reason : SendToBoxReason.values()) {
				if (pName.equalsIgnoreCase(reason.getName())) {
					return reason;
				}
			}
			// backward compatibility (name change)
			if ("wrestle".equals(pName)) {
				return SendToBoxReason.BALL_AND_CHAIN;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
