/**
 * @author zhangjiaru
 * @date: 2021/01/08
 */
public class StringByteCode {
    
    public static void main(String[] args) {
        String s0 = "hello";
        String s1 = ",i am a text";
        String s2 = "hello" + ",i am a text";
        String s3 = "hello,i am a text";
        //new String()会先判断常量池中是否存在，不存在则在常量池和堆中分别创建一个对象
        String s4 = new String("hello,i am a text");
        //s5在这里只会在堆中创建一个对象
        String s5 = new String("hello,i am a text");
        String s6 = s0 + s1;
        
        
        //编译class过程中已经确定字符串,常量池中存在
        System.out.println(s2 == s3);
        //s4.intern返回常量池的内存地址，所以s2 == s4.intern()
        System.out.println(s2 == s4.intern());
        //s4和s5的堆中的地址不同
        System.out.println(s4 == s5);
        //s4和s5在常量池中的地址相同
        System.out.println(s4.intern() == s5.intern());
        //s6 是使用了StringBuilder来相加了(jdk9之前,之后使用makeConcatWithConstants),会在堆中和常量池中生成新的对象
        //s2.s3.s4.s5.s6 都使用的同一个常量池的对象
        System.out.println(s2.intern() == s6.intern());
    }
}
