package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.ClientModeFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.application.NetCommandIdFactory;
import com.fumbbl.ffb.json.IJsonOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Round trip tests for the client identification sent with the join commands.
 */
class ClientCommandJoinJsonTest {

	private IFactorySource source;

	@BeforeEach
	void setUp() {
		source = mock(IFactorySource.class);
		when(source.forContext(any())).thenReturn(source);
		when(source.<NetCommandIdFactory>getFactory(Factory.NET_COMMAND_ID)).thenReturn(new NetCommandIdFactory());
		when(source.<ClientModeFactory>getFactory(Factory.CLIENT_MODE)).thenReturn(new ClientModeFactory());
	}

	@Test
	void joinCommandKeepsClientIdentification() {
		ClientCommandJoin command = new ClientCommandJoin(ClientMode.PLAYER);
		command.setCoach("coach");
		command.setProtocolVersion("3.4.0");
		command.setClientName("Other Client");
		command.setClientVersion("1.2.3");

		ClientCommandJoin restored = new ClientCommandJoin().initFrom(source, command.toJsonValue());

		assertEquals("coach", restored.getCoach());
		assertEquals("3.4.0", restored.getProtocolVersion());
		assertEquals("Other Client", restored.getClientName());
		assertEquals("1.2.3", restored.getClientVersion());
	}

	@Test
	void joinCommandWithoutClientIdentificationCanBeRead() {
		ClientCommandJoin command = new ClientCommandJoin(ClientMode.SPECTATOR);
		JsonObject jsonObject = command.toJsonValue();
		jsonObject.remove(IJsonOption.PROTOCOL_VERSION.getKey());
		jsonObject.remove(IJsonOption.CLIENT_NAME.getKey());
		jsonObject.remove(IJsonOption.CLIENT_VERSION.getKey());

		ClientCommandJoin restored = new ClientCommandJoin().initFrom(source, jsonObject);

		assertNull(restored.getProtocolVersion());
		assertNull(restored.getClientName());
		assertNull(restored.getClientVersion());
	}

	@Test
	void joinReplayCommandKeepsClientIdentification() {
		ClientCommandJoinReplay command =
			new ClientCommandJoinReplay("replay", "coach", 42L, "3.4.0", "Other Client", "1.2.3");

		ClientCommandJoinReplay restored = new ClientCommandJoinReplay().initFrom(source, command.toJsonValue());

		assertEquals("replay", restored.getReplayName());
		assertEquals(42L, restored.getGameId());
		assertEquals("3.4.0", restored.getProtocolVersion());
		assertEquals("Other Client", restored.getClientName());
		assertEquals("1.2.3", restored.getClientVersion());
	}
}
