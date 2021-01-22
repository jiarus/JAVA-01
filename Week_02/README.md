### 第 3 节课作业实践 
>1、使用 GCLogAnalysis.java 自己演练一遍串行/并行/CMS/G1的案例。
不同类型GC日志分析
##### 1、串行垃圾收集器 
执行命令：
```
java -XX:+UseSerialGC -XX:+PrintGCDetails -Xmx128m -Xms128m -Xmn1m -XX:-UseAdaptiveSizePolicy GCLogAnalysis 
```
代码片段：
```

[GC (Allocation Failure) [DefNew: 549K->23K(960K), 0.0003074 secs] 130035K->129647K(131008K), 0.0003297 secs] 
[Times: user=0.00 sys=0.00, real=0.00 secs] 

上面日志表明是minorGC,DefNew(年轻代GC，因为使用的是串行GC): 549K(GC前大小)->23K(发生GC后的大小)(960K)(年轻代总容量)
130035K(整个堆GC前大小)->129647K(整个堆GC后大小)(131008K)(堆总容量)

...
[GC (Allocation Failure) [DefNew: 916K->916K(960K), 0.0000255 secs][Tenured: 129623K->106361K(130048K), 0.1092355 secs] 
130539K->106361K(131008K), [Metaspace: 2571K->2571K(1056768K)], 0.1093388 secs] [Times: user=0.03 sys=0.00, real=0.11 secs] 

这段表示是发生了FullGC,[Tenured: 129623K->106361K(130048K), 0.1092355 secs]  ,老年代GC前从129623K->106361K,堆从130539K->106361K

```
##### 2、并行垃圾收集器  
执行命令：
```
 java -XX:+PrintGCDetails -XX:+UseParallelOldGC -XX:+UseParallelGC -XX:+UseParallelOldGC -Xmx128m -Xms128m -Xmn1m -XX:-UseAdaptiveSizePolicy GCLogAnalysis  
```
代码片段：
```
[GC (Allocation Failure) [PSYoungGen: 510K->453K(1024K)] 767K->719K(130560K), 0.0008225 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
年轻代从 510K->453K
堆内存从 767K->719K
[GC (Allocation Failure) [PSYoungGen: 961K->387K(1024K)] 129169K->128858K(130560K), 0.0024292 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
[Full GC (Ergonomics) [PSYoungGen: 705K->0K(1024K)] [ParOldGen: 129029K->65712K(129536K)] 129734K->65712K(130560K), 
[Metaspace: 2571K->2571K(1056768K)], 0.0201966 secs] [Times: user=0.03 sys=0.01, real=0.02 secs]
发生了一次FullGC,年轻代705K->0K，老年代129029K->65712K,可以看到FullGC前，堆内存只剩下130560K-128858K = 1702k
```
##### 3、CMS GC
执行命令：
```
java -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -Xmx128m -Xms128m -Xmn1m -XX:-UseAdaptiveSizePolicy GCLogAnalysis 
```
日志片段:
```
[GC (Allocation Failure) [ParNew: 670K->30K(960K), 0.0008882 secs] 65354K->65352K(131008K), 0.0009237 secs] 
[Times: user=0.01 sys=0.00, real=0.01 secs]

上面的日志表明发生了MinorGC，基本和串行的日志还没大的区别
  
[GC (CMS Initial Mark) [1 CMS-initial-mark: 65322K(130048K)] 66227K(131008K), 0.0003507 secs] 
[Times: user=0.00 sys=0.00, real=0.00 secs]

这里开始执行CMS并行GC的第一阶段：初始标记，此阶段会STW

[CMS-concurrent-mark-start]

这里开始执行第二阶段：并发标记
 
[CMS-concurrent-preclean-start]
[CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]

这里表明开始第三阶段：并发预处理，标记脏卡

[GC (CMS Final Remark) [YG occupancy: 832 K (960 K)][Rescan (parallel) , 0.0028767 secs][weak refs processing, 0.0000659 secs]
[class unloading, 0.0008019 secs][scrub symbol table, 0.0015306 secs][scrub string table, 0.0002810 secs]
[1 CMS-remark: 67714K(130048K)] 68546K(131008K), 0.0057453 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]

第四阶段：最终标记，此阶段会STW。因为CMS发生在老年代，可以看到老年代占用从初始标记的66227K->67714K，是因为在二、三阶段又生成了新的对象

[CMS-concurrent-sweep-start]
[CMS-concurrent-sweep: 0.000/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 

第五阶段：并发清除

[CMS-concurrent-reset-start]
[CMS-concurrent-reset: 0.000/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 

第六阶段：并发重置  
``` 
##### 4、G1
执行命令：
```
java -XX:+PrintGCDetails -Xmx128m -Xms128m -Xmn1m -XX:-UseAdaptiveSizePolicy GCLogAnalysis ‐XX:+UseG1GC 
注意：G1命令要放在最后，和其他GC不同
```
日志片段:
```
[0.004s][warning][gc] -XX:+PrintGCDetails is deprecated. Will use -Xlog:gc* instead.
[0.015s][info   ][gc,heap] Heap region size: 1M
[0.020s][info   ][gc     ] Using G1
[0.020s][info   ][gc,heap,coops] Heap address: 0x00000007e0000000, size: 512 MB, Compressed Oops mode: Zero based, Oop shift amount: 3
G1垃圾收集器的一些基本信息，每个region大小1m,总堆大小512M

[0.162s][info   ][gc,start     ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
[0.166s][info   ][gc,task      ] GC(0) Using 4 workers of 4 for evacuation
[0.169s][info   ][gc,phases    ] GC(0)   Pre Evacuate Collection Set: 0.0ms
[0.169s][info   ][gc,phases    ] GC(0)   Evacuate Collection Set: 2.9ms
[0.169s][info   ][gc,phases    ] GC(0)   Post Evacuate Collection Set: 0.1ms
[0.169s][info   ][gc,phases    ] GC(0)   Other: 3.9ms
[0.169s][info   ][gc,heap      ] GC(0) Eden regions: 2->0(1)
[0.169s][info   ][gc,heap      ] GC(0) Survivor regions: 0->1(1)
[0.169s][info   ][gc,heap      ] GC(0) Old regions: 0->0
[0.169s][info   ][gc,heap      ] GC(0) Humongous regions: 0->0 （巨型对象区）
[0.169s][info   ][gc,metaspace ] GC(0) Metaspace: 4140K->4140K(1056768K)
[0.169s][info   ][gc           ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 1M->0M(512M) 7.093ms
[0.169s][info   ][gc,cpu       ] GC(0) User=0.01s Sys=0.00s Real=0.01s

年轻代收集日志

[1.135s][info   ][gc            ] GC(353) Pause Young (Normal) (G1 Evacuation Pause) 199M->198M(256M) 1.096ms
[1.135s][info   ][gc,cpu        ] GC(353) User=0.00s Sys=0.00s Real=0.01s
[1.135s][info   ][gc,marking    ] GC(348) Concurrent Mark From Roots 9.584ms
[1.135s][info   ][gc,marking    ] GC(348) Concurrent Preclean
[1.135s][info   ][gc,marking    ] GC(348) Concurrent Preclean 0.032ms
[1.135s][info   ][gc,marking    ] GC(348) Concurrent Mark (1.125s, 1.135s) 9.987ms
[1.135s][info   ][gc,start      ] GC(348) Pause Remark
[1.137s][info   ][gc,stringtable] GC(348) Cleaned string and symbol table, strings: 1514 processed, 0 removed, symbols: 18771 processed, 0 removed
[1.137s][info   ][gc            ] GC(348) Pause Remark 199M->199M(256M) 1.995ms
[1.137s][info   ][gc,cpu        ] GC(348) User=0.01s Sys=0.00s Real=0.00s
[1.137s][info   ][gc,marking    ] GC(348) Concurrent Rebuild Remembered Sets

并发垃圾收集

[1.082s][info   ][gc,start      ] GC(338) Pause Young (Prepare Mixed) (G1 Evacuation Pause)
[1.082s][info   ][gc,task       ] GC(338) Using 4 workers of 4 for evacuation
[1.083s][info   ][gc,phases     ] GC(338)   Pre Evacuate Collection Set: 0.0ms
[1.083s][info   ][gc,phases     ] GC(338)   Evacuate Collection Set: 0.9ms
[1.083s][info   ][gc,phases     ] GC(338)   Post Evacuate Collection Set: 0.1ms
[1.083s][info   ][gc,phases     ] GC(338)   Other: 0.0ms
[1.083s][info   ][gc,heap       ] GC(338) Eden regions: 1->0(1)
[1.083s][info   ][gc,heap       ] GC(338) Survivor regions: 1->1(1)
[1.083s][info   ][gc,heap       ] GC(338) Old regions: 107->107
[1.083s][info   ][gc,heap       ] GC(338) Humongous regions: 92->92
[1.083s][info   ][gc,metaspace  ] GC(338) Metaspace: 4156K->4156K(1056768K)

混合收集

[0.404s][info   ][gc,start       ] GC(62) Pause Full (G1 Evacuation Pause)
[0.404s][info   ][gc,phases,start] GC(62) Phase 1: Mark live objects
[0.406s][info   ][gc,stringtable ] GC(62) Cleaned string and symbol table, strings: 1501 processed, 0 removed, symbols: 18771 processed, 0 removed
[0.406s][info   ][gc,phases      ] GC(62) Phase 1: Mark live objects 1.895ms
[0.406s][info   ][gc,phases,start] GC(62) Phase 2: Prepare for compaction
[0.407s][info   ][gc,phases      ] GC(62) Phase 2: Prepare for compaction 0.475ms
[0.407s][info   ][gc,phases,start] GC(62) Phase 3: Adjust pointers
[0.408s][info   ][gc,phases      ] GC(62) Phase 3: Adjust pointers 1.075ms
[0.409s][info   ][gc,phases,start] GC(62) Phase 4: Compact heap
[0.411s][info   ][gc,phases      ] GC(62) Phase 4: Compact heap 1.409ms
[0.412s][info   ][gc,heap        ] GC(62) Eden regions: 0->0(24)
[0.412s][info   ][gc,heap        ] GC(62) Survivor regions: 0->0(3)

FullGC

G1存在以上4中垃圾收集的情况
```
>2、使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar示例。  

