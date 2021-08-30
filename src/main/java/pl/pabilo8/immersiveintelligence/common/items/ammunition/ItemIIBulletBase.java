package pl.pabilo8.immersiveintelligence.common.items.ammunition;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.client.ClientProxy;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces.ITextureOverride;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry.EnumComponentRole;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletRegistry.EnumCoreTypes;
import pl.pabilo8.immersiveintelligence.api.bullets.IBullet;
import pl.pabilo8.immersiveintelligence.api.bullets.IBulletComponent;
import pl.pabilo8.immersiveintelligence.api.bullets.IBulletCore;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.items.ItemIIBase;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Pabilo8
 * @since 2019-05-11
 */
public abstract class ItemIIBulletBase extends ItemIIBase implements IBullet, ITextureOverride
{
	public static final int BULLET = 0;
	public static final int CORE = 1;
	public final String NAME;

	public ItemIIBulletBase(String name, int stacksize)
	{
		super("bullet_"+name.toLowerCase(), stacksize, "bullet", "core");
		setMetaHidden(BULLET, CORE);
		this.NAME = name;
	}

	public void makeDefault(ItemStack stack)
	{
		if(!ItemNBTHelper.hasKey(stack, "core"))
			ItemNBTHelper.setString(stack, "core", "core_brass");
		if(!ItemNBTHelper.hasKey(stack, "core_type"))
			ItemNBTHelper.setString(stack, "core_type", getAllowedCoreTypes()[0].getName());
	}

	@Override
	public IBulletCore getCore(ItemStack stack)
	{
		if(!ItemNBTHelper.hasKey(stack, "core"))
			makeDefault(stack);
		return BulletRegistry.INSTANCE.getCore(ItemNBTHelper.getString(stack, "core"));
	}

	@Override
	public EnumCoreTypes getCoreType(ItemStack stack)
	{
		if(!ItemNBTHelper.hasKey(stack, "core_type"))
			makeDefault(stack);
		return EnumCoreTypes.v(ItemNBTHelper.getString(stack, "core_type"));
	}

	@Override
	public int getPaintColor(ItemStack stack)
	{
		if(ItemNBTHelper.hasKey(stack, "paint_color"))
			return ItemNBTHelper.getInt(stack, "paint_color");
		return -1;
	}

	@Override
	public void registerSprites(TextureMap map)
	{
		ApiUtils.getRegisterSprite(map, ImmersiveIntelligence.MODID+":items/bullets/ammo/"+getName().toLowerCase()+"/base");
		for(EnumCoreTypes coreType : getAllowedCoreTypes())
			ApiUtils.getRegisterSprite(map, ImmersiveIntelligence.MODID+":items/bullets/ammo/"+getName().toLowerCase()+"/"+coreType.getName());
		ApiUtils.getRegisterSprite(map, ImmersiveIntelligence.MODID+":items/bullets/ammo/"+getName().toLowerCase()+"/paint");
		ApiUtils.getRegisterSprite(map, ImmersiveIntelligence.MODID+":items/bullets/ammo/"+getName().toLowerCase()+"/core");
	}

	@Override
	public IBulletComponent[] getComponents(ItemStack stack)
	{
		if(ItemNBTHelper.hasKey(stack, "components"))
		{
			ArrayList<IBulletComponent> arrayList = new ArrayList<>();
			NBTTagList components = (NBTTagList)ItemNBTHelper.getTag(stack).getTag("components");
			for(int i = 0; i < components.tagCount(); i++)
				arrayList.add(BulletRegistry.INSTANCE.getComponent(components.getStringTagAt(i)));
			return arrayList.toArray(new IBulletComponent[0]);
		}
		return new IBulletComponent[0];
	}

