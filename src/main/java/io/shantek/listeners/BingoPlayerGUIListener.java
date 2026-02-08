package io.shantek.listeners;

import io.shantek.UltimateBingo;
import io.shantek.managers.BingoPlayerGUIManager;
import io.shantek.tools.MaterialList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BingoPlayerGUIListener implements Listener {
    MaterialList materialList;
    public BingoPlayerGUIManager bingoPlayerGUIManager;
    public UltimateBingo ultimateBingo;
    private boolean sentWarning;

    public BingoPlayerGUIListener(MaterialList materialList, BingoPlayerGUIManager bingoPlayerGUIManager, UltimateBingo ultimateBingo) {
        this.materialList = materialList;
        this.bingoPlayerGUIManager = bingoPlayerGUIManager;
        this.ultimateBingo = ultimateBingo;
        sentWarning = false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();

        if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

            // Asegurarse de que el evento fue activado en la GUI de configuración de Bingo
            if (e.getView().getTitle().contains("Welcome to Ultimate Bingo")) {
                e.setCancelled(true);  // Evitar que arrastren ítems

                int slot = e.getRawSlot();
                // Asegurarse de que el click esté dentro del inventario
                if (slot >= 0 && slot < 9) {
                    switch (slot) {
                        case 0:
                            ultimateBingo.bingoFunctions.giveBingoCard(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;

                        case 1:
                            player.openInventory(ultimateBingo.bingoPlayerGUIManager.setupPlayersBingoCardsInventory());
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                    }
                }

            } 
            // GUI de cartas de jugadores o equipos
            else if (e.getView().getTitle().contains("Player Bingo Cards") || (e.getView().getTitle().contains("Team Bingo Cards"))) {

                // Evitar mover ítems
                e.setCancelled(true);

                // Botón de regresar al menú
                int slot = e.getRawSlot();

                if (slot == 53) {
                    ItemStack clickedItem = e.getCurrentItem();
                    if (clickedItem != null && clickedItem.getType() == Material.CHEST) {
                        // Verificar si es el cofre de "Volver al menú"
                        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()
                                && ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Back to menu")) {

                            player.closeInventory();
                            player.openInventory(ultimateBingo.bingoPlayerGUIManager.createPlayerGUI(player));
                        }
                    }
                } 
                else {

                    // Modo equipos
                    if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

                        ItemStack clickedItem = e.getCurrentItem();
                        if (clickedItem == null){
                            return; // Ítem inválido
                        }

                        if (clickedItem.getType() == Material.RED_WOOL) {
                            ultimateBingo.bingoCommand.openBingoTeamCard(player, ultimateBingo.redTeamInventory);
                        } 
                        else if (clickedItem.getType() == Material.BLUE_WOOL) {
                            ultimateBingo.bingoCommand.openBingoTeamCard(player, ultimateBingo.blueTeamInventory);
                        } 
                        else if (clickedItem.getType() == Material.YELLOW_WOOL) {
                            ultimateBingo.bingoCommand.openBingoTeamCard(player, ultimateBingo.yellowTeamInventory);
                        }

                    } 
                    else {

                        // Lista de cartas de jugadores
                        ItemStack clickedItem = e.getCurrentItem();
                        if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) {
                            return; // No es una cabeza válida
                        }

                        // Obtener nombre del jugador desde el ítem
                        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

                        // Verificar si el jugador existe y está conectado
                        Player targetPlayer = Bukkit.getPlayerExact(displayName);
                        if (targetPlayer == null) {
                            e.getWhoClicked().sendMessage(ChatColor.RED + "La carta de bingo del jugador no existe o no está conectado.");
                            return;
                        }

                        // Cerrar inventario actual
                        e.getWhoClicked().closeInventory();

                        // Abrir la carta de bingo del jugador objetivo
                        ultimateBingo.bingoCommand.openBingoOtherPlayer(player, targetPlayer);

                    }
                }
            }
        }
    }
}
