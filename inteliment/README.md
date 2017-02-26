### Introduction
This application implements two REST API's which parses the input file and provides the ouput in the requested format.
Some points of note regarding this application:
* It is a Spring Boot application
* HTTP Basic authentication implemented using Spring Security
* spring-boot-devtools used for improved developer productivity
* Unit tests are written using SpringBootTest

### Please Note
1. The input file is located at => **src/main/resources/sample.txt**
The location is hardcoded into the app. 
To change the input file, modify/replace the file content.

2. User **"user"** and password **"password"** has been hardcoded in *SecurityConfig.java*
Using curl, the credentials can be passed using either of the below approaches:
```
(1) -u user:password
(2) -H "Authorization: Basic dXNlcjpwYXNzd29yZA=="
```

### Compilation
```
# mvn compile
```

### Test execution
```
# mvn test
```

### Generating Executable Jar
```
# mvn package
```

### Running the app
```
# mvn spring-boot:run
# java -jar target/counter-0.0.1-SNAPSHOT.jar  (Generate jar before this step)
```

### APIs

* Find number of occurrences for the given strings and provide output in JSON format.
Note that the search performed is case-insensitive.
```
curl http://localhost:8080/counter-api/search -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -d '{"searchText":["Duis", "Sed", "Donec", "Augue", "Pellentesque", "123"]}' -H "Content-Type: application/json" –X POST
```
   (or)
```
curl http://localhost:8080/counter-api/search -u user:password -d '{"searchText":["Duis", "Sed", "Donec", "Augue", "Pellentesque", "123"]}' -H "Content-Type: application/json" –X POST
```

* Find the top N occurring words and provide output in CSV format
Note that the search performed is case-insensitive.
```
curl http://localhost:8080/counter-api/top/20 -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -H "Accept: text/csv"
```
   (or)
```
curl http://localhost:8080/counter-api/top/20 -u user:password -H "Accept: text/csv"
```

### Incorrect API Calls
Making incorrect API calls will return appropriate HTTP error codes.

* Making API call with no credentials returns "401 Unauthorized"
Example:
```
curl http://localhost:8080/counter-api/top/20 -H "Accept: text/csv, application/json" -v
```

* Making API call with wrong credentials returns "401 Bad Credentials"
Example:
```
curl http://localhost:8080/counter-api/top/20 -H "Authorization: Basic blahblah" -H "Accept: text/csv,application/json" -v
```

* Making API call with wrong method returns "405 Method not allowed"
Example:
```
curl http://localhost:8080/counter-api/top/20 -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -H "Accept: text/csv,application/json" -v
```

* Making API call with wrong Accept header returns "406 Not Acceptable"
Example:
```
curl http://localhost:8080/counter-api/top/20 -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -H "Accept: application/json" -v
```

* Making API call with wrong URL returns "404 Not Found"
Example:
```
curl http://localhost:8080/counter-api/blah -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -H "Accept: text/csv,application/json" -v
```

* Making API call with incorrect input returns "400 Bad Request"
Example:
```
curl http://localhost:8080/counter-api/top/-1 -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -H "Accept: text/csv,application/json" -v
curl http://localhost:8080/counter-api/search -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" -d '{["Dui}' -H "Content-Type: application/json" -v
```
