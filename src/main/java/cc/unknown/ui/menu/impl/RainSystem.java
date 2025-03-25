package cc.unknown.ui.menu.impl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RainSystem {
    private List<Particle> particles;
    private int screenWidth, screenHeight;
    private static final Random random = new Random();

    public RainSystem(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.particles = new ArrayList<>();
    }

    public void update() {
        if (particles.size() < 200) {
            particles.add(new Particle(screenWidth, screenHeight));
        }

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update();
            if (particle.isOutOfBounds()) {
                iterator.remove();
            }
        }
    }

    public void render() {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);

        glBegin(GL_LINES);
        for (Particle particle : particles) {
            particle.render();
        }
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public class Particle {
        private float x, y, speedX, speedY, length;

        public Particle(int screenWidth, int screenHeight) {
            this.x = random.nextFloat() * screenWidth;
            this.y = random.nextFloat() * screenHeight;
            this.speedX = (random.nextFloat() - 0.5f) * 0.2f;
            this.speedY = (random.nextFloat() * 0.5f) + 2f;
            this.length = random.nextFloat() * 5f + 5f;
        }

        public void update() {
            this.x += speedX;
            this.y += speedY;
        }

        public void render() {
        	glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glVertex2f(x, y);
            glVertex2f(x + speedX * 2, y - length);
        }

        public boolean isOutOfBounds() {
            return y > screenHeight;
        }
    }
}
