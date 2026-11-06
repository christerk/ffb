package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.injuryType.ModificationAwareInjuryTypeServer;

/**
 * Everything the modifications and services need to evaluate nodes of one injury tree. The injury type server and
 * coordinates are only available while the tree is built.
 */
public class InjuryChainContext {

	private final GameState gameState;
	private final InjuryTree tree;
	private final ModificationAwareInjuryTypeServer<?> typeServer;
	private final FieldCoordinate defenderCoordinate;
	private final FieldCoordinate fromCoordinate;

	public InjuryChainContext(GameState gameState, InjuryTree tree) {
		this(gameState, tree, null, null, null);
	}

	public InjuryChainContext(GameState gameState, InjuryTree tree, ModificationAwareInjuryTypeServer<?> typeServer,
		FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate) {
		this.gameState = gameState;
		this.tree = tree;
		this.typeServer = typeServer;
		this.defenderCoordinate = defenderCoordinate;
		this.fromCoordinate = fromCoordinate;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Game getGame() {
		return gameState.getGame();
	}

	public InjuryTree getTree() {
		return tree;
	}

	public ModificationAwareInjuryTypeServer<?> getTypeServer() {
		return typeServer;
	}

	public FieldCoordinate getDefenderCoordinate() {
		return defenderCoordinate;
	}

	public FieldCoordinate getFromCoordinate() {
		return fromCoordinate;
	}

	public Player<?> getAttacker(InjuryNode node) {
		return getGame().getPlayerById(node.getContext().fAttackerId);
	}

	public Player<?> getDefender(InjuryNode node) {
		return getGame().getPlayerById(node.getContext().fDefenderId);
	}
}
