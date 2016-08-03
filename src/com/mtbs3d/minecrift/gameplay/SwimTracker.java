package com.mtbs3d.minecrift.gameplay;

import com.mtbs3d.minecrift.api.IRoomscaleAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Created by Hendrik on 02-Aug-16.
 */
public class SwimTracker {
	boolean gravityOverride=false;

	Vec3d motion=Vec3d.ZERO;
	double friction=0.9f;

	double lastDist;

	double sinkspeed=0.005f;
	double swimspeed=0.8f;

	public boolean isActive(EntityPlayerSP p){
		if(!Minecraft.getMinecraft().vrSettings.vrFreeMove || Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(p==null || p.isDead)
			return false;
		if(!p.isInWater() && !p.isInLava())
			return false;
		//Block block=p.worldObj.getBlockState(p.getPosition().add(0,0.7,0)).getBlock();
		return true;
	}

	public void doProcess(Minecraft minecraft, EntityPlayerSP player){
		if(!isActive(player)) {
			if(gravityOverride){
				player.setNoGravity(false);
				gravityOverride=false;
			}
			return;
		}

		gravityOverride=true;
		player.setNoGravity(true);


		Vec3d playerpos=player.getPositionVector();

		Vec3d swimHeight=new Vec3d(0,1.5,0);
		Vec3d maxSwim=new Vec3d(0,1.3,0);

		double depth=2;

		for (int i = 0; i < 4; i++) {
			BlockPos blockpos=new BlockPos(playerpos.add(new Vec3d(0,i+0.5,0)));
			Material block=player.worldObj.getBlockState(blockpos).getMaterial();
			if(!block.isLiquid())
			{
				depth=blockpos.getY()-playerpos.yCoord-2;
				break;
			}
		}

		if(motion.lengthVector()>0.01) {
			if (depth > 1)
				depth = 1;
		}else{
			if (depth > 2)
				depth = 2;
		}

		double buoyancy=(1-depth);


			Material block1=player.worldObj.getBlockState(new BlockPos(playerpos.add(swimHeight))).getMaterial();
			if(!block1.isLiquid()){
				//we are at the surface
				Material block2=player.worldObj.getBlockState(new BlockPos(playerpos.add(maxSwim))).getMaterial();
				if(!block2.isLiquid()){
					//Too high
					player.setNoGravity(false);
				}

			}else{
				player.addVelocity(0, sinkspeed*buoyancy, 0);
			}


		Vec3d controllerR= minecraft.roomScale.getControllerPos_World(0);
		Vec3d controllerL= minecraft.roomScale.getControllerPos_World(1);

		Vec3d middle= controllerL.subtract(controllerR).scale(0.5).add(controllerR);

		Vec3d hmdPos=minecraft.roomScale.getHMDPos_World().subtract(0,0.3,0);

		Vec3d movedir=middle.subtract(hmdPos).normalize().add(
				minecraft.roomScale.getHMDDir_World()).scale(0.5);

		Vec3d contollerDir= minecraft.roomScale.getCustomControllerVector(0,new Vec3d(0,0,-1)).add(
				minecraft.roomScale.getCustomControllerVector(1,new Vec3d(0,0,-1))).scale(0.5);
		double dirfactor=contollerDir.add(movedir).lengthVector()/2;


		double distance= hmdPos.distanceTo(middle);
		double distDelta=lastDist-distance;

		if(distDelta>0){
			Vec3d velo=movedir.scale(distDelta*swimspeed*dirfactor);
			motion=motion.add(velo.scale(0.15));
		}

		lastDist=distance;
		player.addVelocity(motion.xCoord,motion.yCoord,motion.zCoord);
		motion=motion.scale(friction);

	}

}
