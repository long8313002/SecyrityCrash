    Android全局异常捕获，不退出应用，让应用正常运行下去！
  
    当App发现异常后，如果程序没有处理，将交给虚拟机进行处理，通常会弹出一个对话框，然后退出应用。但大多数的应用可能对后续流程影
    响不大，比如分享功能出现。
  一个问题，真的有必要关闭整个应用吗？屏蔽这个功能，对整体来说不会有太大的影响。或者某个页面的数据出现了逻辑错误，大多数关闭当
  前页面， 用户再重新启动便可以正常使用了。
  
    原理介绍：Looper机制是整个App一直运行下去的关键，就和操作系统一样，通过死循环来实现不退出。在Android App 中ActivityThread的main
    方法会主动为主线程
  创建一个Looper，内部维护一个消息队列MessageQueue，通过循环获取消息，处理消息，来使得App运行下去。比如当屏幕被点击，触摸事件通过binder
  子线程，发送消息给主线程消息队列MessageQueue，主线程得到消息处理。
    发生错误后会发生什么事情呢？循环会停止，将不再处理消息。所以大多数捕获到异常后，界面将会卡死，发生ANR。幸运的是，我们可以在循环被系统
    停止前，捕获到它的异常。
  
   new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        IHandlerException handler = mainThreadFactory.get(e);
                        if (onExceptionCallBack != null) {
                            onExceptionCallBack.onThrowException(Thread.currentThread(), e, handler);
                        }
                        if (handler == null) {
                            defaultUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
                            return;
                        }
                        if (handler.handler(e)) {
                            return;
                        }
                    }
                }
            }
        });
        
  插入一条消息，在循环队列后，这样队列消息的处理，将由我们来完成，当然我们也可以捕获到它发出的异常。说到这里，也许大家会发现，这样似乎只能
  捕获主线程的异常，我们仅仅是循环的主线程的消息队列。不过对于子线程来说，默认情况下是没有消息队列的，当然如果你愿意也可以创建，当子线程发生
  异常消息循环将会被终止，并且会交给RuntimeInit.UncaughtHandler处理。在这之前我们要替换掉它，由自己来处理。


   Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                IHandlerException handler = childThreadFactory.get(e);
                if (onExceptionCallBack != null) {
                    onExceptionCallBack.onThrowException(t, e, handler);
                }
                if (handler == null) {
                    defaultUncaughtExceptionHandler.uncaughtException(t, e);
                    return;
                }
                handler.handler(e);
            }
        });
        
        
    具体使用：在Application的onCreate中安装下，SecyrityCrash.install();
    
    public class MyApplication extends Application {

       @Override
       public void onCreate() {
            super.onCreate();
           SecyrityCrash.install();
        }

    }
    我默认实现了一个处理行为，EndCurrenPagerHandler、IgnoreHandler、KillAppHandler。由工厂IHandlerExceptionFactory构建。事实
    我建议大家根据项目定义错误处理。
    setMainThreadFactory：主线程错误处理，用户可以自定义主线程处理逻辑
    setChildThreadFactory：子线程错误处理，用户可以自定义主线程处理逻辑
    setOnExceptionCallBack：异常通知，如果想在外部接收异常，可以通过这个回调，如果你想将异常信息上传到服务器。
    
    
