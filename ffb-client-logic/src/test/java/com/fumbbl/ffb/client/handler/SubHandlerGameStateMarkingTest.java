package com.fumbbl.ffb.client.handler;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubHandlerGameStateMarkingTest {

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
    public void testFirstGameStateHandling() {
        // Create markers
        PlayerMarker marker1 = new PlayerMarker("player1");
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

}