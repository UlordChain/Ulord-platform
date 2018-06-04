# Content audit service server
A spring cloud micro-service module for content audit client service. 
Which can support service to enterprise application.

## Content auth
### Sensitive words client, and content auth service
Sensitive words is bound to Ulord content distributed ecosystem. Current service 
connect to Ulord content auth service to get and sync sensitive words. 
 The new keyword will be update to all Ulord ecosystem.

```
               +-----------------------------+
               | Ulord Content audit Service |
               +-----------------------------+
                   /                   \       ....
   +----------------------------+    +----------------------------+
   | Ulord audit client service |    | Ulord audit client service |
   +----------------------------+    +----------------------------+
                 |                                 |
   +----------------------------+    +----------------------------+
   |   Enterprise application   |    |   Enterprise application   |
   +----------------------------+    +----------------------------+
```

### Service port and work method
Content auth client service content to audit service port, which can change in configuration item:upaas.uauth.server.port.
We connect to auth service using Netty framework.

RESTful API listen 8063 default, which can change in bootrstrap.yml configuration file. or can change in command line
by using:

```
java -Dspring.profiles.active=dev -Dserver.port=2000 -jar content-uauth-client-x.x.x.jar
```

### Auth API

#### POST /contentauth
- Input:(application/json Body)

```
{
  "format":"TEXT",    // TEXT, HTML, Content format
  "content":"",          // Optional, Keyword level
}
```

- Output: (application/json)

```
{
    "errorcode": 0,
    "reason": null,
    "result": {
        "keywords": [
            {
                "keyword": "sex",
                "level": 1
            },
            {
                "keyword": "blackman",
                "level": 2
            }
        ],
        "violateCount": 2
    }
```

