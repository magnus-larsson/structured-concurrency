# Virtual Threads

Created using: https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0-M3&packaging=jar&jvmVersion=22&groupId=ml.virtualthreads&artifactId=structuredconcurrency&name=structuredconcurrency&description=Demo%20project%20for%20Spring%20Boot%20with%20Virtual%20Threads%20and%20Structured%20Concurrency&packageName=ml.virtualthreads.structuredconcurrency


Set Java version:

Linux/macOS:

```
sdk use java 21-tem
sdk use java 24-tem
```

Widows PowerShell:

```
$env:JAVA_HOME = $env:JAVA_HOME_21
$env:JAVA_HOME = $env:JAVA_HOME_24

$env:PATH = "$env:JAVA_HOME/bin;$env:PATH"

echo $env:PATH
echo $env:JAVA_HOME
java -version
```

Compile:

```
./gradlew build 
```

Run backend services:

```
java --enable-preview -D"server.port=7070" -jar backend/build/libs/backend-0.0.1-SNAPSHOT.jar  
```

Run frontend services:

```
java --enable-preview -jar frontend/build/libs/frontend-0.0.1-SNAPSHOT.jar  
```

OK Test:

```
curl http://localhost:8080/composite/ok
```

Fail Test:

```
curl http://localhost:8080/composite/fail
```

Test httpbin:

```
curl http://localhost:8080/httpbin/test
```

E2E tests:

```
./gradlew bootBuildImage && docker compose up -d && docker compose logs -f --tail=0 

cd e2e-tests
./gradlew clean test --tests structuredconcurrency.SCRunner
open build/karate-reports/karate-summary.html
cd ..

docker compose down
```

# Containers

```
cd backend
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=sc-backend

cd ..

cd frontend
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=sc-frontend
```
