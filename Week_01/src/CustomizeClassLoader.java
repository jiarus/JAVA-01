import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhangjiaru
 * @date: 2021/01/11
 */
public class CustomizeClassLoader extends ClassLoader {
    
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = new CustomizeClassLoader().findClass("Hello.xlass");
        Object object = clazz.newInstance();
        Method method = clazz.getMethod("hello");
        method.invoke(object);
        System.out.println(bytesToHex(new byte[]{63,61}));
    }
    
    @Override
    protected Class<?> findClass(String name) {
        String path = System.getProperty("user.dir") + "/Week_01/src/";
        File file = new File(path + name);
        FileInputStream inputStream = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(file);
            byte[] buf = new byte[2048];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                byte[] decodeBuf = new byte[2048];
                for (int i = 0; i < buf.length; i++) {
                    //取反等价于255-buf[i]
                    decodeBuf[i] = (byte) (~buf[i]);
                }
                os.write(decodeBuf, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //print hex string
        String hex = bytesToHex(os.toByteArray());
        System.out.println(hex);
        return defineClass("Hello", os.toByteArray(), 0, os.size());
    }
    
    
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
   
}
