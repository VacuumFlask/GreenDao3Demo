# GreenDao3Demo

GreenDao 3.0改动：
在GreenDao3.0之前需要通过新建GreenDaoGenerator工程生成Java数据对象实体类和DAO对象，而GreenDao3.0最大的变化就是采用注解的方式通过编译方式生成Java数据对象和DAO对象。

注： 暂时没写界面先凑合

而要使用需要在build.gradle工程文件的dependencies节点添加 classpath 'org.greenrobot:greendao-gradle-plugin:3.1.0'

    dependencies {
            classpath 'com.android.tools.build:gradle:2.0.0'
            classpath 'org.greenrobot:greendao-gradle-plugin:3.1.0'
        }

 接着在build.gradle的Module:app文件中导入greenDao3.0的包
 
     compile 'org.greenrobot:greendao:3.2.0'
     
 还要添加一下配置
 
      apply plugin: 'org.greenrobot.greendao'
     
      greendao {
          schemaVersion 1
          targetGenDir 'src/main/java'
          daoPackage 'cn.vacuumflask.greendao3demo.db'//数据库文件位置
      }
      
完整的build.gradle工程文件如下
      
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:2.0.0'
            classpath 'org.greenrobot:greendao-gradle-plugin:3.1.0'
        }
    }

    allprojects {
        repositories {
            jcenter()
        }
    }

    task clean(type: Delete) {
        delete rootProject.buildDir
    }

完整的build.gradle的Module:app文件如下

    apply plugin: 'com.android.application'
    apply plugin: 'org.greenrobot.greendao'

    android {
        compileSdkVersion 25
        buildToolsVersion "24.0.1"

        defaultConfig {
            applicationId "cn.vacuumflask.greendao3demo"
            minSdkVersion 14
            targetSdkVersion 25
            versionCode 1
            versionName "1.0"
        }
        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }
    }


    greendao {
        schemaVersion 1
        targetGenDir 'src/main/java'
        daoPackage 'cn.vacuumflask.greendao3demo.db'//数据库文件位置
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        testCompile 'junit:junit:4.12'
        compile 'com.android.support:appcompat-v7:25.0.1'
        compile 'org.greenrobot:greendao:3.2.0'
    }
 
 整个配置就上面所示，导入包和添加几句代码就行，相对于原来方便了不少，不用再创建一个ExampleDaoGenerator.java文件添加表
 
 现在建表是新建一个实体类 然后通过注解来生产数据表，那么下面开始就是创建实体类User
 
      @Entity
      public class User {
          @Id(autoincrement = true)//主键 自增 类型要用Long对象不能用数字类型long
          private Long id;
          @NotNull//非空
          private String name;
          @NotNull
          private int age;
      }
 
 创建完实体类之后Build-->Make Project，就会在上面配置的路径中自动生成DaoSession和DaoMaster，还有一个UserDao
 
 UserDao相当于一个User表，是根据上面User实体类生成的一个类，里面主要有创建表的createTable方法、删除表格dropTable方法，和一些赋值方法等等
 
下面是UserDao的代码不看可以
 
     public class UserDao extends AbstractDao<User, Long> {

        public static final String TABLENAME = "USER";

        /**
         * Properties of entity User.<br/>
         * Can be used for QueryBuilder and for referencing column names.
         */
        public static class Properties {
            public final static Property Id = new Property(0, Long.class, "id", true, "_id");
            public final static Property Name = new Property(1, String.class, "name", false, "NAME");
            public final static Property Age = new Property(2, int.class, "age", false, "AGE");
        }


        public UserDao(DaoConfig config) {
            super(config);
        }

        public UserDao(DaoConfig config, DaoSession daoSession) {
            super(config, daoSession);
        }

        /** Creates the underlying database table. */
        public static void createTable(Database db, boolean ifNotExists) {
            String constraint = ifNotExists? "IF NOT EXISTS ": "";
            db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                    "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                    "\"NAME\" TEXT NOT NULL ," + // 1: name
                    "\"AGE\" INTEGER NOT NULL );"); // 2: age
        }

        /** Drops the underlying database table. */
        public static void dropTable(Database db, boolean ifExists) {
            String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
            db.execSQL(sql);
        }

        @Override
        protected final void bindValues(DatabaseStatement stmt, User entity) {
            stmt.clearBindings();

            Long id = entity.getId();
            if (id != null) {
                stmt.bindLong(1, id);
            }
            stmt.bindString(2, entity.getName());
            stmt.bindLong(3, entity.getAge());
        }

        @Override
        protected final void bindValues(SQLiteStatement stmt, User entity) {
            stmt.clearBindings();

            Long id = entity.getId();
            if (id != null) {
                stmt.bindLong(1, id);
            }
            stmt.bindString(2, entity.getName());
            stmt.bindLong(3, entity.getAge());
        }

        @Override
        public Long readKey(Cursor cursor, int offset) {
            return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
        }    

        @Override
        public User readEntity(Cursor cursor, int offset) {
            User entity = new User( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // name
                cursor.getInt(offset + 2) // age
            );
            return entity;
        }

        @Override
        public void readEntity(Cursor cursor, User entity, int offset) {
            entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
            entity.setName(cursor.getString(offset + 1));
            entity.setAge(cursor.getInt(offset + 2));
         }

        @Override
        protected final Long updateKeyAfterInsert(User entity, long rowId) {
            entity.setId(rowId);
            return rowId;
        }

        @Override
        public Long getKey(User entity) {
            if(entity != null) {
                return entity.getId();
            } else {
                return null;
            }
        }

        @Override
        public boolean hasKey(User entity) {
            return entity.getId() != null;
        }

        @Override
        protected final boolean isEntityUpdateable() {
            return true;
        }
    
    }
 

创建UserDBHelper类来帮助操作数据库

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
        public List<User> selectGoods(int age) {
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
        public void deleteTask(String name) {
            QueryBuilder<User> queryBuilder = userDao.queryBuilder();
            queryBuilder.where(UserDao.Properties.Name.eq(name));
            DeleteQuery<User> delete = queryBuilder.buildDelete();
            delete.executeDeleteWithoutDetachingEntities();

        }

    }


使用UserDBHelper方式

        //使用的时候实例化操作类
        UserDBHelper dbHelper = UserDBHelper.getDbHelper(this);

        //插入单个数据
        User user = new User();
        user.setName("张三");
        user.setAge(10);
        dbHelper.insertUser(user);

        //插入多个数据
        ArrayList<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new User("李四" + i, (9 + i)));//GreenDao会帮你创建两个构造方法，你也可以自己添加
        }
        dbHelper.insertUserList(list);

        //查询数据
        User selectUser = dbHelper.selectUser("张三", 23);
        Log.i(TAG, "selectUser: " + selectUser.toString());
        //或者
        List<User> userList = dbHelper.selectUser(10);
        Log.i(TAG, "userList: " + userList.toString());

        //删除
        dbHelper.deleteUser("张三");
        //删除所有
        dbHelper.deleteAll();
