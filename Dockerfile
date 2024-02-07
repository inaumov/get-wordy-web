FROM tomcat:10.1.18-jre21
RUN cp -r $CATALINA_HOME/webapps.dist/* $CATALINA_HOME/webapps
ADD /target/GetWordyApp.war /usr/local/tomcat/webapps/
EXPOSE 8080
ENV DB_HOST=${DB_HOST}
ENV DB_PORT=${DB_PORT}
ENV DB_SCHEMA=${DB_SCHEMA}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
