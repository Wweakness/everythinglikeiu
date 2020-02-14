package task;

import java.io.File;
import java.net.URL;

/*
1、初始化数据库，:数据库文件约定好，放在target/everything-like.db
2、并且读取sql文件，
3、再执行sql语句来初始化表
 */
public class DBInit {
    //初始化操作
    public static void init(){
        //获取target编译文件夹的路径
        //通过classLoader.getResource()/classLoader.getResourceAsStream()  这样的方法
        //默认的根路径为编译文件夹路径（target/classes）
        URL classesURL=DBInit.class.getClassLoader().getResource("./");  //通过类加载器获取资源
        //获取target/classes文件夹的父级目录路径
        String dir=new File(classesURL.getPath()).getParent();
        String url="jdbc:sqlite://"+dir+File.separator+"everything-like.db";
        //new SqLiteDateSource(),把这个对象的url设置进去，才会创建这个文件，如果文件已经存在，就会读取这个文件
        System.out.println(url);
       // System.out.println(classesURL.getPath());

    }

    public static void main(String[] args) {
        init();
    }
}
