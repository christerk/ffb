package com.fumbbl.ffb.dialog;

import com.fumbbl.ffb.ReRollProperty;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.json.IJsonOption;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogReRollForTargetsParameterFactoryTest {

	@Test
	void keepsLegacyAndPropertiesParametersSeparate() {
		DialogParameterFactory factory = new DialogParameterFactory();

		assertTrue(factory.createDialogParameter(DialogId.RE_ROLL_FOR_TARGETS)
			instanceof DialogReRollForTargetsParameter);
		assertTrue(factory.createDialogParameter(DialogId.RE_ROLL_FOR_TARGETS_PROPERTIES)
			instanceof DialogReRollForTargetsPropertiesParameter);
	}

	@Test
	void onlyPropertiesParameterSerializesReRollProperties() {
		DialogReRollForTargetsParameter legacy = new DialogReRollForTargetsParameter("player",
			Collections.emptyList(), ReRolledActions.DAUNTLESS, Collections.emptyMap(), Collections.emptyList(),
			false, false, null, null, false);
		DialogReRollForTargetsPropertiesParameter properties = new DialogReRollForTargetsPropertiesParameter("player",
			Collections.emptyList(), ReRolledActions.DAUNTLESS, Collections.emptyMap(), Collections.emptyList(),
			false, false, null, null, false, Collections.singletonList(ReRollProperty.PRO));

		assertFalse(IJsonOption.RE_ROLL_PROPERTIES.isDefinedIn(legacy.toJsonValue()));
		assertTrue(IJsonOption.RE_ROLL_PROPERTIES.isDefinedIn(properties.toJsonValue()));
	}
}
