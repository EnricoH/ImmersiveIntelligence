package pl.pabilo8.immersiveintelligence.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry;
import pl.pabilo8.immersiveintelligence.client.model.IBulletModel;
import pl.pabilo8.immersiveintelligence.common.entity.bullets.EntityBullet;

/**
 * @author Pabilo8
 * @since 08-06-2019
 */
public class BulletRenderer extends Render<EntityBullet>
{

	public BulletRenderer(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	public void doRender(EntityBullet entity, double x, double y, double z, float entityYaw, float partialTicks)
	{

		GlStateManager.pushMatrix();

		double yy, pp;
		yy = entity.prevRotationYaw+((entity.rotationYaw-entity.prevRotationYaw)*partialTicks);
		pp = entity.prevRotationPitch+((entity.prevRotationPitch-entity.prevRotationPitch)*partialTicks);

		GlStateManager.translate(x, y, z);
		GlStateManager.rotate((float)yy, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(90+pp), 1.0F, 0.0F, 0.0F);

		if(entity.bulletCasing!=null)
		{
			int coreColor = entity.bulletCore.getColour();
			IBulletModel model = BulletRegistry.INSTANCE.registeredModels.get(entity.bulletCasing.getName());

			model.renderBulletUsed(coreColor, entity.bulletCoreType, entity.paintColor);
		}

		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public boolean isMultipass()
	{
		return false;
	}


	@Override
	protected ResourceLocation getEntityTexture(EntityBullet entity)
	{
		String texture = ImmersiveIntelligence.MODID+":textures/entity/bullet.png";
		return new ResourceLocation(texture);
	}
}
