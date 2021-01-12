1(可选)、自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和
for，然后自己分析一下对应的字节码，有问题群里讨论。

[HelloByteCode.java](src/HelloByteCode.java)

2(必做)、自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法， 此文件内容是一个 Hello.class 文件所有字节(x=255-x)处理后的文件。文件群里提供。

[CustomizeClassLoader.java](src/CustomizeClassLoader.java)

3(必做)、画一张图，展示 Xmx、Xms、Xmn、Metaspache、DirectMemory、Xss 这些内存参数的关系。

![](src/jvm内存结构.jpg)

4(可选)、检查一下自己维护的业务系统的 JVM 参数配置，用 jstat 和 jstack、jmap 查看一下详情，并且自己独立分析一下大概情况，思考有没有不合理的地方，如何改进。

##第一课笔记
一、JVM（3节课）
* JVM基础
* Java字节码技术
    *  .java -> .class(字节码) -> 类加载器 -> 对象实例
        * 字节码理论支持256个操作码，实际只使用200个左右 
            * 16进制范围：0x00~0xFF 0x(前缀)+(0~9+A~F) 16*16 = 256个字节
            1. 指令类型：栈操作指令（JVM基于栈的一种虚拟机）；流程控制指令；对象操作指令；算数运算、类型转换指令；
        * 查看字节码文件
            1. javap -c .class （助记符）
            2. javap -c -verbose  （详细信息：文件时间、校验和、java版本、行号、类修饰符、常量池、行号）
            3. 使用javac -g 编译后，可以查看本地变量表（局部变量区）
            4. 助记符前缀表明类型（a:操作对象的引用、i:常量、d:double等）
            5. 算数操作与类型转换(jvm只定义了4种:int、long、float、double，最小类型int，简单粗暴、简化操作符数目)
            6. 循环流程控制(if_icmge:int类型compare大于等于,iinc:int类型increment, goto:跳转到)
            7. 方法调用(invokestatic调用静态方法、invokespecial调用构造、invokevirtual调用公共或受保护方法、invokeinterface调用接口、invokedynamic jdk7为了让指针指向不同类型的对象增加的指令，也是jdk8 lambda表达式的实现基础)
            * iconst_0 ~iconst_5 因为常用所以简化为一个字节节省空间，大于5需要用俩个字节表示
* JVM类加载器*
    * 添加引用类的几种方法
        * 放到JDK的lib/ext下，或者-Djava.ext.dirs（使用扩展类加载器加载）
        * java -cp（-classpath） 或者class文件放到当前路径
        * 自定义类加载器（因为应用类加载器-扩展类加载器 父级是URL加载器，继承了ClassLoader类，覆盖findClass，然后将字节码传给defineClass方法（jdk9之前，之后三者平级了，直接Class.forName(“xxx”,new UrlClassLoader(“/path”))））
        * 拿到当前执行类的ClassLoader，反射调用addUrl方法添加Jar或路径
* JVM内存模型*
* JVM启动参数
    * 系统属性参数
        * -server标准参数（所有的虚拟机都要实现）
        * -D 设置系统环境变量，针对当前进程 System.getProperty(“a”)
        * -X 非标准参数 
            * -Xmixed 混合模式，自动选择-Xint(强行解释所有字节码)/-Xcomp(本地化代码) ，就是JIT的原理，自动将多次调用的类本地化，提高执行效率
        * -XX 非稳定参数
    * 运行模式参数
    * 堆内存设置参数
        * -Xmx 最大堆内存（最佳实践，系统内存的60～80%，大内存机器例外可以调大）
        * -Xms 初始内存大小（并不是系统实际分配的内存，而是使用到才分配。专用服务器要保持-Xmx和-Xms大小一样，否则堆内存扩容可能导致性能抖动）
            * ？需要再理解下
        * -Xmn 等价于-XX:NewSize ,设置年轻带的大小，G1垃圾回收器不需要设置该参数（最佳实践，-Xmx的1/2～1/4）
        * -XX:MaxPermSize 1.7之前的参数，之后Meta空间无限大，参数无效
        * -XX:MaxDirectMemorySize 最大对外内存
        * -Xss 每个线程栈的字节数 = XX:ThreadStackSize
    * GC设置参数
        * 下节课
    * 分析诊断参数
        * -XX:+-HeapDumpOnOutOfMemoryError，当堆内存溢出时，自动dump堆内存。（-XX:+-HeapDumpOnOutOfMemoryError -Xmx256m ConsumeHeap -XX:HeapDumpPath=/usr/local/dump）
        * -XX:OnError=“gdb -%p” MyApp 启动gdb的时候，通过参数-p指定目标进程就可以进入调试状态
        * -XX:OnOutOfMemoryError
        * -XX:ErrorFile=filename
        * -Xdebug-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1506，远程调试
    * JavaAgent参数(注入AOP代码，执行统计)


