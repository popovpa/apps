package com.nixira.crowler;

import com.google.gson.Gson;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sun.nio.ch.ThreadPool;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

public class CrowlJob implements Callable<String> {

    private String inn;
    private ThreadLocal<ScriptEngine> engineTL = ThreadLocal.withInitial(() -> {
        return new ScriptEngineManager().getEngineByName("javascript");
    });

    public CrowlJob(String inn) {
        this.inn = inn;
    }

    @Override
    public String call() throws Exception {
        String result = null;
        Document doc = Jsoup.connect(String.format("https://sbis.ru/contragents/%s", inn)).get();
        if (doc != null) {
            for (Iterator<Element> it = doc.select("script").iterator(); it.hasNext(); ) {
                Element el = it.next();
                String jsCode = el.html();
                if (jsCode.contains("window.componentOptions")) {
                    try {
                        engineTL.get().eval("window={};" + jsCode);
                        Bindings bindings = engineTL.get().getBindings(ScriptContext.ENGINE_SCOPE);
                        Object a = ((ScriptObjectMirror) bindings.get("window")).get("componentOptions");
                        if (a != null) {
                            Map obj = new Gson().fromJson(a.toString(), Map.class);
                            if (obj != null && obj.size() > 7) {
                                result = a.toString();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
