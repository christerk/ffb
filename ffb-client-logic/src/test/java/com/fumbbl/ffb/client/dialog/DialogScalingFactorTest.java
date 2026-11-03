package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.JDesktopPane;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DialogScalingFactorTest {
	@Test
	void guiSliderUsesFixedRangeFrom25To100Percent() throws Exception {
		UserInterface userInterface = mock(UserInterface.class);
		SwingUtilities.invokeAndWait(() -> {
			DialogScalingFactor dialog = dialog(userInterface);
			JSlider guiSlider = sliders(dialog).get(1);
			assertEquals(5, guiSlider.getMinimum());
			assertEquals(20, guiSlider.getMaximum());
			guiSlider.setValue(30);
			assertEquals(20, guiSlider.getValue());
			dialog.hideDialog();
		});
	}

	@Test
	void cancelRestoresOriginalScaleAndWindowAfterPreview() throws Exception {
		UserInterface userInterface = mock(UserInterface.class);
		DialogScalingFactor[] dialog = new DialogScalingFactor[1];
		CountDownLatch previewed = new CountDownLatch(1);
		doAnswer(invocation -> { previewed.countDown(); return null; }).when(userInterface)
			.previewScale(anyDouble(), anyDouble(), any(LayoutSettings.class), any(Dimension.class));
		SwingUtilities.invokeAndWait(() -> {
			dialog[0] = dialog(userInterface);
			sliders(dialog[0]).get(0).setValue(30);
		});
		assertTrue(previewed.await(5, TimeUnit.SECONDS));
		SwingUtilities.invokeAndWait(() -> dialog[0].hideDialog());

		verify(userInterface).previewScale(eq(1.5), eq(1.0), any(LayoutSettings.class), eq(new Dimension(800, 600)));
		verify(userInterface).previewScale(eq(1.0), eq(1.0), any(LayoutSettings.class), eq(new Dimension(800, 600)));
	}

	@Test
	void confirmingAppliesPendingValuesAndDoesNotRestoreThemOnClose() throws Exception {
		UserInterface userInterface = mock(UserInterface.class);
		SwingUtilities.invokeAndWait(() -> {
			DialogScalingFactor dialog = dialog(userInterface);
			List<JSlider> sliders = sliders(dialog);
			sliders.get(0).setValue(30);
			sliders.get(1).setValue(15);
			button(dialog, "Confirm").doClick();
			assertEquals(1.5, dialog.getFactor());
			assertEquals(0.75, dialog.getGuiRatio());
			dialog.hideDialog();
		});

		verify(userInterface, times(1)).previewScale(eq(1.5), eq(0.75), any(LayoutSettings.class), eq(new Dimension(800, 600)));
	}

	@Test
	void previewsKeepTheOpeningSizeWhenTheDesktopSizeChanges() throws Exception {
		UserInterface userInterface = mock(UserInterface.class);
		DialogScalingFactor[] dialog = new DialogScalingFactor[1];
		CountDownLatch previewed = new CountDownLatch(1);
		doAnswer(invocation -> { previewed.countDown(); return null; }).when(userInterface)
			.previewScale(anyDouble(), anyDouble(), any(LayoutSettings.class), any(Dimension.class));
		SwingUtilities.invokeAndWait(() -> {
			dialog[0] = dialog(userInterface);
			sliders(dialog[0]).get(1).setValue(14);
		});
		assertTrue(previewed.await(5, TimeUnit.SECONDS));
		SwingUtilities.invokeAndWait(() -> {
			userInterface.getPitchDimensionProvider().getLayoutSettings().setGuiRatio(0.7);
			userInterface.getDesktop().setSize(950, 700);
			sliders(dialog[0]).get(1).setValue(20);
			button(dialog[0], "Confirm").doClick();
			dialog[0].hideDialog();
		});

		verify(userInterface).previewScale(eq(1.0), eq(0.7), any(LayoutSettings.class), eq(new Dimension(800, 600)));
		verify(userInterface).previewScale(eq(1.0), eq(1.0), any(LayoutSettings.class), eq(new Dimension(800, 600)));
		ArgumentCaptor<LayoutSettings> settings = ArgumentCaptor.forClass(LayoutSettings.class);
		verify(userInterface, times(2)).previewScale(eq(1.0), anyDouble(), settings.capture(), eq(new Dimension(800, 600)));
		assertSame(settings.getAllValues().get(0), settings.getAllValues().get(1));
		assertEquals(1.0, settings.getValue().getScale());
		assertEquals(1.0, settings.getValue().getGuiRatio());
	}

	private DialogScalingFactor dialog(UserInterface userInterface) {
		FantasyFootballClient client = mock(FantasyFootballClient.class);
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		PitchViewport viewport = new PitchViewport(new UiDimensionProvider(settings), settings);
		JDesktopPane desktop = new JDesktopPane();
		desktop.setSize(800, 600);
		when(client.getUserInterface()).thenReturn(userInterface);
		when(userInterface.getSize()).thenReturn(new Dimension(800, 600));
		when(userInterface.getDesktop()).thenReturn(desktop);
		when(userInterface.getPitchViewport()).thenReturn(viewport);
		when(userInterface.getPitchDimensionProvider()).thenReturn(new PitchDimensionProvider(settings, viewport));
		return new DialogScalingFactor(client);
	}

	private List<JSlider> sliders(Container container) {
		List<JSlider> result = new ArrayList<>();
		for (Component component : container.getComponents()) {
			if (component instanceof JSlider) {
				result.add((JSlider) component);
			} else if (component instanceof Container) {
				result.addAll(sliders((Container) component));
			}
		}
		return result;
	}

	private JButton button(Container container, String text) {
		for (Component component : container.getComponents()) {
			if (component instanceof JButton && text.equals(((JButton) component).getText())) {
				return (JButton) component;
			}
			if (component instanceof Container) {
				JButton found = button((Container) component, text);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}
}
