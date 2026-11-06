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
 * All precomputed outcomes of one injury, stored flat with ids. Server side only: it contains the dice of branches
 * that are never taken.
 */
public class InjuryTree implements IJsonSerializable {

	public static final int MAX_NODES = 1000;

	private final List<InjuryNode> nodes = new ArrayList<>();
	private int currentId;
	private InjuryDicePool pool = new InjuryDicePool();
	private final List<String> attackerRoleIds = new ArrayList<>();
	private boolean stunIsTreatedAsKo;

	public InjuryNode addNode(InjuryNode node) {
		node.setId(nodes.size());
		nodes.add(node);
		return node;
	}

	public int nextId() {
		return nodes.size();
	}

	public int size() {
		return nodes.size();
	}

	public InjuryNode getNode(int id) {
		return id >= 0 && id < nodes.size() ? nodes.get(id) : null;
	}

	public InjuryNode getRoot() {
		return getNode(0);
	}

	public InjuryNode getCurrent() {
		return getNode(currentId);
	}

	public int getCurrentId() {
		return currentId;
	}

	public void setCurrentId(int currentId) {
		this.currentId = currentId;
	}

	public InjuryDicePool getPool() {
		return pool;
	}

	public List<String> getAttackerRoleIds() {
		return attackerRoleIds;
	}

	public void setAttackerRoleIds(List<String> ids) {
		attackerRoleIds.clear();
		if (ids != null) {
			attackerRoleIds.addAll(ids);
		}
	}

	public boolean isStunIsTreatedAsKo() {
		return stunIsTreatedAsKo;
	}

	public void setStunIsTreatedAsKo(boolean stunIsTreatedAsKo) {
		this.stunIsTreatedAsKo = stunIsTreatedAsKo;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray nodeArray = new JsonArray();
		nodes.forEach(node -> nodeArray.add(node.toJsonValue()));
		IServerJsonOption.INJURY_NODES.addTo(jsonObject, nodeArray);
		IServerJsonOption.CURRENT_NODE_ID.addTo(jsonObject, currentId);
		IServerJsonOption.DICE_POOL.addTo(jsonObject, pool.toJsonValue());
		IServerJsonOption.ATTACKER_ROLE_IDS.addTo(jsonObject, attackerRoleIds);
		IServerJsonOption.STUN_IS_TREATED_AS_KO.addTo(jsonObject, stunIsTreatedAsKo);
		return jsonObject;
	}

	@Override
	public InjuryTree initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		nodes.clear();
		JsonArray nodeArray = IServerJsonOption.INJURY_NODES.getFrom(source, jsonObject);
		if (nodeArray != null) {
			nodeArray.values().forEach(value -> nodes.add(new InjuryNode().initFrom(source, value)));
		}
		currentId = IServerJsonOption.CURRENT_NODE_ID.getFrom(source, jsonObject);
		JsonObject poolObject = IServerJsonOption.DICE_POOL.getFrom(source, jsonObject);
		pool = poolObject != null ? new InjuryDicePool().initFrom(source, poolObject) : new InjuryDicePool();
		attackerRoleIds.clear();
		String[] ids = IServerJsonOption.ATTACKER_ROLE_IDS.getFrom(source, jsonObject);
		if (ids != null) {
			attackerRoleIds.addAll(Arrays.asList(ids));
		}
		stunIsTreatedAsKo = Boolean.TRUE.equals(IServerJsonOption.STUN_IS_TREATED_AS_KO.getFrom(source, jsonObject));
		return this;
	}
}
