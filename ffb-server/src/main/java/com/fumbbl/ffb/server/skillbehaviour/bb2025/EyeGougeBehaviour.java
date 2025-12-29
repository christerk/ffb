package com.fumbbl.ffb.server.skillbehaviour.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.net.commands.ClientCommandUseSkill;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.server.model.StepModifier;
import com.fumbbl.ffb.server.step.StepCommandStatus;
import com.fumbbl.ffb.server.step.bb2025.block.StepPushback;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.skill.bb2025.EyeGouge;

@RulesCollection(Rules.BB2025)
public class EyeGougeBehaviour extends SkillBehaviour<EyeGouge> {

	public EyeGougeBehaviour() {
		super();

    registerModifier(new StepModifier<StepPushback, StepPushback.StepState>(3) {

      @Override
      public StepCommandStatus handleCommandHook(StepPushback step, StepPushback.StepState state,
                                                ClientCommandUseSkill useSkillCommand) {
        return StepCommandStatus.UNHANDLED_COMMAND;
      }

      @Override
      public boolean handleExecuteStepHook(StepPushback step, StepPushback.StepState state) {
        Game game = step.getGameState().getGame();
        FieldModel fieldModel = game.getFieldModel();

        Player<?> pusher = game.getActingPlayer().getPlayer();
        if (!pusher.hasSkillProperty(NamedProperties.canRemoveOpponentAssists)
            || !state.defender.getId().equals(game.getDefenderId())) {
          return false;
        }

        PlayerState targetState = fieldModel.getPlayerState(state.defender);
        fieldModel.setPlayerState(state.defender, targetState.changeEyeGouged(true));
        UtilCards.getSkillWithProperty(pusher, NamedProperties.canRemoveOpponentAssists)
          .ifPresent(skill ->
            step.getResult().addReport(new ReportSkillUse(pusher.getId(), skill, true, SkillUse.EYE_GOUGED)));
        return false;
      }

    });

	}
}
