package ru.tcreator;

import com.google.gson.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Main {
    /**
     * URL адрес
     */
    static final String URL = "https://pokeapi.co/api/v2/pokemon";

    public static void main(String ...args) throws IOException {
        try {
            String responseTextJson = getResultRequest(URL);
            ArrayList<Pokemon> pokemonList = getPokemonList(responseTextJson);
            System.out.println(pokemonList.size());
            Predicate<Pokemon> filtering = (element) -> element.getHeight() > 15;

            pokemonList.stream()
                    .filter(filtering)
                    .forEach(System.out::println);

        } catch (IOException | NotJsonException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Преобразуем в коллекцию объектов
     * @param jsonString {@link String}
     * @return ArrayList<Pokemon>
     */
    static ArrayList<Pokemon> getPokemonList(String jsonString) throws NotJsonException, IOException {
        Gson gson = new Gson();
        final ArrayList<Pokemon> pokemonList = new ArrayList<>();
        String json = jsonString;
        JsonObject jsonObject = getJsonObject(json);
        String next = jsonObject.get("next").getAsString();

        for (int i = 0; i < 10; i++) {
            JsonArray tmpArray = jsonObject.get("results").getAsJsonArray();

            tmpArray.forEach(
                    element -> {
                        try {
                            String tmpPokemonJson = getResultRequest(element
                                    .getAsJsonObject()
                                    .get("url")
                                    .getAsString()
                            );

                            if (isJsonString(tmpPokemonJson)) {
                                Pokemon pokemon = gson.fromJson(tmpPokemonJson, Pokemon.class);
                                pokemonList.add(pokemon);
                            } else {
                                System.out.println("Not json");
                            }

                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    });

            json = getResultRequest(next);
            jsonObject = getJsonObject(json);
            next = jsonObject.get("next").getAsString();
        }
        return pokemonList;
    }

    /**
     * Возвращает JsonObject из строки Json
     * @param json {@link String}
     * @return {@link JsonObject}
     */
    static JsonObject getJsonObject(String json) throws NotJsonException  {
        if(isJsonString(json)) {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(json);
            return jsonElement.getAsJsonObject();
        } else {
            throw new NotJsonException("Строка не является JSON");
        }
    }

    /**
     * Валидация на JSON
     * @param json {@link String}
     * @return {@link Boolean}
     */
    static boolean isJsonString(String json) {
        Gson gson = new Gson();
        try {
            gson.fromJson(json, Object.class);
            return Boolean.TRUE;
        } catch (JsonSyntaxException e) {
            return Boolean.FALSE;
        }
    }




    /**
     * Возвращает результат запроса к ресурсу
     * @param url {@link String}
     * @return {@link String}
     * @throws IOException
     */
    static String getResultRequest(String url) throws IOException {
        try(CloseableHttpClient closeableHttpClient = HttpClientBuilder
                .create()
                .build()
        ) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            closeableHttpResponse
                                    .getEntity()
                                    .getContent()
                    ))) {
                return bufferedReader.readLine();
            }
        }
    }

}
