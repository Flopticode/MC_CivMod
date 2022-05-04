package civmod.custom.entity;

import civmod.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes
{
	public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Main.MOD_ID);
	
	public static final RegistryObject<EntityType<HumanoidEntity>> HUMANOID = ENTITY_TYPES.register(
			"humanoid", 
			()-> EntityType.Builder.of(HumanoidEntity::new, MobCategory.CREATURE).sized(0.5f, 0.9f).build(new ResourceLocation(Main.MOD_ID, "humanoid").toString()));
	
	public static final RegistryObject<EntityType<HumanoidKingEntity>> KING = ENTITY_TYPES.register(
			"king", 
			()-> EntityType.Builder.of(HumanoidKingEntity::new, MobCategory.CREATURE).sized(0.5f, 0.9f).build(new ResourceLocation(Main.MOD_ID, "king").toString()));
	
	
	public static void register(IEventBus eventBus)
	{
		ENTITY_TYPES.register(eventBus);
	}
}
