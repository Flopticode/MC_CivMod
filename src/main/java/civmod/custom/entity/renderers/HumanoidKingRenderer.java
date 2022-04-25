package civmod.custom.entity.renderers;

import civmod.Main;
import civmod.custom.entity.HumanoidKingEntity;
import civmod.custom.entity.models.HumanoidKingModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HumanoidKingRenderer extends MobRenderer<HumanoidKingEntity, HumanoidKingModel<HumanoidKingEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/entity/humanoid.png");
	protected static final Float SHADOW_SIZE = 0.3F;
	
	public HumanoidKingRenderer(Context context)
	{
		super(context, new HumanoidKingModel<HumanoidKingEntity>(context.bakeLayer(HumanoidKingModel.LAYER_LOCATION)), SHADOW_SIZE);
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidKingEntity p_114482_)
	{
		return TEXTURE;
	}

}
