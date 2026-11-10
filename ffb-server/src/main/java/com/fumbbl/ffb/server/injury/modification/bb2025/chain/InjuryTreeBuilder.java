package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.injury.injuryType.ModificationAwareInjuryTypeServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds all reachable outcomes of an injury depth first. Only nodes are stored, every node is evaluated from scratch.
 */
public class InjuryTreeBuilder {

	private final InjuryNodeEvaluationService evaluationService;
	private final InjuryModificationCandidateService candidateService;
	private final InjuryModificationCombinationService combinationService;
	private final DecisionGroupService groupService;

	public InjuryTreeBuilder() {
		this(new InjuryNodeEvaluationService(), new InjuryModificationOrderingService());
	}

	private InjuryTreeBuilder(InjuryNodeEvaluationService evaluationService,
		InjuryModificationOrderingService orderingService) {
		this(evaluationService, new InjuryModificationCandidateService(orderingService),
			new InjuryModificationCombinationService(evaluationService, orderingService), new DecisionGroupService());
	}

	public InjuryTreeBuilder(InjuryNodeEvaluationService evaluationService,
		InjuryModificationCandidateService candidateService, InjuryModificationCombinationService combinationService,
		DecisionGroupService groupService) {
		this.evaluationService = evaluationService;
		this.candidateService = candidateService;
		this.combinationService = combinationService;
		this.groupService = groupService;
	}

	public InjuryTree build(GameState gameState, ModificationAwareInjuryTypeServer<?> typeServer, InjuryContext template,
		FieldCoordinate defenderCoordinate, FieldCoordinate fromCoordinate, List<String> attackerRoleIds,
		boolean stunIsTreatedAsKo) {
		InjuryTree tree = new InjuryTree();
		tree.setAttackerRoleIds(attackerRoleIds);
		tree.setStunIsTreatedAsKo(stunIsTreatedAsKo);
		InjuryChainContext context = new InjuryChainContext(gameState, tree, typeServer, defenderCoordinate,
			fromCoordinate);
		InjuryNode root = tree.addNode(new InjuryNode());
		evaluationService.evaluate(context, template, root);
		expand(context, template, root);
		tree.setCurrentId(root.getId());
		return tree;
	}

	private void expand(InjuryChainContext context, InjuryContext template, InjuryNode node) {
		InjuryTree tree = context.getTree();
		if (tree.size() >= InjuryTree.MAX_NODES) {
			GameState gameState = context.getGameState();
			gameState.getServer().getDebugLog().log(IServerLogLevel.ERROR, gameState.getId(),
				"Injury tree reached " + InjuryTree.MAX_NODES + " nodes, node " + node.getId() + " is not expanded");
			return;
		}
		node.setExpanded(true);
		List<InjuryNode> children = new ArrayList<>();
		for (DecisionGroup group : groupService.groups(tree, node)) {
			List<InjuryModificationCandidate> candidates =
				candidateService.candidates(context, node, group, EligibilityMode.BUILD);
			if (candidates.isEmpty()) {
				continue;
			}
			for (InjuryModificationCombinationService.BuiltOption builtOption :
				combinationService.options(context, template, node, group, candidates)) {
				if (tree.size() >= InjuryTree.MAX_NODES) {
					break;
				}
				InjuryNode child = tree.addNode(builtOption.getChild());
				child.setParentId(node.getId());
				group.addEdge(new DecisionEdge(builtOption.getOption(), child.getId()));
				children.add(child);
			}
			if (!group.getEdges().isEmpty()) {
				node.addGroup(group);
			}
		}
		children.forEach(child -> expand(context, template, child));
	}
}
