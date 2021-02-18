package me.liuli.falcon.check.combat.fakePlayer;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import me.liuli.falcon.cache.CheckCache;
import me.liuli.falcon.manager.CheckResult;
import me.liuli.falcon.manager.CheckType;
import me.liuli.falcon.utils.MathUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FakePlayerManager {
    private static Enchantment[] enchantments = new Enchantment[]{Enchantment.get(Enchantment.ID_PROTECTION_ALL).setLevel(1), Enchantment.get(Enchantment.ID_PROTECTION_ALL).setLevel(2), Enchantment.get(Enchantment.ID_PROTECTION_FIRE).setLevel(1), Enchantment.get(Enchantment.ID_PROTECTION_FIRE).setLevel(2), Enchantment.get(Enchantment.ID_PROTECTION_FALL).setLevel(1), Enchantment.get(Enchantment.ID_PROTECTION_FALL).setLevel(2), Enchantment.get(Enchantment.ID_PROTECTION_EXPLOSION).setLevel(1), Enchantment.get(Enchantment.ID_PROTECTION_EXPLOSION).setLevel(2),};
    private static Item[] heads = new Item[]{new Item(0, 0), new Item(298, 0), new Item(302, 0), new Item(306, 0), new Item(310, 0), new Item(314, 0)};
    private static Item[] chests = new Item[]{new Item(0, 0), new Item(299, 0), new Item(303, 0), new Item(307, 0), new Item(311, 0), new Item(315, 0)};
    private static Item[] legs = new Item[]{new Item(0, 0), new Item(300, 0), new Item(304, 0), new Item(308, 0), new Item(312, 0), new Item(316, 0)};
    private static Item[] boots = new Item[]{new Item(0, 0), new Item(301, 0), new Item(305, 0), new Item(309, 0), new Item(313, 0), new Item(317, 0)};
    private static String[] nameBase = "Air,Stone,Granite,Diorite,Andesite,GrassBlock,Dirt,CoarseDirt,Podzol,Cobblestone,OakPlanks,BirchPlanks,OakSapling,Bedrock,Sand,RedSand,Gravel,GoldOre,IronOre,CoalOre,OakLog,SpruceLog,BirchLog,JungleLog,AcaciaLog,DarkOakLog,OakLeaves,BirchLeaves,Sponge,WetSponge,Glass,LapisOre,LapisBlock,Dispenser,Sandstone,NoteBlock,PoweredRail,Cobweb,Grass,Fern,DeadBush,Piston,WhiteWool,OrangeWool,MagentaWool,YellowWool,LimeWool,PinkWool,GrayWool,CyanWool,PurpleWool,BlueWool,BrownWool,GreenWool,RedWool,BlackWool,Dandelion,Poppy,BlueOrchid,Allium,AzureBluet,RedTulip,OrangeTulip,WhiteTulip,PinkTulip,OxeyeDaisy,Cornflower,WitherRose,RedMushroom,SugarCane,GoldBlock,IronBlock,OakSlab,SpruceSlab,BirchSlab,JungleSlab,AcaciaSlab,DarkOakSlab,StoneSlab,BrickSlab,QuartzSlab,Bricks,Tnt,Bookshelf,Obsidian,Torch,EndRod,ChorusPlant,PurpurBlock,Spawner,OakStairs,Chest,DiamondOre,Farmland,Furnace,Ladder,Rail,Lever,RedstoneOre,Snow,Ice,SnowBlock,Cactus,Clay,Jukebox,OakFence,SpruceFence,BirchFence,JungleFence,AcaciaFence,Pumpkin,Netherrack,SoulSand,SoulSoil,Glowstone,OakTrapdoor,StoneBricks,IronBars,Chain,GlassPane,Melon,Vine,BrickStairs,Mycelium,LilyPad,EndStone,DragonEgg,EmeraldOre,EnderChest,BirchStairs,Beacon,StoneButton,OakButton,BirchButton,Anvil,Hopper,QuartzBlock,Dropper,Barrier,HayBlock,WhiteCarpet,LimeCarpet,PinkCarpet,GrayCarpet,CyanCarpet,BlueCarpet,BrownCarpet,GreenCarpet,RedCarpet,BlackCarpet,Terracotta,CoalBlock,PackedIce,SlimeBlock,GrassPath,Sunflower,Lilac,RoseBush,Peony,TallGrass,LargeFern,Prismarine,SeaLantern,MagmaBlock,BoneBlock,Observer,ShulkerBox,RedConcrete,BlueIce,IronDoor,OakDoor,SpruceDoor,BirchDoor,JungleDoor,AcaciaDoor,DarkOakDoor,Repeater,Comparator,Apple,Bow,Arrow,Coal,Charcoal,Diamond,IronIngot,GoldIngot,WoodenSword,WoodenAxe,WoodenHoe,StoneSword,StoneShovel,StoneAxe,StoneHoe,GoldenSword,GoldenAxe,GoldenHoe,IronSword,IronShovel,IronPickaxe,IronAxe,IronHoe,DiamondAxe,DiamondHoe,Stick,Bowl,String,Feather,Gunpowder,WheatSeeds,Wheat,Bread,IronHelmet,IronBoots,GoldenBoots,Flint,Porkchop,Painting,GoldenApple,OakSign,SpruceSign,BirchSign,JungleSign,AcaciaSign,DarkOakSign,Bucket,WaterBucket,LavaBucket,Minecart,Saddle,Redstone,Snowball,OakBoat,Leather,MilkBucket,CodBucket,Brick,ClayBall,Paper,Book,SlimeBall,Egg,Compass,FishingRod,Clock,Cod,Salmon,Pufferfish,CookedCod,InkSac,CocoaBeans,LapisLazuli,WhiteDye,OrangeDye,MagentaDye,YellowDye,LimeDye,PinkDye,GrayDye,CyanDye,PurpleDye,BrownDye,GreenDye,RedDye,BoneMeal,Bone,Sugar,Cake,WhiteBed,OrangeBed,MagentaBed,YellowBed,LimeBed,PinkBed,GrayBed,CyanBed,PurpleBed,BlueBed,BrownBed,GreenBed,RedBed,BlackBed,Cookie,FilledMap,Shears,MelonSlice,MelonSeeds,Beef,CookedBeef,Chicken,RottenFlesh,EnderPearl,BlazeRod,GhastTear,GoldNugget,NetherWart,Potion,GlassBottle,SpiderEye,BlazePowder,MagmaCream,Cauldron,EnderEye,BatSpawnEgg,BeeSpawnEgg,CatSpawnEgg,CodSpawnEgg,CowSpawnEgg,FoxSpawnEgg,PigSpawnEgg,VexSpawnEgg,FireCharge,WrittenBook,Emerald,ItemFrame,FlowerPot,Carrot,Potato,BakedPotato,Map,ZombieHead,PlayerHead,CreeperHead,NetherStar,PumpkinPie,NetherBrick,Quartz,TntMinecart,Rabbit,RabbitStew,RabbitFoot,RabbitHide,ArmorStand,Lead,NameTag,Mutton,WhiteBanner,LimeBanner,PinkBanner,GrayBanner,CyanBanner,BlueBanner,BrownBanner,GreenBanner,RedBanner,BlackBanner,EndCrystal,ChorusFruit,Beetroot,TippedArrow,Shield,Elytra,SpruceBoat,BirchBoat,JungleBoat,AcaciaBoat,DarkOakBoat,IronNugget,MusicDisc".split(",");

    public static FakePlayer spawnFake(Player player) {
        FakePlayer fakePlayer = new FakePlayer(nameGen(), player.getPosition());
        equipGen(fakePlayer);
        fakePlayer.spawnToPlayer(player);
        fakePlayer.updateHealth(player);
        Timer timer = new Timer("setTimeout", true);
        timer.schedule(new TimerTask() {
            int equipUpdCount = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    timer.cancel();
                    return;
                }
                equipUpdCount++;
                if (equipUpdCount >= 300) {
                    equipGen(fakePlayer);
                    fakePlayer.updateEquip(player);
                    equipUpdCount = 0;
                    fakePlayer.doSwing(player);
                }
                updateBot(player);
            }
        }, 500, 50);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fakePlayer.doSwing(player);
            }
        }, 1000);
        return fakePlayer;
    }

    public static CheckResult checkBotHurt(DataPacketReceiveEvent event, InventoryTransactionPacket packet) {
        InventoryTransactionPacket inventoryTransactionPacket = (InventoryTransactionPacket) packet;
        long entityId = 0;
        if (inventoryTransactionPacket.transactionType == 3) {
            UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) inventoryTransactionPacket.transactionData;
            entityId = useItemOnEntityData.entityRuntimeId;
        }
        FakePlayer fakePlayer = CheckCache.get(event.getPlayer()).fakePlayer;
        if (entityId == fakePlayer.getEntityId()) {
            FakePlayerManager.botHurt(event.getPlayer());
            if (!(Math.abs(fakePlayer.rot) < CheckType.KA_BOT.otherData.getInteger("mustHitRot"))) {
                return new CheckResult("Trying to attack a fakeplayer(rot=" + Math.abs(fakePlayer.rot) + ")");
            }
        }
        return CheckResult.PASSED;
    }

    public static void playerHurt(Player player) {
        CheckCache.get(player).lastHurt = new Date().getTime();
    }

    public static void botHurt(Player player) {
        CheckCache.get(player).lastHurt = new Date().getTime();
        FakePlayer fakePlayer = CheckCache.get(player).fakePlayer;
        fakePlayer.showDamage(player);
        if (fakePlayer.health > 2) {
            fakePlayer.health--;
            fakePlayer.updateHealth(player);
        }
    }

    public static void updateBot(Player player) {
        Location location = player.getLocation();
        boolean onGround = MathUtil.randBool();
        FakePlayer fakePlayer = CheckCache.get(player).fakePlayer;
        String lastNametag = CheckCache.get(player).nametag;
        if (!lastNametag.equals(player.getNameTag())) {
            CheckCache.get(player).nametag = player.getNameTag();
            fakePlayer.updateNametag(player, player.getNameTag().replaceAll(player.getName(), fakePlayer.name));
        }
        if ((new Date().getTime() - CheckCache.get(player).lastHurt) < 15000) {
            double rot = location.yaw + fakePlayer.rot, y = location.y + 1.62;

            if (fakePlayer.inRotate) {
                CheckCache.get(player).lastHurt = System.currentTimeMillis();

                fakePlayer.rot -= CheckType.KA_BOT.otherData.getInteger("rotSpeed");
                if (fakePlayer.rot <= -180) {
                    fakePlayer.rot = 180;
                    fakePlayer.rotCircle++;
                }
                if (fakePlayer.rotCircle >= CheckType.KA_BOT.otherData.getInteger("rotCount")) {
                    fakePlayer.inRotate = false;
                    fakePlayer.rotCircle = 0;
                }
            }

            if (!onGround) {
                y += MathUtil.randDouble(0, 0.5);
            }
            fakePlayer.moveBot(new Position((location.x - Math.sin(rot * Math.PI / 180) * 2) + MathUtil.randDouble(-0.5, 0.5),
                    y, (location.z + Math.cos(rot * Math.PI / 180) * 2) + MathUtil.randDouble(-0.5, 0.5)), onGround, player);
        } else {
            fakePlayer.moveBot(new Position(location.x, MathUtil.randDouble(250, 252), location.z), false, player);
        }
        if (fakePlayer.health < 5) {
            fakePlayer.health = 20;
        }
    }

    private static void equipGen(FakePlayer fakePlayer) {
        if (MathUtil.randBool()) {
            fakePlayer.handEquip = new Item(MathUtil.randInt(265, 293));
        } else {
            fakePlayer.handEquip = new Item(0, 0);
        }
        fakePlayer.headEquip = heads[MathUtil.randInt(0, heads.length - 1)];
        if (MathUtil.randBool()) {
            fakePlayer.headEquip.addEnchantment(enchantments[MathUtil.randInt(0, enchantments.length - 1)]);
        }
        fakePlayer.chestEquip = chests[MathUtil.randInt(0, chests.length - 1)];
        if (MathUtil.randBool()) {
            fakePlayer.chestEquip.addEnchantment(enchantments[MathUtil.randInt(0, enchantments.length - 1)]);
        }
        fakePlayer.legEquip = legs[MathUtil.randInt(0, legs.length - 1)];
        if (MathUtil.randBool()) {
            fakePlayer.legEquip.addEnchantment(enchantments[MathUtil.randInt(0, enchantments.length - 1)]);
        }
        fakePlayer.bootEquip = boots[MathUtil.randInt(0, boots.length - 1)];
        if (MathUtil.randBool()) {
            fakePlayer.bootEquip.addEnchantment(enchantments[MathUtil.randInt(0, enchantments.length - 1)]);
        }
    }

    private static String nameGen() {
        String name = nameBase[MathUtil.randInt(0, nameBase.length - 1)];
        name += MathUtil.randInt(0, 999);
        return name;
    }
}
