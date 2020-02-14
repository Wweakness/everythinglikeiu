package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PinyinUtil {
    /*
     * 中文字符格式
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";


    /*
    汉语拼音格式化类
     */
    private static final HanyuPinyinOutputFormat FORMAT=
            new HanyuPinyinOutputFormat();


    static {
        //设置拼音小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置不带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置带V字符，如绿lv
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }
   //是否包含中文
    public static boolean containsChines(String name){
        return name.matches(".*"+CHINESE_PATTERN+".*");
        //matches（正则表达式）  是否匹配   .*表示0个或多个
    }



    /*
    通过文件名获取全拼+拼音首字母
    中华人民共和国------->zhonghuarenmingongheguo/zhrmghg
    name文件名
    返回 拼音全拼字符串+拼音首字母字符串  数组
     */
    public static String[] get(String name){  //此方法不考虑多音字
        String[] result=new String[2];
        //StringBuilder和StringBuffer 区别：Builder线程不安全，但效率高
        StringBuilder pinyin=new StringBuilder(); //全拼fg555gg
        StringBuilder pinyinFirst=new StringBuilder();  //拼音首字母

        for(char c:name.toCharArray()) {
            //例如把中华人民共和国遍历，中-->[zhong]
            try {
                String[] pinyins= PinyinHelper
                        .toHanyuPinyinStringArray(c, FORMAT);  //c就是文件名 ， format格式化对象
                //不考虑多音字的情况，例如"和"：he,huo,hu,只考虑he 的读音
                if(pinyins==null||pinyins.length==0){
                    //如果出现多音字其他读音，翻译不了拼音，直接把这个字符添加进去
                    pinyin.append(c);
                    pinyinFirst.append(c);
                }else {
                    pinyin.append(pinyins[0]);  //全拼： 和--->he
                    pinyinFirst.append(pinyins[0].charAt(0));   //获取拼音首字母： 和--->h
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                pinyin.append(c);
                pinyinFirst.append(c);
            }
        }
        result[0]=pinyin.toString();
        result[1]=pinyinFirst.toString();
        return result;
    }
    /*
    例如 和【he,huo,hu....】  长【chang,zhang】
    例如 和长和 ：hezhanghe/hezhanghu/hezhanghuo
    name 文件名
    fullSpell true表示全拼，false去拼音首字母
    返回 包含多音字的字符串组合
     */
    public static String[][] get(String name,boolean fullSpell){
        char[] chars=name.toCharArray();
        String[][] result=new String[chars.length][];
        for(int i=0;i<chars.length;i++){
            try {
                //因为不考虑音调，所以会有重复。比如”只“，有三声和一声两个读音，因为不考虑音调不同的多音字所以
                //就会存下两个[zhi，zhi],要去重
                String[] pinyins=PinyinHelper
                        .toHanyuPinyinStringArray(chars[i],FORMAT);
                if(pinyins==null||pinyins.length==0){
                    result[i]=new String[]{String.valueOf(chars[i])}; //返回原始字符

                }
                else if(fullSpell){  //如果全拼
                    result[i]=pinyins;
                }else {     //如果是拼音首字母
                  result[i]=unique(pinyins,fullSpell);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                result[i]=new String[]{String.valueOf(chars[i])};
            }
        }
             return result;
    }
    /*
    字符串数组去重
     */
    public static String[] unique(String[] array, boolean fullspell){
        Set<String> set=new HashSet<>();
        for(String s:array){
            if(fullspell){
                set.add(s);
            }else {
                set.add(String.valueOf(s.charAt(0)));
            }
        }
        return set.toArray(new String[set.size()]);
    }
    /*
    [he,hu,huo...] [zhang,chang]
    hezhang,hechang...排列组合
    每个中文字符返回拼音是字符串数组，每两个字符串数组合并为一个字符数组
    之后依次类推
     */
    public  static String[] compose(String[][] pinyinArray){
        if(pinyinArray==null||pinyinArray.length==0){
            return null;
        }else if(pinyinArray.length==1){
            return pinyinArray[0];
        }else {
            for(int i=0;i<pinyinArray.length;i++){
                pinyinArray[0]=compose(pinyinArray[0],pinyinArray[i]);

            }
            return pinyinArray[0];
        }

    }
    /*
    合并两个拼音数组为一个
    pinyins1[he,hu,huo]
    pinyins2[zhang,chang]
    return hezhang,hechang,huzhang...
     */
    public static String[] compose(String[] pinyins1,String[] pinyins2){
        String[] result=new String[pinyins1.length*pinyins2.length];
        for(int i=0;i<pinyins1.length;i++){
            for(int j=0;j<pinyins2.length;j++){
                result[i*pinyins2.length+j]=pinyins1[i]+pinyins2[j];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(get(  "中华人民共和国")));
        System.out.println(Arrays.toString(get("中华1人民吧、b共和国")));
        System.out.println(Arrays.toString(compose(get("和长",true))));
    }


}
