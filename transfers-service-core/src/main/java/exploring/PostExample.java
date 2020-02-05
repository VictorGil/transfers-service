package exploring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class PostExample{
    private static final Logger log = LoggerFactory.getLogger(PostExample.class);

    public static void main(String[] args) {
    Spark.post("/users", (request, response) -> {
        response.type("application/json");
        // User user = new Gson().fromJson(request.body(), User.class);
        // userService.addUser(user);
        String requestBody = request.body();
        // return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        return "";
    });
   }
}
