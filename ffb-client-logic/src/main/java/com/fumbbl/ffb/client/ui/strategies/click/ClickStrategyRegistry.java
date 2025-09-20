package com.fumbbl.ffb.client.ui.strategies.click;

import com.fumbbl.ffb.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickStrategyRegistry {
	private final List<ClickStrategy> strategies;
	private final Map<String, ClickStrategy> strategyByKey;

	public ClickStrategyRegistry() {
		Scanner<ClickStrategy> scanner = new Scanner<>(ClickStrategy.class);
		List<ClickStrategy> found = new ArrayList<>(scanner.getInstancesImplementing());
		found.sort(Comparator.comparingInt(ClickStrategy::getOrder));
		this.strategies = Collections.unmodifiableList(found);
		this.strategyByKey = new HashMap<>();
		for (ClickStrategy strategy : found) {
			strategyByKey.put(strategy.getKey(), strategy);
		}
	}

	public List<ClickStrategy> getStrategies() {
		return strategies;
	}

	public ClickStrategy getStrategyByKey(String key) {
		return strategyByKey.get(key);
	}
}
