package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AdminYkrank on 2016/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
@Entity(nameInDb = "ReadProgress")
public class ReadProgress implements Parcelable {
    public static final Creator<ReadProgress> CREATOR = new Creator<ReadProgress>() {
        @Override
        public ReadProgress createFromParcel(Parcel in) {
            return new ReadProgress(in);
        }

        @Override
        public ReadProgress[] newArray(int size) {
            return new ReadProgress[size];
        }
    };
    private static final String TimeFormat = "yyyy-MM-dd HH:mm";
    @Id(autoincrement = true)
    @Nullable
    private Long id;
    /**
     * 帖子ID
     */
    @Property(nameInDb = "ThreadId")
    @Index(unique = true)
    private int threadId;
    /**
     * 页数
     */
    @Property(nameInDb = "Page")
    private int page;
    /**
     * 位置
     */
    @Property(nameInDb = "Position")
    private int position;
    /**
     * 更新时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    @SuppressWarnings("WrongConstant")
    protected ReadProgress(Parcel in) {
        boolean hasId = in.readByte() == 1;
        if (hasId) {
            id = in.readLong();
        }
        threadId = in.readInt();
        page = in.readInt();
        position = in.readInt();
        timestamp = in.readLong();
    }

    public ReadProgress() {
        this.timestamp = System.currentTimeMillis();
    }

    public ReadProgress(int threadId, int page, int position) {
        super();
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 1994466630)
    public ReadProgress(Long id, int threadId, int page, int position, long timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.page = page;
        this.position = position;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(threadId);
        dest.writeInt(page);
        dest.writeInt(position);
        dest.writeLong(timestamp);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static String getTimeFormat() {
        return TimeFormat;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void copyFrom(ReadProgress oReadProgress) {
        this.threadId = oReadProgress.threadId;
        this.page = oReadProgress.page;
        this.position = oReadProgress.position;
        this.timestamp = oReadProgress.timestamp;
    }

    @Override
    public String toString() {
        return "ReadProgress{" +
                "threadId=" + threadId +
                ", page=" + page +
                ", position=" + position +
                ", timestamp=" + getTime() +
                '}';
    }

    public void setId(long id) {
        this.id = id;
    }


}
