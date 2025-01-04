package tests;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.*;
import pojo.Order;
import pojo.Store;
import uitilities.Utils;
import utilities.ApiClient;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class StoreTests extends BaseTest {

    ApiClient apiClient = new ApiClient();
    Utils methods = new Utils();
    Store store = new Store();
    Order order = new Order();


    @BeforeMethod(groups = {"needOrderId"})
    public void createOrder() {
        methods.createOrderId(5);
    }

    @Test
    public void getInventory() {
        Response response = apiClient.get("/store/inventory");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().asString().contains("available"));
        assertTrue(response.body().asString().contains("pending"));
        assertTrue(response.body().asString().contains("sold"));
    }

    @Test
    public void createRandomOrder() {
        methods.verifyCreateRandomOrderId();
    }

    @Test (groups = {"needOrderId"})
    public void getStoreOrderById() {
        int orderId = 5;
        Response response = apiClient.get("/store/order/" + orderId);

        response.body().prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertEquals(orderId, response.jsonPath().getInt("id"));
    }

    @Test
    public void getStoreWitOutOrderId() {
        Response response = apiClient.get("/store/order");

        assertEquals(405, response.getStatusCode());
    }

    @Test (groups = {"needOrderId"})
    public void getOrderByStatus() {
        int orderId = 5;
        Response response = apiClient.get("/store/order/" + orderId);
        response.body().prettyPrint();
        assertEquals(200, response.getStatusCode());
        assertEquals(orderId, response.jsonPath().getInt("id"));
    }

    @Test
    public void getOrderNotFound() {
        int orderId = -3;
        Response response = apiClient.get("/store/order/" + orderId);
        assertEquals(404, response.getStatusCode());
        response.body().prettyPrint();
    }

    @Test(groups = {"needOrderId"})
    public void deleteOrder() {
        int orderId = 5;
        Response response = apiClient.delete("/store/order/" + orderId);

        assertEquals(200, response.getStatusCode());

        response = apiClient.get("/store/order/" + orderId);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void deleteOrderNotFound() {

        String invalidOrderId = "**";
        Response response = apiClient.delete("/store/" + invalidOrderId);

        assertEquals(404, response.getStatusCode());
    }


}

