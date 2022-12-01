package dev.azn9.nntest.game;

import dev.azn9.nntest.Point;
import dev.azn9.nntest.genetic.Network;
import reactor.core.publisher.Mono;

import javax.annotation.processing.SupportedSourceVersion;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {

    private static final boolean DEBUG = false;

    private static final int MAX_ALIVE_TIME_SOFT = 200;
    private static final int MAX_ALIVE_TIME_HARD = 1500;

    private static final Random random = new SecureRandom();
    private static final float START_X = 115;
    private static final float START_Y = 550;
    private static final float VISION_ANGLE = 0.7f;

    private final Network network;

    // Data
    private float x = Player.START_X;
    private float y = Player.START_Y;
    private float angle = 0;
    private boolean alive = true;
    private int aliveTime = 0;
    private final List<Point> pointList = new ArrayList<>();

    // Inputs
    private float faceDistance = 0;
    private float leftDistance = 0;
    private float rightDistance = 0;

    // Outputs
    private boolean isMoving = true;
    private int turning = 1;

    // Reward
    private float distance = 0;

    public Player() {
        this.network = new Network(3, 1, 1, 3);
    }

    public Player(Network inputNetwork) {
        this.network = inputNetwork.clone();
    }

    public void reset() {
        this.distance = 0;
        this.x = Player.START_X;
        this.y = Player.START_Y;
        this.angle = 0;
        this.alive = true;
        this.aliveTime = 0;
        this.isMoving = true;
        this.turning = 0;
        this.leftDistance = 0;
        this.rightDistance = 0;
        this.faceDistance = 0;
        this.network.reset();
        this.pointList.clear();
    }

    public void tick(Window window) {
        if (this.alive) {
            this.draw(window);

            this.aliveTime++;

            if (this.aliveTime > Player.MAX_ALIVE_TIME_SOFT && this.distanceToStart() < 100) {
                this.alive = false;
                return;
            }

            if (this.aliveTime > Player.MAX_ALIVE_TIME_HARD) {
                this.alive = false;
                return;
            }

            if (this.aliveTime % 10 == 0) {
                this.pointList.add(new Point(this.x, this.y));
            }

            this.computeInput(window);
            this.drawPartial(window);
            this.think();
            this.computeMovement();
            this.checkDeath(window);
            this.checkReward();
        } else {
            //this.drawPartial(window);
        }
    }

    private double distanceToStart() {
        return Math.sqrt(Math.pow(this.x - Player.START_X, 2) + Math.pow(this.y - Player.START_Y, 2));
    }

    private void checkReward() {
        this.distance = (float) Math.sqrt(Math.pow(this.x - Player.START_X, 2) + Math.pow(this.y - Player.START_Y, 2));
    }

    private void drawPartial(Window window) {
        if (this.alive) {
            window.fill(0, 180, 0);
            window.stroke(0, 180, 0);
        } else {
            window.fill(255, 0, 0);
            window.stroke(255, 0, 0);
        }

        window.strokeWeight(1);
        window.rect(this.x, this.y, 20, 20);

        double newX = this.x + 10 + Math.cos(this.angle) * 30;
        double newY = this.y + 10 + Math.sin(this.angle) * 30;

        window.stroke(0, 150, 150);
        window.strokeWeight(3);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);
    }

    private void draw(Window window) {
        if (!Player.DEBUG) {
            return;
        }

        window.stroke(0, 0, 150);

        double newX;
        double newY;
        double cos;
        double sin;

        cos = Math.cos(this.angle + Player.VISION_ANGLE);
        sin = Math.sin(this.angle + Player.VISION_ANGLE);
        newX = this.x + 10 + cos * 200;
        newY = this.y + 10 + sin * 200;
        window.stroke(0, 0, 150);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);
        newX = this.x + 10 + cos * this.rightDistance;
        newY = this.y + 10 + sin * this.rightDistance;
        window.stroke(255, 0, 255);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);

        cos = Math.cos(this.angle - Player.VISION_ANGLE);
        sin = Math.sin(this.angle - Player.VISION_ANGLE);
        newX = this.x + 10 + cos * 200;
        newY = this.y + 10 + sin * 200;
        window.stroke(0, 0, 150);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);
        newX = this.x + 10 + cos * this.leftDistance;
        newY = this.y + 10 + sin * this.leftDistance;
        window.stroke(255, 0, 255);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);

        cos = Math.cos(this.angle);
        sin = Math.sin(this.angle);
        newX = this.x + 10 + cos * 200;
        newY = this.y + 10 + sin * 200;
        window.stroke(0, 0, 150);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);
        newX = this.x + 10 + cos * this.faceDistance;
        newY = this.y + 10 + sin * this.faceDistance;
        window.stroke(255, 0, 255);
        window.line(this.x + 10, this.y + 10, (float) newX, (float) newY);

        window.fill(0, 0, 0);
        window.text("Left: %.2f".formatted(this.leftDistance), 10, 30);
        window.text("Right: %.2f".formatted(this.rightDistance), 10, 50);
        window.text("Face: %.2f".formatted(this.faceDistance), 10, 70);
    }

    private void computeMovement() {
        this.angle += this.turning * 0.065f;

        if (this.isMoving) {
            this.x += Math.cos(this.angle) * 2;
            this.y += Math.sin(this.angle) * 2;
        }
    }

    private void think() {
        double[] output = this.network.feedForward(new double[]{this.leftDistance, this.rightDistance, this.faceDistance});

        //this.isMoving = output[0] > 0.5;

        if (output[0] > 0.75) {
            this.turning = -1;
        } else if (output[0] < 0.25) {
            this.turning = 1;
        } else {
            this.turning = 0;
        }

        //this.isMoving = Player.random.nextBoolean();
        //this.turning = Player.random.nextInt(3) - 1;

        /*this.turning = 0;
        this.isMoving = false;*/
    }

    private void computeInput(Window window) {
        int[] pixels = window.getBackground().pixels;

        double newX;
        double newY;

        newX = Math.cos(this.angle + Player.VISION_ANGLE);
        newY = Math.sin(this.angle + Player.VISION_ANGLE);

        this.rightDistance = 200;
        for (float i = 0; i < 200; i += 1f) {
            int x = (int) (this.x + 10 + newX * i);
            int y = (int) (this.y + 10 + newY * i);

            if (x < 0 || x >= window.width || y < 0 || y >= window.height) {
                break;
            }

            int pixel = pixels[y * window.width + x];

            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;

            if (red == 0 && green == 0 && blue == 0) {
                this.rightDistance = i;
                break;
            }
        }

        newX = Math.cos(this.angle - Player.VISION_ANGLE);
        newY = Math.sin(this.angle - Player.VISION_ANGLE);

        this.leftDistance = 200;
        for (float i = 0; i < 200; i += 1f) {
            int x = (int) (this.x + 10 + newX * i);
            int y = (int) (this.y + 10 + newY * i);

            if (x < 0 || x >= window.width || y < 0 || y >= window.height) {
                break;
            }

            int pixel = pixels[y * window.width + x];

            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;

            if (red == 0 && green == 0 && blue == 0) {
                this.leftDistance = i;
                break;
            }
        }

        newX = Math.cos(this.angle);
        newY = Math.sin(this.angle);

        this.faceDistance = 200;
        for (float i = 0; i < 200; i += 1f) {
            int x = (int) (this.x + 10 + newX * i);
            int y = (int) (this.y + 10 + newY * i);

            if (x < 0 || x >= window.width || y < 0 || y >= window.height) {
                break;
            }

            int pixel = pixels[y * window.width + x];

            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;

            if (red == 0 && green == 0 && blue == 0) {
                this.faceDistance = i;
                break;
            }
        }
    }

    private void checkDeath(Window window) {
        if (this.x < 0 || this.x >= window.width || this.y < 0 || this.y >= window.height) {
            this.alive = false;
            return;
        }

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                int pixel = window.getBackground().pixels[(int) (this.x + x) + (int) (this.y + y) * window.width];

                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                if (red == 0 && green == 0 && blue == 0) {
                    this.alive = false;
                    return;
                }
            }
        }
    }

    public float getDistance() {
        float distance = 0;

        for (Point point : this.pointList) {
            distance += Math.sqrt(Math.pow(point.x() - this.x, 2) + Math.pow(point.y() - this.y, 2));
        }

        return distance;
    }

    public int getAliveTime() {
        return this.aliveTime;
    }

    public boolean isDead() {
        return !this.alive;
    }

    @Override
    public Player clone() {
        return new Player(this.network);
    }

    public Network getNetwork() {
        return this.network;
    }

    public boolean isAlive() {
        return this.alive;
    }
}
