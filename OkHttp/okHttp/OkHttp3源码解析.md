OkHttp3源码解析

参考文章

[OkHttp3源码解析](https://blog.yorek.xyz/android/3rd-library/okhttp/)

使用OkHttp实现GET请求非常简单，例子如下：

```java
public class GetExample {
    private OkHttpClient client = new OkHttpClient();

    private String run(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void main(String[] args) throws IOException{
        GetExample example = new GetExample();
        String response = example.run("https://raw.githubusercontent.com/square/okhttp/master/README.md");
        System.out.println(response);
    }
}
```

这个简单的例子，展示了OkHttp的使用过程：

(1) 创建OkHttpClient对象；

(2) 构建请求对象Request；

(3) 调用OkHttpClient发送Request；

(4) 解析请求结果。

下面以这个简单的例子为切入点，开始分析OkHttp内部的实现。

# 1 OkHttpClient及Request的构造器

```java

  public OkHttpClient() {
    this(new Builder());
  }

  OkHttpClient(Builder builder) {
    this.dispatcher = builder.dispatcher;
    this.proxy = builder.proxy;
    this.protocols = builder.protocols;
    this.connectionSpecs = builder.connectionSpecs;
    this.interceptors = Util.immutableList(builder.interceptors);
    this.networkInterceptors = Util.immutableList(builder.networkInterceptors);
    this.eventListenerFactory = builder.eventListenerFactory;
    this.proxySelector = builder.proxySelector;
    this.cookieJar = builder.cookieJar;
    this.cache = builder.cache;
    this.internalCache = builder.internalCache;
    this.socketFactory = builder.socketFactory;

    boolean isTLS = false;
    for (ConnectionSpec spec : connectionSpecs) {
      isTLS = isTLS || spec.isTls();
    }

    if (builder.sslSocketFactory != null || !isTLS) {
      this.sslSocketFactory = builder.sslSocketFactory;
      this.certificateChainCleaner = builder.certificateChainCleaner;
    } else {
      X509TrustManager trustManager = systemDefaultTrustManager();
      this.sslSocketFactory = systemDefaultSslSocketFactory(trustManager);
      this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
    }

    this.hostnameVerifier = builder.hostnameVerifier;
    this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(
        certificateChainCleaner);
    this.proxyAuthenticator = builder.proxyAuthenticator;
    this.authenticator = builder.authenticator;
    this.connectionPool = builder.connectionPool;
    this.dns = builder.dns;
    this.followSslRedirects = builder.followSslRedirects;
    this.followRedirects = builder.followRedirects;
    this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
    this.connectTimeout = builder.connectTimeout;
    this.readTimeout = builder.readTimeout;
    this.writeTimeout = builder.writeTimeout;
    this.pingInterval = builder.pingInterval;
  }
```

OkHttpClient默认的构造器内部接受一个OkHttpClient.Builder建造者对象，然后将建造者对象的字段复制给自己。OkHttpClient.Builder的构造器初始化了很多字段，这些字段初始化了默认值。

```java

    public Builder() {
      dispatcher = new Dispatcher(); //调度器，关于异步请求执行的策略。
      protocols = DEFAULT_PROTOCOLS; //支持的HTTP协议，默认是HTTP/1.1，HTTP/2
      connectionSpecs = DEFAULT_CONNECTION_SPECS;//连接规范配置，包括TLS版本和密码套件
      eventListenerFactory = EventListener.factory(EventListener.NONE);// 事件监听器工厂
      proxySelector = ProxySelector.getDefault();//代理服务器选择器。
      cookieJar = CookieJar.NO_COOKIES;//Http Cookie的存储器，负责接受/决绝哪些cookie、负责对Http 的response的Cookie进行保存。根据指定的Http url的请求加载存储器中的cookie。
      socketFactory = SocketFactory.getDefault();// 套接字工厂
      hostnameVerifier = OkHostnameVerifier.INSTANCE;//主机名验证器
      certificatePinner = CertificatePinner.DEFAULT;//证书固定器，用来限制哪些证书是可信的，可以保护证书免遭中间人攻击
      proxyAuthenticator = Authenticator.NONE;//代理身份验证其，用于对远端web服务器或者代理服务器的身份认证请求进行响应，如果返回了身份验证头则服务器身份是可靠的，如果返回null则服务器身份不可靠。
      authenticator = Authenticator.NONE;//本地身份验证器
      connectionPool = new ConnectionPool();//连接池，管理HTTP请求的复用，降低网络延迟，相同地址的请求的连接可以复用。
      dns = Dns.SYSTEM;//域名服务器，用来根据指定的主机名返回IP地址列表。
      followSslRedirects = true;//运行SSL重定向
      followRedirects = true;//允许普通重定向
      retryOnConnectionFailure = true;//支持连接失败后重试
      connectTimeout = 10_000;//连接超时
      readTimeout = 10_000;//读超时
      writeTimeout = 10_000;//写超时
      pingInterval = 0;
    }
```

Request的构造器

```java

public final class Request {
  final HttpUrl url;//请求url
  final String method;//请求方法
  final Headers headers;//请求头
  final @Nullable RequestBody body;//请求体
  final Object tag;//请求tag

  private volatile CacheControl cacheControl; // Lazily initialized.

  Request(Builder builder) {
    this.url = builder.url;
    this.method = builder.method;
    this.headers = builder.headers.build();
    this.body = builder.body;
    this.tag = builder.tag != null ? builder.tag : this;
  }
    
}
```

# 2 Call和RealCall

接着分析client.newCall(request).execute()。

```java
OkHttpClient.java
  @Override public Call newCall(Request request) {
    return new RealCall(this, request, false /* for web socket */);
  }
```

OkHttpClient#newCall(Request request)返回RealCall对象。而RealCall实现了Call接口。

```java

public interface Call extends Cloneable {
  //获取原始请求Request
  Request request();
  //同步执行请求并阻塞当前线程知道获取响应或出现异常。
  Response execute() throws IOException;

  //异步执行请求
  void enqueue(Callback responseCallback);

  //尽可能的取消请求；已经完成的请求无法取消。
  void cancel();

 // 调用了execute或enqueue之后的返回值都为true。
  boolean isExecuted();

  boolean isCanceled();

  
  Call clone();

  interface Factory {
    Call newCall(Request request);
  }
}
```

Call可以视为一个准备好待执行的请求，可以被取消，由于它代表请求/响应对，不能被多次执行。

接着分析RealCall的构造器和成员变量。

```java
RealCall.java
final class RealCall implements Call {
  final OkHttpClient client;
  final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;
  final EventListener eventListener;

  /** The application's original request unadulterated by redirects or auth headers. */
  final Request originalRequest;
  final boolean forWebSocket;

  // Guarded by this.
  private boolean executed;

  RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
    // 事件监听器，默认为NONE类型的。
    final EventListener.Factory eventListenerFactory = client.eventListenerFactory();

    this.client = client;
    this.originalRequest = originalRequest;
    this.forWebSocket = forWebSocket;
    // 重试、重定向的拦截器
    this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);

    // TODO(jwilson): this is unsafe publication and not threadsafe.
    this.eventListener = eventListenerFactory.create(this);
  }
    ...
}
```

# 3 RealCall#execute

重点看看RealCall#execute方法

```java

  @Override public Response execute() throws IOException {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    try {
      client.dispatcher().executed(this);
      Response result = getResponseWithInterceptorChain();
      if (result == null) throw new IOException("Canceled");
      return result;
    } finally {
      client.dispatcher().finished(this);
    }
  }
```

一旦调用了execute方法，那么executed为true，如果多次调用会抛出异常。然后调用client.dispatcher().execute(this);，将RealCall添加到Dispatcher#runningSyncCalls队列中。

```java
Dispatcher.java
public final class Dispatcher {
  ...
  private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
  ...
  /** Used by {@code Call#execute} to signal it is in-flight. */
  synchronized void executed(RealCall call) {
    runningSyncCalls.add(call);
  }
  ...
}
```

接着调用getResponseWithInterceptorChain进行网络请求并获取Response，该方法时OkHttp中最重要的方法，稍后再介绍RealCall#enqueue之后一起说。

最后就是finally代码块里面调用client.dispatcher().finish(this);。将执行完后的RealCall从Dispatcher#runningSyncCalls队列中移除。

```java
Dispatcher.java
  /** Used by {@code Call#execute} to signal completion. */
  void finished(RealCall call) {
    finished(runningSyncCalls, call, false);
  }

  private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
    int runningCallsCount;
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      if (promoteCalls) promoteCalls();
      runningCallsCount = runningCallsCount();
      idleCallback = this.idleCallback;
    }

    if (runningCallsCount == 0 && idleCallback != null) {
      idleCallback.run();
    }
  }

  public synchronized int runningCallsCount() {
    return runningAsyncCalls.size() + runningSyncCalls.size();
  }
```

如果Dispatcher的runningAsyncCalls和runningSyncCalls的和为0，说明Dispatcher空闲了，然后将Dispatcher空闲状态的回调给上层。

# 4 RealCall#enqueue

同execute一样，enqueue方法调用之后，其executed为true，多次调用会抛出异常。然后调用client.dispatcher().enqueue(new AsyncCall(responseCallback))。

```java
RealCall.java
final class RealCall implements Call {
  @Override public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    captureCallStackTrace();
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }
    
  final class AsyncCall extends NamedRunnable {
    private final Callback responseCallback;

    AsyncCall(Callback responseCallback) {
      super("OkHttp %s", redactedUrl());
      this.responseCallback = responseCallback;
    }

    String host() {
      return originalRequest.url().host();
    }

    Request request() {
      return originalRequest;
    }

    RealCall get() {
      return RealCall.this;
    }

    @Override protected void execute() {
      boolean signalledCallback = false;
      try {
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
  }
    
}
```

AsyncCall是一个有name属性的Runnable，在执行前设置线程名为name，执行完毕后恢复。

```java
public abstract class NamedRunnable implements Runnable {
  protected final String name;

  public NamedRunnable(String format, Object... args) {
    this.name = Util.format(format, args);
  }

  @Override public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract void execute();
}
```

Dispatcher#enqueue接受AsyncCall的参数。

```java
public final class Dispatcher {
  private int maxRequests = 64;
  private int maxRequestsPerHost = 5;
  private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();
	
  synchronized void enqueue(AsyncCall call) {
    if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
      runningAsyncCalls.add(call);
      executorService().execute(call);
    } else {
      readyAsyncCalls.add(call);
    }
  }
...
    
  private int runningCallsForHost(AsyncCall call) {
    int result = 0;
    for (AsyncCall c : runningAsyncCalls) {
      if (c.host().equals(call.host())) result++;
    }
    return result;
  }
...
}
```

maxRequests和maxRequestsPerHost都有默认值，分别是64和5，且都有setter方法，setter方法之后都会调用promoteCalls方法尝试执行其他异步任务。

在enqueue方法中，首先会检测正在运行的异步请求数和同一个Host上正在运行的异步请求数是否达到了阈值。如果没有达到则将AsyncCall添加到runningAsyncCalls队列中，同时将这个Runnable提交给线程池执行；否则将AsyncCall添加到readyAsyncCalls队列中进行等待。

这里出现了一个executorService()，这是一个单例实现的线程池，该线程池也可以在Dispatcher的构造方法中初始化。

```java
Dispatcher.java
  ...
  private @Nullable ExecutorService executorService;
  ...
  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }
```

可以看出是一个典型的CachedThreadPool。是一个线程数量不固定的线程池，只有非核心线程，且最大线程数为Integer.Max_VALUE。线程池中的线程都有超时机制，超时时长为60s，超过这个时间的线程会终止被回收。SynchronousQueue是一个无法存储元素的队列，因此提交给线程池的任务都会立即执行。从其特性看，这类线程池适合执行大量的耗时较少的任务。

提交到线程池的AsyncCall会得到执行，最后执行的是AsyncCall#execute方法。

```java
RealCall.java
    @Override protected void execute() {
      boolean signalledCallback = false;
      try {
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        client.dispatcher().finished(this);
      }
    }
```

首先调用getResponseWithInterceptorChain进行网络请求并获取Response，然后根据请求是否被取消，调用对应的回调方法。最后在finally代码块中调用client.dispatcher().finished(this)。

```java
Dispatcher.java
  void finished(AsyncCall call) {
    finished(runningAsyncCalls, call, true);
  }

  private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
    int runningCallsCount;
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      if (promoteCalls) promoteCalls();
      runningCallsCount = runningCallsCount();
      idleCallback = this.idleCallback;
    }

    if (runningCallsCount == 0 && idleCallback != null) {
      idleCallback.run();
    }
  }

  private void promoteCalls() {
    if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
    if (readyAsyncCalls.isEmpty()) return; // No ready calls to promote.

    for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
      AsyncCall call = i.next();

      if (runningCallsForHost(call) < maxRequestsPerHost) {
        i.remove();
        runningAsyncCalls.add(call);
        executorService().execute(call);
      }

      if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
    }
  }
```

将执行完任务的AsyncCall从runningAsyncCalls队列中移除，尝试执行readyAsyncCalls队列中的AsyncCall请求，并叫readyAsyncCalls队列中的AsyncCall移动到runningAsyncCalls中。

# 5 getResponseWithInterceptorChain

```java
RealCall.java
  Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);
    return chain.proceed(originalRequest);
  }
```

首先将以下拦截器添加到List中：

(1) `OkHttpClient`设置的拦截器`interceptors()`；

(2) 重试、重定向的拦截器`RetryAndFollowUpInterceptor`；

(3) 把用户请求转换为网络请求、把网络响应转换为用户响应的`BridgeInterceptor`；

(4) 从缓存中读取响应、将响应写入到缓存的`CacheInterceptor`；

(5) 与服务器建立连接的`ConnectionInterceptor`；

(6) OkHttpClient设置的网络拦截器`networkInterceptors()`；

(7) 真正执行网络请求的`CallServerInterceptor`。

将所有的拦截器保存在interceptors之后，创建一个拦截器责任链RealInterceptorChain，并调用proceed开始处理网络请求。

## 5.1 Interceptor

用来观察、修改、拦截发出去的请求或返回的响应。通常拦截器会在请求或响应上添加、移除、转换头信息。

```java
public interface Interceptor {
  // 可以观察、修改、拦截已经发出去的请求或已返回的响应。
  Response intercept(Chain chain) throws IOException;

  interface Chain {
    Request request();

    Response proceed(Request request) throws IOException;

    @Nullable Connection connection();
  }
}
```

## 5.2 责任链模式实现过程

```java
public final class RealInterceptorChain implements Interceptor.Chain {
  private final List<Interceptor> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  private final int index;
  private final Request request;
  private int calls;

  public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
      HttpCodec httpCodec, RealConnection connection, int index, Request request) {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.request = request;
  }
  
  @Override public Response proceed(Request request) throws IOException {
    return proceed(request, streamAllocation, httpCodec, connection);
  }

  public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec,
      RealConnection connection) throws IOException {
    if (index >= interceptors.size()) throw new AssertionError();

    calls++;

    // If we already have a stream, confirm that the incoming request will use it.
    if (this.httpCodec != null && !this.connection.supportsUrl(request.url())) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must retain the same host and port");
    }

    // If we already have a stream, confirm that this is the only call to chain.proceed().
    if (this.httpCodec != null && calls > 1) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must call proceed() exactly once");
    }

    // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(
        interceptors, streamAllocation, httpCodec, connection, index + 1, request);
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);

    // Confirm that the next interceptor made its required call to chain.proceed().
    if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
      throw new IllegalStateException("network interceptor " + interceptor
          + " must call proceed() exactly once");
    }

    // Confirm that the intercepted response isn't null.
    if (response == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    return response;
  }
}
```

在不考虑OkHttpClient.interceptor()的情况下，上面的代码的解释如下：

(1) 在getResponseWithInterceptorChain创建了一个index为0的RealInterceptorChain（下称链），接着调用proceed方法。

(2) 在RealInterceptorChain#proceed方法中创建了一个index为index + 1的链。

(3) 对当前index的拦截器（RetryAndFollowUpInterceptor）执行interceptor#intercept(chain)方法。在RetryAndFollowUpInterceptor#intercept中执行了chain#proceed方法，而这里chain是RealInterceptorChain的实例，所以又回到RealInterceptorChain#proceed方法中。

(4) 此时index为1，同理会将请求一直传递下去；index自增。

(5) 直到遇到最后一个interceptor CallServerInterceptor并调用其intercept方法。负责与服务器实时通信，获取Response。

(6) Response的返回与Request发出相反，会从最后一个interceptor开始沿着链的反方向传递给每个interceptor。

### 5.2.1 总结

责任链设计模式的实现过程如下：

``RealCall#getResponseWithInterceptorChain`是执行网络请求过程的开始。首先获取处理网络请求的拦截器对象，这些对象会按照次序添加到集合中，依次是OkHttpClient设置的拦截器、重试重定向的拦截器(`RetryAndFollowUpInterceptor`)、负责根据用户请求构建网络请求根据网络响应获取网络响应的BridgeInterceptor`、负责从缓存中获取Response将response保存到缓存中的`CacheInteceptor`、负责与服务器建立连接的`ConnectionInterceptor`、`OkHttpClient`设置的网络拦截器、负责真正与服务器通信的拦截器`CallServerInterceptor`。这些拦截器都是`Interceptor`接口的实现类，Interceptor可以观察、修改、拦截已发出去的请求和已返回的响应。

`OkHttp`会将这些`Interceptor`组织成一个拦截器责任链，将网络请求从链的头部开始沿着链一直传递到尾部的`CallServerInterceptor`，由它与服务器实时通信并获取response。然后response从尾部的interceptor开始沿着链的反方向传递到RealCall的getResponseWithInterceptorChain方法中结束网络请求。

下图为OkHttp工作的大致流程，参考自[拆轮子系列：拆OkHttp](https://blog.piasy.com/2016/07/11/Understand-OkHttp/index.html)

![okhttp_overview](C:\Users\wangjie\Desktop\study\Android\OkHttp3\img\okhttp_overview.jpg)

# 6 RetryAndFollowUpInterceptor

在网络请求过程中发生异常的时候，该拦截器会尝试从异常中恢复，如果恢复失败，则结束网络请求。如果请求成功，根据Response的响应码判断是否需要重试，若不需要则直接返回Response，若需要则检查重试的次数是否达到阈值（20）、是否可以继续重试。

```java
RetryAndFollowUpInterceptor.java
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    streamAllocation = new StreamAllocation(
        client.connectionPool(), createAddress(request.url()), callStackTrace);

    int followUpCount = 0;
    Response priorResponse = null;
    while (true) {
      if (canceled) {
        streamAllocation.release();
        throw new IOException("Canceled");
      }

      Response response = null;
      boolean releaseConnection = true;
      try {
        response = ((RealInterceptorChain) chain).proceed(request, streamAllocation, null, null);
        releaseConnection = false;
      } catch (RouteException e) {
        // The attempt to connect via a route failed. The request will not have been sent.
        if (!recover(e.getLastConnectException(), false, request)) {
          throw e.getLastConnectException();
        }
        releaseConnection = false;
        continue;
      } catch (IOException e) {
        // An attempt to communicate with a server failed. The request may have been sent.
        boolean requestSendStarted = !(e instanceof ConnectionShutdownException);
        if (!recover(e, requestSendStarted, request)) throw e;
        releaseConnection = false;
        continue;
      } finally {
        // We're throwing an unchecked exception. Release any resources.
        if (releaseConnection) {
          streamAllocation.streamFailed(null);
          streamAllocation.release();
        }
      }

      // Attach the prior response if it exists. Such responses never have a body.
      if (priorResponse != null) {
        response = response.newBuilder()
            .priorResponse(priorResponse.newBuilder()
                    .body(null)
                    .build())
            .build();
      }

      Request followUp = followUpRequest(response);

      if (followUp == null) {
        if (!forWebSocket) {
          streamAllocation.release();
        }
        return response;
      }

      closeQuietly(response.body());

      if (++followUpCount > MAX_FOLLOW_UPS) {
        streamAllocation.release();
        throw new ProtocolException("Too many follow-up requests: " + followUpCount);
      }

      if (followUp.body() instanceof UnrepeatableRequestBody) {
        streamAllocation.release();
        throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
      }

      if (!sameConnection(response, followUp.url())) {
        streamAllocation.release();
        streamAllocation = new StreamAllocation(
            client.connectionPool(), createAddress(followUp.url()), callStackTrace);
      } else if (streamAllocation.codec() != null) {
        throw new IllegalStateException("Closing the body of " + response
            + " didn't close its backing stream. Bad interceptor?");
      }

      request = followUp;
      priorResponse = response;
    }
  }
```

1. 调用RealInterceptorChain的proceed方法，将请求沿着责任链传递给BridgeInterceptor。如果遇到异常，则根据异常的类型判断是否
可以恢复，如果不能恢复则抛出异常，结束请求。
2. 根据Response的响应码判断是否需要重试，若不需要则直接返回Response，若需要则检查重试的次数是否达到阈值（20），如果达到阈值则抛出
异常；否则继续重试。

# 7 BridgeInterceptor

将用户请求转换成网络请求，将网络请求沿着责任链传递给CacheInterceptor。将网络响应转换成用户响应。

```java
BridgeInterceptor.java
  @Override public Response intercept(Chain chain) throws IOException {
    Request userRequest = chain.request();
    Request.Builder requestBuilder = userRequest.newBuilder();

    RequestBody body = userRequest.body();
    if (body != null) {
      MediaType contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        requestBuilder.header("Content-Length", Long.toString(contentLength));
        requestBuilder.removeHeader("Transfer-Encoding");
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked");
        requestBuilder.removeHeader("Content-Length");
      }
    }

    if (userRequest.header("Host") == null) {
      requestBuilder.header("Host", hostHeader(userRequest.url(), false));
    }

    if (userRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive");
    }

    // If we add an "Accept-Encoding: gzip" header field we're responsible for also decompressing
    // the transfer stream.
    boolean transparentGzip = false;
    if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
      transparentGzip = true;
      requestBuilder.header("Accept-Encoding", "gzip");
    }

    List<Cookie> cookies = cookieJar.loadForRequest(userRequest.url());
    if (!cookies.isEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies));
    }

    if (userRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", Version.userAgent());
    }

    Response networkResponse = chain.proceed(requestBuilder.build());

    HttpHeaders.receiveHeaders(cookieJar, userRequest.url(), networkResponse.headers());

    Response.Builder responseBuilder = networkResponse.newBuilder()
        .request(userRequest);

    if (transparentGzip
        && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding"))
        && HttpHeaders.hasBody(networkResponse)) {
      GzipSource responseBody = new GzipSource(networkResponse.body().source());
      Headers strippedHeaders = networkResponse.headers().newBuilder()
          .removeAll("Content-Encoding")
          .removeAll("Content-Length")
          .build();
      responseBuilder.headers(strippedHeaders);
      responseBuilder.body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody)));
    }

    return responseBuilder.build();
  }
```

`BridgeInterceptor`总体上做了三件事：(1) 对原始的Request进行检查，设置Content-Type、Content-Length、Transfer-Encoding、Host、Connection、Accept-Encoding、Cookie、UserAgent这些header；(2) 将请求传递给CacheInterceptor；(3) 得到的网络响应数据如果采用了gzip压缩，则对响应进行gzip处理。

在请求前使用了CookieJar的实例读取url关联的Cookie。

```java
    List<Cookie> cookies = cookieJar.loadForRequest(userRequest.url());
    if (!cookies.isEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies));
    }
```
`cookieHeader`就是将cookies里面的键值对拼接成一个字符串k1=v1; k2=v2，其实现如下：
```java
  private String cookieHeader(List<Cookie> cookies) {
    StringBuilder cookieHeader = new StringBuilder();
    for (int i = 0, size = cookies.size(); i < size; i++) {
      if (i > 0) {
        cookieHeader.append("; ");
      }
      Cookie cookie = cookies.get(i);
      cookieHeader.append(cookie.name()).append('=').append(cookie.value());
    }
    return cookieHeader.toString();
  }
```

收到响应后，会存储url的Cookie；

```java
    HttpHeaders.receiveHeaders(cookieJar, userRequest.url(), networkResponse.headers());
```



```java
HttpHeader.java
  public static void receiveHeaders(CookieJar cookieJar, HttpUrl url, Headers headers) {
    if (cookieJar == CookieJar.NO_COOKIES) return;

    List<Cookie> cookies = Cookie.parseAll(url, headers);
    if (cookies.isEmpty()) return;

    cookieJar.saveFromResponse(url, cookies);
  }
```



```java
Cookie.java
  public static List<Cookie> parseAll(HttpUrl url, Headers headers) {
    List<String> cookieStrings = headers.values("Set-Cookie");
    List<Cookie> cookies = null;

    for (int i = 0, size = cookieStrings.size(); i < size; i++) {
      Cookie cookie = Cookie.parse(url, cookieStrings.get(i));
      if (cookie == null) continue;
      if (cookies == null) cookies = new ArrayList<>();
      cookies.add(cookie);
    }

    return cookies != null
        ? Collections.unmodifiableList(cookies)
        : Collections.<Cookie>emptyList();
  }
```

根据Response的Header，获取Set-Cookie字段的Value，由value解析成对应的Cookie对象并使用CookieJar将Cookie存储起来。

**所以，使用OkHttp的Cookie功能时，自定义一个CookieJar就好了，不需要新增拦截器专门处理Cookie问题。**

# 8 CacheInterceptor

该拦截器用来从缓存中读取响应和将响应写入到缓存中。

缓存处理策略一般是：

![cache处理策略](C:\Users\wangjie\Desktop\study\Android\OkHttp3\img\cache处理策略.webp)

```java
CacheInterceptor.java
  @Override public Response intercept(Chain chain) throws IOException {
    // 根据是否设置了缓存以及网络请求，得到一个候选的缓存响应。
    Response cacheCandidate = cache != null
        ? cache.get(chain.request())
        : null;

    long now = System.currentTimeMillis();
	// 根据当前时间、网络请求以及候选缓存响应，获取缓存策略，在缓存策略中，有两个重要的字段
    // networkRequest:网络请求，若为null表示不使用网络；
    // cacheResponse：缓存响应，若为null表示不使用缓存。
    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();
    Request networkRequest = strategy.networkRequest;
    Response cacheResponse = strategy.cacheResponse;

    if (cache != null) {
      cache.trackResponse(strategy);
    }
	//如果候选缓存响应不为null，而缓存响应为null，说明候选缓存响应不可用，关闭它。
    if (cacheCandidate != null && cacheResponse == null) {
      closeQuietly(cacheCandidate.body()); // The cache candidate wasn't applicable. Close it.
    }

    // If we're forbidden from using the network and the cache is insufficient, fail.
    // 如果网络请求为null，且缓存响应过期了，则返回504错误。
    if (networkRequest == null && cacheResponse == null) {
      return new Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(504)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(Util.EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build();
    }

    // If we don't need the network, we're done.
    // 如果网络请求为null，此时命中缓存响应，直接返回缓存响应，不继续处理网络请求了。
    if (networkRequest == null) {
      return cacheResponse.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build();
    }

    Response networkResponse = null;
    try {
      // 没有命中缓存，将网络请求传递给ConnectionInterceptor，执行网络请求。
      networkResponse = chain.proceed(networkRequest);
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
      if (networkResponse == null && cacheCandidate != null) {
        closeQuietly(cacheCandidate.body());
      }
    }

    // If we have a cache response too, then we're doing a conditional get.
    // 如果缓存响应不为null，且网络响应不为null，需要更新缓存响应。
    if (cacheResponse != null) {
      if (networkResponse.code() == HTTP_NOT_MODIFIED) {
        Response response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers(), networkResponse.headers()))
            .sentRequestAtMillis(networkResponse.sentRequestAtMillis())
            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis())
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build();
        networkResponse.body().close();

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
        cache.trackConditionalCacheHit();
        // 跟新缓存响应。
        cache.update(cacheResponse, response);
        return response;
      } else {
        closeQuietly(cacheResponse.body());
      }
    }

    // 构建响应对象。
    Response response = networkResponse.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build();

    if (cache != null) {
      if (HttpHeaders.hasBody(response) && CacheStrategy.isCacheable(response, networkRequest)) {
        // Offer this request to the cache.
        // 将网络响应保存到缓存中。
        CacheRequest cacheRequest = cache.put(response);
        return cacheWritingResponse(cacheRequest, response);
      }

      if (HttpMethod.invalidatesCache(networkRequest.method())) {
        try {
          cache.remove(networkRequest);
        } catch (IOException ignored) {
          // The cache cannot be written.
        }
      }
    }

    return response;
  }
```

`CacheInterceptor`处理请求的逻辑是：

1. 根据`request`从磁盘缓存中获取响应，这是个候选缓存响应；然后根据当前时间、请求和候选缓存响应构造缓存策略`CacheStrategy`。
2. `CacheStrategy`内部有`networkRequest`和`cacheResponse`两个字段。根据`networkRequest`和`cacheResponse`，决定执行网络请求或者使用缓存响应。如果`networkRequest`和`cacheResponse`都为null，则`CacheInterceptor#intercept`返回504的响应；如果`networkRequest`为null，`cacheResponse`不为null则命中了缓存，`CacheInterceptor#intercept`返回`cacheResponse`；如果`networkResponse`不为null，则`CacheInterceptor#intercept`将`networkRequest`继续传递给`ConnectionInterceptor`执行网络请求。
3. 如果得到的网络响应是**304**，表明同一个请求服务器的响应没有更改。且`CacheStrategy`中的`cacheResponse`不为null，需要更新缓存中的响应；根据网络响应的`Header`更新`cacheResponse`的`Header`，更新`cacheResponse`的发送请求的时间和接受响应的时间；`CacheInterceptor#intercept`返回`cacheResponse`。
4. 如果得到的网络响应不是**304**，表明同一个请求服务器的响应更改了。将网络响应保存到缓存中，`CacheInterceptor#intercept`返回网络响应。



## 8.1 Cache

该类可以将网络响应Response保存到文件系统中，可以根据Request获取磁盘缓存的响应Response。

## 8.2 CacheStrategy

其内部有`networkRequest`和`cacheResponse`两个字段。根据`networkRequest`和`cacheResponse`，决定执行网络请求或者使用缓存相应。如果`networkRequest`和`cacheResponse`都为null，则`CacheInterceptor#intercept`返回504的响应；如果`networkRequest`为null，`cacheResponse`不为null则命中了缓存，`CacheInterceptor#intercept`返回`cacheResponse`；如果`networkResponse`不为null，则`CacheInterceptor#intercept`将`networkRequest`继续传递给`ConnectionInterceptor`执行网络请求。

```java
CacheStrategy.java
    public CacheStrategy get() {
      CacheStrategy candidate = getCandidate();

      // 1. 如果networkRequest不为null，且请求头设置了Cache-Control:only-if-cached字段则CacheStrategy中的
    // networkRequest和cacheResponse都为null。CacheInterceptor不会继续传递请求并返回504的响应。
      if (candidate.networkRequest != null && request.cacheControl().onlyIfCached()) {
        // We're forbidden from using the network and the cache is insufficient.
        return new CacheStrategy(null, null);
      }

      return candidate;
    }

    private CacheStrategy getCandidate() {
      // No cached response.
      // 没有缓存响应
      if (cacheResponse == null) {
        return new CacheStrategy(request, null);
      }

      // Drop the cached response if it's missing a required handshake.
      // 如果是HTTPS请求，但是候选的缓存响应没有握手信息，则丢弃该候选的缓存响应。
      if (request.isHttps() && cacheResponse.handshake() == null) {
        return new CacheStrategy(request, null);
      }

      // If this response shouldn't have been stored, it should never be used
      // as a response source. This check should be redundant as long as the
      // persistence store is well-behaved and the rules are constant.
      // 如果不能缓存，则丢弃候选的缓存响应。
      if (!isCacheable(cacheResponse, request)) {
        return new CacheStrategy(request, null);
      }

      CacheControl requestCaching = request.cacheControl();
      // 如果网络请求头制定并不使用缓存，或者网络请求头中含有If-Modified-Since、If-None-Match字段则
      // 使用网络请求，并丢弃候选的缓存响应。
      if (requestCaching.noCache() || hasConditions(request)) {
        return new CacheStrategy(request, null);
      }

      long ageMillis = cacheResponseAge();
      long freshMillis = computeFreshnessLifetime();

      if (requestCaching.maxAgeSeconds() != -1) {
        freshMillis = Math.min(freshMillis, SECONDS.toMillis(requestCaching.maxAgeSeconds()));
      }

      long minFreshMillis = 0;
      if (requestCaching.minFreshSeconds() != -1) {
        minFreshMillis = SECONDS.toMillis(requestCaching.minFreshSeconds());
      }

      long maxStaleMillis = 0;
      CacheControl responseCaching = cacheResponse.cacheControl();
      if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
        maxStaleMillis = SECONDS.toMillis(requestCaching.maxStaleSeconds());
      }

      //根据候选缓存的缓存时间，缓存可接受最大过期时间等等HTTP协议上的规范，来判断缓存是否可用
      if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
        Response.Builder builder = cacheResponse.newBuilder();
        if (ageMillis + minFreshMillis >= freshMillis) {
          builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
        }
        long oneDayMillis = 24 * 60 * 60 * 1000L;
        if (ageMillis > oneDayMillis && isFreshnessLifetimeHeuristic()) {
          builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
        }
        return new CacheStrategy(null, builder.build());
      }

      // Find a condition to add to the request. If the condition is satisfied, the response body
      // will not be transmitted.
      // 请求条件, 当候选缓存的响应头中含有Date字段、Last-Modified、ETag字段的时候，需要构造网络请求
      // 在请求头中添加响应的字段If-None-Match或If-Modified-Since，向服务器确认缓存的有效性。
      String conditionName;
      String conditionValue;
      if (etag != null) {
        conditionName = "If-None-Match";
        conditionValue = etag;
      } else if (lastModified != null) {
        conditionName = "If-Modified-Since";
        conditionValue = lastModifiedString;
      } else if (servedDate != null) {
        conditionName = "If-Modified-Since";
        conditionValue = servedDateString;
      } else {
        return new CacheStrategy(request, null); // No condition! Make a regular request.
      }

      Headers.Builder conditionalRequestHeaders = request.headers().newBuilder();
      Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);
	  // 构造一个请求，该请求到达服务器时会询问服务器资源是否过期
      Request conditionalRequest = request.newBuilder()
          .headers(conditionalRequestHeaders.build())
          .build();
      return new CacheStrategy(conditionalRequest, cacheResponse);
    }
```

`CacheStrategy`满足以下规则：

1. 如果候选的缓存响应为null，则`cacheResponse`为null，没有缓存。

2. 如果请求是`Https`，候选的缓存响应没有`TLS`的握手信息，则`cacheResponse`为null，没有缓存。

3. 如果候选缓存响应的缓存时效时间过期了则`cacheResponse`为null，没有缓存。否则`networkRequest`为null，`cacheResponse`为候选缓存响应，可以命中缓存。

4. 如果候选缓存的响应头中含有Date字段、Last-Modified、ETag字段的时候，需要根据元素的请求构造新的网络请求（请求头添加字段If-None-Match或者If-Modified-Since）询问服务器对应的资源是否更改，`cacheResponse`为候选缓存响应。

   

## 8.3 CacheControl

Cache-Control头部有服务器和客户端的缓存指令，这些指令设置了响应可以被缓存的策略。

# 9 ConnectionInterceptor

该类负责与服务器建立连接，连接池的维护、链接的复用，并将网络请求传递给下一个interceptor。

在创建新connection时，会执行TCP + TLS握手，然后放入连接池中。

```java
ConnectionInterceptor.java 
@Override public Response intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    Request request = realChain.request();
    Transmitter transmitter = realChain.transmitter();

    // We need the network to satisfy this request. Possibly for validating a conditional GET.
    boolean doExtensiveHealthChecks = !request.method().equals("GET");
    Exchange exchange = transmitter.newExchange(chain, doExtensiveHealthChecks);

    return realChain.proceed(request, transmitter, exchange);
  }
```

其中`Transmitter`是关键。

`transmitter.newExchange`会建立链接。



```java
//Transmitter
  Exchange newExchange(Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
    synchronized (connectionPool) {
      if (noMoreExchanges) {
        throw new IllegalStateException("released");
      }
      if (exchange != null) {
        throw new IllegalStateException("cannot make a new request because the previous response "
            + "is still open: please call response.close()");
      }
    }

    ExchangeCodec codec = exchangeFinder.find(client, chain, doExtensiveHealthChecks);
    Exchange result = new Exchange(this, call, eventListener, exchangeFinder, codec);

    synchronized (connectionPool) {
      this.exchange = result;
      this.exchangeRequestDone = false;
      this.exchangeResponseDone = false;
      return result;
    }
  }

```

会调用`ExchangeFinder.find`方法。

```java
//ExchangeFinder
  public ExchangeCodec find(
      OkHttpClient client, Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
    int connectTimeout = chain.connectTimeoutMillis();
    int readTimeout = chain.readTimeoutMillis();
    int writeTimeout = chain.writeTimeoutMillis();
    int pingIntervalMillis = client.pingIntervalMillis();
    boolean connectionRetryEnabled = client.retryOnConnectionFailure();

    try {
      RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout,
          writeTimeout, pingIntervalMillis, connectionRetryEnabled, doExtensiveHealthChecks);
      return resultConnection.newCodec(client, chain);
    } catch (RouteException e) {
      trackFailure();
      throw e;
    } catch (IOException e) {
      trackFailure();
      throw new RouteException(e);
    }
  }

```



该方法有两处要点，第一处就是调用`findHealthyConnection`得到一个可用的connection，第二处就是调用`resultConnection.newCodec`得到一个对HTTP请求进行编码、HTTP响应进行解码的`HttpCodec`。

先跟踪`findHealthyConnection`方法，该方法会调用`findConnection`得到一个connection，然后对其调用`isHealth(true)`方法进行健康诊断。如果健康，那么就可以返回该connection了；否则，从连接池中移除，并继续while循环。



```java
//ExchangeFinder
  private RealConnection findHealthyConnection(int connectTimeout, int readTimeout,
      int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled,
      boolean doExtensiveHealthChecks) throws IOException {
    while (true) {
      RealConnection candidate = findConnection(connectTimeout, readTimeout, writeTimeout,
          pingIntervalMillis, connectionRetryEnabled);

      // If this is a brand new connection, we can skip the extensive health checks.
      synchronized (connectionPool) {
        if (candidate.successCount == 0 && !candidate.isMultiplexed()) {
          return candidate;
        }
      }

      // Do a (potentially slow) check to confirm that the pooled connection is still good. If it
      // isn't, take it out of the pool and start again.
      if (!candidate.isHealthy(doExtensiveHealthChecks)) {
        candidate.noNewExchanges();
        continue;
      }

      return candidate;
    }
  }

```

在`findConnection`方法中，会先看已经存在的connection，然后再看连接池，最后都没有就创建新的connection。

```java

  private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout,
      int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
    boolean foundPooledConnection = false;
    RealConnection result = null;
    Route selectedRoute = null;
    RealConnection releasedConnection;
    Socket toClose;
    synchronized (connectionPool) {
      if (transmitter.isCanceled()) throw new IOException("Canceled");
      hasStreamFailure = false; // This is a fresh attempt.

      // Attempt to use an already-allocated connection. We need to be careful here because our
      // already-allocated connection may have been restricted from creating new exchanges.
      releasedConnection = transmitter.connection;
      toClose = transmitter.connection != null && transmitter.connection.noNewExchanges
          ? transmitter.releaseConnectionNoEvents()
          : null;

      if (transmitter.connection != null) {
        // We had an already-allocated connection and it's good.
        result = transmitter.connection;
        releasedConnection = null;
      }

      if (result == null) {
        // Attempt to get a connection from the pool.
        if (connectionPool.transmitterAcquirePooledConnection(address, transmitter, null, false)) {
          foundPooledConnection = true;
          result = transmitter.connection;
        } else if (nextRouteToTry != null) {
          selectedRoute = nextRouteToTry;
          nextRouteToTry = null;
        } else if (retryCurrentRoute()) {
          selectedRoute = transmitter.connection.route();
        }
      }
    }
    closeQuietly(toClose);

    if (releasedConnection != null) {
      eventListener.connectionReleased(call, releasedConnection);
    }
    if (foundPooledConnection) {
      eventListener.connectionAcquired(call, result);
    }
    if (result != null) {
      // If we found an already-allocated or pooled connection, we're done.
      return result;
    }

    // If we need a route selection, make one. This is a blocking operation.
    boolean newRouteSelection = false;
    if (selectedRoute == null && (routeSelection == null || !routeSelection.hasNext())) {
      newRouteSelection = true;
      routeSelection = routeSelector.next();
    }

    List<Route> routes = null;
    synchronized (connectionPool) {
      if (transmitter.isCanceled()) throw new IOException("Canceled");

      if (newRouteSelection) {
        // Now that we have a set of IP addresses, make another attempt at getting a connection from
        // the pool. This could match due to connection coalescing.
        routes = routeSelection.getAll();
        if (connectionPool.transmitterAcquirePooledConnection(
            address, transmitter, routes, false)) {
          foundPooledConnection = true;
          result = transmitter.connection;
        }
      }

      if (!foundPooledConnection) {
        if (selectedRoute == null) {
          selectedRoute = routeSelection.next();
        }

        // Create a connection and assign it to this allocation immediately. This makes it possible
        // for an asynchronous cancel() to interrupt the handshake we're about to do.
        result = new RealConnection(connectionPool, selectedRoute);
        connectingConnection = result;
      }
    }

    // If we found a pooled connection on the 2nd time around, we're done.
    if (foundPooledConnection) {
      eventListener.connectionAcquired(call, result);
      return result;
    }

    // Do TCP + TLS handshakes. This is a blocking operation.
    result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis,
        connectionRetryEnabled, call, eventListener);
    connectionPool.routeDatabase.connected(result.route());

    Socket socket = null;
    synchronized (connectionPool) {
      connectingConnection = null;
      // Last attempt at connection coalescing, which only occurs if we attempted multiple
      // concurrent connections to the same host.
      if (connectionPool.transmitterAcquirePooledConnection(address, transmitter, routes, true)) {
        // We lost the race! Close the connection we created and return the pooled connection.
        result.noNewExchanges = true;
        socket = result.socket();
        result = transmitter.connection;

        // It's possible for us to obtain a coalesced connection that is immediately unhealthy. In
        // that case we will retry the route we just successfully connected with.
        nextRouteToTry = selectedRoute;
      } else {
        connectionPool.put(result);
        transmitter.acquireConnectionNoEvents(result);
      }
    }
    closeQuietly(socket);

    eventListener.connectionAcquired(call, result);
    return result;
  }

```



```java
//RealConnection
  public void connect(int connectTimeout, int readTimeout, int writeTimeout,
      int pingIntervalMillis, boolean connectionRetryEnabled, Call call,
      EventListener eventListener) {
    if (protocol != null) throw new IllegalStateException("already connected");

    RouteException routeException = null;
    List<ConnectionSpec> connectionSpecs = route.address().connectionSpecs();
    ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);

    if (route.address().sslSocketFactory() == null) {
      if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication not enabled for client"));
      }
      String host = route.address().url().host();
      if (!Platform.get().isCleartextTrafficPermitted(host)) {
        throw new RouteException(new UnknownServiceException(
            "CLEARTEXT communication to " + host + " not permitted by network security policy"));
      }
    } else {
      if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
        throw new RouteException(new UnknownServiceException(
            "H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
      }
    }

    while (true) {
      try {
        if (route.requiresTunnel()) {
          connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
          if (rawSocket == null) {
            // We were unable to connect the tunnel but properly closed down our resources.
            break;
          }
        } else {
          connectSocket(connectTimeout, readTimeout, call, eventListener);
        }
        // 确定协议。
        establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
        eventListener.connectEnd(call, route.socketAddress(), route.proxy(), protocol);
        break;
      } catch (IOException e) {
        closeQuietly(socket);
        closeQuietly(rawSocket);
        socket = null;
        rawSocket = null;
        source = null;
        sink = null;
        handshake = null;
        protocol = null;
        http2Connection = null;

        eventListener.connectFailed(call, route.socketAddress(), route.proxy(), null, e);

        if (routeException == null) {
          routeException = new RouteException(e);
        } else {
          routeException.addConnectException(e);
        }

        if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(e)) {
          throw routeException;
        }
      }
    }

    if (route.requiresTunnel() && rawSocket == null) {
      ProtocolException exception = new ProtocolException("Too many tunnel connections attempted: "
          + MAX_TUNNEL_ATTEMPTS);
      throw new RouteException(exception);
    }

    if (http2Connection != null) {
      synchronized (connectionPool) {
        allocationLimit = http2Connection.maxConcurrentStreams();
      }
    }
  }

...
      private void establishProtocol(ConnectionSpecSelector connectionSpecSelector,
      int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
    if (route.address().sslSocketFactory() == null) {
      if (route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
        socket = rawSocket;
        protocol = Protocol.H2_PRIOR_KNOWLEDGE;
        startHttp2(pingIntervalMillis);
        return;
      }

      socket = rawSocket;
      protocol = Protocol.HTTP_1_1;
      return;
    }

    eventListener.secureConnectStart(call);
    connectTls(connectionSpecSelector);
    eventListener.secureConnectEnd(call, handshake);

    if (protocol == Protocol.HTTP_2) {
      startHttp2(pingIntervalMillis);
    }
  }

  private void startHttp2(int pingIntervalMillis) throws IOException {
    socket.setSoTimeout(0); // HTTP/2 connection timeouts are set per-stream.
    http2Connection = new Http2Connection.Builder(true)
        .socket(socket, route.address().url().host(), source, sink)
        .listener(this)
        .pingIntervalMillis(pingIntervalMillis)
        .build();
    http2Connection.start();
  }

```



在创建新connection时，会执行TCP + TLS握手，然后放入连接池中。
在调用`connect`进行握手时会调用`establishProtocol`方法确定协议：

也就是说，在建立连接时，如果是HTTP_2，就会初始化一个`http2Connection`。



# 10 CallServerInterceptor

该拦截器和服务器进行网络通信，获取网络请求的响应。

```java
CallServerInterceptor.java
  @Override public Response intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    HttpCodec httpCodec = realChain.httpStream();
    StreamAllocation streamAllocation = realChain.streamAllocation();
    RealConnection connection = (RealConnection) realChain.connection();
    Request request = realChain.request();

    long sentRequestMillis = System.currentTimeMillis();
    httpCodec.writeRequestHeaders(request);

    Response.Builder responseBuilder = null;
    if (HttpMethod.permitsRequestBody(request.method()) && request.body() != null) {
      // If there's a "Expect: 100-continue" header on the request, wait for a "HTTP/1.1 100
      // Continue" response before transmitting the request body. If we don't get that, return what
      // we did get (such as a 4xx response) without ever transmitting the request body.
      if ("100-continue".equalsIgnoreCase(request.header("Expect"))) {
        httpCodec.flushRequest();
        responseBuilder = httpCodec.readResponseHeaders(true);
      }

      if (responseBuilder == null) {
        // Write the request body if the "Expect: 100-continue" expectation was met.
        Sink requestBodyOut = httpCodec.createRequestBody(request, request.body().contentLength());
        BufferedSink bufferedRequestBody = Okio.buffer(requestBodyOut);
        request.body().writeTo(bufferedRequestBody);
        bufferedRequestBody.close();
      } else if (!connection.isMultiplexed()) {
        // If the "Expect: 100-continue" expectation wasn't met, prevent the HTTP/1 connection from
        // being reused. Otherwise we're still obligated to transmit the request body to leave the
        // connection in a consistent state.
        streamAllocation.noNewStreams();
      }
    }

    httpCodec.finishRequest();

    if (responseBuilder == null) {
      responseBuilder = httpCodec.readResponseHeaders(false);
    }

    Response response = responseBuilder
        .request(request)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

    int code = response.code();
    if (forWebSocket && code == 101) {
      // Connection is upgrading, but we need to ensure interceptors see a non-null response body.
      response = response.newBuilder()
          .body(Util.EMPTY_RESPONSE)
          .build();
    } else {
      response = response.newBuilder()
          .body(httpCodec.openResponseBody(response))
          .build();
    }

    if ("close".equalsIgnoreCase(response.request().header("Connection"))
        || "close".equalsIgnoreCase(response.header("Connection"))) {
      streamAllocation.noNewStreams();
    }

    if ((code == 204 || code == 205) && response.body().contentLength() > 0) {
      throw new ProtocolException(
          "HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
    }

    return response;
  }
```



# 11 通过添加自定义拦截器实现网络数据缓存

[Retrofit和OkHttp使用网络缓存数据](https://www.jianshu.com/p/e0dd6791653d)

[WanAndroidWYJ github](https://github.com/aSmartTortoise/kotlinLearningPlan/tree/master/WanAndroidWYJ)

由上文`CacheInterceptor`处理请求的源码分析，可以在拦截器链上添加自定义拦截器实现网络数据缓存到本地。

## 11.1 CacheControlInterceptor



```kot

class CacheControlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request
        if (!NetWorkUtil.isNetworkAvailable(WanAndroidApplication.context)) {
            // 无网络时，设置候选缓存响应过期之后的最大可使用的时间24h，如果缓存时间超过了有效期后的12h，
            // 则因为CacheControl#onlyIfCached 为true，则返回504响应。
            request = chain.request().newBuilder()
                .cacheControl(CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(60 * 60 * 24, TimeUnit.SECONDS)
                    .build())
                .build()

        } else {
            // 有网络时，设置候选缓存响应最大有效时间30s，该设置会CacheInterceptor的CacheStrategy有效果
            // 这样在缓存有效期内的请求会复用缓存响应。
           request = chain.request().newBuilder()
                .cacheControl(CacheControl.Builder()
                    .maxAge(30, TimeUnit.SECONDS)
                    .build())
               .build()
        }
        return chain.proceed(request)
    }
}

```

网络可用修改`Request`的Header，设置`Cache-Control`指定缓存的`max-age`；网络不可用修改`Request`的Header，设置`Cache-Control`指定缓存的`max-stale`。

## 11.2 CacheNetworkInterceptor



```k

class CacheNetworkInterceptor : Interceptor {

    companion object {
        private const val TAG = "CacheNetworkInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

//        Log.d(TAG, "intercept: request url:${request.url().toString()}")

        val response = chain.proceed(request)
        val cacheControl = response.cacheControl().toString()

//        Log.d(TAG, "intercept: response header:${response.headers().toString()}")
//        Log.d(TAG, "intercept: response cache control:$cacheControl")
        val maxAge = 60
        return response
            .newBuilder()
            .removeHeader("Pragma")
            //对响应进行最大60秒有效期的缓存，会对CacheInterceptor的CacheStrategy有影响
//            .header("Cache-Control", "public, max-age=$maxAge")
            .build()
    }
}

```

设置网络响应`Response`的`Cache-Control`，比如移除`Pragma`或者设置缓存的有效时间`max-age`。

将`CacheControlInterceptor`添加到`RetryAndFollowUpInterceptor`的前面，将`CacheNetworkInterceptor`添加到`Interceptor`的后面。

如下在构造`OkHttpClient`对象的时候进行拦截器的添加：

```k

    private fun getOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        //设置 请求的缓存的大小跟位置
        val cacheFile = File(WanAndroidApplication.context.cacheDir, "cache")
        val cache = Cache(cacheFile, HttpConstant.MAX_CACHE_SIZE)
        return OkHttpClient.Builder()
            .addInterceptor(CacheControlInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(SaveCookieInterceptor())
            .addNetworkInterceptor(CacheNetworkInterceptor())
            .cache(cache)  //添加缓存
            .connectTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) // 重试
            .build()

//             cookieJar(CookieManager())

    }

```

