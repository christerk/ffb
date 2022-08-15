package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FantasyFootballException;
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
import java.awt.BorderLayout;
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
	private final SoundEngine fSoundEngine;
	private final ScoreBarComponent fScoreBar;
	private final LogComponent fLog;
	private final ChatComponent fChat;
	private final DialogManager fDialogManager;
	private JDesktopPane fDesktop;
	private GameTitle fGameTitle;
	private final PlayerIconFactory fPlayerIconFactory;
	private final MouseEntropySource fMouseEntropySource;

	private final DimensionProvider dimensionProvider;

	public UserInterface(FantasyFootballClient pClient) {

		fDesktop = null;
		fClient = pClient;
		fIconCache = new IconCache(getClient());
		fIconCache.init();
		fSoundEngine = new SoundEngine(getClient());
		fSoundEngine.init();
		fDialogManager = new DialogManager(getClient());
		setGameMenuBar(new GameMenuBar(getClient()));
		setGameTitle(new GameTitle());
		fPlayerIconFactory = new PlayerIconFactory();
		fStatusReport = new StatusReport(getClient());
		fMouseEntropySource = new MouseEntropySource(this);

		dimensionProvider = new DimensionProvider(pClient.getParameters().isPortrait());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);

		fScoreBar = new ScoreBarComponent(getClient());
		fFieldComponent = new FieldComponent(getClient(), dimensionProvider);
		fLog = new LogComponent(getClient(), dimensionProvider);
		fChat = new ChatComponent(getClient(), dimensionProvider);
		fSideBarHome = new SideBarComponent(getClient(), true, dimensionProvider);
		fSideBarAway = new SideBarComponent(getClient(), false, dimensionProvider);

		initComponents(false);

		getChat().requestChatInputFocus();

	}

	public void initComponents(boolean callInit) {
		if (fDesktop != null) {
			getContentPane().remove(fDesktop);
		}
		fDesktop = new JDesktopPane();

		fFieldComponent.initLayout(dimensionProvider);
		fLog.initLayout(dimensionProvider);
		fChat.initLayout(dimensionProvider);
		fSideBarHome.initLayout(dimensionProvider);
		fSideBarAway.initLayout(dimensionProvider);

		JPanel panelContent = dimensionProvider.isPortrait() ? portraitContent() : landscapeContent();
		panelContent.setSize(panelContent.getPreferredSize());

		fDesktop.add(panelContent, -1);
		fDesktop.setPreferredSize(panelContent.getPreferredSize());

		getContentPane().add(fDesktop, BorderLayout.CENTER);
		pack();

		if (callInit) {
			init(null);
		}
	}

	private JPanel landscapeContent() {
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
		fieldPanel.add(fFieldComponent);

		JPanel logChatPanel = new JPanel();
		logChatPanel.setLayout(new BoxLayout(logChatPanel, BoxLayout.X_AXIS));
		logChatPanel.add(getLog());
		logChatPanel.add(Box.createHorizontalStrut(2));
		logChatPanel.add(getChat());
		logChatPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
		panelCenter.add(fieldPanel);
		panelCenter.add(getScoreBar());
		panelCenter.add(logChatPanel);

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		panelMain.add(createSideBarPanel(fSideBarHome));
		panelMain.add(panelCenter);
		panelMain.add(createSideBarPanel(fSideBarAway));

		return panelMain;
	}

	private JPanel createSideBarPanel(SideBarComponent fSideBarHome) {
		JPanel panelHome = new JPanel();
		panelHome.setLayout(new BoxLayout(panelHome, BoxLayout.Y_AXIS));
		panelHome.add(fSideBarHome);
		return panelHome;
	}

	private JPanel portraitContent() {
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
		fieldPanel.add(fFieldComponent);


		JPanel logChatPanel = new JPanel();
		logChatPanel.setLayout(new BoxLayout(logChatPanel, BoxLayout.X_AXIS));
		logChatPanel.add(getLog());
		logChatPanel.add(Box.createHorizontalStrut(2));
		logChatPanel.add(getChat());
		logChatPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		panelMain.add(createSideBarPanel(fSideBarHome));
		panelMain.add(fieldPanel);
		panelMain.add(createSideBarPanel(fSideBarAway));

		JPanel panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		panelContent.add(panelMain);
		panelContent.add(getScoreBar());
		panelContent.add(logChatPanel);

		return panelContent;
	}

	public DimensionProvider getDimensionProvider() {
		return dimensionProvider;
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

		String volumeSetting = getClient().getProperty(IClientProperty.SETTING_SOUND_VOLUME);
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
			SwingUtilities.invokeAndWait(pRunnable);
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
		DialogLeaveGame leaveGameQuestion = new DialogLeaveGame(getClient());
		leaveGameQuestion.showDialog(this);
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
