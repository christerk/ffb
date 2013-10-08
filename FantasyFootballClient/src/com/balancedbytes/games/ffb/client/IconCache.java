package com.balancedbytes.games.ffb.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilUrl;


/**
 * 
 * @author Kalimar
 */
public class IconCache {
  
  private Map<String,BufferedImage> fIconByProperty;
    
  private Map<String,Integer> fCurrentIndexPerKey;
  
  private Map<String,BufferedImage> fIconByUrl;
  
  private FantasyFootballClient fClient;
  
  public IconCache(FantasyFootballClient pClient) {
    fClient = pClient;
    fIconByProperty = new HashMap<String,BufferedImage>();
    fIconByUrl = new HashMap<String,BufferedImage>();
    fCurrentIndexPerKey = new HashMap<String,Integer>();
  }
  
  public boolean loadIconFromArchive(String pIconUrl) {
    String iconBaseUrl = getClient().getProperty(IClientProperty.ICON_BASE_URL);
    String iconBasePath = getClient().getProperty(IClientProperty.ICON_BASE_PATH);
    if (StringTool.isProvided(iconBasePath) && StringTool.isProvided(iconBaseUrl) && StringTool.isProvided(pIconUrl) && pIconUrl.startsWith(iconBaseUrl)) {
      try {
        InputStream iconInputStream = getClass().getResourceAsStream(iconBasePath + pIconUrl.substring(iconBaseUrl.length()));
        if (iconInputStream != null) {
          BufferedImage icon = ImageIO.read(iconInputStream);
          iconInputStream.close();
          if (icon != null) {
            fIconByUrl.put(pIconUrl, icon);
            return true;
          }
        }
      } catch (IOException ioe) {
        // just skip precaching
      }
    }
    return false;
  }

  public BufferedImage getIconByProperty(String pIconProperty) {
    BufferedImage icon = fIconByProperty.get(pIconProperty);
    if (icon == null) {
      String iconFilename = getClient().getProperty(pIconProperty);
      if (iconFilename != null) {
//      	System.out.println(iconFilename);
        try {
          // File iconFile = new File(ICON_DIRECTORY, iconFilename);
          InputStream iconInputStream = getClass().getResourceAsStream("/icons/" + iconFilename);
          // icon = ImageIO.read(iconFile);
          icon = ImageIO.read(iconInputStream);
          fIconByProperty.put(pIconProperty, icon);
          iconInputStream.close();
        } catch (IllegalArgumentException iae) {
          throw new FantasyFootballException("Error reading icon property " + pIconProperty, iae);
        } catch (IOException ioe) {
          throw new FantasyFootballException("Error reading icon property " + pIconProperty, ioe);
        }
      }
    }
    return icon;
  }
  
  public BufferedImage getIconByUrl(String pIconUrl) {
    return fIconByUrl.get(pIconUrl);
  }
  
  public void loadIconFromUrl(String pIconUrl) {
    URL fullIconUrl = null;
    try {
      fullIconUrl = new URL(pIconUrl);
      BufferedImage icon = ImageIO.read(fullIconUrl);
      fIconByUrl.put(pIconUrl, icon);
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
    String iconProperty = null;
    switch (pWeather) {
      case SWELTERING_HEAT:
        iconProperty = IIconProperty.FIELD_HEAT;
        break;
      case VERY_SUNNY:
        iconProperty = IIconProperty.FIELD_SUNNY;
        break;
      case NICE:
        iconProperty = IIconProperty.FIELD_GOOD;
        break;
      case POURING_RAIN:
        iconProperty = IIconProperty.FIELD_RAIN;
        break;
      case BLIZZARD:
        iconProperty = IIconProperty.FIELD_BLIZZARD;
        break;
      case INTRO:
        iconProperty = IIconProperty.FIELD_INTRO;
        break;
    }
    return getIconByProperty(iconProperty);
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
  
  public static String getTeamLogoUrl(Team pTeam) {
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
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
    
}