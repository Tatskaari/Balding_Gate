package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.EnemyComponent
import uk.co.poolefoundries.baldinggate.core.FloorComponent
import uk.co.poolefoundries.baldinggate.core.PlayerComponent
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.pathfinding.*


object PathfinderSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private fun floors() = engine.getEntitiesFor(floorsFamily).toList()
    private fun (Entity).toPosition() = getComponent(PositionComponent::class.java)
    private fun (Entity).toAStar() = AStarNode(toPosition().x, toPosition().y)
    private fun (Entity).toSpread() = SpreadNode(toPosition().x, toPosition().y)
    private fun (AStarNode).toSpread() = SpreadNode(x, y)

    private fun (AStarNode).toPosition() = PositionComponent(x, y)
    private val pathfinder = AStar(manhattanHeuristic())
    private val spreadFinder = SpreadPath
    private var levelMap = mutableListOf<AStarNode>()

    override fun addedToEngine(engine: Engine?) {
        levelMap = floors().map { it.toAStar() }.toMutableList()
    }

    fun findPath(start: PositionComponent, end: PositionComponent): List<PositionComponent> {
        val startNode = AStarNode(start.x, start.y)
        val endNode = AStarNode(end.x, end.y)
        val nodesToRemove = enemies().map { it.toAStar() } + players().map { it.toAStar() }
        return pathfinder.getPath((levelMap-nodesToRemove).map{it.copy()}, startNode, endNode)
            .map { it.toPosition() }.reversed()
    }

    fun findSpread(start:PositionComponent, speed:Int): List<SpreadNode>{
        val startNode = SpreadNode(start.x, start.y)
        val nodesToRemove = enemies().map { it.toSpread() } + players().map { it.toSpread() }
        return spreadFinder.getMovementBorder((levelMap.map{it.toSpread()}).map{it.copy()} , startNode, speed)
    }
}





