System for faculty admission

In order to easily configure the external services (database and S3), go to the root of the project and run `docker-compose up -d`.

Swagger: http://localhost:8080/v3/api-docs  
Swagger UI: http://localhost:8080/swagger-ui/index.html

I. Business requirements:

1. A student can create an account and authenticate
2. A student can submit the required personal info and documents
3. A student can see details about all programs of study
4. A student can submit the admission file to be validated
5. A student can apply to programs of study
6. A student can see his own grades
7. An admin can see details about all programs of study/students/admission files/grades
8. An admin can validate/invalidate an admission file
9. An admin can manage the programs of study
10. An admin can submit grades to students

II. Main features:

1. Management of accounts based on roles
2. Management of personal info and documents
3. Management of programs of study
4. Management of admission files
5. Management of grades


