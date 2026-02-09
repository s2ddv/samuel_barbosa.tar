import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("URL connection error");
            return;
        }

        String urlString = args[0];
        Stack<String> stack = new Stack<>();
        int maxDepth = 0;
        String deepestText = "";

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            )) {

                String line;
                while ((line = reader.readLine()) != null) {

                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    if (line.startsWith("<") && line.endsWith("/>")) {
                        System.out.println("malformed HTML");
                        return;
                    }

                    if (line.startsWith("<") && line.endsWith(">") && line.contains(" ")) {
                        System.out.println("malformed HTML");
                        return;
                    }

                    if (line.startsWith("<") && line.endsWith(">")) {

                        if (line.startsWith("</")) {
                            String tagName = line.substring(2, line.length() - 1);
                            
                            if (tagName.isEmpty()) {
                                System.out.println("malformed HTML");
                                return;
                            }

                            if (stack.isEmpty() || !stack.pop().equals(tagName)) {
                                System.out.println("malformed HTML");
                                return;
                            }
                        } 
                        else {
                            String tagName = line.substring(1, line.length() - 1);
                            
                            if (tagName.isEmpty()) {
                                System.out.println("malformed HTML");
                                return;
                            }
                            
                            stack.push(tagName);
                        }
                    }
                    else if (line.contains("<") || line.contains(">")) {
                        System.out.println("malformed HTML");
                        return;
                    }
                    else {
                        int currentDepth = stack.size();
                        if (currentDepth > maxDepth) {
                            maxDepth = currentDepth;
                            deepestText = line;
                        }
                    }
                }
            }

            if (!stack.isEmpty()) {
                System.out.println("malformed HTML");
            } 
            else if (!deepestText.isEmpty()) {
                System.out.println(deepestText);
            }

        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }
}