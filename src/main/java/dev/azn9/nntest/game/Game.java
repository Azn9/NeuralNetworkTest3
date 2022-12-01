package dev.azn9.nntest.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.azn9.nntest.genetic.Network;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class Game {

    private static final int PLAYER_COUNT = 1000;
    private static final Comparator<Player> DISTANCE_COMPARATOR = (p1, p2) -> Float.compare(p1.getDistance(), p2.getDistance());

    private final Scheduler scheduler = Schedulers.newParallel("tick", Runtime.getRuntime().availableProcessors());
    private final List<Player> playerList = new ArrayList<>();
    private Window window;
    private int generation = 0;
    private int aliveTime = 0;
    private boolean stop = false;

    public void initialize(Window window) {
        this.window = window;

        for (int i = 0; i < Game.PLAYER_COUNT; i++) {
            this.playerList.add(new Player());
        }
    }

    public void tick() {
        if (this.playerList.stream().allMatch(Player::isDead)) {
            if (this.stop) {
                return;
            }

            this.generation++;
            this.aliveTime = 0;

            List<Player> oldPlayerList = new ArrayList<>(this.playerList);
            this.playerList.clear();

            int toKeepCount = (int) (0.05 * Game.PLAYER_COUNT);
            int toAddCount = Game.PLAYER_COUNT - toKeepCount;
            int ratio = (int) ((float) toAddCount / toKeepCount);

            oldPlayerList.sort(Game.DISTANCE_COMPARATOR);
            Collections.reverse(oldPlayerList);

            for (int i = 0; i < toKeepCount; i++) {
                Player oldPlayer = oldPlayerList.remove(0);

                for (int j = 0; j < ratio + 1; j++) {
                    this.playerList.add(oldPlayer.clone());
                }
            }

            this.playerList.forEach(Player::reset);
        } else {
            this.aliveTime++;

            //long start = System.currentTimeMillis();
            //Mono.when(Flux.fromIterable(this.playerList).flatMap(player -> player.tick(this.window)).parallel(Runtime.getRuntime().availableProcessors()).runOn(this.scheduler)).block();
            for (Player player : this.playerList) {
                player.tick(this.window);
            }

        /*long end = System.currentTimeMillis();
        System.out.println("Tick took " + (end - start) + "ms");*/

            this.window.color(0, 0, 0);
            this.window.fill(0, 0, 0);
            this.window.stroke(0, 0, 0);

            this.playerList.stream().max(Game.DISTANCE_COMPARATOR).ifPresent(player -> {
                this.window.text("Best distance: %.2f".formatted(player.getDistance()), 10, 40);
            });

            this.window.text("Generation: %d".formatted(this.generation), 10, 60);
            this.window.text("AliveTime: %d".formatted(this.aliveTime), 10, 80);
            this.window.text("AliveCount: %d".formatted(this.playerList.stream().filter(Player::isAlive).count()), 10, 100);
        }
    }

    public void stop() {
        this.stop = true;

        this.playerList.sort(Game.DISTANCE_COMPARATOR);
        Collections.reverse(this.playerList);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("generation", this.generation);

        for (int i = 0; i < 5; i++) {
            Player player = this.playerList.get(i);
            Network network = player.getNetwork();
            JsonObject playerJsonObject = new JsonObject();

            playerJsonObject.addProperty("distance", player.getDistance());

            network.getInputLayer();
        }

        String json = "";


        File output = new File("output.json");
        try {
            output.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (var writer = new PrintWriter(new FileWriter(output))) {
            writer.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