本地配置： 

CPU核心数:  2 

CPU线程数:  4 

ep1: 并行垃圾收集器
```
java -XX:+UseParallelOldGC -XX:+UseParallelGC  -Xmx128m -Xms128m -Xmn24m -jar gateway-server-0.0.1-SNAPSHOT.jar
➜  ~ wrk -t20 -c100 -d30s http://localhost:8088/api/hello
Running 30s test @ http://localhost:8088/api/hello
  20 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    49.07ms  108.52ms   1.17s    89.82%
    Req/Sec   514.71    276.38     1.90k    61.50%
  300094 requests in 30.10s, 35.83MB read
Requests/sec:   9971.13
Transfer/sec:      1.19MB
```
结果：使用20个线程，100个并发压测30秒，单线程QPS:514.71,总QPS:9971.13，平均延迟49.07ms 

ep2:（启动参数不变，修改并发、线程数）
```
wrk -t2 -c100 -d30s http://localhost:8088/api/hello
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.02ms    3.66ms  73.47ms   89.90%
    Req/Sec    10.13k     2.67k   16.77k    68.70%
  604555 requests in 30.09s, 72.18MB read
Requests/sec:  20094.73
Transfer/sec:      2.40MB
```
结果：使用2个线程，100个并发压测30秒，单线程QPS: 10.13k,总QPS:20094.73，平均延迟4.02ms,同时标准差  3.66ms ，说明延迟波动很小。 

