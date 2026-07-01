package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.Component;
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
	}

	private Player<?> activePlayer;

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

	private void repaint() {
        // 1.0 is original brightness, > 1.0 is brighter, < 1.0 is darker
        float[] scales = {brightness, brightness, brightness, 1.0f};
        float[] offsets = {0, 0, 0, 0};
        RescaleOp rescale = new RescaleOp(scales, offsets, null);

		FieldCoordinate playerCoordinate = client.getGame().getFieldModel().getPlayerCoordinate(activePlayer);

		int upperLeftX = findCenteredIconUpperLeftX(activePlayerIcon, playerCoordinate);
		int upperLeftY = findCenteredIconUpperLeftY(activePlayerIcon, playerCoordinate);
		g2d.setClip(upperLeftX, upperLeftY, activePlayerIcon.getWidth(), activePlayerIcon.getHeight());
		g2d.drawImage(activePlayerIcon, rescale, upperLeftX, upperLeftY);

		refreshPlayerSquare();
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

    private float brightness = 1.0f;
    private float delta = 0.05f; // How fast the brightness changes
    private Timer animationTimer;

	private void startHighlighting() {
        if (animationTimer != null && animationTimer.isRunning()) return;

        PlayerIconFactory playerIconFactory = client.getUserInterface().getPlayerIconFactory();
		activePlayerIcon = playerIconFactory.getIcon(client, activePlayer, pitchDimensionProvider);

        animationTimer = new javax.swing.Timer(40, e -> {
            // Update brightness (Ping-pong logic)
            brightness += delta;
            if (brightness > 1.5f || brightness < 0.8f) {
                delta = -delta; // Reverse direction
            }

            // Trigger a repaint of the component containing the icon
	        repaint();
        });
        animationTimer.start();
    }

    private void stopHighlighting() {
        if (animationTimer != null) animationTimer.stop();
        brightness = 1.0f;
		refreshPlayerSquare();
    }

}
