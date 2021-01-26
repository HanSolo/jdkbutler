module eu.hansolo.fx.jdkbutler {
    // Java
    requires java.base;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    // 3rd Party
    requires com.google.gson;
    requires slf4j.api;
    requires org.apache.logging.log4j;
    requires io.foojay.api.discoclient;
    requires java.validation;

    exports eu.hansolo.fx.jdkbutler;
    exports eu.hansolo.fx.jdkbutler.controls;
    exports eu.hansolo.fx.jdkbutler.tools;
}