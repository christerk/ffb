package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerFactory;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.net.ClientPingTask;
import com.fumbbl.ffb.client.net.CommandEndpoint;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.ClientStateFactory;
import com.fumbbl.ffb.factory.IFactorySource;
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
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class FantasyFootballClient implements IConnectionListener, IDialogCloseListener, IFactorySource {

	private Session fSession;
	private CommandEndpoint fCommandEndpoint;
	private Timer fPingTimer;

	private ClientState fState;
	private final ClientStateFactory fStateFactory;
	private final ClientCommandHandlerFactory fCommandHandlerFactory;

	public FantasyFootballClient(String coach) throws IOException {
		loadProperties();
		loadLocallyStoredProperties(coach);

		fCommandEndpoint = new CommandEndpoint(this);
		fPingTimer = new Timer(true);
		fStateFactory = new ClientStateFactory(this);
		fCommandHandlerFactory = new ClientCommandHandlerFactory(this);
	}

	public abstract UserInterface getUserInterface();

	public abstract Game getGame();

	public abstract void setGame(Game pGame);

	public abstract ClientCommunication getCommunication();

	public abstract void connectionEstablished(boolean pSuccessful);

	public abstract void showUserInterface();

	public abstract void dialogClosed(IDialog pDialog);

	public void startClient() {

		preConnect();

		boolean connectionEstablished = initConnection();
		updateClientState();

		postConnect(connectionEstablished);


	}

	protected abstract void postConnect(boolean connectionEstablished);

	protected abstract void preConnect();

	public abstract void exitClient();

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

	public abstract void updateLocalPropertiesStore();

	public ClientState updateClientState() {
		ClientState newState = fStateFactory.getStateForGame();
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

	public ClientState getClientState() {
		return fState;
	}

	public ClientCommandHandlerFactory getCommandHandlerFactory() {
		return fCommandHandlerFactory;
	}

	public abstract ActionKeyBindings getActionKeyBindings();

	public abstract ClientReplayer getReplayer();

	public abstract void loadProperties() throws IOException;

	public abstract List<CommonProperty> getLocallyStoredPropertyKeys();

	public abstract void setLocallyStoredPropertyKeys(List<CommonProperty> properties);

	public abstract void loadLocallyStoredProperties(String coach);

	public abstract ClientData getClientData();

	public abstract ClientParameters getParameters();

	public abstract ClientMode getMode();

	public abstract void setMode(ClientMode pMode);

	public CommandEndpoint getCommandEndpoint() {
		return fCommandEndpoint;
	}

	public abstract FactoryManager getFactoryManager();

	public abstract IFactorySource getFactorySource();

	public abstract int getCurrentMouseButton();

	public abstract void setCurrentMouseButton(int currentMouseButton);

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
			pAnyException.printStackTrace();
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
			pAnyException.printStackTrace();
		}
		getCommunication().stop();
	}
}
