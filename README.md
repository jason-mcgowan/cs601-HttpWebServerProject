HTTP Web Server at Socket Level in Java
=======================


### Background
This project was done as part of the Fall 2021 CS603 Principles of Software Development course at the University of San Francisco.

### Goals
Implement the following:
- An HTTP web server in Java without using any built-in or external http libraries.
- Two web applications to take user input and provide responses.
- Unit testing.
- Remote deployment of web server.

### Requirements

1. Use raw sockets for this assignment. (No Tomcat, Jetty, java.net.http, etc.)
2. Support `GET` and `POST` requests. Any other HTTP method will result in a `405 Method Not Allowed` status code. See [HTTP Status Code Definitions](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html). 
3. Return [well-formed XHTML](https://www.w3schools.com/html/html_xhtml.asp). *Note: empty elements must always be closed.*
4. Support different web applications. 
5. Multithreading: Each incoming request will be handled by a different thread.

### Design overview
- Server object listens to incoming traffic on a separate thread, passes off messages to a handler threadpool.
- Incoming message is checked for adherence to protocol rules and either processed or an exception is thrown and handled.
- Requests are validated with healper classes and passed to registered handlers as appropriate.
- Handlers process and send an HTTP response to the connected user.
