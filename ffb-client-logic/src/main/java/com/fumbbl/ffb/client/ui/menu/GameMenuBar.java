package com.fumbbl.ffb.client.ui.menu;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.overlay.sketch.ClientSketchManager;
import com.fumbbl.ffb.client.ui.menu.game.GameModeMenu;
import com.fumbbl.ffb.client.ui.menu.game.ReplayMenu;
import com.fumbbl.ffb.client.ui.menu.game.StandardGameMenu;
import com.fumbbl.ffb.client.ui.menu.settings.UserSettingsMenu;
import com.fumbbl.ffb.client.ui.strategies.click.ClickStrategyRegistry;
import com.fumbbl.ffb.client.ui.swing.JLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fumbbl.ffb.client.FontConfig.Size.MEDIUM;
import static javax.swing.SwingConstants.VERTICAL;

/**
 * @author Kalimar
 */
public class GameMenuBar extends JMenuBar implements ActionListener, IDialogCloseListener {

	private final FantasyFootballClient fClient;
	private GameModeMenu gameModeMenu; // Menu for current game mode (StandardGame or Replay)

	private UserSettingsMenu userSettingsMenu;
	private SetupMenu setupMenu;
	private MissingPlayersMenu missingPlayersMenu;
	private InducementsMenu inducementsMenu;

	private final StyleProvider styleProvider;
	private final DimensionProvider dimensionProvider;
	private final LayoutSettings layoutSettings;
	private final ClientSketchManager sketchManager;
	private final ClickStrategyRegistry clickStrategyRegistry;
    private final JLabel gameInfo;
    private final JSeparator gameInfoSeparator;
    private final FontCache fontCache;
    private final FontConfigRegistry fontConfigRegistry;
    private final GameTitle gameTitle;

	private final Set<FfbMenu> subMenus = new HashSet<>();

    public GameMenuBar(FantasyFootballClient client,
                       DimensionProvider dimensionProvider,
                       StyleProvider styleProvider,
                       FontCache fontCache,
                       FontConfigRegistry fontConfigRegistry,
                       ClientSketchManager sketchManager,
                       ClickStrategyRegistry clickStrategyRegistry,
                       GameTitle gameTitle) {

        this.fontCache = fontCache;
        this.fontConfigRegistry = fontConfigRegistry;
        FontConfig fc = fontConfigRegistry.getConfig(dimensionProvider.getLayoutSettings().getLayout());
        setFont(fontCache.font(Font.PLAIN, fc.getSize(MEDIUM), dimensionProvider));

		fClient = client;
		this.sketchManager = sketchManager;
		this.styleProvider = styleProvider;
		this.dimensionProvider = dimensionProvider;
		this.layoutSettings = dimensionProvider.getLayoutSettings();
		this.clickStrategyRegistry = clickStrategyRegistry;

        gameInfo = new JLabel(dimensionProvider, "");
        gameInfo.setVisible(false);
        gameInfoSeparator = new JSeparator(VERTICAL);
        gameInfoSeparator.setMaximumSize(new Dimension(10, 100));
        gameInfoSeparator.setVisible(false);
        this.gameTitle = gameTitle;

		init();
	}

	@Override
	public void remove(Component c) {
		if (c instanceof FfbMenu) {
			subMenus.remove((FfbMenu) c);
		}
		super.remove(c);
	}

	@Override
	public JMenu add(JMenu c) {
		if (c instanceof FfbMenu) {
			subMenus.add((FfbMenu) c);
		}
		return super.add(c);
	}

	public void updateJoinedCoachesMenu() {
		if (gameModeMenu instanceof ReplayMenu) {
			gameModeMenu.refresh();
		}
	}

	public void init() {

		Arrays.stream(this.getComponents()).forEach(this::remove);

		// Create and store the appropriate game mode menu
		if (getClient().getMode() == ClientMode.REPLAY) {
			gameModeMenu = new ReplayMenu(getClient(), dimensionProvider, getClient().getCommunication(), styleProvider, layoutSettings, sketchManager, clickStrategyRegistry);
		} else {
			gameModeMenu = new StandardGameMenu(getClient(), dimensionProvider, getClient().getCommunication(), styleProvider, layoutSettings);
		}

		add(gameModeMenu);

		setupMenu = new SetupMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(setupMenu);

		userSettingsMenu = new UserSettingsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(userSettingsMenu);

		missingPlayersMenu = new MissingPlayersMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(missingPlayersMenu);

		inducementsMenu = new InducementsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(inducementsMenu);

		CardsMenu cardsMenu = new CardsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(cardsMenu);

		PrayersMenu prayersMenu = new PrayersMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(prayersMenu);

		OptionsMenu optionsMenu = new OptionsMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(optionsMenu);

		HelpMenu helpMenu = new HelpMenu(getClient(), dimensionProvider, styleProvider, layoutSettings);
		add(helpMenu);

		subMenus.forEach(FfbMenu::init);

        add(gameInfoSeparator);
        add(gameInfo);

		refresh();
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void refresh() {
		boolean reInit = subMenus.stream().map(FfbMenu::refresh).reduce((a, b) -> a || b).orElse(false);

		if (fClient.getUserInterface() != null && reInit) {
			fClient.getUserInterface().initComponents(true);
		}
        FontConfig fc = fontConfigRegistry.getConfig(dimensionProvider.getLayoutSettings().getLayout());

        Font font = fontCache.font(Font.PLAIN, fc.getSize(MEDIUM), dimensionProvider);
        setFont(font);
        gameInfo.setFont(font);
	}

    public void setGameInfoVisible(boolean isVisible) {
        gameInfoSeparator.setVisible(isVisible);
        gameInfo.setVisible(isVisible);
    }

    public void updateGameInfo(GameTitle fGameTitle) {
        if (gameInfo.isVisible()) {
            this.gameTitle.update(fGameTitle);
            String gameInfoText = gameTitle.toString();
            gameInfo.setText(gameInfoText);
        }
    }

	public void changeState(ClientStateId pStateId) {
		setupMenu.changeState(pStateId);
	}

	public void dialogClosed(IDialog pDialog) {
		fClient.getUserInterface().dialogClosed(pDialog);
	}

	private void updateScaleProperty(double scalingFactor) {
		getClient().setProperty(CommonProperty.SETTING_SCALE_FACTOR, Double.toString(scalingFactor));
		getClient().saveUserSettings(true);
	}

	public void increaseScaling() {
		updateScaleProperty(layoutSettings.largerScale());
	}

	public void decreaseScaling() {
		updateScaleProperty(layoutSettings.smallerScale());
	}

	public void resetScaling() {
		updateScaleProperty(LayoutSettings.BASE_SCALE_FACTOR);
	}


	public void showDialog(IDialog pDialog) {
		fClient.getUserInterface().showDialog(pDialog, this);
	}

	public void updateInducements() {
		inducementsMenu.refresh();
	}


	public void updateMissingPlayers() {
		missingPlayersMenu.refresh();
	}

	public String menuName(CommonProperty menuProperty) {
		return userSettingsMenu.menuName(menuProperty);
	}

	public Map<String, String> menuEntries(CommonProperty menuProperty) {
		return userSettingsMenu.menuEntries(menuProperty);
	}

	public void actionPerformed(ActionEvent e) {
		javax.swing.JMenuItem source = (javax.swing.JMenuItem) (e.getSource());
		if (source == null) {
			return;
		}

		subMenus.forEach(menu -> menu.actionPerformed(e));
	}

}
