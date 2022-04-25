package civmod.custom.entity;

import civmod.Main;
import civmod.custom.entity.models.HumanoidKingModel;
import civmod.custom.entity.models.HumanoidModel;
import civmod.custom.entity.renderers.HumanoidKingRenderer;
import civmod.custom.entity.renderers.HumanoidRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid=Main.MOD_ID, bus=Bus.MOD)
public class CustomEntityRegistry
{
	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers evt)
    {
    	evt.registerEntityRenderer(ModEntityTypes.HUMANOID.get(), HumanoidRenderer::new);
    	evt.registerEntityRenderer(ModEntityTypes.KING.get(), HumanoidKingRenderer::new);
    }
    
	@SubscribeEvent
    public static void onRegisterLayer(EntityRenderersEvent.RegisterLayerDefinitions evt)
    {
    	evt.registerLayerDefinition(HumanoidModel.LAYER_LOCATION, HumanoidModel::createBodyLayer);
    	evt.registerLayerDefinition(HumanoidKingModel.LAYER_LOCATION, HumanoidKingModel::createBodyLayer);
    }
	
	@SubscribeEvent
	public static void addEntityAttributes(EntityAttributeCreationEvent evt)
	{
		evt.put(ModEntityTypes.HUMANOID.get(), HumanoidEntity.setCustomAttributes());
		evt.put(ModEntityTypes.KING.get(), HumanoidEntity.setCustomAttributes());
	}
	
	@SubscribeEvent
	public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> evt)
	{
		evt.getRegistry().register(ModEntityTypes.HUMANOID.get());
		evt.getRegistry().register(ModEntityTypes.KING.get());
	}
}
