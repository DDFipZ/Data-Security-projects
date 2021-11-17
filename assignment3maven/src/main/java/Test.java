import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            Object o = parser.parse(new FileReader("ACL_roles.json"));
            JSONObject jsonObject = (JSONObject) o;
            System.out.println(jsonObject.get("printer_manager"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
