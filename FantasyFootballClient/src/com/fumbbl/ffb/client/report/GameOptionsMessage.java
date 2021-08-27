package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.report.ReportGameOptions;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.GAME_OPTIONS)
@RulesCollection(Rules.COMMON)
public class GameOptionsMessage extends ReportMessageBase<ReportGameOptions> {

    @Override
    protected void render(ReportGameOptions report) {
    }
}
