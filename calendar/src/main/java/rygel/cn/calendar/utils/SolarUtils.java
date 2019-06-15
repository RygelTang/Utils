package rygel.cn.calendar.utils;

import rygel.cn.calendar.bean.Solar;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 公历相关计算工具类
 * @author Rygel
 */
public class SolarUtils {

    private static final int[] LEAP_YEAR_DAYS_OF_MONTH = {31,29,31,30,31,30,31,31,30,31,30,31};

    private static final int[] COMMON_YEAR_DAYS_OF_MONTH = {31,28,31,30,31,30,31,31,30,31,30,31};

    /**
     * 获取当前日期
     * @return
     */
    public static Solar today() {
        Calendar today = Calendar.getInstance();
        today.setTimeZone(TimeZone.getDefault());
        return new Solar(today.get(Calendar.YEAR),today.get(Calendar.MONTH) + 1,today.get(Calendar.DATE));
    }

    /**
     * 用于判断这一天是否在某个月份中
     * @param solar
     * @param year
     * @param month
     * @return
     */
    public static boolean isDateInMonth(Solar solar, int year, int month) {
        return solar.solarYear == year && solar.solarMonth == month;
    }

    /**
     * 获取某月的天数
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDay(int year,int month){
        return isLeapYear(year) ? LEAP_YEAR_DAYS_OF_MONTH[month - 1] : COMMON_YEAR_DAYS_OF_MONTH[month - 1];
    }

    /**
     * 星期,0开始，0对应周日
     * @param solar
     * @return
     */
    public static int getWeekDay(Solar solar){
        return getIntervalDaysToBase(solar) % 7;
    }

    /**
     * 是否闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year){
        return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
    }

    /**
     * 一年内的第几天
     * @param day
     * @return
     */
    public static int getDaysInYear(Solar day){
        final boolean isLeapYear = isLeapYear(day.solarYear);
        int daysInYear = day.solarDay;
        for(int i = 0;i < day.solarMonth - 1;i++){
            if(isLeapYear){
                daysInYear += LEAP_YEAR_DAYS_OF_MONTH[i];
            }else{
                daysInYear += COMMON_YEAR_DAYS_OF_MONTH[i];
            }
        }
        return daysInYear;
    }

    /**
     * 求当年的第几天的日期
     * @param days
     * @param year
     * @return
     */
    private static Solar dayInYear(int days, int year){
        final boolean isLeapYear = isLeapYear(year);
        Solar solar = new Solar(year,0,days);
        while(days > 0){
            solar.solarDay = days;
            solar.solarMonth++;
            if(isLeapYear){
                days -= LEAP_YEAR_DAYS_OF_MONTH[solar.solarMonth - 1];
            }else {
                days -= COMMON_YEAR_DAYS_OF_MONTH[solar.solarMonth - 1];
            }
        }
        return solar;
    }

    /**
     * 判断两个日期哪个更大
     * @param date0
     * @param date1
     * @return
     */
    public static boolean compare(Solar date0, Solar date1){
        return ((date0.solarYear << 9) | (date0.solarMonth << 5) | date0.solarDay) -
                ((date1.solarYear << 9) | (date1.solarMonth << 5) | date1.solarDay) >= 0;
    }

    /**
     * 获取对应日期的上一天
     * @param solar
     * @return
     */
    public static Solar getYesterdayOf(Solar solar){
        solar.solarDay--;
        if(solar.solarDay < 1){
            solar.solarMonth--;
            if(solar.solarMonth == 0){
                solar.solarYear--;
                solar.solarMonth = 12;
            }
            solar.solarDay = getMonthDay(solar.solarYear,solar.solarMonth);
        }
        return solar;
    }

    /**
     * 获取对应日期的下一天
     * @param solar
     * @return
     */
    public static Solar getTomorrowdayOf(Solar solar){
        solar.solarDay++;
        if(solar.solarDay > getMonthDay(solar.solarYear,solar.solarMonth)){
            solar.solarDay = 1;
            solar.solarMonth++;
            if(solar.solarMonth == 13){
                solar.solarMonth = 1;
                solar.solarYear++;
            }
        }
        return solar;
    }

    /**
     * 计算日期间隔
     * @param start
     * @param end
     * @return
     */
    public static int getIntervalDays(Solar start, Solar end){
        return getIntervalDaysToBase(end) - getIntervalDaysToBase(start);
    }

    /**
     * 根据日期间隔计算日期
     * @param start
     * @param interval
     * @return
     */
    public static Solar getDayByInterval(Solar start, int interval){
        final int daysToBase = getIntervalDaysToBase(start);
        final int newDayToBase = daysToBase + interval;
        //大致估计年份
        int year = newDayToBase / 365;
        //大致估计闰年年份(因为年份是不准确的，所以这里也是不准确的)
        final int leapOffset = getLeapYearCount(year);
        //大致估计年份的偏差
        final int yearOffset = leapOffset / 365;
        //粗略修正偏差
        year -= yearOffset;
        Solar yearBase = new Solar(year,1,1);
        //计算当年的年初到标准年的间隔
        int yearBaseDaysToBase = getIntervalDaysToBase(yearBase);
        //误差
        int offset = newDayToBase - yearBaseDaysToBase + 1;
        int daysOfYear = isLeapYear(year) ? 366 : 365;
        while (offset < 0){
            yearBase = new Solar(--year,1,1);
            //计算当年的年初到标准年的间隔
            yearBaseDaysToBase = getIntervalDaysToBase(yearBase);
            //误差
            offset = newDayToBase - yearBaseDaysToBase + 1;
        }
        while (offset > daysOfYear){
            yearBase = new Solar(++year,1,1);
            //计算当年的年初到标准年的间隔
            yearBaseDaysToBase = getIntervalDaysToBase(yearBase);
            //误差
            offset = newDayToBase - yearBaseDaysToBase + 1;
            daysOfYear = isLeapYear(year) ? 366 : 365;
        }
        return dayInYear(offset,year);
    }

    /**
     * 计算到基准年的日期间隔
     * @param day
     * @return
     */
    private static int getIntervalDaysToBase(Solar day){
        final int leapYearCount = getLeapYearCountWithoutThisYear(day.solarYear);
        int daysInYear = getDaysInYear(day);
        return leapYearCount + (day.solarYear - 1) * 365 + daysInYear;
    }

    /**
     * 闰年的年数，不包含今年
     * @param year
     * @return
     */
    private static int getLeapYearCountWithoutThisYear(int year){
        return getLeapYearCount(--year);
    }

    /**
     * 闰年的年数，包含今年
     * @param year
     * @return
     */
    private static int getLeapYearCount(int year){
        return year / 4 - year / 100 + year / 400;
    }

}
