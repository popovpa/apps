package com.nixira.crowler;

import com.google.gson.Gson;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.regex.Pattern;

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
            ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
            for (String inn : inns) {

                Document doc = Jsoup.connect(String.format("https://sbis.ru/contragents/%s", inn)).get();

                if (doc != null) {
                    for (Iterator<Element> it = doc.select("script").iterator(); it.hasNext(); ) {
                        Element el = it.next();
                        String jsCode = el.html();
                        if (jsCode.contains("window.componentOptions")) {
                            try {
                                se.eval("window={};" + jsCode);
                                Bindings bindings = se.getBindings(ScriptContext.ENGINE_SCOPE);
                                Object a = ((ScriptObjectMirror) bindings.get("window")).get("componentOptions");

                                Map obj = new Gson().fromJson(a.toString(), Map.class);
                                if (obj.size() > 7) {
                                    comp.add(a.toString());
                                } else {
                                    System.out.println(a);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
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
