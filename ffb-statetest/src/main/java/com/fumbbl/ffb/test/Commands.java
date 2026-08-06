package com.fumbbl.ffb.test;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.commands.*;

public class Commands {
    public static ClientCommandActingPlayer selectPlayer(String playerId, PlayerAction action) {
        return new ClientCommandActingPlayer(playerId, action, false);
    }

    public static ClientCommandActingPlayer selectPlayer(String playerId, PlayerAction action, boolean jumping) {
        return new ClientCommandActingPlayer(playerId, action, jumping);
    }

    public static ClientCommandBlock block(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, false, false, false, false, false);
    }

    public static ClientCommandBlock stab(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, true, false, false, false, false);
    }

    public static ClientCommandBlock chainsaw(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, false, true, false, false, false);
    }

    public static ClientCommandBlock vomit(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, false, false, true, false, false);
    }

    public static ClientCommandBlock breatheFire(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, false, false, false, true, false);
    }

    public static ClientCommandBlock chomp(String attackerId, String defenderId) {
        return new ClientCommandBlock(attackerId, defenderId, false, false, false, false, true);
    }

    public static ClientCommandBlockChoice blockChoice(int index) {
        return new ClientCommandBlockChoice(index);
    }

    public static ClientCommandPushback pushback(Pushback pushback) {
        return new ClientCommandPushback(pushback);
    }

    public static ClientCommandFollowupChoice followup(boolean follow) {
        return new ClientCommandFollowupChoice(follow);
    }

    public static ClientCommandPass pass(String playerId, FieldCoordinate target) {
        return new ClientCommandPass(playerId, target);
    }

    public static ClientCommandUseSkill useSkill(Skill skill, boolean skillUsed, String playerId) {
        return new ClientCommandUseSkill(skill, skillUsed, playerId, null, false);
    }

    public static ClientCommandUseSkill useSkill(Skill skill, boolean skillUsed, String playerId, boolean neverUse) {
        return new ClientCommandUseSkill(skill, skillUsed, playerId, null, neverUse);
    }

    public static ClientCommandUseBrawler brawler(String attackerId) {
        return new ClientCommandUseBrawler(attackerId);
    }

    public static ClientCommandUseConsummateReRollForBlock singleDieReRoll(int dieIndex) {
        return new ClientCommandUseConsummateReRollForBlock(dieIndex);
    }

    public static ClientCommandUseSingleBlockDieReRoll singleBlockDieReRoll(int dieIndex, ReRollSource reRollSource) {
        return new ClientCommandUseSingleBlockDieReRoll(dieIndex, reRollSource);
    }

    public static ClientCommandTargetSelected selectBlitzTarget(String targetPlayerId) {
        return new ClientCommandTargetSelected(targetPlayerId);
    }

    public static ClientCommandMove move(String playerId, FieldCoordinate from, FieldCoordinate... to) {
        return new ClientCommandMove(playerId, from, to, null);
    }

    public static ClientCommandFoul foul(String playerId, String defenderId) {
        return new ClientCommandFoul(playerId, defenderId, false);
    }

    public static ClientCommandGaze gaze(String playerId, String victimId) {
        return new ClientCommandGaze(playerId, victimId);
    }

    public static ClientCommandThrowTeamMate throwTeammate(String playerId, String thrownId) {
        return new ClientCommandThrowTeamMate(playerId, thrownId);
    }

    public static ClientCommandThrowTeamMate throwTeammate(String playerId, FieldCoordinate targetCoordinate) {
        return new ClientCommandThrowTeamMate(playerId, targetCoordinate);
    }

    public static ClientCommandInterceptorChoice interceptorChoice(String interceptorId) {
        return new ClientCommandInterceptorChoice(interceptorId, null);
    }

    public static ClientCommandPlayerChoice shadowing(com.fumbbl.ffb.model.Player<?> shadower) {
        return new ClientCommandPlayerChoice(com.fumbbl.ffb.PlayerChoiceMode.SHADOWING,
                new com.fumbbl.ffb.model.Player<?>[]{shadower});
    }
}
