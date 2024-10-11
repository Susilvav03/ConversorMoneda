import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConversorMoneda {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/56eee95445f5331e27d520ac/latest/COP";

    // Obtener tasa de cambio
    public static String obtenerTasaDeCambio() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // Analizar respuesta JSON
    public static Map<String, Double> analizarRespuesta(String jsonResponse) {
        Gson gson = new Gson();
        // Convertimos la respuesta en un JsonObject
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        // Obtenemos el objeto que contiene las tasas de cambio
        JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");
        // Convertimos el objeto de tasas a un Map para facilidad de acceso
        return gson.fromJson(rates, Map.class);
    }

    // Convertir a moneda destino
    public static void convertirMoneda(double cantidad, String monedaDestino, Map<String, Double> rates) {
        // Monedas soportadas: USD, ARS, BRL
        if (monedaDestino.equals("USD") || monedaDestino.equals("ARS") || monedaDestino.equals("BRL")) {
            if (rates.containsKey(monedaDestino)) {
                double tasa = rates.get(monedaDestino);
                double resultado = cantidad * tasa;
                System.out.println("La cantidad convertida a " + monedaDestino + " es: " + resultado);
            }
        } else {
            System.out.println("Moneda no soportada.");
        }
    }

    // Mostrar menú
    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        String opcion;

        try {
            // Obtenemos la respuesta JSON de la API
            String jsonResponse = obtenerTasaDeCambio();
            // Analizamos la respuesta y obtenemos las tasas de cambio
            Map<String, Double> rates = analizarRespuesta(jsonResponse);

            do {
                // Solicitamos al usuario la cantidad y la moneda destino
                System.out.print("Ingrese cantidad en COP: ");
                double cantidad = scanner.nextDouble();
                System.out.print("Ingrese moneda destino (USD, ARS, BRL): ");
                String monedaDestino = scanner.next().toUpperCase();

                // Convertimos la cantidad a la moneda deseada
                convertirMoneda(cantidad, monedaDestino, rates);

                // Preguntamos si desea realizar otra conversión
                System.out.print("¿Desea realizar otra conversión? (S/N): ");
                opcion = scanner.next().toUpperCase();

            } while (opcion.equals("S"));

            System.out.println("Gracias por usar el conversor de moneda.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        mostrarMenu(); // Llamamos al menú para iniciar el proceso
    }
}
