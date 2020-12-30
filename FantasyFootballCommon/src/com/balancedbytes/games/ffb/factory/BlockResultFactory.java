package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.BLOCK_RESULT)
@RulesCollection(Rules.COMMON)
public class BlockResultFactory implements INamedObjectFactory {

	public BlockResult forName(String pName) {
		for (BlockResult result : BlockResult.values()) {
			if (result.getName().equalsIgnoreCase(pName)) {
				return result;
			}
		}
		return null;
	}

	public BlockResult forRoll(int pRoll) {
		switch (pRoll) {
		case 1:
			return BlockResult.SKULL;
		case 2:
			return BlockResult.BOTH_DOWN;
		case 5:
			return BlockResult.POW_PUSHBACK;
		case 6:
			return BlockResult.POW;
		default: // 3 and 4
			return BlockResult.PUSHBACK;
		}
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}