	@Override
	public NBTTagCompound[] getComponentsNBT(ItemStack stack)
	{
		if(ItemNBTHelper.hasKey(stack, "component_nbt"))
		{
			ArrayList<NBTTagCompound> arrayList = new ArrayList<>();
			NBTTagList components = (NBTTagList)ItemNBTHelper.getTag(stack).getTag("component_nbt");
			for(int i = 0; i < components.tagCount(); i++)
				arrayList.add(components.getCompoundTagAt(i));
			return arrayList.toArray(new NBTTagCompound[0]);
		}
		return new NBTTagCompound[0];
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
	{
		super.onCreated(stack, worldIn, playerIn);
		makeDefault(stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		boolean b = stack.getMetadata()==BULLET;
		if(b)
		{
			tooltip.add(getFormattedBulletTypeName(stack));
			tooltip.add(I18n.format(CommonProxy.DESCRIPTION_KEY+"bullets.core",
					I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_core_type."+getCoreType(stack).getName()),
					I18n.format("item."+ImmersiveIntelligence.MODID+".bullet.component."+getCore(stack).getName()+".name")
			));
			tooltip.add(I18n.format(CommonProxy.DESCRIPTION_KEY+"bullets.mass", getMass(stack)));
			//tooltip.add(getPenetrationTable(stack));
		}
		tooltip.add(I18n.format(CommonProxy.DESCRIPTION_KEY+"bullets.caliber", Utils.formatDouble(getCaliber(),"#.#")));
	}

	private String getFormattedBulletTypeName(ItemStack stack)
	{
		Set<EnumComponentRole> collect = new HashSet<>();
		if(getCoreType(stack).getRole()!=null)
			collect.add(getCoreType(stack).getRole());
		collect.addAll(Arrays.stream(getComponents(stack)).map(IBulletComponent::getRole).collect(Collectors.toSet()));
		StringBuilder builder = new StringBuilder();
		for(EnumComponentRole enumComponentRole : collect)
		{
			if(enumComponentRole==EnumComponentRole.GENERAL_PURPOSE)
				continue;
			builder.append(I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_type."+enumComponentRole.getName()));
			builder.append(" - ");
		}
		if(builder.toString().isEmpty())
		{
			builder.append(I18n.format(CommonProxy.DESCRIPTION_KEY+"bullet_type."+EnumComponentRole.GENERAL_PURPOSE.getName()));
			builder.append(" - ");
		}
		String s = builder.toString();
		return I18n.format(s.substring(0, Math.max(s.length()-3, 0)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack)
	{
		switch(stack.getMetadata())
		{
			case BULLET:
				return I18n.format("item.immersiveintelligence."+NAME+".bullet.name");
			case CORE:
				return I18n.format("item.immersiveintelligence."+NAME+".core.name");
		}
		return "DO NOT USE, MAY CRASH";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		switch(stack.getMetadata())
		{
			case BULLET:
			{
				switch(pass)
				{
					case 0:
						return 0xffffffff;
					case 1:
						return getCore(stack).getColour();
					case 2:
						return getPaintColor(stack);
				}
			}
			case CORE:
				return getCore(stack).getColour();
		}
		return 0xffffffff;
	}

	public ItemStack getBulletWithParams(String core, String coreType, String... components)
	{
		ItemStack stack = new ItemStack(this, 1, BULLET);
		ItemNBTHelper.setString(stack, "core", core);
		ItemNBTHelper.setString(stack, "core_type", coreType);
		NBTTagList tagList = new NBTTagList();
		Arrays.stream(components).map(NBTTagString::new).forEachOrdered(tagList::appendTag);

		if(tagList.tagCount() > 0)
		{
			ItemNBTHelper.getTag(stack).setTag("components", tagList);
			NBTTagList nbt = new NBTTagList();
			for(int i = 0; i < tagList.tagCount(); i += 1)
				nbt.appendTag(new NBTTagCompound());

			ItemNBTHelper.getTag(stack).setTag("component_nbt", nbt);
		}

		return stack;
	}

	@Override
	public ItemStack setPaintColour(ItemStack stack, int color)
	{
		ItemNBTHelper.setInt(stack, "paint_color", color);
		return stack;
	}

	@Override
	public ItemStack setComponentNBT(ItemStack stack, NBTTagCompound... tagCompounds)
	{
		NBTTagList component_nbt = new NBTTagList();
		for(NBTTagCompound tagCompound : tagCompounds)
			component_nbt.appendTag(tagCompound);
		assert stack.getTagCompound()!=null;
		stack.getTagCompound().setTag("component_nbt", component_nbt);
		return stack;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack stack)
	{
		return ClientProxy.itemFont;
	}

	@Override
	public String getModelCacheKey(ItemStack stack)
	{
		return NAME+"_"+(getPaintColor(stack)==-1?"no_": "paint_")+getCoreType(stack).getName();
	}

	@Override
	public List<ResourceLocation> getTextures(ItemStack stack, String key)
	{
		ArrayList<ResourceLocation> a = new ArrayList<>();
		if(stack.getMetadata()==BULLET)
		{
			a.add(new ResourceLocation(ImmersiveIntelligence.MODID+":items/bullets/ammo/"+NAME.toLowerCase()+"/base"));
			a.add(new ResourceLocation(ImmersiveIntelligence.MODID+":items/bullets/ammo/"+NAME.toLowerCase()+"/"+getCoreType(stack).getName()));
			if(getPaintColor(stack)!=-1)
				a.add(new ResourceLocation(ImmersiveIntelligence.MODID+":items/bullets/ammo/"+NAME.toLowerCase()+"/paint"));

		}
		else if(stack.getMetadata()==CORE)
		{
			a.add(new ResourceLocation(ImmersiveIntelligence.MODID+":items/bullets/ammo/"+NAME.toLowerCase()+"/core"));
			a.add(new ResourceLocation(ImmersiveIntelligence.MODID+":items/bullets/ammo/"+NAME.toLowerCase()+"/"+getCoreType(stack).getName()));
		}
		return a;
	}
}
