# Content audit service server
A spring cloud micro-service module for content audit service.

## Content auth
### Sensitive words server
Sensitive words is bound to Ulord content distributed ecosystem. Current service own a sensitive words database, and
we can manage the records in the database. Add and delete operation on the sensitive words will be synchronized to all 
client service which reside in Ulord enterprise environment. The new keyword will be update to all Ulord ecosystem.

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
Content auth service listen 8070 port default, which can change in configuration item:upaas.uauth.server.port.
We build auth server using Netty framework.

RESTful API listen 8062 default, which can change in bootrstrap.yml configuration file. or can change in command line
by using:

```
java -Dspring.profiles.active=dev -Dserver.port=2000 -jar content-audit-service-x.x.x.jar
```

### Manage API

#### POST /sensitiveword
Add a sensitive word into database.

- Input:(application/json Body)

```
{
  "keyword":"sex",    // Some word we want to check
  "level":1,          // Optional, Keyword level
  "disabled":0        // Optional, 0 for enable, 1 for disabled
}
```

- Output: (application/json)

```
{
  "errorcode":20000,       // Error code, please API Error Code for UPaaS
  "reason":"bala bala...", //
  "result": {}             // Only valid then errorcode = 0
}
```

#### POST /sensitivewords
Add a set of sensitive words into database.

- Input:(application/json Body)

```
[                         // An arrray of keywords
    {
      "keyword":"sex",    // Some word we want to check
      "level":1,          // Optional, Keyword level
      "disabled":0        // Optional, 0 for enable, 1 for disabled
    },
    {}
]
```

- Output: (application/json)

```
{
  "errorcode":20000,       // Error code, please API Error Code for UPaaS
  "reason":"bala bala...", //
  "result": {}             // Only valid then errorcode = 0
}
```

#### DELETE /sensitiveword
Delete a sensitive word from database.

- Input:(Aapplication/json Body)

```
{
  "uid":123           // keyword id in server database
  "keyword":"sex",    // Optional, Some word we want to check
  "level":1,          // Optional, Keyword level
  "disabled":0        // Optional, 0 for enable, 1 for disabled
}
```

- Output: (application/json)

```
{
  "errorcode":20000,       // Error code, please API Error Code for UPaaS
  "reason":"bala bala...", //
  "result": 1              // Only valid then errorcode = 0, indicate operation record count
}
```

#### DELETE /sensitivewords
Delete a set of sensitive words from database.

- Input:(application/json Body)

```
[                         // An arrray of keywords
    {
      "keyword":"sex",    // Some word we want to check
      "level":1,          // Optional, Keyword level
      "disabled":0        // Optional, 0 for enable, 1 for disabled
    },
    {}
]
```

- Output: (application/json)

```
{
  "errorcode":20000,       // Error code, please API Error Code for UPaaS
  "reason":"bala bala...", //
  "result": 1              // Only valid then errorcode = 0, indicate operation record count
}
```

#### GET /sensitiveword
Get all sensitive words from database by page mode

- Input: (application/json Body)

```
{
    "pageNum":1,     // page number, start from 1
    "pageSize":10,   // page size 
    "criteria":1     // Query criteria
}
```
 例如：
 - criteria=abc, query keyword include 'abc' substring
 - criteria=a,b,c, query keyword exactly equal a , b or c
 - criteria=keyword=abc, query keyword include 'abc' substring
 - criteria=username=a,b,c, query keyword exactly equal a , b or c
 - criteria=username=a,b;level=1 query keyword exactly equal a or b, and level like 1, such as 21, 12
 - criteria=username=a,b;level=1,2 query keyword exactly equal a or b, and level exactly equals 1 or 2
 
- Output: (application/json)

```
{
  "errorcode":20000,       // Error code, please API Error Code for UPaaS
  "reason":"bala bala...", //
  "result": [              // Only valid then errorcode = 0, indicate operation record count
    {"keyword":"sex", "level":2},
    {"keyword":"blackman", "level":1},
  ]              
  "totalRecords": 1,
  "pages": 1,
  "pageNum": 1,
  "pageSize": 10,
  "hasMore": false
}
```