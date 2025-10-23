package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.CommonPropertyValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.marking.TransientPlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.commands.ServerCommandGameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubHandlerGameStateMarkingTest {

	private static final PlayerMarker INCOMING_PLAYER = new PlayerMarker("incomingPlayer");
	private static final PlayerMarker EXISTING_PLAYER = new PlayerMarker("existingPlayer");
	private static final TransientPlayerMarker EXISTING_TRANSIENT_PLAYER = new TransientPlayerMarker("existingTransientPlayer", TransientPlayerMarker.Mode.APPEND);
	private static final FieldMarker INCOMING_FIELD = new FieldMarker(new FieldCoordinate(0, 0), "incomingField", "");
	private static final FieldMarker EXISTING_FIELD = new FieldMarker(new FieldCoordinate(0, 0), "existingField", "");
	private static final FieldMarker EXISTING_TRANSIENT_FIELD = new FieldMarker(new FieldCoordinate(0, 0), "existingTransientField", "");

	private SubHandlerGameStateMarking handler;
	private ServerCommandGameState command;

	@Mock
	private FantasyFootballClient client;

	@Mock
	private Game incomingGame;

	@Mock
	private Game existingGame;

	@BeforeEach
	public void setUp() {
		handler = new SubHandlerGameStateMarking(client);

		// Create real FieldModel instances
		FieldModel existingFieldModel = new FieldModel(existingGame);
		FieldModel incomingFieldModel = new FieldModel(incomingGame);

		// Add existing markers
		existingFieldModel.add(EXISTING_PLAYER);
		existingFieldModel.add(EXISTING_FIELD);
		existingFieldModel.addTransient(EXISTING_TRANSIENT_PLAYER);
		existingFieldModel.addTransient(EXISTING_TRANSIENT_FIELD);

		// Add incoming markers
		incomingFieldModel.add(INCOMING_PLAYER);
		incomingFieldModel.add(INCOMING_FIELD);

		given(incomingGame.getFieldModel()).willReturn(incomingFieldModel);
		given(existingGame.getFieldModel()).willReturn(existingFieldModel);
		given(client.getGame()).willReturn(existingGame);

		command = new ServerCommandGameState(incomingGame);
	}

	@Test
	public void testManualReplayInitialGameState() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.REPLAY);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Assert game was set on client
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualReplayGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.REPLAY);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualPlayerInitialGameState() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Assert game was set on client
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualPlayerGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{INCOMING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualSpectatorGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.SPECTATOR);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticReplayInitialGameState() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.REPLAY);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticPlayerInitialGameState() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticPlayerGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticSpectatorGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.SPECTATOR);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticReplayGameStateUpdate() {
		// Set up client mode and marking type
		given(client.getMode()).willReturn(ClientMode.REPLAY);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
		given(existingGame.getId()).willReturn(1L);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{EXISTING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualPlayerReconnecting() {
		// Set up client mode and marking type
		given(incomingGame.getStarted()).willReturn(new Date());
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{INCOMING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testManualSpectatorReconnecting() {
		// Set up client mode and marking type
		given(incomingGame.getStarted()).willReturn(new Date());
		given(client.getMode()).willReturn(ClientMode.SPECTATOR);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticPlayerReconnecting() {
		// Set up client mode and marking type
		given(incomingGame.getStarted()).willReturn(new Date());
		given(client.getMode()).willReturn(ClientMode.PLAYER);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{INCOMING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}

	@Test
	public void testAutomaticSpectatorReconnecting() {
		// Set up client mode and marking type
		given(incomingGame.getStarted()).willReturn(new Date());
		given(client.getMode()).willReturn(ClientMode.SPECTATOR);
		given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);

		// Execute
		Game result = handler.handleNetCommand(command);

		// Verify game was set
		assertSame(incomingGame, result);
		verify(client).setGame(incomingGame);

		// Assert markers were transferred correctly
		FieldModel resultModel = result.getFieldModel();
		assertArrayEquals(new FieldMarker[]{INCOMING_FIELD}, resultModel.getFieldMarkers());
		assertArrayEquals(new FieldMarker[]{EXISTING_TRANSIENT_FIELD}, resultModel.getTransientFieldMarkers());
		assertArrayEquals(new PlayerMarker[]{EXISTING_PLAYER}, resultModel.getPlayerMarkers());
		assertArrayEquals(new TransientPlayerMarker[]{EXISTING_TRANSIENT_PLAYER}, resultModel.getTransientPlayerMarkers());
	}
}