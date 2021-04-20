package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportBombOutOfBounds;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.BOMB_OUT_OF_BOUNDS)
@RulesCollection(Rules.COMMON)
public class BombOutOfBoundsMessage extends ReportMessageBase<ReportBombOutOfBounds> {

    @Override
    protected void render(ReportBombOutOfBounds report) {
  		println(getIndent(), TextStyle.BOLD, "Bomb scattered out of bounds.");
    }
}
