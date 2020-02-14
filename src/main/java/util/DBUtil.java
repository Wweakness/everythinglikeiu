package util;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

public class DBUtil {
    private static volatile DataSource DATA_SOURCE;
    /*
    提供获取数控库连接池的功能
    使用单例模式（多线程安全版本）
     */
    private static DataSource getDataSource(){
        if(DATA_SOURCE==null){
            synchronized (DBUtil.class){
                if(DATA_SOURCE==null){
                    //初始化操作
                    DATA_SOURCE=new SQLiteDataSource();
                    ((SQLiteDataSource)DATA_SOURCE).setUrl();
                }
            }
        }
    }
/*
提供获取数据库连接的方法
从数据库连接池DataSource.getConnection()来获取数据库来凝结
 */
    public static Connection getConnection(){

    }
}