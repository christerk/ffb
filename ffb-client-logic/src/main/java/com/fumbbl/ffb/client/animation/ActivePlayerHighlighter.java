package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ActivePlayerHighlighter {

	private FantasyFootballClient client;
	private PitchDimensionProvider pitchDimensionProvider;
	private BufferedImage fieldLayerPlayers;
	private static ActivePlayerHighlighter instance;
	private Player<?> activePlayer;
	private PlayerIconFactory playerIconFactory;
	private HightlightAnimationPlayerTimer animationTimer;

	//When switching the layout - we do not want more then one instance of
	//ActivePlayerHighlighter...
	private ActivePlayerHighlighter() {
	}

	public static synchronized ActivePlayerHighlighter getInstance() {
		if (instance == null) {
			instance = new ActivePlayerHighlighter();
		}

		return instance;
	}

	public void initialize(FantasyFootballClient client,
	                       PitchDimensionProvider pitchDimensionProvider,
	                       BufferedImage fieldLayerPlayers,
	                       PlayerIconFactory playerIconFactory
	) {
		this.client = client;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.fieldLayerPlayers = fieldLayerPlayers;
		this.playerIconFactory = playerIconFactory;
	}

	/**
	 * When we want to start highlighting animation for active player then we set FieldCoordinate for active player,
	 * when we want to stop highlighting we set null as input.
	 *
	 * @param newActivePlayer
	 */
	public synchronized void setActivePlayer(Player<?> newActivePlayer) {
		if (newActivePlayer == null) {
			if (animationTimer != null) {
				animationTimer.stop();
				animationTimer = null;
			}
			this.activePlayer = null;
		} else if (this.activePlayer != null && this.activePlayer.equals(newActivePlayer)) {
			//If the new active player is the same player as it already was then just do nothing.
			return;
		} else {
			//If the new active player is not null and different player that was active previously,
			// then reinitialize highlighting.
			if (animationTimer != null) {
				animationTimer.stop();
			}
			this.activePlayer = newActivePlayer;
			animationTimer = new HightlightAnimationPlayerTimer(fieldLayerPlayers, newActivePlayer);
			animationTimer.start();
		}
	}

	private void repaint(float brightness, BufferedImage activePlayerIcon, FieldCoordinate playerCoordinate, Graphics2D g2d) {
		// 1.0 is original brightness, > 1.0 is brighter, < 1.0 is darker
		float[] scales = {brightness, brightness, brightness, 1.0f};
		float[] offsets = {0, 0, 0, 0};
		RescaleOp rescale = new RescaleOp(scales, offsets, null);

		int upperLeftX = findCenteredIconUpperLeftX(activePlayerIcon, playerCoordinate);
		int upperLeftY = findCenteredIconUpperLeftY(activePlayerIcon, playerCoordinate);
		//It is possible that animationData.g2d object will be disposed by the time of execution here,
		//but it should not cause errors. Atleast that is what chat Gemini says about behavior of disposed
		// java.awt.Graphics2D objects:
//		When you call dispose() on a Graphics2D object, you are explicitly telling the Java runtime that you are finished
//		with that graphics context and that it should release any system resources (such as native memory or window handles)
//		associated with it.
//
//		Internally, many implementations (such as the standard sun.java2d.SunGraphics2D used in most OpenJDK/Oracle
//		JDK environments) perform a "sabotage" action:
//
//		State Invalidation: The surfaceData—the internal pointer to the memory or component being drawn upon—is set to
//		a NullSurfaceData instance.
//
//		Pipeline Invalidation: The rendering pipe (the mechanism that actually processes drawing commands) is invalidated.
//
//		Because the graphics object no longer points to a valid destination surface, any subsequent calls to drawImage or setClip are ignored by the graphics pipeline. The system intentionally prevents further use to ensure that developers do not rely on "dead" objects, which might otherwise cause memory leaks or unpredictable behavior if the underlying native resources were freed.
		g2d.setClip(upperLeftX, upperLeftY, activePlayerIcon.getWidth(), activePlayerIcon.getHeight());
		g2d.drawImage(activePlayerIcon, rescale, upperLeftX, upperLeftY);
		g2d.dispose();
	}

	private void refreshPlayerSquare(Player<?> currentlyActivePlayer, BufferedImage activePlayerIcon) {
		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(currentlyActivePlayer);

		int upperLeftX = findCenteredIconUpperLeftX(activePlayerIcon, playerCoordinate);
		int upperLeftY = findCenteredIconUpperLeftY(activePlayerIcon, playerCoordinate);
		int width = activePlayerIcon.getWidth();
		int height = activePlayerIcon.getHeight();
		client.getUserInterface().getFieldComponent().refresh(new Rectangle(upperLeftX, upperLeftY, width, height));
	}

	protected int findCenteredIconUpperLeftX(BufferedImage activePlayerIcon, FieldCoordinate pCoordinate) {
		Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.width - (activePlayerIcon.getWidth() / 2);
	}

	protected int findCenteredIconUpperLeftY(BufferedImage activePlayerIcon, FieldCoordinate pCoordinate) {
		Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.height - (activePlayerIcon.getHeight() / 2);
	}

	class HightlightAnimationPlayerTimer extends Timer {

		// Define how many seconds it takes for a full pulse cycle (e.g., 1.5 seconds)
		// or just define the speed (brightness units per second).
		private static final float BRIGHTNESS_SPEED = 1.8f;
		private float direction = 1f; // 1.0 = increasing, -1.0 = decreasing
		private float brightness = 1f;
		private long lastRepaintTime;

		public HightlightAnimationPlayerTimer(final BufferedImage fieldLayerPlayers, final Player<?> currentlyActivatedPlayer) {
			super(40, null);

			// Initialize the timestamp
			lastRepaintTime = System.currentTimeMillis();

			this.addActionListener(e -> {
				long currentTime = System.currentTimeMillis();
				long timeDifferenceMs = currentTime - lastRepaintTime;
				lastRepaintTime = currentTime;

				// 2. Convert ms to seconds for easy math (e.g., 0.04s)
				float deltaTimeSeconds = timeDifferenceMs / 1000f;

				// 3. Update brightness based on time, not frame count
				brightness += BRIGHTNESS_SPEED * deltaTimeSeconds * direction;

				// 4. Ping-pong logic
				if (brightness > 1.6f) {
					brightness = 1.6f;
					direction = -1f;
				} else if (brightness < .6f) {
					brightness = .6f;
					direction = 1f;
				}

				FieldCoordinate playerCoordinate = client
						.getGame()
						.getFieldModel()
						.getPlayerCoordinate(currentlyActivatedPlayer);

				BufferedImage activePlayerIcon = playerIconFactory.getIcon(client, currentlyActivatedPlayer, pitchDimensionProvider);
				repaint(brightness, activePlayerIcon, playerCoordinate, fieldLayerPlayers.createGraphics());
				refreshPlayerSquare(currentlyActivatedPlayer, activePlayerIcon);
			});
		}

		@Override
		public void stop() {
			super.stop();
			brightness = 1.0f;
			direction = 1.0f; // Reset direction
		}
	}

}
