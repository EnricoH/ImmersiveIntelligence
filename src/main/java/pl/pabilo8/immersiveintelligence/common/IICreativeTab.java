package pl.pabilo8.immersiveintelligence.common;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.bullets.IBullet;
import pl.pabilo8.immersiveintelligence.api.crafting.PrecissionAssemblerRecipe;
import pl.pabilo8.immersiveintelligence.common.blocks.types.IIBlockTypes_MetalDecoration;
import pl.pabilo8.immersiveintelligence.common.items.ItemIIBulletMagazine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pabilo8
 * @since 2019-05-07
 */
public class IICreativeTab extends CreativeTabs
{
	public static List<Fluid> fluidBucketMap = new ArrayList<>();

	public IICreativeTab(String name)
	{
		super(name);
	}

	@Override
	public ItemStack getTabIconItem()
	{
		return new ItemStack(IIContent.blockMetalDecoration, 1, IIBlockTypes_MetalDecoration.COIL_DATA.ordinal());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> list)
	{
		super.displayAllRelevantItems(list);

		//addAssemblySchemes(list);
		addExampleBullets(list);

		for(Fluid fluid : fluidBucketMap)
			addFluidBucket(fluid, list);
	}

	public void addFluidBucket(Fluid fluid, NonNullList list)
	{
		UniversalBucket bucket = ForgeModContainer.getInstance().universalBucket;
		ItemStack stack = new ItemStack(bucket);
		FluidStack fs = new FluidStack(fluid, bucket.getCapacity());
		IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
		if(fluidHandler.fill(fs, true)==fs.amount)
		{
			list.add(fluidHandler.getContainer());
		}
	}

	public void addAssemblySchemes(NonNullList list)
	{
		for(PrecissionAssemblerRecipe recipe : PrecissionAssemblerRecipe.recipeList)
		{
			list.add(IIContent.itemAssemblyScheme.getStackForRecipe(recipe));
		}
	}

