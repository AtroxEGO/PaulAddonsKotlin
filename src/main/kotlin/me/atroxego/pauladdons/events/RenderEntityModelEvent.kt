package me.atroxego.pauladdons.events

import net.minecraft.client.model.ModelBase
import net.minecraft.entity.EntityLivingBase

class RenderEntityModelEvent(
    val entity: EntityLivingBase,
    val model: ModelBase,
    val partialTicks: Float
) : Event, ICancellable by Cancellable()