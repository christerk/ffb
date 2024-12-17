package com.fumbbl.ffb.client;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerFactory;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.net.ClientPingTask;
import com.fumbbl.ffb.client.net.CommandEndpoint;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.ClientStateFactory;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.IConnectionListener;
import com.fumbbl.ffb.util.StringTool;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class FantasyFootballClient implements IConnectionListener, IDialogCloseListener, IFactorySource {

	private Session fSession;
	private CommandEndpoint fCommandEndpoint;
	private Timer fPingTimer;
	private final transient ClientData fClientData;
	private ClientState<? extends LogicModule, ? extends FantasyFootballClient> fState;
	protected ClientStateFactory<? extends FantasyFootballClient> fStateFactory;
	private final ClientCommandHandlerFactory fCommandHandlerFactory;
	private final ClientCommunication fCommunication;
	private Game fGame;
	private ClientMode fMode;
	private final ClientParameters parameters;

	private final FactoryManager factoryManager;
	@SuppressWarnings("rawtypes")
	private final Map<FactoryType.Factory, INamedObjectFactory> factories;

	public FantasyFootballClient(ClientParameters parameters) throws IOException {
		loadProperties();
		loadLocallyStoredProperties(parameters.getCoach());
		fClientData = new ClientData();

		this.parameters = parameters;
		setMode(parameters.getMode());

		fCommandEndpoint = new CommandEndpoint(this);
		fPingTimer = new Timer(true);
		fCommandHandlerFactory = new ClientCommandHandlerFactory(this);

		setClientStateFactory();
		factoryManager = new FactoryManager();
		factories = factoryManager.getFactoriesForContext(getContext());
		setGame(new Game(getFactorySource(), factoryManager));

		fCommunication = new ClientCommunication(this);
		Thread fCommunicationThread = new Thread(fCommunication);
		fCommunicationThread.start();
	}

	public long gameId() {
		if (fGame != null) {
			return fGame.getId();
		}
		return 0;
	}

	protected abstract void setClientStateFactory();

	public abstract UserInterface getUserInterface();

	public ClientCommunication getCommunication() {
		return fCommunication;
	}

	public void connectionEstablished(boolean pSuccessful) {
		synchronized (this) {
			this.notify();
		}
	}

	public ClientParameters getParameters() {
		return parameters;
	}
	public abstract void dialogClosed(IDialog pDialog);

	public void startClient() {

		preConnect();

		boolean connectionEstablished = initConnection();
		updateClientState();

		postConnect(connectionEstablished);
	}

	protected abstract void postConnect(boolean connectionEstablished);

	protected abstract void preConnect();

	public abstract String getProperty(CommonProperty property);

	public abstract String getProperty(String pProperty);

	public abstract void setProperty(CommonProperty pProperty, String pValue);

	public abstract void setProperty(String pProperty, String pValue);

	public void saveUserSettings(boolean pUserinterfaceInit) {
		List<CommonProperty> localProperties = getLocallyStoredPropertyKeys();

		List<CommonProperty> remoteProperties = Arrays.stream(CommonProperty._SAVED_USER_SETTINGS)
			.filter(property -> !localProperties.contains(property)).collect(Collectors.toList());

		String[] settingValues = new String[remoteProperties.size()];
		for (int i = 0; i < remoteProperties.size(); i++) {
			settingValues[i] = getProperty(remoteProperties.get(i));
		}
		getCommunication().sendUserSettings(remoteProperties.toArray(new CommonProperty[0]), settingValues);

		updateLocalPropertiesStore();

		if (pUserinterfaceInit) {
			initUI();
		}
	}

	protected abstract void initUI();

	public ClientState<? extends LogicModule, ? extends FantasyFootballClient> updateClientState() {
		ClientState<? extends LogicModule, ? extends FantasyFootballClient> newState = fStateFactory.getStateForGame();
		if ((newState != null) && (newState != fState)) {
			if (fState != null) {
				fState.leaveState();
			}
			fState = newState;
			if (Boolean.parseBoolean(getProperty(CommonProperty.CLIENT_DEBUG_STATE))) {
				getCommunication().sendDebugClientState(fState.getId());
			}
			getUserInterface().getGameMenuBar().changeState(fState.getId());
			fState.enterState();
		}
		return fState;
	}

	public ClientState<? extends LogicModule, ? extends FantasyFootballClient> getClientState() {
		return fState;
	}

	public ClientCommandHandlerFactory getCommandHandlerFactory() {
		return fCommandHandlerFactory;
	}

	// TODO remove from generic client
	public abstract ActionKeyBindings getActionKeyBindings();

	public abstract ClientReplayer getReplayer();

	public abstract void loadProperties() throws IOException;

	public abstract List<CommonProperty> getLocallyStoredPropertyKeys();

	public abstract void setLocallyStoredPropertyKeys(List<CommonProperty> properties);

	public abstract void loadLocallyStoredProperties(String coach);

	public ClientData getClientData() {
		return fClientData;
	}

	public CommandEndpoint getCommandEndpoint() {
		return fCommandEndpoint;
	}

	public IFactorySource getFactorySource() {
		return this;
	}

	@Override
	public IFactorySource forContext(FactoryType.FactoryContext context) {
		if (context == getContext()) {
			return this;
		}
		throw new FantasyFootballException("Trying to get game context from application.");
	}

	public abstract int getServerPort();

	public abstract InetAddress getServerHost() throws UnknownHostException;

	protected boolean initConnection() {
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
			logWithOutGameId(pAnyException);
		}

		String pingIntervalProperty = getProperty(CommonProperty.CLIENT_PING_INTERVAL);
		if (StringTool.isProvided(pingIntervalProperty) && (ClientMode.REPLAY != getMode())) {
			int pingInterval = Integer.parseInt(pingIntervalProperty);
			ClientPingTask fClientPingTask = new ClientPingTask(this);
			fPingTimer.schedule(fClientPingTask, 0, pingInterval);
		}
		return connectionEstablished;
	}

	protected void closeConnection() {
		fPingTimer = null;
		try {
			fSession.close();
			fCommandEndpoint.awaitClose(10, TimeUnit.SECONDS);
		} catch (Exception pAnyException) {
			logWithOutGameId(pAnyException);
		}
		getCommunication().stop();
	}

	public void exitClient() {
		closeConnection();
		exit();
	}

	abstract protected void exit();

	public void updateLocalPropertiesStore() {
		clearPrefs();
		for (CommonProperty property : CommonProperty._SAVED_USER_SETTINGS) {
			String key = property.getKey();
			String value = getProperty(property);
			if (StringTool.isProvided(key) && StringTool.isProvided(value)) {
				setPref(key, value);
			}
		}
	}

	abstract protected void clearPrefs();

	abstract protected void setPref(String key, String value);

	public Game getGame() {
		return fGame;
	}

	public void setGame(Game pGame) {
		fGame = pGame;
		getClientData().clear();
	}

	public ClientMode getMode() {
		return fMode;
	}

	public void setMode(ClientMode pMode) {
		fMode = pMode;
	}


	public FactoryManager getFactoryManager() {
		return factoryManager;
	}

	@Override
	public FactoryType.FactoryContext getContext() {
		return FactoryType.FactoryContext.APPLICATION;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public <T extends INamedObjectFactory> T getFactory(FactoryType.Factory factory) {
		return (T) factories.get(factory);
	}

	public void logError(String message) {
		logError(gameId(), message);
	}

	@SuppressWarnings("unused")
	public void logDebug(String message) {
		logDebug(gameId(), message);
	}
}
