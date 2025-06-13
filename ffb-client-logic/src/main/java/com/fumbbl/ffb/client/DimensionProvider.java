package com.fumbbl.ffb.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class DimensionProvider {

	private final LayoutSettings layoutSettings;
	private final RenderContext renderContext;

	public DimensionProvider(LayoutSettings layoutSettings, RenderContext renderContext) {
		this.layoutSettings = layoutSettings;
		this.renderContext = renderContext;
	}

	public LayoutSettings getLayoutSettings() {
		return layoutSettings;
	}

	public RenderContext getRenderContext() {
		return renderContext;
	}

	public String cacheKey() {
		return renderContext.name();
	}

	public Dimension dimension(Component component) {
		return scale(unscaledDimension(component));
	}

	public Dimension dimension(Component component, double scale) {
		Dimension dimension = unscaledDimension(component);
		return new Dimension(scale(dimension.width, scale), scale(dimension.height, scale));
	}

	public Dimension unscaledDimension(Component component) {
		return component.dimension(layoutSettings.getLayout());
	}

	public boolean isPitchPortrait() {
		return layoutSettings.getLayout().isPortrait();
	}

	protected double effectiveScale() {
		switch (renderContext) {
			case ON_PITCH:
				return layoutSettings.getScale() * layoutSettings.getLayout().getPitchScale();
			case DUGOUT:
				return layoutSettings.getScale() * layoutSettings.getLayout().getDugoutScale();
			default:
				return layoutSettings.getScale();
		}
	}

	public Dimension scale(Dimension dimension) {
		return new Dimension(scale(dimension.width), scale(dimension.height));
	}

	public int scale(int size) {
		return scale(size, effectiveScale());
	}

	private int scale(int size, double scale) {
		return (int) (size * scale);
	}

	public double scale(double size) {
		return (size * effectiveScale());
	}

	public Rectangle scale(Rectangle rectangle) {
		return new Rectangle(scale(rectangle.x), scale(rectangle.y), scale(rectangle.width), scale(rectangle.height));
	}

	public BufferedImage scaleImage(BufferedImage pImage) {
		double effectiveScale = effectiveScale();
		if (effectiveScale == 1.0) {
			return pImage;
		}

		BufferedImage scaledImage = new BufferedImage(scale(pImage.getWidth()), scale(pImage.getHeight()), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(effectiveScale, effectiveScale);
		AffineTransformOp scaleOp =
			new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		try {
			scaledImage = scaleOp.filter(pImage, scaledImage);
		} catch (Exception e) {
			// ignore
		}
		return scaledImage;
	}

	public Icon scaleIcon(ImageIcon icon) {
		icon.setImage(icon.getImage().getScaledInstance(scale(icon.getIconWidth()), scale(icon.getIconHeight()), 0));
		return icon;
	}

	public void scaleFont(java.awt.Component component) {
		Font font = component.getFont();
		if (font != null) {
			component.setFont(new Font(font.getFamily(), font.getStyle(), scale(font.getSize())));
		}
	}


	public TitledBorder scaleFont(TitledBorder border) {
		Font font = border.getTitleFont();
		if (font != null) {
			border.setTitleFont(new Font(font.getFamily(), font.getStyle(), scale(font.getSize())));
		}
		return border;
	}

}
