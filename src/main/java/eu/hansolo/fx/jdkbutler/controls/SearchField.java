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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;


public class SearchField extends TextField {
    private static final PseudoClass     DARK_MODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty darkMode;

    public SearchField() {
        super();
        darkMode = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_MODE_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return SearchField.this; }
            @Override public String getName() { return "darkMode"; }
        };
        getStylesheets().add(getClass().getResource("searchfield.css").toExternalForm());
        if (Platform.isFxApplicationThread()) {
            setSkin(new SearchFieldSkin(this));
        } else {
            Platform.runLater(() -> setSkin(new SearchFieldSkin(this)));
        }
        getStyleClass().add("search-field");
    }

    public boolean isDarkMode() { return darkMode.get(); }
    public void setDarkMode(final boolean darkMode) { this.darkMode.set(darkMode); }
    public BooleanProperty darkModeProperty() { return darkMode; }
}
