package tech.yfshadaow;

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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class SandRunGame extends BukkitRunnable implements Listener {
    World world;
    SandRun plugin;
    List<Player> players;
    List<Player> playersAlive;
    List<Integer> taskIds;

    public SandRunGame(SandRun plugin) {
        this.plugin = plugin;
        this.players = plugin.players;
        this.playersAlive = new ArrayList<>();
        this.taskIds = new ArrayList<>();
        this.world = Bukkit.getWorld("world");
    }
    @EventHandler
    public void preventDamage(EntityDamageEvent ede) {
        if (ede.getEntity() instanceof Player) {
            if (playersAlive.contains(ede.getEntity())) {
                ede.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerChangeGame(PlayerChangeGameEvent pcge) {
        players.remove(pcge.getPlayer());
        playersAlive.remove(pcge.getPlayer());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent pqe) {
        players.remove(pqe.getPlayer());
        playersAlive.remove(pqe.getPlayer());
    }

    @Override
    public void run() {
        World world = Bukkit.getWorld("world");
        for (Entity e :world.getNearbyEntities(new Location(world, 1000,14,-1000),10,10,10) ) {
            if (e instanceof Player) {
                players.add((Player) e);
                playersAlive.add((Player) e);
            }
        }
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
            pasteSchematic("sandrun1body",1000,105,-1000,true);
            pasteSchematic("sandrun1head",1000,105,-1000,true);
            for (int i = addition; i > 0; i--) {
                pasteSchematic("sandrun1body",1000,105 - i * 11,-1000,true);
            }
            Bukkit.getPluginManager().registerEvents(this,plugin);
            Bukkit.getScheduler().runTask(plugin, ()-> {
                world.getBlockAt(1000,15,-996).setType(Material.AIR);
                Block block = world.getBlockAt(996,15,-1000);
                block.setType(Material.OAK_BUTTON);
                BlockData data = block.getBlockData().clone();
                ((Directional)data).setFacing(BlockFace.EAST);
                block.setBlockData(data);
                for (Player p : players) {
                    p.teleport(new Location(world, 1000.5,106.0,-999.5));
                    p.sendTitle("§a游戏还有 10 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                    p.getInventory().clear();
                }
            });
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 5 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                }
            },100);
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 4 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                }
            },120);
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 3 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                }
            },140);
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 2 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                }
            },160);
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 1 秒开始",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,1f);
                }
            },180);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player p: players) {
                    p.sendTitle("§e游戏开始！",null,2,16,2);
                    p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_HARP,1f,2f);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,999999,49,false,false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,999999,0,false,false));
                }

            }, 200);
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
                            for (Block b: blocks) {
                                /*
                                if (!b.getType().equals(Material.SANDSTONE) && !b.getType().equals(Material.ANDESITE) && !b.getType().equals(Material.GLOWSTONE)) {
                                    continue;
                                }*/
                                BlockData data = b.getBlockData();
                                b.setType(Material.AIR);
                                FallingBlock fb = world.spawnFallingBlock(new Location(world,b.getX() + 0.5, b.getY(), b.getZ() + 0.5), data);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    fb.remove();
                                }, 15);
                            }
                        }, 8);
                    }

                },200,1));
            taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->{
                Player playerOut = null;
                for (Player p: playersAlive) {
                    if (p.getLocation().getY() <= 35) {
                        playerOut = p;
                        break;
                    }
                }
                if (playerOut != null) {
                    for (Player p : players) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§f§l" + playerOut.getName() + " §c坠入深渊！"));
                    }
                    playerOut.setGameMode(GameMode.SPECTATOR);
                    playersAlive.remove(playerOut);
                }

            },200,1));
            taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (playersAlive.size() <= 1) {
                    Player winner = playersAlive.get(0);
                    spawnFirework(winner.getLocation());
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        spawnFirework(winner.getLocation());
                    },8);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        spawnFirework(winner.getLocation());
                    },16);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        spawnFirework(winner.getLocation());
                    },24);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        spawnFirework(winner.getLocation());
                    },32);
                    List<Player> playersCopy = new ArrayList<>(players);
                    for (Player p : playersCopy) {
                        p.sendTitle("§e" + playersAlive.get(0).getName() + " §b获胜了！",null,5,50, 5);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            p.teleport(new Location(world,1000.5,14,-999.5));
                            Bukkit.getPluginManager().callEvent(new PlayerEndGameEvent(p));
                            pasteSchematic("sandrunempty",1000,105,-1000,false);
                        },100);
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                        Block block = world.getBlockAt(1000,15,-996);
                        block.setType(Material.OAK_BUTTON);
                        BlockData data = block.getBlockData().clone();
                        ((Directional)data).setFacing(BlockFace.NORTH);
                        block.setBlockData(data);
                        world.getBlockAt(996,15,-1000).setType(Material.AIR);
                        HandlerList.unregisterAll(this);
                    },100);
                    players.clear();
                    playersAlive.clear();
                    List<Integer> taskIdsCopy = new ArrayList<>(taskIds);
                    taskIds.clear();
                    for (int i : taskIdsCopy) {
                        Bukkit.getScheduler().cancelTask(i);
                    }
                }

            },200,1));
        }
    }
    public static void spawnFirework(Location location){
        Location loc = location;
        loc.setY(loc.getY() + 0.9);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
    }
    public long getTime(World world) {
        return world.getGameTime();
    }
    private void pasteSchematic(String name, double x, double y ,double z, boolean ignoreAir) {
        File file = new File("plugins/WorldEdit/schematics/" + name + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard clipboard = null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }catch (Exception e) {
            e.printStackTrace();
        }
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x,y,z))
                    .ignoreAirBlocks(ignoreAir)
                    .build();
            Operations.complete(operation);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Block[] getBlockss(Player p) {
        Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        double x = p.getLocation().getX();
        double z = p.getLocation().getZ();
        double xOffSet = x - (int)(Math.floor(x));
        double zOffset = z - (int)(Math.floor(z));
        if (xOffSet < 0.3) {
            if (zOffset < 0.3) {
                return new Block[] {block,block.getRelative(BlockFace.NORTH_WEST),block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.WEST)};
            } else if (zOffset > 0.7) {
                return new Block[] {block,block.getRelative(BlockFace.SOUTH_WEST),block.getRelative(BlockFace.SOUTH),block.getRelative(BlockFace.WEST)};
            } else {
                return new Block[] {block,block.getRelative(BlockFace.WEST)};
            }
        } else if (xOffSet > 0.7) {
            if (zOffset < 0.3) {
                return new Block[] {block,block.getRelative(BlockFace.NORTH_EAST),block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.EAST)};
            } else if (zOffset > 0.7) {
                return new Block[] {block,block.getRelative(BlockFace.SOUTH_EAST),block.getRelative(BlockFace.SOUTH),block.getRelative(BlockFace.EAST)};
            } else {
                return new Block[] {block,block.getRelative(BlockFace.EAST)};
            }

        } else {
            if (zOffset < 0.3) {
                return new Block[] {block,block.getRelative(BlockFace.NORTH)};
            } else if (zOffset > 0.7) {
                return new Block[] {block,block.getRelative(BlockFace.SOUTH)};
            } else {
                return new Block[] {block};
            }

        }
    }
}
