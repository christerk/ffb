package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class SoundIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public SoundId forName(String pName) {
    for (SoundId sound : SoundId.values()) {
      if (sound.getName().equalsIgnoreCase(pName)) {
        return sound;
      }
    }
    return null;
  }

  public SoundId forId(int pId) {
    for (SoundId sound : SoundId.values()) {
      if (sound.getId() == pId) {
        return sound;
      }
    }
    return null;
  }

}
