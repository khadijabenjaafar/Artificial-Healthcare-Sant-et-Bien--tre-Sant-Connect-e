package org.example.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.example.services.ServiceArticle;
import org.example.services.ServiceCommentaire;

import java.util.ArrayList;
import java.util.List;

public class StatAdmin {

    @FXML
    private Label totalArticlesLabel;
    @FXML
    private Label totalCommentairesLabel;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private AreaChart<String, Number> areaChart;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private ProgressBar progressArticles;
    @FXML
    private ProgressBar progressCommentaires;

    private final ServiceArticle serviceArticle = new ServiceArticle();
    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    private final List<Node> barNodes = new ArrayList<>();
    private final List<Node> areaNodes = new ArrayList<>();
    private final List<Node> lineNodes = new ArrayList<>();

    private boolean countersShown = false;

    @FXML
    public void initialize() {
        totalArticlesLabel.setText(""); // Vide au départ
        totalCommentairesLabel.setText("");

        updateCounters();
        populateBarChart();
        populateAreaChart();
        populateLineChart();
    }

    private void updateCounters() {
        try {
            int totalArticles = serviceArticle.count();
            int totalCommentaires = serviceCommentaire.count();

            progressArticles.setProgress(0);
            progressCommentaires.setProgress(0);

            double progressA = Math.min(1.0, (double) totalArticles / 100);
            double progressC = Math.min(1.0, (double) totalCommentaires / 100);

            animateProgressBar(progressArticles, progressA);
            animateProgressBar(progressCommentaires, progressC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCountersIfNeeded() {
        if (!countersShown) {
            try {
                int totalArticles = serviceArticle.count();
                int totalCommentaires = serviceCommentaire.count();

                animateCounter(totalArticlesLabel, totalArticles, Duration.seconds(1.5));
                animateCounter(totalCommentairesLabel, totalCommentaires, Duration.seconds(1.5));

                countersShown = true;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void animateCounter(Label label, int targetValue, Duration duration) {
        Timeline timeline = new Timeline();
        int frames = 60;
        double increment = (double) targetValue / frames;

        for (int i = 1; i <= frames; i++) {
            int value = (int) Math.round(i * increment);
            KeyFrame kf = new KeyFrame(duration.divide(frames).multiply(i), e -> {
                label.setText(String.valueOf(value));
            });
            timeline.getKeyFrames().add(kf);
        }

        timeline.play();
    }

    private void animateProgressBar(ProgressBar progressBar, double targetProgress) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(progressBar.progressProperty(), targetProgress);
        KeyFrame kf = new KeyFrame(Duration.seconds(1.5), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void populateBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Articles par Mois");
        barChart.getData().clear();
        barNodes.clear();

        try {
            for (int i = 1; i <= 12; i++) {
                int count = serviceArticle.countByMonth(i);
                XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(i), 0);
                series.getData().add(data);

                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(data.YValueProperty(), count);
                KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
                timeline.getKeyFrames().add(kf);
                timeline.play();

                data.nodeProperty().addListener((obs, oldNode, node) -> {
                    if (node != null) {
                        node.setScaleY(0);
                        barNodes.add(node);
                    }
                });
            }

            barChart.getData().add(series);
            barChart.setOnMouseEntered(e -> {
                animateNodes(barNodes);
                showCountersIfNeeded();







            });

            Node barTitle = barChart.lookup(".chart-title");
            if (barTitle != null) {
                barTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateAreaChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commentaires par Mois");
        areaChart.getData().clear();
        areaNodes.clear();

        try {
            for (int i = 1; i <= 12; i++) {
                int count = serviceCommentaire.countByMonth(i);
                XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(i), 0);
                series.getData().add(data);

                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(data.YValueProperty(), count);
                KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
                timeline.getKeyFrames().add(kf);
                timeline.play();

                data.nodeProperty().addListener((obs, oldNode, node) -> {
                    if (node != null) {
                        node.setScaleY(0);
                        areaNodes.add(node);
                    }
                });
            }

            areaChart.getData().add(series);

            // animation zone (opacity du tracé)
            series.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {
                    node.setOpacity(0);
                    Timeline fade = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(node.opacityProperty(), 0)),
                            new KeyFrame(Duration.seconds(1.5), new KeyValue(node.opacityProperty(), 1))
                    );
                    fade.play();
                }
            });

            areaChart.setOnMouseEntered(e -> {
                animateNodes(areaNodes);
                showCountersIfNeeded();
            });

            // 🔥 Titre en gras
            Node areaTitle = areaChart.lookup(".chart-title");
            if (areaTitle != null) {
                areaTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            }






        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateLineChart() {
        XYChart.Series<String, Number> articlesSeries = new XYChart.Series<>();
        articlesSeries.setName("Articles");
        XYChart.Series<String, Number> commentairesSeries = new XYChart.Series<>();
        commentairesSeries.setName("Commentaires");
        lineChart.getData().clear();
        lineNodes.clear();

        try {
            for (int i = 1; i <= 12; i++) {
                int articleCount = serviceArticle.countByMonth(i);
                int commentaireCount = serviceCommentaire.countByMonth(i);

                XYChart.Data<String, Number> articleData = new XYChart.Data<>(String.valueOf(i), 0);
                XYChart.Data<String, Number> commentaireData = new XYChart.Data<>(String.valueOf(i), 0);

                articlesSeries.getData().add(articleData);
                commentairesSeries.getData().add(commentaireData);

                Timeline timeline1 = new Timeline();
                timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new KeyValue(articleData.YValueProperty(), articleCount)));
                timeline1.play();

                Timeline timeline2 = new Timeline();
                timeline2.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new KeyValue(commentaireData.YValueProperty(), commentaireCount)));
                timeline2.play();

                articleData.nodeProperty().addListener((obs, oldNode, node) -> {
                    if (node != null) {
                        node.setScaleX(0);
                        node.setScaleY(0);
                        lineNodes.add(node);
                    }
                });

                commentaireData.nodeProperty().addListener((obs, oldNode, node) -> {
                    if (node != null) {
                        node.setScaleX(0);
                        node.setScaleY(0);
                        lineNodes.add(node);
                    }
                });
            }

            lineChart.getData().addAll(articlesSeries, commentairesSeries);

            articlesSeries.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {
                    node.setOpacity(0);
                    Timeline fade = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(node.opacityProperty(), 0)),
                            new KeyFrame(Duration.seconds(1.5), new KeyValue(node.opacityProperty(), 1))
                    );
                    fade.play();
                }
            });

            commentairesSeries.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {
                    node.setOpacity(0);
                    Timeline fade = new Timeline(
                            new KeyFrame(Duration.seconds(0), new KeyValue(node.opacityProperty(), 0)),
                            new KeyFrame(Duration.seconds(1.5), new KeyValue(node.opacityProperty(), 1))
                    );
                    fade.play();
                }
            });




            lineChart.setOnMouseEntered(e -> {
                animateNodes(lineNodes);
                showCountersIfNeeded();
            });




            Node lineTitle = lineChart.lookup(".chart-title");
            if (lineTitle != null) {
                lineTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateNodes(List<Node> nodes) {
        for (Node node : nodes) {
            ScaleTransition st = new ScaleTransition(Duration.millis(600), node);
            st.setFromX(0);
            st.setFromY(0);
            st.setToX(1);
            st.setToY(1);
            st.play();
        }
    }
}
