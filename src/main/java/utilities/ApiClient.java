package utilities;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

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

    public Response gettt(String endpoint) {
        return given()
                .contentType(ContentType.JSON)  // Content type belirtme
                .accept(ContentType.JSON)       // Accept header ekleme
                .when()
                .get(endpoint)
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

    public Response updatePetWithForm(int petId, String name, String status) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", name)
                .formParam("status", status)
                .when()
                .post("/pet/" + petId)
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
