/*
 * FalseTweaks
 * Copyright (C) 2022 FalsePattern
 * All Rights Reserved
 * The above copyright notice, this permission notice and the word "SNEED"
 * shall be included in all copies or substantial portions of the Software.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.iamacat.multithreading.mixins.client.core;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.SharedThreadPool;
import fr.iamacat.multithreading.batching.BatchedText;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(GuiIngame.class)
public abstract class MixinGUIHUD {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final ConcurrentLinkedQueue<BatchedText> renderQueue = new ConcurrentLinkedQueue<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        SharedThreadPool.getExecutorService()
            .execute(this::drawLoop);
    }

    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void onRenderGameOverlay(CallbackInfo ci) {
        if (Minecraft.getMinecraft().ingameGUI.getChatGUI()
            .getChatOpen() || Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        renderQueue.add(new BatchedText(100, 100, "Hello, world!", 0xFFFFFF));
    }

    private synchronized void drawLoop() {
        try {
            while (!Thread.currentThread()
                .isInterrupted()) {
                BatchedText[] batch = new BatchedText[BATCH_SIZE];
                int count = 0;
                synchronized (renderQueue) {
                    Iterator<BatchedText> iterator = renderQueue.iterator();
                    while (iterator.hasNext() && count < BATCH_SIZE) {
                        batch[count++] = iterator.next();
                        iterator.remove();
                    }
                }
                if (count > 0) {
                    drawBatch(batch, count);
                }
                TimeUnit.MILLISECONDS.sleep(Math.max(0, 5000 - count * 50));
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();
        }
    }

    private void drawBatch(BatchedText[] batch, int count) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinGUIHUD) {
            ScaledResolution sr = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
            int scaleFactor = sr.getScaleFactor();
            int prevWidth = GL11.glGetInteger(GL11.GL_SCISSOR_BOX);
            boolean scissorTestEnabled = GL11.glGetBoolean(GL11.GL_SCISSOR_TEST);

            if (!scissorTestEnabled) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }
            GL11.glPushMatrix();
            GL11.glScaled(scaleFactor, scaleFactor, 1.0D);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                stringBuilder.append(batch[i].text);
                if (i != count - 1) {
                    stringBuilder.append('\n');
                }
            }
            Minecraft.getMinecraft().fontRenderer.drawSplitString(
                stringBuilder.toString(),
                batch[0].x,
                batch[0].y,
                Minecraft.getMinecraft().displayWidth,
                batch[0].color);

            GL11.glPopMatrix();

            if (!scissorTestEnabled) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            GL11.glScissor(0, 0, prevWidth, Minecraft.getMinecraft().displayHeight * scaleFactor);
        }
    }
}
