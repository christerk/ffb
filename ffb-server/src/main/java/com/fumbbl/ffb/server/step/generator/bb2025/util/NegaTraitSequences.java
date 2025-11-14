package com.fumbbl.ffb.server.step.generator.bb2025.util;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

public final class NegaTraitSequences {

	private NegaTraitSequences() {
	}

	public static void append(Sequence sequence, String failureLabel, String successLabel, String alternateSuccessLabel,
														StepParameter[] animalSavageryParams, StepParameter[] setDefenderParams) {

		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY, withFailureParam(animalSavageryParams, failureLabel));
		sequence.add(StepId.STEADY_FOOTING);
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		if (setDefenderParams.length > 0) {
			sequence.add(StepId.SET_DEFENDER, setDefenderParams);
		}

		if (successLabel != null) {
			sequence.add(StepId.GOTO_LABEL,
				from(StepParameterKey.GOTO_LABEL, successLabel),
				from(StepParameterKey.ALTERNATE_GOTO_LABEL, alternateSuccessLabel));
			sequence.add(StepId.BONE_HEAD, successLabel,
				from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		} else {
			sequence.add(StepId.BONE_HEAD,
				from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		}

		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel));
	}

	private static StepParameter[] withFailureParam(StepParameter[] params, String failureLabel) {
		StepParameter[] merged = new StepParameter[params.length + 1];
		System.arraycopy(params, 0, merged, 0, params.length);
		merged[params.length] = from(StepParameterKey.GOTO_LABEL_ON_FAILURE, failureLabel);
		return merged;
	}
}
