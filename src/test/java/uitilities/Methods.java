package uitilities;

import io.restassured.response.Response;
import pojo.Order;
import utilities.ApiClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class Methods {

    ApiClient apiClient = new ApiClient();
    Order order = new Order();



    public void verifyStatus(String status){

        Response response = apiClient.get("/pet/findByStatus?status=" + status);


        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().asString().contains("id"));
    }

    public void verifyCreateRandomOrderId(){

        int availableId = getIdInteger("available").get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedDate = now.format(formatter);

        Random random = new Random();
        int randomId = random.nextInt(10) + 1;
        order.setId(randomId);
        order.setPetId(availableId);
        order.setQuantity(1);
        order.setShipDate(formattedDate);
        order.setStatus("placed");
        order.setComplete(true);

        Response response = apiClient.post("/store/order", order);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().asString().contains("id"));
        assertEquals(availableId, response.jsonPath().getInt("petId"));
        assertEquals(1, response.jsonPath().getInt("quantity"));
        assertEquals(formattedDate, response.jsonPath().getString("shipDate"));
        assertEquals("placed", response.jsonPath().getString("status"));
        assertEquals(true, response.jsonPath().getBoolean("complete"));
    }

    public void createOrderId(int setId){

        int availableId = getIdInteger("available").get(0);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedDate = now.format(formatter);

        order.setId(setId);
        order.setPetId(availableId);
        order.setQuantity(1);
        order.setShipDate(formattedDate);
        order.setStatus("placed");
        order.setComplete(true);

        Response response = apiClient.post("/store/order", order);

        response.body().prettyPrint();
        assertEquals(200, response.getStatusCode());

        int orderId = response.jsonPath().getInt("id");
        assertEquals(setId, orderId);
    }

    public List<Integer> getIdInteger(String status){

        Response response = apiClient.get("/pet/findByStatus?status=" + status);

        response.body().prettyPrint();

        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().asString().contains("id"));

        List<Object> petIds = response.jsonPath().getList("id");
        List<Integer> petIdsInteger = new ArrayList<>();
        for (Object petId : petIds) {
            if (petId instanceof Integer) {
                petIdsInteger.add((Integer) petId);
            }
        }
        return petIdsInteger;
    }

    public List<Long> getIdLong(String status){

        Response response = apiClient.get("/pet/findByStatus?status=" + status);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.body().asString().contains("id"));

        List<Object> petIds = response.jsonPath().getList("id");
        List<Long> petIdsLong = new ArrayList<>();
        for (Object petId : petIds) {
            if (petId instanceof Long) {
                petIdsLong.add((Long) petId);
            }
        }
        return petIdsLong;
    }

}
