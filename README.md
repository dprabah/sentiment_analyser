A server wrapper around the Stanford CoreNLP sentiment engine.

### Config

Port can be set in `application.properties`, by default it is set to 8080, this
may cause problems on some machines.

### Deployment

Deployed using maven can be run with the command:

```
mvn spring-boot:run
```

### Features

Server features one endpoint: `/sentiment`. This endpoint will respond to
RestAPI calls 

CURL example:
```
curl -H "Content-Type: application/json" -X 
POST -d '["The icecream taste was very good","the icecream taste was horrible", "the icecream taste was horrible, the cream taste was very good"]' 
http://localhost:8080/sentiment
```
Responds:
```
{
"0":{
    "sentiment":0.7518080377682766,
    "line":"The icecream taste was very good"
},
"1":{
    "sentiment":0.26697011453632014,
    "line":"the icecream taste was horrible"
},
"2":{
    "sentiment":0.32238827758588146,
    "line":"the icecream taste was horrible, the cream taste was very good"
}
}
```