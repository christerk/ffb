package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.client.report.bb2025.BretonnianPastriesRollMessage;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.util.Scanner;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BretonnianPastriesRollMessageTest {

	@Test
	void messageIsBoundToPastriesReportId() {
		assertEquals(ReportId.BRETONNIAN_PASTRIES_ROLL,
			BretonnianPastriesRollMessage.class.getAnnotation(ReportMessageType.class).value());
	}

	@Test
	void messageIsRegisteredForBb2025() {
		assertTrue(renderersFor("BB2025").stream().anyMatch(renderer -> renderer instanceof BretonnianPastriesRollMessage));
	}

	@Test
	void messageIsNotRegisteredForBb2020() {
		assertFalse(renderersFor("BB2020").stream().anyMatch(renderer -> renderer instanceof BretonnianPastriesRollMessage));
	}

	@SuppressWarnings("rawtypes")
	private Collection<ReportMessageBase> renderersFor(String rulesVersion) {
		GameOptions options = new GameOptions(null);
		options.addOption(new GameOptionString(GameOptionId.RULESVERSION).setValue(rulesVersion));
		return new Scanner<>(ReportMessageBase.class).getSubclassInstances(options);
	}
}
