package com.omnom.android.currency;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Ch3D on 28.05.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyTest {
	@Test
	public void testSymbols() {
		Assert.assertTrue(Currency.RU.getSymbol().equals("\uF5FC"));
		Assert.assertTrue(Currency.US.getSymbol().equals("\uFE69"));
	}

	@Test
	public void testHashcode() {
		Assert.assertTrue(Currency.RU.hashCode() != Currency.US.hashCode());
		Assert.assertTrue(Currency.RU.hashCode() == Currency.RU.hashCode());
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(Currency.RU.equals(Currency.RU));
		Assert.assertFalse(Currency.RU.equals(Currency.US));
		Assert.assertFalse(Currency.US.equals(Currency.RU));
	}
}
