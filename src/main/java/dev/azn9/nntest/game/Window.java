package dev.azn9.nntest.game;

import processing.core.PApplet;
import processing.core.PImage;

public class Window extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{"Window"}, new Window());
    }

    private PImage background;
    private Game game = new Game();

    @Override
    public void settings() {
        this.size(800, 600);
    }

    @Override
    public void setup() {
        this.frameRate(1.0E12F);
        //this.frameRate(60);

        this.game.initialize(this);

        this.background = this.loadImage("map3.png");
        //noLoop();
    }

    @Override
    public void draw() {
        this.clear();
        this.background(this.background);
        //this.background(255, 255, 255);

        this.color(0, 0, 0);
        this.text("FPS : %.1f".formatted(this.frameRate), 10, 20);

        this.game.tick();

        //this.game.tickDraw();
    }

    public PImage getBackground() {
        return this.background;
    }
}
