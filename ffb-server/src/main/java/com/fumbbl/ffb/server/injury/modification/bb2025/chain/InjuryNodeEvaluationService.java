package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.injuryType.ModificationAwareInjuryTypeServer;
import com.fumbbl.ffb.server.util.UtilServerInjury;
import com.fumbbl.ffb.skill.bb2025.SneakyGit;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Recomputes the result of a node from its dice and the modifications applied on its path:
 * base evaluation of the injury type, then the skill modifiers, then the common injury context evaluation.
 */
public class InjuryNodeEvaluationService {

	private final InjuryContextCopyService copyService;
	private final SpottedFoulService spottedFoulService;
	private final OptionalSkillModifierService optionalSkillModifierService;

	public InjuryNodeEvaluationService() {
		this(new InjuryContextCopyService(), new SpottedFoulService(), new OptionalSkillModifierService());
	}

	public InjuryNodeEvaluationService(InjuryContextCopyService copyService, SpottedFoulService spottedFoulService,
		OptionalSkillModifierService optionalSkillModifierService) {
		this.copyService = copyService;
		this.spottedFoulService = spottedFoulService;
		this.optionalSkillModifierService = optionalSkillModifierService;
	}

	public void evaluate(InjuryChainContext context, InjuryContext template, InjuryNode node) {
		GameState gameState = context.getGameState();
		Game game = gameState.getGame();
		ModificationAwareInjuryTypeServer<?> typeServer = context.getTypeServer();
		InjuryDicePool pool = context.getTree().getPool();
		DiceRoller diceRoller = gameState.getDiceRoller();
		Player<?> attacker = game.getPlayerById(template.fAttackerId);
		Player<?> defender = game.getPlayerById(template.fDefenderId);

		InjuryContext injuryContext = copyService.copy(template);

		typeServer.evaluateArmour(game, gameState, new PooledDiceRoller(diceRoller, pool, node.getArmourDice(), null),
			attacker, defender, context.getDefenderCoordinate(), context.getFromCoordinate(), injuryContext);

		if (injuryContext.getArmorRoll() != null
			&& (!node.getRemovedArmourModifiers().isEmpty() || !node.getSkillArmourModifiers().isEmpty())) {
			node.getRemovedArmourModifiers().forEach(injuryContext.fArmorModifiers::remove);
			injuryContext.addArmorModifiers(node.getSkillArmourModifiers());
			injuryContext.setArmorBroken(isArmourBroken(gameState, injuryContext));
		}

		if (injuryContext.isArmorBroken()) {
			typeServer.evaluateInjury(game, gameState, new PooledDiceRoller(diceRoller, pool, null, node.getInjuryDice()),
				attacker, defender, context.getDefenderCoordinate(), context.getFromCoordinate(), injuryContext);
			if (!node.getSkillInjuryModifiers().isEmpty()) {
				injuryContext.addInjuryModifiers(node.getSkillInjuryModifiers());
				clearInjuryResult(injuryContext);
				typeServer.reinterpretInjury(defender, gameState,
					new PooledDiceRoller(diceRoller, pool, null, node.getInjuryDice()), injuryContext);
			}
		} else {
			typeServer.applySavedByArmour(injuryContext);
		}

		if (typeServer.optionalSkillOwnerBecomesAttacker()) {
			node.getAppliedModifications().stream()
				.filter(applied -> optionalSkillModifierService.isOptional(game, applied.getSkill()))
				.findFirst()
				.ifPresent(applied -> injuryContext.setAttackerId(applied.getPlayerId()));
		}

		UtilServerInjury.evaluateInjuryContext(injuryContext.fInjuryType, context.getTree().isStunIsTreatedAsKo(),
			defender, injuryContext, gameState);

		node.setContext(injuryContext);
		node.setSpotted(injuryContext.fInjuryType.isFoul()
			&& spottedFoulService.spottedByDice(game, injuryContext, hasSneakyGit(game, attacker)));
	}

	/**
	 * With {@link GameOptionId#CLAW_DOES_NOT_STACK} the armour is broken if Claws alone or all other modifiers break it.
	 * If neither does, only Claws is kept for the log, like in the base evaluation of blocks.
	 */
	private boolean isArmourBroken(GameState gameState, InjuryContext injuryContext) {
		DiceInterpreter interpreter = DiceInterpreter.getInstance();
		Optional<ArmorModifier> claws = Arrays.stream(injuryContext.getArmorModifiers())
			.filter(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.reducesArmourToFixedValue))
			.findFirst();
		if (!claws.isPresent() || !UtilGameOption.isOptionEnabled(gameState.getGame(), GameOptionId.CLAW_DOES_NOT_STACK)) {
			return interpreter.isArmourBroken(gameState, injuryContext);
		}
		List<ArmorModifier> modifiers = Arrays.asList(injuryContext.getArmorModifiers());
		injuryContext.clearArmorModifiers();
		injuryContext.addArmorModifier(claws.get());
		if (interpreter.isArmourBroken(gameState, injuryContext)) {
			return true;
		}
		injuryContext.clearArmorModifiers();
		modifiers.stream().filter(modifier -> modifier != claws.get()).forEach(injuryContext::addArmorModifier);
		if (interpreter.isArmourBroken(gameState, injuryContext)) {
			return true;
		}
		boolean chainsaw = modifiers.stream()
			.anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.blocksLikeChainsaw));
		if (!chainsaw) {
			injuryContext.clearArmorModifiers();
			injuryContext.addArmorModifier(claws.get());
		}
		return false;
	}

	private void clearInjuryResult(InjuryContext injuryContext) {
		injuryContext.fInjury = null;
		injuryContext.fCasualtyRoll = null;
		injuryContext.fCasualtyRollDecay = null;
		injuryContext.fInjuryDecay = null;
		injuryContext.fSeriousInjury = null;
		injuryContext.fSeriousInjuryDecay = null;
		injuryContext.originalSeriousInjury = null;
		injuryContext.casualtyModifiers.clear();
	}

	private boolean hasSneakyGit(Game game, Player<?> attacker) {
		if (attacker == null) {
			return false;
		}
		SkillFactory skillFactory = game.getFactory(FactoryType.Factory.SKILL);
		Skill sneakyGit = skillFactory.forClass(SneakyGit.class);
		return sneakyGit != null && UtilCards.hasSkill(attacker, sneakyGit);
	}
}
