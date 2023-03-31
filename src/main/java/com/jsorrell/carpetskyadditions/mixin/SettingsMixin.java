package com.jsorrell.carpetskyadditions.mixin;

import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.Settings.class)
public abstract class SettingsMixin {

  @Inject(method = "requiresTool", at = @At("HEAD"), cancellable = true)
  public void allowSnowByHand(CallbackInfoReturnable<AbstractBlock.Settings> info) {
    Material material = ((SettingsAccessor) this).getMaterial();
    if(material.equals(Material.SNOW_LAYER)) {
      info.setReturnValue(((AbstractBlock.Settings)(Object) this));
      info.cancel();
    }
  }
}
