package com.fumbbl.ffb.net.commands;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.application.NetCommandIdFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserSettingsCompatibilityTest {
	@Test
	void legacyDynamicPitchSettingCanBeReadButIsNotSaved() {
		IFactorySource source = mock(IFactorySource.class);
		when(source.forContext(any())).thenReturn(source);
		when(source.<NetCommandIdFactory>getFactory(Factory.NET_COMMAND_ID)).thenReturn(new NetCommandIdFactory());
		ServerCommandUserSettings oldSettings = new ServerCommandUserSettings(
			new CommonProperty[] {CommonProperty.SETTING_DYNAMIC_PITCH_SCALING, CommonProperty.SETTING_SCALE_FACTOR},
			new String[] {"dynamicPitchScalingOff", "1.5"});

		ServerCommandUserSettings restored = new ServerCommandUserSettings().initFrom(source, oldSettings.toJsonValue());

		assertEquals("dynamicPitchScalingOff", restored.getUserSettingValue(CommonProperty.SETTING_DYNAMIC_PITCH_SCALING));
		assertEquals("1.5", restored.getUserSettingValue(CommonProperty.SETTING_SCALE_FACTOR));
		assertFalse(Arrays.asList(CommonProperty._SAVED_USER_SETTINGS).contains(CommonProperty.SETTING_DYNAMIC_PITCH_SCALING));
	}
}
