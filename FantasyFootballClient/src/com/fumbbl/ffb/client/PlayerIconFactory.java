package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerMarker;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilUrl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class PlayerIconFactory {

	private static final Color _MARK_COLOR = new Color(1.0f, 1.0f, 0.0f, 1.0f);

	public static final int MAX_ICON_WIDTH = 40;
	public static final int MAX_ICON_HEIGHT = 40;

	public BufferedImage getBasicIcon(FantasyFootballClient pClient, Player<?> pPlayer, boolean pHomePlayer, boolean pMoving,
	                                  boolean pWithBall, boolean pWithBomb) {

		if ((pClient == null) || (pPlayer == null)) {
			return null;
		}

		IconCache iconCache = pClient.getUserInterface().getIconCache();
		String settingIcons = pClient.getProperty(IClientProperty.SETTING_ICONS);
		BufferedImage icon = null;
		String iconSetUrl = null;

		if (!StringTool.isProvided(settingIcons) || IClientPropertyValue.SETTING_ICONS_TEAM.equals(settingIcons)
			|| (pHomePlayer && IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(settingIcons))) {
			if (pPlayer instanceof ZappedPlayer) {
				iconSetUrl = pClient.getProperty(IIconProperty.ZAPPEDPLAYER_ICONSET_PATH);
			} else {
				iconSetUrl = getIconSetUrl(pPlayer);
			}
		}

		if (!StringTool.isProvided(iconSetUrl) || IClientPropertyValue.SETTING_ICONS_ROSTER_BOTH.equals(settingIcons)
			|| (!pHomePlayer && IClientPropertyValue.SETTING_ICONS_ROSTER_OPPONENT.equals(settingIcons))) {
			if (pPlayer instanceof ZappedPlayer) {
				iconSetUrl = pClient.getProperty(IIconProperty.ZAPPEDPLAYER_ICONSET_PATH);
			} else {
				iconSetUrl = getIconSetUrl(pPlayer.getPosition());
			}
		}

		if (StringTool.isProvided(iconSetUrl)) {
			BufferedImage iconSet = iconCache.getIconByUrl(iconSetUrl);
			if (iconSet != null) {
				int iconSize = iconSet.getWidth() / 4;
				int y = pPlayer.getIconSetIndex() * iconSize;
				int x;
				if (pHomePlayer) {
					x = (pMoving ? 1 : 0) * iconSize;
				} else {
					x = (pMoving ? 3 : 2) * iconSize;
				}
				icon = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = icon.createGraphics();
				g2d.drawImage(iconSet, 0, 0, iconSize, iconSize, x, y, x + iconSize, y + iconSize, null);
				g2d.dispose();
			}
		}

		if ((icon == null) || IClientPropertyValue.SETTING_ICONS_ABSTRACT.equals(settingIcons)) {
			int fontSize;
			Color fontColor = Color.WHITE;
			Color shadowColor = Color.BLACK;
			BufferedImage playerIcon;
			if ((pPlayer.getPosition() != null) && (PlayerType.BIG_GUY == pPlayer.getPosition().getType())) {
				fontSize = 17;
				if (pHomePlayer) {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_LARGE_HOME);
				} else {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_LARGE_AWAY);
				}
			} else if (pPlayer.hasSkillProperty(NamedProperties.smallIcon)) {
				fontSize = 13;
				if (pHomePlayer) {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_SMALL_HOME);
				} else {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_SMALL_AWAY);
				}
			} else {
				fontSize = 15;
				if (pHomePlayer) {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_NORMAL_HOME);
				} else {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_NORMAL_AWAY);
				}
			}
			if (pMoving) {
				fontColor = Color.YELLOW;
				shadowColor = null;
			}
			if (playerIcon != null) {
				icon = new BufferedImage(playerIcon.getWidth() + 2, playerIcon.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
				String shorthand = (pPlayer.getPosition() != null) ? pPlayer.getPosition().getShorthand() : "?";
				if (StringTool.isProvided(shorthand)) {
					Graphics2D g2d = icon.createGraphics();
					g2d.drawImage(playerIcon, 2, 2, null);
					g2d.setFont(new Font("Sans Serif", Font.BOLD, fontSize));
					FontMetrics metrics = g2d.getFontMetrics();
					Rectangle2D stringBounds = metrics.getStringBounds(shorthand, g2d);
					int baselineX = (icon.getWidth() - (int) stringBounds.getWidth()) / 2;
					int baselineY = ((icon.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
					if (shadowColor != null) {
						g2d.setColor(shadowColor);
						g2d.drawString(shorthand, baselineX + 2, baselineY + 2);
					}
					g2d.setColor(fontColor);
					g2d.drawString(shorthand, baselineX + 1, baselineY + 1);
					g2d.dispose();
				}
			}
		}

		icon = decorateIcon(icon, null);

		if (pWithBomb) {
			if (pMoving) {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BOMB_SELECTED));
			} else {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BOMB));
			}
		}

		if (pWithBall && !pWithBomb) {
			if (pMoving) {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BALL_SELECTED));
			} else {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BALL));
			}
		}

		return icon;

	}

	public BufferedImage getIcon(FantasyFootballClient pClient, Player<?> pPlayer) {

		BufferedImage icon = null;
		IconCache iconCache = pClient.getUserInterface().getIconCache();
		Game game = pClient.getGame();

		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		boolean withBomb = (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate)
			&& playerCoordinate.equals(game.getFieldModel().getBombCoordinate()) && !game.getFieldModel().isBombMoving());
		boolean withBall = (FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate) && !game.getFieldModel().isBallMoving()
			&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate()));

		if (playerState.getBase() != PlayerState.PICKED_UP) {
			boolean homePlayer = game.getTeamHome().hasPlayer(pPlayer);
			icon = getBasicIcon(pClient, pPlayer, homePlayer, (playerState.getBase() == PlayerState.MOVING), withBall,
				withBomb);
		}

		boolean fadeIcon = false;
		String decorationProperty1 = null;
		String decorationProperty2 = null;

		if (icon != null) {
			switch (playerState.getBase()) {
				case PlayerState.BEING_DRAGGED:
					fadeIcon = true;
					break;
				case PlayerState.STANDING:
					fadeIcon = !playerState.isActive();
					break;
				case PlayerState.PRONE:
					fadeIcon = !playerState.isActive();
					decorationProperty2 = IIconProperty.DECORATION_PRONE;
					break;
				case PlayerState.EXHAUSTED:
				case PlayerState.STUNNED:
					decorationProperty2 = IIconProperty.DECORATION_STUNNED;
					break;
				case PlayerState.BLOCKED:
				case PlayerState.FALLING:
					if (game.isHomePlaying()) {
						decorationProperty2 = IIconProperty.DECORATION_BLOCK_HOME;
					} else {
						decorationProperty2 = IIconProperty.DECORATION_BLOCK_AWAY;
					}
					break;
				default:
					break;
			}
		}

		if (playerState.isRooted()) {
			if (playerState.isHypnotized()) {
				decorationProperty1 = IIconProperty.DECORATION_ROOTED_HYPNOTIZED;
			} else if (playerState.isConfused()) {
				decorationProperty1 = IIconProperty.DECORATION_ROOTED_CONFUSED;
			} else {
				decorationProperty1 = IIconProperty.DECORATION_ROOTED;
			}
		} else {
			if (playerState.isConfused()) {
				decorationProperty1 = IIconProperty.DECORATION_CONFUSED;
			} else if (playerState.isHypnotized()) {
				decorationProperty1 = IIconProperty.DECORATION_HYPNOTIZED;
			}
		}

		if (playerState.isSelectedBlitzTarget()) {
			decorationProperty2 = IIconProperty.DECORATION_BLITZ_TARGET_SELECTED;
		}
		if (playerState.isSelectedGazeTarget()) {
			decorationProperty2 = IIconProperty.DECORATION_GAZE_TARGET_SELECTED;
		}
		if (playerState.isSelectedBlockTarget()) {
			decorationProperty2 = IIconProperty.DECORATION_BLOCK_TARGET;
		}
		if (playerState.isSelectedStabTarget()) {
			decorationProperty2 = IIconProperty.DECORATION_STAB_TARGET;
		}
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayer() == pPlayer) && actingPlayer.isSufferingBloodLust()) {
			decorationProperty1 = IIconProperty.DECORATION_BLOOD_LUST;
		}

		if (decorationProperty1 != null) {
			icon = decorateIcon(icon, iconCache.getIconByProperty(decorationProperty1));
		}
		if (decorationProperty2 != null) {
			icon = decorateIcon(icon, iconCache.getIconByProperty(decorationProperty2));
		}
		if (fadeIcon) {
			icon = fadeIcon(icon);
		}

		PlayerMarker playerMarker = ClientMode.PLAYER == pClient.getMode() ? game.getFieldModel().getPlayerMarker(pPlayer.getId()) : game.getFieldModel().getTransientPlayerMarker(pPlayer.getId());
		if ((playerMarker != null)) {
			markIcon(icon, playerMarker.getHomeText());
		}

		return icon;

	}

	public static BufferedImage fadeIcon(BufferedImage pIcon) {
		BufferedImage resultingIcon = null;
		if (pIcon != null) {
			resultingIcon = new BufferedImage(pIcon.getWidth(), pIcon.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = resultingIcon.createGraphics();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2d.drawImage(pIcon, 0, 0, null);
			g2d.dispose();
		}
		return resultingIcon;
	}

	public static BufferedImage decorateIcon(BufferedImage pIcon, BufferedImage pDecoration) {
		BufferedImage resultingIcon = new BufferedImage(MAX_ICON_WIDTH, MAX_ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resultingIcon.createGraphics();
		if (pIcon != null) {
			int x = (resultingIcon.getWidth() - pIcon.getWidth()) / 2;
			int y = (resultingIcon.getHeight() - pIcon.getHeight()) / 2;
			g2d.drawImage(pIcon, x, y, null);
		}
		if (pDecoration != null) {
			int x = (resultingIcon.getWidth() - pDecoration.getWidth()) / 2;
			int y = (resultingIcon.getHeight() - pDecoration.getHeight()) / 2;
			g2d.drawImage(pDecoration, x, y, null);
		}
		g2d.dispose();
		return resultingIcon;
	}

	public static void markIcon(BufferedImage pIcon, String pText) {
		if ((pIcon != null) && StringTool.isProvided(pText)) {
			Graphics2D g2d = pIcon.createGraphics();
			g2d.setColor(_MARK_COLOR);
			g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D textBounds = metrics.getStringBounds(pText, g2d);
			int x = (int) ((pIcon.getWidth() - textBounds.getWidth()) / 2);
			int y = pIcon.getHeight() - metrics.getDescent();
			g2d.drawString(pText, x, y);
			g2d.dispose();
		}
	}

	public static String getPortraitUrl(Player<?> pPlayer) {
		if (pPlayer != null) {
			if (StringTool.isProvided(pPlayer.getUrlPortrait())) {
				return getIconUrl(pPlayer, pPlayer.getUrlPortrait());
			} else {
				return getPortraitUrl(pPlayer.getPosition());
			}
		}
		return null;
	}

	public static String getPortraitUrl(Position pPosition) {
		if (pPosition != null) {
			return getIconUrl(pPosition, pPosition.getUrlPortrait());
		}
		return null;
	}

	public static String getIconSetUrl(Player<?> pPlayer) {
		if (pPlayer != null) {
			if (StringTool.isProvided(pPlayer.getUrlIconSet())) {
				return getIconUrl(pPlayer, pPlayer.getUrlIconSet());
			} else {
				return getIconSetUrl(pPlayer.getPosition());
			}
		}
		return null;
	}

	public static String getIconSetUrl(Position pPosition) {
		if (pPosition != null) {
			return getIconUrl(pPosition, pPosition.getUrlIconSet());
		}
		return null;
	}

	private static String getIconUrl(Player<?> pPlayer, String pRelativeUrl) {
		if ((pPlayer != null) && StringTool.isProvided(pRelativeUrl)) {
			Team team = pPlayer.getTeam();
			if (team != null) {
				return UtilUrl.createUrl(team.getBaseIconPath(), pRelativeUrl);
			}
		}
		return pRelativeUrl;
	}

	private static String getIconUrl(Position pPosition, String pRelativeUrl) {
		if ((pPosition != null) && StringTool.isProvided(pRelativeUrl)) {
			Roster roster = pPosition.getRoster();
			if (roster != null) {
				return UtilUrl.createUrl(roster.getBaseIconPath(), pRelativeUrl);
			}
		}
		return pRelativeUrl;
	}

}
