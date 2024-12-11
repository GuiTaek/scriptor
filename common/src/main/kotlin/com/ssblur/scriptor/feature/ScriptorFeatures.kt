package com.ssblur.scriptor.feature

import com.ssblur.scriptor.ScriptorMod
import dev.architectury.registry.level.biome.BiomeModifications
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

@Suppress("unused", "experimental")
object ScriptorFeatures {
    val FEATURES: DeferredRegister<Feature<*>> = DeferredRegister.create(ScriptorMod.MOD_ID, Registries.FEATURE)

    val ENGRAVING_FEATURE: RegistrySupplier<Feature<*>> = FEATURES.register("engraving") {
        ScriptorInscriptionsFeature(NoneFeatureConfiguration.CODEC)
    }

    fun register() {
        FEATURES.register()
        BiomeModifications.addProperties(
            { biome -> biome.hasTag(BiomeTags.IS_OVERWORLD) },
            { _, mutable ->
                mutable.generationProperties.addFeature(
                    GenerationStep.Decoration.RAW_GENERATION,
                    ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("scriptor", "engravings"))
                )
            })
    }
}
