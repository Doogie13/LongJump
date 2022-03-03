package com.lambda.modules

import com.lambda.LongJumpPlugin
import com.lambda.client.event.events.PlayerMoveEvent
import com.lambda.client.manager.managers.TimerManager.modifyTimer
import com.lambda.client.module.Category
import com.lambda.client.module.Module
import com.lambda.client.module.modules.player.LagNotifier
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.MovementUtils.calcMoveYaw
import com.lambda.client.util.threads.safeListener
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class LongJump : PluginModule (
    name = "LongJump",
    category = Category.MOVEMENT,
    description = "Causes you to jump farther",
    pluginMain = LongJumpPlugin
) {

    private val mode by setting("Mode", Mode.NORMAL, description = "Normal -> Does a normal jump but farther, Fall -> Boosts momentum at the peak of your jump")
    private val speed by setting("Speed", 3.8,0.2..10.0,0.1)
    private val timer by setting("Timer", 1.088, 0.1..10.0,0.1, description = "Timer speed to use when LongJumping")

    private var fallen : Boolean = false

    private enum class Mode {NORMAL, FALL}

    private var ticksInAir : Int = 0

    init {

        safeListener<PlayerMoveEvent> {

            if (LagNotifier.paused)
                disable()

            if (player.onGround)
                ticksInAir = 0

            val yaw = calcMoveYaw()
            val dirX = -sin(yaw)
            val dirZ = cos(yaw)

            if (mode == Mode.NORMAL && (player.movementInput.moveForward != 0f || player.movementInput.moveStrafe != 0f)
                || mode == Mode.FALL && (player.motionY < 0 || player.onGround) && !fallen) {

                val thisSpeed = if (mode == Mode.NORMAL) speed * 0.91.pow(ticksInAir) else speed

                ticksInAir++

                if (player.onGround) {

                    player.jump()

                    if (mode == Mode.FALL)
                        return@safeListener

                }

                fallen = true
                it.x = dirX * thisSpeed
                it.z = dirZ * thisSpeed


            }

            if (!mc.player.onGround)
                modifyTimer(50f / timer.toFloat())
            else {

                ticksInAir = 0
                fallen = false

            }

        }

    }

}