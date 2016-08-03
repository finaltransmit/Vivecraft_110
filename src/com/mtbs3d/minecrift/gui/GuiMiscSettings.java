package com.mtbs3d.minecrift.gui;

import com.mtbs3d.minecrift.gui.framework.*;
import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Hendrik on 01-Aug-16.
 */
public class GuiMiscSettings extends BaseGuiSettings implements GuiEventEx {
	private GuiSliderEx rotationSlider;

	static VRSettings.VrOptions[] miscSettings = new VRSettings.VrOptions[]
			{
					VRSettings.VrOptions.REVERSE_HANDS,
					VRSettings.VrOptions.WORLD_SCALE,
					VRSettings.VrOptions.WORLD_ROTATION,
					VRSettings.VrOptions.WORLD_ROTATION_INCREMENT,
			};

	public GuiMiscSettings(GuiScreen guiScreen, VRSettings guivrSettings) {
		super( guiScreen, guivrSettings );
		screenTitle = "Misc. Settings";
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		this.buttonList.clear();
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DEFAULTS, this.width / 2 - 100, this.height / 6 + 148, "Reset To Defaults"));
		this.buttonList.add(new GuiButtonEx(ID_GENERIC_DONE, this.width / 2 - 100, this.height / 6 + 168, "Done"));
		VRSettings.VrOptions[] buttons = miscSettings;

		int extra = 0;
		for (int var12 = 2; var12 < buttons.length + 2; ++var12)
		{
			VRSettings.VrOptions var8 = buttons[var12 - 2];
			int width = this.width / 2 - 155 + var12 % 2 * 160;
			int height = this.height / 6 + 21 * (var12 / 2) - 10 + extra;

			if (var8 == VRSettings.VrOptions.DUMMY)
				continue;

			if (var8 == VRSettings.VrOptions.DUMMY_SMALL) {
				extra += 5;
				continue;
			}

			if (var8.getEnumFloat())
			{
				float minValue = 0.0f;
				float maxValue = 1.0f;
				float increment = 0.01f;

				if(var8 == VRSettings.VrOptions.WORLD_SCALE){
					minValue = 0f;
					maxValue = 29f;
					increment = 1f;
				}
				else if (var8 == VRSettings.VrOptions.WORLD_ROTATION){
					minValue = 0f;
					maxValue = 360f;
					increment = Minecraft.getMinecraft().vrSettings.vrWorldRotationIncrement;
				}
				else if (var8 == VRSettings.VrOptions.WORLD_ROTATION_INCREMENT){
					minValue = 0f;
					maxValue = 4f;
					increment = 1f;
				}

				// VIVE START - new options
				GuiSliderEx slider = new GuiSliderEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8), minValue, maxValue, increment, this.guivrSettings.getOptionFloatValue(var8));
				slider.setEventHandler(this);
				slider.enabled = getEnabledState(var8);
				this.buttonList.add(slider);
				if (var8 == VRSettings.VrOptions.WORLD_ROTATION)rotationSlider = slider;
			}
			else
			{
				GuiSmallButtonEx smallButton = new GuiSmallButtonEx(var8.returnEnumOrdinal(), width, height - 20, var8, this.guivrSettings.getKeyBinding(var8));
				smallButton.setEventHandler(this);
				smallButton.enabled = getEnabledState(var8);
				this.buttonList.add(smallButton);
			}
		}
	}

	private boolean getEnabledState(VRSettings.VrOptions var8)
	{
		return true;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3)
	{
		if (reinit)
		{
			initGui();
			reinit = false;
		}
		super.drawScreen(par1,par2,par3);
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		VRSettings vr = mc.vrSettings;

		if (par1GuiButton.enabled)
		{
			if (par1GuiButton.id == ID_GENERIC_DONE)
			{
				Minecraft.getMinecraft().gameSettings.saveOptions();
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}
			else if (par1GuiButton.id == ID_GENERIC_DEFAULTS)
			{
				vr.vrWorldRotation=0;
				vr.vrWorldRotationIncrement=45f;
				vr.vrWorldScale=1f;
				vr.vrReverseHands=false;
				//end jrbudda

				Minecraft.getMinecraft().gameSettings.saveOptions();
				Minecraft.getMinecraft().vrSettings.saveOptions();
				this.reinit = true;
			}
			else if (par1GuiButton instanceof GuiSmallButtonEx)
			{
				VRSettings.VrOptions num = VRSettings.VrOptions.getEnumOptions(par1GuiButton.id);
				this.guivrSettings.setOptionValue(((GuiSmallButtonEx)par1GuiButton).returnVrEnumOptions(), 1);
				par1GuiButton.displayString = this.guivrSettings.getKeyBinding(VRSettings.VrOptions.getEnumOptions(par1GuiButton.id));
			}
		}
	}

	@Override
	public boolean event(int id, VRSettings.VrOptions enumm)
	{
		if(enumm == VRSettings.VrOptions.WORLD_ROTATION_INCREMENT){
			mc.vrSettings.vrWorldRotation = 0;
			rotationSlider.increment = mc.vrSettings.vrWorldRotationIncrement;
			rotationSlider.setValue(0);
		}

		return true;
	}

	@Override
	public boolean event(int id, String s) {
		return true;
	}

	@Override
	protected String[] getTooltipLines(String displayString, int buttonId)
	{
		VRSettings.VrOptions e = VRSettings.VrOptions.getEnumOptions(buttonId);
		if( e != null )
			switch(e)
			{
				case WORLD_SCALE:
					return new String[] {
							"Scales the player in the world.",
							"Above one makes you larger",
							"And below one makes you small",
							"And the ones that mother gives you",
							"don't do anything at all."
					};
				case WORLD_ROTATION:
					return new String[] {
							"Adds extra rotation to your HMD.",
							"More useful bound to a button or ",
							"changed with the arrow keys."
					};
				case WORLD_ROTATION_INCREMENT:
					return new String[] {
							"How many degrees to rotate when",
							"rotating the world."

					};
				default:
					return null;
			}
		else
			switch(buttonId)
			{
//                case 201:
//                    return new String[] {
//                            "Open this configuration screen to adjust the Head",
//                            "  Tracker orientation (direction) settings. ",
//                            "  Ex: Head Tracking Selection (Hydra/Oculus), Prediction"
//                    };
				default:
					return null;
			}
	}
}
