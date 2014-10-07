package com.balancedbytes.games.ffb.client;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilUrl;


/**
 * 
 * @author Kalimar
 */
public class IconCache {

  private static final String _ICONS_INI = "icons.ini";
  
  private Map<String,BufferedImage> fIconByKey;
  
  private Properties fIconUrlProperties;
    
  private Map<String,Integer> fCurrentIndexPerKey;
  
  private FantasyFootballClient fClient;
  
  public IconCache(FantasyFootballClient pClient) {
    fClient = pClient;
    fIconByKey = new HashMap<String,BufferedImage>();
    fCurrentIndexPerKey = new HashMap<String,Integer>();
  }
  
  public void init() {
    fIconUrlProperties = new Properties();
    try {
      BufferedInputStream propertyInputStream = new BufferedInputStream(new FileInputStream(_ICONS_INI));
      fIconUrlProperties.load(propertyInputStream);
      propertyInputStream.close();
    } catch (IOException pIoException) {
      // empty properties
    }
  }
  
  public boolean loadIconFromArchive(String pIconUrl) {
    if (!StringTool.isProvided(pIconUrl)) {
      return false;
    }
    String iconPath = fIconUrlProperties.getProperty(pIconUrl);
    if (iconPath == null) {
      iconPath = pIconUrl;
    }
    if (!iconPath.startsWith("/")) {
      iconPath = "/" + iconPath;
    }
    if (!iconPath.startsWith("/icons")) {
      iconPath = "/icons" + iconPath;
    }
    try {
      InputStream iconInputStream = getClass().getResourceAsStream(iconPath);
      if (iconInputStream != null) {
        BufferedImage icon = ImageIO.read(iconInputStream);
        iconInputStream.close();
        if (icon != null) {
          fIconByKey.put(pIconUrl, icon);
          return true;
        }
      }
    } catch (IOException ioe) {
      // just skip precaching
    }
    return false;
  }

  public BufferedImage getIconByProperty(String pIconProperty) {
    if (!StringTool.isProvided(pIconProperty)) {
      return null;
    }
    String iconUrl = getClient().getProperty(pIconProperty);
    BufferedImage icon = getIconByUrl(iconUrl);
    if ((icon == null) && loadIconFromArchive(iconUrl)) {
      icon = getIconByUrl(iconUrl);
    }
    return icon;
  }
  
  public BufferedImage getIconByUrl(String pIconUrl) {
    return fIconByKey.get(pIconUrl);
  }
  
  public void loadIconFromUrl(String pIconUrl) {
    URL fullIconUrl = null;
    try {
      fullIconUrl = new URL(pIconUrl);
      BufferedImage icon = ImageIO.read(fullIconUrl);
      fIconByKey.put(pIconUrl, icon);
    } catch (IOException ioe) {
      getClient().getUserInterface().getStatusReport().reportIconLoadFailure(fullIconUrl);
    } catch (Exception _) { // This should catch issues where the image is broken...
      getClient().getUserInterface().getStatusReport().reportIconLoadFailure(fullIconUrl);
    }
  }
  
  public String getNextProperty(String pIconProperty) {
    
    String nextKey = null;
    
    int index = 1;
    Integer currentIndex = fCurrentIndexPerKey.get(pIconProperty);
    if (currentIndex != null) {
      index = currentIndex.intValue() + 1;
    }
    fCurrentIndexPerKey.put(pIconProperty, index);
    
    StringBuilder indexedProperty = new StringBuilder();
    indexedProperty.append(pIconProperty);
    indexedProperty.append(".");
    if (index < 10) {
      indexedProperty.append("0");
    }
    indexedProperty.append(index);
    nextKey = indexedProperty.toString();
    
    if (!StringTool.isProvided(getClient().getProperty(nextKey)) && (index > 1)) {
      fCurrentIndexPerKey.remove(pIconProperty);
      nextKey = getNextProperty(pIconProperty);      
    }

    return nextKey;
    
  }
  
