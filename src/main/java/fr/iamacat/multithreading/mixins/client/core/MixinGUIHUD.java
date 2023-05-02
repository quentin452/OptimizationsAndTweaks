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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
    private final LinkedList<BatchedText> renderQueue = new LinkedList<>();
    private final Lock renderQueueLock = new ReentrantLock();
    private Map<String, Integer> cachedStrings = new HashMap<>();

    // private final Map<String, CachedBatch> cachedBatches = new HashMap<String, CachedBatch>();
    private boolean scissorTestEnabled = false;

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

        // add text to render queue
        renderQueueLock.lock();
        try {
            renderQueue.add(new BatchedText("Hello, world!", 100, 100, 0xFFFFFF, 0x000000));
        } finally {
            renderQueueLock.unlock();
        }
    }

    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void onRenderGameOverlayPost(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinGUIHUD) {
            // draw cached strings
            ScaledResolution sr = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
            int scaleFactor = sr.getScaleFactor();
            int prevWidth = GL11.glGetInteger(GL11.GL_SCISSOR_BOX);

            if (!scissorTestEnabled) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                scissorTestEnabled = true;
            }
            GL11.glPushMatrix();
            GL11.glScaled(scaleFactor, scaleFactor, 1.0D);

            for (BatchedText batchedText : renderQueue) {
                int color = batchedText.color;
                String text = batchedText.text;
                int x = batchedText.x;
                int y = batchedText.y;

                // check if the string is already cached
                Integer stringWidth = cachedStrings.get(text);
                if (stringWidth == null) {
                    // cache the string if it's not already cached
                    stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
                    cachedStrings.put(text, stringWidth);
                }

                // draw the string
                Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);

                // clear the cached strings if the render queue is empty
                if (renderQueue.isEmpty()) {
                    cachedStrings.clear();
                }
            }

            GL11.glPopMatrix();

            if (scissorTestEnabled) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                scissorTestEnabled = false;
            }

            GL11.glScissor(0, 0, prevWidth, Minecraft.getMinecraft().displayHeight * scaleFactor);
        }
    }

    private void drawLoop() {
        try {
            while (!Thread.currentThread()
                .isInterrupted()) {
                BatchedText[] batch = new BatchedText[BATCH_SIZE];
                int count = 0;
                renderQueueLock.lock();
                try {
                    while (!renderQueue.isEmpty() && count < BATCH_SIZE) {
                        batch[count++] = renderQueue.poll();
                    }
                } finally {
                    renderQueueLock.unlock();
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
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer fontRenderer = mc.fontRenderer;
            int scaleFactor = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
            GL11.glPushMatrix();
            GL11.glScaled(scaleFactor, scaleFactor, 1.0D);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                stringBuilder.append(batch[i].text);
                if (i != count - 1) {
                    stringBuilder.append('\n');
                }
            }

            int x = batch[0].x;
            int y = batch[0].y;
            int color = batch[0].color;
            int backgroundColor = batch[0].backgroundColor;
            int stringWidth = fontRenderer.getStringWidth(stringBuilder.toString());

            if (backgroundColor != -1) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(
                    (float) ((backgroundColor >> 16) & 0xFF) / 255f,
                    (float) ((backgroundColor >> 8) & 0xFF) / 255f,
                    (float) (backgroundColor & 0xFF) / 255f,
                    (float) ((backgroundColor >> 24) & 0xFF) / 255f);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(x - 2, y - 2);
                GL11.glVertex2f(x + stringWidth + 2, y - 2);
                GL11.glVertex2f(x + stringWidth + 2, y + fontRenderer.FONT_HEIGHT + 2);
                GL11.glVertex2f(x - 2, y + fontRenderer.FONT_HEIGHT + 2);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            fontRenderer.drawSplitString(stringBuilder.toString(), x, y, Minecraft.getMinecraft().displayWidth, color);

            GL11.glPopMatrix();
        }
    }
}
