package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.ProjectileVomit;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.bb2020.ReportOldPro;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;

import java.util.HashSet;

public class OldProModification extends InjuryContextModification<OldProModificationParams> {

	public OldProModification() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(Chainsaw.class);
			add(Foul.class);
			add(FoulForSpp.class);
			add(FoulForSppWithChainsaw.class);
			add(FoulWithChainsaw.class);
			add(BlockProne.class);
			add(BlockStunned.class);
			add(Stab.class);
			add(ProjectileVomit.class);
		}});
	}

	@Override
	protected OldProModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new OldProModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(OldProModificationParams params) {
		Game game = params.getGameState().getGame();
		params.setSpottedFoul(isSpottedFoul(params.getNewContext(), params.getInjuryType()));
		params.setSelfInflicted(isSelfInflicted(game, params.getNewContext(), params.getInjuryType()));
		return hasPrerequisite(game, params.getNewContext()) && params.getNewContext().fArmorRoll != null
			&& (params.getNewContext().isArmorBroken() == params.isSelfInflicted()
			|| params.isSpottedFoul());
	}

	private boolean hasPrerequisite(Game game, InjuryContext injuryContext) {
		String playerId = StringTool.isProvided(injuryContext.fAttackerId) ? injuryContext.fAttackerId : injuryContext.fDefenderId;
		return UtilServerReRoll.isProReRollAvailable(game.getPlayerById(playerId), game, null);
	}

	private boolean isSpottedFoul(InjuryContext injuryContext, InjuryType injuryType) {
		return injuryContext.fArmorRoll != null && injuryContext.fArmorRoll[0] == injuryContext.fArmorRoll[1] && injuryType.isFoul();
	}

	@Override
	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return true;
	}

	private boolean isSelfInflicted(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		Player<?> attacker = game.getPlayerById(injuryContext.fAttackerId);
		Player<?> defender = game.getPlayerById(injuryContext.fDefenderId);

		boolean hurtTeamMate = attacker != null && defender != null && attacker.getTeam() == defender.getTeam();

		return (!StringTool.isProvided(injuryContext.fAttackerId) && (injuryType.isVomit() || injuryType.isChainsaw()))
			|| (hurtTeamMate && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY);
	}

	@Override
	protected void prepareArmourParams(OldProModificationParams params) {
		ModifiedInjuryContext newContext = params.getNewContext();
		params.setSpottedFoul(newContext.fArmorRoll[0] == newContext.fArmorRoll[1] && params.getInjuryType().isFoul());
		params.setReplaceIndex(newContext.fArmorRoll[0] < newContext.fArmorRoll[1] != params.isSelfInflicted() ? 0 : 1);
		params.setOldValue(newContext.fArmorRoll[params.getReplaceIndex()]);
		newContext.fArmorRoll[params.getReplaceIndex()] = params.isSelfInflicted() ? 1 : 6;
	}

	@Override
	protected boolean armourModificationCantHelp(OldProModificationParams params) {
		return !params.isSpottedFoul() && (params.getNewContext().isArmorBroken() == params.isSelfInflicted());
	}

	@Override
	protected void applyArmourModification(OldProModificationParams params) {
		int newValue = params.getGameState().getDiceRoller().rollDice(6);
		ModifiedInjuryContext newContext = params.getNewContext();
		newContext.fArmorRoll[params.getReplaceIndex()] = newValue;
		String playerId = params.isSelfInflicted() ? newContext.fDefenderId : newContext.fAttackerId;
		newContext.addReport(new ReportOldPro(playerId, params.getOldValue(), newValue, params.isSelfInflicted()));
	}

	@Override
	public SkillUse skillUse() {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}

	@Override
	public boolean requiresConditionalReRollSkill() {
		return true;
	}
}