  public BufferedImage getIcon(PushbackSquare pPushbackSquare) {
    if (pPushbackSquare.isSelected()) {
      switch (pPushbackSquare.getDirection()) {
        case NORTH:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTH_SELECTED);
        case NORTHEAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHEAST_SELECTED);
        case EAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_EAST_SELECTED);
        case SOUTHEAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHEAST_SELECTED);
        case SOUTH:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTH_SELECTED);
        case SOUTHWEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHWEST_SELECTED);
        case WEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_WEST_SELECTED);
        case NORTHWEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHWEST_SELECTED);
        default:
          return null;  
      }
    } else {
      switch (pPushbackSquare.getDirection()) {
        case NORTH:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTH);
        case NORTHEAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHEAST);
        case EAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_EAST);
        case SOUTHEAST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHEAST);
        case SOUTH:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTH);
        case SOUTHWEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHWEST);
        case WEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_WEST);
        case NORTHWEST:
          return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHWEST);
        default:
          return null;  
      }
    }
  }
    
  public BufferedImage getIcon(BloodSpot pBloodspot) {
    String iconProperty = pBloodspot.getIconProperty();
    if (iconProperty == null) {
      // System.out.println(pBloodspot.getInjury());
      switch (pBloodspot.getInjury().getBase()) {
        case PlayerState.KNOCKED_OUT:
          iconProperty = getNextProperty(IIconProperty.BLOODSPOT_KO);
          break;
        case PlayerState.BADLY_HURT:
          iconProperty = getNextProperty(IIconProperty.BLOODSPOT_BH);
          break;
        case PlayerState.SERIOUS_INJURY:
          iconProperty = getNextProperty(IIconProperty.BLOODSPOT_SI);
          break;
        case PlayerState.RIP:
          iconProperty = getNextProperty(IIconProperty.BLOODSPOT_RIP);
          break;
        case PlayerState.HIT_BY_BOMB:
          iconProperty = getNextProperty(IIconProperty.BLOODSPOT_BOMB);
        	break;
        case PlayerState.HIT_BY_FIREBALL:
          iconProperty = IIconProperty.BLOODSPOT_FIREBALL;
        	break;
        case PlayerState.HIT_BY_LIGHTNING:
          iconProperty = IIconProperty.BLOODSPOT_LIGHTNING;
        	break;
        default:
          throw new IllegalArgumentException("Cannot get icon for Bloodspot with injury " + pBloodspot.getInjury() + ".");
      }
      pBloodspot.setIconProperty(iconProperty);
    }
    return getIconByProperty(iconProperty);
  }
  
  public BufferedImage getIcon(Weather pWeather) {
    if (pWeather == null) {
      return null;
    }
    Weather myWeather = pWeather;
    if (IClientPropertyValue.SETTING_PITCH_WEATHER_OFF.equals(getClient().getProperty(IClientProperty.SETTING_PITCH_WEATHER))) {
      myWeather = (pWeather == Weather.INTRO) ? Weather.INTRO : Weather.NICE;
    }
    String customPitchUrl = findCustomPitchUrl(getClient().getGame(), myWeather);
    if ((myWeather == Weather.INTRO)
      || !StringTool.isProvided(customPitchUrl)
      || IClientPropertyValue.SETTING_PITCH_DEFAULT.equals(getClient().getProperty(IClientProperty.SETTING_PITCH_CUSTOMIZATION))) {
      switch (myWeather) {
        case BLIZZARD:
          return getIconByProperty(IIconProperty.PITCH_BLIZZARD);
        case NICE:
          return getIconByProperty(IIconProperty.PITCH_NICE);
        case POURING_RAIN:
          return getIconByProperty(IIconProperty.PITCH_RAIN);
        case SWELTERING_HEAT:
          return getIconByProperty(IIconProperty.PITCH_HEAT);
        case VERY_SUNNY:
          return getIconByProperty(IIconProperty.PITCH_SUNNY);
        default:
          return getIconByProperty(IIconProperty.PITCH_INTRO);
      }
    }
    return getIconByUrl(customPitchUrl);
  }
    
  public BufferedImage getIcon(DiceDecoration pDiceDecoration) {
    String iconProperty = null;
    switch (pDiceDecoration.getNrOfDice()) {
      case -3:
        iconProperty = IIconProperty.DECORATION_DICE_3_AGAINST;
        break;
      case -2:
        iconProperty = IIconProperty.DECORATION_DICE_2_AGAINST;
        break;
      case 1:
        iconProperty = IIconProperty.DECORATION_DICE_1;
        break;
      case 2:
        iconProperty = IIconProperty.DECORATION_DICE_2;
        break;
      case 3:
        iconProperty = IIconProperty.DECORATION_DICE_3;
        break;
    }
    if (iconProperty != null) {
    	return getIconByProperty(iconProperty);
    } else {
    	return null;
    }
  }
  
  public BufferedImage getDiceIcon(int pRoll) {
    switch (pRoll) {
      case 1:
        return getIconByProperty(IIconProperty.DICE_BLOCK_1);
      case 2:
        return getIconByProperty(IIconProperty.DICE_BLOCK_2);
      case 3:
        return getIconByProperty(IIconProperty.DICE_BLOCK_3);
      case 4:
        return getIconByProperty(IIconProperty.DICE_BLOCK_4);
      case 5:
        return getIconByProperty(IIconProperty.DICE_BLOCK_5);
      case 6:
        return getIconByProperty(IIconProperty.DICE_BLOCK_6);
    }
    return null;
  }
  
  public static String findTeamLogoUrl(Team pTeam) {
    String iconUrl = null;
    if ((pTeam != null) && StringTool.isProvided(pTeam.getLogoUrl())) {
      if (StringTool.isProvided(pTeam.getBaseIconPath())) {
        iconUrl = UtilUrl.createUrl(pTeam.getBaseIconPath(), pTeam.getLogoUrl());
      } else {
        iconUrl = pTeam.getLogoUrl();
      }
    }
    return iconUrl;
  }
  
  public static String findCustomPitchUrl(Game pGame, Weather pWeather) {
    if ((pGame == null) || (pWeather == null)) {
      return null;
    }
    IGameOption pitchUrlOption = null;
    switch (pWeather) {
      case BLIZZARD:
        pitchUrlOption = pGame.getOptions().getOption(GameOptionId.PITCH_URL_BLIZZARD);
        break;
      case NICE:
        pitchUrlOption = pGame.getOptions().getOption(GameOptionId.PITCH_URL_NICE);
        break;
      case POURING_RAIN:
        pitchUrlOption = pGame.getOptions().getOption(GameOptionId.PITCH_URL_RAIN);
        break;
      case SWELTERING_HEAT:
        pitchUrlOption = pGame.getOptions().getOption(GameOptionId.PITCH_URL_HEAT);
        break;
      case VERY_SUNNY:
        pitchUrlOption = pGame.getOptions().getOption(GameOptionId.PITCH_URL_SUNNY);
        break;
      default:
        // PitchUrlOption remains null
        break;
    }
    if (pitchUrlOption != null) {
      return pitchUrlOption.getValueAsString();
    }
    return null;
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
    
}