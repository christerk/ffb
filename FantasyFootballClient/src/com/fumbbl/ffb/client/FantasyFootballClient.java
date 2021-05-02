package com.fumbbl.ffb.client;

import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.swing.UIManager;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.client.dialog.DialogAboutHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerFactory;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.net.ClientPingTask;
import com.fumbbl.ffb.client.net.CommandEndpoint;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.ClientStateFactory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.IConnectionListener;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FantasyFootballClient implements IConnectionListener, IDialogCloseListener, IFactorySource {
	private Game fGame;
	private UserInterface fUserInterface;
	private ClientCommunication fCommunication;
	private Thread fCommunicationThread;
	private Timer fPingTimer;
	private ClientPingTask fClientPingTask;
	private Properties fProperties;
	private ClientState fState;
	private ClientStateFactory fStateFactory;
	private ClientCommandHandlerFactory fCommandHandlerFactory;
	private boolean fConnectionEstablished;
	private ActionKeyBindings fActionKeyBindings;
	private ClientReplayer fReplayer;
	private ClientParameters fParameters;
	private ClientMode fMode;

	private Session fSession;
	private CommandEndpoint fCommandEndpoint;

	private transient ClientData fClientData;

	private FactoryManager factoryManager;
	private Map<Factory, INamedObjectFactory> factories;
	
	public FantasyFootballClient(ClientParameters pParameters) throws IOException {
		factoryManager = new FactoryManager();
		factories = factoryManager.getFactoriesForContext(getContext());
		
		fParameters = pParameters;
		setMode(fParameters.getMode());

		fClientData = new ClientData();

		loadProperties();

		fActionKeyBindings = new ActionKeyBindings(this);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		} catch (Exception all) {
			all.printStackTrace();
		}

		setGame(new Game(getFactorySource(), factoryManager));
		fStateFactory = new ClientStateFactory(this);
		fCommandHandlerFactory = new ClientCommandHandlerFactory(this);

		fReplayer = new ClientReplayer(this);

		fUserInterface = new UserInterface(this);
		fUserInterface.refreshSideBars();
		fUserInterface.getScoreBar().refresh();

		fCommandEndpoint = new CommandEndpoint(this);

		fCommunication = new ClientCommunication(this);
		fCommunicationThread = new Thread(fCommunication);
		fCommunicationThread.start();

		fPingTimer = new Timer(true);

	}

	public UserInterface getUserInterface() {
		return fUserInterface;
	}

	public Game getGame() {
		return fGame;
	}

	public void setGame(Game pGame) {
		fGame = pGame;
		getClientData().clear();
	}

	public ClientCommunication getCommunication() {
		return fCommunication;
	}

	public void connectionEstablished(boolean pSuccessful) {
		fConnectionEstablished = pSuccessful;
		synchronized (this) {
			this.notify();
		}
	}

	public void showUserInterface() throws IOException {
		getUserInterface().getFieldComponent().getLayerField().drawWeather(Weather.INTRO);
		getUserInterface().getFieldComponent().refresh();
		getUserInterface().setVisible(true);
		DialogAboutHandler aboutDialogHandler = new DialogAboutHandler(this);
		aboutDialogHandler.showDialog();
	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
	}

	public void startClient() {

		getUserInterface().getStatusReport().reportVersion();
		try {
			getUserInterface().getStatusReport().reportConnecting(getServerHost(), getServerPort());
		} catch (UnknownHostException pUnknownHostException) {
			throw new FantasyFootballException(pUnknownHostException);
		}

		boolean connectionEstablished = false;

		try {

			URI uri = new URI("ws", null, getServerHost().getCanonicalHostName(), getServerPort(), "/command", null, null);
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.setDefaultMaxSessionIdleTimeout(Integer.MAX_VALUE);
			container.setDefaultMaxTextMessageBufferSize(64 * 1024);
			fCommandEndpoint = new CommandEndpoint(this);
			fSession = container.connectToServer(fCommandEndpoint, uri);
			connectionEstablished = (fSession != null);

		} catch (Exception pAnyException) {
			pAnyException.printStackTrace();
		}

		String pingIntervalProperty = getProperty(IClientProperty.CLIENT_PING_INTERVAL);
		if (StringTool.isProvided(pingIntervalProperty) && (ClientMode.REPLAY != getMode())) {
			int pingInterval = Integer.parseInt(pingIntervalProperty);
			fClientPingTask = new ClientPingTask(this);
			fPingTimer.schedule(fClientPingTask, 0, pingInterval);
		}

		getUserInterface().getStatusReport().reportConnectionEstablished(connectionEstablished);

		updateClientState();

	}

	public void exitClient() {
		fPingTimer = null;
		try {
			fSession.close();
			fCommandEndpoint.awaitClose(10, TimeUnit.SECONDS);
		} catch (Exception pAnyException) {
			pAnyException.printStackTrace();
		}
		getCommunication().stop();
		getUserInterface().setVisible(false);
		System.exit(0);
	}

	public String getProperty(String pProperty) {
		return fProperties.getProperty(pProperty);
	}

	public void setProperty(String pProperty, String pValue) {
		if ((fProperties == null) || (pProperty == null) || (pValue == null)) {
			return;
		}
		fProperties.setProperty(pProperty, pValue);
	}

	public ClientState updateClientState() {
		ClientState newState = fStateFactory.getStateForGame();
		if ((newState != null) && (newState != fState)) {
			if (fState != null) {
				fState.leaveState();
			}
			fState = newState;
			if (Boolean.parseBoolean(getProperty(IClientProperty.CLIENT_DEBUG_STATE))) {
				getCommunication().sendDebugClientState(fState.getId());
			}
			getUserInterface().getGameMenuBar().changeState(fState.getId());
			fState.enterState();
		}
		return fState;
	}

	public ClientState getClientState() {
		return fState;
	}

	public ClientCommandHandlerFactory getCommandHandlerFactory() {
		return fCommandHandlerFactory;
	}

	public ClientPingTask getClientPingTask() {
		return fClientPingTask;
	}

	public boolean isConnectionEstablished() {
		return fConnectionEstablished;
	}

	public static void main(String[] args) {

		try {
			ClientParameters parameters = new ClientParameters();
			parameters.initFrom(args);
			if (!parameters.validate()) {
				System.out.println(ClientParameters.USAGE);
				return;
			}
			FantasyFootballClient client = new FantasyFootballClient(parameters);
			client.showUserInterface();
		} catch (Exception all) {
			all.printStackTrace(System.err);
		}

	}

	public ActionKeyBindings getActionKeyBindings() {
		return fActionKeyBindings;
	}

	public ClientReplayer getReplayer() {
		return fReplayer;
	}

	public void loadProperties() throws IOException {
		fProperties = new Properties();
		InputStream propertyInputStream = null;
		try {
			propertyInputStream = getClass().getResourceAsStream("/client.ini");
			fProperties.load(propertyInputStream);
		} finally {
			if (propertyInputStream != null) {
				propertyInputStream.close();
			}
		}
	}

	public ClientData getClientData() {
		return fClientData;
	}

	public ClientParameters getParameters() {
		return fParameters;
	}

	public int getServerPort() {
		if (getParameters().getPort() > 0) {
			return getParameters().getPort();
		} else {
			return Integer.parseInt(getProperty(IClientProperty.SERVER_PORT));
		}
	}

	public InetAddress getServerHost() throws UnknownHostException {
		String serverName = StringTool.isProvided(getParameters().getServer()) ? getParameters().getServer()
				: getProperty(IClientProperty.SERVER_HOST);

		return InetAddress.getByName(serverName);
	}

	public ClientMode getMode() {
		return fMode;
	}

	public void setMode(ClientMode pMode) {
		fMode = pMode;
	}

	public CommandEndpoint getCommandEndpoint() {
		return fCommandEndpoint;
	}

	public FactoryManager getFactoryManager() {
		return factoryManager;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
	}
	
	public IFactorySource getFactorySource() {
		return this;
	}

	@Override
	public IFactorySource forContext(FactoryContext context) {
		if (context == getContext()) {
			return this;
		}
		throw new FantasyFootballException("Trying to get game context from application.");
	}
}