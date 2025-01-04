package tests;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojo.User;
import uitilities.Utils;
import utilities.ApiClient;
import utilities.ConfigurationReader;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

public class UserTests extends BaseTest {

    ApiClient apiClient = new ApiClient();
    User user = new User();
    Utils methods = new Utils();

    @Test
    public void createUserVerifyGet() {
        String username = ConfigurationReader.get("username");
        String password = ConfigurationReader.get("password");
        String email = ConfigurationReader.get("email");
        String phone = ConfigurationReader.get("phone");
        String firstName = ConfigurationReader.get("firstName");
        String lastName = ConfigurationReader.get("lastName");
        int userStatus = Integer.parseInt(ConfigurationReader.get("userStatus"));
        int id = Integer.parseInt(ConfigurationReader.get("id"));

        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setUserStatus(userStatus);

        Response response = apiClient.post( "/user", user);

        assertEquals(response.getStatusCode(), 200, "User should be created successfully with 200 OK!");

        response.body().prettyPrint();

        response = apiClient.get("/user/" + user.getUsername());
        response.body().prettyPrint();
        assertEquals(response.getStatusCode(), 200, "User should be retrievable!");

        assertEquals(response.getBody().path("username"), username, "Username should be the same!");
        assertEquals(response.getBody().path("firstName"), firstName, "First name should be the same!");
        assertEquals(response.getBody().path("lastName"), lastName, "Last name should be the same!");
        assertEquals(response.getBody().path("email"), email, "Email should be the same!");
        assertEquals(response.getBody().path("phone"), phone, "Phone should be the same!");
        assertEquals((int) response.getBody().path("userStatus"), userStatus, "User status should be the same!");
        assertEquals((int) response.getBody().path("id"), id, "User ID should be the same!");
        assertEquals(response.getBody().path("password"), password, "Password should be the same!");

    }

    @Test
    public void createUserNegativeScenario() {
        String empty="";

        Response response = apiClient.post("/user", empty);

        assertEquals(response.getStatusCode(), 405, "Invalid user should not be created!");
    }

    @Test
    public void getUserByUsername() {//---
        createUserVerifyGet();
        Response response = apiClient.get("/user/" + ConfigurationReader.get("username"));

        response.body().prettyPrint();
        assertEquals(response.getStatusCode(), 200, "User retrieval failed!");
        assertTrue(response.body().asString().contains(ConfigurationReader.get("username")));
        assertTrue(response.body().asString().contains(ConfigurationReader.get("firstName")));
        assertTrue(response.body().asString().contains(ConfigurationReader.get("lastName")));

    }

    @Test
    public void getUserByUsernameUserNotFound() {
        String invalidUsername = "nonUserX";
        Response response = apiClient.get("/user/" + invalidUsername);

        assertEquals(response.getStatusCode(), 404, "Non-existent user should return 404!");
        assertEquals(response.getBody().path("message"), "User not found", "'User not found' message!");
    }

//    @Test
//    public void getUserByUsername_NegativeScenario_InvalidUsernameSupplied() {//****404
//        Map<String, Object> queryParams = new HashMap<>();
//        queryParams.put("username", "-*/."); // Boş username
//
//        Response response = apiClient.gettttttt("/user", queryParams);
//
//        assertEquals(response.getStatusCode(), 400, "Invalid username format should return 400!");
//    }

    @Test
    public void loginUser(){
        String username = ConfigurationReader.get("username");
        String password = ConfigurationReader.get("password");

        Response response = apiClient.get("/user/login?usrname=" + username + "&password=" + password);
        assertEquals(response.getStatusCode(), 200, "Login should be successful!");
    }

//    @Test
//    public void loginUser_NegativeScenario_InvalidCredentials() {400 dönmedi
//        int username = 5;
//        int password = 55;
//        Map<String, Object> data = new HashMap<>();
//        data.put("username", "*");
//        data.put("password", "10");
//
//        Response response = apiClient.getttttttttt("/user/login", data);
//
//        response.body().prettyPrint();
//        assertEquals(response.getStatusCode(), 400, "Invalid credentials should return 400 Bad Request!");
//
//        String responseBody = response.getBody().asString();
//        System.out.println("Response: " + responseBody);
//    }

    @Test(dependsOnMethods = {"loginUser"})
    public void logoutUserValidRequest() {
        Response response = apiClient.get("/user/logout");

        assertEquals(response.getStatusCode(), 200, "Logout should return 200 OK!");
    }

