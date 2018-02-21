package com.nixira.crowler;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by pavel.a.popov on 12.02.18.
 */
public class App {
    public static void main(String[] args) {
        try {
            int errors = 0;
            Set<String> inns = new HashSet<>();
            while (inns.size() < 1000) {
                String inn = getRandomString(10);
                if (isValidINN(inn)) {
                    inns.add(inn);
                } else {
                    errors++;
                }
            }
            System.out.println(errors);

            for (String inn : inns) {

                URL url = new URL(String.format("https://sbis.ru/contragents/%s", inn));

                String content = IOUtils.toString(url.openStream(), "UTF-8");
                if (!content.contains("class=\"cCard__MainReq-Name\">Нет данных<")) {
                    System.out.println(content);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final Pattern innPatter = Pattern.compile("\\d{10}|\\d{12}");

    public static boolean isValidINN(String inn) {
        inn = inn.trim();
        if (!innPatter.matcher(inn).matches()) {
            return false;
        }
        int length = inn.length();
        if (length == 12) {
            return INNStep(inn, 2, 1) && INNStep(inn, 1, 0);
        } else {
            return INNStep(inn, 1, 2);
        }
    }

    private static final int[] checkArr = new int[]{3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};

    private static boolean INNStep(String inn, int offset, int arrOffset) {
        int sum = 0;
        int length = inn.length();
        for (int i = 0; i < length - offset; i++) {
            sum += (inn.charAt(i) - '0') * checkArr[i + arrOffset];
        }
        return (sum % 11) % 10 == inn.charAt(length - offset) - '0';
    }

    private static char[] chars = "0123456789".toCharArray();

    private static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars[(int) (Math.random() * (chars.length - 1))]);
        }
        return sb.toString();
    }

}
