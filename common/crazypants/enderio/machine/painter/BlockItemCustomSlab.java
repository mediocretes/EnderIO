package crazypants.enderio.machine.painter;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemCustomSlab extends ItemSlab {

  private boolean isFullBlock;

  public BlockItemCustomSlab(int par1) {
    super(par1, EnderIO.blockCustomSlab, EnderIO.blockCustomDoubleSlab, par1 - 256 == ModObject.blockCustomDoubleSlab.id);
    setHasSubtypes(true);
    isFullBlock = par1 - 256 == ModObject.blockCustomDoubleSlab.id;
    setUnlocalizedName(ModObject.blockCustomSlab.unlocalisedName);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
    list.add(PainterUtil.getTooltTipText(item));
  }

  public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8,
      float par9, float par10) {

    if (this.isFullBlock) {
      return super.onItemUse(par1ItemStack, player, world, x, y, z, par7, par8, par9, par10);
    } else if (par1ItemStack.stackSize == 0) {
      return false;
    } else if (!player.canPlayerEdit(x, y, z, par7, par1ItemStack)) {
      return false;
    } else {
      int i1 = world.getBlockId(x, y, z);
      int j1 = world.getBlockMetadata(x, y, z);
      int k1 = j1 & 7;
      boolean flag = (j1 & 8) != 0;

      if ((par7 == 1 && !flag || par7 == 0 && flag) && i1 == EnderIO.blockCustomSlab.blockID && k1 == par1ItemStack.getItemDamage()) {

        if (world.checkNoEntityCollision(EnderIO.blockCustomDoubleSlab.getCollisionBoundingBoxFromPool(world, x, y, z))) {

          TileEntity te = world.getBlockTileEntity(x, y, z);   
          if(te instanceof TileEntityCustomSlab) {
            ((TileEntityCustomSlab)te).isConvertingToFullBlock = true;            
          } 
          
          if (world.setBlock(x, y, z, EnderIO.blockCustomDoubleSlab.blockID, k1, 3)) {
            
            te = world.getBlockTileEntity(x, y, z);
            if (te instanceof TileEntityCustomBlock) {
              int id = -1;
              Block b = PainterUtil.getSourceBlock(par1ItemStack);
              if (b != null) {
                id = b.blockID;
              }
              TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
              tef.setSourceBlockId(id);
              tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(par1ItemStack));
              world.markBlockForUpdate(x, y, z);
            }
            
            world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F),
                EnderIO.blockCustomDoubleSlab.stepSound.getPlaceSound(), (EnderIO.blockCustomDoubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                EnderIO.blockCustomDoubleSlab.stepSound.getPitch() * 0.8F);
            --par1ItemStack.stackSize;
          } else {
            if(te instanceof TileEntityCustomSlab) {
              ((TileEntityCustomSlab)te).isConvertingToFullBlock = false;
            }

          }
        }

        return true;
      } else {
        return super.onItemUse(par1ItemStack, player, world, x, y, z, par7, par8, par9, par10);
      }
    }
  }
}