	public void addExampleBullets(NonNullList list)
	{
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_steel", "softpoint", "tnt"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_steel", "softpoint", "rdx"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_steel", "softpoint", "hmx"));

		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_tungsten", "piercing", "tnt"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_tungsten", "piercing", "rdx"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_tungsten", "piercing", "hmx"));

		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "tnt"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "rdx"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "hmx"));

		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "hmx", "white_phosphorus").setStackDisplayName("Phosphorgranate mk. 1"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "fluid_napalm").setStackDisplayName("Napalmpatrone mk. 1"));
		list.add(IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "nuke").setStackDisplayName("Geburtstagsgranate mk.1"));

		list.add(IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "tnt").setStackDisplayName("Stielhandgranate mk.1"));
		list.add(IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "white_phosphorus").setStackDisplayName("Phosphorhandgranate mk.1"));
		list.add(IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "rdx").setStackDisplayName("Sprenghandgranate mk.1"));
		list.add(IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "hmx").setStackDisplayName("Sprenghandgranate mk.2"));
		list.add(IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "gas_chlorine").setStackDisplayName("Chemhandgranate mk.1"));
		ItemStack grenade_firework = IIContent.itemGrenade.getBulletWithParams("core_brass", "canister", "firework").setStackDisplayName("Feuerwerkhandgranate mk.1");
		try
		{
			((NBTTagList)ItemNBTHelper.getTag(grenade_firework).getTag("component_nbt")).set(0, JsonToNBT.getTagFromJson("{Explosion:{Type:0b,Colors:[I;3887386]}}"));
		} catch(NBTException e)
		{
			e.printStackTrace();
		}

		list.add(IIContent.itemRailgunGrenade.getBulletWithParams("core_brass", "canister", "gas_chlorine"));
		list.add(IIContent.itemRailgunGrenade.getBulletWithParams("core_brass", "canister", "rdx"));

		list.add(grenade_firework);

		list.add(IIContent.itemAmmoRevolver.getBulletWithParams("core_brass", "canister", "white_phosphorus").setStackDisplayName("Flammpatrone mk.1"));
		list.add(IIContent.itemAmmoRevolver.getBulletWithParams("core_tungsten", "piercing").setStackDisplayName("Wolframpatrone mk.1"));
		ItemStack bullet_tracer = IIContent.itemAmmoRevolver.getBulletWithParams("core_brass", "canister", "tracer_powder").setStackDisplayName("Markierungspatrone mk.1");
		try
		{
			((NBTTagList)ItemNBTHelper.getTag(bullet_tracer).getTag("component_nbt")).set(0, JsonToNBT.getTagFromJson("{colour:3887386}"));
		} catch(NBTException e)
		{
			e.printStackTrace();
		}
		list.add(bullet_tracer);

		ItemStack bullet1 = IIContent.itemAmmoMachinegun.getBulletWithParams("core_brass", "softpoint", "tnt").setStackDisplayName("Sprengpatrone mk.1");
		ItemStack bullet2 = IIContent.itemAmmoMachinegun.getBulletWithParams("core_tungsten", "piercing", "shrapnel_tungsten").setStackDisplayName("Wolframpatrone mk.1");
		ItemStack bullet3 = IIContent.itemAmmoMachinegun.getBulletWithParams("core_steel", "piercing").setStackDisplayName("Stahlpatrone mk.1");
		ItemStack bullet4 = IIContent.itemAmmoMachinegun.getBulletWithParams("core_brass", "softpoint", "white_phosphorus").setStackDisplayName("Phosphorpatrone mk.1");
		list.add(bullet1);
		list.add(bullet2);
		list.add(bullet3);
		list.add(bullet4);
		list.add(ItemIIBulletMagazine.getMagazine("machinegun", bullet1, bullet2, bullet3, bullet4));

		ItemStack bullet5 = IIContent.itemAmmoMachinegun.getBulletWithParams("core_uranium", "piercing", "shrapnel_uranium").setStackDisplayName("M1A1 Uranium Bullet");
		list.add(bullet5);
		list.add(ItemIIBulletMagazine.getMagazine("machinegun", bullet5, bullet5, bullet5, bullet5));


		list.add(addColorBulletMagazine(IIContent.itemAmmoMachinegun, "machinegun", 65327, 16711680, 25343, 16772608));
		list.add(addColorBulletMagazine(IIContent.itemAmmoSubmachinegun, "submachinegun", 65327, 16711680, 25343, 16772608));
		list.add(addColorBulletMagazine(IIContent.itemAmmoSubmachinegun, "submachinegun_drum", 65327, 16711680, 25343, 16772608));

	}

	ItemStack addColorBulletMagazine(IBullet type, String magName, int... colors)
	{
		ArrayList<ItemStack> bullets = new ArrayList<>();
		for(int color : colors)
		{

			ItemStack stack = type.getBulletWithParams("core_brass", "softpoint", "flare_powder").setStackDisplayName(getGermanColorName(color)+"markierungspatrone");
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("colour", color);
			type.setComponentNBT(stack, tag);
			type.setPaintColour(stack, color);
			ItemNBTHelper.setInt(stack, "fuse", 2);
			bullets.add(stack);
		}

		ItemStack stack = new ItemStack(IIContent.itemBulletMagazine, 1, IIContent.itemBulletMagazine.getMetaBySubname(magName));
		NonNullList<ItemStack> l = NonNullList.withSize(ItemIIBulletMagazine.getBulletCapactity(stack), ItemStack.EMPTY);
		for(int i = 0; i < l.size(); i++)
		{
			l.set(i, bullets.get(i%(bullets.size())));
		}
		NBTTagList list = blusunrize.immersiveengineering.common.util.Utils.writeInventory(l);
		ItemNBTHelper.getTag(stack).setTag("bullets", list);
		ItemIIBulletMagazine.makeDefault(stack);
		return stack;
	}

	//Deutsche Qualität
	private String getGermanColorName(int color)
	{
		switch(Utils.getRGBTextFormatting(color))
		{
			case WHITE:
				return "Weiß";
			case ORANGE:
				return "Orange";
			case MAGENTA:
				return "Magenta";
			case LIGHT_BLUE:
				return "Hellblau";
			case YELLOW:
				return "Gelb";
			case LIME:
				return "Lime";
			case PINK:
				return "Rosa";
			case GRAY:
				return "Grau";
			case SILVER:
				return "Silber";
			case CYAN:
				return "Cyan";
			case PURPLE:
				return "Purpur";
			case BLUE:
				return "Blau";
			case BROWN:
				return "Braun";
			case GREEN:
				return "Grün";
			case RED:
				return "Rot";
			default:
			case BLACK:
				return "Schwarz";
		}
	}
}
