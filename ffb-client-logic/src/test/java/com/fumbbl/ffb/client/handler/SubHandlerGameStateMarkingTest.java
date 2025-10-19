package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SubHandlerGameStateMarkingTest {

	private SubHandlerGameStateMarking handler;
	private ServerCommandGameState command;

	@Mock
	private FantasyFootballClient client;

	@Mock
	private Game incomingGame;

	@Mock
	private FieldModel incomingFieldModel;

	@Mock
	private Game existingGame;

	@Mock
	private FieldModel existingFieldModel;

	@BeforeEach
	public void setUp() {
		handler = new SubHandlerGameStateMarking(client);
		given(incomingGame.getFieldModel()).willReturn(incomingFieldModel);
		given(existingGame.getFieldModel()).willReturn(existingFieldModel);
		given(client.getGame()).willReturn(existingGame).willReturn(incomingGame);

		command = new ServerCommandGameState(incomingGame);
	}

	@Test
	public void run() {
		handler.handleNetCommand(command);
	}
}