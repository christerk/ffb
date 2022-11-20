package com.fumbbl.ffb.report;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieStat;

import java.util.List;

public abstract class NoDiceReport implements IReport {

	@Override
	public void addStats(Game game, List<DieStat<?>> diceStats) {
	}
}
