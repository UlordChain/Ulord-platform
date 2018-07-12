# UPaaS Configure Server
A configure server of spring cloud.
Put your application configure into config-repo directory, and commit to git repo, then your application will get new 
configuration. Maybe your application need to restart.

Your application need to:
- Add configure item into bootstrap.yml file:
```
spring.cloud.config.uri: http://localhost:8888/
```

- Add package reference:
```
compile 'org.springframework.cloud:spring-cloud-starter-config'
```


