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

        Player<?> pusher = state.attacker;
        Player<?> target = state.defender;
        System.out.println("EyeGouge hook: pusher=" + (pusher == null ? "null" : pusher.getId())
          + " target=" + (target == null ? "null" : target.getId()));
        if (pusher == null || target == null) {
          return false;
        }

        PlayerState pusherState = fieldModel.getPlayerState(pusher);
        System.out.println("EyeGouge pusher state=" + (pusherState == null ? "null" : pusherState.getBase())
          + " standing=" + (pusherState != null && pusherState.isStanding())
          + " distracted=" + (pusherState != null && pusherState.isDistracted()));

        if (pusherState == null
          || !pusher.hasSkillProperty(NamedProperties.canEyeGouge)
          || !pusherState.isStanding()
          || pusherState.isDistracted()) {
          System.out.println("EyeGouge skip pusher");
          return false;
        }

        if (target.getTeam() == pusher.getTeam()) {
          System.out.println("EyeGouge skip target same team");
          return false;
        }

        PlayerState targetState = fieldModel.getPlayerState(target);
        System.out.println("EyeGouge target state=" + (targetState == null ? "null" : targetState.getBase())
          + " standing=" + (targetState != null && targetState.isStanding()));

        if (targetState == null || !targetState.isStanding()) {
          System.out.println("EyeGouge skip target not standing");
          return false;
        }

        System.out.println("EyeGouge APPLY to " + target.getId());
        fieldModel.setPlayerState(target, targetState.changeEyeGouged(true));
        UtilCards.getSkillWithProperty(pusher, NamedProperties.canEyeGouge)
          .ifPresent(skill ->
            step.getResult().addReport(new ReportSkillUse(pusher.getId(), skill, true, SkillUse.EYE_GOUGED)));
        return false;
      }

    });

	}
}
