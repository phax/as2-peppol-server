FROM maven:3.5.2-jdk-8-alpine
WORKDIR /as2-peppol-server
COPY pom.xml /as2-peppol-server/pom.xml
COPY src /as2-peppol-server/src
VOLUME /as2-peppol-server/data
VOLUME [ "/var/www/peppol-as2/receive", "/var/www/peppol-as2/send" ]
EXPOSE 8080

RUN apk update
RUN apk add openssl
RUN apk add openssh

# generating self signing certificate
# follow the [instruction](https://www.digitalocean.com/community/tutorials/openssl-essentials-working-with-ssl-certificates-private-keys-and-csrs)
# **IMPORTANT** 
#     * CN -- Common Name must follow PEPPOL AP name convention and be in format : APP_1000000XXX, for example APP_1000000312
#     * it uses PKCS12 keystore
#     * AS2 client uses predefined names for the following items:
#         - aliasname must match to the certificate CN (Common Name)
#         - key store password shall be 'peppol'
#         - keystore filename shall be 'test-client-certs.p12' to work with [as2-peppol-client](https://github.com/phax/as2-peppol-client)
WORKDIR /as2-peppol-server/src/main/resources/keystore
RUN openssl req \
    -new \
    -newkey rsa:2048 \
    -nodes \
    -subj "/emailAddress=igor@iwa.fi/CN=APP_1000000312/O=IWA.fi/L=Helsinki/ST=Uusimaa/C=FI" \
    -keyout my-private.key \
    -out my-certificate.csr
RUN openssl x509 -signkey my-private.key -in my-certificate.csr -req -days 365 -out my-certificate.cer
RUN openssl pkcs12 -export -in my-certificate.cer -inkey my-private.key \
    -out ap.pilot.p12 -passout pass:peppol -name ap.pilot

# https://www.eclipse.org/jetty/documentation/9.4.x/jetty-maven-plugin.html
WORKDIR /as2-peppol-server
RUN mvn package -Dmaven.test.skip=true
RUN mvn jetty:deploy-war
CMD ["mvn", "jetty:run"]
