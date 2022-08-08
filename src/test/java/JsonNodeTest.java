
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.keycloak.broker.provider.util.SimpleHttp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author admin
 */
public class JsonNodeTest {

    public static void main(String[] args) {

        try {
            Map<String, Object> jsonObj = new HashMap<>();
            jsonObj.put("c", "0");
            jsonObj.put("m", "");

            Map<String, String> token = new HashMap<>();
            token.put("accessToken", "d5221389e00b8fc49e63");
            token.put("expiresIn", "2592000");
            token.put("traceId", "");
            token.put("f", null);

            jsonObj.put("d", token);

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(jsonObj);
            System.out.println("jsonStr: " + jsonStr);

            JsonNode node = mapper.readTree(jsonStr);
            System.out.println(" " + node.has("d"));
            System.out.println(" " + node.get("d").has("accessToken"));

        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonNodeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
