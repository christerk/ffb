package com.fumbbl.ffb.client;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.client.dialog.DialogHandler;
import com.fumbbl.ffb.client.dialog.DialogInformation;
import com.fumbbl.ffb.client.dialog.DialogLeaveGame;
import com.fumbbl.ffb.client.dialog.DialogManager;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.sound.SoundEngine;
import com.fumbbl.ffb.client.ui.ChatComponent;
import com.fumbbl.ffb.client.ui.GameMenuBar;
import com.fumbbl.ffb.client.ui.LogComponent;
import com.fumbbl.ffb.client.ui.ScoreBarComponent;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.rng.MouseEntropySource;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kalimar
 */
public class UserInterface extends JFrame implements WindowListener, IDialogCloseListener {

	private final FantasyFootballClient fClient;
	private final FieldComponent fFieldComponent;
	private final StatusReport fStatusReport;
	private final SideBarComponent fSideBarHome;
	private final SideBarComponent fSideBarAway;
	private final IconCache fIconCache;
	private final FontCache fontCache;
	private final SoundEngine fSoundEngine;
	private final ScoreBarComponent fScoreBar;
	private final LogComponent fLog;
	private final ChatComponent fChat;
	private final DialogManager fDialogManager;
	private JDesktopPane fDesktop;
	private GameTitle fGameTitle;
	private final PlayerIconFactory fPlayerIconFactory;
	private final MouseEntropySource fMouseEntropySource;

	private final UiDimensionProvider uiDimensionProvider;
	private final PitchDimensionProvider pitchDimensionProvider;
	private final LayoutSettings layoutSettings;
	private final StyleProvider styleProvider;

	public UserInterface(FantasyFootballClient pClient) {

		fDesktop = null;
		fClient = pClient;
		String factorValue = pClient.getProperty(CommonProperty.SETTING_SCALE_FACTOR);
		double scale = 1.0;
		if (StringTool.isProvided(factorValue)) {
			try {
				scale = Double.parseDouble(factorValue);
			} catch (Exception ignored) {
			}
		}

		layoutSettings = new LayoutSettings(pClient.getParameters().getLayout(), scale);
		uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		pitchDimensionProvider = new PitchDimensionProvider(layoutSettings);
		fIconCache = new IconCache(getClient());
		fIconCache.init();
		fontCache = new FontCache();
		fSoundEngine = new SoundEngine(getClient());
		UIManager.put("ToolTip.font", fontCache.font(Font.PLAIN, 14, uiDimensionProvider));
		fSoundEngine.init();
		fDialogManager = new DialogManager(getClient());
		styleProvider = new StyleProvider();
		setGameMenuBar(new GameMenuBar(getClient(), uiDimensionProvider, styleProvider, fontCache));
		setGameTitle(new GameTitle());
		fPlayerIconFactory = new PlayerIconFactory();
		fStatusReport = new StatusReport(getClient());
		fMouseEntropySource = new MouseEntropySource(this);


		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);

		fScoreBar = new ScoreBarComponent(getClient(), uiDimensionProvider, styleProvider, fontCache);
		fFieldComponent = new FieldComponent(getClient(), uiDimensionProvider, pitchDimensionProvider, fontCache);
		fLog = new LogComponent(getClient(), styleProvider, uiDimensionProvider);
		fChat = new ChatComponent(getClient(), uiDimensionProvider, styleProvider);
		fSideBarHome = new SideBarComponent(getClient(), true, uiDimensionProvider, styleProvider, fontCache);
		fSideBarAway = new SideBarComponent(getClient(), false, uiDimensionProvider, styleProvider, fontCache);

		initComponents(false);

