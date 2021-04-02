package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportBombOutOfBounds;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.BOMB_OUT_OF_BOUNDS)
@RulesCollection(Rules.COMMON)
public class BombOutOfBoundsMessage extends ReportMessageBase<ReportBombOutOfBounds> {

    @Override
    protected void render(ReportBombOutOfBounds report) {
  		println(getIndent(), TextStyle.BOLD, "Bomb scattered out of bounds.");
    }
}
