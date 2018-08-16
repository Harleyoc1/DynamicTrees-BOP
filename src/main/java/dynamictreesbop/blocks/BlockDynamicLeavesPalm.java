package dynamictreesbop.blocks;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.util.CoordUtils.Surround;

import dynamictreesbop.DynamicTreesBOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDynamicLeavesPalm extends BlockDynamicLeaves {
	
	public static final Surround hydroSurroundMap[][]  = new Surround[][] {
		{}, //Hydro 0
		{Surround.NE, Surround.SE, Surround.SW, Surround.NW}, //Hydro 1
		{Surround.N, Surround.E, Surround.S, Surround.W}, //Hydro 2
		{}, //Hydro 3
		{} //Hydro 4
	};
	
	public static final IUnlistedProperty<Boolean> CONNECTIONS[];
	
	static {
		CONNECTIONS = new Properties.PropertyAdapter[Surround.values().length];
		
		for (Surround surr : Surround.values()) {
			CONNECTIONS[surr.ordinal()] = new Properties.PropertyAdapter<Boolean>(PropertyBool.create("conn_" + surr.getName()));
			//System.out.println(CONNECTIONS[surr.ordinal()]);
		}
	}
	
	public BlockDynamicLeavesPalm() {
		setDefaultState(this.blockState.getBaseState().withProperty(HYDRO, 4));
		setRegistryName(DynamicTreesBOP.MODID, "leaves_palm");
		setUnlocalizedName("leaves_palm");
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {HYDRO, TREE}, CONNECTIONS);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos) {
		
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState extState = (IExtendedBlockState) state;
			for(Surround surr : hydroSurroundMap[state.getValue(BlockDynamicLeaves.HYDRO)]) {
				IBlockState scanState = access.getBlockState(pos.add(surr.getOffset()));
				if(scanState.getBlock() == this) {
					if( scanState.getValue(BlockDynamicLeaves.HYDRO) == 3 ) {
						extState = extState.withProperty(CONNECTIONS[surr.ordinal()], true);
					}
				}
			}
			
			return extState;
		}
		
		return state;
	}
	
	@Override
	public int getRadiusForConnection(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
		return side == EnumFacing.UP && from.getFamily().isCompatibleDynamicLeaves(blockState, blockAccess, pos) ? fromRadius : 0;
	}
	
	@Override
	public int branchSupport(IBlockState blockState, IBlockAccess blockAccess, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
		return branch.getFamily() == getFamily(blockState, blockAccess, pos) ? BlockBranch.setSupport(0, 1) : 0;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}
}
