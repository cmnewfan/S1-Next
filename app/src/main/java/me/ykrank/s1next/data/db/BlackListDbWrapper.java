package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

import io.reactivex.Single;
import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao.Properties;

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class BlackListDbWrapper {
    private final AppDaoSessionManager appDaoSessionManager;

    BlackListDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        this.appDaoSessionManager = appDaoSessionManager;
    }

    public static BlackListDbWrapper getInstance() {
        return App.getAppComponent().getBlackListDbWrapper();
    }

    private BlackListDao getBlackListDao() {
        return appDaoSessionManager.getDaoSession().getBlackListDao();
    }

    @NonNull
    public List<BlackList> getAllBlackList(int limit, int offset) {
        return getBlackListDao().queryBuilder()
                .limit(limit)
                .offset(offset)
                .list();
    }

    public Single<Cursor> getBlackListCursor() {
        return Single.just(getBlackListDao().queryBuilder())
                .map(builder -> builder.buildCursor().query());
    }

    @NonNull
    public BlackList fromCursor(@NonNull Cursor cursor) {
        return getBlackListDao().readEntity(cursor, 0);
    }

    /**
     * 默认情况下的黑名单查找。如果用户id合法则优先id，否则查找用户name
     *
     * @param id
     * @param name
     * @return
     */
    @Nullable
    public BlackList getBlackListDefault(int id, String name) {
        BlackList oBlackList = null;
        if (id > 0) {
            oBlackList = getBlackListWithAuthorId(id);
        } else if (name != null && !TextUtils.isEmpty(name)) {
            oBlackList = getBlackListWithAuthorName(name);
        }
        return oBlackList;
    }

    /**
     * 根据用户id查找记录
     *
     * @param id
     * @return
     */
    @Nullable
    public BlackList getBlackListWithAuthorId(int id) {
        return getBlackListDao().queryBuilder()
                .where(Properties.AuthorId.eq(id))
                .unique();
    }

    /**
     * 根据用户名查找记录
     *
     * @param name
     * @return
     */
    @Nullable
    public BlackList getBlackListWithAuthorName(String name) {
        return getBlackListDao().queryBuilder()
                .where(Properties.Author.eq(name))
                .unique();
    }

    @BlackList.ForumFLag
    public int getForumFlag(int id, String name) {
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.getForum();
        return BlackList.NORMAL;
    }

    @BlackList.PostFLag
    public int getPostFlag(int id, String name) {
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.getPost();
        return BlackList.NORMAL;
    }

    public void saveBlackList(@NonNull BlackList blackList) {
        if (blackList.getAuthorId() <= 0 && TextUtils.isEmpty(blackList.getAuthor())) {
            return;
        }
        BlackList oBlackList = getBlackListDefault(blackList.getAuthorId(), blackList.getAuthor());
        if (oBlackList == null) {
            getBlackListDao().insert(blackList);
        } else {
            oBlackList.copyFrom(blackList);
            getBlackListDao().update(oBlackList);
        }
    }

    public void delBlackList(@NonNull BlackList blackList) {
        BlackList oBlackList = getBlackListDefault(blackList.getAuthorId(), blackList.getAuthor());
        if (oBlackList != null) {
            getBlackListDao().delete(oBlackList);
        }
    }

    public void delBlackLists(List<BlackList> blackLists) {
        getBlackListDao().deleteInTx(blackLists);
    }

    public void saveDefaultBlackList(int authorid, String author, String remark) {
        BlackList blackList = new BlackList();
        blackList.setAuthorId(authorid);
        blackList.setAuthor(author);
        blackList.setRemark(remark);
        blackList.setPost(BlackList.HIDE_POST);
        blackList.setForum(BlackList.HIDE_FORUM);
        blackList.setTimestamp(System.currentTimeMillis());
        saveBlackList(blackList);
    }

    public void delDefaultBlackList(int authorid, String author) {
        BlackList blackList = new BlackList();
        blackList.setAuthorId(authorid);
        blackList.setAuthor(author);
        delBlackList(blackList);
    }
}
