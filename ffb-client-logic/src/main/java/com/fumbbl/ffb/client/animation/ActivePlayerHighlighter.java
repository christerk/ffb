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
	private volatile boolean isHighlightingOn;
	private volatile AnimationDependentData animationDependentData;
	private Player<?> activePlayer;

	private float brightness = 1.0f;
	private float delta = 0.05f; // How fast the brightness changes
	private Timer animationTimer;

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
	                       BufferedImage fieldLayerPlayers

	) {
		this.client = client;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.fieldLayerPlayers = fieldLayerPlayers;
		if (animationTimer == null) {
			animationTimer = new javax.swing.Timer(40, e -> {
				if (isHighlightingOn) {
					AnimationDependentData currentData = animationDependentData;
					// Update brightness (Ping-pong logic)
					if (currentData != null) {
						brightness += delta;
						if (brightness > 1.5f || brightness < 0.8f) {
							delta = -delta; // Reverse direction
						}
						FieldCoordinate playerCoordinate = client
								.getGame()
								.getFieldModel()
								.getPlayerCoordinate(activePlayer);

						repaint(brightness, currentData, playerCoordinate);
					}
				} else {
					animationTimer.stop();
				}
				refreshPlayerSquare();
			});
		}
	}

	/**
	 * When we want to start highlighting animation for active player then we set FieldCoordinate for active player,
	 * when we want to stop highlighting we set null as input.
	 *
	 * @param newActivePlayer
	 */
	public synchronized void setActivePlayer(Player<?> newActivePlayer) {
		if (newActivePlayer == null) {
			stopHighlighting();
			this.activePlayer = null;
		} else if (this.activePlayer != null && this.activePlayer.equals(newActivePlayer)) {
			//If the new active player is the same player as it already was then just do nothing.
			return;
		} else {
			//If the new active player is not null and different player that was active previously,
			// then reinitialize highlighting.
			stopHighlighting();
			this.activePlayer = newActivePlayer;
			PlayerIconFactory playerIconFactory = client.getUserInterface().getPlayerIconFactory();
			animationDependentData = new AnimationDependentData(playerIconFactory.getIcon(client, newActivePlayer, pitchDimensionProvider), fieldLayerPlayers.createGraphics());
			startHighlighting();
		}
	}


	private void startHighlighting() {
		isHighlightingOn = true;
		animationTimer.start();
	}

	private void stopHighlighting() {
		isHighlightingOn = false;
		if (animationDependentData != null) {
			animationDependentData.g2d.dispose();
			animationDependentData = null;
		}
		brightness = 1.0f;
	}

	private void repaint(float brightness, AnimationDependentData animationData, FieldCoordinate playerCoordinate) {
		// 1.0 is original brightness, > 1.0 is brighter, < 1.0 is darker
		float[] scales = {brightness, brightness, brightness, 1.0f};
		float[] offsets = {0, 0, 0, 0};
		RescaleOp rescale = new RescaleOp(scales, offsets, null);

		BufferedImage activePlayerIcon = animationData.activePlayerIcon;

		int upperLeftX = findCenteredIconUpperLeftX(activePlayerIcon, playerCoordinate);
		int upperLeftY = findCenteredIconUpperLeftY(activePlayerIcon, playerCoordinate);
		Graphics2D g2d = animationData.g2d;
		g2d.setClip(upperLeftX, upperLeftY, activePlayerIcon.getWidth(), activePlayerIcon.getHeight());
		g2d.drawImage(activePlayerIcon, rescale, upperLeftX, upperLeftY);
	}

	private void refreshPlayerSquare() {
		AnimationDependentData currentData = animationDependentData;
		BufferedImage activePlayerIcon = currentData.activePlayerIcon;
		Player<?> currentlyActivePlayer = activePlayer;

		if (currentlyActivePlayer == null || activePlayerIcon == null) {
			return;
		}

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

	static class AnimationDependentData {
		public AnimationDependentData(BufferedImage activePlayerIcon, Graphics2D g2d) {
			this.activePlayerIcon = activePlayerIcon;
			this.g2d = g2d;
		}

		final BufferedImage activePlayerIcon;
		final Graphics2D g2d;
	}
}
