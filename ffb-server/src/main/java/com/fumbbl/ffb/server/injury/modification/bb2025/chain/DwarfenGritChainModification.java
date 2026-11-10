package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

/**
 * The defender may re-roll broken armour, whoever caused the injury.
 */
public class DwarfenGritChainModification extends ArmourReRollChainModification {

	public DwarfenGritChainModification() {
		super(true);
	}

	@Override
	public OwnerSide ownerSide() {
		return OwnerSide.DEFENDER;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		return isArmourRolled(node) && node.getContext().isArmorBroken();
	}
}
