package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.BlockResult;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.factory.BlockResultFactory;
import com.balancedbytes.games.ffb.report.ReportBlockRoll;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.BLOCK_ROLL)
@RulesCollection(Rules.COMMON)
public class BlockRollMessage extends ReportMessageBase<ReportBlockRoll> {

    @Override
    protected void render(ReportBlockRoll report) {
  		if (ArrayTool.isProvided(report.getBlockRoll())) {
  			  setIndent(2);
  				StringBuilder status = new StringBuilder();
  				status.append("Block Roll");
  				if (StringTool.isProvided(report.getDefenderId())) {
  					status.append(" against ").append(report.getDefenderId());
				  }
  				BlockResultFactory blockResultFactory = game.getRules().getFactory(Factory.BLOCK_RESULT);
  				for (int i = 0; i < report.getBlockRoll().length; i++) {
  					BlockResult blockResult = blockResultFactory.forRoll(report.getBlockRoll()[i]);
  					status.append(" [ ").append(blockResult.getName()).append(" ]");
  				}
  				println(getIndent(), TextStyle.ROLL, status.toString());
  			}
    }
}
