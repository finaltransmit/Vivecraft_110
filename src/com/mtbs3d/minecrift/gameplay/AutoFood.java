package com.mtbs3d.minecrift.gameplay;

import com.mtbs3d.minecrift.api.IRoomscaleAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Hendrik on 02-Aug-16.
 */
public class AutoFood {
	float mouthtoEyeDistance=0.1f;
	float threshold=0.15f;
	public boolean eating=false;
	int eattime=1100;//Guess. Not sure what the actual value is
	long eatStart;

	public boolean isActive(EntityPlayerSP p){
		if(p == null) return false;
		if(p.isDead) return false;
		if(p.isPlayerSleeping()) return false;
		if(p.getHeldItemMainhand() == null) return false;
		EnumAction action=p.getHeldItemMainhand().getItemUseAction();
		return	action == EnumAction.EAT || action == EnumAction.DRINK;
	}

	public void doProcess(IRoomscaleAdapter provider, EntityPlayerSP player){
		if(!isActive(player)) {
			eating=false;
			return;
		}
		Vec3d hmdPos=provider.getHMDPos_Room();
		Vec3d mouthPos=provider.getCustomHMDVector(new Vec3d(0,-1,0)).scale(mouthtoEyeDistance).add(hmdPos);
		Vec3d controllerPos=provider.getControllerPos_Room(0).add(provider.getCustomControllerVector(0,new Vec3d(0,0,-1)).scale(0.1));

		if(mouthPos.distanceTo(controllerPos)<threshold){
			if(!eating) {
				player.setItemInUseClient(player.getHeldItemMainhand());
				Minecraft.getMinecraft().playerController.processRightClick(player, player.worldObj, player.getHeldItemMainhand(), EnumHand.MAIN_HAND);
				//eatStart=Minecraft.getSystemTime();
				eating=true;
			}
			/*if(Minecraft.getSystemTime()-eatStart>=eattime){
				Minecraft.getMinecraft().playerController.onStoppedUsingItem(player);
				eating=false;
			}*/
		}else {
			if(eating){
				player.setItemInUseClient(null);
				eating=false;
			}
		}
	}
}
