package de.fhbielefeld.pmdungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import de.fhbielefeld.pmdungeon.PMDungeon;
import de.fhbielefeld.pmdungeon.characters.NonPlayerCharacter;
import de.fhbielefeld.pmdungeon.characters.PlayerCharacter;
import de.fhbielefeld.pmdungeon.characters.nonplayercharacters.demons.Imp;
import de.fhbielefeld.pmdungeon.characters.playercharacters.MaleKnight;
import de.fhbielefeld.pmdungeon.dungeon.Dungeon;
import de.fhbielefeld.pmdungeon.ui.HeadUpDisplay;
import de.fhbielefeld.pmdungeon.util.dungeonconverter.Coordinate;
import de.fhbielefeld.pmdungeon.util.dungeonconverter.DungeonConverter;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameScreen implements Screen {

    public static final float VIRTUAL_HEIGHT = 5f;

    final PMDungeon pmDungeon;

    private OrthographicCamera camera;
    private Dungeon dungeon;
    private PlayerCharacter hero;
    private NonPlayerCharacter imp;
    private final HeadUpDisplay hud;

    public GameScreen(final PMDungeon pmDungeon) {
        this.pmDungeon = pmDungeon;
        hud = new HeadUpDisplay();
        setupCamera();
        setupDungeon();
    }

    private void setupCamera() {
        camera = new OrthographicCamera();
        camera.position.set(0, 0, 0);
        camera.update();
    }

    private void setupDungeon() {
        DungeonConverter dungeonConverter = new DungeonConverter();
        dungeon = dungeonConverter.dungeonFromJson("simple_dungeon.json");
        Coordinate startPosition = dungeon.getStartingPoint();
        hero = new MaleKnight(pmDungeon.getBatch(), dungeon);
        hero.setPosition(startPosition);

        //Testing
        imp = new Imp(pmDungeon.getBatch(), dungeon);
        Coordinate impCoordinate = dungeon.getRoom(1).getPosition();
        impCoordinate.add(dungeon.getRoom(1).getCenter());
        imp.setPosition(impCoordinate);
    }

    private void debugCameraZoom() {
        float cameraZoomSpeed = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS)) {
            camera.zoom -= cameraZoomSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += cameraZoomSpeed * Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen for a Game.
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        debugCameraZoom();
        hero.handleInput(Gdx.input);

        camera.position.set(hero.getPositionX(), hero.getPositionY(), 0);
        camera.update();
        pmDungeon.getBatch().setProjectionMatrix(camera.combined);

        pmDungeon.getBatch().begin();
        dungeon.renderFloor(pmDungeon.getBatch());
        hero.render();
        imp.render();
        dungeon.renderWalls(pmDungeon.getBatch());
        hud.render();
        pmDungeon.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, VIRTUAL_HEIGHT * width / (float) height, VIRTUAL_HEIGHT);
        pmDungeon.getBatch().setProjectionMatrix(camera.combined);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
        // On Android this method is called when the Home button is pressed or an incoming call is received.
        // On desktop this is called just before dispose() when exiting the application.
        // A good place to save the game state.
    }

    @Override
    public void resume() {
        // This method is only called on Android, when the application resumes from a paused state.
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen for a Game.
    }

    @Override
    public void dispose() {
        // Called when the application is destroyed. It is preceded by a call to pause().
        dungeon.dispose();
    }
}
