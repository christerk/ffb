package com.fumbbl.ffb.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static com.fumbbl.ffb.client.ClientLayout.WIDE_FL_1920x1080;

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

    public BufferedImage scaleImageForPlayerPortrait(BufferedImage pImage) {
        if (WIDE_FL_1920x1080.equals(layoutSettings.getLayout())) {
            Dimension portraitDimensions = Component.PLAYER_PORTRAIT.dimension(layoutSettings.getLayout());
            int targetWidth = portraitDimensions.width;
            int targetHeight = portraitDimensions.height;

            // 1. Create the new blank canvas
            BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

            // 2. Get the graphics context
            Graphics2D g2d = scaledImage.createGraphics();

            try {
                // 3. Set rendering hints for bilinear scaling quality
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // 4. Draw the original image stretched to the target width and height
                g2d.drawImage(pImage, 0, 0, targetWidth, targetHeight, null);
            } finally {
                g2d.dispose();
            }

            return scaleImage(scaledImage);
        }

        return scaleImage(pImage);
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

	public BufferedImage scaleEmoji(BufferedImage image, Component component) {
		if (image == null || component == null) {
			return null;
		}

		Dimension dim = dimension(component);
		int size = dim.width;

		// I used SCALE_SMOOTH here instead of bilinear scaling because the quality difference was noticeable
		Image scaled = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);

		BufferedImage buffered = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buffered.createGraphics();
		g2d.drawImage(scaled, 0, 0, null);
		g2d.dispose();

		return buffered;
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
