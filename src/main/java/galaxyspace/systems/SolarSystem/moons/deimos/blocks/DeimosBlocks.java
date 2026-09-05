package galaxyspace.systems.SolarSystem.moons.deimos.blocks;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DeimosBlocks extends Block implements ITerraformableBlock, IDetectableResource, ISortableBlock{

	public static final PropertyEnum<EnumDeimosBlocks> BASIC_TYPE = PropertyEnum.create("type", EnumDeimosBlocks.class);

	public DeimosBlocks()
    {
        super(Material.ROCK);
        this.setTranslationKey("deimosblocks");
        this.setSoundType(SoundType.STONE); 
        this.setHardness(1.0F);
        this.setHarvestLevel("shovel", 1, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_REGOLITE));
        this.setHarvestLevel("pickaxe", 2, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_STONE));
        this.setHarvestLevel("pickaxe", 2, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_IRON_ORE));
        this.setHarvestLevel("pickaxe", 3, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_METEORICIRON_ORE));
        this.setHarvestLevel("pickaxe", 3, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_DESH_ORE));
        this.setHarvestLevel("pickaxe", 2, this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.DEIMOS_NICKEL_ORE));
    }
	
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (EnumDeimosBlocks blockBasic : EnumDeimosBlocks.values())        
            list.add(new ItemStack(this, 1, blockBasic.getMeta()));
        
    }
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
	}
	
	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        switch (getMetaFromState(state))
        {
        	case 2:
        		return AsteroidsItems.basicItem;
        	case 3:
        		return GCItems.meteoricIronRaw;
        	case 5:
        		return MarsItems.marsItemBasic;
        	default:
        		return Item.getItemFromBlock(this);
        }
    }
	
	@Override
    public int damageDropped(IBlockState state)
    {
        switch (getMetaFromState(state))
        {
	        case 2:
	            return 3;
	        case 3:
	        case 5:
	            return 0;
	        default:
	            return getMetaFromState(state);
        }
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        int bonus = 0;
     
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(state, random, fortune))
        {
            int j = random.nextInt(fortune + 2) - 1;

            if (j < 0)
            {
                j = 0;
            }

            return this.quantityDropped(random) * (j + 1) + bonus;
        }
        else
        {
            return this.quantityDropped(random) + bonus;
        }
    }
	
	@Override
	public EnumSortCategoryBlock getCategory(int meta) {
		return EnumSortCategoryBlock.GENERAL;
	}
	
	@Override
	public boolean isValueable(IBlockState metadata) {
		return false;
	}

	@Override
	public boolean isTerraformable(World world, BlockPos pos) {
		return false;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public enum EnumDeimosBlocks implements IStringSerializable
	{
		DEIMOS_REGOLITE(0, "deimos_regolite"),
		DEIMOS_STONE(1, "deimos_stone"),
		DEIMOS_IRON_ORE(2, "deimos_iron_ore"),
		DEIMOS_METEORICIRON_ORE(3, "deimos_meteoriciron_ore"),
		DEIMOS_NICKEL_ORE(4, "deimos_nickel_ore"),
		DEIMOS_DESH_ORE(5, "deimos_desh_ore");


		private final int meta;
		private final String name;

		private EnumDeimosBlocks(int meta, String name)
		{
			this.meta = meta;
			this.name = name;
		}

		public int getMeta() { return this.meta; }       

		private final static EnumDeimosBlocks[] values = values();
		public static EnumDeimosBlocks byMetadata(int meta) { return values[meta % values.length]; }

		@Override
		public String getName() { return this.name; }

	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(BASIC_TYPE, EnumDeimosBlocks.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumDeimosBlocks) state.getValue(BASIC_TYPE)).getMeta();
	}	

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, BASIC_TYPE);
	}
}