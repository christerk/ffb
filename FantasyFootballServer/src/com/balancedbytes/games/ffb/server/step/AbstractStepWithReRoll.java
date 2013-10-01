package com.balancedbytes.games.ffb.server.step;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseReRoll;
import com.balancedbytes.games.ffb.server.GameState;

/**
 * 
 * @author Kalimar
 */
public abstract class AbstractStepWithReRoll extends AbstractStep {
	
  private ReRolledAction fReRolledAction;
  private ReRollSource fReRollSource;

	protected AbstractStepWithReRoll(GameState pGameState) {
		super(pGameState);
	}

	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
				case CLIENT_USE_RE_ROLL:
		      ClientCommandUseReRoll useReRollCommand = (ClientCommandUseReRoll) pNetCommand;
		      setReRolledAction(useReRollCommand.getReRolledAction());
		      setReRollSource(useReRollCommand.getReRollSource());
		      commandStatus = StepCommandStatus.EXECUTE_STEP;
		      break;
	      default:
	      	break;
			}
		}
		return commandStatus;
	}
	
	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}
	
	protected void setReRolledAction(ReRolledAction pReRolledAction) {
		fReRolledAction = pReRolledAction;
	}
	
	public ReRollSource getReRollSource() {
		return fReRollSource;
	}
	
	protected void setReRollSource(ReRollSource pReRollSource) {
		fReRollSource = pReRollSource;
	}
	
	@Override
	public void addTo(ByteList pByteList) {
		super.addTo(pByteList);
		pByteList.addByte((byte) ((fReRolledAction != null) ? fReRolledAction.getId() : 0));
		pByteList.addByte((byte) ((fReRollSource != null) ? fReRollSource.getId() : 0));
	}
	
	@Override
	public int initFrom(ByteArray pByteArray) {
		int byteArraySerializationVersion = super.initFrom(pByteArray);
		fReRolledAction = ReRolledAction.fromId(pByteArray.getByte());
		fReRollSource = ReRollSource.fromId(pByteArray.getByte());
		return byteArraySerializationVersion;
	}

}
