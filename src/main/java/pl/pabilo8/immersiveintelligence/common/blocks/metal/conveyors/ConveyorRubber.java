package pl.pabilo8.immersiveintelligence.common.blocks.metal.conveyors;

import blusunrize.immersiveengineering.common.blocks.metal.conveyors.ConveyorBasic;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;

/**
 * @author Pabilo8
 * @since 29.04.2021
 */
public class ConveyorRubber extends ConveyorBasic
{
	public static ResourceLocation texture_on = new ResourceLocation(ImmersiveIntelligence.MODID+":blocks/conveyors/conveyor");
	public static ResourceLocation texture_off = new ResourceLocation(ImmersiveIntelligence.MODID+":blocks/conveyors/conveyor_off");

	@Override
	public ResourceLocation getActiveTexture()
	{
		return texture_on;
	}

	@Override
	public ResourceLocation getInactiveTexture()
	{
		return texture_off;
	}

	@Override
	public Vec3d getDirection(TileEntity conveyorTile, Entity entity, EnumFacing facing)
	{
		return super.getDirection(conveyorTile, entity, facing).scale(1.75);
	}
}
