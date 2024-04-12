FROM    clojure:lein-alpine
RUN     mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY    project.clj /usr/src/app/
RUN     lein deps
COPY    . /usr/src/app
RUN     mkdir -p /var/log/bmstu
COPY    resources/*.xml /var/log/bmstu/

# RUN     mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar
# EXPOSE 9501
# CMD java -jar app-standalone.jar
CMD lein dev
