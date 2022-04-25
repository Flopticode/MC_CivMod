package civmod.custom.entity.renderers;

import civmod.Main;
import civmod.custom.entity.HumanoidEntity;
import civmod.custom.entity.models.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HumanoidRenderer extends MobRenderer<HumanoidEntity, HumanoidModel<HumanoidEntity>>
{
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/entity/humanoid.png");
	protected static final Float SHADOW_SIZE = 0.3F;
	
	public HumanoidRenderer(Context context)
	{
		super(context, new HumanoidModel<HumanoidEntity>(context.bakeLayer(HumanoidModel.LAYER_LOCATION)), SHADOW_SIZE);
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidEntity p_114482_)
	{
		return TEXTURE;
	}

}
