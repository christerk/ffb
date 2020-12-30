package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

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
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
