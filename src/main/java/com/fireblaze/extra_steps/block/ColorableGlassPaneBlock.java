package com.fireblaze.extra_steps.block;

import com.fireblaze.extra_steps.blockentity.ColorableGlassBlockEntity;
import com.fireblaze.extra_steps.blockentity.ColorableGlassPaneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColorableGlassPaneBlock extends ColorableBlock {

    // Facing Property für 4 Richtungen (N, S, E, W)
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    // Dünne VoxelShape (2 Pixel dick)
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(0, 0, 7, 16, 16, 9); // Z = 7-9
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(7, 0, 0, 9, 16, 16);   // X = 7-9

    public ColorableGlassPaneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorableGlassPaneBlockEntity(pos, state);
    }
    // State Definition registrieren
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // State beim Platzieren setzen
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // Rotation / Mirror Support
    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // 🔹 VoxelShape zurückgeben basierend auf FACING
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
            case EAST, WEST -> SHAPE_EAST_WEST;
            default -> Shapes.block(); // Fallback, sollte nie passieren
        };
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if(level.getBlockEntity(pos) instanceof ColorableGlassPaneBlockEntity be) {
            CompoundTag tag = stack.getTag();
            if (tag == null) return;

            if(stack.hasTag() && stack.getTag().contains("color")) {
                be.setColor(stack.getTag().getInt("color"));
            }

            if(stack.hasTag() && stack.getTag().contains("fillFactor")) {
                be.setFillFactor(stack.getTag().getFloat("fillFactor"));
            }

            for (int i = 0; i < 3; i++) {
                String key = "colorList" + i;

                if (tag.contains(key)) {
                    int[] arr = tag.getIntArray(key);
                    be.setColorList(i, arr);
                }
            }

        }
    }
}