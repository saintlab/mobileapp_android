package com.omnom.android.service.configuration;

import android.location.Location;

import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.restaurateur.model.config.Config;

/**
 * Created by mvpotter on 2/12/2015.
 */
public final class ConfigurationResponse {

	private UserResponse userResponse;
	private Config config;
	private Location location;
	private AuthResponse logLocationResponse;

	public ConfigurationResponse(UserResponse userResponse, Config config, Location location, AuthResponse logLocationResponse) {
		this.userResponse = userResponse;
		this.config = config;
		this.location = location;
		this.logLocationResponse = logLocationResponse;
	}

	public UserResponse getUserResponse() {
		return userResponse;
	}

	public Config getConfig() {
		return config;
	}

	public Location getLocation() {
		return location;
	}

	public AuthResponse getLogLocationResponse() {
		return logLocationResponse;
	}

}
