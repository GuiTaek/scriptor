package com.ssblur.scriptor.word.descriptor.discount;

import com.ssblur.scriptor.api.word.Descriptor;
import com.ssblur.scriptor.helpers.targetable.Targetable;
import com.ssblur.scriptor.api.word.descriptor.CastDescriptor;


public class ClearDiscountDescriptor extends Descriptor implements CastDescriptor {
  @Override
  public Cost cost() { return new Cost(0.9, COSTTYPE.MULTIPLICATIVE); }

  @Override
  public boolean cannotCast(Targetable caster) {
    return caster.getLevel().isRainingAt(caster.getTargetBlockPos());
  }

  @Override
  public boolean allowsDuplicates() {
    return false;
  }
}
