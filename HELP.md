# course-catalogue app with SpringBoot 3

Run locally
------------------------
```
$ .\mvnw clean install
$ java -jar .\backend\target\backend-0.1.jar
$ java -jar .\frontend\target\frontend-0.1.jar
```
App is available at http://localhost:9090

User role: login as username: "user" and password: "user" | Admin role: login as username: "admin" and password: "admin"

Run with Docker compose
------------------------
```
$ .\mvnw clean install
$ .\mvnw spring-boot:build-image
$ cd .\deploy\docker-compose\
$ docker compose up
```
App is available at http://localhost:80

Run with Kubernetes
------------------------
```
$ .\mvnw clean install
$ .\mvnw spring-boot:build-image
$ cd .\deploy\kubernetes\
$ kubectl apply -f .\namespace.yaml
$ kubectl apply -f .
$ kubectl get all -n course-catalogue
```
App is available at http://localhost:<NODE_PORT>,
where <NODE_PORT> is the port number obtained by inspecting service/nginx-service from the previous step.


