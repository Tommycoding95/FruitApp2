package def;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SupermarktApp extends Application {
    private ImageView detailImageView;
    private Label detailNameLabel;
    private Label detailCountryLabel;
    private Label detailPriceLabel;
    private Label detailStockLabel;
    private Label citrusStickerLabel;
    private Label infoMessageLabel;
    private Button addToCartButton;
    private Fruit selectedFruit;
    private final Map<Fruit, Integer> shoppingCartMap = new HashMap<>();
    private VBox cartContentBox;
    private Label totalPriceLabel;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private GridPane fruitGrid;
    private ObservableList<Fruit> observableFruitList;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Supermarkt App");

        List<Fruit> fruitList = FruitLoader.laadFruitVanJson("vruchtenlijst_2.0.json");
        fruitList.sort(Comparator.comparing(Fruit::isAanbieding).reversed()
                .thenComparing(Fruit::getName, String.CASE_INSENSITIVE_ORDER));

        observableFruitList = FXCollections.observableArrayList(fruitList);

        BorderPane root = new BorderPane();

        VBox filterBox = new VBox(10);
        filterBox.setPadding(new Insets(10));
        Label filterLabel = new Label("Filter vruchten:");

        ComboBox<String> landComboBox = new ComboBox<>();
        Set<String> landen = fruitList.stream().map(Fruit::getCountryOfOrigin).collect(Collectors.toCollection(TreeSet::new));
        landen.add("Alle");
        landComboBox.getItems().addAll(landen);
        landComboBox.setValue("Alle");

        TextField zoekVeld = new TextField();
        zoekVeld.setPromptText("Zoek naam of land");

        CheckBox citrusCheck = new CheckBox("Alleen citrusvruchten");

        filterBox.getChildren().addAll(
                filterLabel,
                new HBox(10, new Label("Land:"), landComboBox),
                new HBox(10, new Label("Zoeken:"), zoekVeld),
                citrusCheck
        );

        fruitGrid = new GridPane();
        fruitGrid.setHgap(10);
        fruitGrid.setVgap(10);
        fruitGrid.setPadding(new Insets(10));

        ScrollPane fruitScrollPane = new ScrollPane(fruitGrid);
        fruitScrollPane.setFitToWidth(true);
        VBox leftPane = new VBox(filterBox, fruitScrollPane);
        root.setLeft(leftPane);

        VBox detailPane = new VBox(10);
        detailPane.setPadding(new Insets(20));

        detailImageView = new ImageView();
        detailImageView.setFitHeight(140); // 30% kleiner
        detailImageView.setPreserveRatio(true);

        detailNameLabel = new Label("Selecteer een vrucht");
        detailNameLabel.setFont(Font.font(18));

        detailCountryLabel = new Label();
        detailPriceLabel = new Label();
        detailStockLabel = new Label();
        citrusStickerLabel = new Label();
        citrusStickerLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold");

        infoMessageLabel = new Label();
        infoMessageLabel.setTextFill(Color.DARKBLUE);

        addToCartButton = new Button("Voeg toe aan winkelmand");
        addToCartButton.setDisable(true);
        addToCartButton.setOnAction(e -> voegToeAanWinkelmand());

        detailPane.getChildren().addAll(
                detailImageView,
                detailNameLabel,
                citrusStickerLabel,
                detailCountryLabel,
                detailPriceLabel,
                detailStockLabel,
                addToCartButton,
                infoMessageLabel
        );
        root.setCenter(detailPane);

        VBox winkelmandPane = new VBox(10);
        winkelmandPane.setPadding(new Insets(10));
        winkelmandPane.getChildren().add(new Label("Winkelmand"));

        cartContentBox = new VBox(10);
        ScrollPane cartScroll = new ScrollPane(cartContentBox);
        cartScroll.setFitToWidth(true);

        totalPriceLabel = new Label("Totaal: €0,00");

        Button bevestigBtn = new Button("Bevestig Bestelling");
        bevestigBtn.setOnAction(e -> bevestigBestelling());

        winkelmandPane.getChildren().addAll(cartScroll, totalPriceLabel, bevestigBtn);
        winkelmandPane.setPrefWidth(220);
        root.setRight(winkelmandPane);

        landComboBox.setOnAction(e -> updateFruitGrid(landComboBox, zoekVeld, citrusCheck));
        zoekVeld.textProperty().addListener((obs, old, nieuw) -> updateFruitGrid(landComboBox, zoekVeld, citrusCheck));
        citrusCheck.setOnAction(e -> updateFruitGrid(landComboBox, zoekVeld, citrusCheck));

        updateFruitGrid(landComboBox, zoekVeld, citrusCheck);

        Scene scene = new Scene(root, 1200, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateFruitGrid(ComboBox<String> landComboBox, TextField zoekVeld, CheckBox citrusCheck) {
        fruitGrid.getChildren().clear();
        List<Fruit> filtered = observableFruitList.stream()
                .filter(f -> !citrusCheck.isSelected() || f.isCitrus())
                .filter(f -> {
                    String zoek = zoekVeld.getText().toLowerCase();
                    return zoek.isEmpty()
                            || f.getName().toLowerCase().contains(zoek)
                            || f.getCountryOfOrigin().toLowerCase().contains(zoek);
                })
                .filter(f -> {
                    String land = landComboBox.getValue();
                    return land.equals("Alle") || f.getCountryOfOrigin().equals(land);
                })
                .sorted(Comparator.comparing(Fruit::isAanbieding).reversed()
                        .thenComparing(Fruit::getName))
                .collect(Collectors.toList());

        int kolommen = 4;
        for (int i = 0; i < filtered.size(); i++) {
            Fruit fruit = filtered.get(i);
            VBox tegel = new VBox(5);
            tegel.setAlignment(Pos.CENTER);
            tegel.setPadding(new Insets(4));
            tegel.setPrefSize(100, 100); // 30% kleiner

            StackPane stack = new StackPane();
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/" + fruit.getImagePath()), 70, 70, true, true));
            stack.getChildren().add(img);

            if (fruit.getStock() == 0) {
                Label uitv = new Label("UITVERKOCHT");
                uitv.setTextFill(Color.RED);
                uitv.setStyle("-fx-font-weight: bold");
                Line kruis = new Line(0, 0, 70, 70);
                kruis.setStroke(Color.RED);
                kruis.setStrokeWidth(2);
                stack.getChildren().addAll(uitv, kruis);
            }

            if (fruit.isAanbieding()) {
                Label sticker = new Label("AANBIEDING");
                sticker.setTextFill(Color.ORANGE);
                sticker.setStyle("-fx-font-weight: bold");
                stack.getChildren().add(sticker);
            }

            Label naam = new Label(fruit.getName());
            naam.setStyle("-fx-font-weight: bold");
            tegel.getChildren().addAll(stack, naam);

            tegel.setStyle("-fx-border-color: gray; -fx-border-width: 1.5");

            tegel.setOnMouseClicked(e -> {
                selectedFruit = fruit;
                toonDetail(fruit);
            });

            fruitGrid.add(tegel, i % kolommen, i / kolommen);
        }
    }

    private void toonDetail(Fruit f) {
        detailNameLabel.setText(f.getName());
        detailCountryLabel.setText("Herkomst: " + f.getCountryOfOrigin());
        detailPriceLabel.setText("Prijs: €" + df.format(f.getPrice()));
        detailStockLabel.setText("Voorraad: " + f.getStock());
        citrusStickerLabel.setText(f.isCitrus() ? "Citrusvrucht" : "");

        detailImageView.setImage(new Image(getClass().getResourceAsStream("/" + f.getImagePath())));
        addToCartButton.setDisable(f.getStock() == 0);
        infoMessageLabel.setText("");
    }

    private void voegToeAanWinkelmand() {
        if (selectedFruit != null && selectedFruit.getStock() > 0) {
            shoppingCartMap.put(selectedFruit, shoppingCartMap.getOrDefault(selectedFruit, 0) + 1);
            toonDetail(selectedFruit);
            updateWinkelmand();
            infoMessageLabel.setText(selectedFruit.getName() + " toegevoegd aan winkelmandje");
        }
    }

    private void updateWinkelmand() {
        cartContentBox.getChildren().clear();
        double totaal = 0;

        for (Fruit fruit : shoppingCartMap.keySet()) {
            int aantal = shoppingCartMap.get(fruit);
            double subtotaal = aantal * fruit.getPrice();
            totaal += subtotaal;

            HBox rij = new HBox(5);
            rij.setAlignment(Pos.CENTER_LEFT);

            Label naam = new Label(aantal + " x " + fruit.getName() + " (€" + df.format(fruit.getPrice()) + ")");
            Button plus = new Button("+");
            Button min = new Button("–");

            plus.setOnAction(e -> {
                if (fruit.getStock() > shoppingCartMap.get(fruit)) {
                    shoppingCartMap.put(fruit, shoppingCartMap.get(fruit) + 1);
                    updateWinkelmand();
                }
            });

            min.setOnAction(e -> {
                int huidig = shoppingCartMap.get(fruit);
                if (huidig > 1) {
                    shoppingCartMap.put(fruit, huidig - 1);
                } else {
                    shoppingCartMap.remove(fruit);
                }
                updateWinkelmand();
            });

            rij.getChildren().addAll(naam, plus, min);
            cartContentBox.getChildren().add(rij);
        }

        totalPriceLabel.setText("Totaal: €" + df.format(totaal));
    }

    private void bevestigBestelling() {
        if (shoppingCartMap.isEmpty()) {
            Alert leeg = new Alert(Alert.AlertType.INFORMATION, "Winkelmand is leeg.");
            leeg.showAndWait();
            return;
        }

        Alert bevestiging = new Alert(Alert.AlertType.INFORMATION);
        bevestiging.setTitle("Bestelling bevestigd");
        StringBuilder inhoud = new StringBuilder("Bestelling:");
        double totaal = 0;
        for (Fruit fruit : shoppingCartMap.keySet()) {
            int aantal = shoppingCartMap.get(fruit);
            double prijs = aantal * fruit.getPrice();
            inhoud.append("\n").append(aantal).append(" x ").append(fruit.getName()).append(" = €").append(df.format(prijs));
            totaal += prijs;
        }
        inhoud.append("\nTotaal: €").append(df.format(totaal));
        inhoud.append("\nBestellingsnummer: ").append(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bevestiging.setContentText(inhoud.toString());
        bevestiging.showAndWait();

        shoppingCartMap.clear();
        updateWinkelmand();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