    @Test
    public void createUsersWithArrayInputValidRequest() {
        List<User> users = Arrays.asList(
                new User(1, "suatList1", "suat1", "aydin1", "suat1@example.com", "suat111", "1234567890", 1),
                new User(2, "suatList2", "suat2", "aydin2", "suat2@example.com", "suat222", "0987654321", 1)
        );

        Response response = apiClient.post("/user/createWithArray", users);

        response.body().prettyPrint();

        assertEquals(response.getStatusCode(), 200, "Users should be created successfully with 200 Created!");

        User suatList1 = methods.getUser("suatList1");

        assertEquals(suatList1.getId(), 1, "id should be the same!");
        assertEquals(suatList1.getUsername(), "suatList1", "Username should be the same!");
        assertEquals(suatList1.getFirstName(), "suat1", "First name should be the same!");
        assertEquals(suatList1.getLastName(), "aydin1", "Last name should be the same!");
        assertEquals(suatList1.getEmail(), "suat1@example.com", "Email should be the same!");
        assertEquals(suatList1.getPhone(), "1234567890", "Phone should be the same!");
        assertEquals(suatList1.getUserStatus(), 1, "User status should be the same!");

        User suatList2 = methods.getUser("suatList2");

        assertEquals(suatList2.getId(), 2, "id should be the same!");
        assertEquals(suatList2.getUsername(), "suatList2", "Username should be the same!");
        assertEquals(suatList2.getFirstName(), "suat2", "First name should be the same!");
        assertEquals(suatList2.getLastName(), "aydin2", "Last name should be the same!");
        assertEquals(suatList2.getEmail(), "suat2@example.com", "Email should be the same!");
        assertEquals(suatList2.getPhone(), "0987654321", "Phone should be the same!");
        assertEquals(suatList2.getUserStatus(), 1, "User status should be the same!");

        assertNotEquals(suatList1.getId(), suatList2.getId(), "IDs should be different!");
        assertNotEquals(suatList1.getUsername(), suatList2.getUsername(), "Usernames should be different!");

    }

    @Test(dependsOnMethods = {"createUserVerifyGet"})
    public void updateValidUser() {
        user.setId(Integer.parseInt(ConfigurationReader.get("id")));
        user.setUsername(ConfigurationReader.get("username"));
        user.setFirstName("UpdatedFirstName");
        user.setLastName("UpdatedLastName");
        user.setEmail("updated.email@example.com");
        user.setPassword("newPassword123");
        user.setPhone("1234567890");
        user.setUserStatus(0);

        Response response = apiClient.put("/user/" + user.getUsername(), user);

        response.body().prettyPrint();
        assertEquals(response.getStatusCode(), 200, "User should be updated successfully!");

        Response getUserResponse = apiClient.get("/user/" + user.getUsername());
        assertEquals(getUserResponse.getStatusCode(), 200, "User should be retrievable!");
        getUserResponse.body().prettyPrint();
        assertEquals(getUserResponse.getBody().path("firstName"), user.getFirstName(), "First name should be updated!");
        assertEquals(getUserResponse.getBody().path("lastName"), user.getLastName(), "Last name should be updated!");
        assertEquals(getUserResponse.getBody().path("email"), user.getEmail(), "Email should be updated!");
        assertEquals(getUserResponse.getBody().path("phone"), user.getPhone(), "Phone should be updated!");
        assertEquals(getUserResponse.getBody().path("username"), user.getUsername(), "Username should be the same!");
        assertEquals((int) getUserResponse.getBody().path("userStatus"), user.getUserStatus(), "User status should be the same!");
        assertEquals((int) getUserResponse.getBody().path("id"), user.getId(), "User ID should be the same!");
        assertEquals(getUserResponse.getBody().path("password"), user.getPassword(), "Password should be the same!");

        methods.deleteUser(user.getUsername());
    }

    @Test
    public void updatePartUser() {
        String username = "userNameSuat";
        methods.createUser(username);
        user.setUsername("testUpdated");
        user.setFirstName("UpdatedFirstName");

        Response response = apiClient.put("/user/" + username, user);

        response.body().prettyPrint();
        assertEquals(response.getStatusCode(), 200, "User should be updated successfully!");

        Response getUserResponse = apiClient.get("/user/" + user.getUsername());
        assertEquals(getUserResponse.getStatusCode(), 200, "User should be retrievable!");
        getUserResponse.body().prettyPrint();

        assertEquals(getUserResponse.getBody().path("username"), user.getUsername(), "Username should be the same!");

        methods.deleteUser(user.getUsername());
    }

//    @Test
//    public void updateUser_NegativeScenario_UserNotFound() {
//        user.setUsername("a12121212121212");
//        user.setFirstName("testtest");
//        user.setLastName("User");
//        user.setId(2);
//        user.setPhone("sddsasdasdaasd");
//
//        System.out.println("user.getUsername() = " + user.getUsername());
//        Response response = apiClient.put("/user/" + user.getUsername(), "useyhbugıur");400 dönüyor
//        response.body().prettyPrint();
//        assertEquals(response.getStatusCode(), 404, "Updating a non-existing user should return 404 Not Found!");
//    }

    @Test
    public void updateUserNegativeScenarioInvalidUsername() {
        String id = "invalid";

        Response response = apiClient.put("/user/" + user.getUsername(), id);

        response.body().prettyPrint();
        assertEquals(response.getStatusCode(), 400, "Invalid username format should return 400 Bad Request!");
    }

    @Test(dependsOnMethods = {"createUserVerifyGet"})
    public void deleteUserAndVerify() {
        Response deleteResponse = apiClient.delete("/user/" + ConfigurationReader.get("username"));
        assertEquals(deleteResponse.getStatusCode(), 200, "User should be deleted successfully!");

        Response getUserResponse = apiClient.get("/user/" + ConfigurationReader.get("username"));
        assertEquals(getUserResponse.getStatusCode(), 404, "Deleted user should not be found!");
    }

    @Test
    public void deleteUserNegativeScenarioUserNotFound() {
        String invalidUsername = "nonUserX";
        Response response = apiClient.delete("/user/" + invalidUsername);

        assertEquals(response.getStatusCode(), 404, "Deleting a non-existing user should return 404 Not Found!");
    }







}

