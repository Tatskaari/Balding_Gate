package uk.co.poolefoundries.baldinggate.screens


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher
import uk.co.poolefoundries.baldinggate.systems.CameraSystem

class MainMenuScreen(val game:BaldingGateGame) : ScreenAdapter() {

    private val atlas = TextureAtlas(files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
    private val table = Table()
    private val scrollTable = Table()
    private val scrollPane = ScrollPane(scrollTable, skin)

    init {

        scrollTable.setFillParent(false)
        val scrollPane = ScrollPane(scrollTable, skin)
        scrollPane.fadeScrollBars=false
        table.setFillParent(true)
        table.center().center()
        val playButton = TextButton("play", skin)
        val levelsButton = TextButton("levels", skin)
        val optionsButton = TextButton("options", skin)
        val quitButton = TextButton("quit", skin)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game, "level")
            }
        })
        levelsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelSelectScreen(game, this@MainMenuScreen)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = OptionsScreen(game, this@MainMenuScreen)
            }
        })
        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                DesktopLauncher.application.exit()
            }
        })

        scrollTable.add(playButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(levelsButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(optionsButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(quitButton).padBottom(4F).expand()

        table.add(scrollPane).fill().expand()
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.switchToStage()
        cameraSystem.addActorToStage(table)
        cameraSystem.setScrollFocus(scrollPane)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.switchToStage()
        cameraSystem.newStage()
        cameraSystem.addActorToStage(table)
        cameraSystem.setScrollFocus(scrollPane)
        Gdx.input.inputProcessor = cameraSystem.stage
    }

    override fun render(delta: Float) {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.renderStage()
    }

    override fun dispose() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.newStage()
        skin.dispose()
        atlas.dispose()
    }



}