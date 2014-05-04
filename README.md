# Machine

Project Machine is the next edition of Project Robot; Machine provides well tested APIs that make easy to distribute
and process long running tasks over multiple server instances.

The general idea of Machine is that task processing is split up into:
- a "Master" instance which distributes and preprocesses tasks, polling Machine Management for tasks.
- Consumer" instances which listens for tasks distributed to it.

## Machine Management

Machine Management provides webservice and webapplication interfaces to manage servers and tasks and is also a message
broker, where instances can subscribe to topics and publish events on those topics. Events are routed to all subscribers
to a given message's topic.

## Machine Humanity

Machine Humanity makes making robots seem human as easy as possible. Machine Humanity provides APIs for
generating random sentences.

## Machine Backbone

Machine Backbone is a process that sits on every instance and regularly heartbeats to Management, such that Management
regularly obtains information on instances and instances can be more or less monitored with Machine Backbone.
