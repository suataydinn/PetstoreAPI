package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import utilities.ConfigurationReader;

public class BaseTest {


    @BeforeClass
    public static void setUp() {
            RestAssured.baseURI = ConfigurationReader.get("baseUrl");
    }

}

