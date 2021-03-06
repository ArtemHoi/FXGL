/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.extra.entity.effect.EffectComponent;
import com.almasb.fxgl.extra.entity.effects.WobbleEffect;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(15 * 70);
        settings.setHeight(10 * 70);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).right();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).jump();
            }
        }, KeyCode.W);

        DSLKt.onKeyDown(KeyCode.F, "asd", () -> {
            player.getComponent(EffectComponent.class).startEffect(new WobbleEffect(Duration.seconds(3), 3, 7, Orientation.VERTICAL));
        });

        DSLKt.onKeyDown(KeyCode.G, "asd2", () -> {
            player.getComponent(EffectComponent.class).startEffect(new WobbleEffect(Duration.seconds(3), 2, 4, Orientation.HORIZONTAL));
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MarioFactory());
        getGameWorld().addEntityFactory(new MarioBlockFactory());
        getGameWorld().setLevelFromMap("mario1n.tmx");

        player = getGameWorld().spawn("player", 50, 50);

        getGameScene().getViewport().setBounds(0, 0, 15000, getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);

        getGameWorld().spawn("enemy", 470, 50);
        getGameWorld().spawn("block", new SpawnData(140, 70).put("crush.speed", 770).put("player", player));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.getComponent(CollidableComponent.class).setValue(false);

                Animation<?> anim = Entities.animationBuilder()
                        .duration(Duration.seconds(0.5))
                        .interpolator(Interpolators.ELASTIC.EASE_IN())
                        .scale(coin)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(0, 0))
                        .buildAndPlay();

                anim.setOnFinished(() -> coin.removeFromWorld());
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                getDisplay().showMessageBox("Level Complete!", () -> {
                    System.out.println("Dialog closed!");
                });
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
