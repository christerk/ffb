package com.balancedbytes.games.ffb.server.step.phase.inducement;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.InducementPhase;
import com.balancedbytes.games.ffb.InducementSet;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogUseInducementParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.commands.ClientCommandUseInducement;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.SequenceGenerator;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;
import com.balancedbytes.games.ffb.server.step.StepException;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameter;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.StepParameterSet;
import com.balancedbytes.games.ffb.server.step.UtilSteps;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 * Step to init the inducement sequence.
 * 
 * Needs to be initialized with stepParameter HOME_TEAM.
 * Needs to be initialized with stepParameter INDUCEMENT_PHASE.
 * 
 * Sets stepParameter HOME_TEAM for all steps on the stack.
 * Sets stepParameter INDUCEMENT_PHASE for all steps on the stack.
 * Sets stepParameter END_INDUCEMENT_PHASE for all steps on the stack.
 *
 * @author Kalimar
 */
public final class StepInitInducement extends AbstractStep {
	
	protected InducementPhase fInducementPhase;
	protected boolean fHomeTeam;
	protected InducementType fInducement;
	protected Card fCard;
	
	private transient boolean fEndInducementPhase;
	private transient boolean fTouchdownOrEndOfHalf;
	
	public StepInitInducement(GameState pGameState) {
		super(pGameState);
	}
	
	public StepId getId() {
		return StepId.INIT_INDUCEMENT;
	}
	
  @Override
  public void init(StepParameterSet pParameterSet) {
  	if (pParameterSet != null) {
  		for (StepParameter parameter : pParameterSet.values()) {
  			switch (parameter.getKey()) {
  				// mandatory
  				case INDUCEMENT_PHASE:
  					fInducementPhase = (InducementPhase) parameter.getValue();
  					break;
  				// mandatory
  				case HOME_TEAM:
  					fHomeTeam = (parameter.getValue() != null) ? (Boolean) parameter.getValue() : false;
  					break;
					default:
						break;
  			}
  		}
  	}
  	if (fInducementPhase == null) {
			throw new StepException("StepParameter " + StepParameterKey.INDUCEMENT_PHASE + " is not initialized.");
  	}
  }
	
	@Override
	public void start() {
		super.start();
		executeStep();
	}
	
	@Override
	public StepCommandStatus handleNetCommand(NetCommand pNetCommand) {
		StepCommandStatus commandStatus = super.handleNetCommand(pNetCommand);
		if (commandStatus == StepCommandStatus.UNHANDLED_COMMAND) {
			switch (pNetCommand.getId()) {
	      case CLIENT_USE_INDUCEMENT:
	      	ClientCommandUseInducement useInducementCommand = (ClientCommandUseInducement) pNetCommand;
	      	fInducement = useInducementCommand.getInducementType();
	      	fCard = useInducementCommand.getCard();
	      	fEndInducementPhase = ((fInducement == null) && (fCard == null)); 
          commandStatus = StepCommandStatus.EXECUTE_STEP;
	        break;
        default:
        	break;
			}
		}
		if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
			executeStep();
		}
		return commandStatus;
	}

  private void executeStep() {
  	Game game = getGameState().getGame();
		if (fEndInducementPhase) {
			leaveStep(true);
		} else if ((fCard == null) && (fInducement == null)) {
			fTouchdownOrEndOfHalf = UtilSteps.checkTouchdown(getGameState());
  		Card[] playableCards = findPlayableCards();
  		InducementType[] useableInducements = findUseableInducements();
  		if (ArrayTool.isProvided(useableInducements) || ArrayTool.isProvided(playableCards)) {
  			String teamId = fHomeTeam ? game.getTeamHome().getId() : game.getTeamAway().getId();
  			game.setDialogParameter(new DialogUseInducementParameter(teamId, useableInducements, playableCards));
  		} else {
				leaveStep(true);
  		}
  	} else if (InducementType.WIZARD == fInducement) {
			SequenceGenerator.getInstance().pushWizardSequence(getGameState());
			leaveStep(false);
  	} else if (fCard != null) {
  		SequenceGenerator.getInstance().pushCardSequence(getGameState(), fCard, fHomeTeam);
			leaveStep(false);
		} else {
			leaveStep(true);
		}
  }
  
  private void leaveStep(boolean pEndInducementPhase) {
  	publishParameter(new StepParameter(StepParameterKey.END_INDUCEMENT_PHASE, pEndInducementPhase));
		publishParameter(new StepParameter(StepParameterKey.HOME_TEAM, fHomeTeam));
		publishParameter(new StepParameter(StepParameterKey.INDUCEMENT_PHASE, fInducementPhase));
		getResult().setNextAction(StepAction.NEXT_STEP);
  }
  
  private InducementType[] findUseableInducements() {
		Set<InducementType> useableInducements = new HashSet<InducementType>();
  	Game game = getGameState().getGame();
		TurnData turnData = fHomeTeam ? game.getTurnDataHome() : game.getTurnDataAway();
		if (InducementPhase.END_OF_OWN_TURN == fInducementPhase) {
			if (!fTouchdownOrEndOfHalf && (turnData.getInducementSet().hasUsesLeft(InducementType.WIZARD))) {
				useableInducements.add(InducementType.WIZARD);
			}
		}
		if (InducementPhase.START_OF_OWN_TURN == fInducementPhase) {
			if (turnData.getInducementSet().hasUsesLeft(InducementType.WIZARD)) {
				useableInducements.add(InducementType.WIZARD);
			}
		}
		return useableInducements.toArray(new InducementType[useableInducements.size()]);
  }
  
	private Card[] findPlayableCards() {
  	Game game = getGameState().getGame();
		Set<Card> playableCards = new HashSet<Card>();
		InducementSet inducementSet = fHomeTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		for (Card card : inducementSet.getAvailableCards()) {
			boolean playable = (!card.getTarget().isPlayedOnPlayer() || ArrayTool.isProvided(UtilCards.findAllowedPlayersForCard(game, card)));
			for (InducementPhase phase : card.getPhases()) {
				if (playable && (phase == fInducementPhase) && (!fTouchdownOrEndOfHalf || (phase != InducementPhase.END_OF_OWN_TURN))) {
					playableCards.add(card);
				}
			}
		}
		return playableCards.toArray(new Card[playableCards.size()]);
	}
	  
  public int getByteArraySerializationVersion() {
  	return 1;
  }
  
  @Override
  public void addTo(ByteList pByteList) {
  	super.addTo(pByteList);
  	pByteList.addByte((byte) ((fInducementPhase != null) ? fInducementPhase.getId() : 0));
  	pByteList.addBoolean(fHomeTeam);
  	pByteList.addByte((byte) ((fInducement != null) ? fInducement.getId() : 0));
  	pByteList.addSmallInt((fCard != null) ? fCard.getId() : 0);
  }
  
  @Override
  public int initFrom(ByteArray pByteArray) {
  	int byteArraySerializationVersion = super.initFrom(pByteArray);
  	fInducementPhase = InducementPhase.fromId(pByteArray.getByte());
  	fHomeTeam = pByteArray.getBoolean();
  	fInducement = new InducementTypeFactory().forId(pByteArray.getByte());
  	fCard = new CardFactory().forId(pByteArray.getSmallInt());
  	return byteArraySerializationVersion;
  }

}
