package me.liuli.falcon.check.combat.fakePlayer;

import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import me.liuli.falcon.utils.MathUtil;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class FakePlayer {
    public final String name;
    private final Position pos;
    private final UUID uuid;
    private final long entityId;
    public float health = 20;
    public Item handEquip = new Item(0, 0);
    public Item headEquip = new Item(0, 0);
    public Item chestEquip = new Item(0, 0);
    public Item legEquip = new Item(0, 0);
    public Item bootEquip = new Item(0, 0);
    //rotate
    public boolean inRotate = false;
    public int rot = 180;
    public int rotCircle = 0;
    private Position lastPos = new Position(0, 0, 0);

    public FakePlayer(String name, Position pos) {
        Random random = new Random();
        this.entityId = MathUtil.randInt(10000, 1000000);
        this.name = name;
        this.pos = pos;
        this.uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public long getEntityId() {
        return entityId;
    }

    public void spawnToPlayer(Player player) {
        PlayerListPacket playerListPacket = new PlayerListPacket();
        playerListPacket.type = PlayerListPacket.TYPE_ADD;
        playerListPacket.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, new Skin())};
        player.dataPacket(playerListPacket);
        AddPlayerPacket addPlayerPacket = new AddPlayerPacket();
        addPlayerPacket.uuid = uuid;
        addPlayerPacket.username = name;
        addPlayerPacket.entityRuntimeId = entityId;
        addPlayerPacket.entityUniqueId = entityId;
        addPlayerPacket.x = (float) pos.x;
        addPlayerPacket.y = (float) pos.y;
        addPlayerPacket.z = (float) pos.z;
        addPlayerPacket.speedX = 0;
        addPlayerPacket.speedY = 0;
        addPlayerPacket.speedZ = 0;
        addPlayerPacket.yaw = (float) player.yaw;
        addPlayerPacket.pitch = (float) player.pitch;
        addPlayerPacket.item = handEquip;
        addPlayerPacket.metadata.put(new StringEntityData(4, name));
        player.dataPacket(addPlayerPacket);
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.slots[0] = headEquip;
        mobArmorEquipmentPacket.slots[1] = chestEquip;
        mobArmorEquipmentPacket.slots[2] = legEquip;
        mobArmorEquipmentPacket.slots[3] = bootEquip;
        mobArmorEquipmentPacket.eid = entityId;
        player.dataPacket(mobArmorEquipmentPacket);
    }

    public void updateEquip(Player player) {
        MobEquipmentPacket mobEquipmentPacket = new MobEquipmentPacket();
        mobEquipmentPacket.eid = entityId;
        mobEquipmentPacket.item = handEquip;
        mobEquipmentPacket.hotbarSlot = 0;
        mobEquipmentPacket.windowId = 0;
        mobEquipmentPacket.inventorySlot = 0;
        player.dataPacket(mobEquipmentPacket);
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.slots[0] = headEquip;
        mobArmorEquipmentPacket.slots[1] = chestEquip;
        mobArmorEquipmentPacket.slots[2] = legEquip;
        mobArmorEquipmentPacket.slots[3] = bootEquip;
        mobArmorEquipmentPacket.eid = entityId;
        player.dataPacket(mobArmorEquipmentPacket);
    }

    public void showDamage(Player player) {
        EntityEventPacket entityEventPacket = new EntityEventPacket();
        entityEventPacket.eid = entityId;
        entityEventPacket.event = EntityEventPacket.HURT_ANIMATION;
        player.dataPacket(entityEventPacket);
    }

    public void doSwing(Player player) {
        AnimatePacket animatePacket = new AnimatePacket();
        animatePacket.action = AnimatePacket.Action.SWING_ARM;
        animatePacket.eid = entityId;
        animatePacket.rowingTime = 5;
        player.dataPacket(animatePacket);
    }

    public void moveBot(Position pos, boolean onGround, Player player) {
        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.eid = entityId;
        movePlayerPacket.x = (float) pos.x;
        movePlayerPacket.y = (float) pos.y;
        movePlayerPacket.z = (float) pos.z;
        movePlayerPacket.yaw = (float) player.yaw;
        movePlayerPacket.pitch = (float) player.pitch;
        movePlayerPacket.headYaw = 0;
        movePlayerPacket.onGround = onGround;
        if (pos.distance(new Vector3(lastPos.x, lastPos.y, lastPos.z)) < 7.5) {
            movePlayerPacket.mode = MovePlayerPacket.MODE_NORMAL;
        } else {
            movePlayerPacket.mode = MovePlayerPacket.MODE_TELEPORT;
        }
        player.dataPacket(movePlayerPacket);
    }

    public void updateNametag(Player player, String nameTag) {
        SetEntityDataPacket setEntityDataPacket = new SetEntityDataPacket();
        setEntityDataPacket.eid = entityId;
        setEntityDataPacket.metadata = new EntityMetadata().put(new StringEntityData(4, nameTag));
        player.dataPacket(setEntityDataPacket);
    }

    public void updateHealth(Player player) {
        SetEntityDataPacket setEntityDataPacket = new SetEntityDataPacket();
        setEntityDataPacket.eid = entityId;
        setEntityDataPacket.metadata = new EntityMetadata().put(new FloatEntityData(1, health));
        player.dataPacket(setEntityDataPacket);
    }

    public void removeFromPlayer(Player player) {
        PlayerListPacket playerListPacket = new PlayerListPacket();
        playerListPacket.type = PlayerListPacket.TYPE_REMOVE;
        playerListPacket.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, null)};
        player.dataPacket(playerListPacket);
        RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket();
        removeEntityPacket.eid = entityId;
        player.dataPacket(removeEntityPacket);
    }
}