		getChat().requestChatInputFocus();

	}

	public void initComponents(boolean callInit) {
		if (fDesktop != null) {
			getContentPane().remove(fDesktop);
		}
		fDesktop = new JDesktopPane();
		// TODO remove after moving component to UI element
		fClient.getActionKeyBindings().addKeyBindings(fDesktop, ActionKeyGroup.RESIZE);

		fFieldComponent.initLayout();
		fLog.initLayout();
		fChat.initLayout();
		fSideBarHome.initLayout();
		fSideBarAway.initLayout();
		fScoreBar.initLayout();

		JPanel panelContent;
		switch (layoutSettings.getLayout()) {
			case PORTRAIT:
				panelContent = portraitContent();
				break;
			case SQUARE:
				panelContent = squareContent();
				break;
			default:
				panelContent = landscapeContent();
				break;
		}

		panelContent.setSize(panelContent.getPreferredSize());

		fDesktop.add(panelContent, -1);
		fDesktop.setPreferredSize(panelContent.getPreferredSize());

		getContentPane().add(fDesktop, BorderLayout.CENTER);
		pack();

		if (callInit) {
			init(null);
			getChat().getReplayControl().refresh();
		}
		fDialogManager.setShownDialogParameter(null);
		fDialogManager.updateDialog();
	}

	private JPanel landscapeContent() {
		JPanel fieldPanel = fieldPanel();

		JPanel logChatPanel = logChatPanel();

		JPanel panelCenter = wrapperPanel(BoxLayout.Y_AXIS, fieldPanel, getScoreBar(), logChatPanel);

		return wrapperPanel(BoxLayout.X_AXIS, createSideBarPanel(fSideBarHome), panelCenter, createSideBarPanel(fSideBarAway));
	}

	private JPanel createSideBarPanel(SideBarComponent fSideBarHome) {
		JPanel panelHome = new JPanel();
		panelHome.setLayout(new BoxLayout(panelHome, BoxLayout.Y_AXIS));
		panelHome.add(fSideBarHome);
		return panelHome;
	}

	private JPanel portraitContent() {
		JPanel fieldPanel = fieldPanel();

		JPanel logChatPanel = logChatPanel();

		JPanel panelMain = wrapperPanel(BoxLayout.X_AXIS, createSideBarPanel(fSideBarHome), fieldPanel, createSideBarPanel(fSideBarAway));

		return wrapperPanel(BoxLayout.Y_AXIS, panelMain, getScoreBar(), logChatPanel);
	}

	private JPanel logChatPanel() {
		JPanel logChatPanel = new JPanel();
		logChatPanel.setLayout(new BoxLayout(logChatPanel, BoxLayout.X_AXIS));
		logChatPanel.add(getLog());
		logChatPanel.add(Box.createHorizontalStrut(2));
		logChatPanel.add(getChat());
		logChatPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		return logChatPanel;
	}

	private JPanel fieldPanel() {
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
		fieldPanel.add(fFieldComponent);
		return fieldPanel;
	}

	private JPanel squareContent() {
		JPanel fieldPanel = fieldPanel();

		JPanel logChatScorePanel = wrapperPanel(BoxLayout.Y_AXIS, getLog(), getScoreBar(), getChat());
		logChatScorePanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		JPanel panelMain = wrapperPanel(BoxLayout.X_AXIS, createSideBarPanel(fSideBarHome), fieldPanel, createSideBarPanel(fSideBarAway));

		JPanel panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.X_AXIS));
		panelContent.add(panelMain);
		panelContent.add(logChatScorePanel);

		return panelContent;
	}

	private JPanel wrapperPanel(int axis, JPanel fSideBarHome, JPanel fieldPanel, JPanel fSideBarAway) {
		JPanel panelMain = new JPanel();
		//noinspection MagicConstant
		panelMain.setLayout(new BoxLayout(panelMain, axis));
		panelMain.add(fSideBarHome);
		panelMain.add(fieldPanel);
		panelMain.add(fSideBarAway);
		return panelMain;
	}

	public FontCache getFontCache() {
		return fontCache;
	}

	public StyleProvider getStyleProvider() {
		return styleProvider;
	}

	public UiDimensionProvider getUiDimensionProvider() {
		return uiDimensionProvider;
	}

	public PitchDimensionProvider getPitchDimensionProvider() {
		return pitchDimensionProvider;
	}

	public FieldComponent getFieldComponent() {
		return fFieldComponent;
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public SideBarComponent getSideBarHome() {
		return fSideBarHome;
	}

	public SideBarComponent getSideBarAway() {
		return fSideBarAway;
	}

	public IconCache getIconCache() {
		return fIconCache;
	}

	public SoundEngine getSoundEngine() {
		return fSoundEngine;
	}

	public LogComponent getLog() {
		return fLog;
	}

	public ChatComponent getChat() {
		return fChat;
	}

	public DialogManager getDialogManager() {
		return fDialogManager;
	}

	public GameTitle getGameTitle() {
		return fGameTitle;
	}

	public void setGameTitle(GameTitle pGameTitle) {
		fGameTitle = pGameTitle;
		refreshTitle();
	}

	public void refreshTitle() {
		setTitle((fGameTitle != null) ? fGameTitle.toString() : "FantasyFootball");
	}

	public void refreshSideBars() {
		getSideBarHome().refresh();
		getSideBarAway().refresh();
		getScoreBar().refresh();
	}

	public void refresh() {
		refreshSideBars();
		getFieldComponent().refresh();
		getGameMenuBar().refresh();
	}

	public synchronized void init(GameOptions gameOptions) {

		if (gameOptions != null && ArrayTool.isProvided(gameOptions.getOptions())) {
			getStatusReport().init(gameOptions);
		}
		getSideBarHome().init();
		getSideBarAway().init();
		getScoreBar().init();
		getFieldComponent().init();
		getGameMenuBar().init();

		Game game = getClient().getGame();
		GameTitle gameTitle = new GameTitle();
		gameTitle.setClientMode(getClient().getMode());
		gameTitle.setHomeCoach(game.getTeamHome().getCoach());
		gameTitle.setAwayCoach(game.getTeamAway().getCoach());
		gameTitle.setGameTime(game.getGameTime());
		gameTitle.setTurnTime(game.getTurnTime());
		setGameTitle(gameTitle);

		String volumeSetting = getClient().getProperty(CommonProperty.SETTING_SOUND_VOLUME);
		int volume = StringTool.isProvided(volumeSetting) ? Integer.parseInt(volumeSetting) : 50;
		getClient().getUserInterface().getSoundEngine().setVolume(volume);

	}

	public JDesktopPane getDesktop() {
		return fDesktop;
	}

	public GameMenuBar getGameMenuBar() {
		return (GameMenuBar) getJMenuBar();
	}

	public void setGameMenuBar(GameMenuBar pGameMenuBar) {
		setJMenuBar(pGameMenuBar);
	}

	public ScoreBarComponent getScoreBar() {
		return fScoreBar;
	}

	public PlayerIconFactory getPlayerIconFactory() {
		return fPlayerIconFactory;
	}

	public StatusReport getStatusReport() {
		return fStatusReport;
	}

	public void invokeAndWait(Runnable pRunnable) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				pRunnable.run();
			} else {
				SwingUtilities.invokeAndWait(pRunnable);
			}
		} catch (InterruptedException | InvocationTargetException e) {
			throw new FantasyFootballException(e);
		}
	}

	public void invokeLater(Runnable pRunnable) {
		SwingUtilities.invokeLater(pRunnable);
	}

	public MouseEntropySource getMouseEntropySource() {
		return fMouseEntropySource;
	}

	public void windowClosing(WindowEvent pE) {
		DialogHandler dialogHandler = getDialogManager().getDialogHandler();
		if (dialogHandler != null && dialogHandler.preventsExit()) {
			new DialogInformation(getClient(), "Can't leave game",
				new String[]{"The game is not finished.", "You and your opponent have to close the current dialog first"},
				DialogInformation.OK_DIALOG, true).showDialog(this);
		} else {
			DialogLeaveGame leaveGameQuestion = new DialogLeaveGame(getClient());
			leaveGameQuestion.showDialog(this);
		}
	}

	public void windowActivated(WindowEvent pE) {
	}

	public void windowClosed(WindowEvent pE) {
	}

	public void windowDeactivated(WindowEvent pE) {
	}

	public void windowDeiconified(WindowEvent pE) {
	}

	public void windowIconified(WindowEvent pE) {
	}

	public void windowOpened(WindowEvent pE) {
	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
		if (DialogId.LEAVE_GAME == pDialog.getId()) {
			if (((DialogLeaveGame) pDialog).isChoiceYes()) {
				System.exit(0);
			}
		}
	}

}
