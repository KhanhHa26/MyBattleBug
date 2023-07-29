package BattleBugs;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.lang.annotation.Target;
import info.gridworld.actor.Actor;
import info.gridworld.actor.Rock;

public class BuzzLightyear extends BattleBug2012
{
    int rock = 0;
    boolean trapped = false; 
    boolean dangerEnemy = false; 
    boolean detectEnemies = false; 
    boolean shouldKill = false; 

    public BuzzLightyear(int str, int def, int spd, String name, Color col)
    {
            super(str, def, spd, name, col);
    }

    public void act()
    {

        //Call the getPowerUpLocs() method and store the result in a variable named puLocs.
        ArrayList<Location> puLocs = getPowerUpLocs();
        ArrayList<Actor> actors = getActors();
        ArrayList<PowerUp> powers = getPowerUps(); 
        ArrayList<BattleBug> enemies = new ArrayList<BattleBug>(); 
        int dir = 0;
        ArrayList<Actor> neighbors = getNeighbors();
        ArrayList<Location> validLocations = getValidAdjacentLocations();
        ArrayList<Location> occupiedLocation = getOccupiedAdjacentLocations(); 
        ArrayList<Location> emptyLocations = getEmptyAdjacentLocations();


    //--------------------------------------------------------------POWER UPS--------------------------------------------------------------
        //get the power at the closest location 
        double min = 0; 
        Location goTo = null; 
        PowerUp myPower = null; 
        if (puLocs.size() > 0) {
             min = distance(getLocation().getRow(), puLocs.get(0).getRow(), getLocation().getCol(), puLocs.get(0).getCol()); 
             goTo = puLocs.get(0); 
             myPower = powers.get(0); 
            for (int i = 1; i < puLocs.size(); i++) { 
                if (distance(getLocation().getRow(), puLocs.get(i).getRow(), getLocation().getCol(), puLocs.get(i).getCol()) < min) { 
                    min = distance(getLocation().getRow(), puLocs.get(i).getRow(), getLocation().getCol(), puLocs.get(i).getCol());
                    goTo = puLocs.get(i); 
                }
            }
        }
        
        //get the Speed powerUp
        //if our speed is < 10 and the distance is less than or equal min
        //get the strength powerUp
        //if our strength is < 8 and the distance is less than or equal min
        if (powers.size() > 0) {
            for (int i = 0; i < powers.size(); i++) { 
                //get speed power ups
                if (powers.get(i).getColor() == Color.BLUE && getSpeed() < 10) { 
                    if (distance(getLocation().getRow(), powers.get(i).getLocation().getRow(), getLocation().getCol(), powers.get(i).getLocation().getCol()) <= min) { 
                        myPower = powers.get(i); 
                        goTo = powers.get(i).getLocation();
                    }
                //get strength power up
                } else if (powers.get(i).getColor() == Color.RED && getStrength() < 10) { 
                    if (distance(getLocation().getRow(), powers.get(i).getLocation().getRow(), getLocation().getCol(), powers.get(i).getLocation().getCol()) <= min) { 
                        myPower = powers.get(i); 
                        goTo = powers.get(i).getLocation();
                    }
                }
            }
        }

        //--------------------------------------------------------------ENEMIES--------------------------------------------------------------
       
        addEnemies(actors, enemies);
        removeEnemies(enemies);
        if (detectEnemies(enemies) != null) { 
            attackEnemies(enemies, detectEnemies(enemies));
        } 
        

        //--------------------------------------------------------------MOVEMENTS--------------------------------------------------------------

        if (getNumAct() > 1 && getNumAct() % 40 == 0) { 
            rock++; 
        }
        
        if (getSpeed() >= 10) { 
            if (inDanger2()) { 
                avoidRock2();
            } else { 
                getStuck(neighbors, emptyLocations, goTo);
                if (trapped) { 
                    moveTwice(avoidObstacles(emptyLocations, goTo));
                } else {
                    moveTwice(goTo);
                }
            }

        } else { 
            if (inDanger()) { 
                avoidRock();
            } else { 
                getStuck(neighbors, emptyLocations, goTo);
                if(trapped){
                    goCloset(avoidObstacles(emptyLocations, goTo));
                } else {
                        goCloset(goTo);
                    }
                }
    
                }
            }

