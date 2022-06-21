package main;

import entity.Entity;
import world.Tile;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import world.TileManager;
import entity.*;
import pickup.*;

public class CollisionChecker {
    private static class Distance {
        public int x, y;
        public Distance (int dx, int dy) {
            this.x = dx;
            this.y = dy;
        }
    }
    private static boolean detectCollision (Area one, Area two, Distance d) {
        Area a = new Area(one);

        // We need to translate one of the solidAreas because they dont have the same point of reference
        AffineTransform matrix = new AffineTransform();
        matrix.translate(d.x, d.y);
        Area b = two.createTransformedArea(matrix);

        a.intersect(b); // Sets [a] to intersection of [a] and [b]
        return a.isEmpty() ? false : true; // If intersection is empty then that means there is no collision
    }
    static Distance calculateDistance (Entity origin, int x, int y) {
        int dx = 0, dy = 0;
        switch (origin.direction) {
            case "up":
                dx = x - origin.x;
                dy = y - (origin.y - origin.speed);
                break;
            case "down":
                dx = x - origin.x;
                dy = y - (origin.y + origin.speed);
                break;
            case "left":
                dx = x - (origin.x - origin.speed);
                dy = y - origin.y;
                break;
            case "right":
                dx = x - (origin.x + origin.speed);
                dy = y - origin.y;
                break;
            default:
                System.out.println("Unknown direction: " + origin.direction);
        }
        return new Distance (dx, dy);
    }
    public static void checkPickup (Player player) {
        for (Pickup pickup : TileManager.currentRoom.pickupList) {
            if (detectCollision(player.solidArea, pickup.solidArea, calculateDistance(player, pickup.x, pickup.y)))
                pickup.getPickedUp(player);
        }
    }
    public static void checkMonster (Monster monster) {
        checkEntity(monster);
        Player player = TileManager.currentRoom.player;
        boolean flag = detectCollision(player.solidArea, monster.solidArea, calculateDistance(monster, player.x, player.y));
        if (flag && player.attacking == true)
            monster.takeDamage(player);
        else if (flag && player.invulnerable == false)
            player.takeDamage(monster);
    }
    public static void checkPlayer (Player player) {
        checkEntity (player);
        for (Monster monster : TileManager.currentRoom.monsterList) {
            boolean flag = detectCollision(player.solidArea, monster.solidArea, calculateDistance(player, monster.x, monster.y));
            if (flag && player.attacking == true)
                monster.takeDamage(player);
            else if (flag && player.invulnerable == false)
                player.takeDamage(monster);
        }
    }
    public static void findInteraction (Player player) {
        for (NPC npc : TileManager.currentRoom.npcList) {
            if (detectCollision(player.solidArea, npc.solidArea, calculateDistance(player, npc.x, npc.y)))
                player.interactionNPC = npc;
        }
    }
    static void checkEntity (Entity entity) {
        Rectangle bounds = entity.solidArea.getBounds();

        // Positions of each side of the bounding rectangle
        int entityLeftX = entity.x + bounds.x;
        int entityRightX = entity.x + bounds.x + bounds.width;
        int entityTopY = entity.y + bounds.y;
        int entityBotY = entity.y + bounds.y + bounds.height;

        // Indexes used to get adjacent tiles
        int entityLeftCol = Math.max(entityLeftX/GamePanel.tileSize, 0);
        int entityRightCol = Math.min(entityRightX/GamePanel.tileSize, GamePanel.colNum-1);
        int entityTopRow = Math.max(entityTopY/GamePanel.tileSize, 0);
        int entityBotRow = Math.min(entityBotY/GamePanel.tileSize, GamePanel.rowNum-1);

        // Calculate possibly changed indexes after shifting the hitbox according to current direction
        switch (entity.direction) {
            case "up":
                entityTopRow = Math.max((entityTopY - entity.speed)/GamePanel.tileSize, 0);
                entityBotRow = Math.max((entityBotY - entity.speed)/GamePanel.tileSize, 0);
                break;
            case "down":
                entityTopRow = Math.min((entityTopY + entity.speed)/GamePanel.tileSize, GamePanel.rowNum-1);
                entityBotRow = Math.min((entityBotY + entity.speed)/GamePanel.tileSize, GamePanel.rowNum-1);
                break;
            case "left":
                entityLeftCol = Math.max((entityLeftX - entity.speed)/GamePanel.tileSize, 0);
                entityRightCol = Math.max((entityRightX - entity.speed)/GamePanel.tileSize, 0);
                break;
            case "right":
                entityLeftCol = Math.min((entityLeftX + entity.speed)/GamePanel.tileSize, GamePanel.colNum-1);
                entityRightCol = Math.min((entityRightX + entity.speed)/GamePanel.tileSize, GamePanel.colNum-1);
                break;
            default:
                System.out.println("Unknown direction: " + entity.direction);
                break;
        }

        // Save adjacent tiles
        Tile NW = TileManager.tiles[TileManager.currentRoom.roomTileNum[entityTopRow][entityLeftCol]];
        Tile NE = TileManager.tiles[TileManager.currentRoom.roomTileNum[entityTopRow][entityRightCol]];
        Tile SW = TileManager.tiles[TileManager.currentRoom.roomTileNum[entityBotRow][entityLeftCol]];
        Tile SE = TileManager.tiles[TileManager.currentRoom.roomTileNum[entityBotRow][entityRightCol]];

        int top = entityTopRow * GamePanel.tileSize;
        int bot = entityBotRow * GamePanel.tileSize;
        int left = entityLeftCol * GamePanel.tileSize;
        int right = entityRightCol * GamePanel.tileSize;

        if (detectCollision(entity.solidArea, NW.solidArea, calculateDistance(entity, left, top)) || // Check north-west tile
            detectCollision(entity.solidArea, NE.solidArea, calculateDistance(entity, right, top)) || // Check north-east tile
            detectCollision(entity.solidArea, SW.solidArea, calculateDistance(entity, left, bot)) || // Check south-west tile
            detectCollision(entity.solidArea, SE.solidArea, calculateDistance(entity, right, bot))) { // Check south-east tile
                entity.collisionOn = true;
        }

        // Check collision with other entities
        for (Entity e : TileManager.currentRoom.entityList) {
            if (entity.equals(e))
                continue;
            if (detectCollision(entity.solidArea, e.solidArea, calculateDistance(entity, e.x, e.y))) 
                entity.collisionOn = true;                  
        }
    }
}
