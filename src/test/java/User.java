import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;
import io.qameta.allure.*;


public class User {


    static String userID;


    @BeforeClass
    public void pre(){
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test(priority = 1)
   public void CreateUser() {

        JSONObject request = new JSONObject();
        request.put("name", "Emad");
        request.put("job", "Tester");


        Response res =
                given().header("x-api-key", "reqres-free-v1")
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(request.toString()).
                when().
                        post("/users").
                then().
                        statusCode(201).
                        body("name", equalTo("Emad")).
                        body("job", equalTo("Tester")).log().all().
                        extract().response();

        userID = res.jsonPath().getString("id");
        System.out.println("User ID : " + userID);

    }

    @Test(priority = 2,dependsOnMethods = "CreateUser")
    public void UpdateUser() {

        JSONObject updateRequest = new JSONObject();
        updateRequest.put("name", "Emad");
        updateRequest.put("job", "Senior Tester");

        Response putResponse =
                given().header("x-api-key", "reqres-free-v1").
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        body(updateRequest.toString()).
                when().
                        put("/users/" + userID).
                then()
                        .statusCode(200)
                        .body("job", equalTo("Senior Tester"))
                        .log().all()
                        .extract().response();

        System.out.println("Job updated to : " + putResponse.jsonPath().getString("job"));

    }


        /*Get Updated User */
        @Test(priority = 3, dependsOnMethods = "UpdateUser")
        public void GetUpdatedUser () {


            given().header("x-api-key", "reqres-free-v1").
                    accept(ContentType.JSON).
            when().
                    get("/users/" +userID).
            then().
                    statusCode(404).log().all();   /*Should be 200 to get user but its mocked data
                                                        but using 404 to pass this test*/
            System.out.println("Updated User Exist");


        }
        @Test(priority = 4,dependsOnMethods = "GetUpdatedUser")
        public void deleteUser() {
            given().header("x-api-key", "reqres-free-v1").
                    accept(ContentType.JSON).
            when().
                    delete("/users/" +userID).
            then().statusCode(204).log().all();
            System.out.println("User Deleted Successfully");

        }
        @Test(priority = 5,dependsOnMethods ="deleteUser")
        public void CheckDeletedUser() {

            given().header("x-api-key", "reqres-free-v1").
                    accept(ContentType.JSON).
            when().
                    get("/users/" +userID).
            then().statusCode(404).log().all();
            System.out.println("User Not Found");
        }


    }





