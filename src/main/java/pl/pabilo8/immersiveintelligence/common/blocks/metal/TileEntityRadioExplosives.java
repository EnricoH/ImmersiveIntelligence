package pl.pabilo8.immersiveintelligence.common.blocks.metal;

import blusunrize.immersiveengineering.api.DimensionBlockPos;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.*;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.Config.IIConfig.Weapons.Mines;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletHelper;
import pl.pabilo8.immersiveintelligence.api.bullets.IBullet;
import pl.pabilo8.immersiveintelligence.api.data.DataPacket;
import pl.pabilo8.immersiveintelligence.api.data.radio.IRadioDevice;
import pl.pabilo8.immersiveintelligence.api.data.radio.RadioNetwork;
import pl.pabilo8.immersiveintelligence.common.entity.bullets.EntityBullet;
import pl.pabilo8.immersiveintelligence.common.items.tools.ItemIITrenchShovel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pabilo8
 * @since 06.02.2021
 */
public class TileEntityRadioExplosives extends TileEntityIEBase implements IBlockBounds, ITileDrop, IPlayerInteraction, IDirectionalTile, IRadioDevice
{
	public int frequency = 0;
	public DataPacket programmedPacket = new DataPacket();
	public int coreColor = 0xffffff;
	public ItemStack mineStack = ItemStack.EMPTY;

	public EnumFacing facing = EnumFacing.NORTH;

	private boolean armed = true;

	@Override
	public void readCustomNBT(NBTTagCompound nbtTagCompound, boolean b)
	{
		armed = nbtTagCompound.getBoolean("armed");
		facing = EnumFacing.getFront(nbtTagCompound.getInteger("facing"));
		this.readOnPlacement(null, new ItemStack(nbtTagCompound.getCompoundTag("mineStack")));
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbtTagCompound, boolean b)
	{
		nbtTagCompound.setBoolean("armed", armed);
		nbtTagCompound.setInteger("facing", facing.getIndex());
		nbtTagCompound.setTag("mineStack", mineStack.serializeNBT());
		RadioNetwork.INSTANCE.addDevice(this);
	}

	public void explode()
	{
		if(!armed)
			return;

		RadioNetwork.INSTANCE.removeDevice(this);

		if(!world.isRemote&&mineStack.getItem() instanceof IBullet)
		{
			EntityBullet bullet = BulletHelper.createBullet(world, mineStack, new Vec3d(pos).addVector(0.5, 0.5, 0.5), new Vec3d(0, 0, 0), 1f);
			bullet.fuse = 1;
			world.spawnEntity(bullet);
		}
		world.setBlockToAir(this.getPos());
	}

	@Override
	public float[] getBlockBounds()
	{
		switch(facing)
		{
			case NORTH:
				return new float[]{1/16f, 2/16f, 0, 15/16f, 14/16f, 9/16f};
			case SOUTH:
				return new float[]{1/16f, 2/16f, 1-9/16f, 15/16f, 14/16f, 1};
			case EAST:
				return new float[]{1-9/16f, 2/16f, 1/16f, 1, 14/16f, 15/16f};
			case WEST:
				return new float[]{0, 2/16f, 1/16f, 9/16f, 14/16f, 15/16f};
			case UP:
				return new float[]{1/16f, 1-9/16f, 2/16f, 15/16f, 1, 14/16f};
			case DOWN:
				return new float[]{1/16f, 0, 2/16f, 15/16f, 9/16f, 14/16f};
		}
		return new float[]{0, 0, 0, 1, 1, 1};
	}

	@Override
	public boolean interact(EnumFacing side, EntityPlayer player, EnumHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ)
	{
		if(armed&&heldItem.getItem().getToolClasses(heldItem).contains(Lib.TOOL_WIRECUTTER))
		{
			heldItem.damageItem(8, player);
			world.playSound(pos.getX(), pos.getY()+1, pos.getZ(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1f, 1f, false);
			armed = false;
		}
		return false;
	}

	@Override
	public void readOnPlacement(EntityLivingBase placer, ItemStack stack)
	{
		Item item = stack.getItem();
		if(item instanceof IBullet)
		{
			this.mineStack = stack;
			this.coreColor = ((IBullet)item).getCore(stack).getColour();
			this.programmedPacket = new DataPacket().fromNBT(ItemNBTHelper.getTagCompound(stack, "programmed_data"));
		}
	}

	@Override
	public ItemStack getTileDrop(@Nullable EntityPlayer player, IBlockState state)
	{
		return mineStack;
	}

	@Override
	public NonNullList<ItemStack> getTileDrops(@Nullable EntityPlayer player, IBlockState state)
	{
		explode();
		return NonNullList.from(armed?ItemStack.EMPTY: mineStack);
	}

	@Override
	public EnumFacing getFacing()
	{
		return facing;
	}

	@Override
	public void setFacing(EnumFacing facing)
	{
		this.facing = facing;
	}

	@Override
	public int getFacingLimitation()
	{
		return 0;
	}

	@Override
	public boolean mirrorFacingOnPlacement(EntityLivingBase placer)
	{
		return true;
	}

	@Override
	public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity)
	{
		return false;
	}

	@Override
	public boolean canRotate(EnumFacing axis)
	{
		return false;
	}

	@Override
	public void onRadioSend(DataPacket packet)
	{

	}

	@Override
	public boolean onRadioReceive(DataPacket packet)
	{
		if(packet.equals(this.programmedPacket))
		{
			explode();
		}
		return false;
	}

	@Override
	public int getFrequency()
	{
		return frequency;
	}

	@Override
	public void setFrequency(int value)
	{
		this.frequency = value;
	}

	@Override
	public boolean isBasicRadio()
	{
		return true;
	}

	@Override
	public float getRange()
	{
		return Mines.radioRange;
	}

	@Override
	public float getWeatherRangeDecrease()
	{
		return (float)Mines.weatherHarshness;
	}

	@Override
	public DimensionBlockPos getDevicePosition()
	{
		return new DimensionBlockPos(this);
	}
}
