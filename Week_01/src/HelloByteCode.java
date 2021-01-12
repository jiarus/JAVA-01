/**
 * 1.基础类型的四则运算
 * 2.逻辑控制
 *
 * @author: zhangjiaru
 * @date: 2021/01/08
 */
public class HelloByteCode {
    
    public static void main(String[] args) {
        int a = 128;
        int b = 6;
        int c = (a + b) * (a - b) / 2;
        for (int i = 0; i < 10; i++) {
            int d = (int) (Math.random() * 10);
            if (d > c) {
                //“+”拼接字符串在java9优化为InvokeDynamic #2:makeConcatWithConstants
                // BootstrapMethods是用来记录InvokeDynamic实际使用的方法和参数的。
                System.out.println("d value:" + d);
                break;
            }
        }
    }
}

