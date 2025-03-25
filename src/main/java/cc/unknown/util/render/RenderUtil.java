package cc.unknown.util.render;

import static java.lang.Math.PI;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4ub;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import cc.unknown.util.Accessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public final class RenderUtil implements Accessor {

	private static final Frustum FRUSTUM = new Frustum();
	public static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
	
	public static void drawRect(double left, double top, double right, double bottom, int color) {
		double j;
		if (left < right) {
			j = left;
			left = right;
			right = j;
		}

		if (top < bottom) {
			j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0).endVertex();
		worldrenderer.pos(right, bottom, 0.0).endVertex();
		worldrenderer.pos(right, top, 0.0).endVertex();
		worldrenderer.pos(left, top, 0.0).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public static void roundedRect(final double x, final double y, final double width, final double height,
			final double radius, final int color) {
		drawRoundedRect(x, y, width - x, height - y, radius, color);
	}

	private static void drawRoundedRect(double x, double y, final double width, final double height, final double radius,
			final int color) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		double x2 = x + width;
		double y2 = y + height;
		final float f = (color >> 24 & 0xFF) / 255.0f;
		final float f2 = (color >> 16 & 0xFF) / 255.0f;
		final float f3 = (color >> 8 & 0xFF) / 255.0f;
		final float f4 = (color & 0xFF) / 255.0f;
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);
		x *= 2.0;
		y *= 2.0;
		x2 *= 2.0;
		y2 *= 2.0;
		GL11.glDisable(3553);
		GL11.glColor4f(f2, f3, f4, f);
		GL11.glEnable(2848);
		GL11.glBegin(9);
		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0),
					y + radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
		}
		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0) * (radius * -1.0),
					y2 - radius + Math.cos(i * Math.PI / 180.0) * (radius * -1.0));
		}
		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius,
					y2 - radius + Math.cos(i * Math.PI / 180.0) * radius);
		}
		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x2 - radius + Math.sin(i * Math.PI / 180.0) * radius,
					y + radius + Math.cos(i * Math.PI / 180.0) * radius);
		}
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glScaled(2.0, 2.0, 2.0);
		GL11.glPopAttrib();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRect(float left, float top, float width, float height, Color color) {
		drawRect(left, top, width, height, color.getRGB());
	}

	public static void drawRect(float left, float top, float width, float height, int color) {
		float right = left + width, bottom = top + height;
		if (left < right) {
			float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			float j = top;
			top = bottom;
			bottom = j;
		}

		Gui.drawRect((int)left, (int)top, (int)right, (int)bottom, color);
	}
	
    public static void renderItemStack(ItemStack stack, double x, double y, float scale) {
        renderItemStack(stack, x, y, scale, false);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText) {
        renderItemStack(stack, x, y, scale, enchantedText, scale);
    }

    public static void renderItemStack(ItemStack stack, double x, double y, float scale, boolean enchantedText, float textScale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, boolean enchantedText, float textScale,boolean bg,boolean info) {
        List<ItemStack> items = new ArrayList<>();
        if (target.getHeldItem() != null) {
            items.add(target.getHeldItem());
        }
        for (int index = 3; index >= 0; index--) {
            ItemStack stack = target.inventory.armorInventory[index];
            if (stack != null) {
                items.add(stack);
            }
        }
        float i = x;

        for (ItemStack stack : items) {
            if(bg)
                drawRect(i,y,16 * scale,16 * scale,new Color(0,0,0,150).getRGB());
            renderItemStack(stack, i, y, scale, enchantedText, textScale);
            i += 16;
        }
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale,boolean bg,boolean info) {
        renderItemStack(target,x,y,scale,false,0,bg,info);
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale, float textScale) {
        renderItemStack(target,x,y,scale,true,textScale,false,false);
    }

    public static void renderItemStack(EntityPlayer target, float x, float y, float scale) {
        renderItemStack(target,x,y,scale,scale);
    }
	
	public static void image(final ResourceLocation imageLocation, final int x, final int y, final int width, final int height) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		color(Color.WHITE);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		mc.getTextureManager().bindTexture(imageLocation);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		GlStateManager.resetColor();
		GlStateManager.disableBlend();
	}
	
	public static void color(Color color) {
		if (color == null)
			color = Color.white;
		GL11.glColor4d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
	}
	
	public static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	public static void color(double red, double green, double blue, double alpha) {
		GL11.glColor4d(red, green, blue, alpha);
	}

	public static void color(int color) {
		glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF),
				(byte) (color >> 24 & 0xFF));
	}

	public static void resetColor() {
		color(1, 1, 1, 1);
	}

	public static void setAlphaLimit(float limit) {
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
	}

	public static boolean isBBInFrustum(EntityLivingBase entity) {
		return isBBInFrustum(entity.getEntityBoundingBox());
	}

	public static boolean isBBInFrustum(AxisAlignedBB aabb) {
		FRUSTUM.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		return FRUSTUM.isBoundingBoxInFrustum(aabb);
	}

	private static boolean isInViewFrustum(AxisAlignedBB bb) {
		Entity current = mc.getRenderViewEntity();
		FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
		return FRUSTUM.isBoundingBoxInFrustum(bb);
	}

	public static boolean isInViewFrustum(Entity entity) {
		return isInViewFrustum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
	}

	public static void setupOrientationMatrix(double x, double y, double z) {
		GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY,
				z - mc.getRenderManager().viewerPosZ);
	}

	public static void renderBlock(BlockPos blockPos, int color, boolean outline, boolean shade) {
		renderBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, color, outline, shade);
	}

	public static void renderBox(int x, int y, int z, double x2, double y2, double z2, int color, boolean outline,
			boolean shade) {
		double xPos = x - mc.getRenderManager().viewerPosX;
		double yPos = y - mc.getRenderManager().viewerPosY;
		double zPos = z - mc.getRenderManager().viewerPosZ;
		AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xPos, yPos, zPos, xPos + x2, yPos + y2, zPos + z2);
		drawAxisAlignedBB(axisAlignedBB, shade, outline, color);
	}

	public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean outline, int color) {
		drawAxisAlignedBB(axisAlignedBB, outline, true, color);
	}

	public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean filled, boolean outline, int color) {
		drawSelectionBoundingBox(axisAlignedBB, outline, filled, color);
	}

	public static void drawOutlineBoundingBox(final AxisAlignedBB bb, Color color) {
		RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static void drawFilledBoundingBox(final AxisAlignedBB bb, Color color) {

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(bb.minX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.minX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.minZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		worldrenderer.pos(bb.maxX, bb.minY, bb.maxZ)
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		tessellator.draw();
	}

	public static void drawSelectionBoundingBox(final AxisAlignedBB bb, final boolean outline, final boolean filled,
			int color) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GL11.glDisable(2929);
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();

		if (outline) {

			glEnable(GL_LINE_SMOOTH);

			drawOutlineBoundingBox(bb, new Color(color, true));

			glDisable(GL_LINE_SMOOTH);
		}
		if (filled) {
			drawFilledBoundingBox(bb, new Color(color, true));
		}

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GL11.glEnable(2929);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRoundedRect(double d, double e, double g, double h, float radius, int color) {
		float x1 = (float) (d + g), // @off
				y1 = (float) (e + h);
		final float f = (color >> 24 & 0xFF) / 255.0F, f1 = (color >> 16 & 0xFF) / 255.0F,
				f2 = (color >> 8 & 0xFF) / 255.0F, f3 = (color & 0xFF) / 255.0F; // @on
		GL11.glPushAttrib(0);
		GL11.glScaled(0.5, 0.5, 0.5);

		d *= 2;
		e *= 2;
		x1 *= 2;
		y1 *= 2;

		glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(f1, f2, f3, f);
		GlStateManager.enableBlend();
		glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glBegin(GL11.GL_POLYGON);
		final double v = PI / 180;

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(d + radius + MathHelper.sin((float) (i * v)) * (radius * -1),
					e + radius + MathHelper.cos((float) (i * v)) * (radius * -1));
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(d + radius + MathHelper.sin((float) (i * v)) * (radius * -1),
					y1 - radius + MathHelper.cos((float) (i * v)) * (radius * -1));
		}

		for (int i = 0; i <= 90; i += 3) {
			GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius,
					y1 - radius + MathHelper.cos((float) (i * v)) * radius);
		}

		for (int i = 90; i <= 180; i += 3) {
			GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius,
					e + radius + MathHelper.cos((float) (i * v)) * radius);
		}

		GL11.glEnd();

		glEnable(GL11.GL_TEXTURE_2D);
		glDisable(GL11.GL_LINE_SMOOTH);
		glEnable(GL11.GL_TEXTURE_2D);

		GL11.glScaled(2, 2, 2);

		GL11.glPopAttrib();
		GL11.glColor4f(1, 1, 1, 1);
	}

	public static String stripColor(final String input) {
		return COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static void drawGradientRect(final double left, final double top, double right, double bottom,
			final boolean sideways, final int startColor, final int endColor) {
		right = left + right;
		bottom = top + bottom;
		GL11.glDisable(3553);
		GLUtil.startBlend();
		GL11.glShadeModel(7425);
		GL11.glBegin(7);
		color(startColor);
		if (sideways) {
			GL11.glVertex2d(left, top);
			GL11.glVertex2d(left, bottom);
			color(endColor);
			GL11.glVertex2d(right, bottom);
			GL11.glVertex2d(right, top);
		} else {
			GL11.glVertex2d(left, top);
			color(endColor);
			GL11.glVertex2d(left, bottom);
			GL11.glVertex2d(right, bottom);
			color(startColor);
			GL11.glVertex2d(right, top);
		}
		GL11.glEnd();
		GL11.glDisable(3042);
		GL11.glShadeModel(7424);
		GLUtil.endBlend();
		GL11.glEnable(3553);
	}

	public static void drawBorder(float x, float y, float width, float height, final float outlineThickness,
			int outlineColor) {
		glEnable(GL_LINE_SMOOTH);
		color(outlineColor);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();

		glLineWidth(outlineThickness);
		float cornerValue = (float) (outlineThickness * .19);

		glBegin(GL_LINES);
		glVertex2d(x, y - cornerValue);
		glVertex2d(x, y + height + cornerValue);
		glVertex2d(x + width, y + height + cornerValue);
		glVertex2d(x + width, y - cornerValue);
		glVertex2d(x, y);
		glVertex2d(x + width, y);
		glVertex2d(x, y + height);
		glVertex2d(x + width, y + height);
		glEnd();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();

		glDisable(GL_LINE_SMOOTH);
	}

	public static void drawBorderedRect(float x, float y, float width, float height, final float outlineThickness,
			int rectColor, int outlineColor) {
		drawRect(x, y, width, height, rectColor);
		drawBorder(x, y, width, height, outlineThickness, outlineColor);
	}

	public static void drawHorizontalGradientSideways(double x, double y, double width, double height, int leftColor,
			int rightColor) {
		drawGradientRect(x, y, width, height, true, leftColor, rightColor);
	}

	public static void drawVerticalGradientSideways(double x, double y, double width, double height, int topColor,
			int bottomColor) {
		drawGradientRect(x, y, width, height, false, topColor, bottomColor);
	}
	
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }
    
    public static void drawScaledCustomSizeModalRect(double x, double y, float u, float v, int uWidth, int vHeight, double width, double height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }
}