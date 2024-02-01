package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.BLOCK_RESULT)
@RulesCollection(Rules.COMMON)
public class BlockResultFactory implements INamedObjectFactory<BlockResult> {

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