ep3:（使用CMS，修改并发、线程数不变）
```
java -XX:+UseParNewGC -XX:+UseConcMarkSweepGC  -Xmx128m -Xms128m -Xmn24m -jar gateway-server-0.0.1-SNAPSHOT.jar
~ wrk -t2 -c100 -d30s http://localhost:8088/api/hello
Running 30s test @ http://localhost:8088/api/hello
  2 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.97ms    3.69ms  83.09ms   91.17%
    Req/Sec    10.16k     1.82k   18.23k    72.20%
  604836 requests in 29.95s, 72.21MB read
  Socket errors: connect 0, read 0, write 0, timeout 100
Requests/sec:  20197.67
Transfer/sec:      2.41MB
```
结果：使用2个线程，100个并发压测30秒，单线程QPS: 10.16k ,总QPS:20197.67，平均延迟3.97ms,同时标准差 3.69ms，比并发收集器延迟有所降低（因为本机电脑压测，容易受其他进程影响，结果并太明显，多次压测才有效果）。


结论：其他情况的压测就不贴了，只有当线程数等于当前CPU核心数时候，并发最高，延迟也最低，增加/减少线程都会降低QPS。增加/减少并发,也会导致QPS降低 

    * 当前压测的服务因为是I/O密集型，服务器请求/响应都是耗时操作，在因为wrk可以异步IO,可以使用少量线程,模拟大量并发。 
    
    * 线程数如果过多，CPU上下文切换也会造成效率降低。 
    
    * 并发数受限于服务器内存，理论上增加服务器堆内存会降低GC的发生，或使用并发GC/CMS GC，会提高响应效率，提高QPS。

