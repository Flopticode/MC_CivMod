package civmod.custom.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

//Made with Blockbench 4.0.2
//Exported for Minecraft version 1.17 with Mojang mappings
//Paste this class into your mod and generate all required imports


public class HumanoidModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "humanoid"), "main");
	private final ModelPart bb_main;

	public HumanoidModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -13.0F, -2.5F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -7.5F, -1.0F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(3.0F, -11.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 4).addBox(-5.0F, -11.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, buffer, packedLight, packedOverlay);
	}
}