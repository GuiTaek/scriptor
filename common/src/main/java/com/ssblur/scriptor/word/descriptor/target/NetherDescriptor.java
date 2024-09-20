package com.ssblur.scriptor.word.descriptor.target;

import com.ssblur.scriptor.api.word.Descriptor;
import com.ssblur.scriptor.api.word.descriptor.TargetDescriptor;
import com.ssblur.scriptor.helpers.targetable.Targetable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class NetherDescriptor extends Descriptor implements TargetDescriptor {
  @Override
  public List<Targetable> modifyTargets(List<Targetable> targetables, Targetable owner) {
    List<Targetable> list = new ArrayList<>();

    for(var targetable: targetables) {
      var level = targetable.getLevel();
      if(!(level instanceof ServerLevel server)) continue;
      var nether = server.getServer().getLevel(Level.NETHER);
      if(nether == null || !server.getServer().isLevelEnabled(nether)) return targetables;
      if(level.dimension() != Level.OVERWORLD && level.dimension() != Level.NETHER) {
        list.add(targetable);
        continue;
      }

      var targetLevel = server.getServer().getLevel(level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER);
      double scale = level.dimension() == Level.NETHER ? 8d : 0.125d;
      var pos = targetable.getTargetPos();
      var x = pos.x * scale;
      var z = pos.z * scale;

      list.add(new Targetable(targetLevel, new Vec3(x, pos.y, z)));
    }

    return list;
  }

  @Override
  public boolean replacesSubjectCost() {
    return false;
  }

  @Override
  public Cost cost() {
    return Cost.add(20);
  }
}
