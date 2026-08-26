package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.DebugLog;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.util.UtilPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@MockitoSettings(strictness = Strictness.LENIENT)
class UtilServerPlayerMoveDodgeDerivationTest {

	private static final long GAME_ID = 47L;
	private static final String PLAYER_ID = "moving-player";
	private static final FieldCoordinate DESTINATION = new FieldCoordinate(10, 7);

	@Mock
	private GameState gameState;
	@Mock
	private Game game;
	@Mock
	private ActingPlayer actingPlayer;
	@Mock
	private Player player;
	@Mock
	private MoveSquare offeredMove;
	@Mock
	private FantasyFootballServer server;
	@Mock
	private DebugLog debugLog;

	private MockedStatic<UtilPlayer> utilPlayer;

	@BeforeEach
	void setUp() {
		given(gameState.getGame()).willReturn(game);
		given(gameState.getServer()).willReturn(server);
		given(server.getDebugLog()).willReturn(debugLog);
		given(game.getId()).willReturn(GAME_ID);
		given(game.getActingPlayer()).willReturn(actingPlayer);
		given(actingPlayer.getPlayer()).willReturn(player);
		given(actingPlayer.getPlayerId()).willReturn(PLAYER_ID);
		given(actingPlayer.getPlayerAction()).willReturn(PlayerAction.MOVE);
		utilPlayer = mockStatic(UtilPlayer.class);
	}

	@AfterEach
	void tearDown() {
		utilPlayer.close();
	}

	@Test
	void derivesEachArrayPopFromItsLiveDepartureSquare() {
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(1, 1, 0);

		assertTrue(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, null));
		assertTrue(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, null));
		assertFalse(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, null));

		verify(debugLog, times(2)).log(IServerLogLevel.WARN, GAME_ID,
			"!Dodge derivation divergence command=MOVE player=" + PLAYER_ID
				+ " square=" + DESTINATION + " offered=null derived=true");
	}

	@Test
	void matchingFreshOfferRetainsFirstSquareParityWithoutDiagnostic() {
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(1);
		given(offeredMove.isDodging()).willReturn(true);

		assertTrue(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, offeredMove));

		verifyNoInteractions(debugLog);
	}

	@Test
	void liveMarkedStateOverridesStaleFalseOffer() {
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(1);
		given(offeredMove.isDodging()).willReturn(false);

		assertTrue(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, offeredMove));

		verify(debugLog).log(IServerLogLevel.WARN, GAME_ID,
			"!Dodge derivation divergence command=MOVE player=" + PLAYER_ID
				+ " square=" + DESTINATION + " offered=false derived=true");
	}

	@Test
	void liveUnmarkedStateOverridesStaleTrueOffer() {
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(0);
		given(offeredMove.isDodging()).willReturn(true);

		assertFalse(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, offeredMove));

		verify(debugLog).log(IServerLogLevel.WARN, GAME_ID,
			"!Dodge derivation divergence command=MOVE player=" + PLAYER_ID
				+ " square=" + DESTINATION + " offered=true derived=false");
	}

	@Test
	void missingOfferOnUnmarkedInteriorRemainsNoDodgeWithoutDiagnostic() {
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(0);

		assertFalse(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, null));

		verifyNoInteractions(debugLog);
	}

	@Test
	void tackleZoneIgnoringMoverNeverDodges() {
		given(player.hasSkillProperty(NamedProperties.ignoreTacklezonesWhenMoving)).willReturn(true);
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(2);

		assertFalse(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, null));
		verifyNoInteractions(debugLog);
	}

	@Test
	void jumpingPlayerNeverAlsoDodges() {
		given(actingPlayer.isJumping()).willReturn(true);
		given(offeredMove.isDodging()).willReturn(true);
		utilPlayer.when(() -> UtilPlayer.findTacklezones(game, player)).thenReturn(2);

		assertFalse(UtilServerPlayerMove.deriveDodgeRequired(gameState, DESTINATION, offeredMove));
		verifyNoInteractions(debugLog);
	}
}
