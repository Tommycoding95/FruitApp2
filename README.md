FruitApp

Deze JavaFX-applicatie is ontworpen als een eenvoudige supermarktinterface waarin gebruikers fruit kunnen bekijken, filteren en toevoegen aan een winkelmand. De gegevens van het fruit worden geladen vanuit een JSON-bestand. Elk fruititem bevat eigenschappen zoals naam, prijs, herkomstland, voorraad, categorie en of het in de aanbieding is.

Wat de applicatie doet:

Laadt een lijst van fruitsoorten uit een JSON-bestand
Toont fruit in een raster, inclusief afbeelding en naam
Maakt filtering mogelijk op land van herkomst, zoekterm (naam of land) 
Laat duidelijk zien of een product uitverkocht is of in de aanbieding
Biedt de mogelijkheid om fruit toe te voegen aan een winkelmand
Ondersteunt het verhogen en verlagen van de aantallen per fruitsoort
Toont automatisch het totaalbedrag en bevestigt bestellingen met een uniek nummer
Past het fruitraster automatisch aan op de beschikbare schermruimte
Opbouw van de code:

Fruit.java: modelklasse met eigenschappen zoals naam, prijs, land, voorraad, etc.
FruitLoader.java: leest een JSON-bestand in en zet de data om naar Fruit-objecten
SupermarktApp.java: regelt de gehele GUI en gebruikerslogica via JavaFX
Gebruikte technieken:

Objectgeoriënteerd programmeren met duidelijke verantwoordelijkheden per klasse
Werken met externe data (JSON)
Dynamisch bouwen van GUI-componenten op basis van data
Gebruik van JavaFX voor visuele weergave en interactie
Voorwaarden om te draaien:

Java 21 moet geïnstalleerd zijn 
JavaFX SDK 21 is nodig
Instellen in IntelliJ IDEA:

Voeg het JavaFX-pad toe bij Run > Edit Configurations
VM options:
--module-path [pad naar javafx-sdk-21/lib] --add-modules javafx.controls,javafx.fxml
Of via terminal:

java --module-path [pad naar javafx-sdk-21/lib] --add-modules javafx.controls,javafx.fxml -cp out/production/FruitApp2:lib/json-simple-1.1.1.jar def.SupermarktApp
Structuur:

src/def/ bevat de Java-bestanden
resources/images/ bevat alle fruitafbeeldingen
resources/vruchtenlijst_2.0.json is het databestand
