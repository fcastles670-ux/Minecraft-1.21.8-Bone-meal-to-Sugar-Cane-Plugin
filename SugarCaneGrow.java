package com.tasarimci.sugarcane;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SugarCaneGrow extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Seker Kamisi Buyutme Plugini Aktif!");
    }

    @EventHandler
    public void onSugarCaneInteract(PlayerInteractEvent event) {
        // Sadece bir bloğa sağ tıklandığında çalış
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();

        // Tıklanan blok şeker kamışı mı ve eldeki item kemik tozu mu?
        if (clickedBlock != null && clickedBlock.getType() == Material.SUGAR_CANE 
            && item != null && item.getType() == Material.BONE_MEAL) {

            // Şeker kamışının en üst bloğunu bul (Maksimum 3 blok kuralı için)
            Block topBlock = getTopSugarCane(clickedBlock);
            Block aboveTop = topBlock.getRelative(BlockFace.UP);

            // Eğer üstü boşsa ve toplam yükseklik 3'ü geçmeyecekse büyüt
            if (aboveTop.getType() == Material.AIR && getCaneHeight(topBlock) < 3) {
                
                aboveTop.setType(Material.SUGAR_CANE);
                
                // Efekt ve Ses
                clickedBlock.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, aboveTop.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3);
                clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);

                // Kemik tozunu eksilt (Yaratıcı modda değilse)
                Player player = event.getPlayer();
                if (player.getGameMode().toString().equals("SURVIVAL")) {
                    item.setAmount(item.getAmount() - 1);
                }
                
                event.setCancelled(true); // Bloğun normal etkileşimini engelle
            }
        }
    }

    // Şeker kamışının en üst bloğunu bulan yardımcı metod
    private Block getTopSugarCane(Block block) {
        while (block.getRelative(BlockFace.UP).getType() == Material.SUGAR_CANE) {
            block = block.getRelative(BlockFace.UP);
        }
        return block;
    }

    // Mevcut şeker kamışı sütununun yüksekliğini hesaplar
    private int getCaneHeight(Block block) {
        int height = 1;
        Block temp = block;
        while (temp.getRelative(BlockFace.DOWN).getType() == Material.SUGAR_CANE) {
            temp = temp.getRelative(BlockFace.DOWN);
            height++;
        }
        return height;
    }
}
