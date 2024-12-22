package net.avangardum.gameofcobble;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

class GameOfCobbleRenderer implements BlockEntityRenderer<GameOfCobbleBlockEntity> {
    private record RenderingContext(VertexConsumer vertexConsumer, PoseStack poseStack, int color) {}

    public GameOfCobbleRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(
        @NotNull GameOfCobbleBlockEntity blockEntity,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource buffer,
        int packedLight,
        int packedOverlay
    ) {
        var context = getRenderingContext(buffer, poseStack);
        poseStack.pushPose();
        var facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        applyCommonTransformations(poseStack, facing);
        var grid = blockEntity.getGrid();
        var quadSide = 1f / GameOfCobbleBlockEntity.GRID_SIDE;
        var firstQuadPosition = new Vector3f(1, 1, 0).mul(1 - quadSide / 2);

        for (var row = 0; row < GameOfCobbleBlockEntity.GRID_SIDE; row++) {
            for (var column = 0; column < GameOfCobbleBlockEntity.GRID_SIDE; column++) {
                if (!grid.isCellLiving(row, column)) continue;
                Vector3f position = new Vector3f();
                firstQuadPosition.sub(new Vector3f(column * quadSide, row * quadSide, 0), position);
                drawQuad(context, position, quadSide);
            }
        }

        poseStack.popPose();
    }

    private RenderingContext getRenderingContext(@NotNull MultiBufferSource buffer, @NotNull PoseStack poseStack) {
        var vertexConsumer = buffer.getBuffer(RenderType.debugQuads());
        var color = 0xFF000000;
        return new RenderingContext(vertexConsumer, poseStack, color);
    }

    private void applyCommonTransformations(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH:
                poseStack.translate(0, 0, -0.001);
                break;
            case EAST:
                poseStack.translate(1.001, 0, 0);
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
                break;
            case SOUTH:
                poseStack.translate(1, 0, 1.001);
                poseStack.mulPose(Axis.YN.rotationDegrees(180));
                break;
            case WEST:
                poseStack.translate(-0.001, 0, 1);
                poseStack.mulPose(Axis.YN.rotationDegrees(270));
                break;
        }
    }

    private void drawQuad(@NotNull RenderingContext context, Vector3f position, float side) {
        drawVertex(context, new Vector3f(position.x() - side / 2, position.y() - side / 2, position.z()));
        drawVertex(context, new Vector3f(position.x() - side / 2, position.y() + side / 2, position.z()));
        drawVertex(context, new Vector3f(position.x() + side / 2, position.y() + side / 2, position.z()));
        drawVertex(context, new Vector3f(position.x() + side / 2, position.y() - side / 2, position.z()));
    }

    private void drawVertex(@NotNull RenderingContext context, @NotNull Vector3f position) {
        context.vertexConsumer().vertex(context.poseStack().last().pose(), position.x(), position.y(), position.z())
                .color(context.color())
                .endVertex();
    }
}
