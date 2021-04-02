package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.report.ReportGameOptions;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.GAME_OPTIONS)
@RulesCollection(Rules.COMMON)
public class GameOptionsMessage extends ReportMessageBase<ReportGameOptions> {

    @Override
    protected void render(ReportGameOptions report) {
    }
}
