package com.omnom.android.currency;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.omnom.android.utils.utils.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by Ch3D on 26.05.2015.
 */
public class Money implements Parcelable {
	public static final Creator<Money> CREATOR = new Creator<Money>() {

		@Override
		public Money createFromParcel(Parcel in) {
			return new Money(in);
		}

		@Override
		public Money[] newArray(int size) {
			return new Money[size];
		}
	};

	private static final HashMap<Currency, Money> sZeroMap = new HashMap<>();

	private static final String TAG = Money.class.getSimpleName();

	public static Money max(Money money1, Money money2) {
		return money1.isGreatherThan(money2) ? money1 : money2;
	}

	public static Money min(Money money1, Money money2) {
		return money1.isLessThan(money2) ? money1 : money2;
	}

	public static final Money createFractional(long fractionalAmount, Currency currency) {
		return new Money(fractionalAmount, currency);
	}

	public static final Money createFractional(double fractionalAmount, Currency currency) {
		return new Money((long) fractionalAmount, currency);
	}

	/**
	 * Use this method with caution - it will return #Money with value calculated
	 * based on amount and currency's fraction factor
	 *
	 * @return Money = {@code amount} * {@code currency.getFractionalFactor()}
	 */
	@Deprecated
	public static final Money create(double amount, Currency currency) {
		return createFractional((long) (amount * currency.getFractionalFactor()), currency);
	}

	public static Money getZero(Currency currency) {
		Money money = sZeroMap.get(currency);
		if(money != null) {
			return money;
		}
		money = Money.createFractional(0, currency);
		sZeroMap.put(currency, money);
		return money;
	}

	private final long mFractionalAmount;

	private final Currency mCurrency;

	private final BigDecimal mBaseAmount;

	public Money(Parcel parcel) {
		mFractionalAmount = parcel.readLong();
		mCurrency = parcel.readParcelable(Currency.class.getClassLoader());
		mBaseAmount = new BigDecimal(parcel.readString());
	}

	private Money(final long fractionalAmount, final Currency currency) {
		mFractionalAmount = fractionalAmount;
		if(fractionalAmount == 0) {
			mBaseAmount = BigDecimal.ZERO;
		} else {
			mBaseAmount = BigDecimal.valueOf(fractionalAmount).divide(currency.getDecimalFactor());
		}
		mCurrency = currency;
	}

	public final long getFractionalValue() {
		return mFractionalAmount;
	}

	public final Currency getCurrency() {
		return mCurrency;
	}

	public final BigDecimal getBaseValue() {
		return mBaseAmount;
	}

	public final Money plus(final Money m1) {
		if(m1.getCurrency() != getCurrency()) {
			throw new IllegalArgumentException("Unable to sum different currencies");
		}
		return new Money(getFractionalValue() + m1.getFractionalValue(), getCurrency());
	}

	public final Money subtract(final Money m1) {
		if(m1.getCurrency() != getCurrency()) {
			throw new IllegalArgumentException("Unable to sum different currencies");
		}
		return new Money(getFractionalValue() - m1.getFractionalValue(), getCurrency());
	}

	public String getReadableValue() {
		final double value = mBaseAmount.doubleValue();
		if(value == (long) value) {
			return String.format("%d", (long) value);
		} else {
			return String.format("%.2f", value);
		}
	}

	public String getReadableCurrencyValue() {
		return getReadableValue() + StringUtils.NON_BREAKING_WHITESPACE + mCurrency.getSymbol();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(mFractionalAmount);
		dest.writeParcelable(mCurrency, flags);
		dest.writeString(mBaseAmount.toString());
	}

	public boolean isNegativeOrZero() {
		return mFractionalAmount <= 0;
	}

	public boolean isLessThan(final Money money) {
		return mFractionalAmount < money.getFractionalValue();
	}

	public boolean isLessOrEquals(final Money money) {
		return mFractionalAmount <= money.getFractionalValue();
	}

	public boolean isGreatherThan(final Money money) {
		return mFractionalAmount > money.getFractionalValue();
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		final Money money = (Money) o;

		if(mFractionalAmount != money.mFractionalAmount) {
			return false;
		}
		if(mCurrency != null ? !mCurrency.equals(money.mCurrency) : money.mCurrency != null) {
			return false;
		}
		return !(mBaseAmount != null ? !mBaseAmount.equals(money.mBaseAmount) : money.mBaseAmount != null);

	}

	@Override
	public int hashCode() {
		int result = (int) (mFractionalAmount ^ (mFractionalAmount >>> 32));
		result = 31 * result + (mCurrency != null ? mCurrency.hashCode() : 0);
		result = 31 * result + (mBaseAmount != null ? mBaseAmount.hashCode() : 0);
		return result;
	}

	public boolean isZero() {
		return mFractionalAmount == 0;
	}

	public Money getPercent(final int percent) {
		if(percent < 0) {
			// throw new IllegalArgumentException("Wrong percent parameter value = " + percent);
			Log.w(TAG, "Trying to get " + percent + "% of " + toDebugString());
		}

		if(mFractionalAmount <= 0 || percent <= 0) {
			return Money.getZero(getCurrency());
		}

		return createFractional(mFractionalAmount * ((double) percent / (double) 100), mCurrency);
	}

	public Money multiply(final double factor) {
		return Money.createFractional(mFractionalAmount * factor, mCurrency);
	}

	public Money add(final Money amountTips) {
		return plus(amountTips);
	}

	@Override
	public String toString() {
		return getReadableCurrencyValue();
	}

	public String format(final char decimalSeparator) {
		return StringUtils.formatCurrency(String.valueOf(decimalSeparator), getReadableCurrencyValue());
	}

	public boolean isGreatherOrEquals(final Money value) {
		return isGreatherThan(value) || mFractionalAmount == value.getFractionalValue();
	}

	public Money round() {
		final long round = Math.round(mBaseAmount.doubleValue());
		return create(round, getCurrency());
	}

	public String toDebugString() {
		return "Money{" +
				"mFractionalAmount=" + mFractionalAmount +
				", mCurrency=" + mCurrency.getCode() +
				", mBaseAmount=" + mBaseAmount +
				'}';
	}

	public Money divide(final int value) {
		final float breRoundValue = (float) (getFractionalValue()) / (float) value;
		final BigDecimal bigDecimalValue = new BigDecimal(String.valueOf(breRoundValue)).setScale(0, BigDecimal.ROUND_UP);
		return Money.createFractional(Math.round(bigDecimalValue.floatValue()), mCurrency);
	}
}
