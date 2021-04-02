package com.balancedbytes.games.ffb.client;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.client.dialog.DialogLeaveGame;
import com.balancedbytes.games.ffb.client.dialog.DialogManager;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.client.sound.SoundEngine;
import com.balancedbytes.games.ffb.client.ui.ChatComponent;
import com.balancedbytes.games.ffb.client.ui.GameMenuBar;
import com.balancedbytes.games.ffb.client.ui.LogComponent;
import com.balancedbytes.games.ffb.client.ui.ScoreBarComponent;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;
import com.fumbbl.rng.MouseEntropySource;

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
 * 
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
	private final JDesktopPane fDesktop;
	private GameTitle fGameTitle;
	private final PlayerIconFactory fPlayerIconFactory;
	private final MouseEntropySource fMouseEntropySource;

	public UserInterface(FantasyFootballClient pClient) {

		fClient = pClient;
		fIconCache = new IconCache(getClient());
		fIconCache.init();
		fSoundEngine = new SoundEngine(getClient());
		fSoundEngine.init();
		fDialogManager = new DialogManager(getClient());
		setGameMenuBar(new GameMenuBar(getClient()));
		setGameTitle(new GameTitle());

		fFieldComponent = new FieldComponent(getClient());
		fPlayerIconFactory = new PlayerIconFactory();
		fStatusReport = new StatusReport(getClient());

		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
		fieldPanel.add(fFieldComponent);
		// fieldPanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));

		fLog = new LogComponent(getClient());
		fChat = new ChatComponent(getClient());

		JPanel logChatPanel = new JPanel();
		logChatPanel.setLayout(new BoxLayout(logChatPanel, BoxLayout.X_AXIS));
		logChatPanel.add(getLog());
		logChatPanel.add(Box.createHorizontalStrut(2));
		logChatPanel.add(getChat());
		logChatPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		fScoreBar = new ScoreBarComponent(getClient());

		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
		panelCenter.add(fieldPanel);
		panelCenter.add(getScoreBar());
		panelCenter.add(logChatPanel);

		fSideBarHome = new SideBarComponent(getClient(), true);
		JPanel panelHome = new JPanel();
		panelHome.setLayout(new BoxLayout(panelHome, BoxLayout.Y_AXIS));
		panelHome.add(fSideBarHome);
		// panelHome.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));

		fSideBarAway = new SideBarComponent(getClient(), false);
		JPanel panelAway = new JPanel();
		panelAway.setLayout(new BoxLayout(panelAway, BoxLayout.Y_AXIS));
		panelAway.add(fSideBarAway);
		// panelAway.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));

		JPanel panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.X_AXIS));
		panelContent.add(panelHome);
		// panelContent.add(Box.createHorizontalStrut(1));
		panelContent.add(panelCenter);
		// panelContent.add(Box.createHorizontalStrut(1));
		panelContent.add(panelAway);

		fDesktop = new JDesktopPane();
		panelContent.setSize(panelContent.getPreferredSize());
		fDesktop.add(panelContent, -1);
		fDesktop.setPreferredSize(panelContent.getPreferredSize());

		fMouseEntropySource = new MouseEntropySource(this);

		getContentPane().add(fDesktop, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);

		pack();

		getChat().requestChatInputFocus();

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

	public void init(GameOptions gameOptions) {

		getStatusReport().init(gameOptions);
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
