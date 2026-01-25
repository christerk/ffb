package com.fumbbl.ffb.client.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.state.logic.plugin.LogicPlugin;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FactoryType(FactoryType.Factory.LOGIC_PLUGIN)
@RulesCollection(RulesCollection.Rules.COMMON)
public class LogicPluginFactory implements INamedObjectFactory<LogicPluginFactory> {

	private final Map<LogicPlugin.Type, LogicPlugin> plugins = new HashMap<>();

	public LogicPlugin forType(LogicPlugin.Type type) {
		return plugins.get(type);
	}

	@Override
	public LogicPlugin forName(String pName) {
		return Arrays.stream(LogicPlugin.Type.values())
				.filter(type -> type.name().equals(pName))
				.findFirst()
				.map(plugins::get)
				.orElse(null);
	}

	@Override
	public void initialize(Game game) {
		Scanner<LogicPlugin> scanner = new Scanner<>(LogicPlugin.class);

		scanner.getInstancesImplementing(game.getOptions())
				.forEach(plugin -> plugins.put(plugin.getType(), plugin));
	}
}
