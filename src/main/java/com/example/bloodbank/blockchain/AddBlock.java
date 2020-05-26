package com.example.bloodbank.blockchain;

public class AddBlock {
    private Block block;
    private String nextMiner;


    public AddBlock(Block block, String nextMiner) {
        this.block = block;
        this.nextMiner = nextMiner;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getNextMiner() {
        return nextMiner;
    }

    public void setNextMiner(String nextMiner) {
        this.nextMiner = nextMiner;
    }
}
