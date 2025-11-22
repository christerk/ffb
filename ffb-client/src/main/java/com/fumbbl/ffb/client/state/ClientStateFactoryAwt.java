package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.common.ClientStateLogin;
import com.fumbbl.ffb.client.state.common.ClientStateReplay;
import com.fumbbl.ffb.client.state.common.ClientStateSpectate;
import com.fumbbl.ffb.util.Scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kalimar
 */
public class ClientStateFactoryAwt extends ClientStateFactory<FantasyFootballClientAwt> {
	
	public ClientStateFactoryAwt(FantasyFootballClientAwt client) {
		super(client);
	}

	public void registerStates() {
		register(new ClientStateLogin(client));
		register(new ClientStateSpectate(client));
		register(new ClientStateReplay(client));
	}


	@Override
	public void registerStatesForRules() {

		new Scanner<>(ClientStateAwt.class).getSubclassInstances(client.getGame().getOptions(), (cls) -> {
			@SuppressWarnings("rawtypes") Constructor<? extends ClientStateAwt> constructor;
			try {
				constructor = cls.getConstructor(FantasyFootballClientAwt.class);
				return constructor.newInstance(client);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new FantasyFootballException("Error constructing client state for class " + cls.getCanonicalName(), e);
			}
		}).forEach(this::register);
	}
}