>3、(选做)如果自己本地有可以运行的项目，可以按照2的方式进行演练。 

>4.（必做） 根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 的总结，提交到 Github。

    * 串行垃圾收集器：GC时会暂停所有服务，使用单线程收集垃圾，通常在并发收集发生Concurrent Mode Failure时使用Serial Old收集器。 
    
    * 并行垃圾收集器：因为GC会暂停所有服务，并使用全部GC线程收集垃圾，所有CPU利用率高，吞吐量高，但有标记清理的数据多的话，可能会存在高延迟的情况。适用于对延迟不敏感的应用。
    
    * CMS垃圾收集器：只有在初始标记和最终标记的时候STW，其他阶段都是并发执行。STW暂停时间短，因此服务器响应快。但它对CPU使用比较多，尽量在多核机器上使用。适用于对延迟敏感的应用。
    
    * G1垃圾收集器：一般在大内存（大于4G,大于16G使用ZGC）,它是CMS的升级版，所以也适用于CMS的情况。适用于多核大内存机器，GC时间可控，具有低延迟的特点。
    
    * 总结： 
        1.服务器堆内存不建议太大（系统内存的60～80%，年轻代-Xmx的1/2～1/4），服务器堆内存过大也会使因为标记遍历的对象太多，GC时间过长。
        当然如果服务器CPU性能高，堆内存也可以放大一点。
        2.少创建大对象，大对象可能直接会分配到老年代，导致MajorGC，老年代的GC耗时要比年轻代长。
        3.年轻代空间比例不要太小，防止对象过快进入老年代。

###第 3 节课作业实践
>1.（选做）运行课上的例子，以及 Netty 的例子，分析相关现象。 

>2.（必做）写一段代码，使用 HttpClient 或 OkHttp 访问 http://localhost:8801 ，代码提交到 Github。  

 [MyHttpClient.java](src/main/java/demo/MyHttpClient.java) 
 
 [MyOkHttp.java](src/main/java/demo/MyOkHttp.java)
 