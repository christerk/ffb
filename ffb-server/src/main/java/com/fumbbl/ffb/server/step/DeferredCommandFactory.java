package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.util.Scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DeferredCommandFactory {

	private final GameState fGameState;
	private final Map<DeferredCommandId, Constructor<? extends DeferredCommand>> registry = new HashMap<>();

	public DeferredCommandFactory(GameState pGameState) {
		fGameState = pGameState;
		initialize();
	}

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

	// JSON serialization

	public DeferredCommand forJsonValue(IFactorySource source, JsonValue pJsonValue) {
		if ((pJsonValue == null) || pJsonValue.isNull()) {
			return null;
		}
		DeferredCommand command = null;
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		DeferredCommandId id = (DeferredCommandId) IServerJsonOption.DEFERRED_COMMAND_ID.getFrom(source, jsonObject);
		if (id != null) {
			command = forId(id);
			if (command != null) {
				command.initFrom(source, pJsonValue);
			}
		}
		return command;
	}

	private void initialize() {

		new Scanner<>(DeferredCommand.class).getSubclassInstances(fGameState.getGame().getOptions())
			.forEach(command -> {

				try {
					registry.put(command.getId(), command.getClass().getConstructor());

				} catch (NoSuchMethodException e) {
					throw new FantasyFootballException("Error constructing DeferredCommand for class " + command.getClass().getCanonicalName(), e);
				}
			});

	}
}
