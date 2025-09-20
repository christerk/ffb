package com.fumbbl.ffb.client.ui.strategies.click;

import com.fumbbl.ffb.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClickStrategyRegistry {
	private final List<ClickStrategy> strategies;

	public ClickStrategyRegistry() {
		Scanner<ClickStrategy> scanner = new Scanner<>(ClickStrategy.class);
		List<ClickStrategy> found = new ArrayList<>(scanner.getInstancesImplementing());
		found.sort(Comparator.comparingInt(ClickStrategy::getOrder));
		this.strategies = Collections.unmodifiableList(found);
	}

	public List<ClickStrategy> getStrategies() {
		return strategies;
	}
}