    //--------------------------------------------------------------HELPER METHODS--------------------------------------------------------------
    
    //----------------------------------MOVEMENT METHODS
    //distance method 
    public double distance(int x1, int x2, int y1, int y2) { 
        double output = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)); 
        return output; 
    }

    public boolean isInTheSameRow(Location target){
        return target.getRow() == getLocation().getRow();
    }
    public boolean isInTheSameCol(Location target){
        return target.getCol() == getLocation().getCol();
    }
    public boolean isInTheSameDiagonal(Location target) { 
        return (Math.abs(target.getCol() - getLocation().getCol()) == Math.abs(target.getRow() - getLocation().getRow())); 
    }

    //move method
    public void goCloset(Location target) { 
        //if north, south, east, west then move
        int dir = getDirectionToward(target);
        if (isInTheSameCol(target) || isInTheSameDiagonal(target) || isInTheSameRow(target)) { 
            if (getDirection() == dir) { 
                move(); 
            } else { 
                turnTo(dir);
            }
        } else { 
            int row = getLocation().getRow();
            int col = getLocation().getCol(); 
            int tarRow = target.getRow();
            int tarCol = target.getCol(); 
            //Quadrant III
            if (row > tarRow && col < tarCol) { 
                Location d = new Location (row - 1, col + 1); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move(); 
                } else { 
                    turnTo(d2);
                }

            //Quadrant IV
            } else if (row > tarRow && col > tarCol) { 
                Location d = new Location (row - 1, col - 1); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move(); 
                } else { 
                    turnTo(d2);
                }

            //Quadrant II
            } else if (row < tarRow && col < tarCol) { 
                Location d = new Location (row + 1, col + 1); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move(); 
                } else { 
                    turnTo(d2);
                }                
            } 

            //Quadrant I
            else if (row < tarRow && col > tarCol) { 
                Location d = new Location (row + 1, col - 1); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move(); 
                } else { 
                    turnTo(d2);
                }
            }
        }
    }

    //check if the bug is in the danger zone of rock
    public boolean inDanger() { 
        if (getNumAct() % 40 >= 37 ) { 
            if (getLocation().getCol() <= rock + 1|| getLocation().getCol() >= 25 - rock
                || getLocation().getRow() <= rock + 1|| getLocation().getRow() >= 25- rock) { 
                    return true; 
            }
        }
        return false; 
    }

    //move bug to safe zone
    public void avoidRock() {
        int row = getLocation().getRow();
        int col = getLocation().getCol(); 
        //Quadrant 1
        if (row <= 13 && col >= 13) { 
            row+=2;
            col-=2;
        //Quadrant 2
        } else if (row < 13 && col < 13) { 
            row+=2;
            col+=2;
        //Quadrant 3
        } else if (row > 13 && col <= 13) { 
            row-=2;
            col+=2;
        //Quadrant 4
        } else if (row > 13 && col > 13) { 
            row-=2; 
            col-=2;
        }
        goCloset(new Location(row, col));
    }

    //return the location to avoid the regulator/tombstone
    public Location avoidObstacles(ArrayList<Location> emptyLocations, Location target) { 
        Location goTo = emptyLocations.get(0); 
        if (target != null) {
        double min = distance(target.getRow(), emptyLocations.get(0).getRow(), target.getCol(), emptyLocations.get(0).getCol());
        for (int i = 1; i < emptyLocations.size(); i++) { 
            if (distance(target.getRow(), emptyLocations.get(i).getRow(), target.getCol(), emptyLocations.get(i).getCol()) < min) { 
                min = distance(target.getRow(), emptyLocations.get(i).getRow(), target.getCol(), emptyLocations.get(i).getCol());
                goTo = emptyLocations.get(i); 
            }
        }
    }
        return goTo; 
    }

    //check whether the bug is trapped by the regulator/tombstone
    public void getStuck(ArrayList<Actor> neighbors, ArrayList<Location> emptyLocations, Location target) {  
        int dir = getDirectionToward(avoidObstacles(emptyLocations, target)); 
        for (int i = 0; i < neighbors.size(); i++) { 
            if ((neighbors.get(i) instanceof Regulator && !canMove()) || (neighbors.get(i) instanceof Regulator && getDirection() == dir)
                || (neighbors.get(i) instanceof TombStone && !canMove()) || (neighbors.get(i) instanceof TombStone && getDirection() == dir)) { 
                trapped = true;
            } else { 
                trapped = false; 
            }
        }
    }

    //mehod for move2
    public void moveTwice(Location target) { 
        //if north, south, east, west then move
        int dir = getDirectionToward(target);
        if (isInTheSameCol(target) || isInTheSameDiagonal(target) || isInTheSameRow(target)) { 
            if (getDirection() == dir) { 
                move2(); 
            } else { 
                turnTo(dir);
            }
        } else { 
            int row = getLocation().getRow();
            int col = getLocation().getCol(); 
            int tarRow = target.getRow();
            int tarCol = target.getCol(); 
            //Quadrant III
            if (row > tarRow && col < tarCol) { 
                Location d = new Location (row - 2, col + 2); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move2(); 
                } else { 
                    turnTo(d2);
                }

            //Quadrant IV
            } else if (row > tarRow && col > tarCol) { 
                Location d = new Location (row - 2, col - 2); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move2(); 
                } else { 
                    turnTo(d2);
                }

            //Quadrant II
            } else if (row < tarRow && col < tarCol) { 
                Location d = new Location (row + 2, col + 2); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move2(); 
                } else { 
                    turnTo(d2);
                }                
            } 

            //Quadrant I
            else if (row < tarRow && col > tarCol) { 
                Location d = new Location (row + 2, col - 2); 
                int d2 = getDirectionToward(d); 
                if (getDirection() == d2) { 
                    move2(); 
                } else { 
                    turnTo(d2);
                }
            }
        }
    }

    public boolean inDanger2() { 
        if (getNumAct() % 40 >= 37 && getNumAct() > 1) { 
            if (getLocation().getCol() <= rock + 3 || getLocation().getCol() >= 23 - rock
                || getLocation().getRow() <= rock + 3|| getLocation().getRow() >= 23 - rock) { 
                    return true; 
            }
        }
        return false; 
    }

    public void avoidRock2() {
        int row = getLocation().getRow();
        int col = getLocation().getCol(); 
        //Quadrant 1
        if (row <= 13 && col >= 13) { 
            row+=2;
            col-=2;
        //Quadrant 2
        } else if (row < 13 && col < 13) { 
            row+=2;
            col+=2;
        //Quadrant 3
        } else if (row > 13 && col <= 13) { 
            row-=2;
            col+=2;
        //Quadrant 4
        } else if (row > 13 && col > 13) { 
            row-=2; 
            col-=2;
        }
        moveTwice(new Location(row, col));
    }

    //----------------------------------ENEMIES METHODS
    public void addEnemies(ArrayList<Actor> actors, ArrayList<BattleBug> enemies) { 
        for (int i = 0; i < actors.size(); i++) { 
            if (actors.get(i) instanceof BattleBug) { 
                enemies.add((BattleBug)actors.get(i)); 
            }
        }
    }
     
    public void removeEnemies(ArrayList<BattleBug> enemies) { 
        if (enemies.size() > 0) { 
            for (int i = 0; i < enemies.size(); i++) { 
                if (enemies.get(i).isDead()) { 
                    enemies.remove(i); 
                }
            }
        } 
    }

    public BattleBug detectEnemies(ArrayList<BattleBug> enemies) { 
        // BattleBug toKill = null;
        // if (enemies.size() == 1 && getStrength() - enemies.get(0).getDefense() >= 3 && getAmmo() >= 1) { 
        //     return enemies.get(0);
        // } else 
        if (enemies.size() > 0) {
            for (int i = 0; i < enemies.size(); i++) { 
                if (distance(getLocation().getRow(), enemies.get(i).getLocation().getRow(), getLocation().getCol(), enemies.get(i).getLocation().getCol()) <= 5) {
                    if (getStrength() > 8 && getDefense() > 6 && getAmmo() > 0) {
                        return enemies.get(i); 
                    }
                }
            }            
        }
        return null;
    }

    public void attackEnemies(ArrayList<BattleBug> enemies, BattleBug enemy) { 
        double dis = distance(getLocation().getRow(), detectEnemies(enemies).getLocation().getRow(), getLocation().getCol(), detectEnemies(enemies).getLocation().getCol());
        if (getStrength() - detectEnemies(enemies).getDefense() >= 3 && getAmmo() >= 1) { 
            if ((getStrength() < 10 && dis == 1) 
                || (getStrength() >= 10 && dis <= 2)
                || (getStrength() >= 20 && dis <= 3)) {
                int dir = getDirectionToward(detectEnemies(enemies).getLocation());
                    if (getDirection() == dir) { 
                        attack(); 
                    }
            }
        } 
    }
    
    public BattleBug closiestEnemy(ArrayList<BattleBug> enemies) { 
        for (int i = 0; i < enemies.size(); i++) { 
            if (distance(enemies.get(i).getLocation().getRow(), getLocation().getRow(), enemies.get(i).getLocation().getCol(), getLocation().getCol()) <= 4) { 
                dangerEnemy = true; 
                return enemies.get(i); 
            }
        } 
        return null; 
    }

    public BattleBug isInDangerEnemy(ArrayList<BattleBug> enemies) { 
        if (enemies.size() > 0) {
            for (int i = 0; i < enemies.size(); i++) { 
                if (enemies.get(i).getStrength() >= 3 + getDefense()) { 
                    return enemies.get(i); 
                }
            } 
        }
        return null; 
    }
    
    public void runAway(BattleBug enemy) { 
        int row = getLocation().getRow(); 
        int col = getLocation().getCol(); 
        if (distance(enemy.getLocation().getRow(), getLocation().getRow(), enemy.getLocation().getCol(), getLocation().getCol()) <= 5) {
            if (enemy.getLocation().getRow() < getLocation().getRow() && enemy.getLocation().getCol() == getLocation().getCol()) { 
                row+=2; 
            } else if (enemy.getLocation().getCol() < getLocation().getCol() && enemy.getLocation().getRow() == getLocation().getRow()) { 
                col+=2; 
            } else if (enemy.getLocation().getRow() > getLocation().getRow() && enemy.getLocation().getCol() == getLocation().getCol()) { 
                row-=2;
            } else if (enemy.getLocation().getCol() > getLocation().getCol() && enemy.getLocation().getRow() == getLocation().getRow()) { 
                col-=2;
            //quadrant 4
            } else if (enemy.getLocation().getRow() > getLocation().getRow() && enemy.getLocation().getCol() > getLocation().getCol()) { 
                row+=2;
                col+=2;
            //quadrant 1
            } else if (enemy.getLocation().getRow() < getLocation().getRow() && enemy.getLocation().getCol() > getLocation().getCol()) { 
                row-=2;
                col+=2;
            //quadrant 2
            } else if (enemy.getLocation().getRow() < getLocation().getRow() && enemy.getLocation().getCol() < getLocation().getCol()) { 
                row-=2;
                col-=2;
            //quadrant 3
            } else if (enemy.getLocation().getRow() > getLocation().getRow() && enemy.getLocation().getCol() < getLocation().getCol()) { 
                row+=2;
                col-=2;
            }
        }
        else { 
            dangerEnemy = false; 
        }
            goCloset(new Location(row, col));
        }
    }
