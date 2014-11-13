#as2-peppol-server

An example AS2 server to easily receive AS2 messages from PEPPOL.
This project is only meant as a Demo project for illustrative purposes, on how to
implement a PEPPOL AS2 server. This server discards all incoming documents!

This AS2 server is based on my [as2-lib](https://github.com/phax/as2-lib) library, as well as on
[as2-peppol-servlet](https://github.com/phax/as2-peppol-servlet), 
[peppol-sbdh](https://github.com/phax/peppol-sbdh) and [ph-ubl](https://github.com/phax/ph-ubl).

#Project structure
This project is a Java 1.6+ web application and as such meant to be used in an application
server (like Tomcat, Jetty etc.).

It consists of the following major components:
  * A specialized servlet for retrieval: `com.helger.peppol.as2server.servlet.MyAS2PeppolReceiveServlet` which defines the path to the AS2 server configuration file. The default path is `as2-server-data/as2-server-config.xml`. The servlet is referenced from `WEB-INF/web.xml`.
  * The main handler for an incoming SBDH document in class `com.helger.peppol.as2server.handler.AS2IncomingSBDHandler`. This class is referenced via the default SPI lookup mechanism (see `META-INF/services/`).
  * Additionally a configuration file `as2-server-data\as2-server-config.xml` is provided that defines the AS2 specific setup. The details on the configuration file can be found at the [as2-peppol-servlet](https://github.com/phax/as2-peppol-servlet) project.
  
#Before you start
Before this project can be run in a useful way a PKCS12 keystore with your PEPPOL AP certificate must be provided. By default the keystore must be located in `as2-server-data\server-certs.p12` and must have the password `peppol`. To change this edit the `<certificates>` element in the `as2-server-config.xml` file.

