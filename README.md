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
