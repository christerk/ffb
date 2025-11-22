package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.Scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FactoryType(FactoryType.Factory.DEFERRED_COMMAND)
@RulesCollection(RulesCollection.Rules.BB2025)
public class DeferredCommandFactory implements INamedObjectFactory<DeferredCommand> {

	private final Map<DeferredCommandId, Constructor<? extends DeferredCommand>> registry = new HashMap<>();

	public DeferredCommand forId(DeferredCommandId id) {

		DeferredCommand command = null;

		if (id != null) {

			if (registry.containsKey(id)) {
				Constructor<?> ctr = registry.get(id);
				try {
					command = (DeferredCommand) ctr.newInstance();
				} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |
								 InvocationTargetException e) {
					throw new StepException("Error constructing DeferredCommand " + id, e);
				}
			} else {
				throw new StepException("Unhandled DeferredCommandId " + id);
			}
		}

		return command;

	}

	@Override
	public void initialize(Game game) {
		new Scanner<>(DeferredCommand.class).getSubclassInstances(game.getOptions())
			.forEach(command -> {
				try {
					registry.put(command.getId(), command.getClass().getConstructor());

				} catch (NoSuchMethodException e) {
					throw new FantasyFootballException("Error constructing DeferredCommand for class " + command.getClass().getCanonicalName(), e);
				}
			});
	}

	@Override
	public INamedObject forName(String pName) {
		return Arrays.stream(DeferredCommandId.values()).filter(value -> value.getName().equals(pName)).findFirst().map(this::forId).orElse(null);
	}

}
