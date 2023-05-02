package fr.iamacat.multithreading.mixins.client.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.iamacat.multithreading.batching.BatchedText;
import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

@Mixin(GuiIngame.class)
public abstract class MixinGUIHUD {

    private static final int BATCH_SIZE = MultithreadingandtweaksMultithreadingConfig.batchsize;
    private final List<BatchedText> renderQueue = new LinkedList<>();
    private volatile boolean scissorTestEnabled = false;
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        MultithreadingandtweaksMultithreadingConfig.numberofcpus,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        new ThreadFactoryBuilder().setNameFormat("Chunk-Populator-%d").build());

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        executorService.execute(this::drawLoop);
    }

    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void onRenderGameOverlay(CallbackInfo ci) {
        if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen() || Minecraft.getMinecraft().currentScreen != null) {
            return;
        }

        // add text to render queue
        renderQueue.add(new BatchedText("Hello, world!", 100, 100, 0xFFFFFF, 0x000000));
    }

    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void onRenderGameOverlayPost(CallbackInfo ci) {
        if (!MultithreadingandtweaksMultithreadingConfig.enableMixinGUIHUD) {
            // draw cached strings
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int scaleFactor = sr.getScaleFactor();
            int prevWidth = GL11.glGetInteger(GL11.GL_SCISSOR_BOX);

            if (!scissorTestEnabled) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                scissorTestEnabled = true;
            }
            GL11.glPushMatrix();
            GL11.glScaled(scaleFactor, scaleFactor, 1.0D);

            synchronized (renderQueue) {
                if (!renderQueue.isEmpty()) {
                    int numBatches = (int) Math.ceil((double) renderQueue.size() / BATCH_SIZE);
                    for (int i = 0; i < numBatches; i++) {
                        int start = i * BATCH_SIZE;
                        int end = Math.min(start + BATCH_SIZE, renderQueue.size());
                        List<BatchedText> batch = renderQueue.subList(start, end);
                        drawBatch(batch.toArray(new BatchedText[batch.size()]), batch.size());
                    }
                    renderQueue.clear();
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
            while (!Thread.currentThread().isInterrupted()) {
                BatchedText[] batch = new BatchedText[BATCH_SIZE];
                int count = 0;

                while (count < BATCH_SIZE) {
                    BatchedText text = renderQueue.remove(0);
                    if (text == null) {
                        break; // queue is empty
                    }
                    batch[count++] = text;
                }

                if (count > 0) {
                    drawBatch(batch, count);
                }

                TimeUnit.MILLISECONDS.sleep(Math.max(0, 5000 - count * 50));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
