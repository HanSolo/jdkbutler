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

package eu.hansolo.fx.jdkbutler.controls;

import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;


public class SearchFieldSkin extends TextFieldSkin {
    private Region loupe;
    private Region crossButton;


    public SearchFieldSkin(final SearchField control){
        super(control);

        initGraphics();
        registerListeners();
    }


    private void initGraphics() {
        loupe = new Region();
        loupe.getStyleClass().add("loupe");
        loupe.setFocusTraversable(false);
        AnchorPane.setLeftAnchor(loupe, 0d);

        crossButton = new Region();
        crossButton.getStyleClass().add("cross-button");
        crossButton.setFocusTraversable(false);
        AnchorPane.setRightAnchor(crossButton, 0d);

        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll(crossButton, loupe);

        pane.prefWidthProperty().bind(getSkinnable().widthProperty());

        getChildren().addAll(pane);
    }

    private void registerListeners() {
        crossButton.setOnMouseClicked(event -> getSkinnable().setText(""));
        getSkinnable().textProperty().addListener(o -> crossButton.setVisible(getSkinnable().getText().isEmpty() ? false : true));
        getSkinnable().focusedProperty().addListener(o -> crossButton.setVisible(getSkinnable().isFocused() && !getSkinnable().getText().isEmpty() ? true : false));
    }
}
