package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PushbackSquare;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kalimar
 */
public class FieldLayerOverPlayers extends FieldLayer {

	public static final Color COLOR_MOVE_SQUARE = new Color(1.0f, 1.0f, 0.0f, 0.3f);
	public static final Color COLOR_TARGET_NUMBER = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Color COLOR_FIREBALL_AREA = new Color(1.0f, 0.0f, 0.0f, 0.4f);
	public static final Color COLOR_FIREBALL_AREA_FADED = new Color(1.0f, 0.0f, 0.0f, 0.2f);

	private FieldCoordinate fThrownPlayerCoordinate;
	private FieldCoordinate fMarkerCoordinate;

	public FieldLayerOverPlayers(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
	}

	public void removeThrownPlayer() {
		if (fThrownPlayerCoordinate != null) {
			clear(fThrownPlayerCoordinate, true);
			fThrownPlayerCoordinate = null;
		}
	}

	public void drawThrownPlayer(Game pGame, Player<?> pThrownPlayer, FieldCoordinate pCoordinate, boolean pWithBall) {
		if ((pCoordinate != null) && (pThrownPlayer != null)) {
			clear(pCoordinate, true);
			fThrownPlayerCoordinate = pCoordinate;
			Graphics2D g2d = getImage().createGraphics();
			PlayerIconFactory playerIconFactory = getClient().getUserInterface().getPlayerIconFactory();
			boolean homePlayer = pGame.getTeamHome().hasPlayer(pThrownPlayer);
			BufferedImage icon = playerIconFactory.getBasicIcon(getClient(), pThrownPlayer, homePlayer, false, pWithBall,
					false);
			if (icon != null) {
				g2d.drawImage(icon, findCenteredIconUpperLeftX(icon, pCoordinate),
						findCenteredIconUpperLeftY(icon, pCoordinate), null);
			}
			g2d.dispose();
		}
	}

	public void drawPushbackSquare(PushbackSquare pPushbackSquare) {
		if (pPushbackSquare != null) {
			clear(pPushbackSquare.getCoordinate(), true);
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage pushbackIcon = iconCache.getIcon(pPushbackSquare);
			draw(pushbackIcon, pPushbackSquare.getCoordinate(), 1.0f);
		}
	}

	public void removePushbackSquare(PushbackSquare pPushbackSquare) {
		if (pPushbackSquare != null) {
			clear(pPushbackSquare.getCoordinate(), true);
		}
	}

	public void drawDiceDecoration(DiceDecoration pDiceDecoration) {
		drawDiceDecoration(pDiceDecoration, true);
	}

	private void drawDiceDecoration(DiceDecoration pDiceDecoration, boolean pClearBeforeDraw) {

		if (pDiceDecoration != null) {

			if (pClearBeforeDraw) {
				clear(pDiceDecoration.getCoordinate(), true);
				MoveSquare moveSquare = getClient().getGame().getFieldModel().getMoveSquare(pDiceDecoration.getCoordinate());
				if (moveSquare != null) {
					drawMoveSquare(moveSquare, false);
				}
			}

			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage decorationIcon = iconCache.getIcon(pDiceDecoration);
			draw(decorationIcon, pDiceDecoration.getCoordinate(), 1.0f);

		}

	}

	public void removeDiceDecoration(DiceDecoration pDiceDecoration) {
		if (pDiceDecoration != null) {
			clear(pDiceDecoration.getCoordinate(), true);
		}
	}

	public void drawMoveSquare(MoveSquare pMoveSquare) {
		drawMoveSquare(pMoveSquare, true);
	}

	private void drawMoveSquare(MoveSquare pMoveSquare, boolean pClearBeforeDraw) {

		if ((pMoveSquare != null) && (ClientMode.PLAYER == getClient().getMode())
				&& getClient().getGame().isHomePlaying()) {

			if (pClearBeforeDraw) {
				clear(pMoveSquare.getCoordinate(), true);
			}

			Dimension dimensionWithOffset = dimensionProvider.map(pMoveSquare.getCoordinate(), true);
			Dimension dimension = dimensionProvider.map(pMoveSquare.getCoordinate());
			int x = dimension.width + 2;
			int y = dimension.height + 2;
			Graphics2D g2d = getImage().createGraphics();

			g2d.setPaint(COLOR_MOVE_SQUARE);
			Rectangle bounds = new Rectangle(x, y, dimensionProvider.fieldSquareSize() - 4, dimensionProvider.fieldSquareSize() - 4);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

			g2d.setColor(COLOR_TARGET_NUMBER);
			if ((pMoveSquare.getMinimumRollGoForIt() > 0) && (pMoveSquare.getMinimumRollDodge() > 0)) {

				StringBuilder numberGoForIt = new StringBuilder();
				if (pMoveSquare.getMinimumRollGoForIt() < 6) {
					numberGoForIt.append(pMoveSquare.getMinimumRollGoForIt()).append("+");
				} else {
					numberGoForIt.append(6);
				}
				g2d.setFont(new Font("Sans Serif", Font.PLAIN, 10));
				FontMetrics metrics = g2d.getFontMetrics();
				Rectangle2D numberBounds = metrics.getStringBounds(numberGoForIt.toString(), g2d);
				x = dimensionWithOffset.width - (int) (numberBounds.getWidth() / 2) - 5;
				y = dimensionWithOffset.height + (int) (numberBounds.getHeight() / 2) - 9;
				g2d.drawString(numberGoForIt.toString(), x, y);

				x = dimensionWithOffset.width - 10;
				y = dimensionWithOffset.height + 10;
				g2d.drawLine(x, y, x + 20, y - 20);

				StringBuilder numberDodge = new StringBuilder();
				numberDodge.append(pMoveSquare.getMinimumRollDodge()).append("+");
				g2d.setFont(new Font("Sans Serif", Font.PLAIN, 10));
				metrics = g2d.getFontMetrics();
				numberBounds = metrics.getStringBounds(numberDodge.toString(), g2d);
				x = dimensionWithOffset.width - (int) (numberBounds.getWidth() / 2) + 7;
				y = dimensionWithOffset.height + (int) (numberBounds.getHeight() / 2) + 5;
				g2d.drawString(numberDodge.toString(), x, y);

			} else {

				int minimumRoll = Math.max(pMoveSquare.getMinimumRollGoForIt(), pMoveSquare.getMinimumRollDodge());
				if (minimumRoll > 0) {
					StringBuilder number = new StringBuilder();
					if (minimumRoll < 6) {
						number.append(minimumRoll).append("+");
					} else {
						number.append(6);
					}
					g2d.setFont(new Font("Sans Serif", Font.PLAIN, 11));
					FontMetrics metrics = g2d.getFontMetrics();
					Rectangle2D numberBounds = metrics.getStringBounds(number.toString(), g2d);
					x = dimensionWithOffset.width - (int) (numberBounds.getWidth() / 2) + 1;
					y = dimensionWithOffset.height + (int) (numberBounds.getHeight() / 2) - 2;
					g2d.drawString(number.toString(), x, y);
				}

			}

			g2d.dispose();

			if (pClearBeforeDraw) {
				DiceDecoration diceDecoration = getClient().getGame().getFieldModel()
						.getDiceDecoration(pMoveSquare.getCoordinate());
				if (diceDecoration != null) {
					drawDiceDecoration(diceDecoration, false);
				}
			}

		}

	}

