command BUILD:
mvn clean package

RUN TEST do decode password "admin" ->  so can u can stored pass


curl --location 'http://localhost:9001/api/v1/test' \
--header 'Cookie: JSESSIONID=BEEB43E2D1E848CC7B9EE49A65709B81'

Redirect link http://localhost:9001/login/oauth2/code/google



POST 'http://localhost:9001/api/auth/token'

BODY

{"token":"abcxyz", "provider":"facebook"}

{"token":"abcxyz", "provider":"google"}




BODY POST "http://localhost:9001/api/v1/availabilities"

[
{
"dayOfWeek": 1,
"shiftNumber": 1,
"startTime": "10:00",
"endTime": "12:00"
},
{
"dayOfWeek": 1,
"shiftNumber": 2,
"startTime": "17:30",
"endTime": "21:00"
},
{
"dayOfWeek": 2,
"shiftNumber": 1,
"startTime": "10:00",
"endTime": "12:00"
},
{
"dayOfWeek": 2,
"shiftNumber": 2,
"startTime": "17:30",
"endTime": "21:00"
}
]