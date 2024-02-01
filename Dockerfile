FROM tomcat:10.1.18-jre21
RUN cp -r $CATALINA_HOME/webapps.dist/* $CATALINA_HOME/webapps
ADD /target/GetWordyAdmin.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
