 /*
  * Copyright (c) 2021 by Gerrit Grunwald
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

 package eu.hansolo.fx.jdkbutler;

 import eu.hansolo.fx.jdkbutler.controls.MacOSWindowButton;
 import eu.hansolo.fx.jdkbutler.controls.SearchField;
 import eu.hansolo.fx.jdkbutler.controls.SelectableLabel;
 import eu.hansolo.fx.jdkbutler.tools.Detector;
 import eu.hansolo.fx.jdkbutler.tools.Detector.MacOSAccentColor;
 import eu.hansolo.fx.jdkbutler.tools.Fonts;
 import eu.hansolo.fx.jdkbutler.tools.Helper;
 import eu.hansolo.fx.jdkbutler.tools.ResizeHelper;
 import io.foojay.api.discoclient.DiscoClient;
 import io.foojay.api.discoclient.event.CacheEvt;
 import io.foojay.api.discoclient.event.DownloadEvt;
 import io.foojay.api.discoclient.event.Evt;
 import io.foojay.api.discoclient.event.EvtObserver;
 import io.foojay.api.discoclient.event.EvtType;
 import io.foojay.api.discoclient.pkg.Architecture;
 import io.foojay.api.discoclient.pkg.ArchiveType;
 import io.foojay.api.discoclient.pkg.Bitness;
 import io.foojay.api.discoclient.pkg.Distribution;
 import io.foojay.api.discoclient.pkg.Latest;
 import io.foojay.api.discoclient.pkg.LibCType;
 import io.foojay.api.discoclient.pkg.MajorVersion;
 import io.foojay.api.discoclient.pkg.OperatingSystem;
 import io.foojay.api.discoclient.pkg.PackageType;
 import io.foojay.api.discoclient.pkg.Pkg;
 import io.foojay.api.discoclient.pkg.ReleaseStatus;
 import io.foojay.api.discoclient.pkg.Scope;
 import io.foojay.api.discoclient.pkg.SemVer;
 import io.foojay.api.discoclient.pkg.TermOfSupport;
 import io.foojay.api.discoclient.pkg.VersionNumber;
 import javafx.application.Application;
 import javafx.application.Platform;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.css.PseudoClass;
 import javafx.geometry.Insets;
 import javafx.geometry.Pos;
 import javafx.scene.Scene;
 import javafx.scene.control.Button;
 import javafx.scene.control.CheckBox;
 import javafx.scene.control.Label;
 import javafx.scene.control.ProgressBar;
 import javafx.scene.control.ScrollPane;
 import javafx.scene.control.ScrollPane.ScrollBarPolicy;
 import javafx.scene.control.Toggle;
 import javafx.scene.control.ToggleGroup;
 import javafx.scene.effect.BlurType;
 import javafx.scene.effect.DropShadow;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.layout.AnchorPane;
 import javafx.scene.layout.Background;
 import javafx.scene.layout.BackgroundFill;
 import javafx.scene.layout.Border;
 import javafx.scene.layout.BorderPane;
 import javafx.scene.layout.BorderStroke;
 import javafx.scene.layout.BorderStrokeStyle;
 import javafx.scene.layout.BorderWidths;
 import javafx.scene.layout.CornerRadii;
 import javafx.scene.layout.HBox;
 import javafx.scene.layout.Priority;
 import javafx.scene.layout.Region;
 import javafx.scene.layout.VBox;
 import javafx.scene.paint.Color;
 import javafx.stage.DirectoryChooser;
 import javafx.stage.Stage;
 import javafx.stage.StageStyle;

 import java.io.File;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Optional;
 import java.util.Set;
 import java.util.function.Consumer;


 /**
  * User: hansolo
  * Date: 18.01.21
  * Time: 12:05
  */
 public class Main extends Application {
     private static final double                PADDING                = 10;
     private static final double                TEXT_PADDING           = -5;
     private static final double                SPACING                = 0;
     private static final double                MIN_COLUMN_WIDTH       = 120;
     private static final PseudoClass           DARK_MODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
     private              Stage                 stage;
     private              BooleanProperty       darkMode;
     private              AnchorPane            headerPane;
     private              MacOSWindowButton     closeWindowButton;
     private              Label                 windowTitle;
     private              AnchorPane            pane;
     private              DiscoClient           discoClient;
     private              DirectoryChooser      directoryChooser;
     private              SearchField           versionSearchField;
     private              CheckBox              allOperatingSystemsCheckBox;
     private              CheckBox              javafxBundledCheckBox;
     private              HBox                  optionBox;
     private              Label                 majorVersionTitle;
     private              Label                 versionTitle;
     private              Label                 distributionTitle;
     private              Label                 operatingSystemTitle;
     private              Label                 architectureTitle;
     private              Label                 archiveTypeTitle;
     private              HBox                  titleBox;
     private              VBox                  majorVersionBox;
     private              VBox                  versionBox;
     private              VBox                  distributionBox;
     private              VBox                  operatingSystemBox;
     private              VBox                  architectureBox;
     private              VBox                  archiveTypeBox;
     private              HBox                  drillDownBox;
     private              ScrollPane            scrollPane;
     private              VBox                  mainBox;
     private              Label                 filenameLabel;
     private              ProgressBar           progressBar;
     private              Button                resetButton;
     private              Button                downloadButton;
     private              HBox                  buttonBox;
     private              List<MajorVersion>    majorVersions;
     private              ToggleGroup           majorVersionToggleGroup;
     private              List<SemVer>          versions;
     private              ToggleGroup           versionToggleGroup;
     private              List<Distribution>    distributions;
     private              ToggleGroup           distributionToggleGroup;
     private              List<OperatingSystem> operatingSystems;
     private              ToggleGroup           operatingSystemToggleGroup;
     private              List<Architecture>    architectures;
     private              ToggleGroup           architectureToggleGroup;
     private              List<ArchiveType>     archiveTypes;
     private              ToggleGroup           archiveTypeToggleGroup;
     private              List<Pkg>             pkgs;
     private              SemVer                selectedSemVer;
     private              Distribution          selectedDistribution;
     private              OperatingSystem       selectedOperatingSystem;
     private              Architecture          selectedArchitecture;
     private              ArchiveType           selectedArchiveType;
     private              Pkg                   selectedPkg;
     private              OperatingSystem       os;
     private              MacOSAccentColor      accentColor;
     private              BorderPane            mainPane;


     // ******************** Initialization ***********************************
     @Override public void init() {
         discoClient                = new DiscoClient();
         majorVersions              = new LinkedList<>();
         majorVersionToggleGroup    = new ToggleGroup();
         versions                   = new LinkedList<>();
         versionToggleGroup         = new ToggleGroup();
         distributions              = new LinkedList<>();
         distributionToggleGroup    = new ToggleGroup();
         operatingSystems           = new LinkedList<>();
         operatingSystemToggleGroup = new ToggleGroup();
         architectures              = new LinkedList<>();
         architectureToggleGroup    = new ToggleGroup();
         pkgs                       = new LinkedList<>();
         archiveTypes               = new LinkedList<>();
         archiveTypeToggleGroup     = new ToggleGroup();
         darkMode                   = new BooleanPropertyBase(false) {
             @Override protected void invalidated() {
                 pane.pseudoClassStateChanged(DARK_MODE_PSEUDO_CLASS, get());
                 System.out.println("DarkMode: " + get());
             }
             @Override public Object getBean() { return Main.this; }
             @Override public String getName() { return "darkMode"; }
         };
         pane                       = new AnchorPane();
         os                         = discoClient.getOperatingSystem();

         darkMode.set(Detector.isDarkMode());
         if (Detector.OperatingSystem.MACOS == Detector.getOperatingSystem()) {
             accentColor = Detector.getMacOSAccentColor();
             if (darkMode.get()) {
                 pane.setStyle("-selection-color: " + Helper.colorToCss(accentColor.getColorDark()));
             } else {
                 pane.setStyle("-selection-color: " + Helper.colorToCss(accentColor.getColorAqua()));
             }
         } else {
             accentColor = MacOSAccentColor.MULTI_COLOR;
         }

         closeWindowButton = new MacOSWindowButton(MacOSWindowButton.Type.CLOSE);
         closeWindowButton.setDarkMode(darkMode.get());

         windowTitle = new Label("JDK Butler");
         windowTitle.setFont(Fonts.sfProTextMedium(16));
         windowTitle.setTextFill(darkMode.get() ? Color.web("#dddddd") : Color.web("#000000"));
         windowTitle.setMouseTransparent(true);
         windowTitle.setAlignment(Pos.CENTER);

         versionSearchField = new SearchField();
         versionSearchField.setFont(Fonts.sfPro(14));
         versionSearchField.setPromptText("Version");

         AnchorPane.setTopAnchor(closeWindowButton, 20d);
         AnchorPane.setLeftAnchor(closeWindowButton, 19d);
         AnchorPane.setTopAnchor(windowTitle, 0d);
         AnchorPane.setRightAnchor(windowTitle, 0d);
         AnchorPane.setBottomAnchor(windowTitle, 0d);
         AnchorPane.setLeftAnchor(windowTitle, 0d);
         AnchorPane.setTopAnchor(versionSearchField, 12d);
         AnchorPane.setRightAnchor(versionSearchField, 10d);

         headerPane = new AnchorPane();
         headerPane.getStyleClass().add("header");
         headerPane.setMinHeight(52);
         headerPane.setMaxHeight(52);
         headerPane.setPrefHeight(52);
         headerPane.setEffect(new DropShadow(BlurType.TWO_PASS_BOX,Color.rgb(0, 0, 0, 0.1),1, 0.0, 0, 1));
         headerPane.getChildren().addAll(closeWindowButton, windowTitle, versionSearchField);

         directoryChooser = new DirectoryChooser();
         directoryChooser.setTitle("Choose target folder");
         directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

         allOperatingSystemsCheckBox = new CheckBox("All operating systems");
         allOperatingSystemsCheckBox.setFont(Fonts.sfPro(14));

         javafxBundledCheckBox = new CheckBox("JavaFX bundled");
         javafxBundledCheckBox.setFont(Fonts.sfPro(14));

         optionBox = new HBox(10, allOperatingSystemsCheckBox, javafxBundledCheckBox);

         majorVersionTitle    = createColumnTitleLabel("Major Version");
         versionTitle         = createColumnTitleLabel("Version");
         distributionTitle    = createColumnTitleLabel("Distribution");
         operatingSystemTitle = createColumnTitleLabel("Operating System");
         architectureTitle    = createColumnTitleLabel("Architecture");
         archiveTypeTitle     = createColumnTitleLabel("Archive Type");
         titleBox             = new HBox(10, majorVersionTitle, versionTitle, distributionTitle, operatingSystemTitle, architectureTitle, archiveTypeTitle);

         majorVersionBox = new VBox();
         majorVersionBox.getStyleClass().add("major-version-box");
         majorVersionBox.setMinWidth(MIN_COLUMN_WIDTH);
         majorVersionBox.setSpacing(10);
         majorVersionBox.setFillWidth(true);

         versionBox = new VBox();
         versionBox.getStyleClass().add("version-box");
         versionBox.setMinWidth(MIN_COLUMN_WIDTH);
         versionBox.setSpacing(10);
         versionBox.setVisible(false);

         distributionBox = new VBox();
         distributionBox.getStyleClass().add("distribution-box");
         distributionBox.setMinWidth(MIN_COLUMN_WIDTH);
         distributionBox.setSpacing(10);
         distributionBox.setVisible(false);

         operatingSystemBox = new VBox();
         operatingSystemBox.getStyleClass().add("operating-system-box");
         operatingSystemBox.setMinWidth(MIN_COLUMN_WIDTH);
         operatingSystemBox.setSpacing(10);
         operatingSystemBox.setVisible(false);

         architectureBox = new VBox();
         architectureBox.getStyleClass().add("architecture-box");
         architectureBox.setMinWidth(MIN_COLUMN_WIDTH);
         architectureBox.setSpacing(10);
         architectureBox.setVisible(false);

         archiveTypeBox = new VBox();
         archiveTypeBox.getStyleClass().add("archive-type-box");
         archiveTypeBox.setMinWidth(MIN_COLUMN_WIDTH);
         archiveTypeBox.setSpacing(10);
         archiveTypeBox.setVisible(false);

         drillDownBox = new HBox(SPACING, majorVersionBox, versionBox, distributionBox, operatingSystemBox, architectureBox, archiveTypeBox);
         drillDownBox.setFillHeight(true);

         scrollPane = new ScrollPane(drillDownBox);
         scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
         scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
         VBox.setVgrow(scrollPane, Priority.ALWAYS);

         filenameLabel = new Label("-");
         filenameLabel.getStyleClass().add("filename-label");

         progressBar = new ProgressBar();
         progressBar.setProgress(0);

         resetButton = new Button("Cancel");
         resetButton.setFont(Fonts.sfPro(14));
         downloadButton = new Button("Download");
         downloadButton.setFont(Fonts.sfPro(14));
         downloadButton.setDisable(true);
         Region spacer = new Region();
         HBox.setHgrow(spacer, Priority.ALWAYS);
         buttonBox = new HBox(20, resetButton, spacer, downloadButton);

         mainBox = new VBox(5, optionBox, titleBox, scrollPane, filenameLabel, progressBar, buttonBox);
         AnchorPane.setTopAnchor(mainBox, 0d);
         AnchorPane.setRightAnchor(mainBox, 0d);
         AnchorPane.setBottomAnchor(mainBox, 0d);
         AnchorPane.setLeftAnchor(mainBox, 0d);

         pane.getChildren().add(mainBox);
         pane.setPadding(new Insets(PADDING));
         pane.setPrefHeight(480);
         pane.getStyleClass().add("jdk-butler");

         mainPane = new BorderPane();
         mainPane.setTop(headerPane);
         mainPane.setCenter(pane);


         // Adjustments related to dark/light mode
         if (darkMode.get()) {
             headerPane.setBackground(new Background(new BackgroundFill(Color.web("#343535"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
             pane.setBackground(new Background(new BackgroundFill(Color.web("#1d1f20"), new CornerRadii(0, 0, 10, 10, false), Insets.EMPTY)));
             mainPane.setBackground(new Background(new BackgroundFill(Color.web("#1d1f20"), new CornerRadii(10), Insets.EMPTY)));
             mainPane.setBorder(new Border(new BorderStroke(Color.web("#515352"), BorderStrokeStyle.SOLID, new CornerRadii(10, 10, 10, 10, false), new BorderWidths(1))));
             versionSearchField.setDarkMode(true);
         } else {
             headerPane.setBackground(new Background(new BackgroundFill(Color.web("#efedec"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
             pane.setBackground(new Background(new BackgroundFill(Color.web("#ecebe9"), new CornerRadii(0, 0, 10, 10, false), Insets.EMPTY)));
             mainPane.setBackground(new Background(new BackgroundFill(Color.web("#ecebe9"), new CornerRadii(10), Insets.EMPTY)));
             mainPane.setBorder(new Border(new BorderStroke(Color.web("#f6f4f4"), BorderStrokeStyle.SOLID, new CornerRadii(10, 10, 10, 10, false), new BorderWidths(1))));
             versionSearchField.setStyle("-fx-background-color: #edefef; -fx-border-color: #d2cfcf;");
             versionSearchField.setDarkMode(false);
         }

         registerListeners();
     }

     private void registerListeners() {
         EvtObserver<DownloadEvt> downloadObserver = e -> {
             EvtType<? extends Evt> type = e.getEvtType();
             if (type.equals(DownloadEvt.DOWNLOAD_STARTED)) {
                 downloadButton.setDisable(true);
             } else if (type.equals(DownloadEvt.DOWNLOAD_PROGRESS)) {
                 progressBar.setProgress((double) e.getFraction() / (double) e.getFileSize());
             } else if (type.equals(DownloadEvt.DOWNLOAD_FINISHED)) {
                 downloadButton.setDisable(false);
                 progressBar.setProgress(0);
             } else if (type.equals(DownloadEvt.DOWNLOAD_FAILED)) {

             }
         };
         discoClient.setOnEvt(DownloadEvt.DOWNLOAD_STARTED, downloadObserver);
         discoClient.setOnEvt(DownloadEvt.DOWNLOAD_PROGRESS, downloadObserver);
         discoClient.setOnEvt(DownloadEvt.DOWNLOAD_FINISHED, downloadObserver);
         discoClient.setOnEvt(DownloadEvt.DOWNLOAD_FAILED, downloadObserver);

         EvtObserver<CacheEvt> cacheObserver = e -> {
             EvtType<? extends Evt> type = e.getEvtType();
             if (type.equals(CacheEvt.CACHE_READY)) {
                System.out.println("Cache ready");
             } else if (type.equals(CacheEvt.CACHE_UPDATING)) {
                 System.out.println("Cache updating");
             }
         };
         discoClient.setOnEvt(CacheEvt.CACHE_READY, cacheObserver);
         discoClient.setOnEvt(CacheEvt.CACHE_UPDATING, cacheObserver);

         headerPane.setOnMousePressed(press -> headerPane.setOnMouseDragged(drag -> {
             stage.setX(drag.getScreenX() - press.getSceneX());
             stage.setY(drag.getScreenY() - press.getSceneY());
         }));

         closeWindowButton.setOnMouseReleased((Consumer<MouseEvent>) e -> stop());
         closeWindowButton.setOnMouseEntered(e -> closeWindowButton.setHovered(true));
         closeWindowButton.setOnMouseExited(e -> closeWindowButton.setHovered(false));

         versionSearchField.setOnAction(e -> {
             String text = versionSearchField.getText();
             if (null == text || text.isEmpty()) { return; }

             String[] textToParse;
             if (text.contains(",")) {
                 textToParse = text.split(",");
             } else {
                 textToParse = new String[] { text };
             }

             VersionNumber   versionNumber   = null;
             SemVer          semVer          = null;
             Distribution    distribution    = null;
             OperatingSystem operatingSystem = null;

             for (String t : textToParse) {
                 if (null == versionNumber) {
                     versionNumber = VersionNumber.fromText(t);
                     if (null != versionNumber) {
                         semVer = SemVer.fromText(t).getSemVer1();
                     }
                 }
                 if (null == distribution || Distribution.NOT_FOUND.equals(distribution)) {
                     distribution = Distribution.fromText(t.toLowerCase());
                 }
                 if (null == operatingSystem || OperatingSystem.NOT_FOUND.equals(operatingSystem)) {
                     operatingSystem = OperatingSystem.fromText(t.toLowerCase());
                 }
             }

             if (null == versionNumber) { return; }
             int featureVersion = versionNumber.getFeature().getAsInt();
             Optional<Toggle> optionalMajorVersionToggle = majorVersionToggleGroup.getToggles()
                                                                                  .stream()
                                                                                  .filter(toggle -> ((MajorVersion)((SelectableLabel) toggle).getData()).getAsInt() == featureVersion)
                                                                                  .findFirst();
             if (optionalMajorVersionToggle.isPresent()) {
                 optionalMajorVersionToggle.get().setSelected(true);
                 SemVer sv = null == semVer ? new SemVer(versionNumber) : semVer;
                 Optional<Toggle> optionalVersionToggle = versionToggleGroup.getToggles()
                                                                            .stream()
                                                                            .filter(toggle -> ((SemVer) ((SelectableLabel) toggle).getData()).compareTo(sv) == 0)
                                                                            .findFirst();
                 if (optionalVersionToggle.isPresent()) {
                     optionalVersionToggle.get().setSelected(true);
                 }
             }

             if (null != distribution) {
                 String distributionUiString = distribution.getUiString();
                 Optional<Toggle> optionalDistributionToggle = distributionToggleGroup.getToggles()
                                                                                      .stream()
                                                                                      .filter(toggle -> ((Distribution) ((SelectableLabel) toggle).getData()).getUiString().equals(distributionUiString))
                                                                                      .findFirst();
                 if (optionalDistributionToggle.isPresent()) {
                     optionalDistributionToggle.get().setSelected(true);
                 }
             }

             if (null != operatingSystem && null != distribution) {
                 String operatingSystemUiString = operatingSystem.getUiString();
                 Optional<Toggle> optionalOperatingSystemToggle = operatingSystemToggleGroup.getToggles()
                                                                                            .stream()
                                                                                            .filter(toggle -> ((OperatingSystem) ((SelectableLabel) toggle).getData()).getUiString().equals(operatingSystemUiString))
                                                                                            .findFirst();
                 if (optionalOperatingSystemToggle.isPresent()) {
                     optionalOperatingSystemToggle.get().setSelected(true);
                 }
             }

             versionSearchField.setText("");
         });
         versionSearchField.focusedProperty().addListener((o, ov, nv) -> {
             if (nv) {
                 String focusedBorderColor = Helper.colorToCss((darkMode.get() ? accentColor.getColorDarkFocus() : accentColor.getColorAquaFocus()));
                 String highlightColor     = Helper.colorToCss((darkMode.get() ? accentColor.getColorDarkHighlight() : accentColor.getColorAquaHighlight()));
                 String highlightTextColor = darkMode.get() ? "white" : "black";
                 String backgroundColor    = darkMode.get() ? "#343535" : "#edefef";

                 versionSearchField.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: " + focusedBorderColor + ", " + focusedBorderColor + "; -fx-highlight-fill: " + highlightColor + "; -fx-highlight-text-fill: " + highlightTextColor);
             } else {
                 String borderColor     = darkMode.get() ? "#494943" : "#e0e1e1";
                 String backgroundColor = darkMode.get() ? "#343535" : "#edefef";
                 versionSearchField.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: " + borderColor + "; -fx-highlight-fill: transparent;");
             }
         });

         allOperatingSystemsCheckBox.selectedProperty().addListener((o, ov, nv) -> reset());

         javafxBundledCheckBox.selectedProperty().addListener((o, ov, nv) -> reset());

         resetButton.setOnAction(e -> reset());

         downloadButton.setOnAction(e -> downloadPkg());

         majorVersionTitle.prefWidthProperty().bind(majorVersionBox.widthProperty());
         versionTitle.prefWidthProperty().bind(versionBox.widthProperty());
         distributionTitle.prefWidthProperty().bind(distributionBox.widthProperty());
         operatingSystemTitle.prefWidthProperty().bind(operatingSystemBox.widthProperty());
         architectureTitle.prefWidthProperty().bind(architectureBox.widthProperty());
         archiveTypeTitle.prefWidthProperty().bind(archiveTypeBox.widthProperty());

         drillDownBox.prefHeightProperty().bind(scrollPane.heightProperty());

         filenameLabel.prefWidthProperty().bind(mainBox.widthProperty());
         progressBar.prefWidthProperty().bind(mainBox.widthProperty());
     }


     // ******************** Application Lifecycle ****************************
     @Override public void start(final Stage stage) {
         this.stage = stage;

         Scene scene = new Scene(mainPane);
         scene.setFill(Color.TRANSPARENT);
         scene.getStylesheets().add(Main.class.getResource("jdkbutler.css").toExternalForm());
         scene.widthProperty().addListener(o -> {
            double columnWidth = (pane.getWidth() - PADDING * 2 - 4 * SPACING) / 6;
            majorVersionBox.setPrefWidth(columnWidth);
            versionBox.setPrefWidth(columnWidth);
            distributionBox.setPrefWidth(columnWidth);
            operatingSystemBox.setPrefWidth(columnWidth);
            architectureBox.setPrefWidth(columnWidth);
            archiveTypeBox.setPrefWidth(columnWidth);
         });

         stage.setTitle("JDK Butler");
         stage.setScene(scene);
         stage.initStyle(StageStyle.TRANSPARENT);
         stage.show();
         stage.centerOnScreen();
         stage.setMinHeight(520);
         stage.focusedProperty().addListener((o, ov, nv) -> {
             if (nv) {
                 if (darkMode.get()) {
                     headerPane.setBackground(new Background(new BackgroundFill(Color.web("#343535"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
                     windowTitle.setTextFill(Color.web("#dddddd"));
                     versionSearchField.setStyle("-fx-background-color: #343535;");
                 } else {
                     headerPane.setBackground(new Background(new BackgroundFill(Color.web("#edefef"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
                     windowTitle.setTextFill(Color.web("#000000"));
                     versionSearchField.setStyle("-fx-background-color: #edefef;");
                 }
                 closeWindowButton.setDisable(false);
             } else {
                 if (darkMode.get()) {
                     headerPane.setBackground(new Background(new BackgroundFill(Color.web("#282927"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
                     windowTitle.setTextFill(Color.web("#696a68"));
                     versionSearchField.setStyle("-fx-background-color: #282927;");
                 } else {
                     headerPane.setBackground(new Background(new BackgroundFill(Color.web("#e5e7e7"), new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY)));
                     windowTitle.setTextFill(Color.web("#a9a6a6"));
                     versionSearchField.setStyle("-fx-background-color: #e5e7e7; -fx-prompt-text-fill: #c7c6c4; -icon-color: #a7a6a5;");
                     closeWindowButton.setStyle("-fx-fill: #ceccca;");
                 }
                 closeWindowButton.setDisable(true);
             }
         });

         ResizeHelper.addResizeListener(stage);

         updateMajorVersions();
     }

     @Override public void stop() {
         discoClient.removeAllObservers();
         Platform.exit();
         System.exit(0);
     }


     // ******************** Methods ******************************************
     private void updateMajorVersions() {
         this.majorVersions.clear();
         discoClient.getAllMajorVersionsAsync(Optional.empty(), Optional.of(true), Optional.of(true)).thenAccept(mvs -> {
             this.majorVersions.addAll(mvs);
             List<SelectableLabel> labels = new LinkedList<>();
             majorVersions.forEach(majorVersion -> {
                 SelectableLabel<MajorVersion> label = new SelectableLabel<>(Integer.toString(majorVersion.getAsInt()), majorVersionToggleGroup, majorVersion, true);
                 label.setAlignment(Pos.CENTER_RIGHT);
                 label.setPadding(new Insets(0, TEXT_PADDING, 0, 0));
                 label.prefWidthProperty().bind(majorVersionBox.widthProperty());
                 label.selectedProperty().addListener((o, ov, nv) -> {
                     if (nv) {
                         selectedSemVer          = null;
                         selectedDistribution    = null;
                         selectedOperatingSystem = null;
                         selectedArchitecture    = null;
                         selectedArchiveType     = null;
                         selectedPkg             = null;
                         downloadButton.setDisable(true);
                         filenameLabel.setText("-");
                         distributionBox.setVisible(false);
                         distributions.clear();
                         operatingSystemBox.setVisible(false);
                         operatingSystems.clear();
                         architectureBox.setVisible(false);
                         architectures.clear();
                         archiveTypeBox.setVisible(false);
                         archiveTypes.clear();
                         updateVersions(majorVersion.getVersions());
                     }
                 });
                 labels.add(label);
             });
             Platform.runLater(() -> majorVersionBox.getChildren().setAll(labels));
         });
     }

     private void updateVersions(final List<SemVer> versions) {
         this.versions.clear();
         this.versions.addAll(versions);
         this.versionToggleGroup.getToggles().clear();
         if (!this.versionBox.isVisible()) { this.versionBox.setVisible(true); }
         List<SelectableLabel> labels = new LinkedList<>();
         this.versions.forEach(version -> {
             SelectableLabel<SemVer> label = new SelectableLabel<>(version.toString(), versionToggleGroup, version, true);
             label.setAlignment(Pos.CENTER_RIGHT);
             label.setPadding(new Insets(0, TEXT_PADDING, 0, 0));
             label.prefWidthProperty().bind(versionBox.widthProperty());
             label.selectedProperty().addListener((o, ov, nv) -> {
                 if (nv) {
                     selectedSemVer          = label.getData();
                     selectedDistribution    = null;
                     selectedOperatingSystem = null;
                     selectedArchitecture    = null;
                     selectedArchiveType     = null;
                     selectedPkg             = null;
                     downloadButton.setDisable(true);
                     filenameLabel.setText("-");
                     operatingSystemBox.setVisible(false);
                     operatingSystems.clear();
                     architectureBox.setVisible(false);
                     architectures.clear();
                     archiveTypeBox.setVisible(false);
                     archiveTypes.clear();
                     discoClient.getDistributionsThatSupportAsync(version, allOperatingSystemsCheckBox.isSelected() ? OperatingSystem.NONE : os, Architecture.NONE, LibCType.NONE, ArchiveType.NONE, PackageType.JDK, javafxBundledCheckBox.isSelected(),true).thenAccept(distros -> updateDistributions(distros));
                 }
             });
             labels.add(label);
         });
         Platform.runLater(() -> versionBox.getChildren().setAll(labels));
     }

     private void updateDistributions(final List<Distribution> distributions) {
         this.distributions.clear();
         this.distributions.addAll(distributions);
         this.distributionToggleGroup.getToggles().clear();
         if (!this.distributionBox.isVisible()) { this.distributionBox.setVisible(true); }
         List<SelectableLabel> labels = new LinkedList<>();
         OperatingSystem operatingSystem = allOperatingSystemsCheckBox.isSelected() ? OperatingSystem.NONE : os;
         this.distributions.forEach(distribution -> {
             SelectableLabel<Distribution> label = new SelectableLabel(distribution.getUiString(), distributionToggleGroup, distribution, true);
             label.setAlignment(Pos.CENTER_RIGHT);
             label.setPadding(new Insets(0, TEXT_PADDING, 0, 0));
             label.prefWidthProperty().bind(distributionBox.widthProperty());
             label.selectedProperty().addListener((o, ov, nv) -> {
                 if (nv) {
                     selectedDistribution    = distribution;
                     selectedOperatingSystem = null;
                     selectedArchitecture    = null;
                     selectedArchiveType     = null;
                     selectedPkg             = null;
                     downloadButton.setDisable(true);
                     filenameLabel.setText("-");
                     SemVer        semVer        = ((SelectableLabel<SemVer>) versionToggleGroup.getSelectedToggle()).getData();
                     VersionNumber versionNumber = VersionNumber.fromText(semVer.toString(true));
                     ReleaseStatus releaseStatus = semVer.getReleaseStatus();
                     pkgs.clear();
                     discoClient.getPkgsAsync(distribution, versionNumber, Latest.EXPLICIT, operatingSystem, LibCType.NONE, Architecture.NONE, Bitness.NONE, ArchiveType.NONE,
                                              PackageType.JDK, javafxBundledCheckBox.isSelected(), true, releaseStatus, TermOfSupport.NONE, Scope.PUBLIC).thenAccept(pk -> {
                                                  pkgs.addAll(pk);
                                                  architectureBox.setVisible(false);
                                                  architectures.clear();
                                                  archiveTypeBox.setVisible(false);
                                                  archiveTypes.clear();
                                                  updateOperatingSystems(pkgs);
                                              });
                 }
             });
             labels.add(label);
         });
         Platform.runLater(() -> distributionBox.getChildren().setAll(labels));
     }
     
     private void updateOperatingSystems(final List<Pkg> pkgs) {
         Set<OperatingSystem> operatingSystems = new HashSet<>();
         pkgs.forEach(pkg -> operatingSystems.add(pkg.getOperatingSystem()));
         this.operatingSystems.clear();
         this.operatingSystems.addAll(operatingSystems);
         this.operatingSystemToggleGroup.getToggles().clear();
         if (!this.operatingSystemBox.isVisible()) { this.operatingSystemBox.setVisible(true); }
         List<SelectableLabel> labels = new LinkedList<>();
         this.operatingSystems.forEach(operatingSystem -> {
             SelectableLabel<OperatingSystem> label = new SelectableLabel<>(operatingSystem.getUiString(), operatingSystemToggleGroup, operatingSystem, true);
             label.setAlignment(Pos.CENTER_RIGHT);
             label.setPadding(new Insets(0, TEXT_PADDING, 0, 0));
             label.prefWidthProperty().bind(operatingSystemBox.widthProperty());
             label.selectedProperty().addListener((o, ov, nv) -> {
                 if (nv) {
                     selectedOperatingSystem = label.getData();
                     selectedArchitecture    = null;
                     selectedArchiveType     = null;
                     selectedPkg             = null;
                     downloadButton.setDisable(true);
                     filenameLabel.setText("-");
                     updateArchitectures(pkgs, operatingSystem);
                 }
             });
             labels.add(label);
         });
         Platform.runLater(() -> operatingSystemBox.getChildren().setAll(labels));
     }

     private void updateArchitectures(final List<Pkg> pkgs, final OperatingSystem operatingSystem) {
         Set<Architecture> architectures = new HashSet<>();
         pkgs.stream()
             .filter(pkg -> pkg.getOperatingSystem() == operatingSystem)
             .forEach(pkg -> architectures.add(pkg.getArchitecture()));
         this.architectures.clear();
         this.architectures.addAll(architectures);
         this.architectureToggleGroup.getToggles().clear();
         if (!this.architectureBox.isVisible()) { this.architectureBox.setVisible(true); }
         List<SelectableLabel> labels = new LinkedList<>();
         this.architectures.forEach(architecture -> {
             SelectableLabel<Architecture> label = new SelectableLabel<>(architecture.getUiString(), architectureToggleGroup, architecture, true);
             label.setAlignment(Pos.CENTER_RIGHT);
             label.setPadding(new Insets(0, TEXT_PADDING, 0, 0));
             label.prefWidthProperty().bind(architectureBox.widthProperty());
             label.selectedProperty().addListener((o, ov, nv) -> {
                 if (nv) {
                     selectedArchitecture = label.getData();
                     selectedPkg          = null;
                     downloadButton.setDisable(true);
                     filenameLabel.setText("-");
                     updateArchiveTypes(pkgs, operatingSystem, selectedArchitecture);
                 }
             });
             labels.add(label);
         });
         Platform.runLater(() -> architectureBox.getChildren().setAll(labels));
     }

     private void updateArchiveTypes(final List<Pkg> pkgs, final OperatingSystem operatingSystem, final Architecture architecture) {
         Set<ArchiveType> archiveTypes = new HashSet<>();
         pkgs.stream()
             .filter(pkg -> pkg.getPackageType() == PackageType.JDK)
             .filter(pkg -> pkg.isJavaFXBundled() == javafxBundledCheckBox.isSelected())
             .filter(pkg -> pkg.isDirectlyDownloadable())
             .filter(pkg -> pkg.getOperatingSystem() == operatingSystem)
             .filter(pkg -> pkg.getArchitecture() == architecture)
             .filter(pkg -> pkg.getReleaseStatus() == selectedSemVer.getReleaseStatus())
             .forEach(pkg -> archiveTypes.add(pkg.getArchiveType()));
         this.archiveTypes.clear();
         this.archiveTypes.addAll(archiveTypes);
         this.archiveTypeToggleGroup.getToggles().clear();
         if (!this.archiveTypeBox.isVisible()) { this.archiveTypeBox.setVisible(true); }
         List<SelectableLabel> labels = new LinkedList<>();
         List<Pkg> packages = new LinkedList<>();
         if (discoClient.cacheReady.get()) {
             packages.addAll(discoClient.getAllPackages());
         } else {
             discoClient.getPkgsAsync(selectedDistribution, selectedSemVer.getVersionNumber(), Latest.EXPLICIT, selectedOperatingSystem, LibCType.NONE,selectedArchitecture,selectedArchitecture.getBitness(),ArchiveType.NONE,PackageType.JDK,javafxBundledCheckBox.isSelected(),true,selectedSemVer.getReleaseStatus(),TermOfSupport.NONE,Scope.PUBLIC).thenAccept(pkgsFound -> packages.addAll(pkgs));
         }
         this.archiveTypes.forEach(archiveType -> {
             SelectableLabel<ArchiveType> label = new SelectableLabel<>(archiveType.getUiString(), archiveTypeToggleGroup, archiveType, false);
             label.setAlignment(Pos.CENTER_RIGHT);
             label.setPadding(new Insets(0, 5, 0, 0));
             label.prefWidthProperty().bind(archiveTypeBox.widthProperty());
             label.selectedProperty().addListener((o, ov, nv) -> {
                 if (nv) {
                     selectedArchiveType = archiveType;
                     selectedPkg         = null;
                     downloadButton.setDisable(true);
                     Optional<Pkg> optionalPkg = packages.stream()
                                                         .filter(pkg -> pkg.getPackageType() == PackageType.JDK)
                                                         .filter(pkg -> pkg.isJavaFXBundled() == javafxBundledCheckBox.isSelected())
                                                         .filter(pkg -> pkg.isDirectlyDownloadable())
                                                         .filter(pkg -> pkg.getJavaVersion().compareTo(selectedSemVer) == 0)
                                                         .filter(pkg -> pkg.getDistribution() == selectedDistribution)
                                                         .filter(pkg -> pkg.getOperatingSystem() == selectedOperatingSystem)
                                                         .filter(pkg -> pkg.getArchitecture() == selectedArchitecture)
                                                         .filter(pkg -> pkg.getArchiveType() == selectedArchiveType)
                                                         .findFirst();
                     if (optionalPkg.isPresent()) {
                         selectedPkg = optionalPkg.get();
                         downloadButton.setDisable(false);
                         filenameLabel.setText(selectedPkg.getFileName());
                     }
                 }
             });
             labels.add(label);
         });
         Platform.runLater(() -> archiveTypeBox.getChildren().setAll(labels));
     }

     private void downloadPkg() {
         if (null == selectedPkg) { return; }
         final File targetFolder = directoryChooser.showDialog(stage);
         if (null != targetFolder) {
             final String pkgId    = selectedPkg.getId();
             final String fileName = selectedPkg.getFileName();
             discoClient.downloadPkg(pkgId, targetFolder.getAbsolutePath() + File.separator + fileName);
         }
     }

     private void reset() {
         versionSearchField.setText("");
         selectedSemVer          = null;
         selectedDistribution    = null;
         selectedOperatingSystem = null;
         selectedArchitecture    = null;
         selectedArchiveType     = null;
         selectedPkg             = null;
         downloadButton.setDisable(true);
         filenameLabel.setText("-");
         versionBox.setVisible(false);
         versions.clear();
         distributionBox.setVisible(false);
         distributions.clear();
         operatingSystemBox.setVisible(false);
         operatingSystems.clear();
         architectureBox.setVisible(false);
         architectures.clear();
         archiveTypeBox.setVisible(false);
         archiveTypes.clear();

         updateMajorVersions();
     }

     private Label createColumnTitleLabel(final String text) {
         Label label = new Label(text);
         label.getStyleClass().add("title");
         return label;
     }


     // ******************** Start ********************************************
     public static void main(String[] args) {
         launch(args);
     }
 }
