import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tracker {


    private static final String SESSION = UUID.randomUUID().toString();
    private static final String TRACKER_URL = "https://europe-west1-yo-tracking.cloudfunctions.net/onEvent";


    public static void sendEventAsync(Map<String, String> additionalParams) {
        new Thread(() -> {
            try {
                sendEvent(additionalParams);
            } catch (IOException e) {
                System.err.println("sendEvent failed with IOException: " + e.getLocalizedMessage());
            }
        }).start();
    }

    private static void sendEvent(Map<String, String> additionalParams) throws IOException {
        Map<String, String> data = new HashMap<>();

        data.put("session", SESSION);
        data.put("username", System.getProperty("user.name"));
        data.put("hostname", hostname());

        if (additionalParams != null)
            data.putAll(additionalParams);


        URL url = new URL(TRACKER_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(data));
        out.flush();
        out.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

    }

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ignored) {}
        return "unknown";
    }
}


class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}