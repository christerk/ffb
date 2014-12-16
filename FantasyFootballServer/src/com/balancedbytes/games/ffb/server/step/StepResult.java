package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.SoundIdFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.IByteArrayReadable;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.report.IReport;
import com.balancedbytes.games.ffb.report.ReportList;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class StepResult implements IByteArrayReadable, IJsonSerializable {
	
	private StepAction fNextAction;
	private String fNextActionParameter;
  private ReportList fReportList;
  private Animation fAnimation;
  private SoundId fSound;
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
  
  public SoundId getSound() {
    return fSound;
  }
  
  public void setSound(SoundId pSound) {
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
  
  // ByteArray serialization
  
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = pByteArray.getSmallInt();
  	setNextAction(new StepActionFactory().forId(pByteArray.getByte()));
  	setNextActionParameter(pByteArray.getString());
  	setReportList(new ReportList());
  	getReportList().initFrom(pByteArray);
  	if (pByteArray.getBoolean()) {
  		setAnimation(new Animation());
  		getAnimation().initFrom(pByteArray);
  	}
  	setSound(new SoundIdFactory().forId(pByteArray.getByte()));
  	setSynchronize(pByteArray.getBoolean());
  	return byteArraySerializationVersion;
  }
  
  // JSON serialization
  
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IServerJsonOption.NEXT_ACTION.addTo(jsonObject, fNextAction);
    IServerJsonOption.NEXT_ACTION_PARAMETER.addTo(jsonObject, fNextActionParameter);
    IServerJsonOption.REPORT_LIST.addTo(jsonObject, fReportList.toJsonValue());
    if (fAnimation != null) {
      IServerJsonOption.ANIMATION.addTo(jsonObject, fAnimation.toJsonValue());
    }
    IServerJsonOption.SOUND.addTo(jsonObject, fSound);
    IServerJsonOption.SYNCHRONIZE.addTo(jsonObject, fSynchronize);
    return jsonObject;
  }
  
  public StepResult initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    fNextAction = (StepAction) IServerJsonOption.NEXT_ACTION.getFrom(jsonObject);
    fNextActionParameter = IServerJsonOption.NEXT_ACTION_PARAMETER.getFrom(jsonObject);
    fReportList.clear();
    JsonObject reportListObject = IServerJsonOption.REPORT_LIST.getFrom(jsonObject);
    if (reportListObject != null) {
      fReportList.initFrom(reportListObject); 
    }
    fAnimation = null;
    JsonObject animationObject = IServerJsonOption.ANIMATION.getFrom(jsonObject);
    if (animationObject != null) {
      fAnimation = new Animation().initFrom(animationObject);
    }
    fSound = (SoundId) IServerJsonOption.SOUND.getFrom(jsonObject);
    fSynchronize = IServerJsonOption.SYNCHRONIZE.getFrom(jsonObject);
    return this;
  }
  
}
