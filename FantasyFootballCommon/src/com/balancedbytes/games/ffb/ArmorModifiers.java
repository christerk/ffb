package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ArmorModifiers {

	public static final ArmorModifier MIGHTY_BLOW = new ArmorModifier("Mighty Blow", 1, false);
	public static final ArmorModifier FOUL_PLUS_1 = new ArmorModifier("1 Offensive Assist", 1, true);
	public static final ArmorModifier FOUL_PLUS_2 = new ArmorModifier("2 Offensive Assists", 2, true);
	public static final ArmorModifier FOUL_PLUS_3 = new ArmorModifier("3 Offensive Assists", 3, true);
	public static final ArmorModifier FOUL_PLUS_4 = new ArmorModifier("4 Offensive Assists", 4, true);
	public static final ArmorModifier FOUL_PLUS_5 = new ArmorModifier("5 Offensive Assists", 5, true);
	public static final ArmorModifier FOUL_PLUS_6 = new ArmorModifier("6 Offensive Assists", 6, true);
	public static final ArmorModifier FOUL_PLUS_7 = new ArmorModifier("7 Offensive Assists", 7, true);
	public static final ArmorModifier FOUL_MINUS_1 = new ArmorModifier("1 Defensive Assist", -1, true);
	public static final ArmorModifier FOUL_MINUS_2 = new ArmorModifier("2 Defensive Assists", -2, true);
	public static final ArmorModifier FOUL_MINUS_3 = new ArmorModifier("3 Defensive Assists", -3, true);
	public static final ArmorModifier FOUL_MINUS_4 = new ArmorModifier("4 Defensive Assists", -4, true);
	public static final ArmorModifier FOUL_MINUS_5 = new ArmorModifier("5 Defensive Assists", -5, true);
	public static final ArmorModifier CHAINSAW = new ArmorModifier("Chainsaw", 3, false);
	public static final ArmorModifier FOUL = new ArmorModifier("Foul", 1, false);

	public static final ArmorModifier CLAWS = new ArmorModifier("Claws", 0, false) {
		@Override
		public boolean appliesToContext(ArmorModifierContext context) {
			if (context.isStab || context.isFoul || playerHasChainsaw(context.attacker)) {
				return false;
			}
			if (context.defender.getArmour() > 7) {
				return true;
			}
			return false;
		}
	};

	public static final ArmorModifier STAKES = new ArmorModifier("Stakes", 1, false) {
		@Override
		public boolean appliesToContext(ArmorModifierContext context) {
			boolean applies = false;

			Team otherTeam = context.game.getTeamHome().hasPlayer(context.defender) ? context.game.getTeamHome()
					: context.game.getTeamAway();
			if (context.isStab && (context.attacker != null) && (otherTeam.getRoster().isUndead()
					|| ((context.defender != null) && context.defender.getPosition().isUndead()))) {
				applies = true;
			}
			return applies;
		}
	};

	public static final ArmorModifier DIRTY_PLAYER = new ArmorModifier("Dirty Player", 1, false) {
		@Override
		public boolean appliesToContext(ArmorModifierContext context) {
			return context.isFoul;
		}
	};

	public static boolean playerHasChainsaw(Player<?> player) {
		return player.hasSkillWithProperty(NamedProperties.blocksLikeChainsaw);
	}

	public static boolean chainsawIsInvolved(Player<?> attacker, Player<?> defender) {
		return (playerHasChainsaw(attacker) || playerHasChainsaw(defender));
	}

	private final Map<String, ArmorModifier> values;

	public Map<String, ArmorModifier> values() {
		return values;
	}

	public ArmorModifiers() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = ArmorModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					ArmorModifier modifier = (ArmorModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class ArmorModifierContext {
		public Game game;
		public Player<?> attacker;
		public Player<?> defender;
		public boolean isStab;
		public boolean isFoul;

		public ArmorModifierContext(Game game, Player<?> attacker, Player<?> defender, boolean isStab, boolean isFoul) {
			this.game = game;
			this.attacker = attacker;
			this.defender = defender;
			this.isStab = isStab;
			this.isFoul = isFoul;
		}
	}

}
