package velostream.web;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.boon.Boon;
import velostream.api.StreamAPI;

/**
 * Created by Admin on 23/09/2014.
 */
public class UndertowServer {

    public void start_undertow()
    {
        Undertow server = Undertow.builder()
                .addHttpListener(8000, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        if (exchange.getRequestPath().equals("/")) {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                            exchange.getResponseSender().send(Banner.Banner);
                        } else
                        if (exchange.getRequestPath().equals("/Contents")) {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            System.out.println(exchange.getQueryString());
                            EventNameRequestParams all = Boon.fromJson(exchange.getQueryString(),EventNameRequestParams.class);
                            exchange.getResponseSender().send(Boon.toJson(
                                StreamAPI.doQuery(all.getstreamname(),"All", null)));
                        } else if (exchange.getRequestPath().equals("/Contents/1/AVG")) {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            //exchange.getResponseSender().send("{AVG: " + Math.round(Arrays.stream(thestream.getWindow().getContents()).parallel().mapToDouble(e -> (double) e.getFieldValueAsDouble(1)).average().getAsDouble() * 100) / 100d + "}");
                        } else if (exchange.getRequestPath().equals("/Contents/COUNT")) {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            //exchange.getResponseSender().send("{COUNT: " + Arrays.stream(thestream.getWindow().getContents()).parallel().map(e -> e.getId()).count() + "}");
                        }

                    }
                }).build();
        server.start();
    }

    public static void main(String[] args) {
        try {
            UndertowServer us = new UndertowServer();
            us.start_undertow();
            while (true) Thread.currentThread().sleep(1000);
        }
        catch (Exception e)
        {

        }
    }



}
