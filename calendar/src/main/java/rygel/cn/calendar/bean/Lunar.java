package rygel.cn.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import rygel.cn.calendar.utils.LunarUtils;

/**
 * 农历日期
 * @author Rygel
 */
public class Lunar implements Parcelable {

    public boolean isLeap = false;
    public int lunarYear = 1901;
    public int lunarMonth = 1;
    public int lunarDay = 1;

    public Lunar() { }

    public Lunar(boolean isLeap, int lunarYear, int lunarMonth, int lunarDay) {
        this.isLeap = isLeap;
        this.lunarYear = lunarYear;
        this.lunarMonth = lunarMonth;
        this.lunarDay = lunarDay;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Lunar)){
            return false;
        }
        return ((Lunar) obj).lunarDay == lunarDay &&
                ((Lunar) obj).lunarMonth == lunarMonth &&
                ((Lunar) obj).lunarYear == lunarYear &&
                ((Lunar) obj).isLeap == isLeap;
    }

    @Override
    public String toString() {
        return "Lunar{" +
                "isLeap=" + isLeap +
                ", lunarYear=" + lunarYear +
                ", lunarMonth=" + lunarMonth +
                ", lunarDay=" + lunarDay +
                '}';
    }

    /**
     * 农历转成公历
     * @return
     */
    public Solar toSolar() {
        return LunarUtils.lunarToSolar(this);
    }

    /****************************************** 支持Parcelable ****************************************/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isLeap ? 1 : 0));
        dest.writeInt(lunarYear);
        dest.writeInt(lunarMonth);
        dest.writeInt(lunarDay);
    }

    public final static Creator<Lunar> CREATOR = new Creator<Lunar>() {
        @Override
        public Lunar createFromParcel(Parcel source) {
            Lunar lunar = new Lunar();
            lunar.isLeap = source.readByte() != 0;
            lunar.lunarYear = source.readInt();
            lunar.lunarMonth = source.readInt();
            lunar.lunarDay = source.readInt();
            return lunar;
        }

        @Override
        public Lunar[] newArray(int size) {
            return new Lunar[size];
        }
    };

}
