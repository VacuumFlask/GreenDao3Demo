package cn.vacuumflask.greendao3demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }
}
