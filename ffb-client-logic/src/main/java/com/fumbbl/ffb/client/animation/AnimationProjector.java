package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.model.AnimationType;

import java.awt.*;

public class AnimationProjector {

	private final double stepping;
	private final boolean xAxisAnimation;
	private final Dimension startDimension;
	private final Dimension endDimension;
	private Dimension interceptorDimension;

	private double currentWidth;
	private double currentHeight;

	public AnimationProjector(FieldCoordinate start, FieldCoordinate end, FieldCoordinate interceptor,
														DimensionProvider dimensionProvider, SteppingStrategy steppingStrategy) {
		stepping = steppingStrategy.findStepping();

		startDimension = dimensionProvider.mapToLocal(start, true);
		endDimension = dimensionProvider.mapToLocal(end, true);
		xAxisAnimation = (Math.abs(endDimension.width - startDimension.width) > Math.abs(endDimension.height - startDimension.height));
		interceptorDimension = new Dimension(endDimension);

		if (interceptor != null) {
			interceptorDimension = dimensionProvider.mapToLocal(interceptor, true);
		}

		currentWidth = startDimension.getWidth();
		currentHeight = startDimension.getHeight();
	}

	public Dimension getCurrentDimension() {
		return new Dimension((int) currentWidth, (int) currentHeight);
	}

	public boolean updateCurrentDimension() {
		if (xAxisAnimation) {
			// y - y1 = (y2 - y1) / (x2 - x1) * (x - x1)
			currentHeight = startDimension.height + (int) (((double) (endDimension.height - startDimension.height) / (double) (endDimension.width - startDimension.width)) * (currentWidth - startDimension.width));
			if (startDimension.width < endDimension.width) {
				currentWidth += stepping;
				return (currentWidth >= interceptorDimension.width);
			} else {
				currentWidth -= stepping;
				return (currentWidth <= interceptorDimension.width);
			}
		} else {
			// x - x1 = (x2 - x1) / (y2 - y1) * (y - y1)
			currentWidth = startDimension.width + (int) (((double) (endDimension.width - startDimension.width) / (double) (endDimension.height - startDimension.height)) * (currentHeight - startDimension.height));
			if (startDimension.height < endDimension.height) {
				currentHeight += stepping;
				return currentHeight >= interceptorDimension.height;
			} else {
				currentHeight -= stepping;
				return currentHeight <= interceptorDimension.height;
			}
		}
	}

	public double findScale(AnimationType animationType) {
		if (xAxisAnimation) {
			return findScale(((double) (currentWidth - startDimension.width) / (double) (endDimension.width - startDimension.width)) * 2, animationType);
		} else {
			return findScale(((double) (currentHeight - startDimension.height) / (double) (endDimension.height - startDimension.height)) * 2, animationType);
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