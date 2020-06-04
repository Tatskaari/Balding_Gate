package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.systems.player.PlayerTurnSystem


data class SelectedEntity(
    var entity: Entity? = null,
    var position: PositionComponent? = null,
    var actionPoints: Int = 0,
    var speed: Int = 0
) {
    fun clear() {
        this.entity?.add(ColorComponent(Color.WHITE))
        this.entity = null
        this.position = null
        this.actionPoints = 0
        this.speed = 0
    }
}

object EntitySelectionSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private const val tileSize = 25F //TODO get this from the game engine/level somehow
    private var selectedEntity = SelectedEntity()
    private fun (Entity).toPosition(): PositionComponent {
        return getComponent(PositionComponent::class.java)
    }

    private fun (Entity).toStats(): Stats {
        return getComponent(StatsComponent::class.java).stats
    }

    // Selects next player with AP or deselects
    fun nextPlayer(): Boolean {
        val activePlayers = players().filter { it.toStats().currentAP > 0 }
        return if (activePlayers.isNotEmpty()) {
            val current = activePlayers.indexOf(selectedEntity.entity)
            // loops to select next player
            val next = (current + 1) % activePlayers.size
            selectEntity(activePlayers[next])
        } else {
            deselectEntity()
        }
    }

    // Will update whatever system we use to keep track of selected entity and return whether it was successful
    // should also update highlighted tiles
    private fun selectEntity(entity: Entity): Boolean {
        selectedEntity = SelectedEntity(
            entity,
            entity.toPosition(),
            entity.toStats().currentAP,
            entity.toStats().speed
        )
        return true
    }


    // Will clear selected entity and call updates to highlighted tiles
    fun deselectEntity(): Boolean {
        selectedEntity.clear()
        return true
    }

    // find the game tile position and if the player is selected gets the player system to do the correct action
    // Returns whether an action was carried out
    fun actAt(x: Int, y: Int): Boolean {
        val playerSystem = engine.getSystem(PlayerTurnSystem::class.java)
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val gamePos = cameraSystem.unproject(x, y)
        val targetPos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        if (players().contains(selectedEntity.entity)) {
            return playerSystem.determineAction(selectedEntity.entity!!, targetPos)
        }
        return false
    }

    // Will select entity at given position if valid target exists. Returns whether an entity was selected
    fun selectEntityAt(x: Int, y: Int): Boolean {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val gamePos = cameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())

        val selectedPlayers = players().filter { it.toPosition() == tilePos }
        if (selectedPlayers.isNotEmpty()) {
            selectEntity(selectedPlayers.first())
            return true
        }

        val selectedEnemies = enemies().filter { it.toPosition() == tilePos }
        if (selectedEnemies.isNotEmpty()) {
            selectEntity(selectedEnemies.first())
            return true
        }

        return false
    }

    fun getSelectedEntity():Entity?{
        return selectedEntity.entity
    }

    fun getSelectedEntityStats():Stats?{
        return selectedEntity.entity?.toStats()
    }
}