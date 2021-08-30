package pl.pabilo8.immersiveintelligence.common.items.ammunition;

import net.minecraft.item.ItemStack;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry.EnumCoreTypes;
import pl.pabilo8.immersiveintelligence.client.fx.ParticleUtils;
import pl.pabilo8.immersiveintelligence.client.model.IBulletModel;
import pl.pabilo8.immersiveintelligence.client.model.bullet.ModelBullet3bCal;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.entity.bullets.EntityBullet;

import javax.annotation.Nonnull;

/**
 * @author Pabilo8
 * @since 30-08-2019
 */
public class ItemIIAmmoAutocannon extends ItemIIBulletBase
{
	public ItemIIAmmoAutocannon()
	{
		super("autocannon_3bCal", 24);
	}

	@Override
	public float getComponentMultiplier()
	{
		return 0.125f;
	}

	@Override
	public int getGunpowderNeeded()
	{
		return 7;
	}

	@Override
	public int getCoreMaterialNeeded()
	{
		return 6;
	}

	@Override
	public float getInitialMass()
	{
		return 0.185f;
	}

	@Override
	public float getDefaultVelocity()
	{
		return 6f;
	}

	@Override
	public float getCaliber()
	{
		return 3f;
	}

	@Override
	public @Nonnull Class<? extends IBulletModel> getModel()
	{
		return ModelBullet3bCal.class;
	}

	@Override
	public float getDamage()
	{
		return 10;
	}

	@Override
	public ItemStack getCasingStack(int amount)
	{
		return Utils.getStackWithMetaName(IIContent.itemAmmoCasing,"autocannon_3bcal",amount);
	}

	@Override
	public EnumCoreTypes[] getAllowedCoreTypes()
	{
		return new EnumCoreTypes[]{EnumCoreTypes.SOFTPOINT, EnumCoreTypes.PIERCING};
	}

	@Override
	public float getSupressionRadius()
	{
		return 2.5f;
	}

	@Override
	public int getSuppressionPower()
	{
		return 2;
	}
}
