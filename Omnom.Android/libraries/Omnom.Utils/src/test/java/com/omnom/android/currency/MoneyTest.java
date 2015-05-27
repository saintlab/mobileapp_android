package com.omnom.android.currency;

import com.omnom.android.utils.utils.StringUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 27.05.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class MoneyTest {

	@Test
	public void testCreation() {
		Assert.assertEquals(Money.createFractional(0, Currency.RU).getFractionalValue(), 0);
		Assert.assertEquals(Money.createFractional(0, Currency.RU).getBaseValue(), BigDecimal.ZERO);

		Assert.assertEquals(Money.createFractional(100, Currency.RU).getFractionalValue(), 100);
		Assert.assertEquals(Money.createFractional(100, Currency.RU).getBaseValue(), BigDecimal.ONE);
		Assert.assertEquals(Money.createFractional(100, Currency.RU).getBaseValue(), BigDecimal.valueOf(1));

		Assert.assertEquals(Money.createFractional(210, Currency.RU).getFractionalValue(), 210);
		Assert.assertEquals(Money.createFractional(210, Currency.RU).getBaseValue(), BigDecimal.valueOf(2.1));
		Assert.assertEquals(Money.createFractional(210, Currency.RU).getBaseValue(), BigDecimal.valueOf(2.10));

		Assert.assertEquals(Money.create(210, Currency.RU).getBaseValue(), BigDecimal.valueOf(210));
		Assert.assertEquals(Money.create(210, Currency.RU).getFractionalValue(), 21000);

		Assert.assertEquals(Money.create(10, Currency.RU).getFractionalValue(), 1000);
		Assert.assertEquals(Money.create(10, Currency.RU).getBaseValue(), BigDecimal.TEN);

		Assert.assertEquals(Money.create(0, Currency.RU).getFractionalValue(), 0);
		Assert.assertEquals(Money.create(0, Currency.RU).getBaseValue(), BigDecimal.ZERO);
	}

	@Test
	public void testReadableCurrencyValue() {
		Assert.assertEquals(Money.createFractional(100, Currency.RU).getReadableCurrencyValue(),
		                    "1" + StringUtils.NON_BREAKING_WHITESPACE + Currency.RU.getSymbol());

		Assert.assertEquals(Money.createFractional(201, Currency.RU).getReadableCurrencyValue(),
		                    "2,01" + StringUtils.NON_BREAKING_WHITESPACE + Currency.RU.getSymbol());

		Assert.assertEquals(Money.createFractional(1, Currency.RU).getReadableCurrencyValue(),
		                    "0,01" + StringUtils.NON_BREAKING_WHITESPACE + Currency.RU.getSymbol());

		Assert.assertEquals(Money.createFractional(10, Currency.RU).getReadableCurrencyValue(),
		                    "0,10" + StringUtils.NON_BREAKING_WHITESPACE + Currency.RU.getSymbol());
	}

	@Test
	public void testReadableValue() {
		Assert.assertEquals(Money.createFractional(100, Currency.RU).getReadableValue(), "1");
		Assert.assertEquals(Money.createFractional(201, Currency.RU).getReadableValue(), "2,01");
		Assert.assertEquals(Money.createFractional(1, Currency.RU).getReadableValue(), "0,01");
		Assert.assertEquals(Money.createFractional(10, Currency.RU).getReadableValue(), "0,10");
	}

	@Test
	public void testMoneySubtract() {
		// 100 - 200 = -100
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .subtract(Money.createFractional(200, Currency.RU))
		                       .equals(Money.createFractional(-100, Currency.RU)));

		// -100 - 200 = -300
		Assert.assertTrue(Money.createFractional(-100, Currency.RU)
		                       .subtract(Money.createFractional(200, Currency.RU))
		                       .equals(Money.createFractional(-300, Currency.RU)));

		// -100 - (-200) = 100
		Assert.assertTrue(Money.createFractional(-100, Currency.RU)
		                       .subtract(Money.createFractional(-200, Currency.RU))
		                       .equals(Money.createFractional(100, Currency.RU)));

		// -0 - (-0) = 0
		Assert.assertTrue(Money.createFractional(-0, Currency.RU)
		                       .subtract(Money.createFractional(-0, Currency.RU))
		                       .equals(Money.createFractional(0, Currency.RU)));

		// 0 - 0 = 0
		Assert.assertTrue(Money.createFractional(0, Currency.RU)
		                       .subtract(Money.createFractional(0, Currency.RU))
		                       .equals(Money.createFractional(0, Currency.RU)));

		// 100 - 100 = 0
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .subtract(Money.createFractional(100, Currency.RU))
		                       .equals(Money.createFractional(0, Currency.RU)));
	}

	@Test
	public void testIsGreatherThan() {
		// 200 > 199
		Assert.assertTrue(Money.createFractional(200, Currency.RU)
		                       .isGreatherThan(Money.createFractional(199, Currency.RU)));

		// 200 !> 201
		Assert.assertFalse(Money.createFractional(200, Currency.RU)
		                        .isGreatherThan(Money.createFractional(201, Currency.RU)));

		// 1 > 0
		Assert.assertTrue(Money.createFractional(1, Currency.RU)
		                       .isGreatherThan(Money.createFractional(0, Currency.RU)));

		// 101 > -100
		Assert.assertTrue(Money.createFractional(101, Currency.RU)
		                       .isGreatherThan(Money.createFractional(-100, Currency.RU)));
	}

	@Test
	public void testIsLessThan() {
		Assert.assertTrue(Money.createFractional(200, Currency.RU)
		                       .isLessThan(Money.createFractional(201, Currency.RU)));

		Assert.assertTrue(Money.createFractional(0, Currency.RU)
		                       .isLessThan(Money.createFractional(201, Currency.RU)));

		Assert.assertTrue(Money.createFractional(-1, Currency.RU)
		                       .isLessThan(Money.createFractional(0, Currency.RU)));
	}

	@Test
	public void testIsLessOrEquals() {
		Assert.assertTrue(Money.createFractional(200, Currency.RU)
		                       .isLessOrEquals(Money.createFractional(201, Currency.RU)));

		Assert.assertTrue(Money.createFractional(200, Currency.RU)
		                       .isLessOrEquals(Money.createFractional(202, Currency.RU)));

		Assert.assertFalse(Money.createFractional(203, Currency.RU)
		                        .isLessOrEquals(Money.createFractional(202, Currency.RU)));
	}

	@Test
	public void testIsGreatherOrEquals() {
		Assert.assertFalse(Money.createFractional(200, Currency.RU)
		                        .isGreatherOrEquals(Money.createFractional(201, Currency.RU)));

		Assert.assertTrue(Money.create(200, Currency.RU)
		                       .isGreatherOrEquals(Money.createFractional(200, Currency.RU)));

		Assert.assertTrue(Money.create(20, Currency.RU)
		                       .isGreatherOrEquals(Money.createFractional(0, Currency.RU)));

		Assert.assertTrue(Money.createFractional(-1, Currency.RU)
		                       .isGreatherOrEquals(Money.createFractional(-1, Currency.RU)));

		Assert.assertTrue(Money.create(1, Currency.RU)
		                       .isGreatherOrEquals(Money.createFractional(-100, Currency.RU)));
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .equals(Money.createFractional(100, Currency.RU)));

		Assert.assertTrue(Money.createFractional(0, Currency.RU)
		                       .equals(Money.createFractional(0, Currency.RU)));

		Assert.assertTrue(Money.createFractional(-100, Currency.RU)
		                       .equals(Money.createFractional(-100, Currency.RU)));

		Assert.assertTrue(Money.create(1, Currency.RU)
		                       .equals(Money.createFractional(100, Currency.RU)));

		Assert.assertTrue(Money.create(0, Currency.RU)
		                       .equals(Money.createFractional(0, Currency.RU)));

		Assert.assertTrue(Money.create(-20, Currency.RU)
		                       .equals(Money.createFractional(-2000, Currency.RU)));
	}

	@Test
	public void testRound() {
		Assert.assertTrue(Money.createFractional(90, Currency.RU).round()
		                       .equals(Money.createFractional(100, Currency.RU)));

		Assert.assertTrue(Money.create(1.5, Currency.RU).round()
		                       .equals(Money.createFractional(200, Currency.RU)));

		Assert.assertTrue(Money.create(8.5, Currency.RU).round()
		                       .equals(Money.create(9, Currency.RU)));

		Assert.assertFalse(Money.create(8.4, Currency.RU).round()
		                       .equals(Money.create(9, Currency.RU)));

		Assert.assertTrue(Money.create(8.4, Currency.RU).round()
		                       .equals(Money.create(8, Currency.RU)));
	}

	@Test
	public void testMultiply() {
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .multiply(2)
		                       .equals(Money.createFractional(200, Currency.RU)));

		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .multiply(0)
		                       .equals(Money.createFractional(0, Currency.RU)));

		Assert.assertTrue(Money.create(1, Currency.RU)
		                       .multiply(1)
		                       .equals(Money.createFractional(100, Currency.RU)));

		Assert.assertTrue(Money.create(1, Currency.RU)
		                       .multiply(1.4)
		                       .equals(Money.createFractional(140, Currency.RU)));

		Assert.assertTrue(Money.create(1, Currency.RU)
		                       .multiply(-3.5)
		                       .equals(Money.createFractional(-350, Currency.RU)));
	}

	@Test
	public void testMoneySum() {
		// 100 + 200 = 300
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .plus(Money.createFractional(200, Currency.RU))
		                       .equals(Money.createFractional(300, Currency.RU)));

		// 0 + 200 = 200
		Assert.assertTrue(Money.createFractional(0, Currency.RU)
		                       .add(Money.createFractional(200, Currency.RU))
		                       .equals(Money.createFractional(200, Currency.RU)));

		// 100 + (-100) = 0
		Assert.assertTrue(Money.createFractional(100, Currency.RU)
		                       .plus(Money.createFractional(-100, Currency.RU))
		                       .equals(Money.createFractional(0, Currency.RU)));

		// -100 + (-100) = -200
		Assert.assertTrue(Money.createFractional(-100, Currency.RU)
		                       .add(Money.createFractional(-100, Currency.RU))
		                       .equals(Money.createFractional(-200, Currency.RU)));
	}
}
