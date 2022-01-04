package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepRecoverFromGaze extends AbstractStep {
    public StepRecoverFromGaze(GameState pGameState) {
        super(pGameState);
    }

    @Override
    public StepId getId() {
        return StepId.RECOVER_FROM_GAZE;
    }

    @Override
    public void start() {
        super.start();
        Player<?> player = getGameState().getGame().getActingPlayer().getPlayer();
        PlayerState playerState = getGameState().getGame().getFieldModel().getPlayerState(player);
        getGameState().getGame().getFieldModel().setPlayerState(player, playerState.changeHypnotized(false));
        getResult().setNextAction(StepAction.NEXT_STEP);
    }
}
