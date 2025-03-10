package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilCards;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class PlayerDetailComponent extends JPanel {

	private final FontCache fontCache;
	private Font nameFont;
	private Font statFont;
	private Font positionFont;
	private Font sppFont;
	private Font skillFont;

	private static final int _DISPLAY_NONE = 0;
	private static final int _DISPLAY_ACTING_PLAYER = 1;
	private static final int _DISPLAY_DEFENDING_PLAYER = 2;
	private static final int _DISPLAY_SELECTED_PLAYER = 3;
	public static final int LINE_LENGTH = 19;

	private final SideBarComponent fSideBar;
	private Player<?> fPlayer;
	private BufferedImage fImage;
	private boolean fRefreshNecessary;

	private Dimension size;
	private final DimensionProvider dimensionProvider;
	private final StyleProvider styleProvider;
	private Font skillUsedFont;

	public PlayerDetailComponent(SideBarComponent pSideBar, DimensionProvider dimensionProvider, StyleProvider styleProvider, FontCache fontCache) {
		fSideBar = pSideBar;
		this.fontCache = fontCache;
		fRefreshNecessary = true;
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
	}

	public void initLayout() {
		size = dimensionProvider.dimension(Component.PLAYER_DETAIL);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();

		String swapSetting = fSideBar.getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
		boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

		if (styleProvider.getFrameBackground() == null) {
			BufferedImage background;

			boolean homeSide = getSideBar().isHomeSide();
			if (swapColors) {
				homeSide = !homeSide;
			}
			if (homeSide) {
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_DETAIL_RED, dimensionProvider);
			} else {
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_DETAIL_BLUE, dimensionProvider);
			}
			g2d.drawImage(background, 0, 0, size.width, size.height, null);
		} else {
			g2d.setColor(styleProvider.getFrameBackground());
			g2d.fillRect(0, 0, size.width, size.height);
		}
		if (fPlayer != null) {
			BufferedImage overlay;
			Game game = getSideBar().getClient().getGame();
			boolean homePlayer = (fPlayer.getTeam() == null) || game.getTeamHome().hasPlayer(fPlayer);
			if (swapColors) {
				homePlayer = !homePlayer;
			}
			if (homePlayer) {
				overlay = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_DETAIL_RED, dimensionProvider);
			} else {
				overlay = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_DETAIL_BLUE, dimensionProvider);
			}
			g2d.drawImage(overlay, 0, 0, size.width, size.height, null);
		}
		g2d.dispose();
	}

	private void drawPlayerName() {
		if (fPlayer != null) {
			int x = 3, y = 1;
			Graphics2D g2d = fImage.createGraphics();
			g2d.setFont(nameFont);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			final AttributedString attStr = new AttributedString(getPlayer().getName());
			attStr.addAttribute(TextAttribute.FONT, g2d.getFont());
			final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(),
				new FontRenderContext(null, false, true));
			TextLayout layoutLine1 = measurer.nextLayout(size.width - (2 * x));
			if (layoutLine1 != null) {
				int yLine1 = y + fontMetrics.getHeight() - fontMetrics.getDescent();
				int yLine2 = yLine1 + fontMetrics.getHeight() - 1;
				TextLayout layoutLine2 = measurer.nextLayout(size.width - (2 * x));
				if (layoutLine2 != null) {
					drawShadowedLayout(g2d, layoutLine1, x, yLine1);
					drawShadowedLayout(g2d, layoutLine2, x, yLine2);
				} else {
					drawShadowedLayout(g2d, layoutLine1, x, yLine2);
				}
			}
		}
	}

	private void drawPlayerPortraitAndPosition() {
		if (fPlayer != null) {
			Dimension offset = dimensionProvider.dimension(Component.PLAYER_PORTRAIT_OFFSET);
			int x = offset.width, y = offset.height;
			Graphics2D g2d = fImage.createGraphics();
			String portraitUrl = PlayerIconFactory.getPortraitUrl(getPlayer());
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			StringBuilder positionName = new StringBuilder();
			if (getPlayer() != null) {
				if (getPlayer().getPlayerType() == PlayerType.STAR) {
					positionName.append("Star Player");
				} else if (StringTool.isProvided(getPlayer().getPosition().getDisplayName())) {
					positionName.append(getPlayer().getPosition().getDisplayName());
				} else {
					positionName.append(getPlayer().getPosition().getName());
				}
				positionName.append(" #").append(getPlayer().getNr());
			}
			String positionNameString = positionName.toString();
			g2d.setFont(positionFont);
			FontMetrics metrics = g2d.getFontMetrics();
			BufferedImage playerPortrait = iconCache.getIconByUrl(portraitUrl, dimensionProvider);
			BufferedImage portraitBackground = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_PORTRAIT, dimensionProvider);
			if (playerPortrait != null) {
				drawPortrait(x, y, g2d, playerPortrait);
			} else {
				drawPortrait(x - 1, y + 1, g2d, portraitBackground);
			}
			Dimension portraitDimension = dimensionProvider.dimension(Component.PLAYER_PORTRAIT);
			g2d.rotate(-Math.PI / 2.0);
			g2d.setColor(Color.BLACK);
			g2d.drawString(positionNameString, -(y + portraitDimension.height - 4), portraitDimension.width + metrics.getAscent() + x);
			g2d.setColor(Color.WHITE);
			g2d.drawString(positionNameString, -(y + portraitDimension.height - 5), portraitDimension.width + metrics.getAscent() + x - 1);
			g2d.dispose();
		}
	}

	private void drawPortrait(int x, int y, Graphics2D g2d, BufferedImage playerPortrait) {
		Dimension portraitDimension = dimensionProvider.dimension(Component.PLAYER_PORTRAIT);
		int canvasWidth = portraitDimension.width;
		int canvasHeight = portraitDimension.height;
		int portraitWidth = playerPortrait.getWidth();
		int portraitHeight = playerPortrait.getHeight();

		if (portraitWidth != canvasWidth || portraitHeight != canvasHeight) {
			// Scale portrait to fit both width and height

			float scale = Math.max(
				(float) portraitWidth / (float) canvasWidth,
				(float) portraitHeight / (float) canvasHeight
			);

			portraitWidth = (int) Math.floor(portraitWidth / scale);
			portraitHeight = (int) Math.floor(portraitHeight / scale);
		}

		int originX = (canvasWidth - portraitWidth) / 2;
		int originY = (canvasHeight - portraitHeight) / 2;

		g2d.drawImage(playerPortrait, x + originX, y + originY, portraitWidth, portraitHeight, null);
	}

	private void drawPlayerStats() {

		if (fPlayer != null) {

			Dimension offset = dimensionProvider.dimension(Component.PLAYER_STAT_OFFSET);
			int x = offset.width, y = offset.height;
			Graphics2D g2d = fImage.createGraphics();
			Game game = getSideBar().getClient().getGame();
			StatsMechanic mechanic = (StatsMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC)
				.forName(Mechanic.Type.STAT.name());

			boolean moveIsRed = false;
			int movement = getPlayer().getMovementWithModifiers(game);
			int moveLeft = movement;
			int strength = getPlayer().getStrengthWithModifiers(game);
			int agility = getPlayer().getAgilityWithModifiers(game);
			int armour = getPlayer().getArmourWithModifiers(game);
			ActingPlayer actingPlayer = getSideBar().getClient().getGame().getActingPlayer();
			if (fPlayer == actingPlayer.getPlayer()) {
				moveLeft -= actingPlayer.getCurrentMove();
				if (actingPlayer.isGoingForIt() && (moveLeft <= 0)) {
					moveIsRed = true;
					moveLeft = 2 + moveLeft;
					if (getPlayer().hasSkillProperty(NamedProperties.canMakeAnExtraGfi)) {
						moveLeft++;
					}

					if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canMakeAnExtraGfiOnce)) {
						moveLeft++;
					}
				}
			}

			int statBoxWidth = dimensionProvider.dimension(Component.PLAYER_STAT_BOX).width;
			int[] statSpacings;
			if (dimensionProvider.isPitchPortrait()) {
				statSpacings = new int[]{1, 1, 3, 4};
			} else {
				statSpacings = new int[]{0, 0, 1, 1};
			}

			Player<?> player = getPlayer();
			Position position = player.getPosition();
			int movementModifier = movement - position.getMovement();
			drawStatBox(g2d, x, y, moveLeft, moveIsRed, StatsDrawingModifier.positiveImproves(movementModifier));

			int strengthModifier = strength - position.getStrength();
			drawStatBox(g2d, x + dimensionProvider.scale(statSpacings[0]) + statBoxWidth, y, strength, false, StatsDrawingModifier.positiveImproves(strengthModifier));

			int agilityModifier = agility - position.getAgility();
			drawStatBox(g2d, x + dimensionProvider.scale(statSpacings[1]) + (statBoxWidth * 2), y, agility, false, mechanic.agilityModifier(agilityModifier), mechanic.statSuffix());

			if (mechanic.drawPassing()) {
				int passing = getPlayer().getPassingWithModifiers(game);
				int passingModifier = passing - position.getPassing();
				drawStatBox(g2d, x + dimensionProvider.scale(statSpacings[2]) + (statBoxWidth * 3), y, passing, false, StatsDrawingModifier.positiveImpairs(passingModifier), mechanic.statSuffix());
			}

			int armourModifier = armour - position.getArmour();
			drawStatBox(g2d, x + dimensionProvider.scale(statSpacings[3]) + (statBoxWidth * 4), y, armour, false, StatsDrawingModifier.positiveImproves(armourModifier), mechanic.statSuffix());

			g2d.dispose();

		}

	}

	private int findNigglings() {
		int decreases = 0;
		if (getPlayer() != null) {
			for (SeriousInjury injury : getPlayer().getLastingInjuries()) {
				if (InjuryAttribute.NI == injury.getInjuryAttribute()) {
					decreases++;
				}
			}
			Game game = getSideBar().getClient().getGame();
			PlayerResult playerResult = game.getGameResult().getPlayerResult(getPlayer());
			if ((playerResult != null) && ((playerResult.getSeriousInjury() != null)
				&& (InjuryAttribute.NI == playerResult.getSeriousInjury().getInjuryAttribute())
				|| ((playerResult.getSeriousInjuryDecay() != null)
				&& (InjuryAttribute.NI == playerResult.getSeriousInjuryDecay().getInjuryAttribute())))) {
				decreases++;
			}
		}
		return decreases;
	}

	private void drawNigglingInjuries() {
		int nigglingInjuries = findNigglings();
		if (nigglingInjuries > 0) {
			Graphics2D g2d = fImage.createGraphics();
			int x = 9;
			int y = 36;
			for (int i = 0; i < nigglingInjuries; i++) {
				g2d.setColor(Color.BLACK);
				g2d.fillOval(x + (i * 12) + 1, y + 1, 10, 10);
				g2d.setColor(Color.WHITE);
				g2d.fillOval(x + (i * 12), y, 10, 10);
				g2d.setColor(Color.RED);
				g2d.fillOval(x + (i * 12) + 1, y + 1, 8, 8);
			}
			g2d.dispose();
		}
	}

	private void drawCardOnPlayer() {
		Game game = getSideBar().getClient().getGame();
		if (ArrayTool.isProvided(game.getFieldModel().getCards(getPlayer()))) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			BufferedImage overlayCard = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD, dimensionProvider);
			g2d.drawImage(overlayCard, 76, 36, null);
			g2d.dispose();
		}
	}

	private void drawPlayerSpps() {
		if (fPlayer != null) {
			Dimension offset = dimensionProvider.dimension(Component.PLAYER_SPP_OFFSET);
			int x = offset.width, y = offset.height;
			Graphics2D g2d = fImage.createGraphics();
			g2d.setFont(sppFont);
			FontMetrics metrics = g2d.getFontMetrics();
			Game game = getSideBar().getClient().getGame();
			GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
			PlayerResult playerResult = game.getGameResult().getPlayerResult(getPlayer());
			StringBuilder sppInfo = new StringBuilder();
			if ((playerResult != null) && (getPlayer() != null)) {
				if (getPlayer().getPlayerType() == PlayerType.STAR) {
					Position position = getPlayer().getPosition();
					sppInfo.append(StringTool.formatThousands(position.getCost())).append(" gold");
				} else {
					int oldSpps = playerResult.getCurrentSpps();
					int newSpps = playerResult.totalEarnedSpps();
					sppInfo.append(oldSpps);
					if (newSpps > 0) {
						sppInfo.append("+").append(newSpps);
					}
					sppInfo.append(" ").append(mechanic.calculatePlayerLevel(game, getPlayer()));
				}
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString(sppInfo.toString(), x, y + metrics.getAscent());
			g2d.dispose();
		}
	}

	private void drawPlayerSkills() {
		if (fPlayer != null) {
			Dimension offset = dimensionProvider.dimension(Component.PLAYER_SKILL_OFFSET);
			int x = offset.width, y = offset.height;
			Graphics2D g2d = fImage.createGraphics();
			Game game = getSideBar().getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			PlayerState playerState = game.getFieldModel().getPlayerState(getPlayer());
			Set<String> modifications = new LinkedHashSet<>();
			List<String> acquiredSkills = new ArrayList<>();
			List<String> rosterSkills = new ArrayList<>();
			Set<String> usedSkills = new HashSet<>();
			for (SkillDisplayInfo skillInfo : getPlayer().skillInfos()) {
				if ((SkillCategory.STAT_INCREASE != skillInfo.getSkill().getCategory())
					&& (SkillCategory.STAT_DECREASE != skillInfo.getSkill().getCategory())) {
					switch (skillInfo.getCategory()) {
						case PLAYER:
							acquiredSkills.add(skillInfo.getInfo());
							break;
						case ROSTER:
							rosterSkills.add(skillInfo.getInfo());
							break;
						case TEMPORARY:
							modifications.add(skillInfo.getInfo());
							break;
					}
				}
				Skill unusedProSkill = getPlayer().getSkillWithProperty(NamedProperties.canRerollOncePerTurn);
				if (((getPlayer() == actingPlayer.getPlayer()) && actingPlayer.isSkillUsed(skillInfo.getSkill()))
					|| ((skillInfo.getSkill() == unusedProSkill) && playerState.hasUsedPro()) || getPlayer().isUsed(skillInfo.getSkill())) {
					usedSkills.add(skillInfo.getInfo());
				}
			}
			for (Card card : game.getFieldModel().getCards(getPlayer())) {
				modifications.add(card.getShortName());
			}
			for (CardEffect cardEffect : game.getFieldModel().getCardEffects(getPlayer())) {
				modifications.add(cardEffect.getName());
			}
			modifications.addAll(getPlayer().getEnhancementSources().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));

			acquiredSkills.removeAll(modifications);
			rosterSkills.removeAll(modifications);

			int height = 0;
			if (modifications.size() > 0) {
				g2d.setColor(new Color(220, 0, 0));
				height += drawPlayerSkills(g2d, x, y + height, new ArrayList<>(modifications), usedSkills) + 2;
			}

			if (acquiredSkills.size() > 0) {
				g2d.setColor(new Color(0, 96, 0));
				height += drawPlayerSkills(g2d, x, y + height, acquiredSkills, usedSkills) + 2;
			}
			g2d.setColor(Color.BLACK);
			drawPlayerSkills(g2d, x, y + height, rosterSkills, usedSkills);
			g2d.dispose();
		}
	}

	private int drawPlayerSkills(Graphics2D pG2d, int pX, int pY, List<String> pSkills, Set<String> pUsedSkills) {
		int height = 0;
		if ((pSkills != null) && (pSkills.size() > 0)) {
			int yPos = pY;
			for (String skill : pSkills) {
				if (pUsedSkills.contains(skill)) {
					pG2d.setFont(skillUsedFont);
				} else {
					pG2d.setFont(skillFont);
				}
				FontMetrics metrics = pG2d.getFontMetrics();
				for (String part : splitSkill(skill)) {
					height += metrics.getHeight();
					if (yPos > pY) {
						yPos += metrics.getHeight();
					} else {
						yPos += metrics.getAscent();
					}
					pG2d.drawString(part, pX, yPos);
				}
			}
		}
		return height;
	}

	private List<String> splitSkill(String skill) {
		List<String> parts = new ArrayList<>();
		boolean isShort = StringTool.isProvided(skill) && skill.length() <= LINE_LENGTH;
		if (isShort) {
			parts.add(skill);
			return parts;
		}

		String[] words = skill.split(" ");

		StringBuilder line = new StringBuilder(LINE_LENGTH);

		for (String word : words) {
			if (line.length() + word.length() > LINE_LENGTH && line.toString().trim().length() > 0) {
				addPart(parts, line);
				line = new StringBuilder(LINE_LENGTH);
			}
			line.append(word).append(" ");

		}

		if (line.toString().trim().length() > 0) {
			addPart(parts, line);
		}

		return parts;
	}

	private void addPart(List<String> parts, StringBuilder line) {
		parts.add((parts.isEmpty() ? "" : "  ") + line.toString().trim());
	}

	private void drawStatBox(Graphics2D pG2d, int pX, int pY, int pValue, boolean pStatIsRed, StatsDrawingModifier modifier) {
		drawStatBox(pG2d, pX, pY, pValue, pStatIsRed, modifier, "");
	}

	private void drawStatBox(Graphics2D pG2d, int pX, int pY, int pValue, boolean pStatIsRed, StatsDrawingModifier modifier, String suffix) {
		if (fPlayer != null) {
			Dimension statBox = dimensionProvider.dimension(Component.PLAYER_STAT_BOX);
			int innerHeight = dimensionProvider.dimension(Component.PLAYER_STAT_BOX_MISC).height;

			pG2d.setColor(Color.BLACK);
			pG2d.setFont(statFont);
			if (modifier.isImprovement()) {
				pG2d.setColor(Color.GREEN);
				if (modifier.getAbsoluteModifier() > 1) {
					pG2d.fillRect(pX + 2, pY + statBox.height - innerHeight - 2, statBox.width - 6,
						innerHeight);
				} else {
					pG2d.fillPolygon(new int[]{pX + 2, pX + 2, pX + statBox.width - 3},
						new int[]{pY + statBox.height - innerHeight - 2, pY + statBox.height - 2,
							pY + statBox.height - 2},
						3);
				}
			}
			if (modifier.isImpairment()) {
				pG2d.setColor(Color.RED);
				if (modifier.getAbsoluteModifier() > 1) {
					pG2d.fillRect(pX + 2, pY + statBox.height - innerHeight - 2, statBox.width - 6,
						innerHeight);
				} else {
					pG2d.fillPolygon(new int[]{pX + 2, pX + statBox.width - 3, pX + statBox.width - 3},
						new int[]{pY + statBox.height - innerHeight - 2,
							pY + statBox.height - innerHeight - 2, pY + statBox.height - 2},
						3);
				}
			}

			Color statColor = pStatIsRed ? (modifier.isImpairment() ? Color.cyan : Color.RED) : Color.BLACK;
			String statText = pValue == 0 ? "-" : pValue + suffix;
			if (pValue == 0) {
				// Move the dash more central
				pY -= 1;
			}
			drawCenteredText(pG2d, pX + (statBox.width / 2), pY + statBox.height - 4, statColor,
				statText);
		}
	}

	private void drawCenteredText(Graphics2D pG2d, int pX, int pY, Color pColor, String pText) {
		FontMetrics metrics = pG2d.getFontMetrics();
		Rectangle2D numberBounds = metrics.getStringBounds(pText, pG2d);
		int x = (int) (pX - (numberBounds.getWidth() / 2));
		pG2d.setColor(pColor);
		pG2d.drawString(pText, x, pY);
	}

	private void drawShadowedLayout(Graphics2D pG2d, TextLayout pTextLayout, int pX, int pY) {
		pG2d.setColor(Color.BLACK);
		pTextLayout.draw(pG2d, pX + 1, pY + 1);
		pG2d.setColor(Color.WHITE);
		pTextLayout.draw(pG2d, pX, pY);
	}

	public void refresh() {
		nameFont = fontCache.font(Font.PLAIN, 12, dimensionProvider);
		statFont = fontCache.font(Font.BOLD, 13, dimensionProvider);
		positionFont = fontCache.font(Font.PLAIN, 11, dimensionProvider);
		sppFont = fontCache.font(Font.BOLD, 11, dimensionProvider);
		skillFont = fontCache.font(Font.BOLD, 11, dimensionProvider);
		skillUsedFont = fontCache.font(Font.ITALIC + Font.BOLD, 11, dimensionProvider);

		ClientData clientData = getSideBar().getClient().getClientData();
		int displayMode = findDisplayMode();
		if (!fRefreshNecessary) {
			fRefreshNecessary = (getDisplayedPlayer(displayMode) != getPlayer());
			if ((displayMode == _DISPLAY_ACTING_PLAYER) && clientData.isActingPlayerUpdated()) {
				fRefreshNecessary = true;
				clientData.setActingPlayerUpdated(false);
			}
		}
		if (fRefreshNecessary) {
			fPlayer = getDisplayedPlayer(displayMode);
			drawBackground();
			if (fPlayer != null) {
				drawPlayerName();
				drawPlayerPortraitAndPosition();
				drawPlayerStats();
				drawNigglingInjuries();
				drawCardOnPlayer();
				drawPlayerSpps();
				drawPlayerSkills();
			}
			repaint();
		}
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	public Player<?> getPlayer() {
		return fPlayer;
	}

	public void setPlayer(Player<?> pPlayer) {
		fPlayer = pPlayer;
	}

	public SideBarComponent getSideBar() {
		return fSideBar;
	}

	private int findDisplayMode() {
		int displayMode = _DISPLAY_NONE;
		if (!getSideBar().isBoxOpen()) {
			Game game = getSideBar().getClient().getGame();
			ClientData clientData = getSideBar().getClient().getClientData();
			UserInterface userInterface = getSideBar().getClient().getUserInterface();
			SideBarComponent otherSideBar = getSideBar().isHomeSide() ? userInterface.getSideBarAway()
				: userInterface.getSideBarHome();
			if (otherSideBar.isBoxOpen()) {
				displayMode = _DISPLAY_SELECTED_PLAYER;
				if ((clientData.getSelectedPlayer() == null) && (game.getActingPlayer().getPlayer() != null)) {
					displayMode = _DISPLAY_ACTING_PLAYER;
				}
			} else {
				if (getSideBar().isHomeSide() == game.isHomePlaying()) {
					displayMode = _DISPLAY_ACTING_PLAYER;
				} else {
					displayMode = _DISPLAY_SELECTED_PLAYER;
					if ((clientData.getSelectedPlayer() == null) && (game.getDefender() != null)) {
						displayMode = _DISPLAY_DEFENDING_PLAYER;
					}
				}
			}
		}
		return displayMode;
	}

	private Player<?> getDisplayedPlayer(int pDisplayMode) {
		Player<?> displayedPlayer = null;
		Game game = getSideBar().getClient().getGame();
		ClientData clientData = getSideBar().getClient().getClientData();
		switch (pDisplayMode) {
			case _DISPLAY_ACTING_PLAYER:
				displayedPlayer = game.getActingPlayer().getPlayer();
				break;
			case _DISPLAY_DEFENDING_PLAYER:
				displayedPlayer = game.getDefender();
				break;
			case _DISPLAY_SELECTED_PLAYER:
				displayedPlayer = clientData.getSelectedPlayer();
				break;
			default:
				break;
		}
		return displayedPlayer;
	}

}
