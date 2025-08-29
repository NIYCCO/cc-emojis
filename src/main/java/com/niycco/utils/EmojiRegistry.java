package com.niycco.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiRegistry implements ClientModInitializer {

    private static final Gson GSON = new Gson();
    public static final List<String> EMOJI_NAMES = List.of(new String[]{"smiley", "smile", "grin", "laughing", "sweat_smile", "joy", "rofl", "blush", "innocent", "slight_smile", "upside_down", "wink", "relieved", "heart_eyes", "kissing_closed_eyes", "yum", "tongue_close_eyes", "money_mouth", "hugging", "nerd", "sunglasses", "freezing_clown", "smirk", "unamused", "persevere", "pensive", "confused", "slight_frown", "frowning2", "cold_face", "hot_face", "smiling_imp", "weary", "triumph", "angry", "rage", "neutral_face", "expressionless", "hushed", "frowning", "anguished", "open_mouth", "astonished", "flushed", "scream", "fearful", "cold_sweat", "disappointed_relieved", "sweat", "sob", "snod", "crying", "sweating_pray", "sleeping", "rolling_eyes", "thinking", "lying", "grimacing", "nauseated", "vomiting", "sneezing", "mask", "thermometer", "bandage", "raised_eyebrow", "star_struck", "exploding", "monocle", "shushing", "zany", "worried", "hand_over_mouth"});
    public static Map<String, String> snippets = new HashMap<>();
    public static ArrayList<String> emojiCodesCombined = new ArrayList<>();
    public static ArrayList<String> emojiPreview = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return null;
            }

            @Override
            public void reload(ResourceManager manager) {
                snippets.clear();
                emojiCodesCombined.clear();
                emojiPreview.clear();

                final int totalNames = EMOJI_NAMES.size();
                boolean done = false;

                // Alle default.json aus allen aktiven Packs (inkl. Server-RP)
                var definitions = manager.findResources("font", id -> id.getPath().endsWith("font/default.json"));
                System.out.println("Gefundene font/default.json: " + definitions.keySet());

                for (var entry : definitions.entrySet()) {
                    if (done) break;

                    try (InputStreamReader reader =
                                 new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {

                        JsonObject root = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                        if (root == null) continue;

                        var providers = root.getAsJsonArray("providers");
                        if (providers == null) continue;

                        for (JsonElement el : providers) {
                            if (!el.isJsonObject()) continue;
                            JsonObject prov = el.getAsJsonObject();

                            if (!"bitmap".equals(getString(prov, "type"))) continue;
                            if (!"emojies".equals(getString(prov, "name"))) continue;
                            if (prov.has("group") && !"chat".equals(getString(prov, "group"))) continue;

                            var chars = prov.getAsJsonArray("chars");
                            if (chars == null) continue;

                            int idx = 0;
                            for (JsonElement rowEl : chars) {
                                if (!rowEl.isJsonPrimitive()) continue;

                                int[] cps = rowEl.getAsString().codePoints().toArray();
                                for (int cp : cps) {
                                    if (idx >= totalNames) { done = true; break; }

                                    String ch = new String(Character.toChars(cp));
                                    String name = EMOJI_NAMES.get(idx);

                                    snippets.put(ch, name);         // Key: Zeichen, Value: Emoji-Name
                                    emojiCodesCombined.add(":" + name + ":" + " " + ch);
                                    emojiPreview.add(ch);

                                    idx++;
                                }
                                if (done) break;
                            }

                            done = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            private static String getString(JsonObject obj, String key) {
                JsonElement el = obj.get(key);
                return (el != null && el.isJsonPrimitive()) ? el.getAsString() : null;
            }
        });
    }
}
