package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.bytearray.IByteArraySerializable;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportList;

/**
 * 
 * @author Kalimar
 */
public class StepResult implements IByteArraySerializable {
	
	private StepAction fNextAction;
	
	private String fNextActionParameter;
  
  private ReportList fReportList;
  
  private Animation fAnimation;
  
  private Sound fSound;
  
  private boolean fSynchronize;
  
  public StepResult() {
    setNextAction(StepAction.CONTINUE);
    setSynchronize(true);
  	reset();
  }
  
  public void reset() {
    setReportList(new ReportList());
    setAnimation(null);
    setSound(null);
  }
  
  public void addReport(IReport pReport) {
    if (pReport != null) {
      fReportList.add(pReport);
    }
  }
  
  private void setReportList(ReportList pReportList) {
  	fReportList = pReportList;
  }
  
  public ReportList getReportList() {
    return fReportList;
  }
  
  public Animation getAnimation() {
    return fAnimation;
  }
  
  public void setAnimation(Animation pAnimation) {
    fAnimation = pAnimation;
  }
  
  public Sound getSound() {
    return fSound;
  }
  
  public void setSound(Sound pSound) {
    fSound = pSound;
  }
  
  public StepAction getNextAction() {
		return fNextAction;
	}
  
  public void setNextAction(StepAction pNextAction) {
		fNextAction = pNextAction;
	}
  
  public void setNextAction(StepAction pNextAction, String pNextActionParameter) {
		setNextAction(pNextAction);
		setNextActionParameter(pNextActionParameter);
	}

  public String getNextActionParameter() {
		return fNextActionParameter;
	}
  
  public void setNextActionParameter(String pNextActionParameter) {
		fNextActionParameter = pNextActionParameter;
	}
  
  public void setSynchronize(boolean pSynchronize) {
		fSynchronize = pSynchronize;
	}
  
  public boolean isSynchronize() {
		return fSynchronize;
	}
  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  public void addTo(ByteList pByteList) {
  	pByteList.addSmallInt(getByteArraySerializationVersion());
  	pByteList.addByte((byte) ((getNextAction() != null) ? getNextAction().getId() : 0));
  	pByteList.addString(getNextActionParameter());
  	getReportList().addTo(pByteList);
  	if (getAnimation() != null) {
  		pByteList.addBoolean(true);
  		getAnimation().addTo(pByteList);
  	} else {
  		pByteList.addBoolean(false);
  	}
  	pByteList.addByte((byte) ((getSound() != null) ? getSound().getId() : 0));
  	pByteList.addBoolean(isSynchronize());
  }
  
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
  	setNextAction(StepAction.fromId(pByteArray.getByte()));
  	setNextActionParameter(pByteArray.getString());
  	setReportList(new ReportList());
  	getReportList().initFrom(pByteArray);
  	if (pByteArray.getBoolean()) {
  		setAnimation(new Animation());
  		getAnimation().initFrom(pByteArray);
  	}
  	setSound(Sound.fromId(pByteArray.getByte()));
  	setSynchronize(pByteArray.getBoolean());
  	return byteArraySerializationVersion;
  }
  
}
