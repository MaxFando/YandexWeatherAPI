import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YandexWeatherAPI {
    private static final String API_URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final String API_KEY = "64c70255-0dbe-49b9-bf61-fe8e7a92c386";

    public static void main(String[] args) {
        double lat = 55.75;
        double lon = 37.62;
        int limit = 7;

        try {
            String url = API_URL + "?lat=" + lat + "&lon=" + lon + "&limit=" + limit;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-API-Key", API_KEY);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                System.out.println("Полный JSON-ответ:");
                System.out.println(jsonResponse);

                extractTemperature(jsonResponse);

            } else {
                System.out.println("Ошибка: HTTP код " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void extractTemperature(String jsonResponse) {
        String tempKey = "\"temp\":";
        int tempIndex = jsonResponse.indexOf(tempKey);

        if (tempIndex != -1) {
            int tempStart = tempIndex + tempKey.length();
            int tempEnd = jsonResponse.indexOf(",", tempStart);
            String tempValue = jsonResponse.substring(tempStart, tempEnd).trim();
            System.out.println("\nТекущая температура: " + tempValue);
        } else {
            System.out.println("\nТемпература не найдена в ответе.");
        }

        String forecastKey = "\"temp_avg\":";
        int index = 0;
        int count = 0;
        double sumTemp = 0;

        while ((index = jsonResponse.indexOf(forecastKey, index)) != -1) {
            int valueStart = index + forecastKey.length();
            int valueEnd = jsonResponse.indexOf(",", valueStart);
            String value = jsonResponse.substring(valueStart, valueEnd).trim();
            sumTemp += Double.parseDouble(value);
            count++;
            index = valueEnd;
        }

        if (count > 0) {
            double averageTemp = sumTemp / count;
            System.out.println("\nСредняя температура за " + count + " дней: " + averageTemp);
        } else {
            System.out.println("\nНе удалось вычислить среднюю температуру.");
        }
    }
}