package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem


class LevelScreen(val game: BaldingGateGame) : Screen {

    val input: InputMultiplexer
        get() = InputMultiplexer()

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {

        loadLevel("level").toEntities().forEach(game.engine::addEntity)
        game.engine.addSystem(RenderingSystem(game.camera, game.batch))
        game.engine.addSystem(SkeletonSystem)
        game.engine.addSystem(PlayerSystem)

        input.addProcessor(PanHandler(game.camera))
        input.addProcessor(PlayerInputHandler)
        Gdx.input.inputProcessor = input
//        game.viewport.apply()
//        game.camera.update()

    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.1F, .12F, .16F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.engine.update(delta)

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height)
    }

    override fun dispose() {

    }

}


interface Renderable {
    fun draw(batch: Batch, x: Float, y: Float)
}

data class TextureRenderable(val texture: Texture) : Renderable {
    override fun draw(batch: Batch, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }
}


class RenderingSystem(private val camera: OrthographicCamera, val batch: Batch) : EntitySystem() {
    private val tileSize = 25f
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val visualComponentMapper = ComponentMapper.getFor(VisualComponent::class.java)
    private var entities = ImmutableArray(Array<Entity>())


    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, VisualComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        camera.update()
        batch.projectionMatrix = camera.combined

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        entities.forEach(::drawEntity)
        batch.end()
    }

    private fun drawEntity(entity: Entity) {
        val pos = positionMapper.get(entity)
        val visualComponent = visualComponentMapper.get(entity)

        visualComponent.renderable.draw(batch, pos.x * tileSize, pos.y * tileSize)
    }
}