import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("<") && line.endsWith("/>")) {
                    System.out.println("malformed HTML");
                    reader.close();
                    return;
                }
                if (line.startsWith("<") && line.endsWith(">") && line.contains(" ")) {
                    System.out.println("malformed HTML");
                    reader.close();
                    return;
                }
                if (line.startsWith("<") && line.endsWith(">")) {
                    if (line.startsWith("</")) {
                        String tagName = line.substring(2, line.length() - 1);

                        if (stack.isEmpty() || !stack.pop().equals(tagName)) {
                            System.out.println("malformed HTML");
                            reader.close();
                            return;
                        }
                    }
                    else {
                        String tagName = line.substring(1, line.length() - 1);
                        stack.push(tagName);
                    }
                }

                else if (line.contains("<") || line.contains(">")) {
                    System.out.println("malformed HTML");
                    reader.close();
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

            reader.close();

            if (!stack.isEmpty()) {
                System.out.println("malformed HTML");
            } else if (maxDepth > 0) {
                System.out.println(deepestText);
            }

        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }
}
