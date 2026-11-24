package fibbware.coderot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SavedWorldData extends SavedData {

    public String corruptorMode = "none";

    // timer mode
    public double timer_min = 0.9995;
    public double timer_max = 1.0005;
    public long timer_period = 30000L;

    // distance mode
    public double distance_scale = 1;

    public SavedWorldData() {
    }

    public SavedWorldData(String corruptorMode,

                          // timer mode
                          double timer_min,
                          double timer_max,
                          long timer_period,

                          // distance mode
                          double distance_scale

    ) {
        this.corruptorMode = corruptorMode;
        this.timer_min = timer_min;
        this.timer_max = timer_max;
        this.timer_period = timer_period;
        this.distance_scale = distance_scale;
    }

    public static final Codec<SavedWorldData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("corruptor_mode").forGetter(sd -> sd.corruptorMode),

                    // timer mode
                    Codec.DOUBLE.fieldOf("timer_min").forGetter(sd -> sd.timer_min),
                    Codec.DOUBLE.fieldOf("timer_max").forGetter(sd -> sd.timer_max),
                    Codec.LONG.fieldOf("timer_period").forGetter(sd -> sd.timer_period),

                    // distance mode
                    Codec.DOUBLE.fieldOf("distance_scale").forGetter(sd -> sd.distance_scale)
            ).apply(instance, SavedWorldData::new)
    );

    public static final SavedDataType<SavedWorldData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Coderot.MOD_ID, "coderot_data"),
            SavedWorldData::new,
            CODEC,
            null
    );

    public static SavedWorldData GetSavedWorldData(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }
}
