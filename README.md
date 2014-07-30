# Hileco DRPC

This library lets you make distributed remote procedure calls and get results back, with plain Java.

- Requires Java 8.
- RPC go over HTTP, using Apache HTTPClient and an embedded Grizzly Server.
- RPC content is JSON, written and read streaming, metadata is in HTTP headers.
- You can publish and consume standard java interfaces. 
- Builds with maven.

## Router

```java
SubscriptionStore subscriptionStore = new CacheSubscriptionStore();
RouterServer routerServer = new RouterServer(subscriptionStore);
routerServer.start(8080);
```

This starts a message router on port 8080, using an in-memory store of subscriptions. Subscriptions contain only a topic, an address, and a unique identifier.

## Services

Define the interface of your service(s)

```java
public interface CalculatorService {

    public Integer calculate(Integer a, Integer b);

}
```

Instantiate a client, client to router communication is two way.

```java
Client client = new Client(routerURL, localHostname, localPort);
```

Notify the router we have a `CalculatorService` that other clients can talk to.

```java
client.publish(CalculatorService.class, // functionality to expose
               "remote-calculator",     // service identifier for rpc
               (a, b) -> a + b);        // implementation of our service
```

## Clients

Instantiate a client, client to router communication is two way. Create a service connector using the new client.

```java
Client client = new Client(routerURL, localHostname, localPort);
ServiceConnector<CalculatorService> connector = client.connector(CalculatorService.class);
```

With the connector we can make distributed calls, with regular interfaces, and nice Java 8 syntax to go along with it. This example will call every CalculatorService ( registered as described earlier ), and then logs any results it gets.

```java
connector.drpc(d -> d.calculate(1, 2),
               r -> LOG.info("CalculatorService#calculate(1,2) = {}", r));
```

And targeted calls.

```java
CalculatorService remoteCalculator = connector.connect("remote-calculator"); // connect to the calculator identified by the id we've registered with at the service-side
remoteCalculator.calculate(1,2);
```
