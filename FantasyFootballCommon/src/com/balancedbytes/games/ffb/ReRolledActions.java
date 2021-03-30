package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.skill.AlwaysHungry;
import com.balancedbytes.games.ffb.skill.Animosity;
import com.balancedbytes.games.ffb.skill.BloodLust;
import com.balancedbytes.games.ffb.skill.BoneHead;
import com.balancedbytes.games.ffb.skill.Chainsaw;
import com.balancedbytes.games.ffb.skill.Dauntless;
import com.balancedbytes.games.ffb.skill.FoulAppearance;
import com.balancedbytes.games.ffb.skill.HypnoticGaze;
import com.balancedbytes.games.ffb.skill.JumpUp;
import com.balancedbytes.games.ffb.skill.KickTeamMate;
import com.balancedbytes.games.ffb.skill.Pass;
import com.balancedbytes.games.ffb.skill.ReallyStupid;
import com.balancedbytes.games.ffb.skill.RightStuff;
import com.balancedbytes.games.ffb.skill.SafeThrow;
import com.balancedbytes.games.ffb.skill.TakeRoot;
import com.balancedbytes.games.ffb.skill.ThrowTeamMate;
import com.balancedbytes.games.ffb.skill.WildAnimal;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReRolledActions {

	public static final ReRolledAction GO_FOR_IT = new ReRolledAction("Go For It");
	public static final ReRolledAction DODGE = new ReRolledAction("Dodge");
	public static final ReRolledAction CATCH = new ReRolledAction("Catch");
	public static final ReRolledAction PICK_UP = new ReRolledAction("Pick Up");
	public static final ReRolledAction PASS = new ReRolledAction(Pass.class);
	public static final ReRolledAction DAUNTLESS = new ReRolledAction(Dauntless.class);
	public static final ReRolledAction JUMP = new ReRolledAction("Jump");
	public static final ReRolledAction FOUL_APPEARANCE = new ReRolledAction(FoulAppearance.class);
	public static final ReRolledAction BLOCK = new ReRolledAction("Block");
	public static final ReRolledAction REALLY_STUPID = new ReRolledAction(ReallyStupid.class);
	public static final ReRolledAction BONE_HEAD = new ReRolledAction(BoneHead.class);
	public static final ReRolledAction WILD_ANIMAL = new ReRolledAction(WildAnimal.class);
	public static final ReRolledAction TAKE_ROOT = new ReRolledAction(TakeRoot.class);
	public static final ReRolledAction WINNINGS = new ReRolledAction("Winnings");
	public static final ReRolledAction ALWAYS_HUNGRY = new ReRolledAction(AlwaysHungry.class);
	public static final ReRolledAction THROW_TEAM_MATE = new ReRolledAction(ThrowTeamMate.class);
	public static final ReRolledAction KICK_TEAM_MATE = new ReRolledAction(KickTeamMate.class);
	public static final ReRolledAction RIGHT_STUFF = new ReRolledAction(RightStuff.class);
	public static final ReRolledAction SHADOWING = new ReRolledAction("Shadowing");
	public static final ReRolledAction SHADOWING_ESCAPE = new ReRolledAction("Shadowing Escape");
	public static final ReRolledAction TENTACLES_ESCAPE = new ReRolledAction("Tentacles Escape");
	public static final ReRolledAction ESCAPE = new ReRolledAction("Escape");
	public static final ReRolledAction SAFE_THROW = new ReRolledAction(SafeThrow.class);
	public static final ReRolledAction INTERCEPTION = new ReRolledAction("Interception");
	public static final ReRolledAction JUMP_UP = new ReRolledAction(JumpUp.class);
	public static final ReRolledAction STAND_UP = new ReRolledAction("standUp");
	public static final ReRolledAction CHAINSAW = new ReRolledAction(Chainsaw.class);
	public static final ReRolledAction BLOOD_LUST = new ReRolledAction(BloodLust.class);
	public static final ReRolledAction HYPNOTIC_GAZE = new ReRolledAction(HypnoticGaze.class);
	public static final ReRolledAction ANIMOSITY = new ReRolledAction(Animosity.class);

	private Map<String, ReRolledAction> values;

	public Map<String, ReRolledAction> values() {
		return values;
	}

	public ReRolledActions() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = ReRolledAction.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					ReRolledAction action = (ReRolledAction) f.get(this);
					values.put(action.getName().toLowerCase(), action);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
