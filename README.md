# java-timezone
Display timezone information in JDK

This application will expose the following API to query timezone information in current JDK.
It is using Springboot 2.0 Flux to implement HTTP REST API.

The following CURL command will display timezone information in current JDK.
```
curl http://localhost:8080/timezone
```

The following CURL will return all availabe Java timezone ID in current JDK
```
curl http://localhost:8080/timezoneIds
```
