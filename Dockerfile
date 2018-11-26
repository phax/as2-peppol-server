FROM maven:3.5.2-jdk-8-alpine
WORKDIR /as2-peppol-server
COPY pom.xml /as2-peppol-server/pom.xml
COPY src /as2-peppol-server/src
VOLUME [ "/var/www/peppol-as2/receive", "/var/www/peppol-as2/send", "/var/www/peppol-as2/data" ]
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

# building step
WORKDIR /as2-peppol-server
# NOTE: in maven Docker image default Maven repository location is configured as a volume 
# so anything copied there in a Dockerfile at build time is lost in /root/.m2/repository
# To preserve the project dependecies in the image we use special Maven settings
# for details see https://hub.docker.com/_/maven/
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package

RUN mvn jetty:deploy-war
#NOTE : please refer to jetty version in './pom.xml'
RUN ln -s /root/.m2/repository/org/eclipse/jetty/jetty-runner/9.4.14.v20181114/jetty-runner-9.4.14.v20181114.jar jetty-runner.jar

CMD ["java", "-jar", "jetty-runner.jar", "./target/as2-peppol-server-1.0.0-SNAPSHOT.war"]
