/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.scene.menu

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.scene.FXGLMenu
import com.almasb.fxgl.scene.menu.MenuType
import com.almasb.fxgl.ui.FXGLButton
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.binding.StringBinding
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * Uses a similar design to Warcraft3 main menu.
 * API INCOMPLETE.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Warcraft3Menu(app: GameApplication, menuType: MenuType) : FXGLMenu(app, menuType) {

    private val menu: Node

    init {
        menu = if (type === MenuType.MAIN_MENU)
            createMenuBodyMainMenu()
        else
            createMenuBodyGameMenu()

//        val menuX = 50.0
//        val menuY = app.height / 2 - menu.getLayoutHeight() / 2
//
//        menuRoot.translateX = menuX
//        menuRoot.translateY = menuY
//
//        contentRoot.translateX = menuX * 2 + 200
//        contentRoot.translateY = menuY

        menuRoot.children.add(menu)
        contentRoot.children.add(EMPTY)

        activeProperty().addListener { observable, wasActive, isActive ->
            if (!isActive) {
                // the scene is no longer active so reset everything
                // so that next time scene is active everything is loaded properly
                switchMenuTo(menu)
                switchMenuContentTo(EMPTY)
            } else {
                playAnimation();
            }
        }
    }

    private fun playAnimation() {
        menu.translateY = -600.0

        val timeline = Timeline(
                KeyFrame(Duration.seconds(1.0), KeyValue(menu.translateYProperty(), height - 450, Interpolator.EASE_IN)),
                KeyFrame(Duration.seconds(2.0), KeyValue(menu.translateYProperty(), 50, Interpolator.EASE_OUT))
        )

        timeline.play()
    }

    private fun createMenuBodyMainMenu(): Node {

        val outerBorder = Rectangle(300.0, 450.0, Color.color(0.5, 0.5, 0.5, 0.5))
        with(outerBorder) {
            strokeWidth = 12.0
            stroke = Color.color(0.2, 0.2, 0.2, 0.7)
            strokeLineCap = StrokeLineCap.BUTT
            strokeLineJoin = StrokeLineJoin.ROUND
            strokeType = StrokeType.OUTSIDE
        }

        val itemContinue = createActionButton(FXGL.localizedStringProperty("menu.continue"), { fireContinue() })
        val itemNewGame = createActionButton(FXGL.localizedStringProperty("menu.newGame"), { fireNewGame() })
        val itemExit = createActionButton(FXGL.localizedStringProperty("menu.exit"), { fireExit() })

        val vbox = VBox(10.0, itemContinue, itemNewGame, itemExit)
        vbox.alignment = Pos.CENTER

        return StackPane(outerBorder, vbox)
    }

    private fun createMenuBodyGameMenu(): Node {
        return Text("TODO")
    }

    override fun createActionButton(name: String, action: Runnable): Button {
        with(FXGLButton(name)) {
            onAction = EventHandler { action.run() }
            return this
        }
    }

    override fun createActionButton(name: StringBinding, action: Runnable): Button {
        val btn = FXGLButton(name.value)
        btn.textProperty().bind(name)
        with(btn) {
            onAction = EventHandler { action.run() }
            return this
        }
    }

    override fun createBackground(width: Double, height: Double): Node {
        return Rectangle(width, height, Color.RED)
    }

    override fun createTitleView(title: String): Node {
        return Text(title)
    }

    override fun createVersionView(version: String): Node {
        return Text(version)
    }

    override fun createProfileView(profileName: String): Node {
        return Text(profileName)
    }
}