package fun.kaituo.sandrun;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import fun.kaituo.gameutils.Game;
import fun.kaituo.gameutils.event.PlayerEndGameEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SandRunGame extends Game implements Listener {
    private static final SandRunGame instance = new SandRunGame((SandRun) Bukkit.getPluginManager().getPlugin("SandRun"));
    List<Player> playersAlive;
    int countDownSeconds = 5;

    private SandRunGame(SandRun plugin) {
        this.plugin = plugin;
        players = plugin.players;
        playersAlive = new ArrayList<>();
        initializeGame(plugin, "SandRun", "§e落沙跑酷", new Location(world, 1000, 14, -1000));
        initializeButtons(new Location(world, 1000, 15, -996), BlockFace.NORTH,
                new Location(world, 996, 15, -1000), BlockFace.EAST);
        initializeGameRunnable();
    }

    public static SandRunGame getInstance() {
        return instance;
    }

    @EventHandler
    public void preventDamage(EntityDamageEvent ede) {
        if (ede.getEntity() instanceof Player) {
            if (playersAlive.contains(ede.getEntity())) {
                ede.setCancelled(true);
            }
        }
    }


    private void initializeGameRunnable() {
        gameRunnable = () -> {
            Collection<Player> startingPlayers = getPlayersNearHub(50,50,50);
            players.addAll(startingPlayers);
            playersAlive.addAll(startingPlayers);
            if (players.size() < 2) {
                for (Player p : players) {
                    p.sendMessage("§c至少需要2人才能开始游戏！");
                }
                players.clear();
                playersAlive.clear();
            } else {
                int addition = 1;
                if (players.size() <= 3) {
                    addition = 1;
                } else if (players.size() <= 5) {
                    addition = 2;
                } else if (players.size() <= 7) {
                    addition = 3;
                } else if (players.size() <= 9) {
                    addition = 4;
                } else if (players.size() <= 11) {
                    addition = 5;
                } else {
                    addition = 6;
                }
                pasteSchematic("sandrun1body", 1000, 105, -1000, true);
                pasteSchematic("sandrun1head", 1000, 105, -1000, true);
                for (int i = addition; i > 0; i--) {
                    int finalI = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        pasteSchematic("sandrun1body", 1000, 105 - finalI * 11, -1000, true);
                    }, 10L * i);
                }
                Bukkit.getPluginManager().registerEvents(this, plugin);
                removeStartButton();
                placeSpectateButton();
                startCountdown(countDownSeconds);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : players) {
                        p.teleport(new Location(world, 1000.5, 106.0, -999.5));
                        p.getInventory().clear();
                    }
                });
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player p : players) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 49, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 0, false, false));
                    }

                }, countDownSeconds * 20L);
                taskIds.add(
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                            for (Player p : playersAlive) {
                                if (p.getPlayer().getLocation().getY() < 35) {
                                    continue;
                                }
                                /**
                                 double locationY = p.getLocation().getY();
                                 double yOffSet = locationY - (int)(Math.floor(locationY));
                                 if (yOffSet >= 0.75) {
                                 //p.sendMessage("你没接触地面");
                                 continue;
                                 }
                                 **/
                                /**
                                 Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
                                 if (block.getType().equals(Material.AIR)) {
                                 //p.sendMessage("你脚下是空气");
                                 continue;
                                 }
                                 **/
                                Block[] blocks = getBlockss(p);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    for (Block b : blocks) {
                                        b.setType(Material.AIR);
                                        /*
                                        if (!b.getType().equals(Material.SANDSTONE) && !b.getType().equals(Material.ANDESITE) && !b.getType().equals(Material.GLOWSTONE)) {
                                            continue;
                                        }*/

                                        //Removed Falling block animation for better performance
                                        /*

                                        BlockData data = b.getBlockData();
                                        FallingBlock fb = world.spawnFallingBlock(new Location(world, b.getX() + 0.5, b.getY(), b.getZ() + 0.5), data);
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                            fb.remove();
                                        }, 15);


                                         */
                                    }
                                }, 8);
                            }

                        }, 20L * countDownSeconds, 1));
                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    Player playerOut = null;
                    for (Player p : playersAlive) {
                        if (p.getLocation().getY() <= 35) {
                            playerOut = p;
                            break;
                        }
                    }
                    if (playerOut != null) {
                        for (Player p : players) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f§l" + playerOut.getName() + " §c坠入深渊！"));
                        }
                        playerOut.setGameMode(GameMode.SPECTATOR);
                        playersAlive.remove(playerOut);
                    }

                }, 20L * countDownSeconds, 1));
                taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    if (playersAlive.size() <= 1) {
                        Player winner = playersAlive.get(0);
                        spawnFireworks(winner);
                        List<Player> playersCopy = new ArrayList<>(players);
                        for (Player p : playersCopy) {
                            p.sendTitle("§e" + playersAlive.get(0).getName() + " §b获胜了！", null, 5, 50, 5);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                p.teleport(new Location(world, 1000.5, 14, -999.5));
                                Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p, this));
                                pasteSchematic("sandrunempty", 1000, 105, -1000, false);
                            }, 100);
                        }
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            placeStartButton();
                            removeSpectateButton();
                            HandlerList.unregisterAll(this);
                        }, 100);
                        players.clear();
                        playersAlive.clear();
                        cancelGameTasks();
                    }

                }, 20L * countDownSeconds, 1));
            }
        };
    }

    @Override
    protected void quit(Player p) throws IOException {
        players.remove(p);
        playersAlive.remove(p);
    }

    @Override
    protected boolean rejoin(Player player) {
        return false;
    }

    @Override
    protected boolean join(Player player) {
        player.setBedSpawnLocation(hubLocation, true);
        player.teleport(hubLocation);
        return true;
    }

    @Override
    protected void forceStop() {

    }

    private void pasteSchematic(String name, double x, double y, double z, boolean ignoreAir) {
        File file = new File("plugins/WorldEdit/schematics/" + name + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard clipboard = null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .ignoreAirBlocks(ignoreAir)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Block[] getBlockss(Player p) {
        Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        double x = p.getLocation().getX();
        double z = p.getLocation().getZ();
        double xOffSet = x - (int) (Math.floor(x));
        double zOffset = z - (int) (Math.floor(z));
        if (xOffSet < 0.3) {
            if (zOffset < 0.3) {
                return new Block[]{block, block.getRelative(BlockFace.NORTH_WEST), block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.WEST)};
            } else if (zOffset > 0.7) {
                return new Block[]{block, block.getRelative(BlockFace.SOUTH_WEST), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.WEST)};
            } else {
                return new Block[]{block, block.getRelative(BlockFace.WEST)};
            }
        } else if (xOffSet > 0.7) {
            if (zOffset < 0.3) {
                return new Block[]{block, block.getRelative(BlockFace.NORTH_EAST), block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.EAST)};
            } else if (zOffset > 0.7) {
                return new Block[]{block, block.getRelative(BlockFace.SOUTH_EAST), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST)};
            } else {
                return new Block[]{block, block.getRelative(BlockFace.EAST)};
            }

        } else {
            if (zOffset < 0.3) {
                return new Block[]{block, block.getRelative(BlockFace.NORTH)};
            } else if (zOffset > 0.7) {
                return new Block[]{block, block.getRelative(BlockFace.SOUTH)};
            } else {
                return new Block[]{block};
            }

        }
    }
}
