package cn.vacuumflask.greendao3demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.vacuumflask.greendao3demo.db.DaoMaster;
import cn.vacuumflask.greendao3demo.db.DaoSession;
import cn.vacuumflask.greendao3demo.db.UserDao;

/**
 * Created by Administrator on 2016/12/21 0021.
 * 操作用户表工具类
 */
public class UserDBHelper {
    private static SQLiteDatabase db;
    //表
    private UserDao userDao;

    private static UserDBHelper dbHelper;
    private static Context mContext;

    private UserDBHelper() {//单例
    }

    //单例模式得到DBHelper
    public static synchronized UserDBHelper getDbHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new UserDBHelper();
            if (mContext == null) {
                mContext = context;//上下文赋值
            }
            //拿到DaoSession对象
            DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext.getApplicationContext(), "User.db", null);
            db = openHelper.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            //拿到表对象
            dbHelper.userDao = daoSession.getUserDao();
        }
        return dbHelper;
    }

    /**
     * 判断该数据库是否存在该名字
     *
     * @param name 用户名
     * @return 返回true或false
     */
    public boolean isSaveByName(String name) {
        //拿到查询构造器
        QueryBuilder<User> queryBuilder = userDao.queryBuilder();
        //构造器 构造where语句 -->UserDao表中属性（Properties）-->那个属性（Name）-->eq(equal) 等于那个name的
        queryBuilder.where(UserDao.Properties.Name.eq(name));
        //拿到查询到的位置行数-->id
        int size = queryBuilder.list().size();
        //大于0 代表存在
        return size > 0;
    }

    /**
     * 添加数据（单个添加）
     *
     * @param user 用户实体类
     */
    public void insertUser(User user) {
        //判断如果不存在用户名则添加
        if (user == null) {
            return;
        }
        if (!isSaveByName(user.getName())) {
            userDao.insert(user);//添加
        }
    }

    /**
     * 批量插入
     *
     * @param userList User集合
     */
    public void insertUserList(List<User> userList) {
        //判空
        if (userList == null || userList.size() <= 0) {
            return;
        }
        userDao.insertInTx(userList);//批量添加 支持事务
    }

    /**
     * 更新数据库
     *
     * @param user User对象
     */
    public void updateUser(User user) {
        //判断如果不存在用户名则添加
        if (user == null) {
            return;
        }

        userDao.update(user);
    }


    /**
     * 查询所有数据
     *
     * @return 返回User集合
     */
    public List<User> selectAll() {
        return userDao.loadAll();
    }

    /**
     * 查询单个用户
     *
     * @param name 用户名称
     * @param age  年龄
     * @return 返回User类
     */
    public User selectUser(String name, int age) {
        try {
            //拿到查询构造器
            QueryBuilder<User> queryBuilder = userDao.queryBuilder();
            //构造器 构造where语句 -->UserDao表中属性（Properties）-->那个属性（Username）-->eq(equal) 等于那个name的
            queryBuilder.where(UserDao.Properties.Name.eq(name), UserDao.Properties.Age.eq(age));
            List<User> list = queryBuilder.list();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 条件查询
     *
     * @param age 用户年龄
     * @return 返回符合条件的User集合
     */
    public List<User> selectUser(int age) {
        try {
            //拿到查询构造器
            QueryBuilder<User> queryBuilder = userDao.queryBuilder();
            //构造器 构造where语句 -->UserDao表中属性（Properties）-->那个属性（Username）-->eq(equal) 等于那个name的
            queryBuilder.where(UserDao.Properties.Age.eq(age))
//                    .orderDesc(UserDao.Properties.Age)//降序
                    .orderAsc(UserDao.Properties.Age);//升序 按照某个条件排序
            if (queryBuilder.list().size() > 0) {
                return queryBuilder.list();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        userDao.deleteAll();//删除所有
    }

    /**
     * 条件删除
     *
     * @param name 用户名字
     */
    public void deleteUser(String name) {
        QueryBuilder<User> queryBuilder = userDao.queryBuilder();
        queryBuilder.where(UserDao.Properties.Name.eq(name));
        DeleteQuery<User> delete = queryBuilder.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();

    }

}
