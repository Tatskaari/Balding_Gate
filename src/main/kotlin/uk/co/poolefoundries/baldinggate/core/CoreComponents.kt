package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.Component
import uk.co.poolefoundries.baldinggate.model.Mob
import uk.co.poolefoundries.baldinggate.model.MobType
import uk.co.poolefoundries.baldinggate.model.Tile
import uk.co.poolefoundries.baldinggate.model.TileType
import uk.co.poolefoundries.baldinggate.screens.Renderable
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

data class PositionComponent(val x: Int, val y: Int) : Component {
    fun distance(other: PositionComponent): Double {
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return sqrt((xDiff * xDiff + yDiff * yDiff).toDouble())
    }

    fun direction(other: PositionComponent): PositionComponent {
        val xDiff = other.x - this.x
        val yDiff = other.y - this.y
        // Moves hori
        return if (xDiff.absoluteValue >= yDiff.absoluteValue) {
            PositionComponent(xDiff.sign, 0)
        } else {
            PositionComponent(0, yDiff.sign)
        }
    }

    operator fun plus(other: PositionComponent): PositionComponent {
        return PositionComponent(
            this.x + other.x,
            this.y + other.y
        )
    }

}


object SkeletonComponent : Component
object PlayerComponent : Component

data class StatsComponent(val stats: Stats) : Component

data class VisualComponent(val renderable: Renderable) : Component

object WallComponent : Component

data class Roll(val die: List<Int>, val mod: Int, val typical: Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
    fun typical() = die.map { it / 2 }.sum() + mod
}

data class Level(
    val name: String,
    val tiles: List<Tile>,
    val tileTypes: List<TileType>,
    val mobs: List<Mob>,
    val mobTypes: List<MobType>
)

data class Stats(
    val vitality: Int,
    val hitPoints: Int,
    val speed: Int,
    val maxAP: Int,
    val currentAP: Int,
    val attack: Roll
)



