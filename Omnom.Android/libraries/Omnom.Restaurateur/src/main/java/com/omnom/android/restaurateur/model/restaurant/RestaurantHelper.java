package com.omnom.android.restaurateur.model.restaurant;

import com.omnom.android.entrance.BarEntranceData;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.entrance.TableEntranceData;
import com.omnom.android.entrance.TakeawayEntranceData;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.restaurateur.R;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.schedule.DailySchedule;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.utils.StringUtils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;

import rx.Observable;

/**
 * Created by Ch3D on 09.09.2014.
 */
public class RestaurantHelper {

    public static final String COLOR_PREFIX = "#";

    public static String getAddress(final Context context, final Restaurant restaurant) {
        final Address address = restaurant.address();
        if (address != null) {
            final String floor = !TextUtils.isEmpty(address.getFloor())
                    ? address.getFloor() + context.getString(R.string.floor_suffix)
                    : StringUtils.EMPTY_STRING;
            return StringUtils.concat(context.getString(R.string.restaurant_address_delimiter),
                    address.getCity(),
                    address.getStreet(),
                    address.getBuilding(),
                    floor);
        }
        return StringUtils.EMPTY_STRING;
    }

    public static String getAddressSmall(final Context context, final Restaurant restaurant) {
        final Address address = restaurant.address();
        if (address != null) {
            final String floor = !TextUtils.isEmpty(address.getFloor())
                    ? StringUtils.WHITESPACE + address.getFloor()
                    + StringUtils.NON_BREAKING_WHITESPACE +
                    context.getString(R.string.floor_suffix) : StringUtils.EMPTY_STRING;
            return StringUtils.concat(context.getString(R.string.restaurant_address_delimiter),
                    address.getStreet() + StringUtils.WHITESPACE + address.getBuilding(),
                    floor);
        }
        return StringUtils.EMPTY_STRING;
    }

    public static String getLogo(Restaurant restaurant) {
        if (restaurant != null && restaurant.decoration() != null) {
            return restaurant.decoration().getLogo();
        }
        return StringUtils.EMPTY_STRING;
    }

    public static int getBackgroundColor(Restaurant restaurant) {
        if (restaurant == null || restaurant.decoration() == null) {
            return Color.BLACK;
        }
        return getBackgroundColor(restaurant.decoration().getBackgroundColor());
    }

    public static int getBackgroundColor(String decorationBg) {
        if (!decorationBg.startsWith(COLOR_PREFIX)) {
            return Color.parseColor(COLOR_PREFIX + decorationBg);
        } else {
            return Color.parseColor(decorationBg);
        }
    }

    public static String getOpenedTime(Context context, Restaurant restaurant, int weekDay) {
        final DailySchedule dailySchedule = getDailySchedule(restaurant, weekDay);
        if (dailySchedule.isClosed()) {
            return context.getString(R.string.restaurant_closed);
        }
        if (TextUtils.isEmpty(dailySchedule.getCloseTime()) || TextUtils
                .isEmpty(dailySchedule.getOpenTime())) {
            return StringUtils.EMPTY_STRING;
        }
        return context
                .getString(R.string.restaurant_schedule_from_till, dailySchedule.getOpenTime(),
                        dailySchedule.getCloseTime());
    }

    /**
     * weekDay is integer in form of {@code Calendar.MODAY} ,..., {@code Calendar.SUNDAY}
     *
     * @see java.util.Calendar#MONDAY
     * @see java.util.Calendar#SUNDAY
     */
    public static DailySchedule getDailySchedule(Restaurant restaurant, int weekDay) {
        return getDailySchedule(restaurant.schedules(), weekDay);
    }

    public static DailySchedule getOrderSchedule(Restaurant restaurant, int weekDay) {
        return getDailySchedule(restaurant.orderSchedules(), weekDay);
    }

