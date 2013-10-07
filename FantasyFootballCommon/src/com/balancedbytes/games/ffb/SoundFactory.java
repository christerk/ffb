package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class SoundFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public Sound forName(String pName) {
    for (Sound sound : Sound.values()) {
      if (sound.getName().equalsIgnoreCase(pName)) {
        return sound;
      }
    }
    return null;
  }

  public Sound forId(int pId) {
    for (Sound sound : Sound.values()) {
      if (sound.getId() == pId) {
        return sound;
      }
    }
    return null;
  }

}
