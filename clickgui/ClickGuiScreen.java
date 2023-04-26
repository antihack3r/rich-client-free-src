/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.lwjgl.input.Mouse
 */
package clickgui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import clickgui.panel.Panel;
import clickgui.setting.Setting;
import clickgui.setting.SettingsManager;
import com.google.common.collect.Lists;
import me.rich.Main;
import me.rich.helpers.render.Translate;
import me.rich.module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public final class ClickGuiScreen
extends GuiScreen {
    private static ClickGuiScreen INSTANCE;
    private final List panels = Lists.newArrayList();
    public Translate translate;
    public static double scaling;
    private float curAlpha;
    private int f;
    
    public static float lastPercent;
    public static float percent2;
    public static float lastPercent2;
    public static float outro;
    public static float lastOutro;
	public static float percent;
    
    public ClickGuiScreen() {
        Category[] category = Category.values();
        scaling = 0.0;
        for (int i = category.length - 1; i >= 0; --i) {
            this.panels.add(new Panel(category[i], 5 + 115 * i, 10));
            this.translate = new Translate(0.0f, 0.0f);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int i = 0;
        if (Minecraft.player != null && this.mc.world != null) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            float alpha = 150.0f;
            int step = (int)(alpha / 100.0f);
            if (this.curAlpha < alpha - (float)step) {
                this.curAlpha += (float)step;
            } else if (this.curAlpha > alpha - (float)step && this.curAlpha != alpha) {
                this.curAlpha = (int)alpha;
            } else if (this.curAlpha != alpha) {
                this.curAlpha = (int)alpha;
            }
            Color c = new Color(Main.getClientColor().getRed(), Main.getClientColor().getGreen(), Main.getClientColor().getBlue(), (int) curAlpha);
            Color none = new Color(0, 0, 0, 0);
           this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), none.getRGB(), c.getRGB());
            this.drawDefaultBackground();
            int panelsSize = this.panels.size();
            while (i < panelsSize) {
                ((Panel)this.panels.get(i)).onDraw(mouseX, mouseY);
                this.updateMouseWheel();
                ++i;
            }
        }
    }

    public void updateMouseWheel() {
        int scrollWheel = Mouse.getDWheel();
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            if (scrollWheel < 0) {
                ((Panel)this.panels.get(i)).setY(((Panel)this.panels.get(i)).getY() - 15);
                continue;
            }
            if (scrollWheel <= 0) continue;
            ((Panel)this.panels.get(i)).setY(((Panel)this.panels.get(i)).getY() + 15);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            ((Panel)this.panels.get(i)).onMouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            ((Panel)this.panels.get(i)).onMouseRelease(mouseX, mouseY, state);
        }
    }

    public void loadGui() {
        try {
            String line;
            File file1 = new File(this.mc.mcDataDir + File.separator + "richclient/cfgs");
            File file = new File(file1, "clickgui.cfg");
            if (!file1.exists()) {
                file1.mkdirs();
            }
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                String readString = line.trim();
                String[] split = readString.split(":");
                for (Setting s : SettingsManager.getSettings()) {
                    if (!s.getName().equals(split[0])) continue;
                    s.setValString(split[1]);
                    s.setValBoolean(Boolean.valueOf(split[2]));
                    s.setValDouble(Float.valueOf(split[3]).floatValue());
                }
            }
            br.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void saveGui() {
        try {
            File file1 = new File(this.mc.mcDataDir + File.separator + "richclient/cfgs");
            File file = new File(file1, "clickgui.cfg");
            if (!file1.exists()) {
                file1.mkdirs();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            out.write("Config:Default");
            out.write("\r\n");
            for (Setting s : SettingsManager.getSettings()) {
                if (s.getName().equals("Configs")) continue;
                out.write(s.getName() + ":" + s.getValString() + ":" + s.getValBoolean() + ":" + s.getValDouble());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception e) {
            Main.msg("Failed to save configs!", true);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.mc.entityRenderer.isShaderActive()) {
            this.mc.entityRenderer.theShaderGroup = null;
        }
    }

    @Override
    public void initGui() {
        if (!this.mc.gameSettings.ofFastRender && !this.mc.entityRenderer.isShaderActive()) {
            this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            lastPercent = 1.23f;
            percent2 = 0.98f;
            lastPercent2 = 0.98f;
            outro = 1;
            lastOutro = 1;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            ((Panel)this.panels.get(i)).onKeyPress(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static ClickGuiScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGuiScreen();
        }
        return INSTANCE;
    }
}

