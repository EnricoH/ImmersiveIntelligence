package pl.pabilo8.immersiveintelligence.common.blocks.metal;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.TileEntityImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockOverlayText;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
import blusunrize.immersiveengineering.common.util.network.MessageNoSpamChatComponents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.data.DataPacket;
import pl.pabilo8.immersiveintelligence.api.data.DataWireNetwork;
import pl.pabilo8.immersiveintelligence.api.data.IDataConnector;
import pl.pabilo8.immersiveintelligence.api.data.types.DataPacketTypeString;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.wire.IIDataWireType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Pabilo8
 * @since 11-06-2019
 */
public class TileEntityDataDebugger extends TileEntityImmersiveConnectable implements ITickable, IDataConnector, IHammerInteraction, IDirectionalTile, IOBJModelCallback<IBlockState>, IBlockOverlayText
{
	private boolean toggle = false;
	public int mode = 0;
	EnumFacing facing = EnumFacing.NORTH;
	//Purely decorational, client only
	public int setupTime = 25;
	protected DataWireNetwork wireNetwork = new DataWireNetwork().add(this);
	private boolean refreshWireNetwork = false;
	private DataPacket lastPacket = null;
	private String[] packetString = new String[0];

	@Override
	public void update()
	{
		if(hasWorld()&&!world.isRemote&&!refreshWireNetwork)
		{
			refreshWireNetwork = true;
			wireNetwork.removeFromNetwork(null);
		}

		if(world.isRemote&&setupTime > 0)
			setupTime -= 1;
		else if(!world.isRemote&&mode < 2)
			if(world.getStrongPower(getPos()) > 0&&!toggle)
			{
				toggle = true;
				DataPacket pack = new DataPacket();
				pack.setVariable('a', new DataPacketTypeString("Hello World!"));
				this.getDataNetwork().sendPacket(pack, this);
			}
			else if(world.getStrongPower(getPos())==0&&toggle)
			{
				toggle = false;
			}
	}


	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		mode = nbt.getInteger("mode");
		if(nbt.hasKey("noSetup"))
			setupTime = 0;
		setFacing(EnumFacing.getFront(nbt.getInteger("facing")));
		if(nbt.hasKey("packet"))
		{
			this.lastPacket = new DataPacket();
			this.lastPacket.fromNBT(nbt.getCompoundTag("packet"));
			if(world.isRemote)
				this.packetString = compilePacketString();
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket)
	{
		nbt.setInteger("mode", mode);
		if(setupTime < 25)
			nbt.setBoolean("noSetup", true);
		nbt.setInteger("facing", facing.ordinal());

		if(this.lastPacket!=null)
		{
			if(!world.isRemote)
				this.packetString = compilePacketString();
			nbt.setTag("packet", this.lastPacket.toNBT());
		}
	}

	// TODO: 21.12.2021 make it use hexcol
	private String[] compilePacketString()
	{
		//gets variables in format l:{Value:0}
		return lastPacket.variables.entrySet().stream()
				/*map(entry -> String.format("<hexcol=%s:%s> %s = %s",
						String.format("%06X", entry.getValue().getTypeColour()),*/
				.map(entry -> {
					TextFormatting ff = TextFormatting.getValueByName(Utils.getRGBTextFormatting(entry.getValue().getTypeColour()).getName());
					if(ff==TextFormatting.BLACK)
						ff = TextFormatting.DARK_GRAY;
					return String.format("%s%s§r %s = %s",
							ff,
							entry.getValue().getName(),
							entry.getKey(),
							entry.getValue().valueToString()
					);
				})
				.toArray(String[]::new);
	}

	@Override
	public boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking())
		{
			mode += 1;
			if(mode > 2)
				mode = 0;
			ImmersiveEngineering.packetHandler.sendTo(new MessageNoSpamChatComponents(new TextComponentTranslation(CommonProxy.INFO_KEY+"debugger_mode", new TextComponentTranslation(CommonProxy.INFO_KEY+"debugger_mode."+mode))), ((EntityPlayerMP)player));
			markDirty();
			markBlockForUpdate(pos, null);
		}
		return true;
	}

	@Override
	public EnumFacing getFacing()
	{
		return facing;
	}

	@Override
	public void setFacing(EnumFacing facing)
	{
		if(facing.getAxis().isHorizontal())
			this.facing = facing;
		else
			this.facing = EnumFacing.NORTH;
	}

	@Override
	public int getFacingLimitation()
	{
		return 2;
	}

	@Override
	public boolean mirrorFacingOnPlacement(EntityLivingBase placer)
	{
		return true;
	}

	@Override
	public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity)
	{
		return !entity.isSneaking();
	}

	@Override
	public boolean canRotate(EnumFacing axis)
	{
		return true;
	}

	@Override
	public void setDataNetwork(DataWireNetwork net)
	{
		wireNetwork = net;
	}

	@Override
	public DataWireNetwork getDataNetwork()
	{
		return wireNetwork;
	}

	@Override
	public void onDataChange()
	{
		if(!isInvalid())
		{
			markDirty();
			IBlockState stateHere = world.getBlockState(pos);
			markContainingBlockForUpdate(stateHere);
			markBlockForUpdate(pos.offset(facing), stateHere);
		}
	}

	@Override
	public World getConnectorWorld()
	{
		return getWorld();
	}

	@Override
	public void onPacketReceive(DataPacket packet)
	{
		if(this.mode==0||mode==2)
		{
			this.lastPacket = packet;
			ImmersiveEngineering.packetHandler.sendToAllAround(new MessageNoSpamChatComponents(new TextComponentString(packet.toString())), Utils.targetPointFromTile(this, 8));
			markDirty();
			markBlockForUpdate(this.pos, null);
		}
	}

	@Override
	public void sendPacket(DataPacket packet)
	{

	}

	@Override
	protected boolean isRelay()
	{
		return true;
	}

	@Override
	public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
	{
		if(cableType!=IIDataWireType.DATA)
			return false;
		return limitType==null||limitType==cableType;
	}

	@Override
	public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
	{
		super.connectCable(cableType, target, other);
		DataWireNetwork.updateConnectors(pos, world, wireNetwork);
	}

	@Override
	public void removeCable(@Nullable ImmersiveNetHandler.Connection connection)
	{
		super.removeCable(connection);
		wireNetwork.removeFromNetwork(this);
	}

	@Override
	public Vec3d getConnectionOffset(Connection con)
	{
		EnumFacing side = facing.getOpposite();
		double conRadius = con.cableType.getRenderDiameter()/2;
		return new Vec3d(.5+side.getFrontOffsetX()*0.8*(.5-conRadius), 0.95, .5+side.getFrontOffsetZ()*0.8*(.5-conRadius));
	}

	@Override
	public String[] getOverlayText(EntityPlayer player, RayTraceResult mop, boolean hammer)
	{
		String s_out = I18n.format(CommonProxy.INFO_KEY+"debugger_mode", I18n.format(CommonProxy.INFO_KEY+"debugger_mode."+mode));
		if(lastPacket!=null)
		{
			ArrayList<String> s = new ArrayList<>(Arrays.asList(this.packetString));
			s.add(0, s_out);
			return s.toArray(new String[0]);
		}
		return new String[]{s_out};
	}

	@Override
	public boolean useNixieFont(EntityPlayer player, RayTraceResult mop)
	{
		return false;
	}
}
