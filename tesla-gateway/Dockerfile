FROM dockerfile/ubuntu
ENV TZ="Asia/Shanghai"
ENV LANG C.UTF-8
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer
ADD target/saluki-example-client-1.5.2-SNAPSHOT.jar /root/app.jar
ADD bin/ /root/
RUN chmod +x /root/*.sh;mkdir /root/logs
ENV JAVA_OPTS ""
ENV APP_NAME saluki-example-consumer
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
WORKDIR /root
CMD ["./start.sh"]





