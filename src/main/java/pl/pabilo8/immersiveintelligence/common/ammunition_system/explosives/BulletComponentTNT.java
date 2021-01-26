package pl.pabilo8.immersiveintelligence.common.ammunition_system.explosives;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry.EnumComponentRole;
import pl.pabilo8.immersiveintelligence.api.bullets.IBulletComponent;
import pl.pabilo8.immersiveintelligence.common.entity.bullets.EntityBullet;
import pl.pabilo8.immersiveintelligence.common.util.IIExplosion;

/**
 * @author Pabilo8
 * @since 30-08-2019
 */
public class BulletComponentTNT implements IBulletComponent
{
	@Override
	public String getName()
	{
		return "tnt";
	}

	@Override
	public IngredientStack getMaterial()
	{
		return new IngredientStack("materialTNT");
	}

	@Override
	public float getDensity()
	{
		return 1f;
	}

	@Override
	public void onEffect(float amount, NBTTagCompound tag, World world, BlockPos pos, EntityBullet bullet)
	{
		IIExplosion e = new IIExplosion(world, bullet, bullet.posX, bullet.posY, bullet.posZ, 8*amount, 4, false, true);
		if(!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, e))
		{
			e.doExplosionA();
			e.doExplosionB(true);
		}
		//world.createExplosion(bullet, pos.getX(), pos.getY(), pos.getZ(), amount*8f, true);
	}

	@Override
	public EnumComponentRole getRole()
	{
		return EnumComponentRole.EXPLOSIVE;
	}

	@Override
	public int getColour()
	{
		return 0x282828;
	}
}
