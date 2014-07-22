# Hileco DRPC

This library lets you make distributed remote procedure calls and get results back, with plain Java.

- Requires Java 8.
- Internally RPC go over HTTP, routers & handlers implement [JAX-RS](http://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services) interfaces.
- Internally RPC are in JSON format, almost exactly like [JSON-RPC](http://en.wikipedia.org/wiki/JSON-RPC).
- RPC and distributed RPC calls can be made with standard Java, using service Java interfaces.
- Builds with maven

## Router

```java
    Router router = new Router();
```

`Router` implements `JaxrsRouterService`, which you can place anywhere as a JAX-RS service. Currently routers must be on a single instance and subscriptions are stored in memory

## Services

Define the interface of your service(s)

```java
    public interface CalculatorService {

        public Integer calculate(Integer a, Integer b);
    
    }
```

Point to the router, and configure the Service's port in the `HTTPSubscription` so the router can do asynchronous callbacks.

```java
    RouterClient routerClient = new RouterClient(routerURL, () -> {
        HTTPSubscription subscription = new HTTPSubscription();
        subscription.setPort(port);
        return subscription;
    });
```

If you want to let the service also be the router _you can_, just supply a `Router` object as first parameter instead of a URL

Listen for any service calls for a given type

```java
    routerClient.getClient().listen(CalculatorService.class, calculatorServiceImpl);
```

And / or listen for any service calls for a given type and identifier

```java
    routerClient.getClient().listen(CalculatorService.class, specialCalculatorWithId123, 123);
```

## Clients

Point to the router, and configure the Client's port in the `HTTPSubscription` so the router can do asynchronous callbacks

If you want to let the client also be the router _you can_, just supply a `Router` object as first parameter instead of a URL

```java
    RouterClient routerClient = new RouterClient(router, () -> {
        HTTPSubscription subscription = new HTTPSubscription();
        subscription.setPort(port);
        return subscription;
    });
```

After that you can get your `Client` object with  `RouterClient#getClient`, to create `Connector`s for any service interface

```java
    Client client = routerClient.getClient();
    Connector<CalculatorService, ?> calculatorConnector = client.connector(CalculatorService.class);
    Connector<RemoteObject, ?> remoteObjectConnector = client.connector(RemoteObject.class);
```

With this client you can make distributed calls, with regular interfaces, and nice Java 8 syntax to go along with it. This example will call every CalculatorService ( registered as described earlier ), and then logs any results it gets.

```java
    calculatorConnector.drpc((d) -> d.calculate(1, 2), // `d` is a CalculatorService instance on which you must make 1 call
                             (r) -> LOG.info("CalculatorService#calculate(1,2) = {}", r));
```

And targeted calls

```java
    CalculatorService specialCalculatorWithId123 = remoteObjectConnector.connect(123);
    specialCalculatorWithId123.calculate(1,2);
```
