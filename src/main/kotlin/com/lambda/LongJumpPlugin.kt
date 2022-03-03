package com.lambda

import com.lambda.client.plugin.api.Plugin
import com.lambda.modules.LongJump

internal object LongJumpPlugin : Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(LongJump())
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}