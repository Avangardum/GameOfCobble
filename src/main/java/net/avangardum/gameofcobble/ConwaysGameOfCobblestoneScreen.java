package net.avangardum.gameofcobble;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

final class ConwaysGameOfCobblestoneScreen extends AbstractContainerScreen<ConwaysGameOfCobblestoneMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(
            ConwaysGameOfCobblestoneMod.MOD_ID, "textures/gui/conways_game_of_cobblestone_gui.png");

    public ConwaysGameOfCobblestoneScreen(ConwaysGameOfCobblestoneMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        var offScreenLabelY = 10000;
        titleLabelY = offScreenLabelY;
        inventoryLabelY = offScreenLabelY;
        imageWidth = 193;
        imageHeight = 215;
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
