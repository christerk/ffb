package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.StatsDrawingModifier;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillDisplayInfo;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kalimar
 */
public class PlayerDetailComponent extends JPanel {

	public static final int WIDTH = 145;
	public static final int HEIGHT = 430;

	private static final int _PORTRAIT_WIDTH = 121;
	private static final int _PORTRAIT_HEIGHT = 147;

	private static final int _STAT_BOX_WIDTH = 28;
	private static final int _STAT_BOX_HEIGHT = 29;
	private static final int _STAT_BOX_INNER_HEIGHT = 14;

	private static final Font _NAME_FONT = new Font("Sans Serif", Font.PLAIN, 12);
	private static final Font _STAT_FONT = new Font("Sans Serif", Font.BOLD, 13);
	private static final Font _POSITION_FONT = new Font("Sans Serif", Font.PLAIN, 11);
	private static final Font _SPP_FONT = new Font("Sans Serif", Font.BOLD, 11);
	private static final Font _SKILL_FONT = new Font("Sans Serif", Font.BOLD, 11);
	private static final Font _SKILL_USED_FONT = new Font("Sans Serif", Font.ITALIC + Font.BOLD, 11);

	private static final int _DISPLAY_NONE = 0;
	private static final int _DISPLAY_ACTING_PLAYER = 1;
	private static final int _DISPLAY_DEFENDING_PLAYER = 2;
	private static final int _DISPLAY_SELECTED_PLAYER = 3;

	private SideBarComponent fSideBar;
	private Player<?> fPlayer;
	private BufferedImage fImage;
	private boolean fRefreshNecessary;

