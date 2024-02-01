package com.fumbbl.ffb.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.Scanner;

@FactoryType(FactoryType.Factory.REPORT)
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportFactory implements INamedObjectFactory<IReport> {

	private final Map<ReportId, Constructor<? extends IReport>> constructors = new HashMap<>();

	@Override
	public IReport forName(String name) {
		return forId(ReportId.valueOf(name));
	}

	public IReport forId(ReportId id) {
		try {
			Constructor<? extends IReport> constructor = constructors.get(id);
			if (constructor == null) {
				throw new FantasyFootballException("No constructor registered for reportId " + id.getName());
			}
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new FantasyFootballException("Could not create instance for reportId " + id.getName(), e);
		}
	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(IReport.class).getInstancesImplementing(game.getOptions())
			.forEach(report -> {
				try {
					constructors.put(report.getId(), report.getClass().getConstructor());
				} catch (NoSuchMethodException e) {
					throw new FantasyFootballException("Could not create constructor for reportId" + report.getName());
				}
			});
	}
}
