package com.imbananko;

import com.google.inject.Injector;
import com.imbananko.config.BurlesqueModule;

import static com.google.inject.Guice.createInjector;

public class Main {
    public static void main(String[] args) {
        Injector injector = createInjector(new BurlesqueModule());
        injector.getInstance(BurlesqueREST.class);
    }
}
