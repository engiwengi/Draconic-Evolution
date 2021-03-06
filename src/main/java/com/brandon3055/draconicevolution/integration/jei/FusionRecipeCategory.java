package com.brandon3055.draconicevolution.integration.jei;

import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.FusionUpgradeRecipe;
import com.brandon3055.draconicevolution.api.itemupgrade.IUpgradableItem;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 24/07/2016.
 */
public class FusionRecipeCategory extends BlankRecipeCategory { //TODO Fix animation in PI

    private final IDrawable background;
    private final String localizedName;
    private int xSize = 164;
    private int ySize = 111;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(ResourceHelperDE.getResource(DETextures.GUI_JEI_FUSION), 0, 0, xSize, ySize);
        localizedName = I18n.format("gui.de.fusionCraftingCore.name");
    }

    @Nonnull
    @Override
    public String getUid() {
        return RecipeCategoryUids.FUSION_CRAFTING;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) { //TODO JEI Update
        IFusionRecipe recipe = ((FusionRecipeWrapper) recipeWrapper).recipe;

        IGuiItemStackGroup stackGroup = recipeLayout.getItemStacks();
        stackGroup.init(0, true, xSize / 2 - 9, ySize / 2 - 9 - 23);
        stackGroup.init(1, false, xSize / 2 - 9, ySize / 2 - 9 + 23);

        //region Add Ingredients

        int centerX = xSize / 2;
        int centerY = ySize / 2;

        for (int i = 0; i < recipe.getRecipeIngredients().size(); i++) {
            boolean isLeft = i % 2 == 0;
            boolean isOdd = recipe.getRecipeIngredients().size() % 2 == 1;
            int sideCount = recipe.getRecipeIngredients().size() / 2;

            if (isOdd && !isLeft) {
                sideCount--;
            }

            int xPos;
            int yPos;

            if (isLeft) {
                xPos = centerX - 65;
                int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                int sideIndex = i / 2;

                if (sideCount <= 1 && (!isOdd || recipe.getRecipeIngredients().size() == 1)) {
                    sideIndex = 1;
                    ySize = 40;
                }

                yPos = centerY - 40 + (sideIndex * ySize);
            } else {
                xPos = centerX + 63;
                int ySize = 80 / Math.max(sideCount - (isOdd ? 0 : 1), 1);
                int sideIndex = i / 2;

                if (isOdd) {
                    sideCount++;
                }

                if (sideCount <= 1) {
                    sideIndex = 1;
                    ySize = 40;
                }

                yPos = centerY - 40 + (sideIndex * ySize);
            }

            stackGroup.init(i + 2, true, xPos - 8, yPos - 8);
        }

        stackGroup.set(ingredients);
        //endregion

        if (recipe instanceof FusionUpgradeRecipe) {
            FusionUpgradeRecipe fRecipe = (FusionUpgradeRecipe) recipe;
            List<ItemStack> inputs = new LinkedList<>();
            List<ItemStack> outputs = new LinkedList<>();

            for (ItemStack stack : DEJEIPlugin.iUpgradables) {
                if (stack != null && stack.getItem() instanceof IUpgradableItem) {
                    IUpgradableItem item = (IUpgradableItem) stack.getItem();
                    if (item.getValidUpgrades(stack).contains(fRecipe.upgrade) && item.getMaxUpgradeLevel(stack, fRecipe.upgrade) >= fRecipe.upgradeLevel) {
                        ItemStack input = stack.copy();
                        ItemStack output = stack.copy();
                        UpgradeHelper.setUpgradeLevel(input, fRecipe.upgrade, fRecipe.upgradeLevel - 1);
                        UpgradeHelper.setUpgradeLevel(output, fRecipe.upgrade, fRecipe.upgradeLevel);
                        inputs.add(input);
                        outputs.add(output);
                    }
                }
            }

            stackGroup.set(0, inputs);
            stackGroup.set(1, outputs);
        }
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        GuiHelper.drawBorderedRect(8, 6, 20, 100, 1, 0xFF000000, 0xFFAA00FF);
        GuiHelper.drawBorderedRect(xSize - 28, 6, 20, 100, 1, 0xFF000000, 0xFFAA00FF);
        GuiHelper.drawBorderedRect((xSize / 2) - 10, 22, 20, 66, 1, 0xFF000000, 0xFF00FFFF);
    }
}
