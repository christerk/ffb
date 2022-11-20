package com.fumbbl.ffb.report;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.stats.DieStat;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public interface IReport extends IJsonSerializable, INamedObject {

	String XML_TAG = "report";

	ReportId getId();

	IReport transform(IFactorySource source);

	@Override
	default String getName() {
		return getId().getName();
	}

	@SuppressWarnings("unused") // used by external analysis tools
	default List<DieStat<?>> diceStats(Game game) {
		List<DieStat<?>> diceStats = new ArrayList<>();
		addStats(game, diceStats);
		return diceStats;
	}

	void addStats(Game game, List<DieStat<?>> diceStats);
}
