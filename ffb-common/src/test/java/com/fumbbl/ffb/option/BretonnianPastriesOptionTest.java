package com.fumbbl.ffb.option;

import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.factory.GameOptionIdFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BretonnianPastriesOptionTest {

	private final GameOptionFactory factory = new GameOptionFactory();

	@Test
	void costDefaultsToFifteenThousand() {
		GameOptionInt option = (GameOptionInt) factory.createGameOption(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_COST);

		assertEquals(15000, option.getValue());
		assertEquals("inducementBretonnianPastriesCost", option.getId().getName());
	}

	@Test
	void maxDefaultsToOne() {
		GameOptionInt option = (GameOptionInt) factory.createGameOption(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_MAX);

		assertEquals(1, option.getValue());
		assertEquals("inducementBretonnianPastriesMax", option.getId().getName());
	}

	@Test
	void optionsAreResolvableByName() {
		assertEquals(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_COST, new GameOptionIdFactory().forName("inducementBretonnianPastriesCost"));
		assertEquals(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_MAX, new GameOptionIdFactory().forName("inducementBretonnianPastriesMax"));
	}
}
