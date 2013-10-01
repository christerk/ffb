package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;

/**
 * 
 * @author Kalimar
 */
public class DialogWizardSpellParameter extends DialogWithoutParameter {

  public DialogWizardSpellParameter() {
    super();
  }

  public DialogId getId() {
    return DialogId.WIZARD_SPELL;
  }
  
  // transformation
  
  public IDialogParameter transform() {
    return new DialogWizardSpellParameter();
  }
  
  // ByteArray serialization
  
  public int getByteArraySerializationVersion() {
    return 1;
  }
  
}
