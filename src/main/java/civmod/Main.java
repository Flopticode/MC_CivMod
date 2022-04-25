package civmod;

import civmod.custom.entity.ModEntityTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MOD_ID)
public class Main
{
	public static final String MOD_ID = "civmod";

    public Main()
    {
        ModEntityTypes.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }
}
