package com.fumbbl.ffb.client;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.IClientProperty;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.dialog.DialogAboutHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.ClientStateFactoryAwt;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * @author Kalimar
 */
public class FantasyFootballClientAwt extends FantasyFootballClient {
	private final UserInterface fUserInterface;
	private Properties fProperties;
	private final ActionKeyBindings fActionKeyBindings;
	private final ClientReplayer fReplayer;

	private transient int currentMouseButton;

	private Preferences prefs;

	public FantasyFootballClientAwt(ClientParameters pParameters) throws IOException {
		super(pParameters);
		fActionKeyBindings = new ActionKeyBindings(this);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		} catch (Exception e) {
			logWithOutGameId(e);
		}

		fReplayer = new ClientReplayer(this);

		fUserInterface = new UserInterface(this);
		fUserInterface.refreshSideBars();
		fUserInterface.getScoreBar().refresh();
		setClientStateFactory();
	}

	@Override
	protected void setClientStateFactory() {
		fStateFactory = new ClientStateFactoryAwt(this);
	}

	public UserInterface getUserInterface() {
		return fUserInterface;
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

	public void exit() {
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

	protected void clearPrefs() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			logError(0, "Could not clear locally stored properties: " + e.getMessage());
		}
	}

	protected void setPref(String key, String value) {
		prefs.put(key, value);
	}

	public static void main(String[] args) {

		try {
			ClientParameters parameters = ClientParameters.createValidParams(args);
			if (parameters == null) {
				System.out.println(ClientParameters.USAGE);
				return;
			}
			FantasyFootballClientAwt client = new FantasyFootballClientAwt(parameters);
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

	@Override
	public ClientStateAwt<? extends LogicModule> getClientState() {
		return (ClientStateAwt<? extends LogicModule>) super.getClientState();
	}
}
