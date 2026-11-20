package fibbware.coderot;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.lang.reflect.Field;

public class CoderotPreLaunch implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        try {
            // reflection hack to tell Fabric to use our custom MixinTransformer

            ClassLoader cl = FabricLauncherBase.getLauncher().getTargetClassLoader();
            Field delegateField = cl.getClass().getDeclaredField("delegate");
            delegateField.setAccessible(true);
            Object knotDelegate = delegateField.get(cl);

            Field transformerField = knotDelegate.getClass().getDeclaredField("mixinTransformer");
            transformerField.setAccessible(true);

            IMixinTransformer original = (IMixinTransformer) transformerField.get(knotDelegate);
            IMixinTransformer custom = new CustomMixinTransformer(original);

            transformerField.set(knotDelegate, custom);

            Coderot.LOGGER.info("MixinTransformer hack success");
        } catch (Exception e) {
            Coderot.LOGGER.error("MixinTransformer hack failure", e);
        }
    }
}
