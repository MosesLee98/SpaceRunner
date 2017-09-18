package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class TestGame extends ApplicationAdapter {
	SpriteBatch batch;

    // Backgrounds
	Texture[] bg;

    // Astronauts
    Texture[] astronauts;
    int characterState = 1;
    float astronautY = 0;
    float velocity = 0;
    Rectangle astronautRectangle;

    // Asteroids
    Circle[] asteroidsCircle;
    Texture asteroids;
    int numberOfAsteroids = 7;

    // General Game Fields
    int gameState = 5;
    Random rng;
    float[] AsteroidX = new float[numberOfAsteroids];
    float[] AsteroidOffset = new float[numberOfAsteroids];
    float distanceBetweenAsteroids;
    int score = 0;
    int scoringAsteroid;
    float asteroidVelocity = 4;
    float gravity = 1;
    BitmapFont font;
    Music music;


    @Override
	public void create () {
		batch = new SpriteBatch();

        // Backgrounds depending on gameState
		bg = new Texture[6];
        bg[0] = new Texture("Background.png");
        bg[1] = new Texture("Game_Over.png");
        bg[2] = new Texture("Level_Cleared.png");
        bg[3] = new Texture("Paused.png");
        bg[4] = new Texture("Settings.png");
        bg[5] = new Texture("Blank.png");

        // Astronaut setup
        astronauts = new Texture[2];
        astronauts[0] = new Texture("fly_0003.png"); // Jetpack ON
        astronauts[1] = new Texture("fly_0004.png"); // Jetpack OFF
        // Astronaut hitbox setup
        astronautRectangle = new Rectangle();

        // Asteroids setup
        asteroids = new Texture("Meteor2.png");
        distanceBetweenAsteroids = Gdx.graphics.getWidth() / 5;
        asteroidsCircle = new Circle[numberOfAsteroids];

        // Misc.
        rng = new Random();
        music = Gdx.audio.newMusic(Gdx.files.internal("BackgroundMusic.mp3"));

        // Font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(8);

        startGame(); // Start the game
    }

    public void startGame() {
        astronautY = Gdx.graphics.getHeight() / 2 - astronauts[characterState].getHeight() / 2;

        for (int i = 0; i < numberOfAsteroids; i++) {
            AsteroidOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight());
            AsteroidX[i] = Gdx.graphics.getWidth() / 2 + i * distanceBetweenAsteroids;
            asteroidsCircle[i] = new Circle();
        }

        music.play();
        music.setVolume(0.7f);
        music.setLooping(true);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(bg[gameState],0,0);
        batch.draw(astronauts[characterState], Gdx.graphics.getWidth() / 5, astronautY);

        if (gameState == 5) {
            if (Gdx.input.justTouched()) {
                gameState = 0;
            }
        }

        if (gameState == 0) {

            if (AsteroidX[scoringAsteroid] < Gdx.graphics.getWidth() / 5) {
                score++;
                Gdx.app.log("Score", String.valueOf(score));
                if (scoringAsteroid < numberOfAsteroids - 1) {
                    scoringAsteroid++;
                } else {
                    scoringAsteroid = 0;
                }
            }

            if (Gdx.input.isTouched()) {
                velocity -= 2.3;
                characterState = 0;
            } else {
                characterState = 1;
            }

            for (int i = 0; i < numberOfAsteroids; i++) {
                if (AsteroidX[i] < - asteroids.getWidth()) {
                    AsteroidX[i] += numberOfAsteroids * distanceBetweenAsteroids;
                    AsteroidOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight());
                } else {
                    AsteroidX[i] -= asteroidVelocity;
                }

                batch.draw(asteroids, AsteroidX[i], Gdx.graphics.getHeight() / 2  + AsteroidOffset[i]);

                asteroidsCircle[i] = new Circle(AsteroidX[i] + 145, AsteroidOffset[i] + asteroids.getHeight()*2.4f, asteroids.getHeight()/2 - 10);
            }

            if (astronautY > -175 && astronautY < Gdx.graphics.getHeight() + 300) {
                velocity += gravity;
                astronautY -= velocity;
            } else {
                gameState = 1;
            }

            // Draw Score
            font.draw(batch, String.valueOf(score), 100, 200);

            // Hitboxes
            astronautRectangle.set(Gdx.graphics.getWidth()/5, astronautY, astronauts[characterState].getHeight()/2, astronauts[characterState].getWidth()/2);

            // Collisions
            for (int i = 0; i < numberOfAsteroids; i++) {
                if (Intersector.overlaps(asteroidsCircle[i], astronautRectangle)) {
                    gameState = 1;
                }
            }

        } else if (gameState == 1) {
            font.draw(batch, "YOUR SCORE WAS: " + String.valueOf(score), Gdx.graphics.getWidth()/6, Gdx.graphics.getHeight()/2);
            if (Gdx.input.justTouched()) {
                gameState = 5;
                startGame();
                score = 0;
                scoringAsteroid = 0;
                velocity = 0;
            }
        }
        batch.end();
	}

}
