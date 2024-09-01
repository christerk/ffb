package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.model.AnimationType;

import java.awt.*;

public class AnimationProjector {
	private final FieldCoordinate start;
	private final FieldCoordinate end;

	private final int stepping;
	private final boolean xAxisAnimation;
	private final Dimension startDimension;
	private final Dimension endDimension;
	private Dimension interceptorDimension;

	private final Dimension currentDimension;

	public AnimationProjector(FieldCoordinate start, FieldCoordinate end, FieldCoordinate interceptor, DimensionProvider dimensionProvider) {
		this.start = start;
		this.end = end;
		stepping = findStepping();

		startDimension = dimensionProvider.mapToLocal(start, true);
		endDimension = dimensionProvider.mapToLocal(end, true);
		xAxisAnimation = (Math.abs(endDimension.width - startDimension.width) > Math.abs(endDimension.height - startDimension.height));
		interceptorDimension = new Dimension(endDimension);

		if (interceptor != null) {
			interceptorDimension = dimensionProvider.mapToLocal(interceptor, true);
		}

		currentDimension = new Dimension(startDimension);
	}

	public Dimension getCurrentDimension() {
		return currentDimension;
	}

	private int findStepping() {
		if ((start == null) || (end == null)) {
			return 0;
		}
		int deltaX = Math.abs(end.getX() - start.getX());
		int deltaY = Math.abs(end.getY() - start.getY());
		int deltaMax = Math.max(deltaX, deltaY);
		if (deltaMax <= 7) {
			return 2;
		} else {
			return 3;
		}
	}

	public boolean updateCurrentDimension() {
		if (xAxisAnimation) {
			// y - y1 = (y2 - y1) / (x2 - x1) * (x - x1)
			currentDimension.height = startDimension.height + (int) (((double) (endDimension.height - startDimension.height) / (double) (endDimension.width - startDimension.width)) * (currentDimension.width - startDimension.width));
			if (startDimension.width < endDimension.width) {
				currentDimension.width += stepping;
				return (currentDimension.width >= interceptorDimension.width);
			} else {
				currentDimension.width -= stepping;
				return (currentDimension.width <= interceptorDimension.width);
			}
		} else {
			// x - x1 = (x2 - x1) / (y2 - y1) * (y - y1)
			currentDimension.width = startDimension.width + (int) (((double) (endDimension.width - startDimension.width) / (double) (endDimension.height - startDimension.height)) * (currentDimension.height - startDimension.height));
			if (startDimension.height < endDimension.height) {
				currentDimension.height += stepping;
				return currentDimension.height >= interceptorDimension.height;
			} else {
				currentDimension.height -= stepping;
				return currentDimension.height <= interceptorDimension.height;
			}
		}
	}

	public double findScale(AnimationType animationType) {
		if (xAxisAnimation) {
			return findScale(((double) (currentDimension.width - startDimension.width) / (double) (endDimension.width - startDimension.width)) * 2, animationType);
		} else {
			return findScale(((double) (currentDimension.height - startDimension.height) / (double) (endDimension.height - startDimension.height)) * 2, animationType);
		}
	}

	private double findScale(double pX, AnimationType animationType) {
		if ((AnimationType.PASS == animationType) || (AnimationType.THROW_A_ROCK == animationType)
			|| (AnimationType.THROW_BOMB == animationType)) {
			return 1 - ((pX - 1) * (pX - 1) * 0.5);
		} else if (AnimationType.THROW_TEAM_MATE == animationType || AnimationType.THROW_KEG == animationType) {
			return 1.5 - ((pX - 1) * (pX - 1) * 0.5);
		} else if ((AnimationType.KICK == animationType) || (AnimationType.HAIL_MARY_PASS == animationType)
			|| (AnimationType.HAIL_MARY_BOMB == animationType)) {
			return 1 - ((pX - 1) * (pX - 1) * 0.75);
		} else {
			return 0.0;
		}
	}
}