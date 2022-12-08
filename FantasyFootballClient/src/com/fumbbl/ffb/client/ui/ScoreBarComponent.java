package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.util.UtilClientGraphics;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class ScoreBarComponent extends JPanel implements MouseMotionListener {

	private static final String _TURN = "Turn";

	private static final Font _SCORE_FONT = new Font("Sans Serif", Font.BOLD, 24);
	private static final Font _TURN_NUMBER_FONT = new Font("Sans Serif", Font.BOLD, 22);
	private static final Font _TURN_TEXT_FONT = new Font("Sans Serif", Font.BOLD, 14);

	private static final Font _SPECTATOR_FONT = new Font("Sans Serif", Font.BOLD, 14);

	private static final int LINE_HEIGHT = 32;
	private final DimensionProvider dimensionProvider;
	private Rectangle weatherLocation;
	private Rectangle spectatorLocation;
	private Rectangle coachBannedHome;

	private final FantasyFootballClient fClient;
	private Rectangle coachBannedAway;

	private int fTurnHome;
	private int fTurnAway;
	private int fHalf;
	private int fScoreHome;
	private int fScoreAway;
	private int fSpectators;
	private Weather fWeather;
	private boolean fCoachBannedHome;
	private boolean fCoachBannedAway;
	private boolean fRefreshNecessary;
	private BufferedImage fImage;

	public ScoreBarComponent(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
		setLayout(null);
		ToolTipManager.sharedInstance().registerComponent(this);
		fRefreshNecessary = true;
		addMouseMotionListener(this);
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		IconCache iconCache = getClient().getUserInterface().getIconCache();
		String scorebarBackground = dimensionProvider.getLayout() ==
			DimensionProvider.ClientLayout.SQUARE ? IIconProperty.SCOREBAR_BACKGROUND_SQUARE : IIconProperty.SCOREBAR_BACKGROUND;
		BufferedImage background = iconCache.getIconByProperty(scorebarBackground);
		g2d.drawImage(background, 0, 0, null);
		g2d.dispose();
	}

	private void drawScore() {
		Graphics2D g2d = fImage.createGraphics();
		String scoreHome = Integer.toString(fScoreHome);
		String scoreAway = Integer.toString(fScoreAway);
		g2d.setFont(_SCORE_FONT);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D boundsHome = fontMetrics.getStringBounds(scoreHome, g2d);
		int x;
		x = ((getPreferredSize().width - (int) boundsHome.getWidth()) / 2) - 40;
		int y = ((LINE_HEIGHT + fontMetrics.getHeight()) / 2) - fontMetrics.getDescent() - 1;
		if (dimensionProvider.getLayout() == DimensionProvider.ClientLayout.SQUARE) {
			y += LINE_HEIGHT;
		}
		UtilClientGraphics.drawShadowedText(g2d, scoreHome, x, y);
		Rectangle2D boundsAway = fontMetrics.getStringBounds(scoreAway, g2d);
		x = ((getPreferredSize().width - (int) boundsAway.getWidth()) / 2) + 40;
		UtilClientGraphics.drawShadowedText(g2d, scoreAway, x, y);
		g2d.dispose();
	}

	private void drawTurn() {
		Graphics2D g2d = fImage.createGraphics();
		Game game = getClient().getGame();

		String turn = game.getTurnDataHome().getTurnNr() + " / " +
			game.getTurnDataAway().getTurnNr();

		g2d.setFont(_TURN_NUMBER_FONT);
		FontMetrics metricsInts = g2d.getFontMetrics();
		int yInts = ((LINE_HEIGHT + metricsInts.getHeight()) / 2) - metricsInts.getDescent() - 1;
		Rectangle2D turnBounds = metricsInts.getStringBounds(turn, g2d);

		g2d.setFont(_TURN_TEXT_FONT);
		FontMetrics metricsText = g2d.getFontMetrics();
		int yText = ((LINE_HEIGHT + metricsText.getHeight()) / 2) - metricsText.getDescent();
		Rectangle2D turnPrefixBounds = metricsText.getStringBounds(_TURN, g2d);

		String half;
		if (game.getHalf() > 2) {
			half = "of Overtime";
		} else if (game.getHalf() > 1) {
			half = "of 2nd half";
		} else {
			half = "of 1st half";
		}

		Rectangle2D halfBounds = metricsText.getStringBounds(half, g2d);

		int x;
		if (dimensionProvider.getLayout() == DimensionProvider.ClientLayout.SQUARE) {
			int length = (int) (turnPrefixBounds.getWidth() + turnBounds.getWidth() + halfBounds.getWidth() + 20);
			x = (getWidth() - length) / 2;
		} else {
			x = 4;
		}

		g2d.setFont(_TURN_TEXT_FONT);
		UtilClientGraphics.drawShadowedText(g2d, _TURN, x, yText);
		x += turnPrefixBounds.getWidth() + 10;
		g2d.setFont(_TURN_NUMBER_FONT);
		UtilClientGraphics.drawShadowedText(g2d, turn, x, yInts);
		x += turnBounds.getWidth() + 10;
		g2d.setFont(_TURN_TEXT_FONT);
		UtilClientGraphics.drawShadowedText(g2d, half, x, yText);
		g2d.dispose();
	}

	private void drawSpectators() {
		if (fSpectators > 0) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage spectatorsImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_SPECTATORS);
			g2d.drawImage(spectatorsImage, spectatorLocation.x, spectatorLocation.y, null);
			g2d.setFont(_SPECTATOR_FONT);
			String spectatorString = Integer.toString(fSpectators);
			UtilClientGraphics.drawShadowedText(g2d, spectatorString, spectatorLocation.x + 108, 21);
			g2d.dispose();
		}
	}

	private void drawBannedCoaches() {
		if (fCoachBannedHome || fCoachBannedAway) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			if (fCoachBannedHome) {
				BufferedImage coachBannedImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_COACH_BANNED_HOME);
				g2d.drawImage(coachBannedImage, coachBannedHome.x, coachBannedHome.y, null);
			}
			if (fCoachBannedAway) {
				BufferedImage coachBannedImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_COACH_BANNED_AWAY);
				g2d.drawImage(coachBannedImage, coachBannedAway.x, coachBannedAway.y, null);
			}
			g2d.dispose();
		}
	}

	private void drawWeather() {
		if (fWeather != null) {
			String weatherIconProperty = null;
			switch (fWeather) {
				case BLIZZARD:
					weatherIconProperty = IIconProperty.WEATHER_BLIZZARD;
					break;
				case INTRO:
					weatherIconProperty = IIconProperty.WEATHER_INTRO;
					break;
				case NICE:
					weatherIconProperty = IIconProperty.WEATHER_NICE;
					break;
				case POURING_RAIN:
					weatherIconProperty = IIconProperty.WEATHER_RAIN;
					break;
				case SWELTERING_HEAT:
					weatherIconProperty = IIconProperty.WEATHER_HEAT;
					break;
				case VERY_SUNNY:
					weatherIconProperty = IIconProperty.WEATHER_SUNNY;
					break;
			}
			if (StringTool.isProvided(weatherIconProperty)) {
				IconCache iconCache = getClient().getUserInterface().getIconCache();
				BufferedImage weatherIcon = iconCache.getIconByProperty(weatherIconProperty);
				Graphics2D g2d = fImage.createGraphics();
				g2d.drawImage(weatherIcon, weatherLocation.x, weatherLocation.y, null);
				g2d.dispose();
			}
		}
	}

	public void init() {
		fTurnHome = 0;
		fTurnAway = 0;
		fScoreHome = 0;
		fScoreAway = 0;
		fSpectators = 0;
		fWeather = null;
		fRefreshNecessary = true;
		refresh();
	}

	public void initLayout() {
		Dimension size = dimensionProvider.dimension(DimensionProvider.Component.SCORE_BOARD);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

		if (dimensionProvider.getLayout() == DimensionProvider.ClientLayout.SQUARE) {
			weatherLocation = new Rectangle(159, 64, 100, 32);
			spectatorLocation = new Rectangle(1, 64, 130, 32);
			coachBannedHome = new Rectangle(1, 0, 36, 32);
			coachBannedAway = new Rectangle(size.width - 37, 0, 36, 32);
		} else {
			weatherLocation = new Rectangle(size.width - 101, 0, 100, 32);
			spectatorLocation = new Rectangle((size.width / 2 + 160), 0, 130, 32);
			coachBannedHome = new Rectangle((size.width / 2 - 130 - 36), 0, 36, 32);
			coachBannedAway = new Rectangle((size.width / 2 + 130), 0, 36, 32);
		}
	}

	public void refresh() {
		Game game = getClient().getGame();
		if (game.getHalf() > 0) {
			ClientData clientData = getClient().getClientData();
			if (!fRefreshNecessary) {
				fRefreshNecessary = ((fTurnHome != game.getTurnDataHome().getTurnNr())
					|| (fTurnAway != game.getTurnDataAway().getTurnNr()) || (fHalf != game.getHalf()));
			}
			if (!fRefreshNecessary) {
				fRefreshNecessary = ((fScoreHome != game.getGameResult().getTeamResultHome().getScore())
					|| (fTurnAway != game.getGameResult().getTeamResultAway().getScore()));
			}
			if (!fRefreshNecessary) {
				fRefreshNecessary = (fSpectators != clientData.getSpectators());
			}
			if (!fRefreshNecessary) {
				fRefreshNecessary = (fWeather != game.getFieldModel().getWeather());
			}
			if (!fRefreshNecessary) {
				fRefreshNecessary = (fCoachBannedHome != game.getTurnDataHome().isCoachBanned())
					|| (fCoachBannedAway != game.getTurnDataAway().isCoachBanned());
			}
			if (fRefreshNecessary) {
				fTurnHome = game.getTurnDataHome().getTurnNr();
				fTurnAway = game.getTurnDataAway().getTurnNr();
				fHalf = game.getHalf();
				fScoreHome = game.getGameResult().getTeamResultHome().getScore();
				fScoreAway = game.getGameResult().getTeamResultAway().getScore();
				fSpectators = clientData.getSpectators();
				fWeather = game.getFieldModel().getWeather();
				fCoachBannedHome = game.getTurnDataHome().isCoachBanned();
				fCoachBannedAway = game.getTurnDataAway().isCoachBanned();
				drawBackground();
				drawTurn();
				drawScore();
				drawSpectators();
				drawWeather();
				drawBannedCoaches();
				repaint();
				fRefreshNecessary = false;
			}
		} else {
			drawBackground();
			repaint();
		}
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public String getToolTipText(MouseEvent pMouseEvent) {
		String toolTip = null;
		Game game = getClient().getGame();
		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
		FieldModel fieldModel = game.getFieldModel();
		if ((fWeather != null) && weatherLocation.contains(pMouseEvent.getPoint())) {
			toolTip = "<html><b>" + fieldModel.getWeather().getName() + "</b><br>" +
				mechanic.weatherDescription(fWeather) + "</html>";
		}
		if ((fSpectators > 0) && spectatorLocation.contains(pMouseEvent.getPoint())) {
			toolTip = "<html>" + fSpectators +
				((fSpectators == 1) ? " spectator is watching the game." : " spectators are watching the game.") +
				"</html>";
		}
		return toolTip;
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
	}
}
