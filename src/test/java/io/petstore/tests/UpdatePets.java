package io.petstore.tests;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class UpdatePets extends TestBase{




    @Test(invocationCount = 5)
    public void updatePetNameAndStatus(){

        Response response = given()
                .accept(ContentType.JSON)
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus");

        Assert.assertEquals(response.statusCode(), 200);
        response.prettyPrint();
        List<Map<String,Object>> pets = response.jsonPath().get();

        Random random = new Random();
        Map<String,Object> randomPet = pets.get(random.nextInt(pets.size()));
        String petId = randomPet.get("id").toString();


        String expectedNewPetName = faker.animal().name();
        List<String> statuses = Arrays.asList("available","pending","sold");
        String expectedNewStatus = statuses.get(random.nextInt(statuses.size()));

        System.out.println("petId = " + petId);
        System.out.println("expectedNewPetName = " + expectedNewPetName);
        System.out.println("expectedNewStatus = " + expectedNewStatus);


        Map<String, String> info = new HashMap<>();
        info.put("name", expectedNewPetName);
        info.put("status", expectedNewStatus);

        Response updateResponse = given().accept(ContentType.JSON)
                .contentType(ContentType.URLENC) //application/url-encoded
                .formParams(info)
                .when()
                .post("/pet/" + petId);

        assertEquals(updateResponse.statusCode(), 200);

        Response getThatPet = given().accept(ContentType.JSON)
                .when().get("/pet/" + petId);


        assertEquals(getThatPet.statusCode(), 200);
        String actualName = getThatPet.jsonPath().getString("name");
        String actualStatus = getThatPet.jsonPath().getString("status");
        System.out.println("actualName = " + actualName);
        System.out.println("actualStatus = " + actualStatus);

        assertEquals(expectedNewPetName, actualName);
        assertEquals(expectedNewStatus, actualStatus);
    }
}
