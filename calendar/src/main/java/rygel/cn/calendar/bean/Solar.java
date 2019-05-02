package rygel.cn.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import rygel.cn.calendar.utils.LunarUtils;
import rygel.cn.calendar.utils.SolarUtils;

/**
 * 公历日期
 * @author Rygel
 */
public class Solar implements Parcelable {

    public int solarYear = 1901;
    public int solarMonth = 1;
    public int solarDay = 1;

    public Solar() { }

    public Solar(int solarYear, int solarMonth, int solarDay) {
        this.solarYear = solarYear;
        this.solarMonth = solarMonth;
        this.solarDay = solarDay;
    }

    /**
     * 返回上一天的日期
     * @return
     */
    public Solar last() {
        Solar solar = new Solar(solarYear,solarMonth,solarDay);
        solar.solarDay--;
        if(solar.solarDay < 1){
            solar.solarMonth--;
            if(solar.solarMonth == 0){
                solar.solarYear--;
                solar.solarMonth = 12;
            }
            solar.solarDay = SolarUtils.getMonthDay(solar.solarYear,solar.solarMonth);
        }
        return solar;
    }

    /**
     * 返回下一天的日期
     * @return
     */
    public Solar next() {
        Solar solar = new Solar(solarYear,solarMonth,solarDay);
        solar.solarDay++;
        if(solar.solarDay > SolarUtils.getMonthDay(solar.solarYear,solar.solarMonth)){
            solar.solarDay = 1;
            solar.solarMonth++;
            if(solar.solarMonth == 13){
                solar.solarMonth = 1;
                solar.solarYear++;
            }
        }
        return solar;
    }

    @Override
    public String toString() {
        return "Solar{" +
                "solarYear=" + solarYear +
                ", solarMonth=" + solarMonth +
                ", solarDay=" + solarDay +
                '}';
    }

    /**
     * 转成农历日期
     * @return
     */
    public Lunar toLunar() {
        return LunarUtils.solarToLunar(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Solar)){
            return false;
        }
        return ((Solar) obj).solarDay == solarDay &&
                ((Solar) obj).solarMonth == solarMonth &&
                ((Solar) obj).solarYear == solarYear;
    }

    /****************************************** 支持Parcelable ****************************************/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(solarYear);
        dest.writeInt(solarMonth);
        dest.writeInt(solarDay);
    }

    public final static Creator<Solar> CREATOR = new Creator<Solar>() {
        @Override
        public Solar createFromParcel(Parcel source) {
            return new Solar(source.readInt(),source.readInt(),source.readInt());
        }

        @Override
        public Solar[] newArray(int size) {
            return new Solar[size];
        }
    };

}
