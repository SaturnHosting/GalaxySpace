package galaxyspace.systems.SolarSystem.moons.deimos.items;

import galaxyspace.core.prefab.items.GSItemBlockDesc;
import galaxyspace.systems.SolarSystem.moons.deimos.blocks.DeimosBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlocksDeimos extends GSItemBlockDesc 
{
    public ItemBlocksDeimos(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }
    
    @Override
    public String getTranslationKey(ItemStack is) {
    	
        int metadata = is.getItemDamage();

        return "tile." + DeimosBlocks.EnumDeimosBlocks.byMetadata(metadata).getName();
    }   
    
}