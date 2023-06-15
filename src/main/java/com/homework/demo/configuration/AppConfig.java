package com.homework.demo.configuration;

import groovy.util.GroovyScriptEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public GroovyScriptEngine groovyScriptEngine() {
        URL url;
        try {
            url = new File("./").toURI().toURL();
        } catch (MalformedURLException e) {
            log.error("Exception while creating url for groovy script engine", e);
            throw new RuntimeException(e);
        }
        return new GroovyScriptEngine(new URL[] {url}, this.getClass().getClassLoader());
    }
}
