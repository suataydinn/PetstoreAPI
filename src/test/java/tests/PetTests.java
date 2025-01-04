package tests;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Category;
import pojo.Pet;
import pojo.Tag;
import uitilities.Utils;
import utilities.ApiClient;

import java.io.File;
import java.util.Collections;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class PetTests extends BaseTest {

    ApiClient apiClient = new ApiClient();
    Utils methods = new Utils();
    Pet pet = new Pet();


    private static int petId;

    @Test
    public void addNewPet() {

        pet.setId(1);
        pet.setName("doggie");
        pet.setCategory(new Category(0, "hawk"));
        pet.setPhotoUrls(Collections.singletonList("string"));
        pet.setTags(Collections.singletonList(new Tag(0, "string")));
        pet.setStatus("sold");

        Response response = apiClient.post("/pet", pet);

        assertEquals(200, response.getStatusCode());
        response.body().prettyPrint();

        petId = response.getBody().path("id");
        assertTrue(response.body().asString().contains(String.valueOf(petId)));
        Assert.assertEquals(response.getBody().path("category.name"), pet.getCategory().getName());
        Assert.assertEquals(response.getBody().path("name"), pet.getName());
        Assert.assertEquals(response.getBody().path("status"), pet.getStatus());
    }

    @Test
    public void addNewPetInvalidData() {
        String id = "invalid data";

        Response response = apiClient.post("/pet", id);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void GetPetById() {
        petId=methods.getIdInteger("available").get(0);
        Response response = apiClient.get("/pet/" + petId);

        assertEquals(200, response.getStatusCode());
        response.body().prettyPrint();
        Assert.assertEquals((int) response.getBody().path("id"), petId);
    }

    @Test
    public void GetPetInvalidId() {
        int invalidPetId = 00000000000;
        Response response = apiClient.get("/pet/" + invalidPetId);

        assertEquals(404, response.getStatusCode());
        Assert.assertTrue(response.body().asString().contains("Pet not found"), "Error message does not match");
    }

    @Test
    public void putUpdatePet() {
        petId=methods.getIdInteger("available").get(0);
        pet.setName("updateDoggie");
        pet.setCategory(new Category(0, "haw"));
        pet.setPhotoUrls(Collections.singletonList("hawUrl"));
        pet.setTags(Collections.singletonList(new Tag(0, "hawTag")));
        pet.setStatus("sold");
        pet.setId(2);

        Response response = apiClient.put("/pet", pet);
        assertEquals(200, response.getStatusCode());

        Assert.assertEquals(response.getBody().path("category.name"), pet.getCategory().getName());
        Assert.assertEquals(response.getBody().path("name"), pet.getName());
        Assert.assertEquals(response.getBody().path("status"), pet.getStatus());
    }

    @Test
    public void negativePutUpdatePet() {
        petId=methods.getIdInteger("available").get(0);
        pet.setName("updateDoggie");
        pet.setCategory(new Category(0, "haw"));
        pet.setPhotoUrls(Collections.singletonList("hawUrl"));
        pet.setTags(Collections.singletonList(new Tag(0, "hawTag")));
        pet.setStatus("sold");

        Response response = apiClient.put("/pet/" + petId, pet);
        assertEquals(405, response.getStatusCode());

    }

    @Test
    public void negativePutUpdatePet400() {
        pet.setId(methods.getIdInteger("available").get(0));
        pet.setName(" .. ");
        pet.setCategory(new Category(0, "haw"));
        pet.setPhotoUrls(Collections.singletonList("hawUrl"));
        pet.setTags(Collections.singletonList(new Tag(0, "hawTag")));
        pet.setStatus(" ..");

        Response response = apiClient.put("/pet", pet);
        assertEquals(404, response.getStatusCode());
    }


    @Test
    public void PostUpdatePet(){


        petId=methods.getIdInteger("available").get(0);
        String petName = "Buddy";
        String petStatus = "available";
        String petJson = "{\n" +
                "  \"id\": " + petId + ",\n" +
                "  \"name\": \"" + petName + "\",\n" +
                "  \"category\": {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"string\"\n" +
                "  },\n" +
                "  \"photoUrls\": [\"https://example.com/pet.jpg\"],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"" + petStatus + "\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri("https://petstore.swagger.io/v2")
                .basePath("/pet")
                .contentType(ContentType.JSON)  // JSON formatında veri gönderiyoruz
                .body(petJson)  // Pet verisini JSON formatında gönderiyoruz
                .put();  // PUT isteği gönderiyoruz

        assertEquals(200, response.getStatusCode());
        assertEquals(petName, response.getBody().path("name"));
        assertEquals(petStatus, response.getBody().path("status"));

    }

    @Test
    public void deletePet() {
        petId=methods.getIdInteger("available").get(0);
        Response response = apiClient.delete("/pet/" + petId);

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void deletePetWithInvalidId() {
        int invalidPetId = 000000000000;
        Response response = apiClient.delete("/pet/" + invalidPetId);

        response.body().prettyPrint();
        Assert.assertEquals(404, response.getStatusCode(), "Expected 404 Not Found status");
    }

    @Test
    public void testInvalidHttpMethodOnPetEndpoint() {
        Response response = apiClient.delete("/pet");

        Assert.assertEquals(405, response.getStatusCode(), "Expected 405 Method Not Allowed status");
    }

    @Test
    public void uploadImage() {

        petId=methods.getIdInteger("available").get(0);
        File imageFile = new File("src/main/resources/test-image.png");

        Response response = apiClient.postWithFile("/pet/" + petId + "/uploadImage", imageFile);

        response.prettyPrint();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testFindByStatus() {
        methods.verifyStatus("available");
        methods.verifyStatus("pending");
        methods.verifyStatus("sold");
    }

    @Test
    public void FindByWithInvalidStatus() {

        String invalidStatus = "invalid status";

        Response response = apiClient.get("/pet/findByStatus?status=" + invalidStatus);

        response.body().prettyPrint();
        Assert.assertNotNull(response.getBody(), "Response body should not be null");
        Assert.assertEquals(response.getBody().asString(), "[]");
    }



}
