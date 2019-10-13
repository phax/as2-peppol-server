# as2-peppol-server

[![Build Status](https://travis-ci.org/phax/as2-peppol-server.svg?branch=master)](https://travis-ci.org/phax/as2-peppol-server)
﻿
A demo AS2 server to easily receive AS2 messages from PEPPOL.
This project is only meant as a demo project for illustrative purposes, on how to
implement a PEPPOL AS2 server. This server implementation writes all incoming documents to disc and does not do anything else with them! Additional or different logic must be implemented!

This AS2 server is based on my **[as2-lib](https://github.com/phax/as2-lib)** library, as well as on **[as2-peppol-servlet](https://github.com/phax/as2-peppol-servlet)**, **[peppol-commons](https://github.com/phax/peppol-commons)** and **[ph-ubl](https://github.com/phax/ph-ubl)**.
A client to send messages to this server is [as2-peppol-client](https://github.com/phax/as2-peppol-client).

This project is licensed under the Apache 2 License.

# Project structure
This project is a Java 1.8+ web application and as such meant to be used in an application
server (like Tomcat, Jetty etc.).

It consists of the following major components:
  * A specialized servlet for retrieval: `com.helger.peppol.as2server.servlet.PEPPOLAS2ReceiveServlet` which defines the configuration for processing incoming files. The servlet is referenced from `src/main/webapp/WEB-INF/web.xml`.
  * The main handler for an incoming SBDH document in class `com.helger.peppol.as2server.handler.AS2IncomingSBDHandler`. This class is referenced via the default SPI lookup mechanism (see `src/main/resources/META-INF/services/`).
  * Additionally a configuration file `src/main/resources/as2-server.properties` is provided that defines the AS2 specific setup. It contains the path to the keystore as well as in which folders to store what
  
# Before you start
Before this project can be run in a useful way a PKCS12 keystore with your PEPPOL AP certificate must be provided. By default the keystore must be located in `src/main/resources/keystore/ap.pilot.p12` and must have the password `peppol`. To change this edit the `as2-server.properties` file.

Btw. you need no database to run this server. If you want one just use one - but you don't need to. 

# Run it

There are two options:

### Running locally on your machine

To do so you need to proceed the following steps:
    * get your machine ready for java development with JDK 1.8.X, maven, some IDE tool, e.g. [Eclipse](https://www.eclipse.org/downloads/)
    * you generate keystore with your certificate obtained from PEPPOP authority or self-signed (see procedure below)
    * build the project 
    * execute class `com.helger.peppol.as2server.jetty.RunInJettyPEPPOLAS2` from within your IDE (as a standard Java application). It starts up a minimal server and listens on port 8080. The servlet that receives PEPPOL messages listens to path `/as2/` and supports only HTTP method POST.

## Run already prepared application in Docker container

To do so you have [Docker](https://docs.docker.com/get-started/) installed in your machine.
This approach is useful if you need just run the reference implementation against your PEPPOL Access Point implementation.

## Docker notes

The prebuild Dockerfile builds the WAR and runs it in Jetty.

Build like this: `docker build -t as2-peppol-server .`

Run like this: `docker run -d --name as2-peppol-server -p 8888:8080 as2-peppol-server`

Locate your browser to `http://localhost:8888` to check if it is running.

Stop like this: `docker stop as2-peppol-server`

And remove like this: `docker rm as2-peppol-server`


# Test it
Now that the AS2 server is running you may have a closer look at my **[as2-peppol-client](https://github.com/phax/as2-peppol-client)** project which lets you send AS2 messages to a server.
If both client and server are configured correctly a successful message exchange should be easily possible.

## Notes

### Self-signed certificate

* follow the [instruction](https://www.digitalocean.com/community/tutorials/openssl-essentials-working-with-ssl-certificates-private-keys-and-csrs)

**IMPORTANT** 

    * CN -- Common Name must follow PEPPOL AP name convention and be in format : APP_1000000XXX, for example APP_1000000312
    * it uses PKCS12 keystore
    * AS2 client uses predefined names for the following items:
        - aliasname must match to the certificate CN (Common Name)
        - key store password shall be 'peppol'
        - keystore filename shall be 'test-client-certs.p12' to work with [as2-peppol-client](https://github.com/phax/as2-peppol-client)

For example:
```shell
    $ openssl req -out teho3-certificate.csr -new -newkey rsa:2048 -nodes -keyout teho3-private.key
    $ openssl x509 -signkey teho3-private.key -in teho3-certificate.csr -req -days 365 -out teho3.cer
    $ openssl pkcs12 -export -in teho3.cer -inkey teho3-private.key -out test-client-certs.p12 -passout pass:peppol -name APP_1000000112
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
