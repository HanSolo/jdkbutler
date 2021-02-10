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

import eu.hansolo.fx.jdkbutler.tools.Detector;
import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class MainPreloader extends Preloader {
    private Stage     stage;
    private StackPane pane;
    private ImageView imageView;

    @Override public void init() {
        imageView = new ImageView(new Image(MainPreloader.class.getResourceAsStream("JDK_Butler_Duke.png"), 640, 480, true, false));
    }

    @Override public void start(final Stage stage) throws Exception {
        this.stage = stage;
        this.pane  = new StackPane(imageView);
        if (Detector.isDarkMode()) {
            pane.setBackground(new Background(new BackgroundFill(Color.web("#1d1f20"), new CornerRadii(10, false), Insets.EMPTY)));
        } else {
            pane.setBackground(new Background(new BackgroundFill(Color.web("#eaecec"), new CornerRadii(10, false), Insets.EMPTY)));
        }

        Scene scene = new Scene(pane);

        stage.setWidth(335);
        stage.setHeight(480);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    @Override public void handleStateChangeNotification(final StateChangeNotification info) {
        super.handleStateChangeNotification(info);
        if (Type.BEFORE_START == info.getType()) {
            FadeTransition fader = new FadeTransition(Duration.millis(2000), pane);
            fader.setOnFinished(e -> stage.hide());
            fader.play();
        }
    }
}