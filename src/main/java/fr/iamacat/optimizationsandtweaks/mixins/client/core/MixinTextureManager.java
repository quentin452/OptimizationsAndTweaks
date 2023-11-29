package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Mixin(TextureManager.class)
public class MixinTextureManager implements ITickable, IResourceManagerReloadListener
{
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    private final Map mapTextureObjects = Maps.newHashMap();
    @Shadow
    private final Map mapResourceLocations = Maps.newHashMap();
    @Shadow
    private final List listTickables = Lists.newArrayList();
    @Shadow
    private final Map mapTextureCounters = Maps.newHashMap();
    @Shadow
    private IResourceManager theResourceManager;
    @Unique
    private final List<ITickable> orderedTickables = Lists.newArrayList();
    public MixinTextureManager(IResourceManager p_i1284_1_)
    {
        this.theResourceManager = p_i1284_1_;
    }
    @Shadow
    public ResourceLocation getResourceLocation(int p_130087_1_)
    {
        return (ResourceLocation)this.mapResourceLocations.get(Integer.valueOf(p_130087_1_));
    }
    @Shadow
    public boolean loadTextureMap(ResourceLocation p_130088_1_, TextureMap p_130088_2_)
    {
        if (this.loadTickableTexture(p_130088_1_, p_130088_2_))
        {
            this.mapResourceLocations.put(Integer.valueOf(p_130088_2_.getTextureType()), p_130088_1_);
            return true;
        }
        else
        {
            return false;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean loadTickableTexture(ResourceLocation p_110580_1_, ITickableTextureObject p_110580_2_) {
        if (this.loadTexture(p_110580_1_, p_110580_2_)) {
            this.orderedTickables.add(p_110580_2_);
            return true;
        } else {
            return false;
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean loadTexture(ResourceLocation p_110579_1_, final ITextureObject p_110579_2_)
    {
        boolean flag = true;
        ITextureObject p_110579_2_2 = p_110579_2_;

        try
        {
            p_110579_2_.loadTexture(this.theResourceManager);
        }
        catch (IOException ioexception)
        {
            logger.warn("Failed to load texture: " + p_110579_1_, ioexception);
            p_110579_2_2 = TextureUtil.missingTexture;
            this.mapTextureObjects.put(p_110579_1_, p_110579_2_2);
            flag = false;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", p_110579_1_);
            crashreportcategory.addCrashSectionCallable("Texture object class", () -> p_110579_2_.getClass().getName());
            throw new ReportedException(crashreport);
        }

        this.mapTextureObjects.put(p_110579_1_, p_110579_2_2);
        return flag;
    }
    @Shadow
    public ITextureObject getTexture(ResourceLocation p_110581_1_)
    {
        return (ITextureObject)this.mapTextureObjects.get(p_110581_1_);
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public ResourceLocation getDynamicTextureLocation(String p_110578_1_, DynamicTexture p_110578_2_)
    {
        Integer integer = (Integer)this.mapTextureCounters.get(p_110578_1_);

        if (integer == null)
        {
            integer = 1;
        }
        else
        {
            integer = integer + 1;
        }

        this.mapTextureCounters.put(p_110578_1_, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", p_110578_1_, integer));
        this.loadTexture(resourcelocation, p_110578_2_);
        return resourcelocation;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        for (ITickable tickable : this.orderedTickables) {
            tickable.tick();
        }
    }
    @Shadow
    public void deleteTexture(ResourceLocation p_147645_1_)
    {
        ITextureObject itextureobject = this.getTexture(p_147645_1_);

        if (itextureobject != null)
        {
            TextureUtil.deleteTexture(itextureobject.getGlTextureId());
        }
    }
    @Shadow
    public void onResourceManagerReload(IResourceManager p_110549_1_)
    {
        cpw.mods.fml.common.ProgressManager.ProgressBar bar = cpw.mods.fml.common.ProgressManager.push("Reloading Texture Manager", this.mapTextureObjects.keySet().size(), true);

        for (Object o : this.mapTextureObjects.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            bar.step(entry.getKey().toString());
            this.loadTexture((ResourceLocation) entry.getKey(), (ITextureObject) entry.getValue());
        }
        cpw.mods.fml.common.ProgressManager.pop(bar);
    }
}
