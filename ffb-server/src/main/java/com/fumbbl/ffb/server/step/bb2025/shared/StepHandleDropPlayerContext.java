package com.fumbbl.ffb.server.step.bb2025.shared;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogInjuryModificationChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.ReportFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InjuryModificationOption;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandInjuryModificationChoice;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportInjury;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.logcontrol.SkipInjuryParts;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.AppliedModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.ChainableInjuryModification;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.DecisionEdge;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.DecisionGroup;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.EligibilityMode;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.InjuryChainContext;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.InjuryModificationCandidateService;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.InjuryNode;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.InjuryPhase;
import com.fumbbl.ffb.server.injury.modification.bb2025.chain.InjuryTree;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.model.InjuryTurnoverService;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.UtilServerSteps;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.server.util.UtilServerPlayerMove;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepHandleDropPlayerContext extends AbstractStepWithReRoll {

	private DropPlayerContext dropPlayerContext;
	private String playerId;
	private Skill skill;
	private int offeredGroup = -1;
	private int pendingEdge = -1;
	private final List<Integer> declinedGroups = new ArrayList<>();

	public StepHandleDropPlayerContext(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.HANDLE_DROP_PLAYER_CONTEXT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		if (parameter != null) {
			switch (parameter.getKey()) {
				case DROP_PLAYER_CONTEXT: {
					dropPlayerContext = (DropPlayerContext) parameter.getValue();
					consume(parameter);
					return true;
				}
				case SUCCESSFUL_PRO: {
					boolean successful = toPrimitive((Boolean) parameter.getValue());

					if (pendingEdge >= 0) {
						int childId = pendingEdge;
						pendingEdge = -1;
						if (successful) {
							takeEdge(childId);
						}
					} else if (successful) {
						getGameState().getGame().getPlayerById(playerId).markUsed(skill, getGameState().getGame());
						successfulSkillUse(dropPlayerContext.getInjuryResult());
					} else {
						dropPlayerContext.getInjuryResult().injuryContext().setModifiedInjuryContext(null);
					}
					consume(parameter);
					return true;
				}
				default:
					break;
			}
		}
		return super.setParameter(parameter);
	}

	@Override
	public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
		if (hasTree()) {
			return handleTreeCommand(pReceivedCommand);
		}
		StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			if (pReceivedCommand.getCommand().getId() == NetCommandId.CLIENT_USE_SKILL) {
				commandStatus = StepCommandStatus.EXECUTE_STEP;
				ClientCommandUseSkill clientCommandUseSkill = (ClientCommandUseSkill) pReceivedCommand.getCommand();
				Skill skill = clientCommandUseSkill.getSkill();
				InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
				getResult().addReport(new ReportSkillUse(clientCommandUseSkill.getPlayerId(), skill, clientCommandUseSkill.isSkillUsed(), injuryResult.injuryContext().getModifiedInjuryContext().getSkillUse()));
				if (clientCommandUseSkill.isSkillUsed()) {

					if (skill.getSkillBehaviour().getInjuryContextModification().requiresConditionalReRollSkill()) {
						getGameState().pushCurrentStepOnStack();
						Sequence sequence = new Sequence(getGameState());
						sequence.add(StepId.PRO, StepParameter.from(StepParameterKey.PLAYER_ID, clientCommandUseSkill.getPlayerId()));
						getGameState().getStepStack().push(sequence.getSequence());
						commandStatus = StepCommandStatus.SKIP_STEP;
						getResult().setNextAction(StepAction.NEXT_STEP);
					} else {
						getGameState().getGame().getPlayerById(clientCommandUseSkill.getPlayerId()).markUsed(skill, getGameState().getGame());
						successfulSkillUse(injuryResult);
					}
				}
			}
		}

		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

	private void successfulSkillUse(InjuryResult injuryResult) {
		injuryResult.injuryContext().getModifiedInjuryContext().getReports().forEach(report -> getResult().addReport(report));
		injuryResult.swapToAlternateContext(this, getGameState().getGame());
		dropPlayerContext.setEndTurn(dropPlayerContext.isModifiedInjuryEndsTurn());
	}

	@Override
	public void start() {
		super.start();
		executeStep();
	}

	private void executeStep() {
		getResult().setNextAction(StepAction.NEXT_STEP);
		if (dropPlayerContext != null && dropPlayerContext.getInjuryResult() != null) {

			Game game = getGameState().getGame();

			InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
			if (hasTree()) {
				if (offerNextGroup()) {
					getResult().setNextAction(StepAction.CONTINUE);
					return;
				}
				finishTree();
				drop(game, injuryResult);
			} else if (injuryResult.injuryContext().getModifiedInjuryContext() != null && !injuryResult.isAlreadyReported()) {
				injuryResult.report(this);
				ModifiedInjuryContext injuryContext = injuryResult.injuryContext().getModifiedInjuryContext();
				skill = injuryContext.getUsedSkill();

				if (skill.getSkillBehaviour().getInjuryContextModification().appliesToDefender()) {
					playerId = injuryContext.fDefenderId;
				} else if (StringTool.isProvided(injuryContext.fAttackerId)) {
					playerId = injuryContext.fAttackerId;
				} else {
					playerId = game.getActingPlayer().getPlayerId();
				}

				UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(playerId, skill, 0), true);
				getResult().setNextAction(StepAction.CONTINUE);
			} else {
				drop(game, injuryResult);
			}
		}
	}

	private void drop(Game game, InjuryResult injuryResult) {
		if (!dropPlayerContext.isAlreadyDropped() && (!dropPlayerContext.isRequiresArmourBreak() || injuryResult.injuryContext().isArmorBroken())) {
			publishParameters(UtilServerInjury.dropPlayer(this, game.getPlayerById(dropPlayerContext.getPlayerId()),
				dropPlayerContext.getApothecaryMode(), dropPlayerContext.isEligibleForSafePairOfHands()));
			if (dropPlayerContext.isEndTurn()) {
				publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
			}
			if (dropPlayerContext.getVictimStateKey() != null) {
				publishParameter(new StepParameter(dropPlayerContext.getVictimStateKey(), game.getFieldModel().getPlayerState(game.getDefender())));
			}
			if (ArrayTool.isProvided(dropPlayerContext.getAdditionalVictimStateKeys())) {
				for (StepParameterKey additionalVictimStateKey : dropPlayerContext.getAdditionalVictimStateKeys()) {
					publishParameter(new StepParameter(additionalVictimStateKey, game.getFieldModel().getPlayerState(game.getDefender())));
				}
			}
		} else if (!dropPlayerContext.isAlreadyDropped()
			&& dropPlayerContext.isEndTurnWithoutKnockdown() && dropPlayerContext.isEndTurn()) {
			publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}

		publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResult));
		if (StringTool.isProvided(dropPlayerContext.getLabel())) {
			getResult().setNextAction(StepAction.GOTO_LABEL, dropPlayerContext.getLabel());
		}
		UtilServerPlayerMove.updateMoveSquares(getGameState(), game.getActingPlayer().isJumping());
	}


	private boolean hasTree() {
		return dropPlayerContext != null && dropPlayerContext.getInjuryResult() != null
			&& dropPlayerContext.getInjuryResult().getTree() != null;
	}

	private InjuryTree tree() {
		return dropPlayerContext.getInjuryResult().getTree();
	}

	private InjuryChainContext chainContext() {
		return new InjuryChainContext(getGameState(), tree());
	}

	private StepCommandStatus handleTreeCommand(ReceivedCommand receivedCommand) {
		StepCommandStatus commandStatus = super.handleCommand(receivedCommand);
		if (commandStatus != StepCommandStatus.UNHANDLED_COMMAND || offeredGroup < 0) {
			return commandStatus;
		}
		InjuryTree tree = tree();
		List<DecisionGroup> groups = tree.getCurrent().getGroups();
		if (offeredGroup >= groups.size()) {
			return commandStatus;
		}
		DecisionGroup group = groups.get(offeredGroup);
		List<DecisionEdge> edges = offeredEdges(group);
		if (edges.isEmpty()) {
			return commandStatus;
		}
		Game game = getGameState().getGame();
		Player<?> owner = game.getPlayerById(edges.get(0).getOption().getPlayerIds().get(0));
		NetCommandId commandId = receivedCommand.getCommand().getId();

		if (commandId == NetCommandId.CLIENT_USE_SKILL) {
			ClientCommandUseSkill command = (ClientCommandUseSkill) receivedCommand.getCommand();
			if (!UtilServerSteps.checkCommandIsFromPlayerTeam(getGameState(), receivedCommand, owner)) {
				return commandStatus;
			}
			if (!command.isSkillUsed()) {
				DecisionEdge offered = edges.get(0);
				if (edges.size() == 1 && offered.getOption().getSkills().size() == 1) {
					reportDecline(offered);
				}
				decline();
				executeStep();
				return StepCommandStatus.EXECUTE_STEP;
			}
			Optional<DecisionEdge> edge = edges.stream()
				.filter(candidate -> candidate.getOption().getSkills().size() == 1
					&& candidate.getOption().hasSameSkills(Collections.singletonList(command.getSkill()))
					&& (!StringTool.isProvided(command.getPlayerId())
					|| candidate.getOption().getPlayerIds().contains(command.getPlayerId())))
				.findFirst();
			if (!edge.isPresent()) {
				return commandStatus;
			}
			return accept(edge.get());
		}

		if (commandId == NetCommandId.CLIENT_INJURY_MODIFICATION_CHOICE) {
			ClientCommandInjuryModificationChoice command = (ClientCommandInjuryModificationChoice) receivedCommand.getCommand();
			if (!UtilServerSteps.checkCommandIsFromPlayerTeam(getGameState(), receivedCommand, owner)) {
				return commandStatus;
			}
			if (command.getSkills().isEmpty()) {
				decline();
				executeStep();
				return StepCommandStatus.EXECUTE_STEP;
			}
			Optional<DecisionEdge> edge = edges.stream()
				.filter(candidate -> candidate.getOption().hasSameSkills(command.getSkills()))
				.findFirst();
			if (!edge.isPresent()) {
				return commandStatus;
			}
			return accept(edge.get());
		}
		return commandStatus;
	}

	private StepCommandStatus accept(DecisionEdge edge) {
		InjuryTree tree = tree();
		InjuryNode child = tree.getNode(edge.getChildId());
		offeredGroup = -1;
		boolean conditional = edgeModifications(child).stream()
			.anyMatch(applied -> modificationRequiresConditionalReRollSkill(applied.getSkill()));
		if (conditional) {
			pendingEdge = edge.getChildId();
			getGameState().pushCurrentStepOnStack();
			Sequence sequence = new Sequence(getGameState());
			sequence.add(StepId.PRO, StepParameter.from(StepParameterKey.PLAYER_ID, edge.getOption().getPlayerIds().get(0)));
			getGameState().getStepStack().push(sequence.getSequence());
			getResult().setNextAction(StepAction.NEXT_STEP);
			return StepCommandStatus.SKIP_STEP;
		}
		takeEdge(edge.getChildId());
		executeStep();
		return StepCommandStatus.EXECUTE_STEP;
	}

	private List<AppliedModification> edgeModifications(InjuryNode child) {
		return child.getEdgeModifications(tree().getNode(child.getParentId()));
	}

	private boolean modificationRequiresConditionalReRollSkill(Skill skill) {
		return new InjuryModificationCandidateService().modifications(skill).stream()
			.anyMatch(ChainableInjuryModification::requiresConditionalReRollSkill);
	}

	private void takeEdge(int childId) {
		Game game = getGameState().getGame();
		InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
		InjuryTree tree = tree();
		InjuryNode child = tree.getNode(childId);
		InjuryPhase phase = InjuryPhase.ARMOUR;
		for (AppliedModification applied : edgeModifications(child)) {
			Player<?> player = game.getPlayerById(applied.getPlayerId());
			Skill appliedSkill = applied.getSkill();
			if (player != null && appliedSkill.getSkillUsageType() != SkillUsageType.REGULAR) {
				player.markUsed(appliedSkill, game);
			}
			if (applied.getSkillUse() != null) {
				getResult().addReport(new ReportSkillUse(applied.getPlayerId(), appliedSkill, true, applied.getSkillUse()));
			}
			phase = applied.getPhase();
		}
		for (IReport report : child.getEdgeReports().getReports()) {
			getResult().addReport(report);
		}
		injuryResult.moveTo(childId);
		declinedGroups.clear();
		offeredGroup = -1;
		if (phase == InjuryPhase.ARMOUR) {
			injuryResult.setArmourReported(false);
			injuryResult.setInjuryReported(false);
			reportArmour(injuryResult);
		} else {
			injuryResult.setInjuryReported(false);
			reportInjuryPart(injuryResult);
		}
	}

	private void decline() {
		if (offeredGroup >= 0 && !declinedGroups.contains(offeredGroup)) {
			declinedGroups.add(offeredGroup);
		}
		offeredGroup = -1;
	}

	private void reportDecline(DecisionEdge edge) {
		InjuryNode child = tree().getNode(edge.getChildId());
		edgeModifications(child).stream().findFirst().ifPresent(applied ->
			getResult().addReport(new ReportSkillUse(applied.getPlayerId(), applied.getSkill(), false, applied.getSkillUse())));
	}

	/**
	 * Offers the first group of the current node with remaining options.
	 *
	 * @return whether a dialog was shown
	 */
	private boolean offerNextGroup() {
		InjuryResult injuryResult = dropPlayerContext.getInjuryResult();
		InjuryNode current = tree().getCurrent();
		List<DecisionGroup> groups = current.getGroups();
		for (int index = 0; index < groups.size(); index++) {
			if (declinedGroups.contains(index)) {
				continue;
			}
			DecisionGroup group = groups.get(index);
			List<DecisionEdge> edges = offeredEdges(group);
			if (edges.isEmpty()) {
				continue;
			}
			reportArmour(injuryResult);
			if (group.getPhase() == InjuryPhase.INJURY) {
				reportInjuryPart(injuryResult);
			}
			offeredGroup = index;
			showDialog(edges);
			return true;
		}
		offeredGroup = -1;
		return false;
	}

	private void showDialog(List<DecisionEdge> edges) {
		Game game = getGameState().getGame();
		InjuryModificationOption first = edges.get(0).getOption();
		if (edges.size() == 1 && first.getSkills().size() == 1) {
			playerId = first.getPlayerIds().get(0);
			skill = first.getSkills().get(0);
			UtilServerDialog.showDialog(getGameState(), new DialogSkillUseParameter(playerId, skill, 0), true);
			return;
		}
		Player<?> owner = game.getPlayerById(first.getPlayerIds().get(0));
		String teamId = game.getTeamHome().hasPlayer(owner) ? game.getTeamHome().getId() : game.getTeamAway().getId();
		InjuryContext context = tree().getCurrent().getContext();
		Player<?> defender = game.getPlayerById(context.getDefenderId());
		String defenderName = defender != null ? defender.getName() : "the player";
		List<InjuryModificationOption> options = new ArrayList<>();
		edges.forEach(edge -> options.add(edge.getOption()));
		UtilServerDialog.showDialog(getGameState(), new DialogInjuryModificationChoiceParameter(teamId,
			context.getDefenderId(), "Modify the injury roll against " + defenderName + "?", options), true);
	}

	/**
	 * The edges of the group whose options still pass the offer check for every skill.
	 */
	private List<DecisionEdge> offeredEdges(DecisionGroup group) {
		InjuryChainContext context = chainContext();
		InjuryNode current = tree().getCurrent();
		InjuryModificationCandidateService candidateService = new InjuryModificationCandidateService();
		return group.getEdges().stream().filter(edge -> {
			InjuryModificationOption option = edge.getOption();
			for (int i = 0; i < option.getSkills().size(); i++) {
				String ownerId = option.getPlayerIds().get(i);
				Skill optionSkill = option.getSkills().get(i);
				if (!candidateService.findEligible(context, current, group, optionSkill, ownerId, EligibilityMode.OFFER)
					.isPresent()) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());
	}

	private void reportArmour(InjuryResult injuryResult) {
		if (injuryResult.isArmourReported()) {
			return;
		}
		InjuryContext context = injuryResult.injuryContext();
		addInjuryReport(context, SkipInjuryParts.INJURY);
		injuryResult.setArmourReported(true);
		if (!context.isArmorBroken()) {
			injuryResult.setInjuryReported(true);
		}
	}

	private void reportInjuryPart(InjuryResult injuryResult) {
		if (injuryResult.isInjuryReported()) {
			return;
		}
		InjuryContext context = injuryResult.injuryContext();
		if (context.isArmorBroken()) {
			addInjuryReport(context, SkipInjuryParts.ARMOUR_AND_CAS);
		}
		injuryResult.setInjuryReported(true);
	}

	private void addInjuryReport(InjuryContext context, SkipInjuryParts skip) {
		ReportFactory factory = getGameState().getGame().getFactory(FactoryType.Factory.REPORT);
		ReportInjury reportInjury = (ReportInjury) factory.forId(ReportId.INJURY);
		getResult().addReport(reportInjury.init(context, skip));
	}

	private void finishTree() {
		Game game = getGameState().getGame();
		InjuryTree tree = tree();
		InjuryContext context = dropPlayerContext.getInjuryResult().injuryContext();
		Player<?> defender = game.getPlayerById(context.getDefenderId());
		if (defender != null) {
			UtilServerInjury.evaluateInjuryContext(context.getInjuryType(), tree.isStunIsTreatedAsKo(), defender, context,
				getGameState());
		}
		dropPlayerContext.setEndTurn(new InjuryTurnoverService().endsTurn(game, dropPlayerContext, context));
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		if (dropPlayerContext != null) {
			IServerJsonOption.DROP_PLAYER_CONTEXT.addTo(jsonObject, dropPlayerContext.toJsonValue());
		}
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.SKILL.addTo(jsonObject, skill);
		IServerJsonOption.OFFERED_GROUP.addTo(jsonObject, offeredGroup);
		IServerJsonOption.PENDING_EDGE.addTo(jsonObject, pendingEdge);
		IServerJsonOption.DECLINED_GROUPS.addTo(jsonObject, declinedGroups.stream().mapToInt(Integer::intValue).toArray());
		return jsonObject;
	}

	@Override
	public StepHandleDropPlayerContext initFrom(IFactorySource source, JsonValue jsonValue) {
		StepHandleDropPlayerContext step = (StepHandleDropPlayerContext) super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		if (IServerJsonOption.DROP_PLAYER_CONTEXT.isDefinedIn(jsonObject)) {
			dropPlayerContext = new DropPlayerContext().initFrom(source, IServerJsonOption.DROP_PLAYER_CONTEXT.getFrom(source, jsonObject));
		}
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		skill = (Skill) IServerJsonOption.SKILL.getFrom(source, jsonObject);
		offeredGroup = IServerJsonOption.OFFERED_GROUP.isDefinedIn(jsonObject)
			? IServerJsonOption.OFFERED_GROUP.getFrom(source, jsonObject) : -1;
		pendingEdge = IServerJsonOption.PENDING_EDGE.isDefinedIn(jsonObject)
			? IServerJsonOption.PENDING_EDGE.getFrom(source, jsonObject) : -1;
		declinedGroups.clear();
		int[] declined = IServerJsonOption.DECLINED_GROUPS.getFrom(source, jsonObject);
		if (declined != null) {
			for (int index : declined) {
				declinedGroups.add(index);
			}
		}
		return step;
	}
}