    private static DailySchedule getDailySchedule(final Schedules schedules, int weekDay) {
        if (schedules == null) {
            return DailySchedule.NULL;
        }
        switch (weekDay) {
            case Calendar.MONDAY:
                return schedules.getWorkingSchedule().getMonday();
            case Calendar.TUESDAY:
                return schedules.getWorkingSchedule().getTuesday();
            case Calendar.WEDNESDAY:
                return schedules.getWorkingSchedule().getWednesday();
            case Calendar.THURSDAY:
                return schedules.getWorkingSchedule().getThursday();
            case Calendar.FRIDAY:
                return schedules.getWorkingSchedule().getFriday();
            case Calendar.SATURDAY:
                return schedules.getWorkingSchedule().getSaturday();
            case Calendar.SUNDAY:
                return schedules.getWorkingSchedule().getSunday();

            default:
                return schedules.getWorkingSchedule().getMonday();
        }
    }

    public static boolean isMenuEnabled(final Restaurant restaurant) {
        return hasSettings(restaurant) && restaurant.settings().hasMenu();
    }

    public static boolean isBar(final Restaurant restaurant) {
        if (restaurant == null) {
            return false;
        }
        return restaurant.isBar();
    }

    public static boolean isLunch(final Restaurant restaurant) {
        if (restaurant == null) {
            return false;
        }
        return restaurant.isLunch();
    }

    public static boolean isTakeAway(final Restaurant restaurant) {
        if (restaurant == null) {
            return false;
        }
        return restaurant.isTakeAway();
    }

    public static String getBarUri(final Restaurant restaurant) {
        if (restaurant == null) {
            return StringUtils.EMPTY_STRING;
        }
        return restaurant.ordersPaidUrl();
    }

    public static Observable<MenuResponse> getMenuObservable(MenuObservableApi api,
            Restaurant restaurant) {
        if (restaurant == null || !RestaurantHelper.isMenuEnabled(restaurant)) {
            return Observable.just(MenuResponse.EMPTY);
        }
        return api.getMenu(restaurant.id());
    }

    public static boolean isPromoEnabled(final Restaurant restaurant) {
        return hasSettings(restaurant) && restaurant.settings().hasPromo();
    }

    public static boolean isWaiterEnabled(final Restaurant restaurant) {
        return hasSettings(restaurant) && restaurant.settings().hasWaiterCall();
    }

    private static boolean hasSettings(final Restaurant restaurant) {
        return restaurant != null && restaurant.settings() != null;
    }

    public static String getBackground(final Restaurant restaurant, final int widthPixels) {
        if (restaurant != null && restaurant.decoration() != null) {
            final String backgroundImage = restaurant.decoration().getBackgroundImage();
            if (TextUtils.isEmpty(backgroundImage)) {
                return StringUtils.EMPTY_STRING;
            }
            return backgroundImage + "?w=" + widthPixels;
        }
        return StringUtils.EMPTY_STRING;
    }

    public static boolean hasOnlyTable(final Restaurant restaurant) {
        return restaurant != null && restaurant.tables() != null && restaurant.tables().size() == 1;
    }

    public static boolean hasOrders(final Restaurant restaurant) {
        return restaurant != null && restaurant.orders() != null && restaurant.orders().size() > 0;
    }

    public static boolean hasTables(final Restaurant restaurant) {
        return restaurant != null && restaurant.tables() != null && restaurant.tables().size() > 0;
    }

    public static TableDataResponse getTable(final Restaurant restaurant) {
        TableDataResponse table = null;
        if (hasOnlyTable(restaurant)) {
            table = restaurant.tables().get(0);
        } else if (!hasTables(restaurant) && hasOrders(restaurant)) {
            final Order order = restaurant.orders().get(0);
            // TODO: add internal table id if necessary
            table = new TableDataResponse(order.getTableId(), 0,
                    new ArrayList<String>(), restaurant.id());
        }

        return table;
    }

    public static boolean hasBar(final Restaurant restaurant) {
        return restaurant.settings() != null && restaurant.settings().hasBar();
    }

    public static boolean hasTableOrder(final Restaurant restaurant) {
        return restaurant.settings() != null && restaurant.settings().hasTableOrder();
    }

    public static boolean hasPreOrder(final Restaurant restaurant) {
        return restaurant.settings() != null && restaurant.settings().hasPreOrder();
    }

    public static boolean hasTakeaway(final Restaurant restaurant) {
        return false;
    }

    public static EntranceData getEntranceData(final Restaurant restaurant) {
        if (isBar(restaurant)) {
            return BarEntranceData.create();
        }
        if (isTakeAway(restaurant)) {
            return TakeawayEntranceData.create();
        }
        return TableEntranceData.create();
    }
}