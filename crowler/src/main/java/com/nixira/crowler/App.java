package com.nixira.crowler;

import org.apache.commons.io.IOUtils;

import java.net.URL;

/**
 * Created by pavel.a.popov on 12.02.18.
 */
public class App {
    public static void main(String[] args) {
        try {
            for (int i = 0; i < 10; i++) {

                URL url = new URL("https://market.yandex.ru/product/1729208642");

                String content = IOUtils.toString(url.openStream(), "UTF-8");
                System.out.println(content);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
