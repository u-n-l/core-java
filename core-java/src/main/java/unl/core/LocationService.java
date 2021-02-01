package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class LocationService {
    private LocationService() {
    }

    private static final String GET_REQUEST_METHOD = "GET";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_AUTHENTICATION = "Bearer";

    @Nullable
    public static String callEndpoint(@NotNull String endpoint, @NotNull String apiKey) throws UnlCoreException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(GET_REQUEST_METHOD);
            connection.setRequestProperty(AUTHORIZATION_HEADER, BEARER_AUTHENTICATION + apiKey);
            connection.connect();

            int status = connection.getResponseCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                default:
                    throw new UnlCoreException("There was a problem calling the location endpoint: " + status);
            }
        } catch (IOException e) {
            throw new UnlCoreException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    throw new UnlCoreException(e.getMessage(), e);
                }
            }
        }
    }
}
