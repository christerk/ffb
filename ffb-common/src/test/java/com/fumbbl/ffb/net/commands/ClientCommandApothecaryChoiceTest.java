package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.json.IJsonOption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientCommandApothecaryChoiceTest {

	@Test
	void serializesPlayerAndChoice() {
		ClientCommandApothecaryChoice command = new ClientCommandApothecaryChoice("player-1", true);

		JsonObject jsonObject = command.toJsonValue();

		assertEquals("player-1", IJsonOption.PLAYER_ID.getFrom(null, jsonObject));
		assertEquals(Boolean.TRUE, IJsonOption.CHOICE_NEW_INJURY.getFrom(null, jsonObject));
	}

}
