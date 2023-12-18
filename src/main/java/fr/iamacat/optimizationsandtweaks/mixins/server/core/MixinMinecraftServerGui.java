package fr.iamacat.optimizationsandtweaks.mixins.server.core;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.MinecraftServerGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServerGui.class)
public class MixinMinecraftServerGui {

    // todo avoid the usage of Tread.sleep
    @Unique
    private static CountDownLatch optimizationsAndTweaks$latch = new CountDownLatch(1);
    @Shadow
    private static final Font field_164249_a = new Font("Monospaced", 0, 12);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void createServerGui(final DedicatedServer serverIn) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            ;
        }

        MinecraftServerGui minecraftservergui = new MinecraftServerGui(serverIn);
        JFrame jframe = new JFrame("Minecraft server");
        jframe.add(minecraftservergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component) null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent p_windowClosing_1_) {
                serverIn.initiateShutdown();

                while (!serverIn.isServerStopped()) {
                    long startTime = System.currentTimeMillis();
                    long delay = 100L;

                    while (System.currentTimeMillis() - startTime < delay) {
                        if (serverIn.isServerStopped()) {
                            break;
                        }
                    }
                }

                System.exit(0);
            }
        });
        optimizationsAndTweaks$latch.countDown();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void func_164247_a(final JTextArea p_164247_1_, final JScrollPane p_164247_2_, final String p_164247_3_) {
        try {
            optimizationsAndTweaks$latch.await();
        } catch (InterruptedException e) {}
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> func_164247_a(p_164247_1_, p_164247_2_, p_164247_3_));
        } else {
            Document document = p_164247_1_.getDocument();
            JScrollBar jscrollbar = p_164247_2_.getVerticalScrollBar();
            boolean flag = false;

            if (p_164247_2_.getViewport()
                .getView() == p_164247_1_) {
                flag = (double) jscrollbar.getValue() + jscrollbar.getSize()
                    .getHeight() + (double) (field_164249_a.getSize() * 4) > (double) jscrollbar.getMaximum();
            }

            try {
                document.insertString(document.getLength(), p_164247_3_, (AttributeSet) null);
            } catch (BadLocationException badlocationexception) {
                ;
            }

            if (flag) {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }
        }
    }
}
