package dev.zenix.wynnspells.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class WynnSpellsUpdateChecker implements Runnable {

    // in milliseconds
    private long lastCheckTime;

    // in milliseconds
    private long checkInterval;

    public WynnSpellsUpdateChecker() {
        lastCheckTime = System.currentTimeMillis();
        checkInterval = 3_600_000; // 1 hour in milliseconds
    }

    @Override
    public void run() {
        if (!shouldCheckForUpdates())
            return;

        // update timer
        lastCheckTime = System.currentTimeMillis();

        // try to fetch latest version from GitHub API
        try {
            final String API_URL =
                    "https://api.github.com/repos/OhhhZenix/WynnSpells/releases/latest";
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(API_URL))
                    .header("Accept", "application/json").build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                System.out.println("Failed to fetch. Status code: " + httpResponse.statusCode());
                return;
            }

            String body = httpResponse.body();
            Gson gson = new Gson();
            HashMap<?, ?> json = gson.fromJson(body, HashMap.class);
            String latestVersion = (String) json.get("tag_name");
            String currentVersion = FabricLoader.getInstance()
                    .getModContainer(WynnSpellsClient.MOD_ID).map(modContainer -> modContainer
                            .getMetadata().getVersion().getFriendlyString())
                    .orElse("0.0.0");
            String homepageUrl = FabricLoader.getInstance().getModContainer(WynnSpellsClient.MOD_ID)
                    .flatMap(
                            modContainer -> modContainer.getMetadata().getContact().get("homepage"))
                    .orElse("https://github.com/OhhhZenix/WynnSpells");

            if (compareSemver(latestVersion, currentVersion) <= 0) {
                return;
            }

            // TODO: add config settings for notifications
            WynnSpellsUtils.sendNotification(Text.of("New update available now."), true);

            WynnSpellsClient.LOGGER.info(
                    "{} v{} is now available. You're running v{}. Visit {} to download.",
                    WynnSpellsClient.MOD_NAME, latestVersion, currentVersion, homepageUrl);
        } catch (Exception e) {
            WynnSpellsClient.LOGGER.warn("Failed to check for updates", e);
        }
    }

    private boolean shouldCheckForUpdates() {
        return System.currentTimeMillis() - lastCheckTime > checkInterval;
    }

    private int compareSemver(String v1, String v2) {
        String[] a = v1.split("\\.");
        String[] b = v2.split("\\.");

        int len = Math.max(a.length, b.length);

        for (int i = 0; i < len; i++) {
            int n1 = i < a.length ? Integer.parseInt(a[i]) : 0;
            int n2 = i < b.length ? Integer.parseInt(b[i]) : 0;

            if (n1 != n2) {
                return Integer.compare(n1, n2);
            }
        }
        return 0;
    }
}
