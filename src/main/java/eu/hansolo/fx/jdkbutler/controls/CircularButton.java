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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.function.Consumer;


/**
 * User: hansolo
 * Date: 22.01.21
 * Time: 13:07
 */
@DefaultProperty("children")
public class CircularButton extends Region {
    public enum Type {
        CLOSE(Color.web("#ff5e57"), Color.web("#343535")),
        MINIMIZE(Color.web("#febc2e"), Color.web("#343535")),
        MAXIMIZE(Color.web("#26c840"), Color.web("#343535"));

        public final Color backgroundColor;
        public final Color foregroundColor;

        Type(final Color backgroundColor, final Color foregroundColor) {
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
        }
    }


    private static final double                   PREFERRED_WIDTH  = 12;
    private static final double                   PREFERRED_HEIGHT = 12;
    private static final double                   MINIMUM_WIDTH    = 12;
    private static final double                   MINIMUM_HEIGHT   = 12;
    private static final double                   MAXIMUM_WIDTH    = 12;
    private static final double                   MAXIMUM_HEIGHT   = 12;
    private static final InnerShadow              INNER_SHADOW     = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.5), 1, 0.0, 0, 0);
    private              double                   size;
    private              double                   width;
    private              double                   height;
    private              Canvas                   canvas;
    private              GraphicsContext          ctx;
    private              Type                     _type;
    private              ObjectProperty<Type>     type;
    private              Color                    _backgroundColor;
    private              ObjectProperty<Color>    backgroundColor;
    private              Color                    _backgroundColorHover;
    private              ObjectProperty<Color>    backgroundColorHover;
    private              Color                    _backgroundColorPressed;
    private              ObjectProperty<Color>    backgroundColorPressed;
    private              Color                    _backgroundColorDisabled;
    private              ObjectProperty<Color>    backgroundColorDisabled;
    private              Color                    _foregroundColor;
    private              ObjectProperty<Color>    foregroundColor;
    private              Color                    _foregroundColorHover;
    private              ObjectProperty<Color>    foregroundColorHover;
    private              Color                    _foregroundColorPressed;
    private              ObjectProperty<Color>    foregroundColorPressed;
    private              Color                    _foregroundColorDisabled;
    private              ObjectProperty<Color>    foregroundColorDisabled;
    private              boolean                  showForeground;
    private              EventHandler<MouseEvent> mouseHandler;
    private              Consumer<MouseEvent> mouseEnteredConsumer;
    private              Consumer<MouseEvent> mouseExitedConsumer;
    private              Consumer<MouseEvent> mousePressedConsumer;
    private              Consumer<MouseEvent>     mouseReleasedConsumer;


    // ******************** Constructors **************************************
    public CircularButton() {
        this(Type.CLOSE);
    }
    public CircularButton(final Type type) {
        //getStylesheets().add(CircularButton.class.getResource("circular-button.css").toExternalForm());

        _type                    = type;
        _backgroundColor         = _type.backgroundColor;
        _backgroundColorHover    = _backgroundColor;
        _backgroundColorPressed  = _backgroundColor.brighter();
        _backgroundColorDisabled = Color.web("#4b4d4c");
        _foregroundColor         = _type.foregroundColor;
        _foregroundColorHover    = _foregroundColor;
        _foregroundColorPressed  = _foregroundColor.brighter();
        _foregroundColorDisabled = Color.TRANSPARENT;
        showForeground           = false;
        
        mouseHandler             = e -> handleMouseEvent(e);
        
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("circular-button");
        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();
        
        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        disabledProperty().addListener(e -> redraw());
        addEventFilter(MouseEvent.MOUSE_ENTERED, mouseHandler);
        addEventFilter(MouseEvent.MOUSE_EXITED, mouseHandler);
        addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
        addEventFilter(MouseEvent.MOUSE_RELEASED, mouseHandler);
        hoverProperty().addListener(o -> redraw());
        disabledProperty().addListener(o -> redraw());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    public Type getType() { return null == type ? _type : type.get(); }
    public void setType(final Type type) {
        if (null == this.type) {
            _type = type;
            setBackgroundColor(_type.backgroundColor);
        } else {
            this.type.set(type);
        }
    }
    public ObjectProperty<Type> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<>() {
                @Override protected void invalidated() { setBackgroundColor(get().backgroundColor); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "type"; }
            };
            _type = null;
        }
        return type;
    }


    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            _backgroundColor = backgroundColor;
            redraw();
        } else {
            this.backgroundColor.set(backgroundColor);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public Color getBackgroundColorHover() { return null == backgroundColorHover ? _backgroundColorHover : backgroundColorHover.get(); }
    public void setBackgroundColorHover(final Color backgroundColorHover) {
        if (null == this.backgroundColorHover) {
            _backgroundColorHover = backgroundColorHover;
            redraw();
        } else {
            this.backgroundColorHover.set(backgroundColorHover);
        }
    }
    public ObjectProperty<Color> backgroundColorHoverProperty() {
        if (null == backgroundColorHover) {
            backgroundColorHover = new ObjectPropertyBase<>(_backgroundColorHover) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "backgroundColorHover"; }
            };
            _backgroundColorHover = null;
        }
        return backgroundColorHover;
    }

    public Color getBackgroundColorPressed() { return null == backgroundColorPressed ? _backgroundColorPressed : backgroundColorPressed.get(); }
    public void setBackgroundColorPressed(final Color backgroundColorPressed) {
        if (null == this.backgroundColorPressed) {
            _backgroundColorPressed = backgroundColorPressed;
            redraw();
        } else {
            this.backgroundColorPressed.set(backgroundColorPressed);
        }
    }
    public ObjectProperty<Color> backgroundColorPressedProperty() {
        if (null == backgroundColorPressed) {
            backgroundColorPressed = new ObjectPropertyBase<>(_backgroundColorPressed) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "backgroundColorPressed"; }
            };
            _backgroundColorPressed = null;
        }
        return backgroundColorPressed;
    }

    public Color getBackgroundColorDisabled() { return null == backgroundColorDisabled ? _backgroundColorDisabled : backgroundColorDisabled.get(); }
    public void setBackgroundColorDisabled(final Color backgroundColorDisabled) {
        if (null == this.backgroundColorDisabled) {
            _backgroundColorDisabled = backgroundColorDisabled;
            redraw();
        } else {
            this.backgroundColorDisabled.set(backgroundColorDisabled);
        }
    }
    public ObjectProperty<Color> backgroundColorDisabledProperty() {
        if (null == backgroundColorDisabled) {
            backgroundColorDisabled = new ObjectPropertyBase<>(_backgroundColorDisabled) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "backgroundColorDisabled"; }
            };
            _backgroundColorDisabled = null;
        }
        return backgroundColorDisabled;
    }

    public Color getForegroundColor() { return null == foregroundColor ? _foregroundColor : foregroundColor.get(); }
    public void setForegroundColor(final Color foregroundColor) {
        if (null == this.foregroundColor) {
            _foregroundColor = foregroundColor;
            redraw();
        } else {
            this.foregroundColor.set(foregroundColor);
        }
    }
    public ObjectProperty<Color> foregroundColorProperty() {
        if (null == foregroundColor) {
            foregroundColor = new ObjectPropertyBase<>(_foregroundColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "foregroundColor"; }
            };
            _foregroundColor = null;
        }
        return foregroundColor;
    }

    public Color getForegroundColorHover() { return null == foregroundColorHover ? _foregroundColorHover : foregroundColorHover.get(); }
    public void setForegroundColorHover(final Color foregroundColorHover) {
        if (null == this.foregroundColorHover) {
            _foregroundColorHover = foregroundColorHover;
            redraw();
        } else {
            this.foregroundColorHover.set(foregroundColorHover);
        }
    }
    public ObjectProperty<Color> foregroundColorHoverProperty() {
        if (null == foregroundColorHover) {
            foregroundColorHover = new ObjectPropertyBase<>(_foregroundColorHover) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "foregroundColorHover"; }
            };
            _foregroundColorHover = null;
        }
        return foregroundColorHover;
    }

    public Color getForegroundColorPressed() { return null == foregroundColorPressed ? _foregroundColorPressed : foregroundColorPressed.get(); }
    public void setForegroundColorPressed(final Color foregroundColorPressed) {
        if (null == this.foregroundColorPressed) {
            _foregroundColorPressed = foregroundColorPressed;
            redraw();
        } else {
            this.foregroundColorPressed.set(foregroundColorPressed);
        }
    }
    public ObjectProperty<Color> foregroundColorPressedProperty() {
        if (null == foregroundColorPressed) {
            foregroundColorPressed = new ObjectPropertyBase<>(_foregroundColorPressed) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "foregroundColorPressed"; }
            };
            _foregroundColorPressed = null;
        }
        return foregroundColorPressed;
    }

    public Color getForegroundColorDisabled() { return null == foregroundColorDisabled ? _foregroundColorDisabled : foregroundColorDisabled.get(); }
    public void setForegroundColorDisabled(final Color foregroundColorDisabled) {
        if (null == this.foregroundColorDisabled) {
            _foregroundColorDisabled = foregroundColorDisabled;
            redraw();
        } else {
            this.foregroundColorDisabled.set(foregroundColorDisabled);
        }
    }
    public ObjectProperty<Color> foregroundColorDisabledProperty() {
        if (null == foregroundColorDisabled) {
            foregroundColorDisabled = new ObjectPropertyBase<>(_foregroundColorDisabled) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return CircularButton.this; }
                @Override public String getName() { return "foregroundColorDisabled"; }
            };
            _foregroundColorDisabled = null;
        }
        return foregroundColorDisabled;
    }

    public void setShowForeground(final boolean showForeground) {
        this.showForeground = showForeground;
        redraw();
    }


    // ******************** Mouse Handling ************************************
    public void setOnMouseEntered(final Consumer<MouseEvent> mouseEnteredConsumer)   { this.mouseEnteredConsumer  = mouseEnteredConsumer; }
    public void setOnMouseExited(final Consumer<MouseEvent> mouseExitedConsumer)     { this.mouseExitedConsumer   = mouseExitedConsumer; }
    public void setOnMousePressed(final Consumer<MouseEvent> mousePressedConsumer)   { this.mousePressedConsumer  = mousePressedConsumer; }
    public void setOnMouseReleased(final Consumer<MouseEvent> mouseReleasedConsumer) { this.mouseReleasedConsumer = mouseReleasedConsumer; }

    private void handleMouseEvent(final MouseEvent e) {
        final EventType<? extends MouseEvent> type = e.getEventType();
        if (MouseEvent.MOUSE_ENTERED.equals(type)) {
            if (null == mouseEnteredConsumer) { return; }
            mouseEnteredConsumer.accept(e);
        }
        if (MouseEvent.MOUSE_EXITED.equals(type)) {
            if (null == mouseExitedConsumer) { return; }
            mouseExitedConsumer.accept(e);
        }
        if (MouseEvent.MOUSE_PRESSED.equals(type)) {
            if (null == mousePressedConsumer) { return; }
            mousePressedConsumer.accept(e);
        }
        if (MouseEvent.MOUSE_RELEASED.equals(type)) {
            if (null == mouseReleasedConsumer) { return; }
            mouseReleasedConsumer.accept(e);
        }
        redraw();
    }
    

    // ******************** Layout ********************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
        resize();
    }
    
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;
        
        if (width > 0 && height > 0) {
            canvas.setWidth(size);
            canvas.setHeight(size);
            
            redraw();
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, size, size);
        if (isDisabled()) {
            System.out.println("disabled");
            ctx.setFill(getBackgroundColorDisabled());
            ctx.setStroke(getForegroundColorDisabled());
        } else if (isHover()) {
            ctx.setFill(getBackgroundColorHover());
            ctx.setStroke(getForegroundColorHover());
        } else if (isPressed()) {
            ctx.setFill(getBackgroundColorPressed());
            ctx.setStroke(getForegroundColorPressed());
        } else {
            ctx.setFill(getBackgroundColor());
            ctx.setStroke(getForegroundColor());
        }

        ctx.save();
        ctx.setEffect(INNER_SHADOW);
        ctx.fillOval(0, 0, size, size);
        ctx.restore();

        ctx.setLineWidth(size * 0.1);
        if (isHover() || isPressed() || showForeground) {
            switch (getType()) {
                case CLOSE:
                    ctx.strokeLine(size * 0.33333333, size * 0.33333333, size * 0.66666667, size * 0.66666667);
                    ctx.strokeLine(size * 0.26666667, size * 0.66666667, size * 0.66666667, size * 0.33333333);
                    break;
                case MINIMIZE:
                    ctx.strokeLine(size * 0.20833333, size * 0.5, size * 0.73333333, size * 0.5);
                    break;
                case MAXIMIZE:
                    ctx.setFill(getForegroundColor());
                    ctx.beginPath();
                    ctx.moveTo(size * 0.225, size * 0.225);
                    ctx.lineTo(size * 0.6, size * 0.25);
                    ctx.lineTo(size * 0.25, size * 0.6);
                    ctx.lineTo(size * 0.225, size * 0.225);
                    ctx.moveTo(size * 0.76666667, size * 0.76666667);
                    ctx.lineTo(size * 0.39166667, size * 0.74166667);
                    ctx.lineTo(size * 0.74166667, size * 0.39166667);
                    ctx.lineTo(size * 0.76666667, size * 0.76666667);
                    ctx.closePath();
                    ctx.fill();
                    break;
            }
        }
    }
}