	public void removeMoveSquare(MoveSquare pMoveSquare) {
		if (pMoveSquare != null) {
			clear(pMoveSquare.getCoordinate(), true);
		}
	}

	public void drawSpellMarker(FieldCoordinate pMarkerCoordinate, String iconProperty, boolean pFaded) {
		if ((pMarkerCoordinate != null) && !pMarkerCoordinate.equals(fMarkerCoordinate)) {
			fMarkerCoordinate = pMarkerCoordinate;
			clear(fMarkerCoordinate, true);
			Dimension dimension = dimensionProvider.map(fMarkerCoordinate);
			Graphics2D g2d = getImage().createGraphics();
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage spellIcon = iconCache.getIconByProperty(iconProperty);
			if (pFaded) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			}
			g2d.drawImage(spellIcon, dimension.width, dimension.height, null);
			g2d.dispose();
		}
	}

	public void clearSpellMarker() {
		if (fMarkerCoordinate != null) {
			clear(fMarkerCoordinate, true);
			fMarkerCoordinate = null;
		}
	}

	public void drawFireballMarker(FieldCoordinate pMarkerCoordinate, boolean pFaded) {
		if ((pMarkerCoordinate != null) && !pMarkerCoordinate.equals(fMarkerCoordinate)) {
			fMarkerCoordinate = pMarkerCoordinate;
			clear(fMarkerCoordinate, true);
			Dimension dimension = dimensionProvider.map(fMarkerCoordinate);
			Graphics2D g2d = getImage().createGraphics();
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage fireballIcon = iconCache.getIconByProperty(IIconProperty.GAME_FIREBALL_SMALL);
			if (pFaded) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			}
			g2d.drawImage(fireballIcon, dimension.width, dimension.height, null);
			Game game = getClient().getGame();
			FieldCoordinate[] markedSquares = game.getFieldModel().findAdjacentCoordinates(fMarkerCoordinate,
					FieldCoordinateBounds.FIELD, 1, false);
			for (FieldCoordinate markedSquare : markedSquares) {
				if (pFaded) {
					markSquare(markedSquare, COLOR_FIREBALL_AREA_FADED);
				} else {
					markSquare(markedSquare, COLOR_FIREBALL_AREA);
				}
			}
			g2d.dispose();
		}
	}

	public void clearFireballMarker() {
		if (fMarkerCoordinate != null) {
			Game game = getClient().getGame();
			FieldCoordinate[] markedSquares = game.getFieldModel().findAdjacentCoordinates(fMarkerCoordinate,
				FieldCoordinateBounds.FIELD, 1, true);
			for (FieldCoordinate markedSquare : markedSquares) {
				clear(markedSquare, true);
			}
			fMarkerCoordinate = null;
		}
	}

	private void markSquare(FieldCoordinate pCoordinate, Color pColor) {
		if (pCoordinate != null) {
			clear(pCoordinate, true);
			Dimension dimension = dimensionProvider.map(fMarkerCoordinate);
			Rectangle bounds = new Rectangle(dimension.width + 1, dimension.height + 1, dimensionProvider.fieldSquareSize() - 2, dimensionProvider.fieldSquareSize() - 2);
			Graphics2D g2d = getImage().createGraphics();
			g2d.setPaint(pColor);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2d.dispose();
		}
	}

	public void init() {
		clear(true);
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (fieldModel != null) {
			for (PushbackSquare pushbackSquare : fieldModel.getPushbackSquares()) {
				drawPushbackSquare(pushbackSquare);
			}
			for (DiceDecoration diceDecoration : fieldModel.getDiceDecorations()) {
				drawDiceDecoration(diceDecoration);
			}
			for (MoveSquare moveSquare : fieldModel.getMoveSquares()) {
				drawMoveSquare(moveSquare);
			}
		}
	}

}
