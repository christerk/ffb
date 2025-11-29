package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.BlockResult;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.factory.BlockResultFactory;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportBlockReRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.BLOCK_RE_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BlockReRollMessage extends ReportMessageBase<ReportBlockReRoll> {
	@Override
	protected void render(ReportBlockReRoll report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		StringBuilder status = new StringBuilder("Re-Rolled Block Dice");
		BlockResultFactory blockResultFactory = game.getRules().getFactory(FactoryType.Factory.BLOCK_RESULT);
		for (int i = 0; i < report.getBlockRoll().length; i++) {
			BlockResult blockResult = blockResultFactory.forRoll(report.getBlockRoll()[i]);
			status.append(" [ ").append(blockResult.getName()).append(" ]");
		}
		println(2, TextStyle.ROLL, status.toString());

		status = new StringBuilder(" re-rolled ").append(report.getBlockRoll().length)
		.append(" block ");
		if (report.getBlockRoll().length == 1) {
			status.append("die");
		} else {
			status.append("dice");
		}
		status.append(" using ").append(report.getReRollSource().getName(game)).append(".");

		print(3, true, player);
		println(3, TextStyle.EXPLANATION, status.toString());
	}
}
