package def;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FruitLoader {

    public static List<Fruit> laadFruitVanJson(String bestandsNaam) {
        List<Fruit> fruitLijst = new ArrayList<>();

        try {
            // Probeer het bestand te laden vanuit de resources folder
            InputStream inputStream = FruitLoader.class.getClassLoader().getResourceAsStream(bestandsNaam);
            if (inputStream == null) {
                throw new RuntimeException("Kon bestand " + bestandsNaam + " niet vinden in resources.");
            }

            Reader reader = new InputStreamReader(inputStream);

            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object obj : jsonArray) {
                JSONObject jsonFruit = (JSONObject) obj;

                String name = (String) jsonFruit.get("name");
                String country = (String) jsonFruit.get("countryOfOrigin");
                String imagePath = (String) jsonFruit.get("imagePath");
                double price = ((Number) jsonFruit.get("price")).doubleValue();
                int stock = ((Number) jsonFruit.get("stock")).intValue();
                boolean isCitrus = (Boolean) jsonFruit.get("isCitrus");
                boolean aanbieding = (Boolean) jsonFruit.get("aanbieding");
                String categorie = (String) jsonFruit.get("categorie");

                Fruit fruit = new Fruit(name, country, imagePath, price, stock, isCitrus, aanbieding, categorie);
                fruitLijst.add(fruit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fruitLijst;
    }
}
