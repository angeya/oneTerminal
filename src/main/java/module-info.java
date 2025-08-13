module top.angeya.oneterminal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.kodedu.terminalfx;
    requires com.fasterxml.jackson.databind;
    requires atlantafx.base;
    requires java.logging;
    requires org.slf4j;
    requires org.apache.commons.lang3;
    requires javafx.web;

    opens top.angeya.oneterminal to com.fasterxml.jackson.databind;
    exports top.angeya.oneterminal;
}