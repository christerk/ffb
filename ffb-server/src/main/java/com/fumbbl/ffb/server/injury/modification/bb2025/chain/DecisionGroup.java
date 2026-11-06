package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One asked coach in one phase of a node with the ordered options leading to the children.
 */
public class DecisionGroup implements IJsonSerializable {

	private InjuryPhase phase;
	private OwnerSide side;
	private final List<String> ownerIds = new ArrayList<>();
	private final List<DecisionEdge> edges = new ArrayList<>();

	public DecisionGroup() {
	}

	public DecisionGroup(InjuryPhase phase, OwnerSide side, List<String> ownerIds) {
		this.phase = phase;
		this.side = side;
		if (ownerIds != null) {
			this.ownerIds.addAll(ownerIds);
		}
	}

	public InjuryPhase getPhase() {
		return phase;
	}

	public OwnerSide getSide() {
		return side;
	}

	public List<String> getOwnerIds() {
		return ownerIds;
	}

	public List<DecisionEdge> getEdges() {
		return edges;
	}

	public void addEdge(DecisionEdge edge) {
		edges.add(edge);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.INJURY_PHASE.addTo(jsonObject, phase.name());
		IServerJsonOption.OWNER_SIDE.addTo(jsonObject, side.name());
		IServerJsonOption.OWNER_IDS.addTo(jsonObject, ownerIds);
		JsonArray edgeArray = new JsonArray();
		edges.forEach(edge -> edgeArray.add(edge.toJsonValue()));
		IServerJsonOption.DECISION_EDGES.addTo(jsonObject, edgeArray);
		return jsonObject;
	}

	@Override
	public DecisionGroup initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		phase = InjuryPhase.valueOf(IServerJsonOption.INJURY_PHASE.getFrom(source, jsonObject));
		side = OwnerSide.valueOf(IServerJsonOption.OWNER_SIDE.getFrom(source, jsonObject));
		ownerIds.clear();
		String[] ids = IServerJsonOption.OWNER_IDS.getFrom(source, jsonObject);
		if (ids != null) {
			ownerIds.addAll(Arrays.asList(ids));
		}
		edges.clear();
		JsonArray edgeArray = IServerJsonOption.DECISION_EDGES.getFrom(source, jsonObject);
		if (edgeArray != null) {
			edgeArray.values().forEach(value -> edges.add(new DecisionEdge().initFrom(source, value)));
		}
		return this;
	}
}
