import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Scanner;

public class Main {
    private static final String API_KEY = "2675de7e694effc093859346"; // Key provided by ExchangeRate-API
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (option != 9) {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.println("Opcion invalida. Favor de elegir una opcion valida.");
                scanner.next(); // clear the invalid input
                printMenu();
            }
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    convertCurrency(scanner, "USD", "ARS");
                    break;
                case 2:
                    convertCurrency(scanner, "ARS", "USD");
                    break;
                case 3:
                    convertCurrency(scanner, "USD", "BRL");
                    break;
                case 4:
                    convertCurrency(scanner, "BRL", "USD");
                    break;
                case 5:
                    convertCurrency(scanner, "USD", "COP");
                    break;
                case 6:
                    convertCurrency(scanner, "COP", "USD");
                    break;
                case 7:
                    convertCurrency(scanner, "USD", "MXN");
                    break;
                case 8:
                    convertCurrency(scanner, "MXN", "USD");
                    break;
                case 9:
                    System.out.println("Cerrando...");
                    break;
                default:
                    System.out.println("Opcion no disponible. Favor de elegir una opcion valida.");
                    break;
            }
            System.out.println("\n\n");
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("************************************************");
        System.out.println("Sea bienvenido/a al Conversor de Moneda");
        System.out.println("************************************************");
        System.out.println("1) Dólar => Peso argentino");
        System.out.println("2) Peso argentino => Dólar");
        System.out.println("3) Dólar => Real brasileño");
        System.out.println("4) Real brasileño => Dólar");
        System.out.println("5) Dólar => Peso colombiano");
        System.out.println("6) Peso colombiano => Dólar");
        System.out.println("7) Dólar => Peso mexicano");
        System.out.println("8) Peso mexicano => Dólar");
        System.out.println("9) Salir");
        System.out.print("Elija una opción válida: ");
        System.out.println("************************************************");
    }

    private static void convertCurrency(Scanner scanner, String fromCurrency, String toCurrency) {
        double amount = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Ingrese el valor que deseas convertir: ");
            if (scanner.hasNextDouble()) {
                amount = scanner.nextDouble();
                validInput = true;
            } else {
                System.out.println("Solo se puede convertir valores numericos. Intente de nuevo.");
                scanner.next(); // clear the invalid input
            }
        }

        final double finalAmount = amount;
        final String finalFromCurrency = fromCurrency;
        final String finalToCurrency = toCurrency;

        String url = BASE_URL + fromCurrency;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(jsonResponse -> {
                    try {
                        double rate = getConversionRate(jsonResponse, finalToCurrency);
                        printConvertedAmount(finalAmount, rate, finalFromCurrency, finalToCurrency);
                    } catch (Exception e) {
                        System.err.println("Error procesando la respuesta JSON: " + e.getMessage());
                    }
                    return null;
                })
                .join();
    }

    private static double getConversionRate(String jsonResponse, String toCurrency) throws Exception {
        JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
        if (!json.has("conversion_rates") || !json.getAsJsonObject("conversion_rates").has(toCurrency)) {
            throw new Exception("Codigo de moneda invalido o tasa de cambio no encontrada en la respuesta JSON.");
        }
        return json.getAsJsonObject("conversion_rates").get(toCurrency).getAsDouble();
    }

    private static void printConvertedAmount(double amount, double rate, String fromCurrency, String toCurrency) {
        double convertedAmount = amount * rate;
        System.out.printf("El valor %.2f [%s] corresponde al valor final de >>> %.2f [%s]%n", amount, fromCurrency, convertedAmount, toCurrency);
    }
}
