package com.omnom.android.currency;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.utils.utils.StringUtils;

import java.math.BigDecimal;

import hugo.weaving.DebugLog;

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

	public static final Money ZERO = createFractional(0, Currency.NULL);

	public static final Money createFractional(long fractionalAmount, Currency currency) {
		return new Money(fractionalAmount, currency);
	}

	public static final Money createFractional(double fractionalAmount, Currency currency) {
		return new Money((long) fractionalAmount, currency);
	}

	@Deprecated
	public static final Money create(double amount, Currency currency) {
		return createFractional((long) (amount * currency.getFractionalFactor()), currency);
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
			mBaseAmount = BigDecimal.valueOf(fractionalAmount / currency.getFractionalFactor());
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
		if(m1.getCurrency() != getCurrency() && m1.mCurrency != Currency.NULL && mCurrency != Currency.NULL) {
			throw new IllegalArgumentException("Unable to sum different currencies");
		}
		return new Money(getFractionalValue() + m1.getFractionalValue(), getCurrency());
	}

	public final Money subtract(final Money m1) {
		if(m1.getCurrency() != getCurrency() && m1.mCurrency != Currency.NULL && mCurrency != Currency.NULL) {
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
		return getReadableValue() + "\u00A0" + mCurrency.getSymbol();
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

	@DebugLog
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
			throw new IllegalArgumentException("Wrong percent parameter value = " + percent);
		}

		if(mFractionalAmount <= 0 || percent == 0) {
			return Money.ZERO;
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
}
