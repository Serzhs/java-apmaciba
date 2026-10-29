package lv.macibas.patterns;

/**
 * BUILDER — soli pa solim būvē sarežģītu objektu ar daudziem (bieži neobligātiem) parametriem.
 * Tips: Creational.
 *
 * Problēma, ko risina: "telescoping constructor" —
 *   new HttpRequest("GET", "/x", null, null, 30, true, false) — kas ir kas?!
 *
 * Kur redzēsi dabā: StringBuilder, HttpRequest.newBuilder(), Lombok @Builder,
 * Spring ResponseEntity.status(...).body(...), WebClient.builder().
 */
public class BuilderPattern {

    static final class HttpRequest {
        private final String method;
        private final String url;
        private final String body;
        private final int timeoutSeconds;
        private final boolean followRedirects;

        private HttpRequest(Builder b) { // privāts — veidot var tikai caur builderi
            this.method = b.method;
            this.url = b.url;
            this.body = b.body;
            this.timeoutSeconds = b.timeoutSeconds;
            this.followRedirects = b.followRedirects;
        }

        static Builder builder(String url) { return new Builder(url); }

        @Override
        public String toString() {
            return method + " " + url + " body=" + body + " timeout=" + timeoutSeconds + "s redirects=" + followRedirects;
        }

        static final class Builder {
            private final String url;          // obligāts
            private String method = "GET";     // noklusējuma vērtības
            private String body;
            private int timeoutSeconds = 30;
            private boolean followRedirects = true;

            private Builder(String url) { this.url = url; }

            Builder method(String method) { this.method = method; return this; } // atgriež this -> "fluent API"
            Builder body(String body) { this.body = body; return this; }
            Builder timeout(int seconds) { this.timeoutSeconds = seconds; return this; }
            Builder noRedirects() { this.followRedirects = false; return this; }

            HttpRequest build() {
                // validācija vienuviet, pirms objekts tiek izveidots
                if ("GET".equals(method) && body != null) {
                    throw new IllegalStateException("GET pieprasījumam nevajag body");
                }
                return new HttpRequest(this);
            }
        }
    }

    public static void main(String[] args) {
        HttpRequest get = HttpRequest.builder("/api/books").build();
        HttpRequest post = HttpRequest.builder("/api/books")
                .method("POST")
                .body("{\"title\":\"Dune\"}")
                .timeout(5)
                .noRedirects()
                .build();
        System.out.println(get);
        System.out.println(post);
    }
}
