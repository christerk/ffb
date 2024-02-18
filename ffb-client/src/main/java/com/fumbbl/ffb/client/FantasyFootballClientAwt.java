package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryManager;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.dialog.DialogAboutHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.UIManager;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class FantasyFootballClientAwt extends FantasyFootballClient {
	private Game fGame;
	private final UserInterface fUserInterface;
	private final ClientCommunication fCommunication;
	private Properties fProperties;
	private final ActionKeyBindings fActionKeyBindings;
	private final ClientReplayer fReplayer;
	private final ClientParameters fParameters;
	private ClientMode fMode;


	private final transient ClientData fClientData;

	private final FactoryManager factoryManager;
	@SuppressWarnings("rawtypes")
	private final Map<Factory, INamedObjectFactory> factories;

	private transient int currentMouseButton;

	private Preferences prefs;

	public FantasyFootballClientAwt(ClientParameters pParameters) throws IOException {
		super(pParameters.getCoach());

		factoryManager = new FactoryManager();
		factories = factoryManager.getFactoriesForContext(getContext());

		fParameters = pParameters;
		setMode(fParameters.getMode());

		fClientData = new ClientData();

		fActionKeyBindings = new ActionKeyBindings(this);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		} catch (Exception all) {
			all.printStackTrace();
		}

		setGame(new Game(getFactorySource(), factoryManager));

		fReplayer = new ClientReplayer(this);

		fUserInterface = new UserInterface(this);
		fUserInterface.refreshSideBars();
		fUserInterface.getScoreBar().refresh();

		fCommunication = new ClientCommunication(this);
		Thread fCommunicationThread = new Thread(fCommunication);
		fCommunicationThread.start();

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
		synchronized (this) {
			this.notify();
		}
	}

	public void showUserInterface() {
		getUserInterface().getFieldComponent().getLayerField().drawWeather(Weather.INTRO);
		getUserInterface().getFieldComponent().refresh();
		getUserInterface().setVisible(true);

		DialogAboutHandler aboutDialogHandler = new DialogAboutHandler(this);
		aboutDialogHandler.showDialog();
	}

	public void dialogClosed(IDialog pDialog) {
		pDialog.hideDialog();
	}

	public void exitClient() {
		closeConnection();
		getUserInterface().setVisible(false);
		System.exit(0);
	}

	public String getProperty(CommonProperty property) {
		return getProperty(property.getKey());
	}

	public String getProperty(String pProperty) {
		return fProperties.getProperty(pProperty);
	}

	public void setProperty(CommonProperty pProperty, String pValue) {
		setProperty(pProperty.getKey(), pValue);
	}

	public void setProperty(String pProperty, String pValue) {
		if ((fProperties == null) || (pProperty == null) || (pValue == null)) {
			return;
		}
		fProperties.setProperty(pProperty, pValue);
	}

	protected void initUI() {
		getUserInterface().init(getGame().getOptions());
	}

	public void updateLocalPropertiesStore() {
		try {
			prefs.clear();
			for (CommonProperty property : CommonProperty._SAVED_USER_SETTINGS) {
				String key = property.getKey();
				String value = getProperty(property);
				if (StringTool.isProvided(key) && StringTool.isProvided(value)) {
					prefs.put(key, value);
				}
			}

		} catch (BackingStoreException e) {
			logError(0, "Could not update locally stored properties: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		try {
			ClientParameters parameters = new ClientParameters();
			parameters.initFrom(args);
			if (!parameters.validate()) {
				System.out.println(ClientParameters.USAGE);
				return;
			}
			FantasyFootballClient client = new FantasyFootballClientAwt(parameters);
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
		try (InputStream propertyInputStream = getClass().getResourceAsStream("/client.ini")) {
			fProperties.load(propertyInputStream);
		}
	}

	public List<CommonProperty> getLocallyStoredPropertyKeys() {
		String property = getProperty(CommonProperty.SETTING_LOCAL_SETTINGS);
		if (!StringTool.isProvided(property)) {
			return Collections.emptyList();
		}

		return Arrays.stream(property.split(","))
			.map(CommonProperty::forKey)
			.filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setLocallyStoredPropertyKeys(List<CommonProperty> properties) {
		setProperty(CommonProperty.SETTING_LOCAL_SETTINGS, properties.stream().map(CommonProperty::getKey).collect(Collectors.joining(",")));
	}

	@Override
	public void loadLocallyStoredProperties(String coach) {
		try {
			prefs = Preferences.userRoot().node("FfbUserSettings_" + coach);
			Arrays.stream(prefs.keys()).map(CommonProperty::forKey)
				.filter(Objects::nonNull)
				.forEach(property -> setProperty(property, prefs.get(property.getKey(), "")));
		} catch (BackingStoreException e) {
			logDebug(0, "Could not load Preferences: " + e.getMessage());
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


	public FactoryManager getFactoryManager() {
		return factoryManager;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public <T extends INamedObjectFactory> T getFactory(Factory factory) {
		return (T) factories.get(factory);
	}

	@Override
	public void logError(long gameId, String message) {
		System.err.println(gameId + ": " + message);
	}

	@Override
	public void logDebug(long gameId, String message) {
		System.out.println(gameId + ": " + message);
	}

	@Override
	public void logWithOutGameId(Throwable throwable) {
		throwable.printStackTrace(System.err);
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

	public int getCurrentMouseButton() {
		return currentMouseButton;
	}

	public void setCurrentMouseButton(int currentMouseButton) {
		this.currentMouseButton = currentMouseButton;
	}

	@Override
	protected void postConnect(boolean connectionEstablished) {
		getUserInterface().getStatusReport().reportConnectionEstablished(connectionEstablished);
	}

	@Override
	protected void preConnect() {
		getUserInterface().getStatusReport().reportVersion();
		try {
			getUserInterface().getStatusReport().reportConnecting(getServerHost(), getServerPort());
		} catch (UnknownHostException pUnknownHostException) {
			throw new FantasyFootballException(pUnknownHostException);
		}
	}
}
