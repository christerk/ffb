package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IEnumWithIdFactory;
import com.balancedbytes.games.ffb.IEnumWithNameFactory;

/**
 * 
 * @author Kalimar
 */
public class DialogIdFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public DialogId forId(int pId) {
    for (DialogId dialogId : DialogId.values()) {
      if (dialogId.getId() == pId) {
        return dialogId;
      }
    }
    return null;
  }

  public DialogId forName(String pName) {
    for (DialogId dialogId : DialogId.values()) {
      if (dialogId.getName().equalsIgnoreCase(pName)) {
        return dialogId;
      }
    }
    return null;
  }

}
