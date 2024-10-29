package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DimensionProvider {

	public static final double MIN_SCALE_FACTOR = 0.5;
	public static final double MAX_SCALE_FACTOR = 3;
	public static final double BASE_SCALE_FACTOR = 1.0;


	private static final int SIDEBAR_WIDTH_L = 145;
	private static final int SIDEBAR_WIDTH_P = 165;
	private final double scaleStep = 0.05;
	private double scale;
	private ClientLayout layout;

	public DimensionProvider(ClientLayout layout, double scale) {
		this.layout = layout;
		this.scale = scale;
	}

	public Dimension dimension(Component component) {
		return scale(unscaledDimension(component));
	}

	public Dimension dimension(Component component, double scale) {
		Dimension dimension = unscaledDimension(component);
		return new Dimension(scale(dimension.width, scale), scale(dimension.height, scale));
	}

	public Dimension unscaledDimension(Component component) {
		return component.dimension(layout);
	}

	public boolean isPitchPortrait() {
		return layout.portrait;
	}

	public ClientLayout getLayout() {
		return layout;
	}

	public void setLayout(ClientLayout layout) {
		this.layout = layout;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public FieldCoordinate mapToGlobal(FieldCoordinate fieldCoordinate) {
		if (isPitchPortrait()) {
			return new FieldCoordinate(25 - fieldCoordinate.getY(), fieldCoordinate.getX());
		}

		return fieldCoordinate;
	}

	public int fieldSquareSize() {
		return fieldSquareSize(1);
	}

	public int fieldSquareSize(double factor) {
		return (int) scale(unscaledFieldSquare() * factor);
	}

	public int unscaledFieldSquare() {
		return unscaledDimension(Component.FIELD_SQUARE).width;
	}

	public int imageOffset() {
		return fieldSquareSize() / 2;
	}

	public Dimension mapToLocal(int x, int y, boolean addImageOffset) {
		int offset = addImageOffset ? unscaledFieldSquare() / 2 : 0;


		if (isPitchPortrait()) {
			return scale(new Dimension(y * unscaledFieldSquare() + offset, (25 - x) * unscaledFieldSquare() + offset));
		}
		return scale(new Dimension(x * unscaledFieldSquare() + offset, y * unscaledFieldSquare() + offset));

	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate) {
		return mapToLocal(fieldCoordinate, false);
	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate, boolean addImageOffset) {
		return mapToLocal(fieldCoordinate.getX(), fieldCoordinate.getY(), addImageOffset);
	}

	public Direction mapToLocal(Direction direction) {
		if (isPitchPortrait()) {
			switch (direction) {
				case NORTHEAST:
					return Direction.NORTHWEST;
				case EAST:
					return Direction.NORTH;
				case SOUTHEAST:
					return Direction.NORTHEAST;
				case SOUTHWEST:
					return Direction.SOUTHEAST;
				case WEST:
					return Direction.SOUTH;
				case NORTHWEST:
					return Direction.SOUTHWEST;
				case NORTH:
					return Direction.WEST;
				case SOUTH:
					return Direction.EAST;
			}
		}

		return direction;
	}

	public Dimension scale(Dimension dimension) {
		return new Dimension(scale(dimension.width), scale(dimension.height));
	}

	public int scale(int size) {
		return scale(size, scale);
	}

	public int scale(int size, double scale) {
		return (int) (size * scale);
	}

	public double scale(double size) {
		return (size * scale);
	}

	public Rectangle scale(Rectangle rectangle) {
		return new Rectangle(scale(rectangle.x), scale(rectangle.y), scale(rectangle.width), scale(rectangle.height));
	}

	public BufferedImage scaleImage(BufferedImage pImage) {
		if (scale == 1) {
			return pImage;
		}

		BufferedImage scaledImage = new BufferedImage(scale(pImage.getWidth()), scale(pImage.getHeight()), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp =
			new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		try {
			scaledImage = scaleOp.filter(pImage, scaledImage);
		} catch (Exception e) {
			// ignore
		}
		return scaledImage;
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

	public double largerScale() {
		return Math.min(MAX_SCALE_FACTOR, scale + scaleStep);
	}

	public double smallerScale() {
		return Math.max(MIN_SCALE_FACTOR, scale - scaleStep);
	}

	public enum ClientLayout {
		LANDSCAPE(false), PORTRAIT(true), SQUARE(true), WIDE(false);

		private final boolean portrait;

		ClientLayout(boolean portrait) {
			this.portrait = portrait;
		}
	}

	public enum Component {
		FIELD(new Dimension(782, 452), new Dimension(452, 782), new Dimension(1484, 857)),
		CHAT(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343), new Dimension(741, 139)),
		LOG(new Dimension(389, 226), new Dimension(389, 153), new Dimension(260, 343), new Dimension(741, 139)),
		REPLAY_CONTROL(new Dimension(389, 26), new Dimension(389, 26), new Dimension(260, 26), new Dimension(389, 26)),
		TURN_DICE_STATUS(new Dimension(SIDEBAR_WIDTH_L, 92), new Dimension(SIDEBAR_WIDTH_P, 101), new Dimension(SIDEBAR_WIDTH_L, 92)),
		RESOURCE(new Dimension(SIDEBAR_WIDTH_L, 168), new Dimension(SIDEBAR_WIDTH_P, 185), new Dimension(SIDEBAR_WIDTH_L, 484)),
		BUTTON_BOX(new Dimension(SIDEBAR_WIDTH_L, 22), new Dimension(SIDEBAR_WIDTH_P, 24), new Dimension(SIDEBAR_WIDTH_L, 22)),
		BOX(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472), new Dimension(SIDEBAR_WIDTH_L, 430)),
		PLAYER_DETAIL(new Dimension(SIDEBAR_WIDTH_L, 430), new Dimension(SIDEBAR_WIDTH_P, 472), new Dimension(SIDEBAR_WIDTH_L, 430)),
		SIDEBAR(new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(ClientLayout.LANDSCAPE)), new Dimension(SIDEBAR_WIDTH_P, sidebarHeight(ClientLayout.PORTRAIT)), new Dimension(SIDEBAR_WIDTH_L, sidebarHeight(ClientLayout.WIDE))),
		PLAYER_PORTRAIT(new Dimension(121, 147), new Dimension(133, 162), new Dimension(121, 147)),
		PLAYER_PORTRAIT_OFFSET(new Dimension(3, 32), new Dimension(13, 32), new Dimension(3, 32)),
		PLAYER_STAT_OFFSET(new Dimension(3, 179), new Dimension(4, 198), new Dimension(3, 179)),
		PLAYER_STAT_BOX(new Dimension(28, 29), new Dimension(31, 30), new Dimension(28, 29)),
		PLAYER_STAT_BOX_MISC(new Dimension(0, 14), new Dimension(0, 15), new Dimension(0, 14)),
		PLAYER_SPP_OFFSET(new Dimension(8, 222), new Dimension(10, 245), new Dimension(8, 222)),
		PLAYER_SKILL_OFFSET(new Dimension(8, 246), new Dimension(10, 270), new Dimension(8, 246)),
		BOX_BUTTON(new Dimension(72, 22), new Dimension(82, 22), new Dimension(72, 22)),
		END_TURN_BUTTON(new Dimension(143, 31), new Dimension(163, 34),new Dimension(143, 31)),
		SCORE_BOARD(new Dimension(782, 32), new Dimension(782, 32), new Dimension(260, 96), new Dimension(1486, 32)),
		REPLAY_ICON_GAP(new Dimension(10, 0), new Dimension(10, 0), new Dimension(0, 0), new Dimension(10, 0)),
		REPLAY_ICON(new Dimension(36, 0), new Dimension(36, 0), new Dimension(30, 0), new Dimension(36, 0)),
		FIELD_SQUARE(new Dimension(30, 30), new Dimension(30, 30)),
		INDUCEMENT_COUNTER_SIZE(new Dimension(15, 15)),
		RESOURCE_SLOT(new Dimension(46, 40)),
		MAX_ICON(new Dimension(40, 40), new Dimension(76, 76)),
		ABOUT_DIALOG(new Dimension(813, 542)),
		CLIENT_SIZE(new Dimension(1078, 762), new Dimension(788, 1019), new Dimension(1050, 834), new Dimension(1920, 1080)),
		BOX_SQUARE(new Dimension(39, 39));

		private final Map<ClientLayout, Dimension> dimensions = new HashMap<>();

		Component(Dimension landscape, Dimension portrait, Dimension square, Dimension wide) {
			dimensions.put(ClientLayout.LANDSCAPE, landscape);
			dimensions.put(ClientLayout.PORTRAIT, portrait);
			dimensions.put(ClientLayout.SQUARE, square);
			dimensions.put(ClientLayout.WIDE, wide);
		}

		Component(Dimension landscape, Dimension portrait, Dimension wide) {
			this(landscape, portrait, portrait, wide);
		}

		Component(Dimension landscape, Dimension wide) {
			this(landscape, landscape, landscape, wide);
		}

		Component(Dimension landscape) {
			this(landscape, landscape, landscape, landscape);
		}

		private static int sidebarHeight(ClientLayout layout) {
			return (int) Arrays.stream(new Component[]{Component.TURN_DICE_STATUS, Component.RESOURCE, Component.BOX, Component.BUTTON_BOX})
				.map(comp -> comp.dimension(layout)).mapToDouble(Dimension::getHeight).sum();
		}

		private Dimension dimension(ClientLayout layout) {
			return dimensions.get(layout);
		}
	}
}
