# Stache

Stache (Stub+Cache) is Java test library that enables fast + powerful stubbing 
of external web services. It allows you to gain all the power of a live integration test 
during development and debugging, while in-turn being able to seamlessly cache and play back data in 
a safe and deterministic way for use in unit testing.

## How It Works

The Stache framework wraps your Stache stubs in a proxy that intercepts all calls to the stub.
By default, this proxy is backed by a read-first cache short-circuiting mechanism called the Smart Cache logic. 

In the case of cache hits, the proxy + Smart Cache logic will altogether bypass the
live web service call and return the cached data. In the case of cache misses, the proxy + Smart Cache logic
will call the live web service, cache the response data, and return the live data.

**The net effect here is that you have a test stub that automatically teaches itself how to playback
real web service data with no manual intervention.** This pattern is extremely powerful
if used very early on in the development process as your build out your web service integration code, and it is
equally powerful for finalizing your web service integrations into repeatable unit tests that you know are backed by real data.

## Usage

*It is assumed you are using Junit as your test runner framework*

### Test Class Setup

If you plan on using the Stache annotation framework (recommendated), you need to add the Stache extension to your 
test class

```java
@ExtendWith(StacheJunitExtension.class)
public class MyTestSuite {
}
```

### Stache Stub Setup

In your test class, annotate your web client of choice with the `@Stache` annotation

```java
@Stache
HelloResource helloResource = (HelloResource) new JaxrsFactoryImpl().createJaxrsProxy("http://0.0.0.0:8181/services/hello", HelloResource.class);
```
*Here I am using a Jaxrs annotated interface and client proxy implementation for the live web service, but you can use any web client implementation you want.*

There is an abbreviated setup if your web targets are already Jaxrs annotated interfaces. In this setup, the Stache annotation framework
will automatically create you a Jaxrs proxy client implementation.

```java
@JaxrsStache(url = "http://0.0.0.0:8181/services/hello")
private HelloResource helloResource;
```

### Training Your Stache

Once your Stache stubs are propery setup and initializing properly, you can simply start using them in your tests as
you would a normal test stub

```java
@Test
void smartCacheMode() {
    final Greeting greeting1 = helloResource.greet();
}
```

For the first test run, your Stache stub will not have any backing cache data, so it will reach out to the live web 
service and automatically cache the data.

All subsequent test runs will simply read this cached data. **No live web service calls, and no manual stubbing.**

You can always delete your backing cache data or change the RunStrategy (see below) if you want to 
force the Stache to reach out to the live web service and cache new data.

# Configuration

## Run Stratgy Options

The Stache framework supports 3 different RunStrategy options. The 3 options are:

1. `SMART_CACHE_MODE` - This is the default mode. It will read from the cache if it exists, and if it does not exist, it will call the live web service and cache the response.
2. `DEV_MODE` - This mode effectively bypasses all Smart Caching logic. It always reaches out to the live web service, and never caches anything. Intended for development purposes and debugging purposes, as this essentially turns the stub into a live web service.
3. `READ_ONLY_MODE` - This option will only ever read data from the cache, and will never call a live web service. If there is a cache miss, an error is thrown.

The `RunStrategy` defaults to `SmartCacheMode`. You can easily swap between the modes to achieve the behavior you want. 

```java
@Stache(
        runConfig = RunStrategy.DEV_MODE
)
HelloResource helloJaxrsResource = (HelloResource) new JaxrsFactoryImpl().createJaxrsProxy("http://0.0.0.0:8181/services/hello", HelloResource.class);
```

## Advanced Configuration

There are advanced configuration options supported and available through the `@Advanced` annotation.

```java
@Stache(
        runConfig = RunStrategy.SMART_CACHE_MODE,
        advanced = @Advanced(
                resourcesDirectoryPath = "src/test/resources",
                cacheDirectoryName = "stache",
                cacheNamingStrategy = CacheNamingStrategy.METHOD_SCOPED
        )
)
HelloResource helloJaxrsResource = (HelloResource) new JaxrsFactoryImpl().createJaxrsProxy("http://0.0.0.0:8181/services/hello", HelloResource.class);
```
- `resourcesDirectoryPath` - The path to the base where the cache data will be stored. Default is `src/test/resources` and assumes you have a standard maven project setup
- `cacheDirectoryName` - The name of the directory where the cache data will be stored for this particular Stache instance. Default is `stache`
- `cacheNamingStrategy` - Gives more fine tune control to how the cache is named for an individual Stache instance and unit test.

## Manual Instantiation (advanced)

If the Annotation framework doesn't meet your needs, you can manually instantiate a Stache stub.

First manually build a Java config:

```java
StacheConfiguration stacheConfiguration = StacheConfiguration.builder()
        .runConfig(StacheRunConfiguration.builder()
                .runStrategy(RunStrategy.SMART_CACHE_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir(TEST_CACHE_DIR)
                .cacheNamingStrategy(Defaults.CACHE_NAMING_STRATEGY)
                .build())
        .testContext(StacheTestContext.builder()
                .testMethodName(testMethodName)
                .build())
        .build();
```

Then build a `CachingProxyFactory`:

```java
final CachingProxyFactory cachingProxyFactory = new CachingProxyFactory(stacheConfiguration);
```

Then use the `CachingProxyFactory` to build your Stache stub:

```java
final HelloResource helloResource = cachingProxyFactory.buildCachingProxy(realLiveImpl, HelloResource.class);
```

Note you need to bring your own 'real implementation' to wrap in the Stache caching proxy implementation. 


## More Implementation + Usage Details

The caching logic is intelligent enough to differentiate between the same Stache stub method
being called with different parameters. So the backing data behind GET request to www.example.com/user/1 will be cached
separately from a GET request to www.example.com/user/2.

The cache is stored be in the form of files on your file system, and the intention is that they 
can eventually get added your source control when you are comfortable with them

The cache data default will be stored in your test resources directory (assumes maven) but this is configurable,
along with many other aspects of the Stache configuration.

It is essentially a proxy proxy !


