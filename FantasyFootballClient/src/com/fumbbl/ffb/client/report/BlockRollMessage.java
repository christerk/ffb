package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.report.ReportBlockRoll;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

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
