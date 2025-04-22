package ru.netology.testmode.data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private DataGenerator() {
    }

    @Value
    public static class User {
        String login;
        String password;
        String status;
    }

    public static String generateInvalidLogin(User validUser) {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            String login = faker.name().username();
            if (!login.equals(validUser.getLogin())) {
                return login;
            }
        }
        throw new RuntimeException("Не удалось сгенерировать уникальный логин за 10 попыток");
    }

    public static String generateInvalidPassword(User validUser) {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            String password = faker.internet().password();
            if (!password.equals(validUser.getPassword())) {
                return password;
            }
        }
        throw new RuntimeException("Не удалось сгенерировать уникальный пароль за 10 попыток");
    }


    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(io.restassured.filter.log.LogDetail.ALL)
            .build();

    public static User generateUser(String status) {
        Faker faker = new Faker(new Locale("ru"));
        String login = faker.name().username();
        String password = faker.internet().password();

        User user = new User(login, password, status);

        given()
                .spec(requestSpec)
                .body(new Gson().toJson(user))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);

        return user;
    }
}