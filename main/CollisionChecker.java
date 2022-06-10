package main;

import entity.Entity;
import world.Tile;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import world.TileManager;

public class CollisionChecker {
    GamePanel gp;
    public CollisionChecker(GamePanel gp){
        this.gp=gp;
    }
    private void detectCollision (Entity entity, Area solidArea, int dx, int dy) {
        // t1B - Tile solidArea, entB - entity solidArea
        // dx, dy - distance to upper-left corner of the tile (@)
        // @-----------------#   /\
        // |                 |   || dy+entB.y
        // |  dx+entB.x      |   \/ 
        // | <=========> #-------#   
        // |             |  entB | 
        // |             #-------#
        // |    #-----#      |
        // |    | t1B |      |
        // |    |     |      |
        // #----#-----#------#
        Area one = new Area(solidArea);
        AffineTransform matrix = new AffineTransform();
        matrix.translate(dx, dy);
        Area two = entity.solidArea.createTransformedArea(matrix);
        one.intersect(two);
        if (!(one.isEmpty())) {
            entity.collisionOn = true;
        }
    }
    public void checkTiles (Entity entity) {
        Rectangle bounds = entity.solidArea.getBounds();
        int entityLeftX = entity.x + bounds.x;
        int entityRightX = entity.x + bounds.x + bounds.width;
        int entityTopY = entity.y + bounds.y;
        int entityBotY = entity.y + bounds.y + bounds.height;

        int entityLeftCol = Math.max(entityLeftX/GamePanel.tileSize, 0);
        int entityRightCol = Math.min(entityRightX/GamePanel.tileSize, GamePanel.colNum-1);
        int entityTopRow = Math.max(entityTopY/GamePanel.tileSize, 0);
        int entityBotRow = Math.min(entityBotY/GamePanel.tileSize, GamePanel.rowNum-1);

        Tile tileNW, tileNE, tileSW, tileSE;
        int dx, dy;

        switch(entity.direction){
            case "up":
                entityTopRow = Math.max((entityTopY - entity.speed)/GamePanel.tileSize, 0);

                tileNW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityLeftCol]];
                tileNE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityRightCol]];
                tileSW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityLeftCol]];
                tileSE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityRightCol]];
                
                // North-west tile
                dx = entity.x - (entityLeftCol * GamePanel.tileSize);
                dy = (entity.y - entity.speed) - (entityTopRow * GamePanel.tileSize);
                detectCollision (entity, tileNW.solidArea, dx, dy);

                // North-east tile
                dx = entity.x - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileNE.solidArea, dx, dy);

                // South-west tile
                dx = entity.x - (entityLeftCol * GamePanel.tileSize);
                dy = (entity.y - entity.speed) - (entityBotRow * GamePanel.tileSize);
                detectCollision (entity, tileSW.solidArea, dx, dy);

                // South-east tile
                dx = entity.x - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileSE.solidArea, dx, dy);
                
                for (Entity e : gp.tM.currentRoom.entityList) {
                    dx = entity.x - e.x;
                    dy = (entity.y - entity.speed) - e.y;
                    detectCollision(entity, e.solidArea, dx, dy);
                }
                break;

            case "down":
                entityBotRow = Math.min((entityBotY + entity.speed)/GamePanel.tileSize, GamePanel.rowNum-1);
                
                tileNW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityLeftCol]];
                tileNE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityRightCol]];
                tileSW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityLeftCol]];
                tileSE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityRightCol]];
                
                // North-west tile
                dx = entity.x - (entityLeftCol * GamePanel.tileSize);
                dy = (entity.y + entity.speed) - (entityTopRow * GamePanel.tileSize);
                detectCollision (entity, tileNW.solidArea, dx, dy);

                // North-east tile
                dx = entity.x - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileNE.solidArea, dx, dy);

                // South-west tile
                dx = entity.x - (entityLeftCol * GamePanel.tileSize);
                dy = (entity.y + entity.speed) - (entityBotRow * GamePanel.tileSize);
                detectCollision (entity, tileSW.solidArea, dx, dy);

                // South-east tile
                dx = entity.x - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileSE.solidArea, dx, dy);   
                for (Entity e : gp.tM.currentRoom.entityList) {
                    dx = entity.x - e.x;
                    dy = (entity.y + entity.speed) - e.y;
                    detectCollision(entity, e.solidArea, dx, dy);
                }      
                break;

            case "left":
                entityLeftCol = Math.max((entityLeftX - entity.speed)/GamePanel.tileSize, 0);
                
                tileNW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityLeftCol]];
                tileNE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityRightCol]];
                tileSW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityLeftCol]];
                tileSE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityRightCol]];
                
                // North-west tile
                dx = (entity.x - entity.speed) - (entityLeftCol * GamePanel.tileSize);
                dy = entity.y - (entityTopRow * GamePanel.tileSize);
                detectCollision (entity, tileNW.solidArea, dx, dy);

                // North-east tile
                dy = entity.y - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileNE.solidArea, dx, dy);

                // South-west tile
                dx = (entity.x - entity.speed) - (entityLeftCol * GamePanel.tileSize);
                dy = entity.y - (entityBotRow * GamePanel.tileSize);
                detectCollision (entity, tileSW.solidArea, dx, dy);

                // South-east tile
                dy = entity.y - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileSE.solidArea, dx, dy);  
                
                for (Entity e : gp.tM.currentRoom.entityList) {
                    dx = (entity.x - entity.speed) - e.x;
                    dy = entity.y - e.y;
                    detectCollision(entity, e.solidArea, dx, dy);
                }
                break;

            case "right":
                entityRightCol = Math.min((entityRightX + entity.speed)/GamePanel.tileSize, GamePanel.colNum-1);
                
                tileNW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityLeftCol]];
                tileNE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityTopRow][entityRightCol]];
                tileSW = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityLeftCol]];
                tileSE = TileManager.tiles[gp.tM.currentRoom.roomTileNum[entityBotRow][entityRightCol]];
                
                // North-west tile
                dx = (entity.x + entity.speed) - (entityLeftCol * GamePanel.tileSize);
                dy = entity.y - (entityTopRow * GamePanel.tileSize);
                detectCollision (entity, tileNW.solidArea, dx, dy);

                // North-east tile
                dy = entity.y - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileNE.solidArea, dx, dy);

                // South-west tile
                dx = (entity.x + entity.speed) - (entityLeftCol * GamePanel.tileSize);
                dy = entity.y - (entityBotRow * GamePanel.tileSize);
                detectCollision (entity, tileSW.solidArea, dx, dy);

                // South-east tile
                dy = entity.y - (entityRightCol * GamePanel.tileSize);
                detectCollision (entity, tileSE.solidArea, dx, dy);    
                
                for (Entity e : gp.tM.currentRoom.entityList) {
                    dx = (entity.x + entity.speed) - e.x;
                    dy = entity.y - e.y;
                    detectCollision(entity, e.solidArea, dx, dy);
                }
                break;
        }
    }
}