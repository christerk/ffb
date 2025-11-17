## Current Usage

### Move (BB2025)

- Failure label: `IStepLabel.END_MOVING`
- Success/Resume label: `IStepLabel.NEXT`
- Alternate success label: `IStepLabel.END_MOVING`
- `animalSavageryParams`: none (helper injects the failure label automatically)
- `setDefenderParams`: `BLOCK_DEFENDER_ID = params.getGazeVictimId()`, `IGNORE_NULL_VALUE = true`

```java
Sequence sequence = new Sequence(gameState);

sequence.add(StepId.INIT_MOVING, ...);

NegaTraitSequences.append(
	sequence,
	IStepLabel.END_MOVING,
	IStepLabel.NEXT,
	IStepLabel.END_MOVING,
	new StepParameter[0],
	new StepParameter[]{
		from(StepParameterKey.BLOCK_DEFENDER_ID, params.getGazeVictimId()),
		from(StepParameterKey.IGNORE_NULL_VALUE, true)
	}
);
```

### Block (BB2025)

- Failure label: `IStepLabel.END_BLOCKING`
- Success/Resume label: `IStepLabel.NEXT`
- Alternate success label: `IStepLabel.END_BLOCKING`
- `animalSavageryParams`: `BLOCK_DEFENDER_ID = params.getBlockDefenderId()`
- `setDefenderParams`: `BLOCK_DEFENDER_ID = params.getBlockDefenderId()`

```java
Sequence sequence = new Sequence(gameState);

sequence.add(StepId.INIT_BLOCKING, ...);

NegaTraitSequences.append(
	sequence,
	IStepLabel.END_BLOCKING,
	IStepLabel.NEXT,
	IStepLabel.END_BLOCKING,
	new StepParameter[]{
		from(StepParameterKey.BLOCK_DEFENDER_ID, params.getBlockDefenderId())
	},
	new StepParameter[]{
		from(StepParameterKey.BLOCK_DEFENDER_ID, params.getBlockDefenderId())
	}
);
```

## Likely BB2020 Candidates

“Branching” here means the action resumes at a specific label when all nega-trait checks pass (so they need `successLabel`/`alternateSuccessLabel`); “non-branching” actions just keep executing inline and therefore pass `null` for both labels.

- **Branching sequences (need success labels):** `Move`, `Block`, `MultiBlock`, `Pass`, `Foul`, `Select`, `SelectBlitzTarget`, `SelectGazeTarget`, `ThrowTeamMate`. Each already has a `NEXT`/`END_*` label pair.
- **Non-branching specials (no success label):** `ThrowKeg`, `ThenIStartedBlastin`, `BalefulHex`, `Treacherous`, `RaidingParty`, `CatchOfTheDay`, `BlackInk`, `FuriousOutburst`. Call the helper with `successLabel = null` to get the simple Bone Head version.

Example for a non-branching special (ThrowKeg, BB2020):

```java
Sequence sequence = new Sequence(gameState);

sequence.add(StepId.INIT_ACTIVATION);

NegaTraitSequences.append(
	sequence,
	IStepLabel.END,
	null,            // no success label, helper uses the simple Bone Head variant
	null,
	new StepParameter[0],
	new StepParameter[0]
);

sequence.add(StepId.THROW_KEG, from(StepParameterKey.TARGET_PLAYER_ID, params.getPlayerId()));
```
