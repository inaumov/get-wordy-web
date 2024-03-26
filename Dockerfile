FROM tomcat:10.1.18-jre21
RUN cp -r $CATALINA_HOME/webapps.dist/* $CATALINA_HOME/webapps
ADD /target/GetWordyApp.war /usr/local/tomcat/webapps/
EXPOSE 8080
ENV POSTGRES_URL=${POSTGRES_URL}
ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
