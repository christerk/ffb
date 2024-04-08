package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.marking.PlayerMarker;
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class PlayerIconFactory {

	public static BufferedImage decorateIcon(BufferedImage pIcon, BufferedImage pDecoration, Dimension maxIconSize) {
		BufferedImage resultingIcon = new BufferedImage(maxIconSize.width, maxIconSize.height, BufferedImage.TYPE_INT_ARGB);
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

	private static void markIcon(BufferedImage pIcon, String pText, FontCache fontCache, StyleProvider styleProvider, boolean homePlayer) {
		if ((pIcon != null) && StringTool.isProvided(pText)) {
			Graphics2D g2d = pIcon.createGraphics();
			g2d.setColor(homePlayer ? styleProvider.getPlayerMarkerHome() : styleProvider.getPlayerMarkerAway());
			g2d.setFont(fontCache.font(Font.BOLD, 12));
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D textBounds = metrics.getStringBounds(pText, g2d);
			int x = (int) ((pIcon.getWidth() - textBounds.getWidth()) / 2);
			int y = pIcon.getHeight() - metrics.getDescent();
			g2d.drawString(pText, x, y);
			g2d.dispose();
		}
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

	public BufferedImage getBasicIcon(FantasyFootballClient pClient, Player<?> pPlayer, boolean pHomePlayer, boolean pMoving,
	                                  boolean pWithBall, boolean pWithBomb) {

		if ((pClient == null) || (pPlayer == null)) {
			return null;
		}

		IconCache iconCache = pClient.getUserInterface().getIconCache();
		String settingIcons = pClient.getProperty(CommonProperty.SETTING_ICONS);
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

		boolean useHomeColor = pHomePlayer;

		String swapSetting = pClient.getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

		if (swapColors) {
			useHomeColor = !pHomePlayer;
		}

		DimensionProvider dimensionProvider = pClient.getUserInterface().getDimensionProvider();
		if (StringTool.isProvided(iconSetUrl)) {
			BufferedImage iconSet = iconCache.getUnscaledIconByUrl(iconSetUrl);
			if (iconSet != null) {
				int iconSize = iconSet.getWidth() / 4;
				int y = pPlayer.getIconSetIndex() * iconSize;
				int x;
				if (useHomeColor) {
					x = (pMoving ? 1 : 0) * iconSize;
				} else {
					x = (pMoving ? 3 : 2) * iconSize;
				}
				int scaledIconSize = dimensionProvider.scale(iconSize);
				icon = new BufferedImage(scaledIconSize, scaledIconSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = icon.createGraphics();
				if (swapColors) {
					g2d.drawImage(iconSet, scaledIconSize, 0, 0, scaledIconSize, x, y, x + iconSize, y + iconSize, null);
				} else {
					g2d.drawImage(iconSet, 0, 0, scaledIconSize, scaledIconSize, x, y, x + iconSize, y + iconSize, null);
				}
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
				if (useHomeColor) {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_LARGE_HOME);
				} else {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_LARGE_AWAY);
				}
			} else if (pPlayer.hasSkillProperty(NamedProperties.smallIcon)) {
				fontSize = 13;
				if (useHomeColor) {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_SMALL_HOME);
				} else {
					playerIcon = iconCache.getIconByProperty(IIconProperty.PLAYER_SMALL_AWAY);
				}
			} else {
				fontSize = 15;
				if (useHomeColor) {
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
					FontCache fontCache = pClient.getUserInterface().getFontCache();
					Graphics2D g2d = icon.createGraphics();
					g2d.drawImage(playerIcon, 2, 2, null);
					g2d.setFont(fontCache.font(Font.BOLD, fontSize));
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

		Dimension maxIconSize = dimensionProvider.dimension(DimensionProvider.Component.MAX_ICON);

		icon = decorateIcon(icon, null, maxIconSize);

		if (pWithBomb) {
			if (pMoving) {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BOMB_SELECTED), maxIconSize);
			} else {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BOMB), maxIconSize);
			}
		}

		if (pWithBall && !pWithBomb) {
			if (pMoving) {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BALL_SELECTED), maxIconSize);
			} else {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_BALL), maxIconSize);
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
		boolean playerOnPitch = FieldCoordinateBounds.FIELD.isInBounds(playerCoordinate);
		boolean withBomb = (playerOnPitch
			&& playerCoordinate.equals(game.getFieldModel().getBombCoordinate()) && !game.getFieldModel().isBombMoving());
		boolean withBall = (playerOnPitch && !game.getFieldModel().isBallMoving()
			&& playerCoordinate.equals(game.getFieldModel().getBallCoordinate()));

		boolean homePlayer = game.getTeamHome().hasPlayer(pPlayer);
		if (playerState.getBase() != PlayerState.PICKED_UP && playerState.getBase() != PlayerState.IN_THE_AIR) {
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
				case PlayerState.SETUP_PREVENTED:
					decorationProperty2 = IIconProperty.DECORATION_STUNNED;
					break;
				case PlayerState.BLOCKED:
				case PlayerState.FALLING:
				case PlayerState.HIT_ON_GROUND:
					boolean useHomeProperty = game.isHomePlaying();
					if (game.getTurnMode() == TurnMode.TRICKSTER) {
						useHomeProperty = !useHomeProperty;
					}
					if (useHomeProperty) {
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

		Dimension maxIconSize = pClient.getUserInterface().getDimensionProvider().dimension(DimensionProvider.Component.MAX_ICON);

		if (decorationProperty1 != null) {
			icon = decorateIcon(icon, iconCache.getIconByProperty(decorationProperty1), maxIconSize);
		}
		if (decorationProperty2 != null) {
			icon = decorateIcon(icon, iconCache.getIconByProperty(decorationProperty2), maxIconSize);
		}
		if (fadeIcon) {
			icon = fadeIcon(icon);
			if (!playerState.isActive() && playerState.getBase() != PlayerState.BEING_DRAGGED && playerOnPitch
				&& IClientPropertyValue.SETTING_MARK_USED_PLAYERS_CHECK_ICON_GREEN.equals(pClient.getProperty(CommonProperty.SETTING_MARK_USED_PLAYERS))) {
				icon = decorateIcon(icon, iconCache.getIconByProperty(IIconProperty.DECORATION_CHECK_ICON_GREEN), maxIconSize);
			}
		}

		PlayerMarker playerMarker = ClientMode.PLAYER == pClient.getMode() ? game.getFieldModel().getPlayerMarker(pPlayer.getId()) : game.getFieldModel().getTransientPlayerMarker(pPlayer.getId());
		if ((playerMarker != null)) {
			String homeText = playerMarker.getHomeText();
			pClient.logDebug(0, Thread.currentThread().getName() + " marking " + pPlayer.getId() + " with " + homeText);
			markIcon(icon, homeText, pClient.getUserInterface().getFontCache(), pClient.getUserInterface().getStyleProvider(), homePlayer);
		}

		return icon;

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
