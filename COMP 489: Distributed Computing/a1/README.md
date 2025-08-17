# COMP489: Distributed Computing - Assignment 1

# Test Plan

by Alex Perrin

April 16, 2025

## Testing Environment

For my development environment, I’m using Ubuntu 24.04.2 LTS running in WSL on Windows 11.

```bash
alex@alex-desktop:~$ cat /etc/lsb-release
DISTRIB_ID=Ubuntu
DISTRIB_RELEASE=24.04
DISTRIB_CODENAME=noble
DISTRIB_DESCRIPTION="Ubuntu 24.04.2 LTS"
```

Following https://ubuntu.com/tutorials/install-jre to install the Java Runtime Environment (JRE), as well as the Install the Java Development Kit (JDK)

```bash
alex@alex-desktop:~$ sudo apt install default-jre default-jdk
```

Installs the OpenJDK JRE 21.0.6 `java` runtime…

```bash
alex@alex-desktop:~$ java --versionopenjdk 21.0.6 2025-01-21
OpenJDK Runtime Environment (build 21.0.6+7-Ubuntu-124.04.1)OpenJDK 64-Bit Server VM (build 21.0.6+7-Ubuntu-124.04.1, mixed mode, sharing)
```

and `javac` 21.0.6 Java compiler.

```bash
alex@alex-desktop:~/comp489-a1$ javac --version
javac 21.0.6
```

All assignment program files are located in the `comp489-a1` directory.

```bash
alex@alex-desktop:~$ cd comp489-a1
alex@alex-desktop:~/comp489-a1$
```

## Testing Procedure

Compile all java files in the working directory using wildcard glob.

```bash
alex@alex-desktop:~/comp489-a1$ javac *.java
```

In seperate shells, start WebServer, Proxy, and Client Java programs.

**WebServer**

```bash
alex@alex-desktop:~/comp489-a1$ java WebServer
Web Server is listening on port 8080
```

**Proxy**

```bash
alex@alex-desktop:~/comp489-a1$ java Proxy
Proxy Server is listening on port 8081
```

**Client**

```bash
alex@alex-desktop:~/comp489-a1$ java Client
Enter URL:
```

## Test Results

| Test Case | Client Input (STDIN) | Client Output (STDOUT) | Proxy Output (STDOUT) | WebServer Output (STDOUT) | Notes |
| --- | --- | --- | --- | --- | --- |
| 1 | Enter URL: http://localhost/testfile.html | `HTTP/1.1 200 OK
Content-Type: text/html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Test HTML File</title>
</head>
    <body><h1>Hello, testfile.html</h1>
</body>
</html>` | `Client connected
GET http://localhost/testfile.html HTTP/1.1
Forwarding to WebServer` | `Client connected
GET /testfile.html HTTP/1.1
HTTP/1.1 200 OK
Client disconnected` | testfile.html request successful. |
| 2 | Enter URL: http://localhost/testfile.jpg | `HTTP/1.1 200 OK
Content-Type: image/jpeg

����JFIF``��:ExifMMQQQ��C` | `Client connected
GET http://localhost/testfile.jpg HTTP/1.1
Forwarding to WebServer` | `Client connected
GET /testfile.jpg HTTP/1.1
HTTP/1.1 200 OK
Client disconnected` | testfile.html request successful. |
| 3 | Enter URL: http://localhost/ | `HTTP/1.1 404 Not Found` | `Client connected
GET http://localhost/ HTTP/1.1
Forwarding to WebServer` | `Client connected
GET / HTTP/1.1
HTTP/1.1 404 Not Found
Client disconnected` | Root path not served. |
| 4 | Enter URL: *(empty line)* | `Error: URL cannot be empty.` | *(none)* | *(none)* | Basic input validation. |
| 5 | Enter URL: htp:/wrong | `Error: Malformed URL.` | *(none)* | *(none)* | Basic input validation. |
| 6 | Enter URL: http://google.com | `HTTP/1.1 200 OK`
*(or another 2xx/3xx/4xx depending on response)* | `Client connected
GET http://google.com HTTP/1.1
Forwarding to internet` | *(none)* | Proxy handles requests to external hosts successfully. |
| 7 | Enter URL: [ftp://ftp.us.dell.com/readme.txt](ftp://ftp.us.dell.com/readme.txt) | *(none)* | `java.lang.IllegalArgumentException: invalid URI scheme ftp` | *(none)* | Client will send the request to Proxy, but Proxy cannot handle FTP requests and will throw `IllegalArgumentException`. |

### Testing WebServer with curl

| Test Case | Shell Input Command | Shell Output (STDOUT) | Notes |
| --- | --- | --- | --- |
| 8 | curl -X POST http://localhost:8080 -v | `* Host localhost:8080 was resolved.
* IPv6: ::1
* IPv4: 127.0.0.1
*   Trying [::1]:8080...
* Connected to localhost (::1) port 8080
> POST / HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/8.5.0
> Accept: */*
>
< HTTP/1.1 400 Bad Request` | WebServer does not support HTTP POST requests, returns a 400 Bad Request error. |

### Testing Webserver basic functionality with internet browser.

![testplan-browser-html.png](testplan-browser-html.png)

![testplan-browser-jpeg.png](testplan-browser-jpeg.png)