package com.nixira.crowler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by pavel.a.popov on 12.02.18.
 */
public class App {

    static Pattern p = Pattern.compile("(?is)key = \"(.+?)\"");

    public static void main(String[] args) {
        try {
            Set<String> inns = new HashSet<>();
            while (inns.size() < 1000) {
                String inn = getRandomString(10);
                if (isValidINN(inn)) {
                    inns.add(inn);
                }
            }

            List<String> comp = new ArrayList<>();
            for (String inn : inns) {

                //URL url = new URL(String.format("https://sbis.ru/contragents/%s", inn));

                Document doc = Jsoup.connect(String.format("https://sbis.ru/contragents/%s", inn)).get();

                if (doc != null) {
                    for (Iterator<Element> it = doc.select("script").iterator(); it.hasNext(); ) {
                        Element el = it.next();
                        if (el.html().contains("window.componentOptions")) {
                            String json = Arrays.stream(el.html().split("\n"))
                                    .filter(s -> s.startsWith("window.componentOptions"))
                                    .collect(Collectors.toList()).get(0).split("=")[1].trim();

                            comp.add(json);
                        }
                    }
                }

                //String content = IOUtils.toString(url.openStream(), "UTF-8");
                //if (!content.contains("class=\"cCard__MainReq-Name\">Нет данных<")) {
                //    System.out.println(content);
                //}
            }

            if (!inns.isEmpty()) {
                System.out.println(inns.size());
                System.out.println(comp.size());
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
