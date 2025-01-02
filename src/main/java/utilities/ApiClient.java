package utilities;

import com.mongodb.util.JSON;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ApiClient {

    public Response get(String endpoint) {
        return given()
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response postWithString(String endpoint, String body) {
        System.out.println("baseURI = " + baseURI);
        return given()
                .contentType("application/json")
                .log().all()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }


    public Response post(String endpoint, Object body) {
        System.out.println("baseURI = " + baseURI);
        return given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response postJson(String endpoint, String body) {
        System.out.println("baseURI = " + baseURI);
        return given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }


    public Response put(String endpoint, Object body) {
        return given()
                .contentType("application/json")
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response delete(String endpoint) {
        return given()
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response postWithAuth(String endpoint, Object body, String username, String password) {
        return given()
                .contentType("application/json")
                .body(body)
                .auth().basic(username, password)  // Basic auth kullanarak istek gönderiyoruz
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response postWithFile(String endpoint, File file) {
        return given()
                .multiPart("file", file)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

}
