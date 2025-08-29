package com.niycco;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCEmojis implements ModInitializer {
	public static final String MOD_ID = "cc-emojis";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("CCEmojis initialized");
	}
}