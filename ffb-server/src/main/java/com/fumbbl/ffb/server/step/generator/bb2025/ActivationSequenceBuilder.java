package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.util.StringTool;

import static com.fumbbl.ffb.server.step.StepParameter.from;

public final class ActivationSequenceBuilder {

	private ActivationSequenceBuilder() {
	}

	public static ActivationSequenceBuilder create() {
		return new ActivationSequenceBuilder();
	}

	private String failureLabel, oldDefender, eventualDefender;
	private boolean preventNullDefender;
	private FieldCoordinate targetCoordinate;

	/**
	 * Label to use when a nega trait like Animal Savagery or Bone Head fails (usually jumps to the end of the sequence)
	 */
	public ActivationSequenceBuilder withFailureLabel(final String failureLabel) {
		this.failureLabel = failureLabel;
		return this;
	}

	/**
	 * ID of a previously selected player, e.g. target of a block action
	 */
	public ActivationSequenceBuilder withOldDefender(final String oldDefender) {
		this.oldDefender = oldDefender;
		return this;
	}

	/**
	 * ID of the player that should be selected after all nega traits have been resolved, mostly used because #
	 * Animal Savagery sets the defender id when lashing out, so we have to reset it
	 */
	public ActivationSequenceBuilder withEventualDefender(final String eventualDefender) {
		this.eventualDefender = eventualDefender;
		return this;
	}

	/**
	 * Currently only used for move actions, it can occur that Animal Savagery clears the defender ID, and we need
	 * to prevent the propagation of this, so this setting tells StepSetDefender to ignore a null ID.
	 */
	public ActivationSequenceBuilder preventNullDefender() {
		this.preventNullDefender = true;
		return this;
	}

	/**
	 * In case of a pass action we need to have access to this coordinate when resolving Animal Savagery
	 */
	public ActivationSequenceBuilder withTargetCoordinate(final FieldCoordinate targetCoordinate) {
		this.targetCoordinate = targetCoordinate;
		return this;
	}

	public Sequence addTo(Sequence sequence) {
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel),
			from(StepParameterKey.BLOCK_DEFENDER_ID, oldDefender),
			from(StepParameterKey.TARGET_COORDINATE, targetCoordinate));
		sequence.add(StepId.STEADY_FOOTING);
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		if (StringTool.isProvided(eventualDefender)) {
			sequence.add(StepId.SET_DEFENDER,
				from(StepParameterKey.BLOCK_DEFENDER_ID, eventualDefender),
				from(StepParameterKey.IGNORE_NULL_VALUE, preventNullDefender));
		}

		sequence.add(StepId.GOTO_LABEL,
			from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT),
			from(StepParameterKey.ALTERNATE_GOTO_LABEL, failureLabel));

		sequence.add(StepId.BONE_HEAD, IStepLabel.NEXT,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));

		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));

		return sequence;
	}

}
