package fr.iamacat.optimizationsandtweaks.mixins.common.core;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import fr.iamacat.optimizationsandtweaks.utils.optimizationsandtweaks.MainUtil;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

@Mixin(Main.class)
public class MixinMain {

    /**
     * @author 
     * @reason
     */
    @Overwrite
    public static void main(String[] p_main_0_)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec = optionparser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> argumentacceptingoptionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        ArgumentAcceptingOptionSpec<File> argumentacceptingoptionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        ArgumentAcceptingOptionSpec<File> argumentacceptingoptionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> argumentacceptingoptionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> argumentacceptingoptionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec10 = optionparser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec12 = optionparser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> argumentacceptingoptionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        ArgumentAcceptingOptionSpec<Integer> argumentacceptingoptionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec15 = optionparser.accepts("userProperties").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec16 = optionparser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec17 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        NonOptionArgumentSpec<String> nonoptionargumentspec = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(nonoptionargumentspec);
        String s = optionset.valueOf(argumentacceptingoptionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null)
        {
            try
            {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(argumentacceptingoptionspec6)));
            }
            catch (Exception ignored)
            {
            }
        }

        final String s1 = optionset.valueOf(argumentacceptingoptionspec7);
        final String s2 = optionset.valueOf(argumentacceptingoptionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && func_110121_a(s1) && func_110121_a(s2))
        {
            Authenticator.setDefault(new Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = optionset.valueOf(argumentacceptingoptionspec13);
        int j = optionset.valueOf(argumentacceptingoptionspec14);
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("demo");
        String s3 = optionset.valueOf(argumentacceptingoptionspec12);
        HashMultimap<Object, Object> hashmultimap = HashMultimap.create();

        for (Object o : ((Map) (new Gson()).fromJson(optionset.valueOf(argumentacceptingoptionspec15), MainUtil.field_152370_a)).entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            hashmultimap.putAll(entry.getKey(), (Iterable) entry.getValue());
        }

        File file2 = optionset.valueOf(argumentacceptingoptionspec2);
        File file3 = optionset.has(argumentacceptingoptionspec3) ? optionset.valueOf(argumentacceptingoptionspec3) : new File(file2, "assets/");
        File file1 = optionset.has(argumentacceptingoptionspec4) ? optionset.valueOf(argumentacceptingoptionspec4) : new File(file2, "resourcepacks/");
        String s4 = optionset.has(argumentacceptingoptionspec10) ? argumentacceptingoptionspec10.value(optionset) : argumentacceptingoptionspec9.value(optionset);
        String s5 = optionset.has(argumentacceptingoptionspec16) ? argumentacceptingoptionspec16.value(optionset) : null;
        Session session = new Session(argumentacceptingoptionspec9.value(optionset), s4, argumentacceptingoptionspec11.value(optionset), argumentacceptingoptionspec17.value(optionset));
        Minecraft minecraft = new Minecraft(session, i, j, flag, flag1, file2, file3, file1, proxy, s3, hashmultimap, s5);
        String s6 = optionset.valueOf(argumentacceptingoptionspec);

        if (s6 != null)
        {
            minecraft.setServer(s6, optionset.valueOf(argumentacceptingoptionspec1));
        }

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread")
        {
            @Override
            public void run()
            {
                Minecraft.stopIntegratedServer();
            }
        });

        if (!list.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + list);
        }

        Thread.currentThread().setName("Client thread");
        minecraft.run();
    }
    @Shadow
    private static boolean func_110121_a(String p_110121_0_)
    {
        return p_110121_0_ != null && !p_110121_0_.isEmpty();
    }
}
