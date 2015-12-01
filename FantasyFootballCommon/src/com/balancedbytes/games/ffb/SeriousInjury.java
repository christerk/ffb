package com.balancedbytes.games.ffb;

/**
 * @author Dominic Schabel
 */
public enum SeriousInjury implements IEnumWithName {

  BROKEN_RIBS("Broken Ribs (MNG)", "Broken Ribs (Miss next game)", "has broken some ribs (Miss next game)", "is recovering from broken ribs", false, null),
  GROIN_STRAIN("Groin Strain (MNG)", "Groin Strain (Miss next game)", "has got a groin strain (Miss next game)", "is recovering from a groin strain", false, null),
  GOUGED_EYE("Gouged Eye (MNG)", "Gouged Eye (Miss next game)", "has got a gouged eye (Miss next game)", "is recovering from a gouged eye", false, null),
  BROKEN_JAW("Broken Jaw (MNG)", "Broken Jaw (Miss next game)", "has got a broken jaw (Miss next game)", "is recovering from a broken jaw", false, null),
  FRACTURED_ARM("Fractured Arm (MNG)", "Fractured Arm (Miss next game)", "has got a fractured arm (Miss next game)", "is recovering from a fractured arm", false, null),
  FRACTURED_LEG("Fractured Leg (MNG)", "Fractured Leg (Miss next game)", "has got a fractured leg (Miss next game)", "is recovering from a fractured leg", false, null),
  SMASHED_HAND("Smashed Hand (MNG)", "Smashed Hand (Miss next game)", "has got a smashed hand (Miss next game)", "is recovering from a smashed hand", false, null),
  PINCHED_NERVE("Pinched Nerve (MNG)", "Pinched Nerve (Miss next game)", "has got a pinched nerve (Miss next game)", "is recovering from a pinched nerve", false, null),
  DAMAGED_BACK("Damaged Back (NI)", "Damaged Back (Niggling Injury)", "has got a damaged back (Niggling Injury)", "is recovering from a damaged back (Niggling Injury)", true, InjuryAttribute.NI),
  SMASHED_KNEE("Smashed Knee (NI)", "Smashed Knee (Niggling Injury)", "has got a smashed knee (Niggling Injury)", "is recovering from a smashed knee (Niggling Injury)", true, InjuryAttribute.NI),
  SMASHED_HIP("Smashed Hip (-MA)", "Smashed Hip (-1 MA)", "has got a smashed hip (-1 MA)", "is recovering from a smashed hip (-1 MA)", true, InjuryAttribute.MA),
  SMASHED_ANKLE("Smashed Ankle (-MA)", "Smashed Ankle (-1 MA)", "has got a smashed ankle (-1 MA)", "is recovering from a smashed ankle (-1 MA)", true, InjuryAttribute.MA),
  SERIOUS_CONCUSSION("Serious Concussion (-AV)", "Serious Concussion (-1 AV)", "has got a serious concussion (-1 AV)", "is recovering from a serious concussion (-1 AV)", true, InjuryAttribute.AV),
  FRACTURED_SKULL("Fractured Skull (-AV)", "Fractured Skull (-1 AV)", "has got a fractured skull (-1 AV)", "is recovering from a fractured skull (-1 AV)", true, InjuryAttribute.AV),
  BROKEN_NECK("Broken Neck (-AG)", "Broken Neck (-1 AG)", "has got a broken neck (-1 AG)", "is recovering from a broken neck (-1 AG)", true, InjuryAttribute.AG),
  SMASHED_COLLAR_BONE("Smashed Collar Bone (-ST)", "Smashed Collar Bone (-1 ST)", "has got a smashed collar bone (-1 ST)", "is recovering from a smashed collar bone (-1 ST)", true, InjuryAttribute.ST),
  DEAD("Dead (RIP)", "Dead (RIP)", "is dead", "is dead", true, null);

  private String fName;
  private String fButtonText;
  private String fDescription;
  private String fRecovery;
  private boolean fLasting;
  private InjuryAttribute fInjuryAttribute;

  private SeriousInjury(String pName, String pButtonText, String pDescription, String pRecovery, boolean pLasting, InjuryAttribute pInjuryAttribute) {
    fName = pName;
    fButtonText = pButtonText;
    fDescription = pDescription;
    fRecovery = pRecovery;
    fLasting = pLasting;
    fInjuryAttribute = pInjuryAttribute;
  }

  public String getName() {
    return fName;
  }
  
  public String getButtonText() {
	  return fButtonText;
  }

  public String getDescription() {
    return fDescription;
  }
  
  public String getRecovery() {
    return fRecovery;
  }
  
  public boolean isLasting() {
    return fLasting;
  }
  
  public InjuryAttribute getInjuryAttribute() {
    return fInjuryAttribute;
  }
  
}
