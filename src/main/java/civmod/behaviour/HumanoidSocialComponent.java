package civmod.behaviour;

import civmod.custom.entity.HumanoidEntity;
import net.minecraft.world.entity.Entity;

public interface HumanoidSocialComponent
{
	public abstract void tick();
	public abstract Entity getEntity();
	public abstract void setEntity(HumanoidEntity entity);
}
