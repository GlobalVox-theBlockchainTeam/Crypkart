FROM tomcat:8.5.24-jre8
ADD build/libs/GDL.war /usr/local/tomcat/webapps/
