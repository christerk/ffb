package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.StyleProvider;
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

	private final FontCache fontCache;
	private Font scoreFont;
	private Font turnNumberFont;
	private Font turnTextFont;

	private final DimensionProvider dimensionProvider;
	private final StyleProvider styleProvider;
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
	private Font spectatorFont;

	public ScoreBarComponent(FantasyFootballClient pClient, DimensionProvider dimensionProvider,
													 StyleProvider styleProvider, FontCache fontCache) {
		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
		setLayout(null);
		ToolTipManager.sharedInstance().registerComponent(this);
		fRefreshNecessary = true;
		addMouseMotionListener(this);
		this.fontCache = fontCache;
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		if (styleProvider.getFrameBackground() == null) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			String scorebarBackground = dimensionProvider.getLayout() ==
				DimensionProvider.ClientLayout.SQUARE ? IIconProperty.SCOREBAR_BACKGROUND_SQUARE : IIconProperty.SCOREBAR_BACKGROUND;
			BufferedImage background = iconCache.getIconByProperty(scorebarBackground);
			g2d.drawImage(background, 0, 0, fImage.getWidth(), fImage.getHeight(), null);
		} else {
			g2d.setColor(styleProvider.getFrameBackground());
			Dimension dimension = dimensionProvider.dimension(DimensionProvider.Component.SCORE_BOARD);
			g2d.fillRect(0, 0, dimension.width, dimension.height);
		}
		g2d.dispose();
	}

	private int lineHeight() {
		return dimensionProvider.scale(32);
	}

	private void drawScore() {
		Graphics2D g2d = fImage.createGraphics();
		String scoreHome = Integer.toString(fScoreHome);
		String scoreAway = Integer.toString(fScoreAway);
		g2d.setFont(scoreFont);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D boundsHome = fontMetrics.getStringBounds(scoreHome, g2d);
		int x;
		x = ((getPreferredSize().width - (int) boundsHome.getWidth()) / 2) - dimensionProvider.scale(40);
		int y = ((lineHeight() + fontMetrics.getHeight()) / 2) - fontMetrics.getDescent() - 1;
		if (dimensionProvider.getLayout() == DimensionProvider.ClientLayout.SQUARE) {
			y += lineHeight();
		}
		UtilClientGraphics.drawShadowedText(g2d, scoreHome, x, y, styleProvider);
		Rectangle2D boundsAway = fontMetrics.getStringBounds(scoreAway, g2d);
		x = ((getPreferredSize().width - (int) boundsAway.getWidth()) / 2) + dimensionProvider.scale(40);
		UtilClientGraphics.drawShadowedText(g2d, scoreAway, x, y, styleProvider);
		g2d.dispose();
	}

	private void drawTurn() {
		Graphics2D g2d = fImage.createGraphics();
		Game game = getClient().getGame();

		String turn = game.getTurnDataHome().getTurnNr() + " / " +
			game.getTurnDataAway().getTurnNr();

		g2d.setFont(turnNumberFont);
		FontMetrics metricsInts = g2d.getFontMetrics();
		int yInts = ((lineHeight() + metricsInts.getHeight()) / 2) - metricsInts.getDescent() - 1;
		Rectangle2D turnBounds = metricsInts.getStringBounds(turn, g2d);

		g2d.setFont(turnTextFont);
		FontMetrics metricsText = g2d.getFontMetrics();
		int yText = ((lineHeight() + metricsText.getHeight()) / 2) - metricsText.getDescent();
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
			int length = (int) (turnPrefixBounds.getWidth() + turnBounds.getWidth() + halfBounds.getWidth() + dimensionProvider.scale(20));
			x = (getWidth() - length) / 2;
		} else {
			x = 4;
		}

		g2d.setFont(turnTextFont);
		UtilClientGraphics.drawShadowedText(g2d, _TURN, x, yText, styleProvider);
		x += turnPrefixBounds.getWidth() + dimensionProvider.scale(10);
		g2d.setFont(turnNumberFont);
		UtilClientGraphics.drawShadowedText(g2d, turn, x, yInts, styleProvider);
		x += turnBounds.getWidth() + dimensionProvider.scale(10);
		g2d.setFont(turnTextFont);
		UtilClientGraphics.drawShadowedText(g2d, half, x, yText, styleProvider);
		g2d.dispose();
	}

	private void drawSpectators() {
		if (fSpectators > 0) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage spectatorsImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_SPECTATORS);
			g2d.drawImage(spectatorsImage, spectatorLocation.x, spectatorLocation.y, null);
			g2d.setFont(spectatorFont);
			String spectatorString = Integer.toString(fSpectators);
			UtilClientGraphics.drawShadowedText(g2d, spectatorString, spectatorLocation.x + dimensionProvider.scale(108), spectatorLocation.y + dimensionProvider.scale(21), styleProvider);
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

		int unscaledWidth = dimensionProvider.unscaledDimension(DimensionProvider.Component.SCORE_BOARD).width;

		if (dimensionProvider.getLayout() == DimensionProvider.ClientLayout.SQUARE) {
			weatherLocation = dimensionProvider.scale(new Rectangle(159, 64, 100, 32));
			spectatorLocation = dimensionProvider.scale(new Rectangle(1, 64, 130, 32));
			coachBannedHome = dimensionProvider.scale(new Rectangle(1, 0, 36, 32));
			coachBannedAway = dimensionProvider.scale(new Rectangle(unscaledWidth - 37, 0, 36, 32));
		} else {
			weatherLocation = dimensionProvider.scale(new Rectangle(unscaledWidth - 101, 0, 100, 32));
			spectatorLocation = dimensionProvider.scale(new Rectangle((unscaledWidth / 2 + 160), 0, 130, 32));
			coachBannedHome = dimensionProvider.scale(new Rectangle((unscaledWidth / 2 - 130 - 36), 0, 36, 32));
			coachBannedAway = dimensionProvider.scale(new Rectangle((unscaledWidth / 2 + 130), 0, 36, 32));
		}
	}

	public void refresh() {
		scoreFont = fontCache.font(Font.BOLD, 24);
		turnNumberFont = fontCache.font(Font.BOLD, 22);
		turnTextFont = fontCache.font(Font.BOLD, 14);

		spectatorFont = fontCache.font(Font.BOLD, 14);

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
