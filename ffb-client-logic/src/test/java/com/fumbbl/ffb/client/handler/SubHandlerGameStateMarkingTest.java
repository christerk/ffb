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
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class SubHandlerGameStateMarkingTest {

    private static final PlayerMarker INCOMING_PLAYER = new PlayerMarker("incomingPlayer");
    private static final PlayerMarker EXISTING_PLAYER = new PlayerMarker("existingPlayer");
    private static final TransientPlayerMarker INCOMING_TRANSIENT_PLAYER = new TransientPlayerMarker("incomingTransientPlayer", TransientPlayerMarker.Mode.APPEND);
    private static final TransientPlayerMarker EXISTING_TRANSIENT_PLAYER = new TransientPlayerMarker("existingTransientPlayer", TransientPlayerMarker.Mode.APPEND);
    private static final FieldMarker INCOMING_FIELD = new FieldMarker(new FieldCoordinate(0,0), "incomingField", "");
    private static final FieldMarker EXISTING_FIELD = new FieldMarker(new FieldCoordinate(0,0), "existingField", "");
    private static final FieldMarker INCOMING_TRANSIENT_FIELD = new FieldMarker(new FieldCoordinate(0,0), "incomingTransientField", "");
    private static final FieldMarker EXISTING_TRANSIENT_FIELD = new FieldMarker(new FieldCoordinate(0,0), "existingTransientField", "");

    private SubHandlerGameStateMarking handler;
    private ServerCommandGameState command;

    @Mock
    private FantasyFootballClient client;

    @Mock
    private Game incomingGame;

    @Mock
    private Game existingGame;

    private FieldModel incomingFieldModel;
    private FieldModel existingFieldModel;

    @BeforeEach
    public void setUp() {
        handler = new SubHandlerGameStateMarking(client);
        
        // Create real FieldModel instances
        existingFieldModel = new FieldModel(existingGame);
        incomingFieldModel = new FieldModel(incomingGame);
        
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

        // Create markers
        PlayerMarker marker1 = INCOMING_PLAYER;
        marker1.setHomeText("home1");
        marker1.setAwayText("away1");
        PlayerMarker marker2 = new PlayerMarker("player2"); 
        marker2.setHomeText("home2");
        marker2.setAwayText("away2");
        
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);
        transientMarker.setHomeText("transient_home");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        fieldMarker.setHomeText("field_home");
        fieldMarker.setAwayText("field_away");
        
        // Add markers to existing field model
        existingFieldModel.add(marker1);
        existingFieldModel.add(marker2);
        existingFieldModel.addTransient(transientMarker);
        existingFieldModel.add(fieldMarker);
        
        // Execute
        Game result = handler.handleNetCommand(command);
        
        // Assert game was set on client
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);
        
        // Assert markers were transferred correctly
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1, marker2}, resultModel.getPlayerMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{fieldMarker}, resultModel.getFieldMarkers());
    }

    @Test
    public void testManualReplayGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.REPLAY);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
        given(existingGame.getId()).willReturn(1L);

        // Create and add markers
        PlayerMarker existingMarker = new PlayerMarker("existing");
        existingFieldModel.add(existingMarker);
        
        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        incomingFieldModel.add(incomingMarker);
        
        // Execute
        Game result = handler.handleNetCommand(command);
        
        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);
        
        // All markers should remain existing
        assertArrayEquals(new PlayerMarker[]{incomingMarker}, result.getFieldModel().getPlayerMarkers());
    }

    @Test
    public void testManualPlayerGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.PLAYER);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field and Player markers should be incoming, Transients existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{incomingMarker}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testManualSpectatorGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.SPECTATOR);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticReplayInitialGameState() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.REPLAY);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(incomingGame.getId()).willReturn(0L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to existing model
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // All markers should be transferred
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{fieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticPlayerGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.PLAYER);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticSpectatorGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.SPECTATOR);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticReplayGameStateUpdate() {
        // Set up client mode and marking type
        given(client.getMode()).willReturn(ClientMode.REPLAY);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to existing model
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // All markers should remain existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{fieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testManualPlayerReconnecting() {
        // Set up client mode and marking type
        given(incomingGame.getStarted()).willReturn(new Date());
        given(client.getMode()).willReturn(ClientMode.PLAYER);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field and Player markers should be incoming, Transients existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{incomingMarker}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testManualSpectatorReconnecting() {
        // Set up client mode and marking type
        given(incomingGame.getStarted()).willReturn(new Date());
        given(client.getMode()).willReturn(ClientMode.SPECTATOR);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_MANUAL);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticPlayerReconnecting() {
        // Set up client mode and marking type
        given(incomingGame.getStarted()).willReturn(new Date());
        given(client.getMode()).willReturn(ClientMode.PLAYER);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }

    @Test
    public void testAutomaticSpectatorReconnecting() {
        // Set up client mode and marking type
        given(incomingGame.getStarted()).willReturn(new Date());
        given(client.getMode()).willReturn(ClientMode.SPECTATOR);
        given(client.getProperty(CommonProperty.SETTING_PLAYER_MARKING_TYPE)).willReturn(CommonPropertyValue.SETTING_PLAYER_MARKING_TYPE_AUTO);
        given(existingGame.getId()).willReturn(1L);

        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
        FieldMarker fieldMarker = new FieldMarker(new FieldCoordinate(5, 5));
        TransientPlayerMarker transientMarker = new TransientPlayerMarker("transient1", TransientPlayerMarker.Mode.APPEND);

        // Add markers to models
        existingFieldModel.add(marker1);
        existingFieldModel.add(fieldMarker);
        existingFieldModel.addTransient(transientMarker);

        PlayerMarker incomingMarker = new PlayerMarker("incoming");
        FieldMarker incomingFieldMarker = new FieldMarker(new FieldCoordinate(6, 6));
        incomingFieldModel.add(incomingMarker);
        incomingFieldModel.add(incomingFieldMarker);

        // Execute
        Game result = handler.handleNetCommand(command);

        // Verify game was set
        assertSame(incomingGame, result);
        verify(client).setGame(incomingGame);

        // Field markers incoming, rest existing
        FieldModel resultModel = result.getFieldModel();
        assertArrayEquals(new PlayerMarker[]{marker1}, resultModel.getPlayerMarkers());
        assertArrayEquals(new FieldMarker[]{incomingFieldMarker}, resultModel.getFieldMarkers());
        assertArrayEquals(new TransientPlayerMarker[]{transientMarker}, resultModel.getTransientPlayerMarkers());
    }
}