# spring-oauth2-google
Sample project code for Spring Boot OAuth2 Login with Google
### Follow our written tutorial here: [Spring Boot OAuth2 Login with Google Example](https://www.codejava.net/frameworks/spring-boot/oauth2-login-with-google-example)
### Watch coding in action on YouTube: [Spring Boot OAuth2 Social Login with Google Example](https://www.youtube.com/watch?v=lmS0hX5F_QQ)
## Learn Full-stack Development with Java and Spring Boot:
### [Spring Boot E-Commerce Ultimate Course](https://www.udemy.com/course/spring-boot-e-commerce-ultimate/?referralCode=3A24FAC7220029CEDFD6)


RUN TEST do decode password "admin" ->  so can u can stored pass


curl --location 'http://localhost:9001/api/v1/test' \
--header 'Cookie: JSESSIONID=8E49CEE533A01A032E61BB61CADB4105; JSESSIONID=BEEB43E2D1E848CC7B9EE49A65709B81'

Redirect link http://localhost:9001/login/oauth2/code/google



POST 'http://localhost:9001/api/auth/token'

BODY

{"token":"abcxyz", "provider":"facebook"}

{"token":"abcxyz", "provider":"google"}
