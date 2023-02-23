package dev.zontreck.otemod.events;

import dev.zontreck.libzontreck.util.ItemUtils;
import dev.zontreck.otemod.OTEMod;
import dev.zontreck.otemod.enchantments.MobEggEnchantment;
import dev.zontreck.otemod.enchantments.ModEnchantments;
import dev.zontreck.otemod.items.tags.ItemStatType;
import dev.zontreck.otemod.ore.OreGenerator;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;

@Mod.EventBusSubscriber(modid=OTEMod.MOD_ID)
public class EventHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addOresToBiomes(final BiomeLoadingEvent ev){
        //ShapedAionResources.LOGGER.info("Biome loading event called. Registering aion ores");
        OTEMod.LOGGER.info("/!\\ Registering OTEMod ores /!\\");
        OreGenerator.generateOres(ev);
    }

    @SubscribeEvent
    public void onEntityKilled(LivingDropsEvent ev){
        if(ev.getEntity().level.isClientSide)return;

        Entity ent = ev.getSource().getEntity();
        if(ent instanceof Player)
        {
            ServerPlayer play = (ServerPlayer)ent;
            LivingEntity killed = ev.getEntityLiving();

            ItemStack stack = play.getMainHandItem();
            int levelOfEgging = ItemUtils.getEnchantmentLevel(ModEnchantments.MOB_EGGING_ENCHANTMENT.get(),stack);
            
            if(levelOfEgging==0)return;
            CompoundTag tag = stack.getTag();
            int bias = tag.getInt(MobEggEnchantment.TAG_BIAS);


            if(MobEggEnchantment.givesEgg(levelOfEgging, bias))
            {
                bias=0;
                tag.putInt(MobEggEnchantment.TAG_BIAS, bias);
                // .25% chance
                // Check enchantment level for looting
                int level = ItemUtils.getEnchantmentLevel (Enchantments.MOB_LOOTING,stack);
                if(level==3){
                    ItemStack egg = new ItemStack(ForgeSpawnEggItem.fromEntityType(killed.getType()));
                    ev.getDrops().add(new ItemEntity(killed.level, killed.getX(), killed.getY(), killed.getZ(), egg));
                    LoreHandlers.updateItem(stack, ItemStatType.EGGING);
                }
            }else{
                bias += 1;
                tag.putInt(MobEggEnchantment.TAG_BIAS, bias);
            }
        }
    }
    
}
