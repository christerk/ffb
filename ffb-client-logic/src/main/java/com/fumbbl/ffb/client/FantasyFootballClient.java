package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.handler.ClientCommandHandlerFactory;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.net.CommandEndpoint;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.IConnectionListener;

import java.io.IOException;
import java.util.List;

public interface FantasyFootballClient extends IConnectionListener, IDialogCloseListener, IFactorySource {

	UserInterface getUserInterface();

	Game getGame();

	void setGame(Game pGame);

	ClientCommunication getCommunication();

	void connectionEstablished(boolean pSuccessful);

	void showUserInterface();

	void dialogClosed(IDialog pDialog);

	void startClient();

	void exitClient();

	String getProperty(CommonProperty property);

	String getProperty(String pProperty);

	void setProperty(CommonProperty pProperty, String pValue);

	void setProperty(String pProperty, String pValue);

	void saveUserSettings(boolean pUserinterfaceInit);

	void updateLocalPropertiesStore();

	ClientState updateClientState();

	ClientState getClientState();

	ClientCommandHandlerFactory getCommandHandlerFactory();

	ActionKeyBindings getActionKeyBindings();

	ClientReplayer getReplayer();

	void loadProperties() throws IOException;

	List<CommonProperty> getLocallyStoredPropertyKeys();

	void setLocallyStoredPropertyKeys(List<CommonProperty> properties);

	ClientData getClientData();

	ClientParameters getParameters();

	ClientMode getMode();

	void setMode(ClientMode pMode);

	CommandEndpoint getCommandEndpoint();

	FactoryManager getFactoryManager();

	IFactorySource getFactorySource();

	int getCurrentMouseButton();

	void setCurrentMouseButton(int currentMouseButton);
}
