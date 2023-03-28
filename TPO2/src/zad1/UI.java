package zad1;

import javafx.application.*;
import javafx.embed.swing.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.web.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Insets;
import java.util.*;

import static javafx.scene.layout.Region.*;


public class UI {

    private JFrame frame;
    private JFXPanel webPanel;
    private JComboBox<String> countryComboBox;
    private JTextField cityField;
    private JComboBox<String> currencyComboBox;
    private JButton submitButton;
    private JTextArea weatherLabel;
    private JLabel exchangeLabel;
    private JLabel nbpLabel;
    private WebEngine engine;

    public UI() {
        createUI();
        createScene();
    }

    private void createUI() {
        frame = new JFrame("Weather and Currency Converter");
        webPanel = new JFXPanel();

        countryComboBox = new JComboBox<>(getCountriesList());
        cityField = new JTextField();
        currencyComboBox = new JComboBox<>(getCurrenciesList());
        submitButton = new JButton("Get Data");

        JLabel countryLabel = new JLabel("Country:");
        JLabel cityLabel = new JLabel("City:");
        JLabel currencyLabel = new JLabel("Currency:");

        countryComboBox.setToolTipText("Select the country where the city is located");
        cityField.setToolTipText("Enter the name of the city to check the weather");
        currencyComboBox.setToolTipText("Select the currency to convert to");

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 5, 3, 5);
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(countryLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        inputPanel.add(countryComboBox, c);

        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(cityLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        inputPanel.add(cityField, c);

        c.gridx = 0;
        c.gridy = 2;
        inputPanel.add(currencyLabel, c);

        c.gridx = 1;
        c.gridy = 2;
        inputPanel.add(currencyComboBox, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        inputPanel.add(submitButton, c);

        weatherLabel = new JTextArea("");
        exchangeLabel = new JLabel("");
        nbpLabel = new JLabel("");

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));

        JPanel outputLabelsPanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        outputLabelsPanel.add(new JLabel("Weather: "), c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        weatherLabel = new JTextArea("");
        outputLabelsPanel.add(weatherLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        outputLabelsPanel.add(new JLabel("Exchange rate: "), c);

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        exchangeLabel = new JLabel("");
        outputLabelsPanel.add(exchangeLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        outputLabelsPanel.add(new JLabel("NBP: "), c);

        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        nbpLabel = new JLabel("");
        outputLabelsPanel.add(nbpLabel, c);

        outputPanel.add(outputLabelsPanel, BorderLayout.NORTH);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(outputPanel, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(1024, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        submitButton.addActionListener(e -> updateOutputLabels());
    }

    private void updateOutputLabels() {
        try {
            String country = Objects.requireNonNull(countryComboBox.getSelectedItem()).toString();
            String currency = Objects.requireNonNull(currencyComboBox.getSelectedItem()).toString();
            String city = cityField.getText();

            Service service = new Service(country);

            String weatherDescription = service.getWeather(city);
            weatherLabel.setText(weatherDescription == null ? "N/A" : weatherDescription);
            weatherLabel.setOpaque(true);
            weatherLabel.setEditable(false);
            weatherLabel.setLineWrap(true);
            weatherLabel.setWrapStyleWord(true);
            weatherLabel.setPreferredSize(new Dimension(500, 100));
            weatherLabel.setBackground(UIManager.getColor("exchangeLabel.background"));
            weatherLabel.setFont(UIManager.getFont("exchangeLabel.font"));

            Double rate = service.getRateFor(currency);
            exchangeLabel.setText(rate == null ? "N/A" : rate.toString());

            Double nbpRate = service.getNBPRate();
            nbpLabel.setText(nbpRate == null ? "N/A" : nbpRate.toString());

            String url = String.format("https://en.wikipedia.org/wiki/%s", city);
            loadUrl(url);
        } catch (NullPointerException ignored) {
        }
    }

    private String[] getCountriesList() {
        return Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::getDisplayCountry)
                .filter(country -> !country.isEmpty())
                .sorted()
                .toArray(String[]::new);
    }

    private String[] getCurrenciesList() {
        return Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .sorted()
                .toArray(String[]::new);
    }

    private void createScene() {
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();
            webPanel.setScene(new Scene(view));
        });
    }

    private void loadUrl(String url) {
        Platform.runLater(() -> engine.load(url));
    }
}