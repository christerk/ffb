package com.fumbbl.ffb.server.step.bb2025.kickoff;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.MechanicsFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.factory.SequenceGeneratorFactory;
import com.fumbbl.ffb.server.mechanic.SetupMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Select;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;
import com.fumbbl.ffb.server.util.UtilServerTimer;

/**
 * Step in kickoff sequence to handle blitz kickoff result.
 * <p>
 * Expects stepParameter END_TURN to be set by a preceding step. (parameter is
 * consumed on TurnMode.BLITZ)
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public final class StepBlitzTurn extends AbstractStep {

    public StepBlitzTurn(GameState pGameState) {
        super(pGameState);
    }

    public StepId getId() {
        return StepId.BLITZ_TURN;
    }

    @Override
    public void start() {
        super.start();
        executeStep();
    }

    @Override
    public StepCommandStatus handleCommand(ReceivedCommand pReceivedCommand) {
        StepCommandStatus commandStatus = super.handleCommand(pReceivedCommand);
        if (commandStatus == StepCommandStatus.EXECUTE_STEP) {
            executeStep();
        }
        return commandStatus;
    }

    private void executeStep() {

        Game game = getGameState().getGame();

        if (game.getTurnMode() == TurnMode.BLITZ) {
            game.setTurnMode(TurnMode.KICKOFF);
        } else {

            Team blitzingTeam = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

            MechanicsFactory mechanicsFactory = game.getFactory(FactoryType.Factory.MECHANIC);
            SetupMechanic mechanic = (SetupMechanic) mechanicsFactory.forName(Mechanic.Type.SETUP.name());
            mechanic.pinPlayersInTacklezones(getGameState(), blitzingTeam, true);

            game.setTurnMode(TurnMode.BLITZ);
            long currentTimeMillis = System.currentTimeMillis();
            if (game.isTurnTimeEnabled()) {
                UtilServerTimer.stopTurnTimer(getGameState(), currentTimeMillis);
                game.setTurnTime(0);
                UtilServerTimer.startTurnTimer(getGameState(), currentTimeMillis);
            }
            game.startTurn();
            // insert select sequence into kickoff sequence after this step
            getGameState().pushCurrentStepOnStack();
            SequenceGeneratorFactory factory = game.getFactory(FactoryType.Factory.SEQUENCE_GENERATOR);
            ((Select) factory.forName(SequenceGenerator.Type.Select.name()))
                    .pushSequence(new Select.SequenceParams(getGameState(), true));
        }

        getResult().setNextAction(StepAction.NEXT_STEP);

    }

}
