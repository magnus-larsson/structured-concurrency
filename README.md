# Virtual Threads

Created using: https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0-M3&packaging=jar&jvmVersion=22&groupId=ml.virtualthreads&artifactId=structuredconcurrency&name=structuredconcurrency&description=Demo%20project%20for%20Spring%20Boot%20with%20Virtual%20Threads%20and%20Structured%20Concurrency&packageName=ml.virtualthreads.structuredconcurrency

Compile:

```
sdk use java 22-tem
./mvnw package 
```

Run backend services:

```
java --enable-preview -Dserver.port=7070 -jar target/structuredconcurrency-backend-0.0.1-SNAPSHOT.jar
```

Run frontend services:

```
java --enable-preview -jar target/structuredconcurrency-0.0.1-SNAPSHOT.jar
```

OK Test:

```
time curl http://localhost:8080/composite/ok
```

Fail Test:

```
time curl http://localhost:8080/composite/fail
```

Test httpbin:

```
curl http://localhost:8080/httpbin/test
```

# Java post-v8 syntax

JEP 459: String Templates (Second Preview)

```
System.out.println(STR."REQUEST body to httpbin: \{article.articleId} & \{article.title}");
```