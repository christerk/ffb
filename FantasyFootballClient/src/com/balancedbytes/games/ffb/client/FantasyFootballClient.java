package com.balancedbytes.games.ffb.client;

import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.swing.UIManager;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.dialog.DialogAboutHandler;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.client.handler.ClientCommandHandlerFactory;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.client.net.ClientPingTask;
import com.balancedbytes.games.ffb.client.net.CommandSocket;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.client.state.ClientStateFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.IConnectionListener;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FantasyFootballClient implements IConnectionListener, IDialogCloseListener {

  public static final String CLIENT_VERSION = "1.2.5";
  public static final String SERVER_VERSION = "1.2.5";
  
  private Game fGame;
  private UserInterface fUserInterface;
  private ClientCommunication fCommunication;
  private Thread fCommunicationThread;
  private Timer fPingTimer;
  private Timer fTurnTimer;
  private TurnTimerTask fTurnTimerTask;
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
  
  private WebSocketClientFactory fWebSocketClientFactory;
  private WebSocketClient fWebSocketClient;
  private CommandSocket fCommandSocket;
  
  private transient ClientData fClientData;

  public FantasyFootballClient(ClientParameters pParameters) throws IOException {

  	fParameters = pParameters;
    setMode(fParameters.getMode());

    fClientData = new ClientData();
    
    loadProperties();

    fActionKeyBindings = new ActionKeyBindings(this);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
      UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));      
    } catch (Exception all) {
      all.printStackTrace();
    }

    setGame(new Game());
    fStateFactory = new ClientStateFactory(this);
    fCommandHandlerFactory = new ClientCommandHandlerFactory(this);
    
    fReplayer = new ClientReplayer(this);

    fUserInterface = new UserInterface(this);
    fUserInterface.refreshSideBars();
    fUserInterface.getScoreBar().refresh();
    
    fWebSocketClientFactory = new WebSocketClientFactory();
    
    fCommandSocket = new CommandSocket(this);
    
    fCommunication = new ClientCommunication(this);
    fCommunicationThread = new Thread(fCommunication);
    fCommunicationThread.start();

    fPingTimer = new Timer(true);
    fTurnTimer = new Timer(true);
    
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
    startClient();
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
      
      fWebSocketClientFactory.start();
      
      URI uri = new URI("ws", null, getServerHost().getCanonicalHostName(), getServerPort(), "/command", null, null);
      fWebSocketClient = fWebSocketClientFactory.newWebSocketClient();
      WebSocket.Connection connection = fWebSocketClient.open(uri, fCommandSocket).get();
      connectionEstablished = (connection != null);
      
    } catch (Exception pAnyException) {
      pAnyException.printStackTrace();
    }

    getUserInterface().getStatusReport().reportConnectionEstablished(connectionEstablished);

    if (ClientMode.REPLAY != getMode()) {
      fTurnTimerTask = new TurnTimerTask(this);
      fTurnTimer.scheduleAtFixedRate(fTurnTimerTask, 0, 1000);
    }
      
    updateClientState();
    
  }
  
  public void stopClient() {
    fPingTimer = null;
    fTurnTimer = null;
    try {
      fWebSocketClientFactory.stop();
      fCommandSocket.awaitClose(10, TimeUnit.SECONDS);
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
    // System.out.println("setProperty(" + pProperty + "=" + pValue + ")");
    
    if (IClientProperty.CLIENT_PING_INTERVAL.equals(pProperty) && StringTool.isProvided(pValue)) {
      int pingInterval = Integer.parseInt(pValue);
      String pingMaxDelayProperty = getProperty(IClientProperty.CLIENT_PING_MAX_DELAY);
      int pingMaxDelay = StringTool.isProvided(pingMaxDelayProperty) ? Integer.parseInt(pingMaxDelayProperty) : 0;
      fClientPingTask = new ClientPingTask(this, pingMaxDelay);
      fPingTimer.schedule(fClientPingTask, 0, pingInterval);
    }

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
    InputStream propertyInputStream = getClass().getResourceAsStream("/client.ini");
    fProperties.load(propertyInputStream);
    propertyInputStream.close();
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
	  return InetAddress.getByName(getProperty(IClientProperty.SERVER_HOST));
  }
  
  public ClientMode getMode() {
		return fMode;
	}
  
  public void setMode(ClientMode pMode) {
		fMode = pMode;
	}
  
  public CommandSocket getCommandSocket() {
    return fCommandSocket;
  }
  
}
