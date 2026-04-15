package com.mycompany.roaaalsham_dropnumbergame;

public class RoaaAlsham_Main {

    public static void main(String[] args) {
        RoaaAlsham_DropNumberGrid game = new RoaaAlsham_DropNumberGrid();

        // All moves in the exact order from the project images
        RoaaAlsham_NumberBlock[] moves = {
            
            new RoaaAlsham_NumberBlock(2, 0), 
            new RoaaAlsham_NumberBlock(2, 3), 
            new RoaaAlsham_NumberBlock(4, 1), 
            new RoaaAlsham_NumberBlock(2,2 ), 
            new RoaaAlsham_NumberBlock(4, 4), 
            new RoaaAlsham_NumberBlock(2, 1), 
            new RoaaAlsham_NumberBlock(4, 4), 
            new RoaaAlsham_NumberBlock(8, 0),
            new RoaaAlsham_NumberBlock(8, 0), 
            new RoaaAlsham_NumberBlock(32, 1), 
            new RoaaAlsham_NumberBlock(2, 2), 
            new RoaaAlsham_NumberBlock(64, 2), 
            new RoaaAlsham_NumberBlock(16, 3), 

            new RoaaAlsham_NumberBlock(64, 1), 
            new RoaaAlsham_NumberBlock(32, 2), 
            new RoaaAlsham_NumberBlock(16, 0), 
            new RoaaAlsham_NumberBlock(16, 4), 
            new RoaaAlsham_NumberBlock(32, 2), 
            new RoaaAlsham_NumberBlock(64, 1),
            new RoaaAlsham_NumberBlock(8, 3),  
            new RoaaAlsham_NumberBlock(4, 3), 

            new RoaaAlsham_NumberBlock(2, 3), 
            new RoaaAlsham_NumberBlock(2, 3), 
            
            new RoaaAlsham_NumberBlock(2, 1), 
            new RoaaAlsham_NumberBlock(64, 2), 
            new RoaaAlsham_NumberBlock(32, 2), 

            new RoaaAlsham_NumberBlock(16, 2), 
            new RoaaAlsham_NumberBlock(8, 2),  
            new RoaaAlsham_NumberBlock(8, 2),  
           
            new RoaaAlsham_NumberBlock(4, 1),  
            new RoaaAlsham_NumberBlock(8, 1)   
        };

        System.out.println("=== Drop Number Game Simulation ===\n");
        int i = 0;
        while (i < moves.length && !game.isGameOver()) {
            RoaaAlsham_NumberBlock block = moves[i];
            System.out.printf("Step %d: Drop %d into column %d%n",
                    i + 1, block.getValue(), block.getColumn());

            game.dropBlock(block);
            i++;

            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                System.out.print(e.getMessage());
            }
        }
        
        System.out.println("_________game___is___over_________");
        
    }
}