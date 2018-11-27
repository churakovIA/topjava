### MealsGetAll
`curl -X GET -H "Content-Type: application/json" "http://localhost:8080/topjava/rest/meals"`
### MealsGetBetween
`curl -X GET -H "Content-Type: application/json" "http://localhost:8080/topjava/rest/meals/filter?startDate=2015-05-30&endDate=2015-05-30&startTime=13:00&endTime=13:00"`
### MealsGetBetweenDateTime
`curl -X GET -H "Content-Type: application/json" "http://localhost:8080/topjava/rest/meals/filterDateTime?start=2015-05-30T00:00:00&end=2015-05-30T23:59:59"`
### MealsGet
`curl -X GET -H "Content-Type: application/json" "http://localhost:8080/topjava/rest/meals/100002"`
### MealsCreate
`curl -X PUT -d "[{\"id\":100003,\"dateTime\":\"2015-05-30T10:00:01\",\"description\":\"New\",\"calories\":200}]" -H "Content-Type: application/json" "http://localhost:8080/topjava/rest/meals/100003"`
### MealsDelete
`curl -X DELETE http://localhost:8080/topjava/rest/meals/100005`
### MealsUpdate
`curl -H "Content-Type: application/json" -X POST -d "{\"dateTime\":\"2015-06-01T19:00:01\",\"description\":\"dinner33\",\"calories\":333}" http://localhost:8080/topjava/rest/meals`
