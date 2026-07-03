package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ActivePlayerHighlighter {

	private FantasyFootballClient client;
	private PitchDimensionProvider pitchDimensionProvider;
	private BufferedImage activePlayerIcon;
	private Graphics2D g2d;

	private static ActivePlayerHighlighter instance;
	private volatile boolean isHighlightingOn;
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
	                       Graphics2D g2d

	) {
		this.client = client;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.g2d = g2d;
		if (animationTimer == null) {
			animationTimer = new javax.swing.Timer(40, e -> {
				if (isHighlightingOn) {
					// Update brightness (Ping-pong logic)
					brightness += delta;
					if (brightness > 1.5f || brightness < 0.8f) {
						delta = -delta; // Reverse direction
					}
					repaint(brightness,
							g2d,
							activePlayerIcon,
							client.getGame().getFieldModel().getPlayerCoordinate(activePlayer));
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
	 * @param activePlayer
	 */
	public void setActivePlayer(Player<?> activePlayer) {
		if (activePlayer == null) {
			stopHighlighting();
		} else {
			this.activePlayer = activePlayer;
			stopHighlighting();
			startHighlighting();
		}
	}

	private void startHighlighting() {
		isHighlightingOn = true;

		PlayerIconFactory playerIconFactory = client.getUserInterface().getPlayerIconFactory();
		activePlayerIcon = playerIconFactory.getIcon(client, activePlayer, pitchDimensionProvider);
		animationTimer.start();
	}

	private void stopHighlighting() {
		isHighlightingOn = false;
		brightness = 1.0f;
	}

	private void repaint(float brightness, Graphics2D g2d, BufferedImage activePlayerIcon, FieldCoordinate playerCoordinate) {
		// 1.0 is original brightness, > 1.0 is brighter, < 1.0 is darker
		float[] scales = {brightness, brightness, brightness, 1.0f};
		float[] offsets = {0, 0, 0, 0};
		RescaleOp rescale = new RescaleOp(scales, offsets, null);

		int upperLeftX = findCenteredIconUpperLeftX(activePlayerIcon, playerCoordinate);
		int upperLeftY = findCenteredIconUpperLeftY(activePlayerIcon, playerCoordinate);
		g2d.setClip(upperLeftX, upperLeftY, activePlayerIcon.getWidth(), activePlayerIcon.getHeight());
		g2d.drawImage(activePlayerIcon, rescale, upperLeftX, upperLeftY);
	}

	private void refreshPlayerSquare() {
		if (activePlayer == null || activePlayerIcon == null) {
			return;
		}

		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(activePlayer);

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
}