	public PlayerDetailComponent(SideBarComponent pSideBar) {
		fSideBar = pSideBar;
		fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		setLayout(null);
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		fRefreshNecessary = true;
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
		BufferedImage background;
		if (getSideBar().isHomeSide()) {
			background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_DETAIL_RED);
		} else {
			background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_DETAIL_BLUE);
		}
		g2d.drawImage(background, 0, 0, null);
		if (fPlayer != null) {
			BufferedImage overlay;
			Game game = getSideBar().getClient().getGame();
			if ((fPlayer.getTeam() == null) || game.getTeamHome().hasPlayer(fPlayer)) {
				overlay = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_DETAIL_RED);
			} else {
				overlay = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_DETAIL_BLUE);
			}
			g2d.drawImage(overlay, 0, 0, null);
		}
		g2d.dispose();
	}

	private void drawPlayerName() {
		if (fPlayer != null) {
			int x = 3, y = 1;
			Graphics2D g2d = fImage.createGraphics();
			g2d.setFont(_NAME_FONT);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			final AttributedString attStr = new AttributedString(getPlayer().getName());
			attStr.addAttribute(TextAttribute.FONT, g2d.getFont());
			final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(),
				new FontRenderContext(null, false, true));
			TextLayout layoutLine1 = measurer.nextLayout(WIDTH - (2 * x));
			if (layoutLine1 != null) {
				int yLine1 = y + fontMetrics.getHeight() - fontMetrics.getDescent();
				int yLine2 = yLine1 + fontMetrics.getHeight() - 1;
				TextLayout layoutLine2 = measurer.nextLayout(WIDTH - (2 * x));
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
			int x = 3, y = 32;
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
			g2d.setFont(_POSITION_FONT);
			FontMetrics metrics = g2d.getFontMetrics();
			BufferedImage playerPortrait = iconCache.getIconByUrl(portraitUrl);
			BufferedImage portraitBackground = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_PLAYER_PORTRAIT);
			if (playerPortrait != null) {
				drawPortrait(x, y, g2d, playerPortrait);
			} else {
				drawPortrait(x - 1, y + 1, g2d, portraitBackground);
			}
			g2d.rotate(-Math.PI / 2.0);
			g2d.setColor(Color.BLACK);
			g2d.drawString(positionNameString, -(y + _PORTRAIT_HEIGHT - 4), _PORTRAIT_WIDTH + metrics.getAscent() + 3);
			g2d.setColor(Color.WHITE);
			g2d.drawString(positionNameString, -(y + _PORTRAIT_HEIGHT - 5), _PORTRAIT_WIDTH + metrics.getAscent() + 2);
			g2d.dispose();
		}
	}

	private void drawPortrait(int x, int y, Graphics2D g2d, BufferedImage playerPortrait) {
		int canvasWidth = _PORTRAIT_WIDTH;
		int canvasHeight = _PORTRAIT_HEIGHT;
		int portraitWidth = playerPortrait.getWidth();
		int portraitHeight = playerPortrait.getHeight();

		if (portraitWidth > canvasWidth || portraitHeight > canvasHeight) {
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

			int x = 3, y = 179;
			Graphics2D g2d = fImage.createGraphics();
			Game game = getSideBar().getClient().getGame();
			PlayerResult playerResult = game.getGameResult().getPlayerResult(getPlayer());
			boolean moveIsRed = false;
			int movement = getPlayer().getMovementWithModifiers()
				- findNewStatDecreases(playerResult, InjuryAttribute.MA);
			int moveLeft = movement;
			int strength = getPlayer().getStrengthWithModifiers()
				- findNewStatDecreases(playerResult, InjuryAttribute.ST);
			int agility = getPlayer().getAgilityWithModifiers() - findNewStatDecreases(playerResult, InjuryAttribute.AG);
			int armour = getPlayer().getArmourWithModifiers() - findNewStatDecreases(playerResult, InjuryAttribute.AV);
			ActingPlayer actingPlayer = getSideBar().getClient().getGame().getActingPlayer();
			if (fPlayer == actingPlayer.getPlayer()) {
				moveLeft -= actingPlayer.getCurrentMove();
				if (actingPlayer.isGoingForIt() && (moveLeft <= 0)) {
					moveIsRed = true;
					if (getPlayer().hasSkillProperty(NamedProperties.canMakeAnExtraGfi)) {
						moveLeft = 3 + moveLeft;
					} else {
						moveLeft = 2 + moveLeft;
					}
				}
			}

			StatsMechanic mechanic = (StatsMechanic) game.getRules().getFactory(FactoryType.Factory.MECHANIC)
				.forName(Mechanic.Type.STAT.name());
			Player<?> player = getPlayer();
			Position position = player.getPosition();
			int movementModifier = movement - position.getMovement();
			drawStatBox(g2d, x, y, moveLeft, moveIsRed, StatsDrawingModifier.positiveImproves(movementModifier));

			int strengthModifier = strength - position.getStrength();
			drawStatBox(g2d, x + _STAT_BOX_WIDTH, y, strength, false, StatsDrawingModifier.positiveImproves(strengthModifier));

			int agilityModifier = agility - position.getAgility();
			drawStatBox(g2d, x + (_STAT_BOX_WIDTH * 2), y, agility, false, mechanic.agilityModifier(agilityModifier), mechanic.statSuffix());

			if (mechanic.drawPassing()) {
				int passing = getPlayer().getPassingWithModifiers();
				if (passing > 0) {
					passing += findNewStatDecreases(playerResult, InjuryAttribute.PA);
				}
				int passingModifier = passing - position.getPassing();
				drawStatBox(g2d, x + (_STAT_BOX_WIDTH * 3), y, passing, false, StatsDrawingModifier.positiveImpairs(passingModifier), mechanic.statSuffix());
			}

			int armourModifier = armour - position.getArmour();
			drawStatBox(g2d, x + (_STAT_BOX_WIDTH * 4), y, armour, false, StatsDrawingModifier.positiveImproves(armourModifier), mechanic.statSuffix());

			g2d.dispose();

		}

	}

	private int findNewStatDecreases(PlayerResult pPlayerResult, InjuryAttribute pInjuryAttribute) {
		int decreases = 0;
		if (pPlayerResult != null) {
			if ((pPlayerResult.getSeriousInjury() != null)
				&& (pPlayerResult.getSeriousInjury().getInjuryAttribute() == pInjuryAttribute)) {
				decreases++;
			}
			if ((pPlayerResult.getSeriousInjuryDecay() != null)
				&& (pPlayerResult.getSeriousInjuryDecay().getInjuryAttribute() == pInjuryAttribute)) {
				decreases++;
			}
		}
		return decreases;
	}

	private int findStatDecreases(InjuryAttribute pInjuryAttribute) {
		int decreases = 0;
		if ((getPlayer() != null) && (pInjuryAttribute != null)) {
			for (SeriousInjury injury : getPlayer().getLastingInjuries()) {
				if (pInjuryAttribute == injury.getInjuryAttribute()) {
					decreases++;
				}
			}
			Game game = getSideBar().getClient().getGame();
			PlayerResult playerResult = game.getGameResult().getPlayerResult(getPlayer());
			if ((playerResult != null) && ((playerResult.getSeriousInjury() != null)
				&& (pInjuryAttribute == playerResult.getSeriousInjury().getInjuryAttribute())
				|| ((playerResult.getSeriousInjuryDecay() != null)
				&& (pInjuryAttribute == playerResult.getSeriousInjuryDecay().getInjuryAttribute())))) {
				decreases++;
			}
		}
		if (pInjuryAttribute != InjuryAttribute.NI) {
			return Math.min(2, decreases);
		} else {
			return decreases;
		}
	}

	private void drawNigglingInjuries() {
		int nigglingInjuries = findStatDecreases(InjuryAttribute.NI);
		if (nigglingInjuries > 0) {
			Graphics2D g2d = fImage.createGraphics();
			int x = 9;
			int y = 36;
			for (int i = 0; i < nigglingInjuries; i++) {
				g2d.setColor(Color.BLACK);
				g2d.fillOval(x + (i * 12) + 1, y + 1, 10, 10);
				g2d.setColor(Color.RED);
				g2d.fillOval(x + (i * 12), y, 10, 10);
			}
			g2d.dispose();
		}
	}

	private void drawCardOnPlayer() {
		Game game = getSideBar().getClient().getGame();
		if (ArrayTool.isProvided(game.getFieldModel().getCards(getPlayer()))) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			BufferedImage overlayCard = iconCache.getIconByProperty(IIconProperty.SIDEBAR_OVERLAY_PLAYER_CARD);
			g2d.drawImage(overlayCard, 76, 36, null);
			g2d.dispose();
		}
	}

	private void drawPlayerSpps() {
		if (fPlayer != null) {
			int x = 8, y = 222;
			Graphics2D g2d = fImage.createGraphics();
			g2d.setFont(_SPP_FONT);
			FontMetrics metrics = g2d.getFontMetrics();
			Game game = getSideBar().getClient().getGame();
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
					if (oldSpps > 175) {
						sppInfo.append(" Legend");
					} else if (oldSpps > 75) {
						sppInfo.append(" Super Star");
					} else if (oldSpps > 50) {
						sppInfo.append(" Star");
					} else if (oldSpps > 30) {
						sppInfo.append(" Emerging");
					} else if (oldSpps > 15) {
						sppInfo.append(" Veteran");
					} else if (oldSpps > 5) {
						sppInfo.append(" Experienced");
					} else {
						sppInfo.append(" Rookie");
					}
				}
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString(sppInfo.toString(), x, y + metrics.getAscent());
			g2d.dispose();
		}
	}

	private void drawPlayerSkills() {
		if (fPlayer != null) {
			int x = 8, y = 246;
			Graphics2D g2d = fImage.createGraphics();
			Game game = getSideBar().getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			PlayerState playerState = game.getFieldModel().getPlayerState(getPlayer());
			List<String> cardSkills = new ArrayList<>();
			List<String> acquiredSkills = new ArrayList<>();
			List<String> rosterSkills = new ArrayList<>();
			List<String> cardEffects = new ArrayList<>();
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
							cardSkills.add(skillInfo.getInfo());
							break;
					}
				}
				Skill unusedProSkill = getPlayer().getSkillWithProperty(NamedProperties.canRerollOncePerTurn);
				if (((getPlayer() == actingPlayer.getPlayer()) && actingPlayer.isSkillUsed(skillInfo.getSkill()))
					|| ((skillInfo.getSkill() == unusedProSkill) && playerState.hasUsedPro())) {
					usedSkills.add(skillInfo.getInfo());
				}
			}
			for (Card card : game.getFieldModel().getCards(getPlayer())) {
				cardSkills.add(card.getShortName());
			}
			for (CardEffect cardEffect : game.getFieldModel().getCardEffects(getPlayer())) {
				cardEffects.add(cardEffect.getName());
			}
			int height = 0;
			if (cardEffects.size() > 0) {
				g2d.setColor(new Color(220, 0, 0));
				height += drawPlayerSkills(g2d, x, y + height, cardEffects, usedSkills) + 2;
			}
			if (cardSkills.size() > 0) {
				g2d.setColor(new Color(220, 0, 0));
				height += drawPlayerSkills(g2d, x, y + height, cardSkills, usedSkills) + 2;
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
					pG2d.setFont(_SKILL_USED_FONT);
				} else {
					pG2d.setFont(_SKILL_FONT);
				}
				FontMetrics metrics = pG2d.getFontMetrics();
				for (String part: splitSkill(skill)) {
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
		boolean isLong = StringTool.isProvided(skill) && skill.length() > 20;
		if (!isLong) {
			parts.add(skill);
			return parts;
		}

		int index = skill.indexOf("(");
		if (index < 0) {
			parts.add(skill);
			return parts;
		}

		parts.add(skill.substring(0, index));
		parts.add("  " + skill.substring(index));
		return parts;
	}

	private void drawStatBox(Graphics2D pG2d, int pX, int pY, int pValue, boolean pStatIsRed, StatsDrawingModifier modifier) {
		drawStatBox(pG2d, pX, pY, pValue, pStatIsRed, modifier, "");
	}

	private void drawStatBox(Graphics2D pG2d, int pX, int pY, int pValue, boolean pStatIsRed, StatsDrawingModifier modifier, String suffix) {
		if (fPlayer != null) {
			pG2d.setColor(Color.BLACK);
			pG2d.setFont(_STAT_FONT);
			if (modifier.isImprovement()) {
				pG2d.setColor(Color.GREEN);
				if (modifier.getAbsoluteModifier() > 1) {
					pG2d.fillRect(pX + 2, pY + _STAT_BOX_HEIGHT - _STAT_BOX_INNER_HEIGHT - 2, _STAT_BOX_WIDTH - 6,
						_STAT_BOX_INNER_HEIGHT);
				} else {
					pG2d.fillPolygon(new int[]{pX + 2, pX + 2, pX + _STAT_BOX_WIDTH - 3},
						new int[]{pY + _STAT_BOX_HEIGHT - _STAT_BOX_INNER_HEIGHT - 2, pY + _STAT_BOX_HEIGHT - 2,
							pY + _STAT_BOX_HEIGHT - 2},
						3);
				}
			}
			if (modifier.isImpairment()) {
				pG2d.setColor(Color.RED);
				if (modifier.getAbsoluteModifier() > 1) {
					pG2d.fillRect(pX + 2, pY + _STAT_BOX_HEIGHT - _STAT_BOX_INNER_HEIGHT - 2, _STAT_BOX_WIDTH - 6,
						_STAT_BOX_INNER_HEIGHT);
				} else {
					pG2d.fillPolygon(new int[]{pX + 2, pX + _STAT_BOX_WIDTH - 3, pX + _STAT_BOX_WIDTH - 3},
						new int[]{pY + _STAT_BOX_HEIGHT - _STAT_BOX_INNER_HEIGHT - 2,
							pY + _STAT_BOX_HEIGHT - _STAT_BOX_INNER_HEIGHT - 2, pY + _STAT_BOX_HEIGHT - 2},
						3);
				}
			}

			Color statColor = pStatIsRed ? (modifier.isImpairment() ? Color.cyan : Color.RED) : Color.BLACK;
			String statText = pValue == 0 ? "-" : pValue + suffix;
			if (pValue == 0) {
				// Move the dash more central
				pY -= 1;
			}
			drawCenteredText(pG2d, pX + (_STAT_BOX_WIDTH / 2), pY + _STAT_BOX_HEIGHT - 4, statColor,
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
		}
		return displayedPlayer;
	}

}