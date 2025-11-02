package com.fumbbl.ffb.server.model;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for DropPlayerContext to simplify construction in tests and production code.
 */
public class DropPlayerContextBuilder {
	private InjuryResult injuryResult;
	private boolean endTurn;
	private boolean eligibleForSafePairOfHands;
	private boolean requiresArmourBreak;
	private boolean alreadyDropped;
	private boolean modifiedInjuryEndsTurn;
	private boolean endTurnWithoutKnockdown;
	private String label;
	private String playerId;
	private ApothecaryMode apothecaryMode;
	private StepParameterKey victimStateKey;
	private StepParameterKey[] additionalVictimStateKeys;
	private final List<StepParameter> stepParameters = new ArrayList<>();
	private final List<DeferredCommand> deferredCommands = new ArrayList<>();

	private DropPlayerContextBuilder() {
	}

	public static DropPlayerContextBuilder builder() {
		return new DropPlayerContextBuilder();
	}

	public static DropPlayerContextBuilder from(DropPlayerContext original) {
		DropPlayerContextBuilder builder = new DropPlayerContextBuilder();
		builder.injuryResult = original.getInjuryResult();
		builder.endTurn = original.isEndTurn();
		builder.eligibleForSafePairOfHands = original.isEligibleForSafePairOfHands();
		builder.requiresArmourBreak = original.isRequiresArmourBreak();
		builder.alreadyDropped = original.isAlreadyDropped();
		builder.modifiedInjuryEndsTurn = original.isModifiedInjuryEndsTurn();
		builder.endTurnWithoutKnockdown = original.isEndTurnWithoutKnockdown();
		builder.label = original.getLabel();
		builder.playerId = original.getPlayerId();
		builder.apothecaryMode = original.getApothecaryMode();
		builder.victimStateKey = original.getVictimStateKey();
		builder.additionalVictimStateKeys = original.getAdditionalVictimStateKeys();

		if (original.getStepParameters() != null) {
			builder.stepParameters.addAll(original.getStepParameters());
		}
		if (original.getDeferredCommands() != null) {
			builder.deferredCommands.addAll(original.getDeferredCommands());
		}
		return builder;
	}

	public DropPlayerContextBuilder injuryResult(InjuryResult injuryResult) {
		this.injuryResult = injuryResult;
		return this;
	}

	public DropPlayerContextBuilder endTurn(boolean endTurn) {
		this.endTurn = endTurn;
		return this;
	}

	public DropPlayerContextBuilder eligibleForSafePairOfHands(boolean eligible) {
		this.eligibleForSafePairOfHands = eligible;
		return this;
	}

	public DropPlayerContextBuilder requiresArmourBreak(boolean requiresArmourBreak) {
		this.requiresArmourBreak = requiresArmourBreak;
		return this;
	}

	public DropPlayerContextBuilder alreadyDropped(boolean alreadyDropped) {
		this.alreadyDropped = alreadyDropped;
		return this;
	}

	public DropPlayerContextBuilder modifiedInjuryEndsTurn(boolean modified) {
		this.modifiedInjuryEndsTurn = modified;
		return this;
	}

	public DropPlayerContextBuilder endTurnWithoutKnockdown(boolean endTurnWithoutKnockdown) {
		this.endTurnWithoutKnockdown = endTurnWithoutKnockdown;
		return this;
	}

	public DropPlayerContextBuilder label(String label) {
		this.label = label;
		return this;
	}

	public DropPlayerContextBuilder playerId(String playerId) {
		this.playerId = playerId;
		return this;
	}

	public DropPlayerContextBuilder apothecaryMode(ApothecaryMode mode) {
		this.apothecaryMode = mode;
		return this;
	}

	public DropPlayerContextBuilder victimStateKey(StepParameterKey key) {
		this.victimStateKey = key;
		return this;
	}

	public DropPlayerContextBuilder additionalVictimStateKeys(StepParameterKey... keys) {
		this.additionalVictimStateKeys = keys != null ? Arrays.copyOf(keys, keys.length) : null;
		return this;
	}

	public DropPlayerContextBuilder stepParameters(List<StepParameter> parameters) {
		if (parameters != null) {
			this.stepParameters.addAll(parameters);
		}
		return this;
	}

	public DropPlayerContextBuilder stepParameter(StepParameter parameter) {
		if (parameter != null) {
			this.stepParameters.add(parameter);
		}
		return this;
	}

	public DropPlayerContextBuilder deferredCommands(List<DeferredCommand> commands) {
		if (commands != null) {
			this.deferredCommands.addAll(commands);
		}
		return this;
	}

	public DropPlayerContextBuilder deferredCommand(DeferredCommand command) {
		if (command != null) {
			this.deferredCommands.add(command);
		}
		return this;
	}

	public DropPlayerContext build() {
		return new DropPlayerContext(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode,
			requiresArmourBreak, alreadyDropped, victimStateKey, modifiedInjuryEndsTurn, endTurnWithoutKnockdown,
			additionalVictimStateKeys, stepParameters, deferredCommands);
	}
}
