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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;


public class SelectableLabel<T> extends Label implements Toggle {
    private static final PseudoClass                 SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private              Region                      selectionArrow;
    private              BooleanProperty             selected;
    private              ObjectProperty<ToggleGroup> toggleGroup;
    private              T                           data;
    private              boolean                     selectionArrowEnabled;

    public SelectableLabel() {
        super();
        this.selectionArrowEnabled = false;
        init();
    }
    public SelectableLabel(final String text){
        super(text);
        this.selectionArrowEnabled = false;
        init();
    }
    public SelectableLabel(final String text, final ToggleGroup toggleGroup) {
        super(text);
        this.selectionArrowEnabled = false;
        init();
        this.toggleGroup.set(toggleGroup);
    }
    public SelectableLabel(final String text, final ToggleGroup toggleGroup, final T data, final boolean selectionArrowEnabled) {
        super(text);
        this.selectionArrowEnabled = selectionArrowEnabled;
        init();
        this.toggleGroup.set(toggleGroup);
        this.data = data;
    }


    private void init() {
        getStyleClass().setAll("selectable-label");

        selectionArrow = new Region();
        selectionArrow.getStyleClass().add("selection-arrow");
        selectionArrow.prefWidthProperty().bind(heightProperty());
        selectionArrow.prefHeightProperty().bind(heightProperty());

        if (selectionArrowEnabled) { setGraphic(selectionArrow); }
        setContentDisplay(ContentDisplay.RIGHT);

        toggleGroup = new ObjectPropertyBase<>(null) {
            private ToggleGroup oldToggleGroup;
            @Override protected void invalidated() {
                final ToggleGroup toggleGroup = get();
                if (null != toggleGroup && !toggleGroup.getToggles().contains(SelectableLabel.this)) {
                    if (oldToggleGroup != null) { oldToggleGroup.getToggles().remove(SelectableLabel.this); }
                    toggleGroup.getToggles().add(SelectableLabel.this);
                } else if (null == toggleGroup) {
                    oldToggleGroup.getToggles().remove(SelectableLabel.this);
                }
                oldToggleGroup = toggleGroup;
            }
            @Override public Object getBean() { return SelectableLabel.this; }
            @Override public String getName() { return "toggleGroup"; }
        };

        selected = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
                final boolean selected = get();
                selectionArrow.setVisible(selected);
                final ToggleGroup toggleGroup = getToggleGroup();
                if (toggleGroup != null) {
                    if (selected) {
                        toggleGroup.selectToggle(SelectableLabel.this);
                    } else if (toggleGroup.getSelectedToggle() == SelectableLabel.this) {
                        toggleGroup.getSelectedToggle().setSelected(false);
                        toggleGroup.selectToggle(null);
                    }
                }
            }
            @Override public Object getBean() { return SelectableLabel.this; }
            @Override public String getName() { return "selected"; }
        };

        registerListeners();
    }

    private void registerListeners() {
        setOnMousePressed(e -> {
            if (!selected.get()) {
                selected.set(selected.get() ? false : true);
            }
        });
    }

    @Override public final ToggleGroup getToggleGroup() { return null == toggleGroup ? null : toggleGroup.get(); }
    @Override public final void setToggleGroup(final ToggleGroup toggleGroup) { this.toggleGroup.set(toggleGroup); }
    @Override public final ObjectProperty<ToggleGroup> toggleGroupProperty() { return toggleGroup; }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(final boolean selected) { this.selected.set(selected); }
    public BooleanProperty selectedProperty() { return selected; }

    public T getData() { return data; }
    public void setData(final T data) { this.data = data; }
}
