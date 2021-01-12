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
                    decodeBuf[i] = (byte) (255 - buf[i]);
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
        return defineClass("Hello", os.toByteArray(), 0, os.size());
    }
}
